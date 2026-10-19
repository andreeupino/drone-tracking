package com.asdt.backend.websocket;

import com.asdt.backend.model.Detection;
import com.asdt.backend.repository.DetectionRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class IngestHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper;
    private final Validator validator;
    private final DetectionRepository repo;
    private final LiveHandler live;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if (message.getPayloadLength() > 8192) return;         
        try {
            Detection d = mapper.readValue(message.getPayload(), Detection.class);
            if (!validator.validate(d).isEmpty()) return;       
            d.setId(null);
            d.setTimestamp(Instant.now());
            live.broadcast(mapper.writeValueAsString(repo.save(d)));
        } catch (JacksonException ignored) { }
    }
}