package com.ecommerce.monolith.dto;

import jakarta.validation.constraints.NotNull;

public record AdjustInventoryRequest(@NotNull Integer delta) {
}
