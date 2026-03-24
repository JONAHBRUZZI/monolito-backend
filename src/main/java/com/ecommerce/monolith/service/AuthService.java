package com.ecommerce.monolith.service;

import com.ecommerce.monolith.dto.LoginRequest;
import com.ecommerce.monolith.dto.LoginResponse;
import com.ecommerce.monolith.exception.BusinessException;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Map<String, DemoUser> DEMO_USERS = Map.of(
        "admin@gmail.com", new DemoUser("Admin.2026", "INTERNAL", "Administrador"),
        "cliente@gmail.com", new DemoUser("Cliente.2026", "CUSTOMER", "Cliente Demo")
    );

    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        if (!normalizedEmail.endsWith("@gmail.com")) {
            throw new BusinessException("Solo se permiten cuentas Gmail para este login demo");
        }

        DemoUser user = DEMO_USERS.get(normalizedEmail);
        if (user == null || !user.password().equals(request.password())) {
            throw new BusinessException("Credenciales invalidas");
        }

        return new LoginResponse(
            UUID.randomUUID().toString(),
            normalizedEmail,
            user.role(),
            user.displayName()
        );
    }

    private record DemoUser(String password, String role, String displayName) {
    }
}
