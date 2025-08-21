package com.arth.bot.dto.segment;

import lombok.Data;

import java.util.Map;

@Data
public class AtSegment implements MessageSegment {

    private final String type = "at";

    private Map<String, String> data;
}