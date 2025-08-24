package com.arth.bot.application.routing.impl;

import com.arth.bot.authorization.annotation.DirectAuthInterceptor;
import com.arth.bot.authorization.model.AuthMode;
import com.arth.bot.authorization.model.AuthScope;
import com.arth.bot.common.dto.ParsedPayloadDTO;
import com.arth.bot.application.routing.GroupCommandRoutingService;
import com.arth.bot.common.util.ActionBuildUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Slf4j
@Service
public class GroupCommandRoutingServiceImpl implements GroupCommandRoutingService {

    @Resource
    private ActionBuildUtil actionBuildUtil;

    @Override
    public List<String> hi(ParsedPayloadDTO payload) {
        log.debug("build response: hi!");
        return List.of(actionBuildUtil.buildGroupSendStrAction(payload.getGroupId(), "hi!"));
    }

    @Override
    @DirectAuthInterceptor(scope = AuthScope.USER, mode = AuthMode.DENY, targets = "{1093664084L}")
    public List<String> test1(ParsedPayloadDTO payload) {
        return null;
    }

    @Override
    public List<String> test2(ParsedPayloadDTO payload) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return List.of(actionBuildUtil.buildGroupSendStrAction(payload.getGroupId(), "123456"));
    }
}
