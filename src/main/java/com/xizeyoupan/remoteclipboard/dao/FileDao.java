package com.xizeyoupan.remoteclipboard.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xizeyoupan.remoteclipboard.entity.File;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDao extends BaseMapper<File> {
}
