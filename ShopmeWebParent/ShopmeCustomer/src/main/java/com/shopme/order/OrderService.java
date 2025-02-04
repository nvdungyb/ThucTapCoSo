package com.shopme.order;

import com.shopme.cart_item.CartItemRepository;
import com.shopme.common.shop.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.shop.Order;
import com.shopme.common.shop.OrderItem;
import com.shopme.common.shop.ProductOrder;
import com.shopme.common.utils.DeliveryStatus;
import com.shopme.common.utils.OrderStatus;
import com.shopme.shoppingcart.CartRepository;
import com.shopme.shoppingcart.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductOrderRepository productOrderRepository;
    @Autowired
    private ShoppingCartService service;
    @Autowired
    private CartRepository cartRepository;

    public boolean checkout(Customer customer) {
        try {
            List<CartItem> cartItems = service.listCartItems(customer);

            float productCost = (float) cartItems.stream().mapToDouble(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity()).sum();

            Order order = new Order();
            order.setCustomer(customer);
            order.setOrderStatus(OrderStatus.CONFIRMED);
            order.setOrderTime(new Date());
            // todo: need to fix that
//            order.setShippingAddress(customer.getAddressLine1());
            order.setProductPrices(productCost);
            order.setShippingCost(50000);
            // todo: need to fix that
//            order.setTotal(order.getProductCost() + order.getShippingCost());

            Order savedOrder = orderRepository.save(order);

            List<ProductOrder> listProductOrder = new ArrayList<ProductOrder>();
            for (CartItem cartItem : cartItems) {
                ProductOrder productOrder = new ProductOrder();
                productOrder.setOrder(savedOrder);
                productOrder.setProduct(cartItem.getProduct());
                productOrder.setQuantity(cartItem.getQuantity());
                listProductOrder.add(productOrder);
            }

            Iterable<ProductOrder> res = productOrderRepository.saveAll(listProductOrder);
            if (res != null) {
                service.deleteAllCartItem(cartItems);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Order> getAllOrdersForCustomer(Customer customer) {
        return orderRepository.findAllByCustomer(customer);
    }

    public List<ProductOrder> listProductOrders(Integer id) {
        List<ProductOrder> productOrders = productOrderRepository.findAllByOrderId(id);
        return productOrders;
    }

    public void placeOrder(Order order) {
        List<CartItem> cartItems = cartRepository.findByCustomerId(order.getCustomer().getId()).getCartItems();
        cartItems.forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setProductPrice(cartItem.getProduct().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);
            orderItem.setCreateAt(new Date());
            order.getOrderItems().add(orderItem);
        });
        order.setDeliveryStatus(DeliveryStatus.PENDING);
        order.setUpdateDate(new Date());

        cartRepository.deleteAllItemsById(cartItems.stream().map(CartItem::getId).toList());
        orderRepository.save(order);
    }
}
