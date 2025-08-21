package com.arth.bot.config;

import com.arth.bot.controller.OneBotWsController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final OneBotWsController wsController;

    public WebSocketConfig(OneBotWsController wsController) {
        this.wsController = wsController;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(wsController, "/onebot/v11/ws")
                .setAllowedOriginPatterns("*");
    }
}