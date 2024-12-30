package com.shopme.order;

import com.shopme.common.entity.Customer;
import com.shopme.common.shop.Order;
import com.shopme.common.shop.ProductOrder;
import com.shopme.security.ShopmeCustomerDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    public String viewOrderList(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ShopmeCustomerDetails customerDetails = (ShopmeCustomerDetails) authentication.getPrincipal();
        Customer customer = customerDetails.getCustomer();

        List<Order> listOrders = orderService.getAllOrdersForCustomer(customer);
        model.addAttribute("listOrders", listOrders);

        return "order/orders";
    }

    @GetMapping("/orders/details/{id}")
    public String viewOrderDetails(@PathVariable("id") Integer id, Model model) {
        List<ProductOrder> listProductOrders = orderService.listProductOrders(id);

        float total = (float) listProductOrders.stream().mapToDouble(productOrder -> productOrder.getProduct().getPrice() * productOrder.getQuantity()).sum();

        model.addAttribute("orderDetails", listProductOrders);
        model.addAttribute("total", total);

        return "order/order_details";
    }


}
