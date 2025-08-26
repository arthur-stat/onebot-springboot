package com.arth.bot.plugins;


import com.arth.bot.adapter.sender.Sender;
import com.arth.bot.adapter.sender.action.ForwardChainBuilder;
import com.arth.bot.core.authorization.annotation.DirectAuthInterceptor;
import com.arth.bot.core.authorization.model.AuthMode;
import com.arth.bot.core.authorization.model.AuthScope;
import com.arth.bot.core.common.dto.ParsedPayloadDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("plugins.test")
@RequiredArgsConstructor
public class Test {

    private final Sender sender;
    private final ForwardChainBuilder forwardChainBuilder;

    /**
     * 权限测试
     * @param payload
     */
    @DirectAuthInterceptor(
            scope = AuthScope.USER,
            mode  = AuthMode.DENY,
            targets = "1093664084"
    )
    public void quanxian(ParsedPayloadDTO payload) {
        sender.sendText(payload, "test: group not blacklisted -> passed");
    }

    /**
     * 多线程异步测试
     * 参数解析测试
     * @param payload
     */
    public void zuse(ParsedPayloadDTO payload, List<String> args) {
        long ms = 5000L;  // 默认延迟 5 秒
        try {
            if (args != null && !args.isEmpty()) {
                ms = Math.max(0, Long.parseLong(args.get(0)));
            }
        } catch (NumberFormatException ignore) {}

        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        sender.sendText(payload, "test: done after " + ms + " ms");
    }

    /**
     * 回复功能测试
     * @param payload
     */
    public void huifu(ParsedPayloadDTO payload) {
        sender.responseText(payload, "test");
    }

    /**
     * 图片发送测试
     */
    @Value("${server.port}")
    private String port;
    public void tu(ParsedPayloadDTO payload) {
        String url = "http://localhost:" + port + "/saki.jpg";
        sender.responseImage(payload, List.of(url, url, url));
    }

    /**
     * 视频发送测试
     * @param payload
     */
    public void shipin(ParsedPayloadDTO payload) {
        String url = "http://localhost:" + port + "/icsk.mp4";
        sender.sendVideo(payload, url);
    }

    /**
     * 生成转发消息测试
     * @param payload
     */
    public void zhuanfa(ParsedPayloadDTO payload, List<String> args) {
        System.out.println(args.toString());
        long idd = Long.parseLong(args.get(0));
        String json = forwardChainBuilder.create()
                .addRefNode(payload.getGroupId())
                .addCustomNode(idd, args.get(1), n -> n.text(args.get(2)))
                .toGroupJson(payload.getGroupId());

        sender.sendRawJSON(payload.getSelfId(), json);
    }
}