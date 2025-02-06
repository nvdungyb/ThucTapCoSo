package com.shopme.dto.response;

import com.shopme.common.entity.Seller;
import com.shopme.common.enums.ERole;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class SellerResponseDto {
    private String id;
    private String firstName;
    private String lastName;
    private Set<ERole> eRoles;
    private String shopName;

    private SellerResponseDto() {
    }

    public static SellerResponseDto build(Seller seller) {
        SellerResponseDto sellerResponseDto = new SellerResponseDto();
        sellerResponseDto.setId(seller.getId().toString());
        sellerResponseDto.setFirstName(seller.getUser().getFirstName());
        sellerResponseDto.setLastName(seller.getUser().getLastName());
        sellerResponseDto.setERoles(seller.getUser().getRoles().stream()
                .map(role -> role.getERole())
                .collect(Collectors.toSet()));
        sellerResponseDto.setShopName(seller.getShopName());

        return sellerResponseDto;
    }
}
