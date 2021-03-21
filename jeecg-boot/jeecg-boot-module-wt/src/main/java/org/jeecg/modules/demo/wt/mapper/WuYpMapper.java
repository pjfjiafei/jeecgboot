package org.jeecg.modules.demo.wt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.wt.entity.WuYp;

import java.util.List;

/**
 * @Description: 委托样品
 * @Author: jeecg-boot
 * @Date:   2021-03-08
 * @Version: V1.0
 */
public interface WuYpMapper extends BaseMapper<WuYp> {

	 boolean deleteByMainId(@Param("mainId") String mainId);

	 List<WuYp> selectByMainId(@Param("mainId") String mainId);

    void updateTreeNodeStatus(String pid, String nochild);
}
