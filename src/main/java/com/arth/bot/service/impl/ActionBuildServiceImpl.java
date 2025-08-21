package com.arth.bot.service.impl;

import com.arth.bot.dto.OneBotReturnActionDTO;
import com.arth.bot.service.ActionBuildService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ActionBuildServiceImpl implements ActionBuildService {

    private final ObjectMapper objectMapper;

    @Override
    public String buildGroupSendStrAction(long groupId, String text) {
        return buildSendMsg("group", Map.of("group_id", groupId), text);
    }

    @Override
    public String buildPrivateSendStrAction(long userId, String text) {
        return buildSendMsg("private", Map.of("user_id", userId), text);
    }

    private String buildSendMsg(String messageType, Map<String, Object> target, String text) {
        OneBotReturnActionDTO dto = new OneBotReturnActionDTO();
        dto.setAction("send_msg");
        dto.setEcho("echo-" + System.currentTimeMillis());

        Map<String, Object> params = new HashMap<>(target);
        params.put("message_type", messageType);
        Map<String, Object> textSeg = new HashMap<>();
        textSeg.put("type", "text");
        textSeg.put("data", Map.of("text", text));
        params.put("message", List.of(textSeg));
        dto.setParams(params);

        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("serialize OneBotActionDTO failed", e);
        }
    }
}
