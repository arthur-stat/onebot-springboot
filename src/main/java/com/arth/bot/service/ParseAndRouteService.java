package com.arth.bot.service;

import com.arth.bot.dto.ParsedPayloadDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface ParseAndRouteService {

    /**
     * 将 OneBot 传入的消息解析为 DTO
     * @param raw
     * @return
     * @throws JsonProcessingException
     */
    ParsedPayloadDTO parsedRawToDTO(String raw) throws JsonProcessingException;

    /**
     * 初步解析 DTO，进入群聊逻辑分支或私聊逻辑分支
     * @param payload
     * @return 返回 action JSON
     * @throws JsonProcessingException
     */
    List<String> parsedAndRouting(ParsedPayloadDTO payload) throws JsonProcessingException;

    /**
     * 群聊消息处理，路由至具体群聊命令
     * @param payload
     * @param plainText
     * @return
     * @throws JsonProcessingException
     */
    List<String> routeGroupMessage(ParsedPayloadDTO payload, String plainText) throws JsonProcessingException;

    /**
     * 私聊消息处理，路由至具体私聊命令
     * @param payload
     * @param plainText
     * @return
     * @throws JsonProcessingException
     */
    List<String> routePrivateMessage(ParsedPayloadDTO payload, String plainText) throws JsonProcessingException;

    /**
     * 命令参数解析器，参考 Linux CLI 实现方式
     * @param command
     * @return
     */
    List<String> parsedParameters(String command);
}
