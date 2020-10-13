package com.face;

import com.face.utils.ImageUtils;
import com.seetaface2.SeetaFace2JNI;
import com.seetaface2.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * @Author Sugar
 * @Version 2019/4/22 15:56
 */
public class FaceHelper {
    private static Logger logger = LoggerFactory.getLogger(FaceHelper.class);

    private static int CROP_SIZE = 256 * 256 * 3;

    private static SeetaFace2JNI seeta = SeetafaceBuilder.build();

    static {
        ImageIO.setUseCache(false);
    }

    /**
     * 人脸比对
     *
     * @param img1
     * @param img2
     * @return 相似度
     * @throws IOException
     */
    public static float compare(File img1, File img2) throws IOException {
        BufferedImage image1 = ImageIO.read(img1);
        BufferedImage image2 = ImageIO.read(img2);

        return compare(image1, image2);
    }

    /**
     * 人脸比对
     *
     * @param img1
     * @param img2
     * @return 相似度
     */
    public static float compare(byte[] img1, byte[] img2) throws IOException {
        BufferedImage image1 = ImageIO.read(new ByteArrayInputStream(img1));
        BufferedImage image2 = ImageIO.read(new ByteArrayInputStream(img2));
        return compare(image1, image2);
    }

    /**
     * `
     * 人脸比对
     *
     * @param image1
     * @param image2
     * @return 相似度
     */
    public static float compare(BufferedImage image1, BufferedImage image2) {
        if (image1 == null || image2 == null) {
            return 0;
        }
        SeetaImageData imageData1 = new SeetaImageData(image1.getWidth(), image1.getHeight(), 3);
        imageData1.data = ImageUtils.getMatrixBGR(image1);

        SeetaImageData imageData2 = new SeetaImageData(image2.getWidth(), image2.getHeight(), 3);
        imageData2.data = ImageUtils.getMatrixBGR(image2);

        return seeta.compare(imageData1, imageData2);
    }


    /**
     * 人脸提取（裁剪）
     *
     * @param img
     * @return return cropped face
     */
    public static BufferedImage crop(byte[] img) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(img));
        return crop(image);
    }

    /**
     * 人脸提取（裁剪）
     *
     * @param image
     * @return return cropped face
     */
    public static BufferedImage crop(BufferedImage image) {
        if (image == null) {
            return null;
        }
        SeetaImageData imageData = new SeetaImageData(image.getWidth(), image.getHeight(), 3);
        imageData.data = ImageUtils.getMatrixBGR(image);
        byte[] bytes = seeta.crop(imageData);
        if (bytes == null || bytes.length != CROP_SIZE) {
            return null;
        }
        return ImageUtils.bgrToBufferedImage(bytes, 256, 256);
    }

    /**
     * 人脸识别
     *
     * @param img
     * @return
     */
    public static SeetaRect[] detect(byte[] img) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(img));
        return detect(image);
    }

    /**
     * 人脸识别
     *
     * @param image
     * @return
     */
    public static SeetaRect[] detect(BufferedImage image) {
        if (image == null) {
            return null;
        }
        SeetaImageData imageData = new SeetaImageData(image.getWidth(), image.getHeight(), 3);
        imageData.data = ImageUtils.getMatrixBGR(image);
        return seeta.detect(imageData);
    }

    /**
     * 人脸特征识别
     *
     * @param image
     * @return
     */
    public static FaceLandmark detectLandmark(BufferedImage image) {
        if (image == null) {
            return null;
        }
        SeetaImageData imageData = new SeetaImageData(image.getWidth(), image.getHeight(), 3);
        imageData.data = ImageUtils.getMatrixBGR(image);
        SeetaRect[] rects = seeta.detect(imageData);
        if (rects == null) {
            return null;
        }
        FaceLandmark faces = new FaceLandmark();
        faces.rects = rects;
        faces.points = seeta.detect(imageData, rects);
        return faces;
    }
}
