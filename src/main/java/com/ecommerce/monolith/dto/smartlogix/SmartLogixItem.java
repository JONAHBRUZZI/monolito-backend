package com.ecommerce.monolith.dto.smartlogix;

import java.math.BigDecimal;

public record SmartLogixItem(
    String sku,
    int quantity,
    BigDecimal unitPrice
) {}
