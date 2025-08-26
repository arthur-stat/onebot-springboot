package com.arth.bot.adapter.parser;

import com.arth.bot.core.common.dto.ParsedPayloadDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface PayloadParser {

    ParsedPayloadDTO parseRawToDTO(String raw) throws JsonProcessingException;
}
