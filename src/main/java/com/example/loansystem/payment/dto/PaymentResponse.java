package com.example.loansystem.payment.dto;

import com.example.loansystem.loan.LoanStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class PaymentResponse {

    private String paymentId;
    private String loanId;
    private BigDecimal paymentAmount;
    private String status;
    private BigDecimal remainingBalance;
    private LoanStatus loanStatus;

}
