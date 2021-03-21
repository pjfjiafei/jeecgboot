package org.jeecg.modules.demo.wt.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Slf4j
public class PdfUtils {

    public static final String FONT="C:/WINDOWS/Fonts/SIMSUN.TTC,1";

    /**
     *
     * @param outputStream  输出流
     * @param templatePath  模板地址
     * @param data          数据
     * @param isExit        生成的文件是否可编辑
     * @throws IOException
     * @throws DocumentException
     */
    public static void export(OutputStream  outputStream, String templatePath, Map<String,String> data,boolean isExit) throws IOException, DocumentException {
        // 1.指定解析器
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        // 2.读入pdf表单
        PdfReader pdfReader = new PdfReader(templatePath);
        //	3.根据表单生成一个新的PDF
        PdfStamper pdfStamper=new PdfStamper(pdfReader,outputStream);
        // 4.获取pdf表单
        AcroFields form=pdfStamper.getAcroFields();
        // 5.给表单添加中文字体 这里采用系统字体，不设置的话 中文可能无法显示
        BaseFont baseFont = BaseFont.createFont(FONT,
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        form.addSubstitutionFont(baseFont);

        data.forEach((k,v)->{
            try {
                form.setField(k, v);
            } catch (IOException | DocumentException e) {
                log.error("PDF导出失败");
                e.printStackTrace();
            }
        });
        pdfStamper.setFormFlattening(isExit);
    }
    private PdfStamper pdfStamper;
    private PdfReader pdfReader;
    private AcroFields fields;

    /**
     * 对pdf进行初始化 必须执行
     * @param outputStream
     * @param templatePath
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    public PdfStamper createPdf(OutputStream  outputStream, String templatePath) throws IOException, DocumentException {
        // 1.指定解析器
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        // 2.读入pdf表单
        pdfReader = new PdfReader(templatePath);
        //	3.根据表单生成一个新的PDF
        pdfStamper=new PdfStamper(pdfReader,outputStream);
        // 4.获取pdf表单
        fields = pdfStamper.getAcroFields();
        // 5.给表单添加中文字体 这里采用系统字体，不设置的话 中文可能无法显示
        BaseFont baseFont = BaseFont.createFont(FONT,
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        fields.addSubstitutionFont(baseFont);
        return pdfStamper;
    }

    /**
     * pdf页面插入数据
     * @param data
     */
    public void pdfInstallData(Map<String,String> data){
        data.forEach((k, v) -> {
            try {
                fields.setField(k, v);
            } catch (IOException | DocumentException e) {
                log.error("数据插入失败");
                e.printStackTrace();
            }
        });
    }

    /**
     * pdf页面插入图片
     * @param imgCode   图片code
     * @param imgPath   图片路径
     * @throws IOException
     * @throws DocumentException
     */
    public void pdfInstallImg(String imgCode,String imgPath) throws IOException, DocumentException {
        int pageNo = fields.getFieldPositions(imgCode).get(0).page;
        Rectangle rectangle = fields.getFieldPositions(imgCode).get(0).position;
        float x = rectangle.getLeft();
        float y = rectangle.getBottom();
        // 根据路径读取图片
        Image image = Image.getInstance(imgPath);
        // 获取图片页面
        PdfContentByte pdfContentByte = pdfStamper.getOverContent(pageNo);
        // 图片大小自适应
        image.scaleToFit(rectangle.getWidth(), rectangle.getHeight());
        // 添加图片
        image.setAbsolutePosition(x, y );
        pdfContentByte.addImage(image);
    }

    /**
     * 插入完数据之后关闭流，最后执行
     */
    public void close(){
        try {
            pdfStamper.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        pdfReader.close();
    }



}
