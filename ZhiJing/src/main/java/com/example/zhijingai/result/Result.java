package com.example.zhijingai.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    @ApiModelProperty("返回编码 1成功 0失败")
    private Integer code; //编码：1成功，0和其它数字为失败
    @ApiModelProperty("返回错误信息")
    private String msg; //错误信息
    @ApiModelProperty("返回数据")
    private T data; //数据


    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 1;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 1;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 0;
        return result;
    }
}
