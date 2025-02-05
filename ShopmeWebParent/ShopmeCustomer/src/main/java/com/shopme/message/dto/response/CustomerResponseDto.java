package com.shopme.message.dto.response;

import com.shopme.common.entity.Customer;
import com.shopme.common.enums.ERole;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class CustomerResponseDto {
    private String id;
    private String firstName;
    private String lastName;
    private Set<ERole> eRoles;
    private String fullName;

    private CustomerResponseDto() {
    }

    public static CustomerResponseDto build(Customer customer) {
        CustomerResponseDto customerResponseDto = new CustomerResponseDto();
        customerResponseDto.setId(customer.getId().toString());
        customerResponseDto.setFirstName(customer.getUser().getFirstName());
        customerResponseDto.setLastName(customer.getUser().getLastName());
        customerResponseDto.setERoles(customer.getUser().getRoles().stream()
                .map(role -> role.getERole())
                .collect(Collectors.toSet()));
        customerResponseDto.setFullName(customer.getFullName());

        return customerResponseDto;
    }
}
