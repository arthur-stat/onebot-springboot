package com.arth.bot.dto;

import lombok.Data;

import java.util.Map;

@Data
public class OneBotReturnActionDTO {

    private String action;

    private Map<String, Object> params;

    private String echo;
}
