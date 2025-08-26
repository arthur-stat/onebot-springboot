package com.arth.bot.core.infrastructure.forwarder.matcher.impl;

import com.arth.bot.core.common.dto.ParsedPayloadDTO;
import com.arth.bot.core.infrastructure.forwarder.matcher.ForwardMatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component("defaultForwardMatcher")
@RequiredArgsConstructor
public class DefaultForwardMatcher implements ForwardMatcher {

    @Override
    public boolean matches(ParsedPayloadDTO payload) {
        return true;
    }
}
