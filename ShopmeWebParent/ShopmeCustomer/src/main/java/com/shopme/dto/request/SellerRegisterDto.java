package com.shopme.dto.request;

import com.shopme.common.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
public class SellerRegisterDto {
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

    private Set<ERole> roleTypes;

    private boolean enabled;

    private Date registrationDate;

    private String taxId;
    private String shopName;
    private double shopRating;
    private int numberOfOrders;

    public SellerRegisterDto() {
        this.enabled = false;
        this.registrationDate = new Date();
        this.roleTypes = Set.of(ERole.SELLER);
        this.shopRating = 0.0;
        this.numberOfOrders = 0;
    }
}
