package com.pkpm.util;

import com.google.common.base.Preconditions;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * 页面返回对象
 * auth zhangshuai
 * since 09/03/18
 */
@ApiModel
public class ResultObject implements Serializable {

    @ApiModelProperty(example = "200")
    private Integer code;
    @ApiModelProperty(example = "成功")
    private String message;
    @ApiModelProperty(example = "返回数据")
    private Object data;

    private ResultObject() {
    }


    public static ResultObject success() {
        ResultObject result = new ResultObject();
        result.code = HttpStatus.OK.value();
        return result;
    }

    public static ResultObject success(Object data) {
        ResultObject result = new ResultObject();
        result.code = HttpStatus.OK.value();
        result.data = data;
        return result;
    }

    public static ResultObject success(Object data, String message) {
        ResultObject result = new ResultObject();
        result.code = HttpStatus.OK.value();
        result.message = message;
        result.data = data;
        return result;
    }

    public static ResultObject failure(String message) {
        ResultObject result = new ResultObject();
        //  result.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        result.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        result.message = message;
        return result;
    }

    public static ResultObject failure(Integer code, String message) {
        ResultObject result = new ResultObject();
        result.code = code;
        result.message = message;
        return result;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }




}
