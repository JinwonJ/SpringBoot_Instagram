package org.clonestudy.instagram.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final JwtProperties props;
    private final Key key;

    public JwtTokenProvider(JwtProperties props) {
        this.props = props;
        // HS256: secret 바이트 길이 너무 짧으면 WeakKeyException 발생
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long userId, String username) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.accessTokenExpMin() * 60);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("type", "access")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long userId) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.refreshTokenExpDays() * 24 * 60 * 60);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("type", "refresh")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public boolean isAccessToken(String token) {
        try {
            return "access".equals(parse(token).getBody().get("type", String.class));
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            return "refresh".equals(parse(token).getBody().get("type", String.class));
        } catch (JwtException e) {
            return false;
        }
    }

    public Long getUserId(String token) {
        return Long.valueOf(parse(token).getBody().getSubject());
    }

    public Instant getExpiry(String token) {
        return parse(token).getBody().getExpiration().toInstant();
    }

    public String getUsername(String token) {
        return parse(token).getBody().get("username", String.class);
    }
}
