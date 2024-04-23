package com.house.hunter.util;

import com.house.hunter.exception.UserAuthenticationException;
import com.house.hunter.model.entity.User;
import com.house.hunter.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@AllArgsConstructor
public class JWTUtil {
    private final String secretKey;
    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;
    private final JwtParser jwtParser;
    private UserRepository userRepository;
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    public JWTUtil() {
        this.secretKey = SecretKeyGenerator.readEncryptedSecretFromEnv();
        this.jwtParser = Jwts.parser().setSigningKey(secretKey);
    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.get("email", String.class);
    }

    public String generateNewAccessToken(String refreshToken) {
        if (validateToken(refreshToken)) {
            String username = getUsernameFromToken(refreshToken);
            // Fetch the user from the database based on the username
            Optional<User> user = userRepository.findByEmail(username);
            if (user.isPresent()) {
                return generateAccessToken(user.get());
            }
        }
        return null;
    }

    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public Claims resolveClaims(HttpServletRequest req) {
        try {
            // Get the token from the request
            final String token = resolveToken(req);
            if (token != null) {
                return parseJwtClaims(token);
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

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        // Check if the token is in the correct format
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean validateClaims(Claims claims) throws UserAuthenticationException {
        try {
            // Check if the token is expired, email is invalid, or role is invalid
            if (!claims.getExpiration().after(new Date()) ||
                    getEmail(claims) == null || getEmail(claims).isEmpty() ||
                    getRole(claims) == null || !isValidRole(getRole(claims))) {
                throw new UserAuthenticationException("Invalid token");
            }

            return true;
        } catch (Exception e) {
            throw new UserAuthenticationException("Invalid token : " + e.getMessage());
        }
    }

    private boolean isValidRole(String role) {
        // Check if the role is one of the allowed roles
        return role.equals("ADMIN") || role.equals("LANDLORD") || role.equals("TENANT") || role.equals("GUEST");
    }

    public String getEmail(Claims claims) {
        return claims.getSubject();
    }

    public String getRole(Claims claims) {
        return (String) claims.get("role");
    }
}
