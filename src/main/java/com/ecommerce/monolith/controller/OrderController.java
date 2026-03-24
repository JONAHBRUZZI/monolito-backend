package com.ecommerce.monolith.controller;

import com.ecommerce.monolith.dto.CreateOrderRequest;
import com.ecommerce.monolith.dto.OrderResponse;
import com.ecommerce.monolith.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderResponse> listOrders() {
        return orderService.listOrders();
    }

    @GetMapping("/history")
    public List<OrderResponse> listOrderHistory(@RequestParam @Email String email) {
        return orderService.listOrderHistory(email);
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }
}
