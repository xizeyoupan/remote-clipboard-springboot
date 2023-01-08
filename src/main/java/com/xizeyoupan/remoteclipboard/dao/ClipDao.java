package com.xizeyoupan.remoteclipboard.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xizeyoupan.remoteclipboard.entity.Clip;
import org.springframework.stereotype.Repository;

@Repository
public interface ClipDao extends BaseMapper<Clip> {
}
