package com.restaurapp.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final Key key;
    private final int accessTtlSec;
    private final int refreshTtlSec;
    private final long allowedClockSkewSec = 30; // tolerancia por desfases de reloj

    /**
     * Si tu secret está en Base64, pon app.jwt.base64=true.
     */
    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.base64:false}") boolean secretBase64,
            @Value("${app.jwt.access-ttl-sec:3600}") int accessTtlSec,
            @Value("${app.jwt.refresh-ttl-sec:1209600}") int refreshTtlSec // 14 días por defecto
    ) {
        this.key = buildSigningKey(secret, secretBase64);
        this.accessTtlSec = accessTtlSec;
        this.refreshTtlSec = refreshTtlSec;
    }

    private Key buildSigningKey(String secret, boolean base64) {
        byte[] keyBytes = base64
                ? Decoders.BASE64.decode(secret)
                : secret.getBytes(StandardCharsets.UTF_8);

        // HS256 requiere >= 256 bits (32 bytes)
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                    "app.jwt.secret debe tener al menos 32 bytes (256 bits). " +
                            "Actualiza tu secret o usa app.jwt.base64=true si ya está en Base64."
            );
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(accessTtlSec)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(refreshTtlSec)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .setAllowedClockSkewSeconds(allowedClockSkewSec)
                .build()
                .parseClaimsJws(token);
    }

    // ------- Helpers útiles --------
    public String getSubject(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isExpired(String token) {
        Date exp = getClaim(token, Claims::getExpiration);
        return exp.before(new Date());
    }

    public <T> T getClaim(String token, Function<Claims, T> resolver) {
        Claims claims = parse(token).getBody();
        return resolver.apply(claims);
    }

    public int getAccessTtlSec() { return accessTtlSec; }
}
