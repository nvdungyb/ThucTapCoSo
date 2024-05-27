package com.shopme.shoppingCart;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.shoppingcart.CartRepository;
import com.shopme.shoppingcart.ShoppingCartService;
import jakarta.annotation.security.RolesAllowed;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class Cart {
    @Autowired
    private CartRepository repo;

    @Autowired
    ShoppingCartService cartService;

    @Test
    public void test() {
         CartItem cartItem = new CartItem();
         cartItem.setQuantity(1);
         cartItem.setProduct(new Product(1));
         cartItem.setCustomer(new Customer(1));
         repo.save(cartItem);

         assert(repo.count() == 1);
    }
}
