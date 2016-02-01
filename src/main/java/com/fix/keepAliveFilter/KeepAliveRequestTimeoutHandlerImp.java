package com.fix.keepAliveFilter;

import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeepAliveRequestTimeoutHandlerImp implements KeepAliveRequestTimeoutHandler 
{
	
	private Logger logger = LoggerFactory.getLogger(KeepAliveRequestTimeoutHandlerImp.class);

	
	public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session)
			throws Exception {
		
		long old = session.getLastReadTime();
		if((System.currentTimeMillis() - old) >= 60 * 1000)
		{
			logger.info("心跳发送60s后没有得到任何报文反馈,正在关闭连接.....");
			CloseFuture closeFuture = session.close(true);
			closeFuture.addListener(new IoFutureListener<IoFuture>() {
				@Override
				public void operationComplete(IoFuture ioFuture) {
					if(ioFuture instanceof CloseFuture)
					{
						((CloseFuture) ioFuture).setClosed();
						logger.info("session连接关闭完成...............");
					}
				}
				
			});
		}
		
	}

}
