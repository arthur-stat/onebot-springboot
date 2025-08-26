package com.arth.bot.adapter.sender.action.impl;

import com.arth.bot.adapter.sender.action.ActionBuilder;
import com.arth.bot.adapter.sender.action.ForwardChainBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class OneBotForwardChainBuilder implements ForwardChainBuilder {

    private final List<Map<String, Object>> nodes = new ArrayList<>();
    private final ActionBuilder actionBuilder;

    @Autowired
    public OneBotForwardChainBuilder(ActionBuilder actionBuilder) {
        this.actionBuilder = actionBuilder;
    }

    @Override
    public ForwardChainBuilder create() {
        return new OneBotForwardChainBuilder(actionBuilder);
    }

    /**
     * 引用已有消息：data.id
     */
    @Override
    public ForwardChainBuilder addRefNode(long messageId) {
        Map<String, Object> node = new HashMap<>();
        node.put("type", "node");
        node.put("data", Map.of("id", String.valueOf(messageId))); // 用字符串更稳
        nodes.add(node);
        return this;
    }

    /**
     * 自定义节点（“伪造”发送者 + 内容分段）
     */
    @Override
    public ForwardChainBuilder addCustomNode(long uin, String name, Consumer<ForwardChainBuilder.NodeContent> builder) {
        OneBotForwardChainBuilder.NodeContent nc = new OneBotForwardChainBuilder.NodeContent();
        builder.accept(nc);

        Map<String, Object> data = new HashMap<>();
        data.put("uin", String.valueOf(uin));
        data.put("name", name);
        data.put("content", nc.segments);

        Map<String, Object> node = new HashMap<>();
        node.put("type", "node");
        node.put("data", data);
        nodes.add(node);
        return this;
    }

    /**
     * 生成群聊转发消息 JSON
     */
    @Override
    public String toGroupJson(long groupId) {
        return actionBuilder.buildGroupForwardAction(groupId, nodes);
    }

    /**
     * 生成私聊转发消息 JSON
     */
    @Override
    public String toPrivateJson(long userId) {
        return actionBuilder.buildPrivateForwardAction(userId, nodes);
    }

    /**
     * 内部：链式构造节点里的 message segments（text/image/at/...）
     * 此内部类实现了 ForwardChainBuilder.NodeContent 接口
     */
    public static final class NodeContent implements ForwardChainBuilder.NodeContent {
        private final List<Map<String, Object>> segments = new ArrayList<>();

        public NodeContent text(String text) {
            Map<String, Object> seg = new HashMap<>();
            seg.put("type", "text");
            seg.put("data", Map.of("text", text == null ? "" : text));
            segments.add(seg);
            return this;
        }

        public NodeContent image(String file) {
            if (file != null && !file.isBlank()) {
                Map<String, Object> seg = new HashMap<>();
                seg.put("type", "image");
                seg.put("data", Map.of("file", file));
                segments.add(seg);
            }
            return this;
        }

        public NodeContent at(long qq) {
            Map<String, Object> seg = new HashMap<>();
            seg.put("type", "at");
            seg.put("data", Map.of("qq", String.valueOf(qq)));
            segments.add(seg);
            return this;
        }
    }
}