package com.example.loansystem.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class PaymentRequest {

    @NotBlank(message = "loanId is required")
    private String loanId;

    @NotNull(message = "paymentAmount is required")
    @DecimalMin(value = "0.01",
            message = "paymentAmount must be greater than zero")
    private BigDecimal paymentAmount;

}
