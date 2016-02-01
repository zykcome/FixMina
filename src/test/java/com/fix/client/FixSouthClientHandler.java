package com.fix.client;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.fix.message.MessageModel;
import com.fix.messageType.EnumResult;
import com.fix.messageType.TDeviceEvent;
import com.fix.util.DataTypeUtil;

public class FixSouthClientHandler extends IoHandlerAdapter
{

	public static Logger logger = Logger.getLogger(FixSouthClientHandler.class);
	public DataTypeUtil dataTypeUtil;
	
	public FixSouthClientHandler()
	{
		this.dataTypeUtil = new DataTypeUtil();
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception
	{
		cause.printStackTrace();
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception
	{
		logger.info("接收报文:" + message.toString());
		MessageModel msg = (MessageModel)message;
		if(msg.getMsgLength() == 0x8C)
		{
			
		}
		if(msg.getMsgLength() == 0x120)
		{
			TDeviceEvent event = (TDeviceEvent)msg.getMsgInfo();
			System.out.println("收到的火灾报文 :" + String.valueOf(event.getDescription()));
			session.write(new MessageModel((short)0x10, msg.getMsgSequence(), (short)0x01, (short)0x66,EnumResult.SUCCESS));
		
		}
		
			
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception
	{
		
		logger.info("客户端要发出的信息是 : " + message.toString());
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception
	{
		// TODO Auto-generated method stub
		super.sessionClosed(session);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception
	{
		// TODO Auto-generated method stub
		super.sessionCreated(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception
	{
		// TODO Auto-generated method stub
		session.write(new MessageModel((short)0x0C,12L,(short)0x01,(short)0x65,""));
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception
	{
		logger.info("已经和服务器建立连接.................");
		
	}
	
	public void queryXfxt(IoSession session)
	{
			
	}
	
	

}
