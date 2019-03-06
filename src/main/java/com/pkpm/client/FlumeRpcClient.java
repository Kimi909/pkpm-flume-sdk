package com.pkpm.client;

import com.google.common.base.Preconditions;
import com.pkpm.util.FlumeRpcClientUtils;
import com.pkpm.util.FlumeUtil;
import com.pkpm.util.ResultObject;
import io.swagger.annotations.ApiParam;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/6.
 */
public class FlumeRpcClient {

    public boolean uploadByte( byte[] bytes ) throws IOException {

        boolean flag = false;

        if( bytes.length !=  0){

            Event event = EventBuilder.withBody(bytes);
            flag = FlumeRpcClientUtils.append(event);
            return flag;
        }
        return flag;
    }

    /**
     * header的话，就是在封装Event对象的时候，我们可以自定义的设置一些key-value对，这样做的目的，是为了后续的通道多路复用做准备的
     * headers不会sink进去
      * @param bytes
     * @param headers
     * @return
     */
    public boolean uploadByte( byte[] bytes , Map<String,String> headers){
        boolean flag = false;

        if( bytes.length !=  0){

            Event event = EventBuilder.withBody(bytes,headers);
            flag = FlumeRpcClientUtils.append(event);
            return flag;
        }
        return flag;
    }
}
