package org.jeecg.modules.demo.wt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.demo.wt.entity.WeiWtd;
import org.jeecg.modules.demo.wt.entity.WuYp;
import org.jeecg.modules.demo.wt.mapper.WuYpMapper;
import org.jeecg.modules.demo.wt.mapper.WeiWtdMapper;
import org.jeecg.modules.demo.wt.service.IWeiWtdService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 委托单
 * @Author: jeecg-boot
 * @Date:   2021-03-08
 * @Version: V1.0
 */
@Service
public class WeiWtdServiceImpl extends ServiceImpl<WeiWtdMapper, WeiWtd> implements IWeiWtdService {

	@Resource
	private WeiWtdMapper weiWtdMapper;
	@Resource
	private WuYpMapper wuYpMapper;
	
	@Override
	@Transactional
	public void delMain(String id) {
		wuYpMapper.deleteByMainId(id);
		WeiWtd weiWtd = new WeiWtd();
		weiWtd.setWuDel("1");
		weiWtd.setId(id);
		weiWtdMapper.updateById(weiWtd);
	}

	@Override
	@Transactional
	public void delBatchMain(List<String>idList) {
		for(String id:idList) {
			this.delMain(id);
		}
	}

	@Override
	public boolean commit(String id) {
		QueryWrapper<WuYp> queryWrapper = new QueryWrapper<>();
		queryWrapper.lambda().eq(WuYp::getFid,id);
		Integer integer = wuYpMapper.selectCount(queryWrapper);
		if (integer > 0) {
			UpdateWrapper<WeiWtd> updateWrapper = new UpdateWrapper<>();
			updateWrapper.lambda()
					.set(WeiWtd::getStatus,1)
					.eq(WeiWtd::getId,id);
			this.update(updateWrapper);
			return  true;
		}
		return false;
	}

	@Override
	public boolean reject(String id) {
		UpdateWrapper<WeiWtd> updateWrapper = new UpdateWrapper<>();
		updateWrapper.lambda().set(WeiWtd::getStatus,0).eq(WeiWtd::getId,id);
		this.update(updateWrapper);
		return true;
	}
	// 清空垃圾箱
	@Override
	public void recycleEmpty(List<String> idList) {
		idList.forEach(this::recycleDelete);
	}

	@Override
	public void recycleDelete(String id) {
		weiWtdMapper.deleteById(id);
		QueryWrapper<WuYp> queryWrapper = new QueryWrapper<>();
		queryWrapper.lambda().eq(WuYp::getFid, id);
		wuYpMapper.delete(queryWrapper);
	}

	@Override
	public void recycleRecover(List<String> idList) {
		idList.forEach(this::recycleRecoverOne);
	}

	@Override
	public void recycleRecoverOne(String id) {
		WeiWtd weiWtd = new WeiWtd();
		weiWtd.setId(id);
		weiWtd.setWuDel("0");
		weiWtdMapper.updateById(weiWtd);
		UpdateWrapper<WuYp> updateWrapper = new UpdateWrapper<>();
		updateWrapper.lambda().eq(WuYp::getFid,id);
		WuYp wuYp = new WuYp();
		wuYp.setWuDel("0");
		wuYpMapper.update(wuYp,updateWrapper);

	}
}
