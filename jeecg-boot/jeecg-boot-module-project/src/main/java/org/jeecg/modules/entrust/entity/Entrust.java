package org.jeecg.modules.entrust.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 委托单信息
 * @Author: jeecg-boot
 * @Date:   2021-03-18
 * @Version: V1.0
 */
@ApiModel(value="entrust对象", description="委托单信息")
@Data
@TableName("entrust")
public class Entrust implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	/**委托单编号*/
	@Excel(name = "委托单编号", width = 15)
    @ApiModelProperty(value = "委托单编号")
    private java.lang.String entrustCode;
	/**委托单名称*/
	@Excel(name = "委托单名称", width = 15)
    @ApiModelProperty(value = "委托单名称")
    private java.lang.String entrustName;
	/**委托单位*/
	@Excel(name = "委托单位", width = 15, dictTable = "sys_depart", dicText = "depart_name", dicCode = "id")
    @Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "id")
    @ApiModelProperty(value = "委托单位")
    private java.lang.String entrustDept;
	/**委托人*/
	@Excel(name = "委托人", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "委托人")
    private java.lang.String entrustUser;
	/**计划开始时间*/
	@Excel(name = "计划开始时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始时间")
    private java.util.Date planStartDate;
	/**计划结束时间*/
	@Excel(name = "计划结束时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "计划结束时间")
    private java.util.Date planEndDate;
	/**委托人联系电话*/
	@Excel(name = "委托人联系电话", width = 15)
    @ApiModelProperty(value = "委托人联系电话")
    private java.lang.String phoneNumber;
	/**测试项目*/
	@Excel(name = "测试项目", width = 15, dicCode = "testItems")
    @Dict(dicCode = "testItems")
    @ApiModelProperty(value = "测试项目")
    private java.lang.String testItems;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "entrust_status")
    @Dict(dicCode = "entrust_status")
    @ApiModelProperty(value = "状态")
    private java.lang.String status;
	/**密级*/
	@Excel(name = "密级", width = 15, dicCode = "classification")
    @Dict(dicCode = "classification")
    @ApiModelProperty(value = "密级")
    private java.lang.String classification;
}
