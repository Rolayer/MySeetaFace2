package com.face.controller;

import com.face.dto.ResponseResult;
import com.face.dto.Result;
import com.face.service.IFaceIndexService;
import com.face.utils.MultipartFileToFile;
import com.leenleda.common.dto.vo.PatientInfoVo;
import com.leenleda.common.utils.ShardKit;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @Author: pengbenlei
 * @Date: 2020/9/24 9:28
 * @Description:
 */
@RestController
@RequestMapping("face")
public class FaceIndexController {


    @Autowired
    IFaceIndexService faceIndexService;


    @Autowired
    ShardKit shardKit;

    /**
     * 人脸注册
     */
    @PostMapping("/register")
    public ResponseResult registerFace(@RequestPart(value = "file") MultipartFile[] files, @RequestParam String patientCode) throws IOException {
        ResponseResult result = new ResponseResult();
        if (files.length > 1 || files.length == 0) {
            result.setMsg("请上传一张图片作为识别凭证！");
            result.setCode(200);
            result.setStatus(false);
        } else if (faceIndexService.exist(patientCode)) {
            // 验证该患者卡号是否已经注册过
            result.setMsg("卡号已被注册！");
            result.setCode(200);
            result.setStatus(false);
        } else {
            for (MultipartFile file : files) {
                faceIndexService.faceRegister(file.getOriginalFilename(), file.getBytes(), patientCode);
            }
            result.setMsg("ok");
            result.setCode(200);
            result.setStatus(true);
        }

        return result;
    }

    /**
     * 根据照片匹配人脸数据
     */
    @PostMapping("/search")
    public ResponseResult faceSearch(@RequestPart(value = "file") MultipartFile files, HttpServletRequest request) throws Exception {
        ResponseResult result = new ResponseResult();
        File file = MultipartFileToFile.multipartFileToFile(files);
        Result searchResult = faceIndexService.search(FileUtils.readFileToByteArray(file));
        MultipartFileToFile.delteTempFile(file);
        if (searchResult == null) {
            // 没有搜索到结果
            result.setCode(200);
            result.setMsg("找不到匹配人脸！");
            result.setStatus(false);
            return result;
        } else if (StringUtils.isEmpty(searchResult.getPatientSourceCode()) || searchResult.getSimilar() < 0.6) {
            result.setCode(200);
            result.setMsg("找不到匹配人脸！");
            result.setStatus(false);
            return result;
        }
        // 根据匹配的卡号找到对应的患者
        String ip = shardKit.getIpAddr(request);
        List<PatientInfoVo> vos = faceIndexService.getPatientsByCode(searchResult.getPatientSourceCode(), ip);
        result.setCode(200);
        result.setMsg("ok");
        result.setStatus(true);
        result.setData(vos);
        return result;
    }

}
