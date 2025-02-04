package com.shopme.shoppingcart;

import com.shopme.cart_item.CartItemRepository;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerRepository;
import com.shopme.security.ShopmeCustomerDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShoppingCartRestController {
    @Autowired
    private ShoppingCartService cartService;
    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping("/cart/add/{productId}/{quantity}")
    public ResponseEntity<String> addProductToCart(@PathVariable(name = "productId") Integer productId, @PathVariable(name = "quantity") Integer quantity, HttpServletRequest request) {

        Customer customer = null;
        try {
            customer = getCustomer(request);
            Integer updatedQuantity = cartService.addProduct(productId, quantity, customer);
            return ResponseEntity.ok("Product added to cart successfully!");
        } catch (Exception e) {
            if (customer == null) {
                return ResponseEntity.status(401).body("You must login to add product to cart!");
            }
            return ResponseEntity.status(500).body("Failed to add product to cart!");
        }
    }

    private Customer getCustomer(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ShopmeCustomerDetails customerDetails = (ShopmeCustomerDetails) authentication.getPrincipal();
        Customer customer = customerDetails.getCustomer();

        return customer;
    }
}
