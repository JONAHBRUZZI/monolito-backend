package com.ecommerce.monolith.dto;

import java.math.BigDecimal;

public record InventoryResponse(
    Long productId,
    String sku,
    String name,
    BigDecimal price,
    Integer available
) {
}
