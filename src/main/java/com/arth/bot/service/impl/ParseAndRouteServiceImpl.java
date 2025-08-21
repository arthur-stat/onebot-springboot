package com.arth.bot.service.impl;

import com.arth.bot.dto.ParsedPayloadDTO;
import com.arth.bot.dto.segment.AtSegment;
import com.arth.bot.dto.segment.ImageSegment;
import com.arth.bot.dto.segment.MessageSegment;
import com.arth.bot.dto.segment.TextSegment;
import com.arth.bot.service.ParseAndRouteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParseAndRouteServiceImpl implements ParseAndRouteService {

    private final ObjectMapper objectMapper;

    @Resource
    private final ActionBuildServiceImpl actionBuildService;

    @Override
    public ParsedPayloadDTO parsedRawToDTO(String raw) throws JsonProcessingException {
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
                    seg.setData(Map.of("qq", data.path("qq").asText(""))); // 某些实现也叫 "user_id"
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
        payload.setRaw(root);
        return payload;
    }

    @Override
    public List<String> routingAndReturn(ParsedPayloadDTO payload, String sessionId) throws JsonProcessingException {
        if (payload == null || !"message".equals(payload.getPostType())) {
            return null;
        }

        // 解析正文
        String plainText = extractPlainText(payload);
        if (!"/hi".equals(plainText)) {
            return null;
        }

        if ("group".equals(payload.getMessageType()) && payload.getGroupId() != null) {
            return List.of(actionBuildService.buildGroupSendStrAction(payload.getGroupId(), "hi"));
        } else if ("private".equals(payload.getMessageType())) {
            return List.of(actionBuildService.buildPrivateSendStrAction(payload.getUserId(), "hi"));
        }
        return null;
    }

    /**
     * 从 segments 中提取正文内容，例如应排除 @username
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
            return sb.toString();
        }
        return payload.getRawText() != null ? payload.getRawText() : "";
    }
}
