package com.face.utils;

import com.face.SeetafaceBuilder;
import com.face.service.IFaceIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author: pengbenlei
 * @Date: 2020/9/24 14:56
 * @Description:
 */
@Component
public class FaceInitUtils {

    @Autowired
    IFaceIndexService faceIndexService;

    @PostConstruct
    public void init() {
        SeetafaceBuilder.build();//系统启动时先调用初始化方法
        // 初始化人脸库
        faceIndexService.loadFaceDb();
    }
}
