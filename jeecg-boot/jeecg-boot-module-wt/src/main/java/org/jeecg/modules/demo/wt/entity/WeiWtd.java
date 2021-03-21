package org.jeecg.modules.demo.wt.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.jeecgframework.poi.excel.annotation.Excel;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 委托单
 * @Author: jeecg-boot
 * @Date:   2021-03-08
 * @Version: V1.0
 */
@Data
@TableName("wei_wtd")
@ApiModel(value="wei_wtd对象", description="委托单")
public class WeiWtd implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**客户名称*/
    @Excel(name = "客户名称", width = 15)
    @ApiModelProperty(value = "客户名称")
    private String kuName;
	/**委托单号*/
    @Excel(name = "委托单号", width = 15)
    @ApiModelProperty(value = "委托单号")
    private java.lang.String wtNum;
	/**送件人*/
    @Excel(name = "送件人", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "送件人")
    private java.lang.String sjr;
	/**状态*/
    @Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    private java.lang.String status;
	/**委托日期*/
    @Excel(name = "委托日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "委托日期")
    private java.util.Date wtTime;
	/**委托完成日期*/
    @Excel(name = "委托完成日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "委托完成日期")
    private Date wcTime;
	/**是否删除*/
    @Excel(name = "是否删除", width = 15)
    @ApiModelProperty(value = "是否删除")
    private String wuDel;
	/**委托单位*/
    @Excel(name = "委托单位", width = 15, dicCode = "org_category")
    @Dict(dicCode = "org_category")
    @ApiModelProperty(value = "委托单位")
    private java.lang.String wuDw;
	/**测试项目*/
    @Excel(name = "测试项目", width = 15)
    @ApiModelProperty(value = "测试项目")
    private java.lang.String wuCsxm;
	/**委托人*/
    @Excel(name = "委托人", width = 15)
    @ApiModelProperty(value = "委托人")
    private java.lang.String wur;
	/**委托人电话*/
    @Excel(name = "委托人电话", width = 15)
    @ApiModelProperty(value = "委托人电话")
    private java.lang.String wurPhone;
}
