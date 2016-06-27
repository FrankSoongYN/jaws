package com.frank.soong.jaws.qunar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.frank.soong.jaws.qunar.service.ICrawlService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/spring-context.xml","classpath:/spring-web.xml"})
public class DealTest {
	@Autowired
	private ICrawlService crawlService;
	
	@Test
	public void dealTest(){
	  int a=	crawlService.dealListInfo();
	  System.out.println("插入数值："+a);
	}
	
	@Test
	public void getInfoTest(){
		crawlService.qunarCrawlJob();
	}
}
