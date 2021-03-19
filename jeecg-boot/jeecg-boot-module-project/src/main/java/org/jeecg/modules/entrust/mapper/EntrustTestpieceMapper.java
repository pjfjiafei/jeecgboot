package org.jeecg.modules.entrust.mapper;

import java.util.List;
import org.jeecg.modules.entrust.entity.EntrustTestpiece;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 被试件表
 * @Author: jeecg-boot
 * @Date:   2021-03-18
 * @Version: V1.0
 */
public interface EntrustTestpieceMapper extends BaseMapper<EntrustTestpiece> {

	public boolean deleteByMainId(@Param("mainId") String mainId);
    
	public List<EntrustTestpiece> selectByMainId(@Param("mainId") String mainId);
}
