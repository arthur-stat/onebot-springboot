package com.arth.bot.application.routing.impl;

import com.arth.bot.common.util.ActionBuildUtil;
import com.arth.bot.application.routing.PrivateCommandRoutingService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class PrivateCommandRoutingServiceImpl implements PrivateCommandRoutingService {

    @Resource
    private ActionBuildUtil actionBuildUtil;
}
