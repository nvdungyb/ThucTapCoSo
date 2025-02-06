package com.shopme.service;

import com.shopme.Reposistory.RoleRepository;
import com.shopme.Reposistory.UserRepository;
import com.shopme.Reposistory.CustomerRepository;
import com.shopme.advice.exception.EmailAlreadyExistsException;
import com.shopme.advice.exception.RoleNotFoundException;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import com.shopme.common.enums.ERole;
import com.shopme.dto.request.CustomerRegisterDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public CustomerService(PasswordEncoder passwordEncoder, CustomerRepository customerRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getUser().getPassword());
        customer.getUser().setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Long id, String email) {
        Optional<Customer> customerOptional = customerRepository.findByUserEmail(email);
        if (customerOptional.isEmpty())
            return true;

        boolean isCreatingNew = (id == null);
        if (isCreatingNew || customerOptional.map(Customer::getId).get() != id) {
            return false;
        }
        return true;
    }

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

    public Optional<Customer> updatePassword(@Email String email, @NotBlank String newPassword) {
        Customer customer = customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        customer.getUser().setPassword(newPassword);
        encodePassword(customer);

        return Optional.of(customerRepository.save(customer));
    }
}
