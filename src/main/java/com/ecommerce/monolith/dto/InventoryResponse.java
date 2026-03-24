package com.ecommerce.monolith.dto;

public record InventoryResponse(
    Long productId,
    String sku,
    String name,
    Integer available
) {
}
