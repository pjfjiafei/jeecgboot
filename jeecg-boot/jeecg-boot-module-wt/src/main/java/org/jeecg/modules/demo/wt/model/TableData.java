package org.jeecg.modules.demo.wt.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("表格数据")
@Data
public class TableData {
    @ApiModelProperty("内容")
    private String textData;
    @ApiModelProperty("颜色")
    private String strColor;
}
