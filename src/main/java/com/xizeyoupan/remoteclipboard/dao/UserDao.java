package com.xizeyoupan.remoteclipboard.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xizeyoupan.remoteclipboard.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends BaseMapper<User> {
}
