package com.example.loansystem.payment;

import com.example.loansystem.loan.Loan;
import com.example.loansystem.loan.LoanRepository;
import com.example.loansystem.loan.LoanStatus;
import com.example.loansystem.payment.dto.PaymentRequest;
import com.example.loansystem.payment.dto.PaymentResponse;
import com.example.loansystem.payment.exception.InvalidPaymentException;
import com.example.loansystem.common.exception.LoanNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LoanRepository loanRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          LoanRepository loanRepository) {
        this.paymentRepository = paymentRepository;
        this.loanRepository = loanRepository;
    }

    @Transactional
    public PaymentResponse recordPayment(PaymentRequest request) {

        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() ->
                        new LoanNotFoundException(
                                "Loan with ID " +
                                        request.getLoanId() +
                                        " not found"));

        if (loan.getStatus() == LoanStatus.SETTLED) {

            throw new InvalidPaymentException(
                    "Loan is already fully settled");
        }

        BigDecimal paymentAmount = request.getPaymentAmount();

        if (paymentAmount.compareTo(
                loan.getRemainingBalance()) > 0) {

            throw new InvalidPaymentException(
                    "Payment exceeds remaining balance");
        }

        // Update balance
        BigDecimal updatedBalance =
                loan.getRemainingBalance()
                        .subtract(paymentAmount);

        loan.setRemainingBalance(updatedBalance);

        if (updatedBalance.compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(LoanStatus.SETTLED);
        }

        loanRepository.save(loan);

        // Better ID generation
        String paymentId = "PMT-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        Payment payment = Payment.builder()
                .paymentId(paymentId)
                .loanId(loan.getLoanId())
                .paymentAmount(paymentAmount)
                .build();


        Payment savedPayment =
                paymentRepository.save(payment);

        return PaymentResponse.builder()
                .paymentId(savedPayment.getPaymentId())
                .loanId(savedPayment.getLoanId())
                .paymentAmount(savedPayment.getPaymentAmount())
                .loanStatus(loan.getStatus())
                .status("RECORDED")
                .remainingBalance(loan.getRemainingBalance())
                .build();
    }
}