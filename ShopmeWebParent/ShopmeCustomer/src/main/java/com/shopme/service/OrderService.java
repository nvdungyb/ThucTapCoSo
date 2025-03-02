package com.shopme.service;

import com.shopme.Reposistory.*;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.User;
import com.shopme.common.enums.*;
import com.shopme.common.shop.*;
import jakarta.persistence.*;
import org.hibernate.procedure.ProcedureOutputs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderReposistory orderReposistory;
    private final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final CartItemReposistory cartItemReposistory;
    private final CartReposistory cartReposistory;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    public OrderService(OrderReposistory orderReposistory, CartItemReposistory cartItemReposistory, CartReposistory cartReposistory, ProductRepository productRepository, UserRepository userRepository, EntityManager entityManager) {
        this.orderReposistory = orderReposistory;
        this.cartItemReposistory = cartItemReposistory;
        this.cartReposistory = cartReposistory;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    /**
     * RabbitMQ, hoặc Kafka. use this tech stack to handle large number of order in Queue.
     * <p>
     * [API Order Service] → [RabbitMQ] → [Order Processing Worker] → [Database]
     * ↓
     * [Kafka] → [Analytics, BI, Machine Learning]
     */

    // todo: Tối ưu hóa truy vấn để tránh N+1 problem. Sử dụng JOIN FETCH trong JPA hoặc @EntityGraph
    // Mới fetch thành công quan hệ giữa Cart và CartItem.
    // Khi thêm 1 item trong order => +3 truy vấn vào database.
    // với 2 item => 11 jdbc statements (10 + 1 batch) ~ 43ms. Với 3 item => 13 jdbc statements (12 + 1 batch) ~ 71ms.
    // todo: Có thể giảm thiểu số lần truy vấn vào database bằng cách cache dữ liệu tĩnh.
    @Transactional
    public Order createOrder(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {             // +1 try vấn, load User(Cart, CartItems).
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not fount!");
        });

        // 1. Get cart by user id (tối ưu truy vấn ở đây)
        Cart userCart = user.getCart();

        // 2. Get all selected cartItem.
        List<CartItem> cartItems = userCart.getCartItems().stream()
                .filter(CartItem::isSelected)
                .collect(Collectors.toList());

        // 3. Check if cartItems is valid.
        boolean isValidCartItems = checkCartItem(cartItems);                // +1 truy vấn/Product. Hibernate không thể tối ưu được câu lệnh quá phức tạp.

        // 4. Create order.
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            return OrderItem.builder()
                    .product(product)
                    .productPrice(product.getPrice())
                    .quantity(cartItem.getQuantity())
                    .deliveryStatus(EDeliveryStatus.PENDING)
                    .returnStatus(EReturnStatus.NONE)
                    .createAt(new Date())
                    .build();
        }).collect(Collectors.toList());

        Order order = Order.builder()
                .orderTime(new Date())
                .shippingCost(calculateShippingCost(orderItems))
                .productPrices(orderItems.stream()
                        .mapToDouble(item -> item.getProductPrice() * item.getQuantity())
                        .sum()
                ).orderStatus(EOrderStatus.PENDING)
                .shippingAddress(getDefaultUserAddress(user))                   // +1 truy vấn.
                .user(user)
                .updateDate(new Date())
                .orderItems(orderItems)
                .build();

        Payment payment = Payment.builder()
                .paymentMethod(defaultUserPaymentMethod(user))
                .amount(totalCost(order))
                .currency(ECurrency.USD)
                .paymentStatus(EPaymentStatus.PENDING)
                .createdDate(new Date()).paymentDate(null)
                .updatedDate(null)
                .order(order)
                .build();

        order.setPayments(List.of(payment));
        orderItems.forEach(orderItem -> orderItem.setOrder(order));

        // 5. Update product quantity in stock and save order and delete cartItem.
        // todo: Để giải quyết vấn đề này (race condition: nhiều người cùng mua 1 sản phẩm đồng thời) sử dụng hàng đợi (message queue) cho hệ thống lớn.
        Map<CartItem, String> failedUpdates = updateProductQuantity(cartItems);     // +1 truy vấn/Product.

        // Loại khỏi order những item không thành công.
        removeUnsuccessfullOrderItems(order, failedUpdates);

        if (order.getOrderItems().size() == 0) {
            throw new RuntimeException("No item is available to order.");
        }

        // chỉ xóa cartItem đã đc cập nhật.
        deleteCartItemsWithSuccessUpdateProduct(cartItems, failedUpdates.keySet());  // +1 truy vấn, (sql)batch delete CartItems.

        return orderReposistory.save(order);                                         // +1 truy vấn save Order, +1 truy vấn/save OrderItem => có thể tối ưu đc không, +1 truy vấn save Payment.
    }

    private void removeUnsuccessfullOrderItems(Order order, Map<CartItem, String> failedUpdates) {
        Set<Long> failedUpdateProductIds = failedUpdates.keySet().stream()
                .map(cartItem -> cartItem.getProduct().getId())
                .collect(Collectors.toSet());

        order.getOrderItems()
                .removeIf(orderItem -> failedUpdateProductIds.contains(orderItem.getProduct().getId()));
    }

    // (Sql) Batch delete.
    private void deleteCartItemsWithSuccessUpdateProduct(List<CartItem> cartItems, Set<CartItem> failedUpdates) {
        cartItems.removeAll(failedUpdates);
        List<Long> cartItemIds = cartItems.stream()
                .map(CartItem::getId)
                .collect(Collectors.toList());

        if (!cartItems.isEmpty())
            cartReposistory.deleteCartItems(cartItemIds);
    }

    // Batch update.
    @Transactional
    protected Map<CartItem, String> updateProductQuantity(List<CartItem> cartItems) {
        Map<CartItem, String> failedUpdates = new HashMap<>(cartItems.size());
        entityManager.setFlushMode(FlushModeType.COMMIT);

        List<Long> productIds = cartItems.stream()                    // +1 truy vấn.
                .map(item -> item.getProduct().getId())
                .collect(Collectors.toList());

        TypedQuery<Product> query = entityManager.createQuery("SELECT p FROM Product p WHERE p.id IN :ids", Product.class);
        query.setParameter("ids", productIds);
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        List<Product> products = query.getResultList();

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (CartItem item : cartItems) {
            try {
                Product product = productMap.get(item.getProduct().getId());
                if (product == null) {
                    failedUpdates.put(item, "Product not found");
                    continue;
                }

                if (product.getStockQuantity() >= item.getQuantity()) {
                    product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
                    entityManager.merge(product);
                } else {
                    failedUpdates.put(item, "Insufficient stock quantity");
                }
            } catch (PessimisticLockException e) {
                logger.warn("Unable to acquire lock for product ID {}", item.getProduct().getId(), e);
                failedUpdates.put(item, "Unable to acquire lock due to high contention");
            } catch (Exception e) {
                // Lỗi khác (ví dụ: database connection, constraint violation)
                logger.error("Error updating product id: " + item.getProduct().getId(), e);
                failedUpdates.put(item, "Error updating product id: " + item.getProduct().getId());
            }
        }
        return failedUpdates;
    }

    public double totalCost(Order order) {
        return order.getProductPrices() + order.getShippingCost() - discountAmount(order);
    }

    public double discountAmount(Order order) {
        return order.getOrderItems().stream().mapToDouble(item -> {
            ProductDiscount discount = item.getDiscount();
            return discount != null ? discount.getDiscountPercentage() * item.getQuantity() * item.getProductPrice() : 0.0;
        }).sum();
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
