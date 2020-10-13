package com.face.dto;

import com.seetaface2.model.RecognizeResult;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author Sugar
 * @Version 2019/4/22 17:50
 */
@Getter
@Setter
public class Result implements Serializable {
    private String key;
    private float similar;

    private String patientSourceCode;

    public Result() {

    }

    public Result(RecognizeResult result) {
        this.similar = result.similar;
    }


    @Override
    public String toString() {
        return "{" +
                "" + key +
                ": " + similar +
                ": " + patientSourceCode +
                '}';
    }
}
