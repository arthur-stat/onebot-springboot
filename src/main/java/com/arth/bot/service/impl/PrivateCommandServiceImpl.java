package com.arth.bot.service.impl;

import com.arth.bot.service.ActionBuildService;
import com.arth.bot.service.PrivateCommandService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class PrivateCommandServiceImpl implements PrivateCommandService {

    @Resource
    private ActionBuildService actionBuildService;
}
