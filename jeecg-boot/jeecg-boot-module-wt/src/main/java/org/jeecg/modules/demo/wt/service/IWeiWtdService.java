package org.jeecg.modules.demo.wt.service;

import org.jeecg.modules.demo.wt.entity.WuYp;
import org.jeecg.modules.demo.wt.entity.WeiWtd;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 委托单
 * @Author: jeecg-boot
 * @Date:   2021-03-08
 * @Version: V1.0
 */
public interface IWeiWtdService extends IService<WeiWtd> {

	/**
	 * 删除一对多
	 */
	 void delMain (String id);
	
	/**
	 * 批量删除一对多
	 */
	 void delBatchMain (List<String> idList);


    boolean commit(String id);

	boolean reject(String id);
 	void recycleEmpty(List<String> idList);
 	void recycleDelete(String id);

	void recycleRecover(List<String> idList);
	void recycleRecoverOne(String id);
}
