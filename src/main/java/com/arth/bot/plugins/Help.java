package com.arth.bot.plugins;

import com.arth.bot.adapter.sender.Sender;
import com.arth.bot.core.common.dto.ParsedPayloadDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("plugins.help")
@RequiredArgsConstructor
public class Help {

    private final Sender sender;

    public void index(ParsedPayloadDTO payload) {
        sender.responseText(payload, """
                这是一个基于 OneBot v11 协议的框架（本身是框架不是 bot），目前支持以下命令：
                
                - test:
                    - quanxian: 测试鉴权切面，硬编码仅允许 1093664084
                    - zuse <time>: 测试多线程异步，支持输入的时间参数 <time>
                    - huifu: 测试 bot 回复
                    - tu: 测试发图
                    - shipin: 测试发视频
                    - zhuanfa <QQid> <QQname> <text>: 测试链式构造合并转发消息，可以伪造消息记录
                    - yinyong: 测试 bot 获取图片引用消息
                
                项目地址为：https://github.com/arthur-stat/onebot-springboot
                """);
    }
}