package com.arth.bot.core.common.dto;

import com.arth.bot.core.common.dto.segment.MessageSegment;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

/** 用于将传入的 OneBot v11 消息解析 POJO 的 DTO */
@Data
public class ParsedPayloadDTO {

    /* 消息类型，例如 message */
    private String postType;

    /* 消息子类型，区分群聊与私聊 */
    private String messageType;

    /* bot 账号 ID */
    private long selfId;

    /* 消息发送者账号 ID */
    private long userId;

    /* 群聊 ID */
    private Long groupId;

    private long messageId;

    private long time;

    /* 原始报文中携带的纯文本段字段内容 */
    private String rawText;

    /* 消息发送者角色，例如管理员或普通成员 */
    private String senderRole;

    /* 从原始报文处理后得到的结构化消息段，例如 "@" 段、纯文本段 */
    private List<MessageSegment> segments;

    /* 协议原始报文解析后的 JSON 对象 */
    private JsonNode rawRoot;

    /* 协议原始报文 */
    private String originalJsonString;
}
