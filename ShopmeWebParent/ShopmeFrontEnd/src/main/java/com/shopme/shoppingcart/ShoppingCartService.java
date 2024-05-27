package com.shopme.shoppingcart;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ShoppingCartService {
    @Autowired
    private CartRepository cartRepository;

    public Integer addProduct(Integer productId, Integer quantity, Customer customer) {
        Integer updatedQuantity = quantity;
        Product product = new Product(productId);

        CartItem cartItem = cartRepository.findByCustomerAndProduct(customer, product);
        if (cartItem != null) {
            updatedQuantity = cartItem.getQuantity() + quantity;
        } else {
            cartItem = new CartItem();
            cartItem.setCustomer(customer);
            cartItem.setProduct(product);
        }
        cartItem.setQuantity(updatedQuantity);

        cartRepository.save(cartItem);

        return updatedQuantity;
    }
}
