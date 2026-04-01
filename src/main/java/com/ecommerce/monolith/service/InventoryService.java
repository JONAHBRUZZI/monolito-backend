package com.ecommerce.monolith.service;

import com.ecommerce.monolith.dto.InventoryResponse;
import com.ecommerce.monolith.exception.BusinessException;
import com.ecommerce.monolith.exception.NotFoundException;
import com.ecommerce.monolith.model.InventoryItem;
import com.ecommerce.monolith.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public InventoryResponse adjustStock(Long productId, Integer delta) {
        InventoryItem item = inventoryRepository.findByProductIdForUpdate(productId)
            .orElseThrow(() -> new NotFoundException("Inventory not found for product " + productId));

        int nextStock = item.getAvailable() + delta;
        if (nextStock < 0) {
            throw new BusinessException("Insufficient stock for product " + productId);
        }

        item.setAvailable(nextStock);
        inventoryRepository.save(item);

        return map(item);
    }

    @Transactional(readOnly = true)
    public InventoryResponse getStock(Long productId) {
        InventoryItem item = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new NotFoundException("Inventory not found for product " + productId));
        return map(item);
    }

    private InventoryResponse map(InventoryItem item) {
        return new InventoryResponse(
            item.getProduct().getId(),
            item.getProduct().getSku(),
            item.getProduct().getName(),
            item.getProduct().getPrice(),
            item.getAvailable()
        );
    }
}
