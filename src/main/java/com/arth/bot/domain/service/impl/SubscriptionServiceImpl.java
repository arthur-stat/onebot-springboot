package com.arth.bot.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.arth.bot.domain.model.Subscription;
import com.arth.bot.domain.service.SubscriptionService;
import com.arth.bot.infrastructure.persistence.mapper.SubscriptionMapper;
import org.springframework.stereotype.Service;

/**
* @author asheo
* @description 针对表【t_subscription】的数据库操作Service实现
* @createDate 2025-08-24 13:01:00
*/
@Service
public class SubscriptionServiceImpl extends ServiceImpl<SubscriptionMapper, Subscription>
    implements SubscriptionService{

}




