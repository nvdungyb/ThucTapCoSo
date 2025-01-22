package com.shopme.customer;

import com.shopme.advice.exception.EmailAlreadyExistsException;
import com.shopme.advice.exception.RoleNotFoundException;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import com.shopme.common.shop.Cart;
import com.shopme.common.utils.ERole;
import com.shopme.message.dto.request.CustomerRegisterDto;
import com.shopme.role.RoleRepository;
import com.shopme.shoppingcart.CartRepository;
import com.shopme.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository repo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public Customer save(Customer customer) {
        boolean isUpdatingUser = (customer.getId() != null);
        if (isUpdatingUser) {
            Customer exitingUser = repo.findById(customer.getId()).get();
            if (customer.getUser().getPassword().isEmpty()) {
                customer.getUser().setPassword(exitingUser.getUser().getPassword());
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
        cart.setCreateAt(customer.getUser().getRegistrationDate());
        cart.setUpdateAt(customer.getUser().getRegistrationDate());
        cart.setCustomer(customer);

        Customer entry = repo.save(customer);
        cartRepository.save(cart);
        return entry;
    }

    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getUser().getPassword());
        customer.getUser().setPassword(encodedPassword);
    }

//    public boolean isEmailUnique(Long id, String email) {
//        Customer customerByEmail = repo.findByEmail(email);
//        if (customerByEmail == null) return true;
//        boolean isCreatingNew = (id == null);
//        if (isCreatingNew || customerByEmail.getId() != id) {
//            return false;
//        }
//        return true;
//    }

    public Customer register(CustomerRegisterDto registerDto) throws EmailAlreadyExistsException, RoleNotFoundException {
        // Check if this email is already in use.
        String emailRegistered = registerDto.getEmail();
        Optional<User> userOptional = userRepository.findByEmail(emailRegistered);
        if (userOptional.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists!");
        }

        Set<Role> roles = registerDto.getRoleTypes().stream()
                .map(name -> roleRepository.findByERole(name))
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());

        // Make sure that every customer has the CUSTOMER role.
        roles.add(roleRepository.findByERole(ERole.CUSTOMER)
                .orElseThrow(() -> new RoleNotFoundException("Error: Role CUSTOMER is not found.")));

        Customer customer = Customer.builder()
                .user(User.builder()
                        .email(registerDto.getEmail())
                        .password(passwordEncoder.encode(registerDto.getPassword()))
                        .firstName(registerDto.getFirstName())
                        .lastName(registerDto.getLastName())
                        .phoneNumber(registerDto.getPhoneNumber())
                        .roles(roles)
                        .enabled(registerDto.isEnabled())
                        .registrationDate(registerDto.getRegistrationDate())
                        .build())
                .loyaltyPoints(registerDto.getLoyaltyPoints())
                .totalSpent(registerDto.getTotalSpent())
                .build();

        return customerRepository.save(customer);
    }
}
