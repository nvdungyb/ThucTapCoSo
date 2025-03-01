package com.shopme.service;

import com.shopme.Reposistory.*;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.User;
import com.shopme.common.enums.*;
import com.shopme.common.shop.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderReposistory orderReposistory;
    private final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final CartItemReposistory cartItemReposistory;
    private final CartReposistory cartReposistory;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderReposistory orderReposistory, CartItemReposistory cartItemReposistory, CartReposistory cartReposistory, ProductRepository productRepository, UserRepository userRepository) {
        this.orderReposistory = orderReposistory;
        this.cartItemReposistory = cartItemReposistory;
        this.cartReposistory = cartReposistory;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * RabbitMQ, hoặc Kafka. use this tech stack to handle large number of order in Queue.
     * <p>
     * [API Order Service] → [RabbitMQ] → [Order Processing Worker] → [Database]
     * ↓
     * [Kafka] → [Analytics, BI, Machine Learning]
     */

    // Tối ưu hóa truy vấn để tránh N+1 problem. Sử dụng JOIN FETCH trong JPA hoặc @EntityGraph
    @Transactional
    public Order createOrder(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not fount!");
                });

        // 1. Get cart by user id (tối ưu truy vấn ở đây)
        Cart userCart = cartReposistory.findByUser(user).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Can not fount cart for this user id.");
        });

        // 2. Get all selected cartItem.
        List<CartItem> cartItems = userCart.getCartItems().stream()
                .filter(CartItem::isSelected)
                .collect(Collectors.toList());

        // 3. Check if cartItems is valid.
        boolean isValidCartItems = checkCartItem(cartItems);

        // 4. Create order.
        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    return OrderItem.builder()
                            .product(product)
                            .productPrice(product.getPrice())
                            .quantity(cartItem.getQuantity())
                            .deliveryStatus(EDeliveryStatus.PENDING)
                            .returnStatus(EReturnStatus.NONE)
                            .createAt(new Date())
                            .build();
                })
                .collect(Collectors.toList());

        Order order = Order.builder()
                .orderTime(new Date())
                .shippingCost(calculateShippingCost(orderItems))
                .productPrices(orderItems.stream()
                        .mapToDouble(item -> item.getProductPrice() * item.getQuantity())
                        .sum())
                .orderStatus(EOrderStatus.PENDING)
                .shippingAddress(getDefaultUserAddress(user))
                .user(user)
                .updateDate(new Date())
                .orderItems(orderItems)
                .build();

        Payment payment = Payment.builder()
                .paymentMethod(defaultUserPaymentMethod(user))
                .amount(totalCost(order))
                .currency(ECurrency.USD)
                .paymentStatus(EPaymentStatus.PENDING)
                .createdDate(new Date())
                .paymentDate(null)
                .updatedDate(null)
                .order(order)
                .build();

        order.setPayments(List.of(payment));
        orderItems.forEach(orderItem -> orderItem.setOrder(order));

        // 5. Update product quantity in stock and save order and delete cartItem.
        // todo: Để giải quyết vấn đề này (race condition: nhiều người cùng mua 1 sản phẩm đồng thời) sử dụng hàng đợi (message queue).
        updateProductQuantity(cartItems);
        deleteSelectedCartItems(cartItems);

        return orderReposistory.save(order);
    }

    // Use batch delete, instead delete cartItem one by one, n -> 1 query.
    private void deleteSelectedCartItems(List<CartItem> cartItems) {
        List<Long> cartItemIds = cartItems.stream()
                .map(CartItem::getId)
                .collect(Collectors.toList());
        cartReposistory.deleteCartItems(cartItemIds);
    }

    // todo: sử dụng batch update.
    private void updateProductQuantity(List<CartItem> cartItems) {
        cartItems.forEach(cartItem -> {
            productRepository.updateQuantity(cartItem.getProduct().getId(), cartItem.getQuantity());
        });
    }

    public double totalCost(Order order) {
        return order.getProductPrices() + order.getShippingCost() - discountAmount(order);
    }

    public double discountAmount(Order order) {
        return order.getOrderItems().stream()
                .mapToDouble(item -> {
                    ProductDiscount discount = item.getDiscount();
                    return discount != null ? discount.getDiscountPercentage() * item.getQuantity() * item.getProductPrice() : 0.0;
                })
                .sum();
    }

    // todo: need to fix that.
    private EPaymentMethod defaultUserPaymentMethod(User user) {
        // That is not true.
        return EPaymentMethod.CASHONDELIVERY;
    }

    // todo: need to fix that.
    private float calculateShippingCost(List<OrderItem> orderItems) {
        // That is wrong (bad design), we need to call api to delivery for each CartItem.
        return 10;
    }

    private String getDefaultUserAddress(User user) {
        List<Address> userAddress = user.getAddresses();
        if (userAddress == null || userAddress.size() == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please chose you location!");
        return userAddress.get(0).toString();
    }

    private boolean checkCartItem(List<CartItem> cartItems) {
        if (cartItems.size() == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to chose at least 1 product!");

        cartItems.forEach(item -> {
            Product product = item.getProduct();
            if (item.getQuantity() > product.getStockQuantity())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product + " + product.getName() + " is not enough to by!");
        });

        return true;
    }

}
