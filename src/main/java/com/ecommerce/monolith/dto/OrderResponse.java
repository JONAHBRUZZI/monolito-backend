package com.ecommerce.monolith.dto;

import com.ecommerce.monolith.model.OrderStatus;
import com.ecommerce.monolith.model.ShippingMethod;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderResponse(
    Long orderId,
    String customerEmail,
    ShippingMethod shippingMethod,
    String shippingAddress,
    OrderStatus status,
    BigDecimal totalAmount,
    OffsetDateTime createdAt,
    List<OrderItemResponse> items
) {
}
