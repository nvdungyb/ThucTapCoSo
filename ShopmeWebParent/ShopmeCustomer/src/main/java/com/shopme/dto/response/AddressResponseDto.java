package com.shopme.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponseDto {
    private String province;
    private String district;
    private String town;
    private String detail;
    private long userId;
}
