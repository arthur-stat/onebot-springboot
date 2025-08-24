package com.arth.bot.infrastructure.persistence.mapper;

import com.arth.bot.domain.model.Group;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author asheo
* @description 针对表【t_group】的数据库操作Mapper
* @createDate 2025-08-24 13:01:00
* @Entity com.arth.bot.domain.model.Group
*/
@Mapper
public interface GroupMapper extends BaseMapper<Group> {

}




