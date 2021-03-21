package org.jeecg.modules.demo.wt.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.wt.entity.WeiWtd;
import org.jeecg.modules.demo.wt.entity.WuYp;
import org.jeecg.modules.demo.wt.mapper.PublicMapper;
import org.jeecg.modules.demo.wt.model.WordBookMark;
import org.jeecg.modules.demo.wt.service.IWeiWtdService;
import org.jeecg.modules.demo.wt.service.IWuYpService;
import org.jeecg.modules.demo.wt.util.BeanUtils;
import org.jeecg.modules.demo.wt.util.PdfUtils;
import org.jeecg.modules.demo.wt.util.QRCodeUtils;
import org.jeecg.modules.demo.wt.util.WordUtils;
import org.jeecg.modules.demo.wt.vo.WuYpVO;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 委托单
 * @Author: jeecg-boot
 * @Date: 2021-03-08
 * @Version: V1.0
 */
@Api(tags = "委托单")
@RestController
@RequestMapping("/wt/weiWtd")
@Slf4j
public class WeiWtdController extends JeecgController<WeiWtd, IWeiWtdService> {

    @Autowired
    private IWeiWtdService weiWtdService;

    @Autowired
    private IWuYpService wuYpService;

    /*---------------------------------委托单下载-------------------------------------*/
    @Resource
    private PublicMapper publicMapper;

    @GetMapping("/testSql")
    public Result testSql(@RequestParam("id") String id) {
        String sql = String.format("SELECT\t*\tFROM\twei_wtd\tWHERE\tid=%s", id);
        log.info("sql语句：" + sql);
        HashMap<String, Object> map = publicMapper.getPublicItems(sql);
        return Result.OK(map);
    }

/*    @GetMapping("/testWord")
    public Result testWord(HttpServletResponse response,@RequestParam("id")String id) throws UnsupportedEncodingException {
        String sql=String.format("SELECT * FROM \"wei_wtd\" WHERE \"id\"='%s'",id);
        String wordPath = wordUtils.createWordBySql("委托单", "D:\\WordWebService\\templates\\委托单.docx", sql, false);
        response.setContentType("application/word");
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode("委托单.docx", "UTF-8"));
        File file = new File(wordPath);
        try (OutputStream outputStream = response.getOutputStream();
             InputStream inputStream = new FileInputStream(file)
        ) {
            byte[] bytes = new byte[1024];
            while (inputStream.read(bytes) != -1) {
                outputStream.write(bytes, 0, bytes.length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            new File(wordPath).delete();
        }
        return Result.OK();
    }*/

    @Autowired
    private WordUtils wordUtils;

    @GetMapping("/testexportWord")
    @AutoLog("委托单Word下载")
    @ApiOperation("委托单Word下载")
    public String testExportWord(HttpServletResponse response, @RequestParam("id") String id) throws IOException, WriterException {
        WeiWtd weiWtd = weiWtdService.getById(id);
        List<WuYp> wuYps = wuYpService.selectByMainId(weiWtd.getId());

        List<WordBookMark> list = new ArrayList<>();
        BeanUtils.wordInsertData(list, weiWtd);
        BeanUtils.wordInsertTable(list, wuYps);
        String imagePath = QRCodeUtils.encode(weiWtd.getWtNum());
        BeanUtils.wordInsertImage(list, "img", imagePath, "2", "2", "3", "1", "10");
        String jsonData = BeanUtils.bean2Json(list);
        String wordPath = wordUtils.createWordByJson("委托单", "D:\\WordWebService\\templates\\hello.docx", jsonData, false);

        log.info(jsonData);
        response.setContentType("application/word");
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode("委托单.docx", "UTF-8"));
        FileCopyUtils.copy(new FileInputStream(wordPath), response.getOutputStream());
        new File(wordPath).delete();
        new File(imagePath).delete();
        return jsonData;
    }


    @GetMapping("/exportWord")
    @AutoLog("委托单Word下载")
    @ApiOperation("委托单Word下载")
    public String exportWord(HttpServletResponse response, @RequestParam("id") String id) throws IOException {
        WeiWtd weiWtd = weiWtdService.getById(id);
        String wordPath = wordUtils.createWord("委托单", "D:\\WordWebService\\templates\\hello.docx", weiWtd, false);
        response.setContentType("application/word");
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode("委托单.docx", "UTF-8"));
        File file = new File(wordPath);
        FileCopyUtils.copy(new FileInputStream(wordPath), response.getOutputStream());
        file.delete();
        return "委托单名称啊";
    }

    @GetMapping("testexportPdf")
    @AutoLog("委托单PDF下载")
    @ApiOperation("委托单下载")
    public String testExportPdf(HttpServletResponse response, @RequestParam("id") String id) throws IOException, WriterException, DocumentException {
        WeiWtd weiWtd = weiWtdService.getById(id);
        log.info("pdf开始导出");

        PdfUtils pdfUtils = new PdfUtils();
        ServletOutputStream outputStream = response.getOutputStream();
        pdfUtils.createPdf(outputStream, "C:\\Users\\dell\\Desktop\\template\\hello.pdf");
        Map<String, String> data = new HashMap<>();
        data.put("wur", weiWtd.getWur());
        data.put("wtNum", weiWtd.getWtNum());
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.CHINA);

        data.put("wcTime", dateFormat.format(weiWtd.getWcTime()));
        data.put("wtTime", dateFormat.format(weiWtd.getWtTime()));
        data.put("kuName", weiWtd.getKuName());
        data.put("status", weiWtd.getStatus());
        data.put("sjr", weiWtd.getSjr());
        data.put("wuCsxm", weiWtd.getWuCsxm());
        data.put("wurPhone", weiWtd.getWurPhone());
        data.put("wuDw", weiWtd.getWuDw());

        pdfUtils.pdfInstallData(data);
        String encode = QRCodeUtils.encode("Hello啊" + weiWtd.getWuDw());
        pdfUtils.pdfInstallImg("img", encode);
        pdfUtils.close();
        outputStream.close();

        return null;
    }


    @GetMapping("exportPdf")
    @AutoLog("委托单PDF下载")
    @ApiOperation("委托单下载")
    public String exportPdf(HttpServletResponse response, @RequestParam("id") String id) throws IOException, WriterException {
        WeiWtd weiWtd = weiWtdService.getById(id);

        log.info("pdf开始导出");

        // 1.指定解析器
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        String file;

        String path = "C:\\Users\\dell\\Desktop\\template";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode("委托单.pdf", "UTF-8"));
        OutputStream outputStream = null;
        PdfStamper pdfStamper = null;
        PdfReader pdfReader = null;
        String imgPath = QRCodeUtils.encode(weiWtd.getWtNum(), null, path, true);
        try {
            outputStream = response.getOutputStream();
            // 2.读入pdf表单
            pdfReader = new PdfReader(path + "\\" + "test.pdf");
            //	3.根据表单生成一个新的PDF
            pdfStamper = new PdfStamper(pdfReader, outputStream);
            // 4.获取pdf表单
            AcroFields form = pdfStamper.getAcroFields();
            // 5.给表单添加中文字体 这里采用系统字体，不设置的话 中文可能无法显示
            BaseFont baseFont = BaseFont.createFont("C:/WINDOWS/Fonts/SIMSUN.TTC,1",
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            form.addSubstitutionFont(baseFont);
            // 6.查询数据
            int pageNo = form.getFieldPositions("img").get(0).page;
            Rectangle rectangle = form.getFieldPositions("img").get(0).position;
            float left = rectangle.getLeft();
            float right = rectangle.getRight();
            // 根据路径读取图片
            Image image = Image.getInstance(imgPath);
            // 获取图片页面
            PdfContentByte pdfContentByte = pdfStamper.getOverContent(pageNo);
            // 图片大小自适应
            image.scaleToFit(rectangle.getWidth(), rectangle.getHeight());
            // 添加图片
            image.setAbsolutePosition(left, right + 210);
            pdfContentByte.addImage(image);

            // 添加完图片删除
            new File(imgPath).delete();

            Map<String, String> data = new HashMap<>();
            data.put("wur", weiWtd.getWur());
            data.put("wtNum", weiWtd.getWtNum());
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.CHINA);

            data.put("wcTime", dateFormat.format(weiWtd.getWcTime()));
            data.put("wtTime", dateFormat.format(weiWtd.getWtTime()));
            data.put("kuName", weiWtd.getKuName());
            data.put("status", weiWtd.getStatus());
            data.put("sjr", weiWtd.getSjr());
            data.put("wuCsxm", weiWtd.getWuCsxm());
            data.put("wurPhone", weiWtd.getWurPhone());
            data.put("wuDw", weiWtd.getWuDw());
            data.forEach((k, v) -> {
                try {
                    form.setField(k, v);
                } catch (IOException | DocumentException e) {
                    log.error("PDF导出失败1");
                    e.printStackTrace();
                }
            });

            pdfStamper.setFormFlattening(true);
            log.info("PDF导出完成");
        } catch (IOException | DocumentException e) {
            log.error("PDF导出失败2");
            e.printStackTrace();
        } finally {
            try {
                pdfStamper.close();
                pdfReader.close();
                outputStream.close();
            } catch (Exception e) {
                log.error("PDF导出失败3");
                e.printStackTrace();
            }

        }
        return null;
    }


    @GetMapping("/user")
    @AutoLog(value = "获取用户信息")
    @ApiOperation(value = "获取用户信息", notes = "获取用户信息")
    public Result loginUser() {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        return Result.OK(loginUser);
    }

    @GetMapping("/commit")
    @AutoLog(value = "委托单提交")
    @ApiOperation(value = "委托单提交", notes = "委托单提交")
    public Result commit(@RequestParam("id") String id) {
        if (weiWtdService.commit(id)) {
            return Result.OK();
        }
        return Result.error("提交失败");
    }

    @GetMapping("/reject")
    @AutoLog(value = "委托单撤回")
    @ApiOperation(value = "委托单提交", notes = "委托单提交")
    public Result reject(@RequestParam("id") String id) {
        if (weiWtdService.reject(id)) {
            return Result.OK();
        }
        return Result.error("提交失败");
    }
    /*---------------------------------主表处理-begin-------------------------------------*/


    @GetMapping("/queryById")
    @AutoLog(value = "根据id查询一条")
    @ApiOperation(value = "根据id查询一条", notes = "根据id查询一条")
    public Result queryById(@RequestParam("id") String id) {
        WeiWtd weiWtd = weiWtdService.getById(id);
        return Result.OK(weiWtd);

    }

    /**
     * 分页列表查询
     *
     * @param weiWtd
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "委托单-分页列表查询")
    @ApiOperation(value = "委托单-分页列表查询", notes = "委托单-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(WeiWtd weiWtd,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<WeiWtd> queryWrapper = QueryGenerator.initQueryWrapper(weiWtd, req.getParameterMap());
        queryWrapper.lambda().eq(WeiWtd::getWuDel, "0");
        Page<WeiWtd> page = new Page<WeiWtd>(pageNo, pageSize);
        IPage<WeiWtd> pageList = weiWtdService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 分页列表查询
     *
     * @param weiWtd
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "委托单-回收箱分页列表查询")
    @ApiOperation(value = "委托单-分页列表查询", notes = "委托单-分页列表查询")
    @GetMapping(value = "/recyclelist")
    public Result<?> queryRecyclePageList(WeiWtd weiWtd,
                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                          HttpServletRequest req) {
        QueryWrapper<WeiWtd> queryWrapper = QueryGenerator.initQueryWrapper(weiWtd, req.getParameterMap());
        queryWrapper.lambda().eq(WeiWtd::getWuDel, "1");
        Page<WeiWtd> page = new Page<WeiWtd>(pageNo, pageSize);
        IPage<WeiWtd> pageList = weiWtdService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    @AutoLog(value = "委托单-回收箱清空")
    @ApiOperation(value = "委托单-回收箱清空", notes = "委托单-回收箱清空")
    @DeleteMapping(value = "/recycleEmpty")
    public Result<?> recycleEmpty(@RequestParam(name = "ids", required = true) String ids) {
        weiWtdService.recycleEmpty(Arrays.asList(ids.split(",")));
        return Result.OK();
    }

    @AutoLog(value = "委托单-回收箱恢复")
    @ApiOperation(value = "委托单-回收箱恢复", notes = "委托单-回收箱恢复")
    @GetMapping(value = "/recycleRecover")
    public Result<?> recycleRecover(@RequestParam(name = "ids", required = true) String ids) {
        weiWtdService.recycleRecover(Arrays.asList(ids.split(",")));
        return Result.OK();
    }

    /**
     * 添加
     *
     * @param weiWtd
     * @return
     */
    @AutoLog(value = "委托单-添加")
    @ApiOperation(value = "委托单-添加", notes = "委托单-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody WeiWtd weiWtd) {
        weiWtdService.save(weiWtd);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param weiWtd
     * @return
     */
    @AutoLog(value = "委托单-编辑")
    @ApiOperation(value = "委托单-编辑", notes = "委托单-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody WeiWtd weiWtd) {
        weiWtdService.updateById(weiWtd);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "委托单-通过id删除")
    @ApiOperation(value = "委托单-通过id删除", notes = "委托单-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        weiWtdService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "委托单-批量删除")
    @ApiOperation(value = "委托单-批量删除", notes = "委托单-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.weiWtdService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 导出
     *
     * @return
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WeiWtd weiWtd) {
        return super.exportXls(request, weiWtd, WeiWtd.class, "委托单");
    }

    /**
     * 导入
     *
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, WeiWtd.class);
    }
    /*---------------------------------主表处理-end-------------------------------------*/


    /*--------------------------------子表处理-委托样品-begin----------------------------------------------*/

    /**
     * 通过主表ID查询
     *
     * @return
     */
    @AutoLog(value = "委托样品-通过主表ID查询")
    @ApiOperation(value = "委托样品-通过主表ID查询", notes = "委托样品-通过主表ID查询")
    @GetMapping(value = "/listWuYpByMainId")
    public Result<?> listWuYpByMainId(WuYp wuYp,
                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                      HttpServletRequest req) {
        QueryWrapper<WuYp> queryWrapper = QueryGenerator.initQueryWrapper(wuYp, req.getParameterMap());
        Page<WuYp> page = new Page<WuYp>(pageNo, pageSize);
        IPage<WuYp> pageList = wuYpService.page(page, queryWrapper);

        Page<WuYpVO> wuYpVOPage = new Page<>();


        List<WuYpVO> treeList = wuYpService.buildTree(pageList.getRecords());

        org.springframework.beans.BeanUtils.copyProperties(pageList, wuYpVOPage);
        wuYpVOPage.setRecords(treeList);
        return Result.OK(wuYpVOPage);
    }

    /**
     * 获取子数据
     *
     * @param testTree
     * @param req
     * @return
     */
    @AutoLog(value = "测试树-获取子数据")
    @ApiOperation(value = "测试树-获取子数据", notes = "测试树-获取子数据")
    @GetMapping(value = "/childList")
    public Result<?> queryPageList(WuYp testTree, HttpServletRequest req) {
        QueryWrapper<WuYp> queryWrapper = QueryGenerator.initQueryWrapper(testTree, req.getParameterMap());
        List<WuYp> list = wuYpService.list(queryWrapper);
        IPage<WuYp> pageList = new Page<>(1, 10, list.size());
        pageList.setRecords(list);
        return Result.OK(pageList);
    }

    /**
     * 批量查询子节点
     *
     * @param parentIds 父ID（多个采用半角逗号分割）
     * @param parentIds
     * @return
     */
    @AutoLog(value = "测试树-批量获取子数据")
    @ApiOperation(value = "测试树-批量获取子数据", notes = "测试树-批量获取子数据")
    @GetMapping("/getChildListBatch")
    public Result getChildListBatch(@RequestParam("parentIds") String parentIds) {
        try {
            QueryWrapper<WuYp> queryWrapper = new QueryWrapper<>();
            List<String> parentIdList = Arrays.asList(parentIds.split(","));
            queryWrapper.in("pid", parentIdList);
            List<WuYp> list = wuYpService.list(queryWrapper);
            IPage<WuYp> pageList = new Page<>(1, 10, list.size());
            pageList.setRecords(list);
            return Result.OK(pageList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("批量查询子节点失败：" + e.getMessage());
        }
    }


    /**
     * 添加
     *
     * @param wuYp
     * @return
     */
    @AutoLog(value = "委托样品-添加")
    @ApiOperation(value = "委托样品-添加", notes = "委托样品-添加")
    @PostMapping(value = "/addWuYp")
    public Result<?> addWuYp(@RequestBody WuYp wuYp) {
        wuYpService.addTestTree(wuYp);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param wuYp
     * @return
     */
    @AutoLog(value = "委托样品-编辑")
    @ApiOperation(value = "委托样品-编辑", notes = "委托样品-编辑")
    @PutMapping(value = "/editWuYp")
    public Result<?> editWuYp(@RequestBody WuYp wuYp) {
        wuYpService.updateTestTree(wuYp);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "委托样品-通过id删除")
    @ApiOperation(value = "委托样品-通过id删除", notes = "委托样品-通过id删除")
    @DeleteMapping(value = "/deleteWuYp")
    public Result<?> deleteWuYp(@RequestParam(name = "id", required = true) String id) {
        wuYpService.deleteTestTree(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "委托样品-批量删除")
    @ApiOperation(value = "委托样品-批量删除", notes = "委托样品-批量删除")
    @DeleteMapping(value = "/deleteBatchWuYp")
    public Result<?> deleteBatchWuYp(@RequestParam(name = "ids", required = true) String ids) {
        this.wuYpService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 导出
     *
     * @return
     */
    @RequestMapping(value = "/exportWuYp")
    public ModelAndView exportWuYp(HttpServletRequest request, WuYp wuYp) {
        // Step.1 组装查询条件
        QueryWrapper<WuYp> queryWrapper = QueryGenerator.initQueryWrapper(wuYp, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // Step.2 获取导出数据
        List<WuYp> pageList = wuYpService.list(queryWrapper);
        List<WuYp> exportList = null;

        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        } else {
            exportList = pageList;
        }

        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "委托样品"); //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.CLASS, WuYp.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("委托样品报表", "导出人:" + sysUser.getRealname(), "委托样品"));
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }

    /**
     * 导入
     *
     * @return
     */
    @RequestMapping(value = "/importWuYp/{mainId}")
    public Result<?> importWuYp(HttpServletRequest request, HttpServletResponse response, @PathVariable("mainId") String mainId) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<WuYp> list = ExcelImportUtil.importExcel(file.getInputStream(), WuYp.class, params);
                for (WuYp temp : list) {
                    temp.setFid(mainId);
                }
                long start = System.currentTimeMillis();
                wuYpService.saveBatch(list);
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                return Result.OK("文件导入成功！数据行数：" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    /*--------------------------------子表处理-委托样品-end----------------------------------------------*/


}
