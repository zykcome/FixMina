package com.fix.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.fix.codec.FixCodecFactory;
import com.fix.keepAliveFilter.KeepAliveMessageFactoryImp;
import com.fix.keepAliveFilter.KeepAliveRequestTimeoutHandlerImp;
import com.fix.util.PropertiesEditer;

/**
 * 
* @ClassName: XfXtServerImp 
* @Description:  消防系统与FASTView系统通信的服务端的启动类
* @author zhouyoukuan
* @date 2015年10月28日 下午2:47:55 
*
 */
public class FixServer{

	private static IoAcceptor acceptor = null;
	private static final int HEARTBEATRATE = 10;
	
	private FixServer(){};
	
	public static IoAcceptor getAcceptor()
	{
		if(null == acceptor )
			acceptor = new NioSocketAcceptor();
		return acceptor;
	}
	
	
	public static boolean serverStart() {
		
		DefaultIoFilterChainBuilder filterChain = getAcceptor().getFilterChain();

	//	设置服务端的编码和解码器
		filterChain.addLast("codec", new ProtocolCodecFilter(new FixCodecFactory()));
		//filterChain.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
		
		LoggingFilter loggingFilter = new LoggingFilter();
		loggingFilter.setMessageSentLogLevel(LogLevel.INFO);
		loggingFilter.setMessageReceivedLogLevel(LogLevel.INFO);
		filterChain.addLast("logger",loggingFilter);
		//filterChain.addLast("exceutor", new ExecutorFilter()); 

		getAcceptor().setHandler(new FixServerHandler());
		getAcceptor().getSessionConfig().setReadBufferSize(40960);
	//	getAcceptor().getSessionConfig().setUseReadOperation(true);
		
		//getAcceptor().getSessionConfig().setBothIdleTime(HEARTBEATRATE);
	
		//心跳机制的处理
		KeepAliveMessageFactoryImp keepAliveMessageFactoryImp = new KeepAliveMessageFactoryImp();
		//设置为聋子型，不需要应答心跳
		KeepAliveFilter keepAliveFilter = new KeepAliveFilter(keepAliveMessageFactoryImp,IdleStatus.WRITER_IDLE,KeepAliveRequestTimeoutHandlerImp.DEAF_SPEAKER) ;
		
		//设置心跳包发送的时间
		keepAliveFilter.setRequestInterval(HEARTBEATRATE);
		//设置心跳超时时间
		//keepAliveFilter.setRequestTimeout(10);
		//设置发送心跳包的时候也会触发IOHandler的sessionIdle方法
		//keepAliveFilter.setForwardEvent(true);
		//添加心跳过滤器
		filterChain.addLast("heartbeat", keepAliveFilter);
		
		try
		{
			String port = PropertiesEditer.getValue("server.port");
			getAcceptor().bind(new InetSocketAddress(Integer.parseInt("9091")));
			return true;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	public static void main(String[] args) {
		
		if(serverStart())
		{
			System.out.println("服务器启动了....................");
		}
	}

	
	
	
	
	
	
	
	
	
}
