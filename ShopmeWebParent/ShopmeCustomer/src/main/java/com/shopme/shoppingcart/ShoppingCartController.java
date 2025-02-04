package com.shopme.shoppingcart;

import com.shopme.common.shop.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.shop.Order;
import com.shopme.common.shop.OrderItem;
import com.shopme.common.utils.OrderStatus;
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

import java.util.Date;
import java.util.List;

@Controller
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService service;
    @Autowired
    private OrderService orderService;

    @GetMapping("/carts")
    public String viewCart(Model model, HttpServletRequest request) {
        Customer customer = getCustomer();

        List<CartItem> cartItems = service.findAllCustomerCartItems(customer);

        // hard code, don't do this in real project
        float estimatedTotal = 0;
        for (CartItem cart : cartItems) {
            if (cart.getProduct().getCurrency().equals("USD")) {
                estimatedTotal += cart.getQuantity() * cart.getProduct().getPrice() * 24_000;
            } else if (cart.getProduct().getCurrency().equals("EUR")) {
                estimatedTotal += cart.getQuantity() * cart.getProduct().getPrice() * 26_000;
            } else {
                estimatedTotal += cart.getQuantity() * cart.getProduct().getPrice();
            }
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
        Customer customer = getCustomer();

        List<CartItem> cartItems = service.findAllCustomerCartItems(customer);

        // hard code, don't do this in real project
        float estimatedTotal = 0;
        for (CartItem cart : cartItems) {
            if (cart.getProduct().getCurrency().equals("USD")) {
                estimatedTotal += cart.getQuantity() * cart.getProduct().getPrice() * 24_000;
            } else if (cart.getProduct().getCurrency().equals("EUR")) {
                estimatedTotal += cart.getQuantity() * cart.getProduct().getPrice() * 26_000;
            } else {
                estimatedTotal += cart.getQuantity() * cart.getProduct().getPrice();
            }
        }

        // create order
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderTime(new Date());
        order.setOrderStatus(OrderStatus.PENDING);
        double productPrices = cartItems.stream().mapToDouble(cartItem -> cartItem.getProduct().getPrice()).sum();
        order.setProductPrices((float) productPrices);

        Double productWeights = cartItems.stream().mapToDouble(cartItem -> cartItem.getProduct().getWeight()).sum();
        model.addAttribute("productWeights", productWeights);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("estimatedTotal", estimatedTotal);
        model.addAttribute("order", order);
        model.addAttribute("orderItems", order.getOrderItems());

        return "cart/checkout";
    }

    private Customer getCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ShopmeCustomerDetails customerDetails = (ShopmeCustomerDetails) authentication.getPrincipal();

        Customer customer = customerDetails.getCustomer();
        return customer;
    }

}
