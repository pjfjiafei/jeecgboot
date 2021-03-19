package org.jeecg.modules.entrust.service;

import org.jeecg.modules.entrust.entity.EntrustTestpiece;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 被试件表
 * @Author: jeecg-boot
 * @Date:   2021-03-18
 * @Version: V1.0
 */
public interface IEntrustTestpieceService extends IService<EntrustTestpiece> {

	public List<EntrustTestpiece> selectByMainId(String mainId);
}
