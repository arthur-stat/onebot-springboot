package com.arth.bot.service.routing.impl;

import com.arth.bot.authorization.annotation.AuthInterceptor;
import com.arth.bot.authorization.model.AuthMode;
import com.arth.bot.authorization.model.AuthScope;
import com.arth.bot.common.dto.ParsedPayloadDTO;
import com.arth.bot.service.routing.GroupCommandRoutingService;
import com.arth.bot.utils.ActionBuildUtil;
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
    @AuthInterceptor(scope = AuthScope.USER, mode = AuthMode.DENY, targets = "{1093664084L}")
    public List<String> test(ParsedPayloadDTO payload) {
        return null;
    }
}
