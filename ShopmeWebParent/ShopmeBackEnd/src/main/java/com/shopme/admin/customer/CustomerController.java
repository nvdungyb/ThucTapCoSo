package com.shopme.admin.customer;

import com.shopme.common.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping("/customers")
    public String viewFirstPageCustomers(Model model) {
        return viewByPageCustomers(1, null, model);
    }

    @GetMapping("/customers/page/{pageNumber}")
    public String viewByPageCustomers(@PathVariable(name = "pageNumber") int pageNum, @RequestParam(name = "keyword", required = false) String keyword, Model model) {
        Page<Customer> page = customerService.listByPage(pageNum, keyword);
        List<Customer> listCustomers = page.getContent();

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
        model.addAttribute("listCustomers", listCustomers);

        return "customers/customer";
    }

    @GetMapping("/customers/{id}/enabled/{status}")
    public String editCustomerEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled) {
        customerService.updateCustomerEnabledStatus(id, enabled);
        return "redirect:/customers";
    }

}
