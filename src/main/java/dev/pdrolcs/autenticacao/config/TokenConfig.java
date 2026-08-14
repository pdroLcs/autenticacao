package dev.pdrolcs.autenticacao.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import dev.pdrolcs.autenticacao.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TokenConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.register.expiration}")
    private long expirationTime;

    public String generateToken(User user) {
        var algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withClaim("userId", user.getPublicId().toString())
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plusSeconds(expirationTime))
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

}
