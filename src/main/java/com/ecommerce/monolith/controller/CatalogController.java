package com.ecommerce.monolith.controller;

import com.ecommerce.monolith.dto.CreateProductRequest;
import com.ecommerce.monolith.dto.InventoryResponse;
import com.ecommerce.monolith.service.CatalogService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public List<InventoryResponse> listProducts() {
        return catalogService.listProductsWithStock();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        return catalogService.createProduct(request);
    }
}
