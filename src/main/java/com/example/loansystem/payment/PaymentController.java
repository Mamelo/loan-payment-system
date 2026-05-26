package com.example.loansystem.payment;

import com.example.loansystem.payment.dto.PaymentRequest;
import com.example.loansystem.payment.dto.PaymentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> recordPayment(
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse response =
                paymentService.recordPayment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}