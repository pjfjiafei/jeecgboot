package org.jeecg.modules.entrust.service.impl;

import org.jeecg.modules.entrust.entity.Entrust;
import org.jeecg.modules.entrust.entity.EntrustTestpiece;
import org.jeecg.modules.entrust.mapper.EntrustTestpieceMapper;
import org.jeecg.modules.entrust.mapper.EntrustMapper;
import org.jeecg.modules.entrust.service.IEntrustService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 委托单信息
 * @Author: jeecg-boot
 * @Date:   2021-03-18
 * @Version: V1.0
 */
@Service
public class EntrustServiceImpl extends ServiceImpl<EntrustMapper, Entrust> implements IEntrustService {

	@Autowired
	private EntrustMapper entrustMapper;
	@Autowired
	private EntrustTestpieceMapper entrustTestpieceMapper;
	
	@Override
	@Transactional
	public void saveMain(Entrust entrust, List<EntrustTestpiece> entrustTestpieceList) {
		entrustMapper.insert(entrust);
		if(entrustTestpieceList!=null && entrustTestpieceList.size()>0) {
			for(EntrustTestpiece entity:entrustTestpieceList) {
				//外键设置
				entity.setEntrustId(entrust.getId());
				entrustTestpieceMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional
	public void updateMain(Entrust entrust,List<EntrustTestpiece> entrustTestpieceList) {
		entrustMapper.updateById(entrust);
		
		//1.先删除子表数据
		entrustTestpieceMapper.deleteByMainId(entrust.getId());
		
		//2.子表数据重新插入
		if(entrustTestpieceList!=null && entrustTestpieceList.size()>0) {
			for(EntrustTestpiece entity:entrustTestpieceList) {
				//外键设置
				entity.setEntrustId(entrust.getId());
				entrustTestpieceMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional
	public void delMain(String id) {
		entrustTestpieceMapper.deleteByMainId(id);
		entrustMapper.deleteById(id);
	}

	@Override
	@Transactional
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			entrustTestpieceMapper.deleteByMainId(id.toString());
			entrustMapper.deleteById(id);
		}
	}
	
}
