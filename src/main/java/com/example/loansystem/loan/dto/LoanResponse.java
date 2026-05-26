package com.example.loansystem.loan.dto;

import com.example.loansystem.loan.LoanStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class LoanResponse {

    private String loanId;
    private BigDecimal loanAmount;
    private BigDecimal remainingBalance;
    private Integer term;
    private LoanStatus status;

}