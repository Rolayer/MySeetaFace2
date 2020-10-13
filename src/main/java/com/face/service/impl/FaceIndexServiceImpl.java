package com.face.service.impl;

import com.face.SeetafaceBuilder;
import com.face.dto.Result;
import com.face.service.IFaceIndexService;
import com.face.utils.ImageUtils;
import com.leenleda.common.data.FaceIndex;
import com.leenleda.common.data.Screen;
import com.leenleda.common.data.Triage;
import com.leenleda.common.dto.vo.PatientInfoVo;
import com.leenleda.common.mapper.FaceIndexMapper;
import com.leenleda.common.mapper.PatientMapper;
import com.leenleda.common.mapper.ScreenMapper;
import com.leenleda.common.mapper.TriageMapper;
import com.seetaface2.SeetaFace2JNI;
import com.seetaface2.model.RecognizeResult;
import com.seetaface2.model.SeetaImageData;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: pengbenlei
 * @Date: 2020/9/23 14:56
 * @Description:
 */
@Service
public class FaceIndexServiceImpl implements IFaceIndexService {

    @Resource
    FaceIndexMapper faceIndexMapper;

    @Resource
    PatientMapper patientMapper;

    @Resource
    TriageMapper triageMapper;

    @Resource
    ScreenMapper screenMapper;

    private static int CROP_SIZE = 256 * 256 * 3;

    private SeetaFace2JNI seeta = SeetafaceBuilder.build();

    /**
     * 加载人脸库
     */
    @Override
    public void loadFaceDb() {
        List<FaceIndex> list = faceIndexMapper.selectAll();
        list.forEach(face -> {
            try {
                register(face.getFaceKey(), face);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 将历史注册过的所有人脸重新加载到数据库
     *
     * @param key  人脸照片唯一标识
     * @param face 人脸照片
     * @return
     * @throws IOException
     */
    private void register(String key, FaceIndex face) {
        SeetaImageData imageData = new SeetaImageData(face.getWidth(), face.getHeight(), face.getChannel());
        imageData.data = face.getImageData();
        Integer index = SeetafaceBuilder.seeta.register(imageData);
        if (index < 0) {
            return;
        }
        face.setFaceKey(key);
        face.setFaceIndex(index);
        faceIndexMapper.updateByPrimaryKey(face);
    }

    /**
     * 建立人脸库索引
     */
    @Override
    public void buildIndex() {
        synchronized (SeetafaceBuilder.class) {
            new Thread(() -> {
                SeetafaceBuilder.seeta.clear();
                loadFaceDb();
            }).start();
        }
    }

    /**
     * 注册人脸(会对人脸进行裁剪)
     *
     * @param key 人脸照片唯一标识
     * @param img 人脸照片
     * @return
     * @throws IOException
     */
    @Override
    public boolean faceRegister(String key, byte[] img, String patientSourceCode) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new ByteArrayInputStream(img));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //先对人脸进行裁剪
        SeetaImageData imageData = new SeetaImageData(image.getWidth(), image.getHeight(), 3);
        imageData.data = ImageUtils.getMatrixBGR(image);
        byte[] bytes = seeta.crop(imageData);

        if (bytes == null || bytes.length != CROP_SIZE) {
            return false;
        }
        imageData = new SeetaImageData(256, 256, 3);
        imageData.data = bytes;
        int index = seeta.register(imageData);
        if (index < 0) {
            return false;
        }
        FaceIndex face = new FaceIndex();
        face.setFaceKey(key);
        face.setImageData(imageData.data);
        face.setWidth(imageData.width);
        face.setHeight(imageData.height);
        face.setChannel(imageData.channels);
        face.setFaceIndex(index);
        face.setPatientSourceCode(patientSourceCode);
        faceIndexMapper.insertSelective(face);
        // 重新加载人脸库
        buildIndex();
        return true;
    }

    /**
     * 注册人脸(不裁剪图片)
     *
     * @param key   人脸照片唯一标识
     * @param image 人脸照片
     * @return
     * @throws IOException
     */
    @Override
    public boolean faceRegister(String key, BufferedImage image) {
        SeetaImageData imageData = new SeetaImageData(image.getWidth(), image.getHeight(), 3);
        imageData.data = ImageUtils.getMatrixBGR(image);
        int index = seeta.register(imageData);
        if (index < 0) {
            return false;
        }
        FaceIndex face = new FaceIndex();
        face.setFaceKey(key);
        face.setImageData(imageData.data);
        face.setWidth(imageData.width);
        face.setHeight(imageData.height);
        face.setChannel(imageData.channels);
        face.setFaceIndex(index);
        faceIndexMapper.insertSelective(face);
        return true;
    }

    @Override
    public Result search(byte[] img) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(img));
        if (image == null) {
            return null;
        }
        SeetaImageData imageData = new SeetaImageData(image.getWidth(), image.getHeight(), 3);
        imageData.data = ImageUtils.getMatrixBGR(image);
        RecognizeResult rr = seeta.recognize(imageData);

        if (rr == null || rr.index == -1) {
            return null;
        }
        Result result = new Result(rr);
        FaceIndex faceIndex = faceIndexMapper.selectOneByExample(new Example.Builder(FaceIndex.class)
                .where(WeekendSqls.<FaceIndex>custom().andEqualTo(FaceIndex::getFaceIndex, rr.index)).build());
        if (faceIndex != null) {
            result.setKey(faceIndex.getFaceKey());
            result.setPatientSourceCode(faceIndex.getPatientSourceCode());
        }
        return result;
    }

    @Override
    public void removeRegister(String... keys) {
        List<String> list = Arrays.asList(keys);
        faceIndexMapper.deleteByExample(new Example.Builder(FaceIndex.class)
                .where(WeekendSqls.<FaceIndex>custom().andIn(FaceIndex::getFaceKey, list)));//删除数据库的人脸
        buildIndex();//重新建立索引
    }

    @Override
    public void clear() {
        faceIndexMapper.delAll();
        seeta.clear();
    }

    @Override
    public boolean exist(String patientCode) {
        FaceIndex faceIndex = faceIndexMapper.selectOneByExample(new Example.Builder(FaceIndex.class)
                .where(WeekendSqls.<FaceIndex>custom().andLike(FaceIndex::getPatientSourceCode, patientCode)).build());
        return !(faceIndex == null);
    }

    @Override
    public List<PatientInfoVo> getPatientsByCode(String patientCode, String ip) {
        Screen exitScreen = screenMapper.selectOneByExample(new Example.Builder(Screen.class)
                .where(WeekendSqls.<Screen>custom().andEqualTo(Screen::getScreenIp, ip)).build());
        Assert.notNull(exitScreen, "未查询到屏幕信息！");
        Triage triage = triageMapper.selectByPrimaryKey(exitScreen.getTcsTriageId());
        Assert.notNull(triage, "签到机绑定的分诊台不存在！");
        List<PatientInfoVo> patientList = patientMapper.getPatientByCode(patientCode, triage.getTriageIp());
        return patientList;
    }
}
