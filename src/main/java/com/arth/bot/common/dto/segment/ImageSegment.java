package com.arth.bot.common.dto.segment;

import lombok.Data;

import java.util.Map;

@Data
public class ImageSegment implements MessageSegment {

    private final String type = "image";

    private Map<String, String> data;
}