package com.arth.bot.infrastructure.persistence.mapper;

import com.arth.bot.domain.model.Membership;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author asheo
* @description 针对表【t_membership】的数据库操作Mapper
* @createDate 2025-08-24 13:01:00
* @Entity com.arth.bot.domain.model.Membership
*/
public interface MembershipMapper extends BaseMapper<Membership> {

}




