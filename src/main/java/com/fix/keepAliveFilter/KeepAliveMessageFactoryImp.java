package com.fix.keepAliveFilter;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fix.message.MessageModel;
import com.fix.server.FixServerHandler;

/**
 * 
* @ClassName: KeepAliveMessageFactoryImp 
* @Description: 处理心跳类
* @author zhouyoukuan
* @date 2015年10月28日 下午3:17:24 
*
 */
public class KeepAliveMessageFactoryImp implements KeepAliveMessageFactory
{
	private Logger logger = LoggerFactory.getLogger(KeepAliveMessageFactoryImp.class);
	
	@Override
	public Object getRequest(IoSession session) {
		logger.info("发送心跳包");
		long sequence = (Long)session.getAttribute(FixServerHandler.HEART_BEAT_COUNT);
		session.setAttribute(FixServerHandler.HEART_BEAT_COUNT, sequence+1);
		MessageModel HEART_BEAT = new MessageModel((short)0x0C,sequence,(short)0x01,(short)0x65,"");
		return HEART_BEAT;
		
	}

	@Override
	public Object getResponse(IoSession session, Object request) 
	{
		
//		MessageModel msg = (MessageModel)request;
//		if(msg.getMsgType() == 0x65 && msg.getMsgSystem() == 0x01)
//		{
//			logger.info("心跳反馈包");
//			ACK_SUCCESS = new MessageModel((short)0x10, msg.getMsgSequence(), (short)0x01, (short)0x66,EnumResult.SUCCESS);
//			return ACK_SUCCESS;
//		}
	
		return null;
		
	}

	@Override
	public boolean isRequest(IoSession session, Object request) {
		
		
		MessageModel msg = (MessageModel)request;
		if(msg.getMsgType() == 0x65 && msg.getMsgSystem() == 0x01 && msg.getMsgCRC() == 0x73)
		{
			return true;
		}
		return false;
		
		
	}

	@Override
	public boolean isResponse(IoSession session, Object request) {
		return false;
	}

}
