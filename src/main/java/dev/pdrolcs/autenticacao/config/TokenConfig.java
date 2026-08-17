package dev.pdrolcs.autenticacao.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.pdrolcs.autenticacao.entity.User;
import dev.pdrolcs.autenticacao.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TokenConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.token.expiration}")
    private long expirationAccessTokenTime;

    @Value("${jwt.refresh.token.expiration}")
    private long expirationRefreshTokenTime;

    public String generateAccessToken(User user) {
        var algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withClaim("userId", user.getPublicId().toString())
                .withClaim("type", "access")
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plusSeconds(expirationAccessTokenTime))
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

    public String generateRefreshToken(User user) {
        var algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withClaim("userId", user.getPublicId().toString())
                .withClaim("type", "refresh")
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plusSeconds(expirationRefreshTokenTime))
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

    public DecodedJWT validateAccessToken(String accessToken) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            var verifier = JWT.require(algorithm)
                    .withClaim("type", "access")
                    .build();
            return verifier.verify(accessToken);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid access token");
        }
    }

    public DecodedJWT validateRefreshToken(String refreshToken) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            var verifier = JWT.require(algorithm)
                    .withClaim("type", "refresh")
                    .build();
            return verifier.verify(refreshToken);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid refresh token");
        }
    }

}
