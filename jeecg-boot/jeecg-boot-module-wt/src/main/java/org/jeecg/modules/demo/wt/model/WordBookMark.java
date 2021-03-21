package org.jeecg.modules.demo.wt.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@ApiModel("WORD书签封装实体")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WordBookMark {
    @ApiModelProperty("书签类型 1：文字 2：图片 3：表格")
    private String bookmarkType;
    @ApiModelProperty("书签名称")
    private String bookmarkName;
    @ApiModelProperty("内容")
    private String textData;
    @ApiModelProperty("图片长度")
    private String length;
    @ApiModelProperty("图片高度")
    private String height;
    @ApiModelProperty("类型 1：嵌入式 2：四周型环绕 3：紧密型环绕 4： 衬于文字下方 5：浮于文字上方 6： 上下型环绕 7:穿越型环绕")
    private String textAroundType;
    @ApiModelProperty("先找书签所在页，距离该页的左上位置，单位：厘米")
    private String leftTop;
    @ApiModelProperty("先找书签所在页，距离该页的左边位置，单位：厘米")
    private String left;

    @ApiModelProperty
    private String copyType;
    @ApiModelProperty()
    private String justType;
    @ApiModelProperty()
    private String b_NoChangeAspect;
    @ApiModelProperty("图片大小")
    private String imageSize;
    @ApiModelProperty("图片地址")
    private String imageFilePath;
    @ApiModelProperty()
    private String b_Top;
    @ApiModelProperty("图片名称")
    private ImageName imageName;
    @ApiModelProperty()
    private String copyStartBookmarkName;
    @ApiModelProperty()
    private String copyEndBookmarkName;
    @ApiModelProperty()
    private String b_OneAddPage;
    @ApiModelProperty()
    private String b_AddPage;
    @ApiModelProperty()
    private String pageAllRowNum;
    @ApiModelProperty()
    private String pageColumnNum;
    @ApiModelProperty()
    private String startPageColumnNo;
    @ApiModelProperty()
    private String endPageColumnNo;
    @ApiModelProperty()
    private String b_complementColumn;
    @ApiModelProperty()
    private String startColumnNum;
    @ApiModelProperty()
    private String tableWidthUnitValues;
    @ApiModelProperty()
    private String tableVerticalAlignmentValues;
    @ApiModelProperty()
    private String creationType;
    @ApiModelProperty()
    private String tableWidth;
    @ApiModelProperty("表头")
    private List<List<HeaderData>> headerDataList;
    @ApiModelProperty("表格数据")
    private List<List<TableData>> tableDataList;

}
