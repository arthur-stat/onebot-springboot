package com.arth.bot.adapter.sender;

import com.arth.bot.core.common.dto.ParsedPayloadDTO;

public interface Sender {

    /**
     * 控制 bot 发送文本字符串（非回复），接收 String 或 List<String>
     * 传入 List<String> 时，遍历发送
     * @param payload
     * @param text
     */
    void sendText(ParsedPayloadDTO payload, Object text);

    /**
     * 控制 bot 发送回复文本字符串，接收 String 或 List<String>
     * 传入 List<String> 时，遍历发送
     * @param payload
     * @param text
     */
    void responseText(ParsedPayloadDTO payload, Object text);

    /**
     * 控制 bot 发送一张图片（非回复），接收表示文件地址的 String 或 List<String>
     * 支持在同一消息中发送多张图片，传入 List<String> 即可
     * @param payload
     * @param image
     */
    void sendImage(ParsedPayloadDTO payload, Object image);

    /**
     * 控制 bot 发送一张回复图片，接收表示文件地址的 String 或 List<String>
     * 支持在同一消息中发送多张图片，传入 List<String> 即可
     * @param payload
     * @param image
     */
    void responseImage(ParsedPayloadDTO payload, Object image);

    /**
     * 控制 bot 发送视频，接收 String
     * @param payload
     * @param video
     */
    void sendVideo(ParsedPayloadDTO payload, Object video);

    /**
     * 直接向机器人发送原始的 action JSON，不构造 action
     * forward 需要该接口
     * @param selfId
     * @param json
     */
    void sendRawJSON(long selfId, String json);
}
