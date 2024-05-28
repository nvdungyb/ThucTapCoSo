package com.shopme.order;

import com.shopme.common.entity.*;
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

    public boolean checkout(Customer customer) {
        try {
            List<CartItem> cartItems = service.listCartItems(customer);

            float productCost = (float) cartItems.stream().mapToDouble(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity()).sum();

            Order order = new Order();
            order.setCustomer(customer);
            order.setOrderStatus(true);
            order.setOrderTime(new Date());
            order.setShippingAddress(customer.getAddressLine1());
            order.setProductCost(productCost);
            order.setShippingCost(50000);
            order.setTotal(order.getProductCost() + order.getShippingCost());

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
}
