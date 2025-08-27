package com.arth.bot.adapter.forwarder.matcher;

import com.arth.bot.core.common.dto.ParsedPayloadDTO;
import org.springframework.context.annotation.Lazy;

@Lazy
public interface ForwardMatcher {

    boolean matches(ParsedPayloadDTO payload);
}
