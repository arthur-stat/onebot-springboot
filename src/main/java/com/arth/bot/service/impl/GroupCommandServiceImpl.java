package com.arth.bot.service.impl;

import com.arth.bot.authorization.annotation.AuthInterceptor;
import com.arth.bot.authorization.model.AuthMode;
import com.arth.bot.authorization.model.AuthScope;
import com.arth.bot.dto.ParsedPayloadDTO;
import com.arth.bot.service.GroupCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class GroupCommandServiceImpl implements GroupCommandService {

    @Resource
    private ActionBuildServiceImpl actionBuildServiceImpl;

    @Override
    public List<String> hi(ParsedPayloadDTO payload) {
        log.debug("build response: hi!");
        return List.of(actionBuildServiceImpl.buildGroupSendStrAction(payload.getGroupId(), "hi!"));
    }

    @Override
    @AuthInterceptor(scope = AuthScope.USER, mode = AuthMode.DENY, targets = "{1093664084L}")
    public List<String> test(ParsedPayloadDTO payload) {
        return null;
    }
}
