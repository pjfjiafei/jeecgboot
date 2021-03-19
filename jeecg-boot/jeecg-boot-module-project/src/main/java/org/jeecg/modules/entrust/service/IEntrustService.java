package org.jeecg.modules.entrust.service;

import org.jeecg.modules.entrust.entity.EntrustTestpiece;
import org.jeecg.modules.entrust.entity.Entrust;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 委托单信息
 * @Author: jeecg-boot
 * @Date:   2021-03-18
 * @Version: V1.0
 */
public interface IEntrustService extends IService<Entrust> {

	/**
	 * 添加一对多
	 * 
	 */
	public void saveMain(Entrust entrust,List<EntrustTestpiece> entrustTestpieceList) ;
	
	/**
	 * 修改一对多
	 * 
	 */
	public void updateMain(Entrust entrust,List<EntrustTestpiece> entrustTestpieceList);
	
	/**
	 * 删除一对多
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);
	
}
