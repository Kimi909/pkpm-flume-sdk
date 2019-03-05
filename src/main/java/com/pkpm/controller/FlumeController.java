package com.pkpm.controller;

import com.google.common.base.Preconditions;
import com.pkpm.annotation.RequestJson;
import com.pkpm.util.FlumeRpcClientUtils;
import com.pkpm.util.ResultObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @ApiOperation(value = "文件流转换成字符串")
    @PostMapping("/upload")
    public ResultObject upload(@RequestParam("file")  @ApiParam(value = "文件流") MultipartFile multipartFile ) throws IOException {

        Preconditions.checkArgument(!multipartFile.isEmpty(), "请上传文件!");
        boolean flag = false;

        if(!multipartFile.isEmpty()){
            InputStream inputStream = multipartFile.getInputStream();
            //把流文件解析成字符串
            String result = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().parallel().collect(Collectors.joining(System.lineSeparator()));

            flag = FlumeRpcClientUtils.appendBatch(result);
            return ResultObject.success(flag);
        }


        return ResultObject.success(flag);
    }


    @ApiOperation(value = "文件流转换成字节流传输")
    @PostMapping("/byte")
    public ResultObject uploadByte(@RequestParam("file")  @ApiParam(value = "文件流") MultipartFile multipartFile ) throws IOException {

        Preconditions.checkArgument(!multipartFile.isEmpty(), "请上传文件!");
        boolean flag = false;

        if(!multipartFile.isEmpty()){
            InputStream inputStream = multipartFile.getInputStream();
            //把流文件解析成字节数组
            byte[] bytes = toByteArray(inputStream);
            Event event =EventBuilder.withBody(bytes);
            flag = FlumeRpcClientUtils.append(event);
            return ResultObject.success(flag);
        }
        return ResultObject.success(flag);
    }


    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        byte[] buffer=new byte[1024*4];
        int n=0;
        while ( (n=in.read(buffer)) !=-1) {
            out.write(buffer,0,n);
        }
        return out.toByteArray();
    }

}
