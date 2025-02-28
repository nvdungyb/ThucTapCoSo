package com.shopme.mapper;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.User;
import com.shopme.dto.request.AddressDto;
import com.shopme.dto.response.AddressResponseDto;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    public Address toEntity(AddressDto addressDto, long userId) {
        return Address.builder()
                .province(addressDto.getProvince())
                .district(addressDto.getDistrict())
                .town(addressDto.getTown())
                .details(addressDto.getDetails())
                .user(new User(userId))
                .build();
    }

    public AddressResponseDto toDto(Address address) {
        return AddressResponseDto.builder()
                .province(address.getProvince())
                .district(address.getDistrict())
                .town(address.getTown())
                .detail(address.getDetails())
                .userId(address.getUser().getId())
                .build();
    }
}
