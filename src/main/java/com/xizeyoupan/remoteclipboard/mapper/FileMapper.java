package com.xizeyoupan.remoteclipboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xizeyoupan.remoteclipboard.entity.File;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface FileMapper extends BaseMapper<File> {
}
