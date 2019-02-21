package com.pkpm.controller;

import com.pkpm.util.FlumeRpcClientUtils;
import com.pkpm.util.ResultObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by Kbp on 2019/2/21.
 */
@RestController
@RequestMapping("/flume")
@Api(description = "flume接口RESTFUL接口调用")
public class FlumeController {

    @ApiOperation(value = "发送字符串")
    @PostMapping("/append")
    public ResultObject append(String data){

        boolean flag = FlumeRpcClientUtils.append(data);
        return ResultObject.success(flag);
    }

    @ApiOperation(value = "发送字符串和请求头")
    @PostMapping("/append")
    public ResultObject append(String data, Map<String, String> headers){

        boolean flag = FlumeRpcClientUtils.append(data,headers);
        return ResultObject.success(flag);

    }


    @ApiOperation(value = "使用批量方式-发送单条字符串")
    @PostMapping("/appendBatch")
    public ResultObject appendBatch(String data){

        boolean flag = FlumeRpcClientUtils.appendBatch(data);
        return ResultObject.success(flag);
    }

    @ApiOperation(value = "使用批量方式-发送单条字符串和请求头")
    @PostMapping("/appendBatch")
    public ResultObject appendBatch(String data, Map<String, String> headers){

        boolean flag = FlumeRpcClientUtils.appendBatch(data,headers);
        return ResultObject.success(flag);

    }


    @ApiOperation(value = "批量发送字符串")
    @PostMapping("/appendBatchList")
    public ResultObject appendBatchList(List<String> items){

        boolean flag = FlumeRpcClientUtils.appendBatch(items);
        return ResultObject.success(flag);
    }

    @ApiOperation(value = "批量发送字符串和请求头")
    @PostMapping("/appendBatchList")
    public ResultObject appendBatchList(List<String> items, Map<String, String> headers){

        boolean flag = FlumeRpcClientUtils.appendBatch(items,headers);
        return ResultObject.success(flag);

    }


}
