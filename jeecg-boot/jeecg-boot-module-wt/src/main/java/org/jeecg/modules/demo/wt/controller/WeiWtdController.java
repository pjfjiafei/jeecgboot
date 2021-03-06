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
 * @Description: ?????????
 * @Author: jeecg-boot
 * @Date: 2021-03-08
 * @Version: V1.0
 */
@Api(tags = "?????????")
@RestController
@RequestMapping("/wt/weiWtd")
@Slf4j
public class WeiWtdController extends JeecgController<WeiWtd, IWeiWtdService> {

    @Autowired
    private IWeiWtdService weiWtdService;

    @Autowired
    private IWuYpService wuYpService;

    /*---------------------------------???????????????-------------------------------------*/
    @Resource
    private PublicMapper publicMapper;

    @GetMapping("/testSql")
    public Result testSql(@RequestParam("id") String id) {
        String sql = String.format("SELECT\t*\tFROM\twei_wtd\tWHERE\tid=%s", id);
        log.info("sql?????????" + sql);
        HashMap<String, Object> map = publicMapper.getPublicItems(sql);
        return Result.OK(map);
    }

/*    @GetMapping("/testWord")
    public Result testWord(HttpServletResponse response,@RequestParam("id")String id) throws UnsupportedEncodingException {
        String sql=String.format("SELECT * FROM \"wei_wtd\" WHERE \"id\"='%s'",id);
        String wordPath = wordUtils.createWordBySql("?????????", "D:\\WordWebService\\templates\\?????????.docx", sql, false);
        response.setContentType("application/word");
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode("?????????.docx", "UTF-8"));
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
    @AutoLog("?????????Word??????")
    @ApiOperation("?????????Word??????")
    public String testExportWord(HttpServletResponse response, @RequestParam("id") String id) throws IOException, WriterException {
        WeiWtd weiWtd = weiWtdService.getById(id);
        List<WuYp> wuYps = wuYpService.selectByMainId(weiWtd.getId());

        List<WordBookMark> list = new ArrayList<>();
        BeanUtils.wordInsertData(list, weiWtd);
        BeanUtils.wordInsertTable(list, wuYps);
        String imagePath = QRCodeUtils.encode(weiWtd.getWtNum());
        BeanUtils.wordInsertImage(list, "img", imagePath, "2", "2", "3", "1", "10");
        String jsonData = BeanUtils.bean2Json(list);
        String wordPath = wordUtils.createWordByJson("?????????", "D:\\WordWebService\\templates\\hello.docx", jsonData, false);

        log.info(jsonData);
        response.setContentType("application/word");
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode("?????????.docx", "UTF-8"));
        FileCopyUtils.copy(new FileInputStream(wordPath), response.getOutputStream());
        new File(wordPath).delete();
        new File(imagePath).delete();
        return jsonData;
    }


    @GetMapping("/exportWord")
    @AutoLog("?????????Word??????")
    @ApiOperation("?????????Word??????")
    public String exportWord(HttpServletResponse response, @RequestParam("id") String id) throws IOException {
        WeiWtd weiWtd = weiWtdService.getById(id);
        String wordPath = wordUtils.createWord("?????????", "D:\\WordWebService\\templates\\hello.docx", weiWtd, false);
        response.setContentType("application/word");
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode("?????????.docx", "UTF-8"));
        File file = new File(wordPath);
        FileCopyUtils.copy(new FileInputStream(wordPath), response.getOutputStream());
        file.delete();
        return "??????????????????";
    }

    @GetMapping("testexportPdf")
    @AutoLog("?????????PDF??????")
    @ApiOperation("???????????????")
    public String testExportPdf(HttpServletResponse response, @RequestParam("id") String id) throws IOException, WriterException, DocumentException {
        WeiWtd weiWtd = weiWtdService.getById(id);
        log.info("pdf????????????");

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
        String encode = QRCodeUtils.encode("Hello???" + weiWtd.getWuDw());
        pdfUtils.pdfInstallImg("img", encode);
        pdfUtils.close();
        outputStream.close();

        return null;
    }


    @GetMapping("exportPdf")
    @AutoLog("?????????PDF??????")
    @ApiOperation("???????????????")
    public String exportPdf(HttpServletResponse response, @RequestParam("id") String id) throws IOException, WriterException {
        WeiWtd weiWtd = weiWtdService.getById(id);

        log.info("pdf????????????");

        // 1.???????????????
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        String file;

        String path = "C:\\Users\\dell\\Desktop\\template";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode("?????????.pdf", "UTF-8"));
        OutputStream outputStream = null;
        PdfStamper pdfStamper = null;
        PdfReader pdfReader = null;
        String imgPath = QRCodeUtils.encode(weiWtd.getWtNum(), null, path, true);
        try {
            outputStream = response.getOutputStream();
            // 2.??????pdf??????
            pdfReader = new PdfReader(path + "\\" + "test.pdf");
            //	3.??????????????????????????????PDF
            pdfStamper = new PdfStamper(pdfReader, outputStream);
            // 4.??????pdf??????
            AcroFields form = pdfStamper.getAcroFields();
            // 5.??????????????????????????? ?????????????????????????????????????????? ????????????????????????
            BaseFont baseFont = BaseFont.createFont("C:/WINDOWS/Fonts/SIMSUN.TTC,1",
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            form.addSubstitutionFont(baseFont);
            // 6.????????????
            int pageNo = form.getFieldPositions("img").get(0).page;
            Rectangle rectangle = form.getFieldPositions("img").get(0).position;
            float left = rectangle.getLeft();
            float right = rectangle.getRight();
            // ????????????????????????
            Image image = Image.getInstance(imgPath);
            // ??????????????????
            PdfContentByte pdfContentByte = pdfStamper.getOverContent(pageNo);
            // ?????????????????????
            image.scaleToFit(rectangle.getWidth(), rectangle.getHeight());
            // ????????????
            image.setAbsolutePosition(left, right + 210);
            pdfContentByte.addImage(image);

            // ?????????????????????
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
                    log.error("PDF????????????1");
                    e.printStackTrace();
                }
            });

            pdfStamper.setFormFlattening(true);
            log.info("PDF????????????");
        } catch (IOException | DocumentException e) {
            log.error("PDF????????????2");
            e.printStackTrace();
        } finally {
            try {
                pdfStamper.close();
                pdfReader.close();
                outputStream.close();
            } catch (Exception e) {
                log.error("PDF????????????3");
                e.printStackTrace();
            }

        }
        return null;
    }


    @GetMapping("/user")
    @AutoLog(value = "??????????????????")
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    public Result loginUser() {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        return Result.OK(loginUser);
    }

    @GetMapping("/commit")
    @AutoLog(value = "???????????????")
    @ApiOperation(value = "???????????????", notes = "???????????????")
    public Result commit(@RequestParam("id") String id) {
        if (weiWtdService.commit(id)) {
            return Result.OK();
        }
        return Result.error("????????????");
    }

    @GetMapping("/reject")
    @AutoLog(value = "???????????????")
    @ApiOperation(value = "???????????????", notes = "???????????????")
    public Result reject(@RequestParam("id") String id) {
        if (weiWtdService.reject(id)) {
            return Result.OK();
        }
        return Result.error("????????????");
    }
    /*---------------------------------????????????-begin-------------------------------------*/


    @GetMapping("/queryById")
    @AutoLog(value = "??????id????????????")
    @ApiOperation(value = "??????id????????????", notes = "??????id????????????")
    public Result queryById(@RequestParam("id") String id) {
        WeiWtd weiWtd = weiWtdService.getById(id);
        return Result.OK(weiWtd);

    }

    /**
     * ??????????????????
     *
     * @param weiWtd
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "?????????-??????????????????")
    @ApiOperation(value = "?????????-??????????????????", notes = "?????????-??????????????????")
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
     * ??????????????????
     *
     * @param weiWtd
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "?????????-???????????????????????????")
    @ApiOperation(value = "?????????-??????????????????", notes = "?????????-??????????????????")
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

    @AutoLog(value = "?????????-???????????????")
    @ApiOperation(value = "?????????-???????????????", notes = "?????????-???????????????")
    @DeleteMapping(value = "/recycleEmpty")
    public Result<?> recycleEmpty(@RequestParam(name = "ids", required = true) String ids) {
        weiWtdService.recycleEmpty(Arrays.asList(ids.split(",")));
        return Result.OK();
    }

    @AutoLog(value = "?????????-???????????????")
    @ApiOperation(value = "?????????-???????????????", notes = "?????????-???????????????")
    @GetMapping(value = "/recycleRecover")
    public Result<?> recycleRecover(@RequestParam(name = "ids", required = true) String ids) {
        weiWtdService.recycleRecover(Arrays.asList(ids.split(",")));
        return Result.OK();
    }

    /**
     * ??????
     *
     * @param weiWtd
     * @return
     */
    @AutoLog(value = "?????????-??????")
    @ApiOperation(value = "?????????-??????", notes = "?????????-??????")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody WeiWtd weiWtd) {
        weiWtdService.save(weiWtd);
        return Result.OK("???????????????");
    }

    /**
     * ??????
     *
     * @param weiWtd
     * @return
     */
    @AutoLog(value = "?????????-??????")
    @ApiOperation(value = "?????????-??????", notes = "?????????-??????")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody WeiWtd weiWtd) {
        weiWtdService.updateById(weiWtd);
        return Result.OK("????????????!");
    }

    /**
     * ??????id??????
     *
     * @param id
     * @return
     */
    @AutoLog(value = "?????????-??????id??????")
    @ApiOperation(value = "?????????-??????id??????", notes = "?????????-??????id??????")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        weiWtdService.delMain(id);
        return Result.OK("????????????!");
    }

    /**
     * ????????????
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "?????????-????????????")
    @ApiOperation(value = "?????????-????????????", notes = "?????????-????????????")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.weiWtdService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("??????????????????!");
    }

    /**
     * ??????
     *
     * @return
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WeiWtd weiWtd) {
        return super.exportXls(request, weiWtd, WeiWtd.class, "?????????");
    }

    /**
     * ??????
     *
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, WeiWtd.class);
    }
    /*---------------------------------????????????-end-------------------------------------*/


    /*--------------------------------????????????-????????????-begin----------------------------------------------*/

    /**
     * ????????????ID??????
     *
     * @return
     */
    @AutoLog(value = "????????????-????????????ID??????")
    @ApiOperation(value = "????????????-????????????ID??????", notes = "????????????-????????????ID??????")
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
     * ???????????????
     *
     * @param testTree
     * @param req
     * @return
     */
    @AutoLog(value = "?????????-???????????????")
    @ApiOperation(value = "?????????-???????????????", notes = "?????????-???????????????")
    @GetMapping(value = "/childList")
    public Result<?> queryPageList(WuYp testTree, HttpServletRequest req) {
        QueryWrapper<WuYp> queryWrapper = QueryGenerator.initQueryWrapper(testTree, req.getParameterMap());
        List<WuYp> list = wuYpService.list(queryWrapper);
        IPage<WuYp> pageList = new Page<>(1, 10, list.size());
        pageList.setRecords(list);
        return Result.OK(pageList);
    }

    /**
     * ?????????????????????
     *
     * @param parentIds ???ID????????????????????????????????????
     * @param parentIds
     * @return
     */
    @AutoLog(value = "?????????-?????????????????????")
    @ApiOperation(value = "?????????-?????????????????????", notes = "?????????-?????????????????????")
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
            return Result.error("??????????????????????????????" + e.getMessage());
        }
    }


    /**
     * ??????
     *
     * @param wuYp
     * @return
     */
    @AutoLog(value = "????????????-??????")
    @ApiOperation(value = "????????????-??????", notes = "????????????-??????")
    @PostMapping(value = "/addWuYp")
    public Result<?> addWuYp(@RequestBody WuYp wuYp) {
        wuYpService.addTestTree(wuYp);
        return Result.OK("???????????????");
    }

    /**
     * ??????
     *
     * @param wuYp
     * @return
     */
    @AutoLog(value = "????????????-??????")
    @ApiOperation(value = "????????????-??????", notes = "????????????-??????")
    @PutMapping(value = "/editWuYp")
    public Result<?> editWuYp(@RequestBody WuYp wuYp) {
        wuYpService.updateTestTree(wuYp);
        return Result.OK("????????????!");
    }

    /**
     * ??????id??????
     *
     * @param id
     * @return
     */
    @AutoLog(value = "????????????-??????id??????")
    @ApiOperation(value = "????????????-??????id??????", notes = "????????????-??????id??????")
    @DeleteMapping(value = "/deleteWuYp")
    public Result<?> deleteWuYp(@RequestParam(name = "id", required = true) String id) {
        wuYpService.deleteTestTree(id);
        return Result.OK("????????????!");
    }

    /**
     * ????????????
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "????????????-????????????")
    @ApiOperation(value = "????????????-????????????", notes = "????????????-????????????")
    @DeleteMapping(value = "/deleteBatchWuYp")
    public Result<?> deleteBatchWuYp(@RequestParam(name = "ids", required = true) String ids) {
        this.wuYpService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("??????????????????!");
    }

    /**
     * ??????
     *
     * @return
     */
    @RequestMapping(value = "/exportWuYp")
    public ModelAndView exportWuYp(HttpServletRequest request, WuYp wuYp) {
        // Step.1 ??????????????????
        QueryWrapper<WuYp> queryWrapper = QueryGenerator.initQueryWrapper(wuYp, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // Step.2 ??????????????????
        List<WuYp> pageList = wuYpService.list(queryWrapper);
        List<WuYp> exportList = null;

        // ??????????????????
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        } else {
            exportList = pageList;
        }

        // Step.3 AutoPoi ??????Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "????????????"); //???????????????filename?????? ,??????????????????????????????
        mv.addObject(NormalExcelConstants.CLASS, WuYp.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("??????????????????", "?????????:" + sysUser.getRealname(), "????????????"));
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }

    /**
     * ??????
     *
     * @return
     */
    @RequestMapping(value = "/importWuYp/{mainId}")
    public Result<?> importWuYp(HttpServletRequest request, HttpServletResponse response, @PathVariable("mainId") String mainId) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// ????????????????????????
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
                log.info("????????????" + (System.currentTimeMillis() - start) + "??????");
                return Result.OK("????????????????????????????????????" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("??????????????????:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("?????????????????????");
    }

    /*--------------------------------????????????-????????????-end----------------------------------------------*/


}
