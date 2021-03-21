package org.jeecg.modules.demo.wt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.wt.entity.WuYp;
import org.jeecg.modules.demo.wt.mapper.WuYpMapper;
import org.jeecg.modules.demo.wt.service.IWuYpService;
import org.jeecg.modules.demo.wt.vo.WuYpVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 委托样品
 * @Author: jeecg-boot
 * @Date:   2021-03-08
 * @Version: V1.0
 */
@Service
public class WuYpServiceImpl extends ServiceImpl<WuYpMapper, WuYp> implements IWuYpService {
	
	@Resource
	private WuYpMapper wuYpMapper;
	
	@Override
	public List<WuYp> selectByMainId(String mainId) {
		return wuYpMapper.selectByMainId(mainId);
	}

	@Override
	public void addTestTree(WuYp wuYp) {
		if(oConvertUtils.isEmpty(wuYp.getPid())){
			wuYp.setPid(IWuYpService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			WuYp parent = baseMapper.selectById(wuYp.getPid());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
		baseMapper.insert(wuYp);
	}

	@Override
	public void updateTestTree(WuYp wuYp) {
		WuYp entity = this.getById(wuYp.getId());
		if(entity==null) {
			throw new JeecgBootException("未找到对应实体");
		}
		String old_pid = entity.getPid();
		String new_pid = wuYp.getPid();
		if(!old_pid.equals(new_pid)) {
			updateOldParentNode(old_pid);
			if(oConvertUtils.isEmpty(new_pid)){
				wuYp.setPid(IWuYpService.ROOT_PID_VALUE);
			}
			if(!IWuYpService.ROOT_PID_VALUE.equals(wuYp.getPid())) {
				baseMapper.updateTreeNodeStatus(wuYp.getPid(), IWuYpService.HASCHILD);
			}
		}
		baseMapper.updateById(wuYp);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteTestTree(String id) throws JeecgBootException {
		//查询选中节点下所有子节点一并删除
		id = this.queryTreeChildIds(id);
		if(id.indexOf(",")>0) {
			StringBuffer sb = new StringBuffer();
			String[] idArr = id.split(",");
			for (String idVal : idArr) {
				if(idVal != null){
					WuYp testTree = this.getById(idVal);
					String pidVal = testTree.getPid();
					//查询此节点上一级是否还有其他子节点
					List<WuYp> dataList = baseMapper.selectList(new QueryWrapper<WuYp>().eq("pid", pidVal).notIn("id", Arrays.asList(idArr)));
					if((dataList == null || dataList.size()==0) && !Arrays.asList(idArr).contains(pidVal)
							&& !sb.toString().contains(pidVal)){
						//如果当前节点原本有子节点 现在木有了，更新状态
						sb.append(pidVal).append(",");
					}
				}
			}
			//批量删除节点
			baseMapper.deleteBatchIds(Arrays.asList(idArr));
			//修改已无子节点的标识
			String[] pidArr = sb.toString().split(",");
			for(String pid : pidArr){
				this.updateOldParentNode(pid);
			}
		}else{
			WuYp testTree = this.getById(id);
			if(testTree==null) {
				throw new JeecgBootException("未找到对应实体");
			}
			updateOldParentNode(testTree.getPid());
			baseMapper.deleteById(id);
		}
	}

	@Override
	public List<WuYp> queryTreeListNoPage(QueryWrapper<WuYp> queryWrapper) {
		List<WuYp> dataList = baseMapper.selectList(queryWrapper);
		List<WuYp> mapList = new ArrayList<>();
		for(WuYp data : dataList){
			String pidVal = data.getPid();
			//递归查询子节点的根节点
			if(pidVal != null && !"0".equals(pidVal)){
				WuYp rootVal = this.getTreeRoot(pidVal);
				if(rootVal != null && !mapList.contains(rootVal)){
					mapList.add(rootVal);
				}
			}else{
				if(!mapList.contains(data)){
					mapList.add(data);
				}
			}
		}
		return mapList;
	}

	/**
	 * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
	 * @param pid
	 */
	private void updateOldParentNode(String pid) {
		if(!IWuYpService.ROOT_PID_VALUE.equals(pid)) {
			Integer count = baseMapper.selectCount(new QueryWrapper<WuYp>().eq("pid", pid));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, IWuYpService.NOCHILD);
			}
		}
	}

	/**
	 * 递归查询节点的根节点
	 * @param pidVal
	 * @return
	 */
	private WuYp getTreeRoot(String pidVal){
		WuYp data =  baseMapper.selectById(pidVal);
		if(data != null && !"0".equals(data.getPid())){
			return this.getTreeRoot(data.getPid());
		}else{
			return data;
		}
	}

	/**
	 * 根据id查询所有子节点id
	 * @param ids
	 * @return
	 */
	private String queryTreeChildIds(String ids) {
		//获取id数组
		String[] idArr = ids.split(",");
		StringBuffer sb = new StringBuffer();
		for (String pidVal : idArr) {
			if(pidVal != null){
				if(!sb.toString().contains(pidVal)){
					if(sb.toString().length() > 0){
						sb.append(",");
					}
					sb.append(pidVal);
					this.getTreeChildIds(pidVal,sb);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 递归查询所有子节点
	 * @param pidVal
	 * @param sb
	 * @return
	 */
	private StringBuffer getTreeChildIds(String pidVal,StringBuffer sb){
		List<WuYp> dataList = baseMapper.selectList(new QueryWrapper<WuYp>().eq("pid", pidVal));
		if(dataList != null && dataList.size()>0){
			for(WuYp tree : dataList) {
				if(!sb.toString().contains(tree.getId())){
					sb.append(",").append(tree.getId());
				}
				this.getTreeChildIds(tree.getId(),sb);
			}
		}
		return sb;
	}
	@Override
	public List<WuYpVO> buildTree(List<WuYp> nodes){
		List<WuYpVO> list = toVo(nodes);
		List<WuYpVO> treeList = new ArrayList<>();
		List<WuYpVO> rootNodes = getRootNodes(list);
		for (WuYpVO rootNode:rootNodes) {
			buildChildNodes(list,rootNode);
			treeList.add(rootNode);
		}
		return treeList;
	}

	private List<WuYpVO> toVo(List<WuYp> wuYpList){
		if (wuYpList == null) {
			return new ArrayList<>();
		}
		List<WuYpVO> list=new ArrayList<>();
		wuYpList.forEach(wuYp -> {
			WuYpVO wuYpVO = new WuYpVO();
			BeanUtils.copyProperties(wuYp,wuYpVO);
			list.add(wuYpVO);
		});
		return list;
	}




	private void buildChildNodes(List<WuYpVO> nodes, WuYpVO rootNode) {
		List<WuYpVO> children= getChildNodes(nodes, rootNode);
		if (!children.isEmpty()) {
			for (WuYpVO child:children) {
				buildChildNodes(nodes,child);
			}
			rootNode.setChildren(children);
		}

	}

	private List<WuYpVO> getChildNodes(List<WuYpVO> nodes, WuYpVO rootNode) {
		List<WuYpVO> childNodes = new ArrayList<>();
		for (WuYpVO n:nodes) {
			if (rootNode.getId().equals(n.getPid())) {
				childNodes.add(n);
			}
		}
		return childNodes;
	}

	private List<WuYpVO> getRootNodes(List<WuYpVO> nodes) {
		List<WuYpVO> rootNodes = new ArrayList<>();
		nodes.forEach(n->{
			if (rootNode(nodes,n)) {
				rootNodes.add(n);
			}
		});
		return rootNodes;
	}

	/**
	 * 判断是否是节点
	 * @param nodes
	 * @param n
	 * @return
	 */
	private boolean rootNode(List<WuYpVO> nodes,WuYpVO n) {
		boolean isRootNode=true;
		for (WuYpVO node:nodes) {
			if ("0".equals(n.getPid())) {
				break;
			}
			if (node.getId().equals(n.getPid())) {
				isRootNode=false;
				break;
			}
		}
		return isRootNode;
	}


}
