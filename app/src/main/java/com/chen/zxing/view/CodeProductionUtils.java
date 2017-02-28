package com.chen.zxing.view;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

public class CodeProductionUtils {

    /**
     * 生成二维码
     *
     * @param content
     * @param size
     * @return
     * @throws WriterException
     */
    public static Bitmap getQrCodeBitmap(String content, int size) throws WriterException {

        return getBitmap(content, size, size, BarcodeFormat.QR_CODE);
    }

    /**
     * 生成条形码
     *
     * @param content
     * @param width
     * @param height
     * @return
     * @throws WriterException
     */
    public static Bitmap getBarCodeBitmap(String content, int width, int height) throws WriterException {
        return getBitmap(content, width, height, BarcodeFormat.CODE_39);
    }

    private static Bitmap getBitmap(String content, int width, int height, BarcodeFormat format) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        Hashtable<EncodeHintType, Object> hst = new Hashtable<>();
        hst.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hst.put(EncodeHintType.MARGIN, 1);
        BitMatrix matrix = writer.encode(content, format, width, height, hst);

        width = matrix.getWidth();
        height = matrix.getHeight();

        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }

}