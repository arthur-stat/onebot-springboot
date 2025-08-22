package com.arth.bot.service;

import com.arth.bot.dto.ParsedPayloadDTO;

import java.util.List;

public interface GroupCommandService {

    List<String> hi(ParsedPayloadDTO payload);

    List<String> test(ParsedPayloadDTO payload);
}
