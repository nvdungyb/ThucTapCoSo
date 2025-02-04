package com.shopme.shoppingcart;

import com.shopme.common.shop.Cart;
import com.shopme.common.shop.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.shop.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends CrudRepository<Cart, Integer> {
    Cart findByCustomerId(Integer id);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.id IN :list")
    void deleteAllItemsById(List<Integer> list);

//    CartItem findByProductId(Integer productId);

//    @Modifying
//    @Query("UPDATE CartItem c SET c.quantity = :quantity WHERE c.id = :id")
//    void updateQuantity(@Param("id") Integer id, @Param("quantity") int quantity);
//    public List<CartItem> findByCustomer(Customer customer);
//
//    public CartItem findByCustomerAndProduct(Customer customer, Product product);
//
//    @Modifying
//    @Query("UPDATE CartItem c SET c.quantity = ?1 WHERE c.customer.id = ?2 AND c.product.id = ?3")
//    public void UpdateQuantity(Integer quantity, Integer customerId, Integer productId);
//
//    @Modifying
//    @Query("DELETE FROM CartItem c WHERE c.customer.id = ?1 AND c.product.id = ?2")
//    public void deleteByCustomerAndProduct(Integer customerId, Integer productId);
//
//    @Modifying
//    @Query("UPDATE CartItem c SET c.quantity = ?2 WHERE c.id = ?1")
//    void updateQuantity(Integer itemId, Integer quantity);
}
