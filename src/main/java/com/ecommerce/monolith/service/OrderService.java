package com.ecommerce.monolith.service;

import com.ecommerce.monolith.dto.CreateOrderRequest;
import com.ecommerce.monolith.dto.OrderItemResponse;
import com.ecommerce.monolith.dto.OrderLineRequest;
import com.ecommerce.monolith.dto.OrderResponse;
import com.ecommerce.monolith.exception.BusinessException;
import com.ecommerce.monolith.exception.NotFoundException;
import com.ecommerce.monolith.model.CustomerOrder;
import com.ecommerce.monolith.model.InventoryItem;
import com.ecommerce.monolith.model.OrderItem;
import com.ecommerce.monolith.model.OrderStatus;
import com.ecommerce.monolith.model.Product;
import com.ecommerce.monolith.repository.InventoryRepository;
import com.ecommerce.monolith.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final SmartLogixService smartLogixService;

    public OrderService(
        OrderRepository orderRepository,
        InventoryRepository inventoryRepository,
        SmartLogixService smartLogixService
    ) {
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
        this.smartLogixService = smartLogixService;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        CustomerOrder order = new CustomerOrder();
        order.setCustomerEmail(request.customerEmail());
        order.setShippingMethod(request.shippingMethod());
        order.setShippingAddress(request.shippingAddress().trim());
        order.setStatus(OrderStatus.CREATED);

        BigDecimal total = BigDecimal.ZERO;

        for (OrderLineRequest line : request.items()) {
            InventoryItem inventory = inventoryRepository.findByProductIdForUpdate(line.productId())
                .orElseThrow(() -> new NotFoundException("Product not found: " + line.productId()));

            if (inventory.getAvailable() < line.quantity()) {
                throw new BusinessException("Insufficient stock for product " + line.productId());
            }

            inventory.setAvailable(inventory.getAvailable() - line.quantity());
            inventoryRepository.save(inventory);

            Product product = inventory.getProduct();
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(line.quantity()));

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(line.quantity());
            item.setUnitPrice(product.getPrice());
            item.setLineTotal(lineTotal);
            order.addItem(item);

            total = total.add(lineTotal);
        }

        order.setTotalAmount(total);
        CustomerOrder saved = orderRepository.save(order);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    smartLogixService.sendOrderNotification(saved);
                } catch (Exception ex) {
                    logger.error("Webhook delivery failed for order {}", saved.getId(), ex);
                }
            }
        });

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders() {
        return orderRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listOrderHistory(String email) {
        return orderRepository.findAllByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(email.trim())
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {
        CustomerOrder order = orderRepository.findWithItemsById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
        return toResponse(order);
    }

    @Transactional
    public OrderResponse markShipped(Long orderId) {
        CustomerOrder order = orderRepository.findWithItemsById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Cancelled orders cannot be shipped");
        }

        order.setStatus(OrderStatus.SHIPPED);
        return toResponse(orderRepository.save(order));
    }

    private OrderResponse toResponse(CustomerOrder order) {
        return new OrderResponse(
            order.getId(),
            order.getCustomerEmail(),
            order.getShippingMethod(),
            order.getShippingAddress(),
            order.getStatus(),
            order.getTotalAmount(),
            order.getCreatedAt(),
            order.getItems().stream()
                .map(item -> new OrderItemResponse(
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getLineTotal()
                ))
                .toList()
        );
    }
}
