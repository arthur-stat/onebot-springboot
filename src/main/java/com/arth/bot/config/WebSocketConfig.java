package com.arth.bot.config;

import com.arth.bot.controller.OneBotWsController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final OneBotWsController wsController;
    private final String websocketPath;
    private final String allowedOrigins;

    public WebSocketConfig(OneBotWsController wsController,
                           @Value("${onebot.websocket.path}") String websocketPath,
                           @Value("${onebot.websocket.allowed-origins}") String allowedOrigins) {
        this.wsController = wsController;
        this.websocketPath = websocketPath;
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(wsController, websocketPath)
                .setAllowedOriginPatterns(allowedOrigins);
    }
}