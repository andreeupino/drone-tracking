package com.asdt.backend.config;

import com.asdt.backend.websocket.IngestHandler;
import com.asdt.backend.websocket.LiveHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final IngestHandler ingest;
    private final LiveHandler live;
    private final String token;

    public WebSocketConfig(IngestHandler ingest,
                           LiveHandler live,
                           @Value("${app.ingest-token}") String token) {
        this.ingest = ingest;
        this.live = live;
        this.token = token;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(ingest, "/ws/ingest")
                .addInterceptors(new TokenInterceptor(token));

        registry.addHandler(live, "/ws/live")
                .setAllowedOrigins("http://localhost:4200");
    }

    private static class TokenInterceptor implements HandshakeInterceptor {
        private final String token;

        TokenInterceptor(String token) {
            this.token = token;
        }

        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler handler, Map<String, Object> attributes) {
            return token.equals(request.getHeaders().getFirst("X-Ingest-Token"));
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler handler, Exception exception) {
        }
    }
}