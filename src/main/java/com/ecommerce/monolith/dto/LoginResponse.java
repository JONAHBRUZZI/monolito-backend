package com.ecommerce.monolith.dto;

public record LoginResponse(
    String token,
    String email,
    String role,
    String displayName
) {
}
