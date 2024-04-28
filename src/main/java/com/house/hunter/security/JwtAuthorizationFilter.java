package com.house.hunter.security;

import com.house.hunter.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        /*String token = getTokenFromRequest(request);

        if (token != null && jwtUtil.validateToken(token)) {
            String email = jwtUtil.getEmailFromToken(token);
            Claims claims = jwtUtil.resolveClaims(request);
            String role = claims.get("role", String.class);

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    email, null, List.of(authority));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if (isRefreshTokenRequest(request)) {
            String refreshToken = getRefreshTokenFromRequest(request);
            if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
                String newAccessToken = jwtUtil.generateNewAccessToken(refreshToken);
                if (newAccessToken != null) {
                    response.setHeader("Access-AccessToken", newAccessToken);
                    response.setStatus(HttpServletResponse.SC_OK);
                    return;
                }
            }
        }
    */
        chain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean isRefreshTokenRequest(HttpServletRequest request) {
        return request.getRequestURI().equals("/api/v1/user/refresh-token");
    }

    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("Refresh-AccessToken");
    }
}
