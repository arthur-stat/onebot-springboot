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
     * 命令路由：解析 DTO 并跳转至相应的功能模块
     * @param payload
     * @param SessionId
     * @return 返回 action JSON
     * @throws JsonProcessingException
     */
    List<String> routingAndReturn(ParsedPayloadDTO payload, String SessionId) throws JsonProcessingException;
}
