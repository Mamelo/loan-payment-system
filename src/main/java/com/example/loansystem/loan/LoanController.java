package com.example.loansystem.loan;

import com.example.loansystem.loan.LoanService;
import com.example.loansystem.loan.dto.LoanRequest;
import com.example.loansystem.loan.dto.LoanResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
@CrossOrigin(origins = "*")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(
            @Valid @RequestBody LoanRequest request) {

        LoanResponse response =
                loanService.createLoan(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponse> getLoanById(
            @PathVariable String loanId) {

        LoanResponse response =
                loanService.getLoanById(loanId);

        return ResponseEntity.ok(response);
    }
}