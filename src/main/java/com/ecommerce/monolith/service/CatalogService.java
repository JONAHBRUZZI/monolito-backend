package com.ecommerce.monolith.service;

import com.ecommerce.monolith.dto.CreateProductRequest;
import com.ecommerce.monolith.dto.InventoryResponse;
import com.ecommerce.monolith.exception.BusinessException;
import com.ecommerce.monolith.model.InventoryItem;
import com.ecommerce.monolith.model.Product;
import com.ecommerce.monolith.repository.InventoryRepository;
import com.ecommerce.monolith.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public CatalogService(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public InventoryResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new BusinessException("SKU already exists: " + request.sku());
        }
        if (request.initialStock() < 0) {
            throw new BusinessException("Initial stock must be >= 0");
        }

        Product product = new Product();
        product.setSku(request.sku());
        product.setName(request.name());
        product.setPrice(request.price());
        Product savedProduct = productRepository.save(product);

        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setProduct(savedProduct);
        inventoryItem.setAvailable(request.initialStock());
        inventoryRepository.save(inventoryItem);

        return new InventoryResponse(savedProduct.getId(), savedProduct.getSku(), savedProduct.getName(), inventoryItem.getAvailable());
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> listProductsWithStock() {
        return inventoryRepository.findAll().stream()
            .map(item -> new InventoryResponse(
                item.getProduct().getId(),
                item.getProduct().getSku(),
                item.getProduct().getName(),
                item.getAvailable()
            ))
            .toList();
    }
}
