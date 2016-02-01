package com.fix.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Test;

/**
* @ClassName: PropertiesEditer 
* @Description: 读取配置文件
* @author zhouyoukuan
* @date 2015年10月27日 上午11:41:34 
*
 */
public class PropertiesEditer {

	private static Properties prop = null;
	
	static
	{
		prop = PropertiesEditer.getProperties("/config.properties");
	}
	
	public static Properties getProperties(String fileName)
	{
		
		Properties prop = new Properties();
	
		InputStream in  = null;
		
		try
		{
			in = PropertiesEditer.class.getResourceAsStream(fileName);
		}
		catch(Exception e)
		{
			System.out.println("配置文件找不到");
			e.printStackTrace();
		}
		
		try
		{
			prop.load(in);
			in.close();
		}
		catch(IOException e)
		{
			System.out.println("读取配置文件失败了");
			e.printStackTrace();
		}
		
		return prop;
	}
	
	public static String getValue(String name)
	{
		String value = prop.getProperty(name);
		return value;
	}
	
	@Test
	public void test()
	{
		System.out.println(PropertiesEditer.getValue("server.port"));
	}
	
	
}
