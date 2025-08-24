package com.arth.bot.application.routing;

import com.arth.bot.common.dto.ParsedPayloadDTO;

import java.util.List;

public interface GroupCommandRoutingService {

    List<String> hi(ParsedPayloadDTO payload);

    List<String> test1(ParsedPayloadDTO payload);

    List<String> test2(ParsedPayloadDTO payload);
}
