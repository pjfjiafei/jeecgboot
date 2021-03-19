package org.jeecg.modules.entrust.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 被试件表
 * @Author: jeecg-boot
 * @Date:   2021-03-18
 * @Version: V1.0
 */
@ApiModel(value="entrust对象", description="委托单信息")
@Data
@TableName("entrust_testpiece")
public class EntrustTestpiece implements Serializable {
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
	/**被试件编号*/
	@Excel(name = "被试件编号", width = 15)
    @ApiModelProperty(value = "被试件编号")
    private java.lang.String testpieceCode;
	/**被试件名称*/
	@Excel(name = "被试件名称", width = 15)
    @ApiModelProperty(value = "被试件名称")
    private java.lang.String testpieceName;
	/**被试件数量*/
	@Excel(name = "被试件数量", width = 15)
    @ApiModelProperty(value = "被试件数量")
    private java.lang.String testpieceNumbei;
	/**外键*/
    @ApiModelProperty(value = "外键")
    private java.lang.String entrustId;
}
