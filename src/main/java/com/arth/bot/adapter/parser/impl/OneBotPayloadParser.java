package com.arth.bot.adapter.parser.impl;

import com.arth.bot.adapter.parser.PayloadParser;
import com.arth.bot.core.common.dto.ParsedPayloadDTO;
import com.arth.bot.core.common.dto.segment.AtSegment;
import com.arth.bot.core.common.dto.segment.ImageSegment;
import com.arth.bot.core.common.dto.segment.MessageSegment;
import com.arth.bot.core.common.dto.segment.TextSegment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OneBotPayloadParser implements PayloadParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

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
