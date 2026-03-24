package com.ecommerce.monolith.controller;

import com.ecommerce.monolith.dto.PaymentRequest;
import com.ecommerce.monolith.dto.PaymentResponse;
import com.ecommerce.monolith.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponse processPayment(@Valid @RequestBody PaymentRequest request) {
        return paymentService.processPayment(request.orderId(), request.paymentMethodId());
    }
}
