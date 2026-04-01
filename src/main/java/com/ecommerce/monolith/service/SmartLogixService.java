package com.ecommerce.monolith.service;

import com.ecommerce.monolith.dto.smartlogix.SmartLogixOrder;
import com.ecommerce.monolith.model.CustomerOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Service
public class SmartLogixService {

    private static final Logger logger = LoggerFactory.getLogger(SmartLogixService.class);

    @Value("${smartlogix.webhook.url}")
    private String webhookUrl;

    @Value("${smartlogix.webhook.secret}")
    private String webhookSecret;

    private final ObjectMapper objectMapper;
    private ObjectMapper signingObjectMapper;
    private final RestTemplate restTemplate;

    public SmartLogixService(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        objectMapper.findAndRegisterModules();
        signingObjectMapper = objectMapper.copy()
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    }

    public void sendOrderNotification(CustomerOrder order) {
        try {
            SmartLogixOrder smartLogixOrder = SmartLogixOrder.fromCustomerOrder(order);
            String payload = signingObjectMapper.writeValueAsString(smartLogixOrder);
            String signature = calculateHmacSha256(payload);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-HMAC-Signature", signature);

            HttpEntity<String> request = new HttpEntity<>(payload, headers);

            logger.info("Sending order {} to SmartLogix", order.getId());
            restTemplate.postForEntity(webhookUrl, request, String.class);
            logger.info("Successfully sent order {} to SmartLogix", order.getId());

        } catch (JsonProcessingException e) {
            logger.error("Error serializing order {} for SmartLogix", order.getId(), e);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("Error calculating HMAC for order {}", order.getId(), e);
        } catch (Exception e) {
            logger.error("Error sending order {} to SmartLogix", order.getId(), e);
        }
    }

    private String calculateHmacSha256(String payload) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] signatureBytes = sha256Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(signatureBytes);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
