package org.jeecg.modules.demo.wt.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("图片名称")
public class ImageName {
    @ApiModelProperty("图片标题")
    private String textData;
    @ApiModelProperty("")
    private String justType;
}
