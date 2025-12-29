package com.nexsol.tpa.support.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    private final SecretKey key;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generate(Long id, String role, Set<String> serviceTypes, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        Date validity = new Date(now + (jwtProperties.expirationSeconds() * 1000));

        Map<String, Object> finalClaims = new HashMap<>(claims);
        finalClaims.put("role", role);
        if (serviceTypes != null && !serviceTypes.isEmpty()) {
            finalClaims.put("scope", String.join(",", serviceTypes));
        }

        return Jwts.builder()
                .subject(String.valueOf(id))
                .claims(finalClaims)
                .issuer(jwtProperties.issuer())
                .issuedAt(new Date(now))
                .expiration(validity)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        } catch (SecurityException | MalformedJwtException e) {
            throw new IllegalArgumentException("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            throw new IllegalArgumentException("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT 토큰이 잘못되었습니다.");
        }
    }

    public void validateToken(String token) {
        // parseClaims 내부에서 예외가 터지면 검증 실패로 간주
        parseClaims(token);
    }

    public Long getUserId(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public Set<String> getScope(String token) {
        String scopeString = parseClaims(token).get("scope", String.class);
        if (scopeString == null || scopeString.isEmpty()) {
            return Collections.emptySet();
        }

        return Set.of(scopeString.split(","));
    }

}
