package com.shopme.admin.order;

import com.shopme.admin.customer.CustomerService;
import com.shopme.common.shop.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    public String viewFirstPageOrder(Model model) {
        return viewByPageOrder(1, null, model);
    }

    @GetMapping("/orders/page/{pageNumber}")
    public String viewByPageOrder(@PathVariable(name = "pageNumber") int pageNum, @RequestParam(name = "keyword", required = false) String keyword, Model model) {
        Page<Order> page = orderService.listByPage(pageNum, keyword);
        List<Order> listOrders = page.getContent();

        long startCount = (pageNum - 1) * CustomerService.CUSTOMER_PER_PAGE + 1;
        long endCount = startCount + CustomerService.CUSTOMER_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listOrders", listOrders);

        return "orders/order";
    }

    @GetMapping("/orders/{id}/enabled/{status}")
    public String editCustomerEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled) {
        orderService.updateCustomerEnabledStatus(id, enabled);
        return "redirect:/orders";
    }

}
