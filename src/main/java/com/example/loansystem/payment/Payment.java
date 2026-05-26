package com.example.loansystem.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    @Column(name = "payment_id", unique = true, nullable = false)
    private String paymentId;

    @Column(name = "loan_id", nullable = false)
    private String loanId;

    @Column(name = "payment_amount", nullable = false)
    private BigDecimal paymentAmount;

}
