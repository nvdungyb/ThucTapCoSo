package com.shopme.cart_item;

import com.shopme.common.entity.Customer;
import com.shopme.common.shop.Cart;
import com.shopme.shoppingcart.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;

    public void deleteCartItem(Integer itemId, Customer customer) {
        Cart cart = cartRepository.findByCustomerId(customer.getId());

        cartItemRepository.deleteByItemId(itemId, cart.getId());
    }
}
