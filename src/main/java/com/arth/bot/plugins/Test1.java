package com.arth.bot.plugins;


import com.arth.bot.adapter.sender.Sender;
import com.arth.bot.core.authorization.annotation.DirectAuthInterceptor;
import com.arth.bot.core.authorization.model.AuthMode;
import com.arth.bot.core.authorization.model.AuthScope;
import com.arth.bot.core.common.dto.ParsedPayloadDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("plugins.test1")
@RequiredArgsConstructor
public class Test1 {

    private final Sender sender;

    @DirectAuthInterceptor(
            scope = AuthScope.USER,
            mode  = AuthMode.DENY,
            targets = "1093664084"
    )
    public void index(ParsedPayloadDTO payload) {
        sender.sendText(payload, "test1: group not blacklisted -> passed");
    }
}