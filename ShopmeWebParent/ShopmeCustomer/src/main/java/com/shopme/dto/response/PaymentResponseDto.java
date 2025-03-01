package com.shopme.dto.response;

import com.shopme.common.enums.ECurrency;
import com.shopme.common.enums.EPaymentMethod;
import com.shopme.common.enums.EPaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class PaymentResponseDto {
    private final long id;
    private final EPaymentMethod paymentMethod;
    private final double amount;
    private ECurrency currency;
    private EPaymentStatus paymentStatus;
    private final Date paymentDate;
    private final Date createdDate;
    private final Date updatedDate;
}
