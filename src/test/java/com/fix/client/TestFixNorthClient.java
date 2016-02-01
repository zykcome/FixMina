package com.fix.client;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.fix.codec.FixCodecFactory;
import com.fix.util.PropertiesEditer;

/**
 *
* @ClassName: XfXtClient 
* @Description: 测试客户端
* @author zhouyoukuan
* @date 2015年10月27日 下午3:46:51 
*
 */
public class TestFixNorthClient 
{
	public static Logger logger = Logger.getLogger(TestFixNorthClient.class);
	
	public static void main(String[] args)
	{
		
		String host = PropertiesEditer.getValue("server.host");
//		int port = Integer.parseInt(PropertiesEditer.getValue("server.port"));
//		String host = "134.201.124.163";
		int port = 9091;
		
		IoConnector connector = new NioSocketConnector();
		//设置连接的超时时间
		connector.setConnectTimeout(3000);
		//添加过滤器
		//connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF8"))));
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new FixCodecFactory()));
		//设置处理器
		connector.setHandler(new FixNorthClientHandler());
		connector.getSessionConfig().setReadBufferSize(1024);
		connector.getSessionConfig().setWriterIdleTime(10);
		IoSession session = null;
		try
		{
			
			ConnectFuture future = connector.connect(new InetSocketAddress(host, port));
			logger.info("等待连接的创建完成....");
			future.awaitUninterruptibly();
			session = future.getSession();
			
			
		}catch(Exception e)
		{
			logger.error("客服端出现错误!!");
			e.printStackTrace();
		}
		
		logger.info("等待连接的关闭..");
		session.getCloseFuture().awaitUninterruptibly();
		connector.dispose();
		logger.info("连接关闭了...");
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
