package com.pkpm.controller;

import com.pkpm.annotation.RequestJson;
import com.pkpm.util.FlumeRpcClientUtils;
import com.pkpm.util.ResultObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    @PostMapping("/data")
    public ResultObject append(@RequestJson(value = "data") String data){

        boolean flag = FlumeRpcClientUtils.append(data);
        return ResultObject.success(flag);
    }


    @ApiOperation(value = "使用批量方式-发送单条字符串")
    @PostMapping("/batchData")
    public ResultObject appendBatch(@RequestJson(value = "data") String data){

        boolean flag = FlumeRpcClientUtils.appendBatch(data);
        return ResultObject.success(flag);
    }


    @ApiOperation(value = "批量发送字符串")
    @PostMapping("/batchListItem")
    public ResultObject appendBatchList(@RequestBody List<String> items){

        boolean flag = FlumeRpcClientUtils.appendBatch(items);
        return ResultObject.success(flag);
    }


}
