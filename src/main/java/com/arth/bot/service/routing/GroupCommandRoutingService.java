package com.arth.bot.service.routing;

import com.arth.bot.common.dto.ParsedPayloadDTO;

import java.util.List;

public interface GroupCommandRoutingService {

    List<String> hi(ParsedPayloadDTO payload);

    List<String> test(ParsedPayloadDTO payload);
}
