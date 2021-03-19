package org.jeecg.modules.entrust.service.impl;

import org.jeecg.modules.entrust.entity.EntrustTestpiece;
import org.jeecg.modules.entrust.mapper.EntrustTestpieceMapper;
import org.jeecg.modules.entrust.service.IEntrustTestpieceService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 被试件表
 * @Author: jeecg-boot
 * @Date:   2021-03-18
 * @Version: V1.0
 */
@Service
public class EntrustTestpieceServiceImpl extends ServiceImpl<EntrustTestpieceMapper, EntrustTestpiece> implements IEntrustTestpieceService {
	
	@Autowired
	private EntrustTestpieceMapper entrustTestpieceMapper;
	
	@Override
	public List<EntrustTestpiece> selectByMainId(String mainId) {
		return entrustTestpieceMapper.selectByMainId(mainId);
	}
}
