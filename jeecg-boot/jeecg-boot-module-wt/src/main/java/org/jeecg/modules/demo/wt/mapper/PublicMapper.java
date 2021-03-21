package org.jeecg.modules.demo.wt.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;

@Mapper
public interface PublicMapper{
    HashMap<String,Object> getPublicItems(@Param("sqlStr")String sql);
}
