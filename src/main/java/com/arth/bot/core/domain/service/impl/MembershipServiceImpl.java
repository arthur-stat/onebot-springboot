package com.arth.bot.core.domain.service.impl;

import com.arth.bot.core.domain.model.Membership;
import com.arth.bot.core.domain.service.MembershipService;
import com.arth.bot.core.mapper.MembershipMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @author asheo
* @description 针对表【t_membership】的数据库操作Service实现
* @createDate 2025-08-24 13:01:00
*/
@Service
public class MembershipServiceImpl extends ServiceImpl<MembershipMapper, Membership>
    implements MembershipService{

}




