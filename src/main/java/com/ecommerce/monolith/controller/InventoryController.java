package com.ecommerce.monolith.controller;

import com.ecommerce.monolith.dto.AdjustInventoryRequest;
import com.ecommerce.monolith.dto.InventoryResponse;
import com.ecommerce.monolith.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productId}")
    public InventoryResponse getByProduct(@PathVariable Long productId) {
        return inventoryService.getStock(productId);
    }

    @PatchMapping("/{productId}")
    public InventoryResponse adjust(@PathVariable Long productId, @Valid @RequestBody AdjustInventoryRequest request) {
        return inventoryService.adjustStock(productId, request.delta());
    }
}
