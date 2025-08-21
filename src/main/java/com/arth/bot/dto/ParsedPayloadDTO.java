package com.arth.bot.dto;

import com.arth.bot.dto.segment.MessageSegment;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

@Data
public class ParsedPayloadDTO {

    private String postType;

    private String messageType;

    private long selfId;

    private long userId;

    private Long groupId;

    private long messageId;

    private long time;

    private String rawText;

    private String senderRole;

    private List<MessageSegment> segments;

    private JsonNode raw;
}
