package com.messenger.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.messenger.common.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Only bot-gateway may call /internal/v1/bot/** on core. Requires X-Internal-Bot-Key.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InternalBotGatewayAuthFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Internal-Bot-Key";

    private final ObjectMapper objectMapper;

    @Value("${messenger.internal-bot-gateway-key:}")
    private String expectedKey;

    public InternalBotGatewayAuthFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri == null || !uri.startsWith("/internal/v1/bot");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (expectedKey == null || expectedKey.isBlank()) {
            writeError(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Bot internal API disabled: set messenger.internal-bot-gateway-key (INTERNAL_BOT_GATEWAY_KEY)");
            return;
        }
        String provided = request.getHeader(HEADER);
        if (!expectedKey.equals(provided)) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing " + HEADER);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(),
                ErrorResponse.of(status, status == 503 ? "SERVICE_UNAVAILABLE" : "UNAUTHORIZED", message));
    }
}
