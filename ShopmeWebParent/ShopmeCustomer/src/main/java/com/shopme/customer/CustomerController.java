package com.shopme.customer;

import com.shopme.common.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class CustomerController {
    @Autowired
    private CustomerService service;

    @PostMapping("/customer/save")
    public @ResponseBody Customer saveCustomer(@RequestBody Customer customer, RedirectAttributes redirectAttributes) throws IOException {

        Customer savedCustomer = service.save(customer);

        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");
        return savedCustomer;
    }

    @PostMapping("/customer/register")
    public String registerCustomer(Customer customer, RedirectAttributes redirectAttributes) {
        service.save(customer);
        redirectAttributes.addFlashAttribute("message", "You have registered successfully. Please login.");
        return "redirect:/login";
    }
}
