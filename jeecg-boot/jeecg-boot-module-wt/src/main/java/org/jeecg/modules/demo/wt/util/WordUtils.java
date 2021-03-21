package org.jeecg.modules.demo.wt.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.demo.wt.mapper.PublicMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class WordUtils {
    @Value("${webServiceUrl}")
    private String webServiceUrl;
    @Resource
    private PublicMapper publicMapper;


    // 创建文档
    public static final String CREATE_WORD_URL = "/CreateWord";
    // 合并报告
    public static final String MERGE_WORD_URL = "/MergeWord";
    // 合并报告504三期待用
    public static final String MERGE_WORD_URL504 = "/MergeWord";
    // 合并word
    public static final String MERGEWORD_OUTDOC="/MergeWord_OutDoc";


    public  String createWordBySql(String fileName,String templateUrl, String sql, boolean deleteBookmarkData)   {
        HashMap<String, Object> map = publicMapper.getPublicItems(sql);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(map);
        String jsonData = jsonArray.toString();
        return createWordByJson(fileName,templateUrl,jsonData,deleteBookmarkData);
    }

    /**
     *  生成WORD文档
     * @param fileName  文件名
     * @param templateUrl 模板地址
     * @param jsonData    json数据
     * @param deleteBookmarkData    是否删除未操作的书签
     * @return  文档生成地址
     */
    public  String createWordByJson(String fileName,String templateUrl, String jsonData, boolean deleteBookmarkData)   {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        String outputPath = getOutputPath(fileName);
        map.add("outputDirPath",outputPath );
        map.add("templateFilePath", templateUrl);
        map.add("jsonData",jsonData);
        map.add("deleteBookmarkData", Boolean.toString(deleteBookmarkData));
        Result result = httpClientPost(webServiceUrl + CREATE_WORD_URL, map);
        boolean success = result.isSuccess();
        if (!success) {
            log.error("系统请求web-service服务失败",result);
        }
        return outputPath;
    }

    private String getOutputPath(String fileName) {
        String outputDirPath;
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        String path = System.getProperty("user.dir");
        if (fileName == null || "".equals(fileName)) {
            fileName= UUID.randomUUID().toString();
        }
        outputDirPath = path +"\\"+ fileName + ".docx";
        map.add("outputDirPath", outputDirPath);
        return outputDirPath;
    }
    /**
     *  合并报告
     * @param fileName  文件名称
     * @param jsonData  输入Json
     * @param sqlJson   更新数据库进度JSON
     * @param deleteFile    是否删除合并文件列表
     * @param disposeImageName  是否处理图片名称
     * @return
     */
    public String MergeWordByJson(String fileName, String jsonData, String sqlJson,boolean deleteFile,boolean disposeImageName) {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        String outputPath = getOutputPath(fileName);
        map.add("outputDirPath", outputPath);
        map.add("jsonData", jsonData);
        map.add("sqlJson",sqlJson);
        map.add("deleteFile", Boolean.toString(deleteFile));
        map.add("disposeImageName", Boolean.toString(disposeImageName));
        Result result = httpClientPost(webServiceUrl + MERGE_WORD_URL, map);
        boolean success = result.isSuccess();
        if (!success) {
            log.error("系统请求web-service服务失败",result);
        }
        return outputPath;
    }
    /**
     *  合并报告2007
     * @param fileName  文件名称
     * @param jsonData  输入Json
     * @param sqlJson   更新数据库进度JSON
     * @param deleteFile    是否删除合并文件列表
     * @return
     */
    public String MergeWord2007ByJson(String fileName, String jsonData, String sqlJson,boolean deleteFile ) {
        String outputDirPath;
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        String path = System.getProperty("user.dir");
        if (fileName == null || "".equals(fileName)) {
            fileName= UUID.randomUUID().toString();
        }
        outputDirPath = path +"\\"+ fileName + ".docx";
        map.add("outputDirPath", outputDirPath);
        map.add("jsonData", jsonData);
        map.add("sqlJson",sqlJson);
        map.add("deleteFile", Boolean.toString(deleteFile));
        Result result = httpClientPost(webServiceUrl + MERGE_WORD_URL504, map);
        boolean success = result.isSuccess();
        if (!success) {
            log.error("系统请求web-service服务失败",result);
        }
        return outputDirPath;
    }
    /**
     *  合并报告
     * @param fileName  文件名称
     * @param jsonData  输入Json
     * @param sqlJson   更新数据库进度JSON
     * @param deleteFile   是否删除合并文件列表
     * @return
     */
    public String MergeWordOutDocxByJson(String fileName, String jsonData, String sqlJson,boolean deleteFile ) {
        String outputDirPath;
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        String path = System.getProperty("user.dir");
        if (fileName == null || "".equals(fileName)) {
            fileName= UUID.randomUUID().toString();
        }
        outputDirPath = path +"\\"+ fileName + ".docx";
        map.add("outputDirPath", outputDirPath);
        map.add("jsonData", jsonData);
        map.add("sqlJson",sqlJson);
        map.add("deleteFile", Boolean.toString(deleteFile));
        Result result = httpClientPost(webServiceUrl + MERGEWORD_OUTDOC, map);
        boolean success = result.isSuccess();
        if (!success) {
            log.error("系统请求web-service服务失败",result);
        }
        return outputDirPath;
    }


    /**
     *  生成WORD文档
     * @param templateUrl   模板地址
     * @param data      数据
     * @param deleteBookmarkData    是否删除未操作的书签
     * @return
     */
    public  String createWord(String fileName,String templateUrl, Object data, boolean deleteBookmarkData) {
        return createWordByJson(fileName,templateUrl, resolver(data), deleteBookmarkData);
    }

    private  String resolver(Object data) {
//        List<Bookmark> list = new ArrayList<>();
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
//                Bookmark bookmark = new Bookmark();
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
//
//        }
//        Object o = JSONArray.toJSON(list);
//        return o.toString();
        return null;
    }
    /**
     * 发送POST请求
     * @param url   地址
     * @param map   参数
     * @return  请求body
     */
    private Result httpClientPost(String url,LinkedMultiValueMap<String, String> map){
        RestTemplate restTemplate = new RestTemplate();
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        HttpEntity<MultiValueMap> request = new HttpEntity<>(map, header);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        String body = exchange.getBody();
        Document document ;
        Result result;
        try {
            document = DocumentHelper.parseText(body);
            Element rootElement = document.getRootElement();
            String text = rootElement.getText();
            result = JSONObject.parseObject(text, Result.class);
        } catch (DocumentException e) {
            result=Result.error("系统请求web-service服务失败");
            e.printStackTrace();
        }
        return result;
    }

    @Data
    public static class Bookmark {
        String bookmarkType;
        String bookmarkName;
        String textData;
    }
}
