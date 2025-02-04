package com.shopme.shoppingcart;

import com.shopme.cart_item.CartItemRepository;
import com.shopme.common.shop.Cart;
import com.shopme.common.shop.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.shop.Product;
import com.shopme.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ShoppingCartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;

    public Integer addProduct(Integer productId, Integer quantity, Customer customer) {
        Cart cart = cartRepository.findByCustomerId(customer.getId());

        Integer updatedQuantity = quantity;
        Product product = productRepository.findById(productId).get();

        CartItem cartItem = cartItemRepository.findByProductId(productId);
        if (cartItem != null) {
            updatedQuantity = cartItem.getQuantity() + quantity;
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
        }
//        cartItem.setProductPrice(product.getPrice());
        cartItem.setQuantity(updatedQuantity);
        cartItem.setCreatedAt(new Date());
        cartItem.setCart(cart);

        cartItemRepository.save(cartItem);

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
        cartItemRepository.updateQuantity(itemId, quantity);
    }

    public void deleteAllCartItem(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            cartRepository.deleteById(cartItem.getId());
        }
    }

    public List<CartItem> findAllCustomerCartItems(Customer customer) {
        Cart cart = cartRepository.findByCustomerId(customer.getId());

        return cartItemRepository.findAllByCartId(cart.getId());
    }
}
