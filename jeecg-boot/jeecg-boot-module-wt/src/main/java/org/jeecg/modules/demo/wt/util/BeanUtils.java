package org.jeecg.modules.demo.wt.util;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.demo.wt.annotation.WordCode;
import org.jeecg.modules.demo.wt.annotation.WordTable;
import org.jeecg.modules.demo.wt.model.HeaderData;
import org.jeecg.modules.demo.wt.model.TableData;
import org.jeecg.modules.demo.wt.model.WordBookMark;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings("all")
public class BeanUtils {

    /**
     * 插入文本数据  以字段名为书签名，以字段值为内容进行填充
     *
     * @param list 进行填充的数据容器 不可为Null,可为空
     * @param data 填充的数据数据
     */
    public static void wordInsertData(@NotNull List<WordBookMark> list, Object data) {
//        Class<?> clazz = data.getClass();
//        Field[] fields = clazz.getDeclaredFields();
//        for (Field field : fields) {
//            String fieidName = field.getName();
//            if ("serialVersionUID".equals(fieidName)) {
//                continue;
//            }
//            try {
//                Method method = clazz.getDeclaredMethod("get" + org.jeecg.common.util.StringUtils.captureName(fieidName));
//                Object value = method.invoke(data);
//                WordBookMark bookmark = new WordBookMark();
//                bookmark.setBookmarkName(fieidName);
//                if (value instanceof Date) {
//                    JsonFormat jsonFormat = field.getAnnotation(JsonFormat.class);
//                    String pattern = "yyyy-MM-dd HH:mm:ss";
//                    if (jsonFormat != null) {
//                        pattern = jsonFormat.pattern();
//                    }
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//                    String format = simpleDateFormat.format((Date) value);
//                    bookmark.setTextData(format);
//                } else {
//                    bookmark.setTextData((String) value);
//                }
//                bookmark.setBookmarkType("1");
//                list.add(bookmark);
//            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * word插入图片
     * @param list  word数据
     * @param imgCode   图片书签名
     * @param imagePath 图片地址
     * @param length    长度
     * @param height    高度
     * @param textAroundType 类型 1：嵌入式 2：四周型环绕 3：紧密型环绕 4： 衬于文字下方 5：浮于文字上方 6： 上下型环绕 7:穿越型环绕
     * @param leftTop   先找书签所在页，距离该页的左上位置，单位：厘米
     * @param left  先找书签所在页，距离该页的左边位置，单位：厘米
     */
    public static void wordInsertImage(@NotNull List<WordBookMark> list,String imgCode,String imagePath,String length,String height,String textAroundType,String leftTop,String left) {
        WordBookMark wordBookMark = new WordBookMark().builder()
                .bookmarkType("2")
                .bookmarkName(imgCode)
                .imageFilePath(imagePath)
                .length(length)
                .height(height)
                .textAroundType(textAroundType)
                .leftTop(leftTop)
                .left(left)
                .build();
        list.add(wordBookMark);
    }
    /**
     * word插入表格数据
     *
     * @param list
     * @param code         表格书签名
     * @param tableHeaders 表头
     * @param tableData    表格数据
     */
    public static void wordInsertTable(@NotNull List<WordBookMark> list, List<? extends Object> tableData) {
        if (tableData == null|| tableData.isEmpty()) {
            return;
        }

        WordBookMark.WordBookMarkBuilder wordBookMarkBuilder = new WordBookMark()
                .builder();
        String bookmarkName = null;
        // 表头数据
        List<List<HeaderData>> headerDataList = new ArrayList<>();
        List<HeaderData> headerDatas = new ArrayList<>();
        // 表格数据
        List<List<TableData>> tableDataList = new ArrayList<>();
        Class<?> clazz = tableData.get(0).getClass();
        WordCode annotation = clazz.getAnnotation(WordCode.class);
        Assert.notNull(annotation, "表格书签名注解为空");
        bookmarkName = annotation.value();

        Field[] fields = clazz.getDeclaredFields();
        Map<String, Work> map = new HashMap<>();
        for (Field field : fields) {
            WordTable wordTable = field.getAnnotation(WordTable.class);
            if (wordTable == null) {
                continue;
            }
            Work work = new Work();
            // 表格表头
            work.textData = wordTable.value();
            // 表格顺序
            work.solt = wordTable.solt();

            work.b_Bold = wordTable.b_Bold();

            work.color = wordTable.color();

            map.put(field.getName(), work);

        }
        // 根据solt排序之后的map
        Map<String, Work> soltMap = new LinkedHashMap<>();
        map.entrySet()
                .stream()
                .sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue()))
                .collect(Collectors.toList())
                .forEach(ele -> soltMap.put(ele.getKey(), ele.getValue()));

        soltMap.forEach((s, work) ->{
            HeaderData headerData = new HeaderData();
            headerData.setB_Bold(work.b_Bold);
            headerData.setTextData(work.textData);
            headerDatas.add(headerData);
        });

        tableData.forEach(data -> {
            Class<?> aClass = data.getClass();
            List<TableData> tableDatas = new ArrayList<>();
            soltMap.forEach(((fieldName, work) -> {
                        TableData tableCol = null;
                        try {
                            // 表格一个单元格数据
                            tableCol = new TableData();

                            Field field = aClass.getDeclaredField(fieldName);
                            Method getMethod = aClass.getDeclaredMethod("get" + StringUtils.captureName(fieldName));
                            Object value = getMethod.invoke(data);
                            if (value instanceof Date) {
                                JsonFormat jsonFormat = field.getAnnotation(JsonFormat.class);
                                String pattern = "yyyy-MM-dd HH:mm:ss";
                                if (jsonFormat != null) {
                                    pattern = jsonFormat.pattern();
                                }
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String format = simpleDateFormat.format((Date) value);
                                tableCol.setTextData(format);
                            } else {
                                tableCol.setTextData((String) value);
                            }
                            tableCol.setStrColor(work.color);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                        tableDatas.add(tableCol);
                    })
            );
            tableDataList.add(tableDatas);
        });

        headerDataList.add(headerDatas);
        wordBookMarkBuilder
                .headerDataList(headerDataList)
                .bookmarkType("3")
                .bookmarkName(bookmarkName)
                .tableDataList(tableDataList)
                .copyType("3")
                .tableWidthUnitValues("3")
                .creationType("1");
        list.add(wordBookMarkBuilder.build());
    }

    public static String bean2Json(List<WordBookMark> list) {
        JSONArray jsonArray = new JSONArray();
        Object o = jsonArray.toJSON(list);
        return o.toString();
    }

    public static class Work implements Serializable, Comparable<Work> {
        private int solt;
        private String fieldName;
        private boolean b_Bold;
        private String color;
        private String textData;

        @Override
        public int compareTo(Work o) {
            return o.solt - this.solt;
        }

    }

}
