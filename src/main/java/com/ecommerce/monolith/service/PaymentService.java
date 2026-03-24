package com.ecommerce.monolith.service;

import com.ecommerce.monolith.dto.PaymentResponse;
import com.ecommerce.monolith.exception.BusinessException;
import com.ecommerce.monolith.exception.NotFoundException;
import com.ecommerce.monolith.model.CustomerOrder;
import com.ecommerce.monolith.model.OrderStatus;
import com.ecommerce.monolith.repository.OrderRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public PaymentService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public PaymentResponse processPayment(Long orderId, String paymentMethodId) {
        CustomerOrder order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.SHIPPED) {
            throw new BusinessException("Order already paid or shipped");
        }

        try {
            Stripe.apiKey = stripeApiKey;

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(order.getTotalAmount().multiply(java.math.BigDecimal.valueOf(100)).longValue())
                .setCurrency("usd")
                .setPaymentMethod(paymentMethodId)
                .setConfirm(true)
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .setAllowRedirects(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER
                        )
                        .build()
                )
                .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            if ("succeeded".equals(paymentIntent.getStatus())) {
                order.setStatus(OrderStatus.PAID);
                orderRepository.save(order);
                return new PaymentResponse(
                    paymentIntent.getId(),
                    "succeeded",
                    "Pago procesado correctamente"
                );
            } else if ("requires_action".equals(paymentIntent.getStatus())) {
                return new PaymentResponse(
                    paymentIntent.getId(),
                    "requires_action",
                    "Se requiere acciones adicionales para completar el pago"
                );
            } else {
                throw new BusinessException("Payment failed with status: " + paymentIntent.getStatus());
            }

        } catch (StripeException e) {
            throw new BusinessException("Stripe error: " + e.getMessage());
        }
    }
}
