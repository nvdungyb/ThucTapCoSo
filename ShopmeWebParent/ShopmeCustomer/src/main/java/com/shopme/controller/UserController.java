package com.shopme.controller;

import com.shopme.common.dto.ApiResponse;
import com.shopme.common.entity.Address;
import com.shopme.dto.request.AddressDto;
import com.shopme.mapper.AddressMapper;
import com.shopme.security.UserDetailsImpl;
import com.shopme.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class UserController {
    private final UserService userService;
    private final AddressMapper addressMapper;

    public UserController(UserService userService, AddressMapper addressMapper) {
        this.userService = userService;
        this.addressMapper = addressMapper;
    }

    // todo: we need to store user entity in UserDetails.
    @PostMapping("/users/address/add")
    public ResponseEntity<?> addCustomerAddress(@Valid @RequestBody AddressDto addressDto,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                HttpServletRequest request) {
        Long userId = userDetails.getId();
        Address address = userService.createUserAddress(addressDto, userId);

        return ResponseEntity.ok().body(
                ApiResponse.builder()
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                        .status(HttpStatus.OK.value())
                        .message("New address was added successfully to userId: " + userId)
                        .data(addressMapper.toDto(address))
                        .path(request.getRequestURI())
                        .build()
        );
    }
}
