package com.ecommerce.monolith.service;

import com.ecommerce.monolith.dto.LoginRequest;
import com.ecommerce.monolith.dto.LoginResponse;
import com.ecommerce.monolith.exception.BusinessException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Map<String, DemoUser> DEMO_USERS = Map.of(
        "admin@gmail.com", new DemoUser("Admin.2026", "INTERNAL", "Administrador"),
        "cliente@gmail.com", new DemoUser("Cliente.2026", "CUSTOMER", "Cliente Demo")
    );

    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
        "ecommerce-monolith-secret-key-2026-min32chars-required".getBytes()
    );
    private static final long TOKEN_EXPIRATION_MS = 86400000; // 24 hours

    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        if (!normalizedEmail.endsWith("@gmail.com")) {
            throw new BusinessException("Solo se permiten cuentas Gmail para este login demo");
        }

        DemoUser user = DEMO_USERS.get(normalizedEmail);
        if (user == null || !user.password().equals(request.password())) {
            throw new BusinessException("Credenciales invalidas");
        }

        String token = Jwts.builder()
            .subject(normalizedEmail)
            .claim("role", user.role())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_MS))
            .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
            .compact();

        return new LoginResponse(
            token,
            normalizedEmail,
            user.role(),
            user.displayName()
        );
    }

    private record DemoUser(String password, String role, String displayName) {
    }
}
