package com.arth.bot.service.routing.impl;

import com.arth.bot.utils.ActionBuildUtil;
import com.arth.bot.service.routing.PrivateCommandRoutingService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class PrivateCommandRoutingServiceImpl implements PrivateCommandRoutingService {

    @Resource
    private ActionBuildUtil actionBuildUtil;
}
