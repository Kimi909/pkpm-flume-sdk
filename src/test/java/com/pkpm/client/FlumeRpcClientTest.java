package com.pkpm.client;

import com.pkpm.PkpmFlumeSdkApplicationTests;
import com.pkpm.util.FlumeUtil;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/6.
 */
public class FlumeRpcClientTest extends PkpmFlumeSdkApplicationTests{

      private   FlumeRpcClient client = new FlumeRpcClient();

      @Test
      public void uploadByteTest() throws IOException {
          FileInputStream input = new FileInputStream("C:\\Users\\Administrator\\Desktop\\flume集群.txt");
          byte[] bytes = FlumeUtil.toByteArray(input);

          boolean flag = client.uploadByte(bytes);
          System.out.println(flag);

      }


    @Test
    public void uploadByteTest2() throws IOException {
        FileInputStream input = new FileInputStream("C:\\Users\\Administrator\\Desktop\\flume集群.txt");
        byte[] bytes = FlumeUtil.toByteArray(input);

        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Security-Policy","script-src chrome://resources 'self';object-src 'none';child-src 'none'");
        headers.put("X-Frame-Options","DENY");

        boolean flag = client.uploadByte(bytes, headers);
        System.out.println(flag);

    }
}
