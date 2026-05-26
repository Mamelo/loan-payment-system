package com.example.loansystem.loan;

import com.example.loansystem.common.exception.LoanNotFoundException;
import com.example.loansystem.loan.Loan;
import com.example.loansystem.loan.LoanRepository;
import com.example.loansystem.loan.dto.LoanRequest;
import com.example.loansystem.loan.dto.LoanResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public LoanResponse createLoan(LoanRequest request) {

        // Better ID generation
        String loanId = "LOAN-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        Loan loan = Loan.builder()
                .loanId(loanId)
                .loanAmount(request.getLoanAmount())
                .term(request.getTerm())
                .remainingBalance(request.getLoanAmount())
                .status(LoanStatus.ACTIVE)
                .build();

        Loan savedLoan = loanRepository.save(loan);

        return mapToResponse(savedLoan);
    }

    public LoanResponse getLoanById(String loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new LoanNotFoundException(
                                "Loan with ID " + loanId + " not found"));

        return mapToResponse(loan);
    }

    private LoanResponse mapToResponse(Loan loan) {

        return LoanResponse.builder()
                .loanId(loan.getLoanId())
                .loanAmount(loan.getLoanAmount())
                .remainingBalance(loan.getRemainingBalance())
                .term(loan.getTerm())
                .status(loan.getStatus())
                .build();
    }
}