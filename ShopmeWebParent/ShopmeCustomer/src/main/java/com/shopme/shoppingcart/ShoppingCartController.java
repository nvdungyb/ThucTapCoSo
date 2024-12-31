package com.shopme.shoppingcart;

import com.shopme.common.shop.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.order.OrderService;
import com.shopme.security.ShopmeCustomerDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService service;
    @Autowired
    private OrderService orderService;

    @GetMapping("/carts")
    public String viewCart(Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ShopmeCustomerDetails customerDetails = (ShopmeCustomerDetails) authentication.getPrincipal();
        Customer customer = customerDetails.getCustomer();

        List<CartItem> cartItems = service.findAllCustomerCartItems(customer);

        float estimatedTotal = 0;
        for (CartItem cart : cartItems) {
            estimatedTotal += cart.getSubtotal();
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("estimatedTotal", estimatedTotal);

        return "cart/shopping_cart";
    }

    @PostMapping("/carts/update")
    public String updateQuantityProduct(Model model, HttpServletRequest request) {
        Integer itemId = Integer.parseInt(request.getParameter("id"));
        Integer quantity = Integer.parseInt(request.getParameter("quantity"));

        service.updateItem(itemId, quantity);

        return "redirect:/carts";
    }

    @GetMapping("/carts/checkout")
    public String viewCheckout(HttpServletRequest request, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ShopmeCustomerDetails customerDetails = (ShopmeCustomerDetails) authentication.getPrincipal();

        Customer customer = customerDetails.getCustomer();

        if (orderService.checkout(customer))
            return "redirect:/orders";
        return "redirect:/carts";
    }

}
