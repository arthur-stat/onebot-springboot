package com.arth.bot.application.routing.impl;

import com.arth.bot.common.dto.ParsedPayloadDTO;
import com.arth.bot.common.dto.segment.AtSegment;
import com.arth.bot.common.dto.segment.ImageSegment;
import com.arth.bot.common.dto.segment.MessageSegment;
import com.arth.bot.common.dto.segment.TextSegment;
import com.arth.bot.application.routing.GroupCommandRoutingService;
import com.arth.bot.application.routing.ParseAndRouteService;
import com.arth.bot.application.routing.PrivateCommandRoutingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParseAndRouteServiceImpl implements ParseAndRouteService {

    private final ObjectMapper objectMapper;

    @Resource
    private final GroupCommandRoutingService groupCommandRoutingService;

    @Resource
    private final PrivateCommandRoutingService privateCommandRoutingService;

    @Override
    public ParsedPayloadDTO parseRawToDTO(String raw) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(raw);
        List<MessageSegment> segments = new ArrayList<>();

        for (JsonNode s : root.path("message")) {
            String type = s.path("type").asText("");
            JsonNode data = s.path("data");

            switch (type) {
                case "text" -> {
                    TextSegment seg = new TextSegment();
                    seg.setData(Map.of("text", data.path("text").asText("")));
                    segments.add(seg);
                }
                case "at", "mention" -> {
                    AtSegment seg = new AtSegment();
                    seg.setData(Map.of("qq", data.path("qq").asText(""))); // "user_id"
                    segments.add(seg);
                }
                case "image" -> {
                    ImageSegment seg = new ImageSegment();
                    String url = data.hasNonNull("url") ? data.get("url").asText("")
                            : data.path("file").asText("");
                    seg.setData(Map.of("url", url));
                    segments.add(seg);
                }
                default -> {
                    // ...
                }
            }
        }

        ParsedPayloadDTO payload = new ParsedPayloadDTO();
        payload.setPostType(root.path("post_type").asText());
        payload.setMessageType(root.path("message_type").asText());
        payload.setSelfId(root.path("self_id").asLong(0));
        payload.setUserId(root.path("user_id").asLong(0));
        payload.setGroupId(root.has("group_id") ? root.path("group_id").asLong() : null);
        payload.setMessageId(root.path("message_id").asLong(0));
        payload.setTime(root.path("time").asLong(0));
        payload.setRawText(root.path("raw_message").asText(""));
        payload.setSenderRole(root.path("sender").path("role").asText(""));
        payload.setSegments(segments);
        payload.setRawRoot(root);
        payload.setOriginalJsonString(raw);
        return payload;
    }

    @Override
    public List<String> initialRoute(ParsedPayloadDTO payload) throws JsonProcessingException {
        if (payload == null || !"message".equals(payload.getPostType())) {
            return null;
        }

        String plainText = extractPlainText(payload);

        return switch (payload.getMessageType()) {
            case "group" -> routeGroupMessage(payload, plainText);
            case "private" -> routePrivateMessage(payload, plainText);
            default -> null;
        };
    }

    @Override
    public List<String> routeGroupMessage(ParsedPayloadDTO payload, String plainText) throws JsonProcessingException {
        // 命令分支
        if (plainText != null && plainText.startsWith("/")) {
            String[] params = plainText.split("\\s+");
            log.debug("command detected: {}", params[0]);

            switch (params[0]) {
                case "/hi" -> {
                    return groupCommandRoutingService.hi(payload);
                }
                case "/test1" -> {
                    return groupCommandRoutingService.test1(payload);
                }
                case "/test2" -> {
                    return groupCommandRoutingService.test2(payload);
                }
            }
        }

        return null;
    }

    @Override
    public List<String> routePrivateMessage(ParsedPayloadDTO payload, String plainText) throws JsonProcessingException {
        return null;
    }

    @Override
    public List<String> parsedParameters(String command) {
        return null;
    }

    /**
     * 从 segments 中提取正文内容，例如应排除 @username，并去除前缀空格（以便判断是否文本属于命令）
     * @param payload
     * @return
     */
    private String extractPlainText(ParsedPayloadDTO payload) {
        if (payload.getSegments() != null && !payload.getSegments().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (MessageSegment seg : payload.getSegments()) {
                if ("text".equalsIgnoreCase(seg.getType())) {
                    sb.append(seg.getData().getOrDefault("text", ""));
                }
            }
            return sb.toString().stripLeading();
        }
        return payload.getRawText() != null ? payload.getRawText().stripLeading() : "";
    }

}
