package com.arth.bot.adapter.sender;

import com.arth.bot.core.common.dto.ParsedPayloadDTO;

public interface Sender {

    /**
     * 控制 bot 发送消息（非回复）
     * @param payload
     * @param o
     */
    void send(ParsedPayloadDTO payload, Object o);

    /**
     * 控制 bot 发送文本字符串（非回复），接收 String 或 List<String>
     * @param payload
     * @param text
     */
    void sendText(ParsedPayloadDTO payload, Object text);

    /**
     * 为 forward 提供的原样透传提供的接口
     * @param selfId
     * @param json
     */
    void sendRawJSON(long selfId, String json);
}
