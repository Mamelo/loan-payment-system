package com.example.loansystem.loan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class LoanRequest {

    @NotNull(message = "loanAmount is required")
    @DecimalMin(value = "0.01",
            message = "loanAmount must be greater than zero")
    private BigDecimal loanAmount;

    @NotNull(message = "term is required")
    @Min(value = 1,
            message = "term must be greater than zero")
    private Integer term;

}