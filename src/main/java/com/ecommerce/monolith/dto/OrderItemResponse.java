package com.ecommerce.monolith.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
    Long productId,
    String productName,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal lineTotal
) {
}
