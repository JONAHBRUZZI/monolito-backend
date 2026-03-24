package com.ecommerce.monolith.dto;

public record PaymentResponse(
    String paymentIntentId,
    String status,
    String message
) {
}
