package com.xizeyoupan.remoteclipboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xizeyoupan.remoteclipboard.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
