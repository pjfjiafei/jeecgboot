package org.jeecg.modules.demo.wt.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("表头")
@Data
public class HeaderData {
    @ApiModelProperty("表头内容")
    private String textData;
    @ApiModelProperty("是否黑体加粗")
    private boolean b_Bold;
}
