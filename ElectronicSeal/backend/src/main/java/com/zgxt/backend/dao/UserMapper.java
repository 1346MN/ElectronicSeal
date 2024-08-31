package com.zgxt.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zgxt.backend.entity.UserEntity;
import org.mapstruct.Mapper;

/**
 * 用户映射器接口
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
