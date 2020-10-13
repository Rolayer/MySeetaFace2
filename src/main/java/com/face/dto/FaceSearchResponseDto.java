package com.face.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Author: pengbenlei
 * @Date: 2020/9/24 11:42
 * @Description:
 */
@Getter
@Setter
public class FaceSearchResponseDto {

    /**
     * 患者姓名
     */
    private String patientName;

    /**
     * 患者id
     */
    private Integer id;

    /**
     * 队列名称
     */
    private String queueTypeName;

    /**
     * 预约时间
     */
    private Date reserveTime;
}
