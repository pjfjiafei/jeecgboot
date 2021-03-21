package org.jeecg.modules.demo.wt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.demo.wt.entity.WuYp;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.demo.wt.vo.WuYpVO;

import java.util.List;

/**
 * @Description: 委托样品
 * @Author: jeecg-boot
 * @Date:   2021-03-08
 * @Version: V1.0
 */
public interface IWuYpService extends IService<WuYp> {

	/**根节点父ID的值*/
	 String ROOT_PID_VALUE = "0";

	/**树节点有子节点状态值*/
 	String HASCHILD = "1";

	/**树节点无子节点状态值*/
	 String NOCHILD = "0";

	 List<WuYp> selectByMainId(String mainId);

	/**新增节点*/
	void addTestTree(WuYp wuYp);

	/**修改节点*/
	void updateTestTree(WuYp testTree) throws JeecgBootException;

	/**删除节点*/
	void deleteTestTree(String id) throws JeecgBootException;

	/**查询所有数据，无分页*/
	List<WuYp> queryTreeListNoPage(QueryWrapper<WuYp> queryWrapper);

	List<WuYpVO> buildTree(List<WuYp> nodes);
}
