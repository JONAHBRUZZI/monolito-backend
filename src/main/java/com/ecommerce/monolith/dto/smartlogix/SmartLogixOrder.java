package com.ecommerce.monolith.dto.smartlogix;

import com.ecommerce.monolith.model.CustomerOrder;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public record SmartLogixOrder(
    String externalOrderId,
    String source,
    SmartLogixCustomer customer,
    List<SmartLogixItem> items,
    BigDecimal totalAmount,
    Instant createdAt
) {
    private static final String SOURCE = "MONOLITH";

    public static SmartLogixOrder fromCustomerOrder(CustomerOrder order) {
        String externalOrderId = "ORDEN-MONOLITO-" + order.getId();
        SmartLogixCustomer customer = new SmartLogixCustomer(
            "CLIENTE-" + order.getId(), // Assuming some logic to generate a customer ID
            "Unknown", // Customer name is not in CustomerOrder
            order.getCustomerEmail(),
            "N/A" // Phone is not in CustomerOrder
        );
        List<SmartLogixItem> items = order.getItems().stream()
            .map(orderItem -> new SmartLogixItem(
                "PRODUCTO-" + orderItem.getProduct().getId(), // Assuming SKU is based on product ID
                orderItem.getQuantity(),
                orderItem.getUnitPrice()
            ))
            .collect(Collectors.toList());

        return new SmartLogixOrder(
            externalOrderId,
            SOURCE,
            customer,
            items,
            order.getTotalAmount(),
            order.getCreatedAt().toInstant()
        );
    }
}
