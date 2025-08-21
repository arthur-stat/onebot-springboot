package com.arth.bot.service;

public interface ActionBuildService {

    /**
     * 构建纯文本内容的回复群聊 action 的 JSON
     * @param groupId
     * @param text
     * @return
     */
    String buildGroupSendStrAction(long groupId, String text);

    /**
     * 构建纯文本内容的回复私聊 action 的 JSON
     * @param groupId
     * @param text
     * @return
     */
    String buildPrivateSendStrAction(long groupId, String text);
}
