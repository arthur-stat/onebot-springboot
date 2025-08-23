package com.arth.bot.common.dto;

import com.arth.bot.common.dto.segment.MessageSegment;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

/** 用于将传入的 OneBot v11 消息解析 POJO 的 DTO */
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

    private JsonNode rawRoot;

    private String originalJsonString;
}
