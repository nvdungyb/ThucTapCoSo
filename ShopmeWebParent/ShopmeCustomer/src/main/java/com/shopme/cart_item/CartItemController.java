package com.shopme.cart_item;

import com.shopme.common.entity.Customer;
import com.shopme.security.ShopmeCustomerDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Transactional
public class CartItemController {
    @Autowired
    private CartItemService cartItemService;

    @GetMapping("/cart_item/delete/{itemId}")
    public String deleteCartItem(@PathVariable(name = "itemId") Integer itemId) {
        Customer customer = getCustomer();

        cartItemService.deleteCartItem(itemId, customer);
        return "redirect:/carts";
    }

    private Customer getCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ShopmeCustomerDetails customerDetails = (ShopmeCustomerDetails) authentication.getPrincipal();
        return customerDetails.getCustomer();
    }
}
