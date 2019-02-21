package com.pkpm;

import com.pkpm.util.FlumeRpcClientUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PkpmFlumeSdkApplicationTests {

	@Test
	public void contextLoads() {
	}


	/**
	 *1、调用工具类发送日志
	 */
	@Test
	public void testSend(){
		// 初始化
		FlumeRpcClientUtils.init();

		for(int i = 0; i < 10; i++){
			String data = "发送第" + i + "条数据";
			FlumeRpcClientUtils.append(data);
		}
	}
}
