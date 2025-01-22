package com.shopme.customer;

import com.shopme.advice.exception.EmailAlreadyExistsException;
import com.shopme.advice.exception.RoleNotFoundException;
import com.shopme.common.entity.Customer;
import com.shopme.message.dto.request.CustomerRegisterDto;
import com.shopme.message.ApiResponse;
import com.shopme.message.dto.response.CustomerResponseDto;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RestController
@Validated
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @PostMapping("/customer/save")
    public @ResponseBody Customer saveCustomer(@RequestBody Customer customer, RedirectAttributes redirectAttributes) throws IOException {

        Customer savedCustomer = customerService.save(customer);

        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");
        return savedCustomer;
    }

    @PostMapping("/customer/register")
    public String registerCustomer(Customer customer, RedirectAttributes redirectAttributes) {
        customer.getUser().setEnabled(true);
        customer.getUser().setRegistrationDate(new Date());

        customerService.save(customer);
        redirectAttributes.addFlashAttribute("message", "You have registered successfully. Please login.");
        return "redirect:/login";
    }

    @PostMapping("/customers/register")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody CustomerRegisterDto registerDto) throws EmailAlreadyExistsException, RoleNotFoundException {
        logger.info("DTO: {}", registerDto);

        // Register customer.
        Customer customer = customerService.register(registerDto);
        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("New customer account has been created successfully")
                .data(CustomerResponseDto.build(customer))
                .path("/customers/register")
                .build()
        );
    }
}
