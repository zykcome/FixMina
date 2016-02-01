package com.fix.server;

import org.apache.commons.lang3.RandomUtils;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fix.message.MessageModel;
import com.fix.message.MessageQueue;
import com.fix.messageType.EnumAlarmStatus;
import com.fix.messageType.EnumDeviceStatus;
import com.fix.messageType.EnumResult;
import com.fix.messageType.PK_Type;
import com.fix.messageType.TDevID;
import com.fix.messageType.TDeviceEvent;
import com.fix.messageType.TDeviceState;
import com.fix.util.DataTypeUtil;
import com.fix.worker.ServerSendMsgThread;


public class FixServerHandler extends IoHandlerAdapter
{
	
	public static Logger logger =LoggerFactory.getLogger(FixServerHandler.class);
	public DataTypeUtil dataTypeUtil;
	
	public static final AttributeKey HEART_BEAT_COUNT = new AttributeKey(
	        FixServerHandler.class, "heart_beat_count");
	
	public FixServerHandler()
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
		
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception
	{	
		logger.info("服务器发送了报文....................");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.info("关闭"+session+"连接, 剩余的连接个数 : " + FixServer.getAcceptor().getManagedSessionCount());
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		logger.info("服务器和客户端正在创建连接........");
		session.setAttribute(HEART_BEAT_COUNT,1L);
		
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception
	{
		
		if(status  == IdleStatus.WRITER_IDLE)
		{
			long old = session.getLastReadTime();
			if((System.currentTimeMillis() - old) > 60 * 1000)
			{
				logger.info("心跳发送60s后没有得到任何报文反馈,正在关闭连接.....");
				CloseFuture closeFuture = session.close(true);
				closeFuture.addListener(new IoFutureListener<IoFuture>() {

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
		if(session.isConnected())
		{
			Long sequence = (long)session.getAttribute(HEART_BEAT_COUNT);
			session.setAttribute(HEART_BEAT_COUNT, sequence+1);
			MessageModel HEART_BEAT = new MessageModel((short)0x0C,sequence,(short)0x01,(short)0x65,"");
			session.write(HEART_BEAT);
		}
		
	}
	
	
	
	
	
	@Override
	public void sessionOpened(IoSession session) throws Exception
	{
	
		MessageQueue queue = new MessageQueue();
		ServerSendMsgThread sThread = new ServerSendMsgThread(session, queue);
	//	sThread.start();
		sThread.setName("向客户端发送消息的线程");
			
	}
	
	/**
	 * 
	* @Title: QueryStatus 
	* @Description:  FASTView系统查询或者设置第三方消防系统中的设备状态信息
	* @param     
	* @return void   
	* @throws
	 */
	public void FASTViewQueryOrSetStatus(IoSession session, MessageModel message)
	{
		if(message.getMsgLength() == 0x8C && message.getMsgSystem() == 0x01)
		{
			TDevID devId  = (TDevID) message.getMsgInfo();
			char[] addr = devId.getAddr();
			String addrString = dataTypeUtil.charsToString(addr);
			
			switch(message.getMsgType())
			{
				case PK_Type.GET_DEVICE_STATE:
				{
					logger.info("FASTView系统,请求消防设备当前状态.....");
					EnumDeviceStatus status = xfxtQueryStatus(addrString);
					TDeviceState deviceState = new TDeviceState(devId,status);
					
					MessageModel ackMsg = new MessageModel();
					ackMsg.setMsgLength((short)0x90);
					ackMsg.setMsgSequence(RandomUtils.nextInt(0, 1000));
					ackMsg.setMsgSystem((short)0x01);
					ackMsg.setMsgType(PK_Type.SET_DEVICE_STATE);
					ackMsg.setMsgInfo(deviceState);
					session.write(ackMsg);
					logger.info("应答消防设备当前状态给FASTView系统...");
				}
				break;
				case PK_Type.REQ_ALARM_OFF :
				{
					logger.info("FASTView系统,请求消防设备报警恢复.....");
					MessageModel ackMsg = new MessageModel();
					ackMsg.setMsgLength((short)0x10);
					ackMsg.setMsgSequence(message.getMsgSequence());
					ackMsg.setMsgSystem((short)0x01);
					ackMsg.setMsgType(PK_Type.REQ_ALARM_OFF_ACK);
					
					if(xfxtSetStatus(addrString))
					{	
						ackMsg.setMsgInfo(EnumResult.SUCCESS);
						logger.info("反馈消防设备报警恢复成功给FASTView系统....");
					}
					else
					{
						ackMsg.setMsgInfo(EnumResult.FAILURE);
						logger.info("反馈消防设备报警恢复失败给FASTView系统....");
					}
					
				}
				break;
				case PK_Type.REQ_DEVICE_RESET :
				{
					logger.info("FASTView系统,请求复位消防设备.....");
					MessageModel ackMsg = new MessageModel();
					ackMsg.setMsgLength((short)0x10);
					ackMsg.setMsgSequence(message.getMsgSequence());
					ackMsg.setMsgSystem((short)0x01);
					ackMsg.setMsgType(PK_Type.REQ_DEVICE_RESET_ACK);
					
					if(xfxtSetStatus(addrString))
					{	
						ackMsg.setMsgInfo(EnumResult.SUCCESS);
					}
					else
					{
						ackMsg.setMsgInfo(EnumResult.FAILURE);
					}
					session.write(ackMsg);
				}
				break;
				case PK_Type.REQ_DEVICE_SILENCE :
				{
					logger.info("FASTView系统,请求消防设备消音.....");
					MessageModel ackMsg = new MessageModel();
					ackMsg.setMsgLength((short)0x10);
					ackMsg.setMsgSequence(message.getMsgSequence());
					ackMsg.setMsgSystem((short)0x01);
					ackMsg.setMsgType(PK_Type.REQ_DEVICE_SILENCE_ACK);
					
					if(xfxtSetStatus(addrString))
					{	
						ackMsg.setMsgInfo(EnumResult.SUCCESS);
						logger.info("反馈消防设备消音成功的信息给FASTView系统");
					}
					else
					{
						ackMsg.setMsgInfo(EnumResult.FAILURE);
						logger.info("反馈消防设备消音失败的信息给FASTView系统");
					}
					session.write(ackMsg);
				
				}
				break;
			}
		}
	}
	
	/**
	 * 
	* @Title: xfxtQueryStatus 
	* @Description: 请求查询消防系统的状态
	* @param id
	* @return EnumDeviceStatus   
	* @throws
	 */
	public EnumDeviceStatus xfxtQueryStatus(String id)
	{	
		//查询后返回，暂时写死
		return EnumDeviceStatus.TROUBLE;
	}
	
	/**
	 * 
	* @Title: xfxtSetStatus 
	* @Description: 设置消防系统的状态,如果成功则返回true
	* @param   id
	* @return boolean   
	* @throws
	 */
	public boolean xfxtSetStatus(String id)
	{
		//设置状态后返回，暂时写死
		return true;
	}
	
}






