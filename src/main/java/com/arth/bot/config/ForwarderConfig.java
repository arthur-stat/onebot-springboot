package com.arth.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(ForwarderConfig.ForwarderProperties.class)
public class ForwarderConfig {

    @Data
    @ConfigurationProperties(prefix = "forwarder")
    public static class ForwarderProperties {

        private boolean enabled = false;
        private int messageQueueCapacity = 50;
        private String dropPolicy = "DROP_OLDEST";
        private List<Target> targets = List.of();
        private Reconnect reconnect = new Reconnect();

        @Data
        public static class Target {

            private String url;
            private String matcher;
            private String secWebSocketProtocol = "onebot.v11";
            private String origin;
            private String role = "Universal";
            private String token;
            private Long selfId;
        }

        @Data
        public static class Reconnect {
            private long initialDelayMs = 1000;
            private long maxDelayMs = 30000;
        }
    }
}
