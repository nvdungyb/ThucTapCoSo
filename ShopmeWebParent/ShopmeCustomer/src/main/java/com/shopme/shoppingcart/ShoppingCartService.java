package com.shopme.shoppingcart;

import com.shopme.common.shop.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.shop.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ShoppingCartService {
    @Autowired
    private CartRepository cartRepository;

    public Integer addProduct(Integer productId, Integer quantity, Customer customer) {
        Integer updatedQuantity = quantity;
        Product product = new Product(productId);

        CartItem cartItem = cartRepository.findByProductId(productId);
        if (cartItem != null) {
            updatedQuantity = cartItem.getQuantity() + quantity;
        } else {
            cartItem = new CartItem();
//            cartItem.setCustomer(customer);
            cartItem.setProduct(product);
        }
        cartItem.setQuantity(updatedQuantity);

        cartRepository.save(cartItem);

        return updatedQuantity;
    }

    public List<CartItem> listCartItems(Customer customer) {
//        return cartRepository.findByCustomer(customer);
        return new ArrayList<>();
    }

    public void delete(Integer itemId) {
        cartRepository.deleteById(itemId);
    }

    public void updateItem(Integer itemId, Integer quantity) {
        cartRepository.updateQuantity(itemId, quantity);
    }

    public void deleteAllCartItem(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            cartRepository.deleteById(cartItem.getId());
        }
    }
}
