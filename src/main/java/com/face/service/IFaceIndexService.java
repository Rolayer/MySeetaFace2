package com.face.service;

import com.face.dto.Result;
import com.leenleda.common.dto.vo.PatientInfoVo;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * @Author: pengbenlei
 * @Date: 2020/9/23 14:56
 * @Description:
 */
public interface IFaceIndexService {

    void loadFaceDb();

    void buildIndex();

    /**
     * 注册人脸(会对人脸进行裁剪)
     *
     * @param key 人脸照片唯一标识
     * @param img 人脸照片
     * @return
     * @throws IOException
     */
    boolean faceRegister(String key, byte[] img,String patientSourceCode);

    /**
     * 注册人脸(不裁剪图片)
     *
     * @param key   人脸照片唯一标识
     * @param image 人脸照片
     * @return
     * @throws IOException
     */
    boolean faceRegister(String key, BufferedImage image);

    /**
     * 搜索人脸
     *
     * @param img 人脸照片
     * @return
     * @throws IOException
     */
    Result search(byte[] img) throws IOException;

    /**
     * 删除已注册的人脸
     *
     * @param keys
     */
    void removeRegister(String... keys);

    void clear();

    /**
     * 卡号是否被注册
     * */
    boolean exist(String patientCode);

    /**
     * 根据卡号获取患者
     * */
    List<PatientInfoVo> getPatientsByCode(String patientCode, String ip);
}
