package com.house.hunter.util;

import com.house.hunter.constant.UserRole;
import com.house.hunter.model.entity.RefreshToken;
import com.house.hunter.model.entity.User;
import com.house.hunter.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
@Data
public class JWTUtil {
    private final String secretKey;
    @Value("${jwt.access.expiration}")
    private long accessTokenExpirationTime;
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpirationTime;

    private UserRepository userRepository;
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    public JWTUtil() {
        this.secretKey = SecretKeyGenerator.readEncryptedSecretFromEnv();
    }

    public RefreshToken generateRefreshToken(User user) {
        String token = buildRefreshToken(user.getEmail(), user.getRole());
        return RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(new Date(System.currentTimeMillis() + refreshTokenExpirationTime).toInstant())
                .build();
    }

    public String buildRefreshToken(String email, UserRole role) {
        return buildToken(email, role, refreshTokenExpirationTime);
    }

    public String buildAccessToken(String email, UserRole role) {
        return buildToken(email, role, accessTokenExpirationTime);
    }

    private String buildToken(String email, UserRole role, long expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);
        return TOKEN_PREFIX + Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(expirationTime)))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        if (token == null) {
            return false;
        }
        try {
            if (validateTokenFormat(token)) {
                token = trimTokenPrefix(token);
                return validateTokenClaims(parseClaims(token));
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private String trimTokenPrefix(String token) {
        return token.substring(TOKEN_PREFIX.length());
    }


    private Claims parseClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public String getEmailFromToken(String token) {
        return parseClaims(token).get("email", String.class);
    }


    public Claims resolveClaims(HttpServletRequest req) {
        try {
            // Get the token from the request
            final String token = extractTokenFromRequest(req);
            if (token != null) {
                return parseClaims(token);
            }
            return null;
        } catch (ExpiredJwtException ex) {
            req.setAttribute("expired", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            req.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        // Check if the token is in the correct format and has valid claims
        if (validateTokenFormat(bearerToken) && validateTokenClaims(parseClaims(bearerToken))) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    private boolean validateTokenFormat(String token) {
        return token != null && token.startsWith(TOKEN_PREFIX);
    }

    private boolean validateTokenClaims(Claims claims) {
        try {
            // Check if the token is expired, email is invalid, or role is invalid
            return claims.getExpiration().after(new Date()) &&
                    getEmail(claims) != null && !getEmail(claims).isEmpty() &&
                    getRole(claims) != null && isValidRole(getRole(claims));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidRole(String role) {
        // Check if the role is one of the allowed roles
        return role.equals("ADMIN") || role.equals("LANDLORD") || role.equals("TENANT") || role.equals("GUEST");
    }

    private String getEmail(Claims claims) {
        return claims.getSubject();
    }

    private String getRole(Claims claims) {
        return (String) claims.get("role");
    }

}
