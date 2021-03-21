package org.jeecg.modules.demo.wt.util;

import cn.hutool.extra.qrcode.BufferedImageLuminanceSource;
import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.UUID;

@Slf4j
public class QRCodeUtils {
    private static final String CHARSET = "UTF-8";
    private static final String FORMAT_NAME = "JPG";
    //  二维码尺寸
    private static final int QRCODE_SIZE=300;
    // LOGO宽度
    private static final int WIDTH=60;
    //  LOGO高度
    private static final int HEIGHT=60;

    /**
     *  生成二维码
     * @param context 内容
     * @param imgPath LOGO地址
     * @param destPath 存放目录
     * @param needCompress 是否压缩LOGO
     * @return 二维码地址
     * @throws IOException
     * @throws WriterException
     */
    public static String encode(String context, String imgPath, String destPath, boolean needCompress) throws IOException, WriterException {
        BufferedImage image = QRCodeUtils.createImage(context, imgPath, needCompress);
        mkdirs(destPath);
        String filePath = UUID.randomUUID().toString() + ".jpg";
        ImageIO.write(image, FORMAT_NAME, new File(destPath + "/" + filePath));
        return destPath + "/" + filePath;
    }

    /**
     * 生成二维码
     * @param context  内容
     * @return 二维码地址
     * @throws IOException
     * @throws WriterException
     */
    public static String encode(String context) throws IOException, WriterException {
        String destPath = System.getProperty("user.dir");
        return encode(context,null,destPath,false);
    }
    /**
     * 创建文件夹
     * @param destPath
     */
    public static void mkdirs(String destPath) {
        File file = new File(destPath);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }

    public static String decode(String path)throws Exception {
        return QRCodeUtils.decode(new File(path));
    }

    public static String decode(File file) throws IOException, NotFoundException {
        BufferedImage image;
        image = ImageIO.read(file);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;

        Hashtable<DecodeHintType, Object> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        result = new MultiFormatReader().decode(binaryBitmap, hints);
        String resultStr=result.getText();
        return resultStr;
    }


    private static BufferedImage createImage(String context, String imgPath, boolean needCompress) throws WriterException, IOException {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(context, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x,y,bitMatrix.get(x,y)? 0xFF000000: 0xFFFFFFFF );
            }
        }
        if (imgPath == null || "".equals(imgPath)) {
            return image;
        }
        // 插入图片
        QRCodeUtils.insertImage(image, imgPath, needCompress);
        return image;
    }

    private static void insertImage(BufferedImage image, String imgPath, boolean needCompress) throws IOException {
        File file = new File(imgPath);
        if (!file.exists()) {
            log.error("[{}]该文件不存在",imgPath);
            return;
        }
        Image src = ImageIO.read(new File(imgPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        // 压缩LOGO
        if (needCompress) {
            if (width > WIDTH) {
                width=WIDTH;
            }
            if (height > HEIGHT) {
                height = HEIGHT;
            }
            Image img = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = bufferedImage.getGraphics();
            graphics.drawImage(img, 0, 0, null);// 绘制图
            graphics.dispose();
            src=img;
        }
        //  插入LOGO
        Graphics2D graphics = image.createGraphics();
        int x=(QRCODE_SIZE-width)/2;
        int y=(QRCODE_SIZE-height)/2;
        graphics.drawImage(src, x, y, width, height, null);
        Shape  shape = new RoundRectangle2D.Float(x, y, width, height, 6, 6);
        graphics.setStroke(new BasicStroke(3f));
        graphics.draw(shape);
        graphics.dispose();
    }


}
