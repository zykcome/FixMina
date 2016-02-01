package com.fix.worker;

import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fix.message.MessageModel;
import com.fix.message.MessageQueue;
import com.fix.messageType.EnumResult;
import com.fix.server.FixServer;

/**
 * 
* @ClassName: ServerSendMsgThread 
* @Description: 系统发送的火灾报警和故障状态信息的线程类
* @author zhouyoukuan
* @date 2015年11月12日 下午3:51:25 
*
 */
public class ServerSendMsgThread extends Thread
{
	private final Logger logger = LoggerFactory.getLogger(ServerSendMsgThread.class);
	private IoSession session = null;  
    private MessageQueue queue = null;  
      
	public ServerSendMsgThread(IoSession session){}
    public ServerSendMsgThread(IoSession session, MessageQueue queue)
    {  
        this.queue = queue;  
        this.session = session;
    }  
	
	
	@Override
	public void run()
	{	
		sendMessage();
	}

	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}
	
	public MessageQueue getQueue() {
		return queue;
	}
	public void setQueue(MessageQueue queue) {
		this.queue = queue;
	}

	
	private void sendMessage()
	{
		while(true)
		{
		    try
		    {	
		    	if(session != null && FixServer.getAcceptor().getManagedSessionCount() != 0)
		    	{	
		    		MessageModel message = queue.take();
		    		if(queue.offer(message))
		    			logger.info("重新将消息放回队列");
		    		session.write(message);
		    		logger.info("消防系统发送火灾报警或者故障状态信息..........");
		    		ReadFuture read = session.read();
		    		read.awaitUninterruptibly(8000);
		    		if(read.isRead())
		    		{
		    			logger.info("接收客户端的确认报文............");
					
		    			MessageModel msg =  (MessageModel) read.getMessage();
					
		    			if(!(msg.getMsgInfo() instanceof EnumResult))
		    			{
		    				continue;
		    			}
		    			EnumResult result = (EnumResult)msg.getMsgInfo();
		    			System.out.println(msg.getMsgLength() + " : " + msg.getMsgType() + " : " + result.getResult());
		    			if(msg.getMsgLength() == 0x10 && msg.getMsgType() == 0x66 && result.getResult() == 0 )
		    			{
		    				 queue.take();
		    				 logger.info("重队列中移除已经发送完成的信息.............");
		    			}	
		    			else
		    			{
		    				logger.info("收到失败的报文,重新发送报文...............");
		    				continue;
		    			}
		    		}
		    		else
		    		{
		    			if(FixServer.getAcceptor().getManagedSessionCount() == 0)
		    			{
		    				read.setClosed();
		    				continue;
		    			}
		    			logger.info("接收不到确认报文...重新发送" );
		    		}
		    	}
					
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
	}
}
