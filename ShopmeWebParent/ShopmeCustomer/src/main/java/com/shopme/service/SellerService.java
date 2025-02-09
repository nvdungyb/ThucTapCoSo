package com.shopme.service;

import com.shopme.Reposistory.RoleRepository;
import com.shopme.Reposistory.SellerReposistory;
import com.shopme.Reposistory.UserRepository;
import com.shopme.advice.exception.EmailAlreadyExistsException;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.Seller;
import com.shopme.common.entity.User;
import com.shopme.common.enums.ERole;
import com.shopme.dto.request.SellerRegisterDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SellerService {
    private final SellerReposistory sellerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public SellerService(SellerReposistory sellerRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.sellerRepository = sellerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public Seller register(SellerRegisterDto registerDto) throws EmailAlreadyExistsException {
        String emailRegistered = registerDto.getEmail();
        Optional<User> userOptional = userRepository.findByEmail(emailRegistered);
        if (userOptional.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists!");
        }

        Set<Role> roles = registerDto.getRoleTypes().stream()
                .map(name -> roleRepository.findByERole(name))
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());

        // Add default role
        roleRepository.findByERole(ERole.SELLER).ifPresent(roles::add);

        Seller seller = Seller.builder()
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
                .shopName(registerDto.getShopName())
                .shopRating(registerDto.getShopRating())
                .taxId(registerDto.getTaxId())
                .numberOfOrders(registerDto.getNumberOfOrders())
                .build();

        return sellerRepository.save(seller);
    }
}
