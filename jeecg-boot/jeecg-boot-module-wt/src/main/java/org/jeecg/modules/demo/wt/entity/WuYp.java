package org.jeecg.modules.demo.wt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.jeecg.modules.demo.wt.annotation.WordCode;
import org.jeecg.modules.demo.wt.annotation.WordTable;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 委托样品
 * @Author: jeecg-boot
 * @Date:   2021-03-08
 * @Version: V1.0
 */
@Data
@ToString
@TableName("wu_yp")
@ApiModel(value="wei_wtd对象", description="委托单")
@WordCode("表格")
public class WuYp implements Serializable {
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
    @WordTable(value = "创建日期",solt = 3)
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    @WordTable(value = "更新人",solt = 5)
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    @WordTable(value = "更新日期",b_Bold = true,color = "yellow",solt = 4)
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    @WordTable(value = "所属部门",solt = 2)
    private String sysOrgCode;
	/**样品名称*/
	@Excel(name = "样品名称", width = 15)
    @ApiModelProperty(value = "样品名称")
    @WordTable(value = "样品名称",b_Bold = true,color = "red",solt = 1)
    private java.lang.String name;
	/**主表id*/
    @ApiModelProperty(value = "主表id")
    private java.lang.String fid;
	/**文件数量*/
	@Excel(name = "文件数量", width = 15)
    @ApiModelProperty(value = "文件数量")
    private java.lang.String fileNum;
	/**样品数量*/
	@Excel(name = "样品数量", width = 15)
    @ApiModelProperty(value = "样品数量")
    private java.lang.String ypNum;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
    @ApiModelProperty(value = "是否删除")
    private java.lang.String wuDel;

	@ApiModelProperty(value = "父id")
    private String pid;
	@ApiModelProperty(value = "是否有子节点")
    private String hasChild;
}
