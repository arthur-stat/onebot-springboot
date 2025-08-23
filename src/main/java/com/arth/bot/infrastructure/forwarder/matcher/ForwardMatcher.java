package com.arth.bot.infrastructure.forwarder.matcher;

import com.arth.bot.common.dto.ParsedPayloadDTO;
import org.springframework.context.annotation.Lazy;

@Lazy
public interface ForwardMatcher {

    boolean matches(ParsedPayloadDTO payload);
}
