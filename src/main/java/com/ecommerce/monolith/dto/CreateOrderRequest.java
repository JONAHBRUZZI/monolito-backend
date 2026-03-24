package com.ecommerce.monolith.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.ecommerce.monolith.model.ShippingMethod;
import java.util.List;

public record CreateOrderRequest(
    @NotNull @Email String customerEmail,
    @NotNull ShippingMethod shippingMethod,
    @NotEmpty @Size(min = 8, max = 240) String shippingAddress,
    @NotEmpty List<@Valid OrderLineRequest> items
) {
}
