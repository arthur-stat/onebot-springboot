package com.arth.bot.adapter.sender.action;

public interface ActionBuilder {

    /**
     * 构建纯文本内容的回复群聊 action 的 JSON
     * @param groupId
     * @param text
     * @return
     */
    String buildGroupSendTextAction(long groupId, String text);

    /**
     * 构建纯文本内容的回复私聊 action 的 JSON
     * @param userId
     * @param text
     * @return
     */
    String buildPrivateSendTextAction(long userId, String text);
}
