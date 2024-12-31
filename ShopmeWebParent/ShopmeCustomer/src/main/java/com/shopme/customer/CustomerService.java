package com.shopme.customer;

import com.shopme.common.entity.Customer;
import com.shopme.common.shop.Cart;
import com.shopme.shoppingcart.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository repo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CartRepository cartRepository;

    public Customer save(Customer customer) {
        boolean isUpdatingUser = (customer.getId() != null);
        if (isUpdatingUser) {
            Customer exitingUser = repo.findById(customer.getId()).get();
            if (customer.getPassword().isEmpty()) {
                customer.setPassword(exitingUser.getPassword());
            } else {
                encodePassword(customer);
            }
        } else {
            encodePassword(customer);
        }
        return saveCustomerAndCart(customer);
    }

    private Customer saveCustomerAndCart(Customer customer) {
        Cart cart = new Cart();
        cart.setActice(true);
        cart.setCreateAt(customer.getRegistrationDate());
        cart.setUpdateAt(customer.getRegistrationDate());
        cart.setCustomer(customer);

        Customer entry = repo.save(customer);
        cartRepository.save(cart);
        return entry;
    }

    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Integer id, String email) {
        Customer customerByEmail = repo.findByEmail(email);
        if (customerByEmail == null) return true;
        boolean isCreatingNew = (id == null);
        if (isCreatingNew || customerByEmail.getId() != id) {
            return false;
        }
        return true;
    }

}
