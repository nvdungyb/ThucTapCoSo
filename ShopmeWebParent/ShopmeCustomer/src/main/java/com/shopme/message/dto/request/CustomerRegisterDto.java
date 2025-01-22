package com.shopme.message.dto.request;

import com.shopme.common.utils.ERole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.*;

@Builder
@Data
@AllArgsConstructor
public class CustomerRegisterDto {
    @Size(min = 5, max = 30, message = "Email must be between 5 and 30 characters")
    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 3, max = 16, message = "Password must be between 5 and 16 characters")
    @NotBlank(message = "Password is mandatory")
    private String password;

    @Size(min = 3, max = 10, message = "First name must be between 3 and 10 characters")
    private String firstName;

    @Size(min = 3, max = 10, message = "Last name must be between 3 and 10 characters")
    private String lastName;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;

    @NotNull
    private Set<ERole> roleTypes;

    private boolean enabled;

    private Date registrationDate;

    private int loyaltyPoints;

    private double totalSpent;

    public CustomerRegisterDto() {
        this.enabled = true;
        this.registrationDate = new Date();
        this.loyaltyPoints = 0;
        this.totalSpent = 0.0;
        this.roleTypes = new HashSet<>();
    }
}
