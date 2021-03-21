package org.jeecg.modules.demo.wt.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.jeecg.modules.demo.wt.entity.WuYp;

import java.io.Serializable;
import java.util.List;

@ApiModel("样品")
@Data
@ToString
public class WuYpVO extends WuYp implements Serializable {
    @ApiModelProperty("子")
    private List<WuYpVO> children;
}
