package com.fix.codec;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fix.message.MessageModel;
import com.fix.messageType.EnumAlarmStatus;
import com.fix.messageType.EnumDeviceStatus;
import com.fix.messageType.EnumResult;
import com.fix.messageType.TDevID;
import com.fix.messageType.TDeviceEvent;
import com.fix.messageType.TDeviceState;
import com.fix.util.DataTypeUtil;
import com.fix.util.PropertiesEditer;


/**
 * 
* @ClassName: MessageDecoder 
* @Description: 接受到报文的解码
* @author zhouyoukuan
* @date 2015年10月28日 下午3:08:59 
*
 */

public class MessageDecoder extends CumulativeProtocolDecoder
{
	private Logger logger = LoggerFactory.getLogger(MessageDecoder.class);
	private final CharsetDecoder charsetDecoder = Charset.forName(PropertiesEditer.getValue("encoding")).newDecoder();  ;
	private DataTypeUtil dataTypeUtil = new DataTypeUtil();
	
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception 
	{
		in.order(ByteOrder.BIG_ENDIAN);
		MessageModel message = new MessageModel();
		if(in.remaining() >= 12) //buffer的长度至少要大于或者等于心跳包才解析
		{
			String buffer = in.getHexDump();
			logger.info("接收报文 : " + buffer);
			in.mark();//标记当前的位置，便于reset
			message.setMsgLength(in.getShort());
			message.setMsgSequence(in.getInt());
			message.setMsgSystem(in.getShort());
			message.setMsgType(in.getShort());
			
			if(message.getMsgSystem() == 0x01)
			{

				if(message.getMsgLength() == 0x0C)
				{
					//logger.info("接收到心跳报文");
				}
				else if(message.getMsgLength() == 0x10 && in.limit() >=16) //确认报文
				{
					Long msgStatus = (long)in.getInt();
					if(msgStatus == 0)
					{
						message.setMsgInfo(EnumResult.SUCCESS);
					}
					else
					{
						message.setMsgInfo(EnumResult.FAILURE);			 
					}
				}
				else if(message.getMsgLength() == 0x120 && in.limit() >= 0x120) //报警报文
				{
					TDeviceEvent deviceEvent = new TDeviceEvent();
					//获取id
					TDevID id = new TDevID(dataTypeUtil.stringTochars(in.getString(128, charsetDecoder), 128));
					deviceEvent.setId(id);
					//获取状态
					Long msgStatus = (long)in.getInt();
					if(msgStatus == 0)
						deviceEvent.setAlarmState(EnumAlarmStatus.NOALARM);
					else if(msgStatus == 1)
						deviceEvent.setAlarmState(EnumAlarmStatus.FATAL);
					else if(msgStatus == 2)
						deviceEvent.setAlarmState(EnumAlarmStatus.MAIN);
					else if(msgStatus == 3)
						deviceEvent.setAlarmState(EnumAlarmStatus.NORMAL);
						
					//获取时间
					deviceEvent.setDateTime(dataTypeUtil.stringTochars(in.getString(16, charsetDecoder), 16));
						
					//获取描述
					deviceEvent.setDescription(dataTypeUtil.stringTochars(in.getString(128, charsetDecoder), 128));
					System.out.println("描述 ： " + String.valueOf(deviceEvent.getDescription()));	
					message.setMsgInfo(deviceEvent);
						
				}
				else if(message.getMsgLength() == 0x8C && in.limit() >= 0x8C) //设置消防报文
				{
					TDevID id = new TDevID(dataTypeUtil.stringTochars(in.getString(128, charsetDecoder), 128));
					message.setMsgInfo(id);
				}
				else if(message.getMsgLength() == 0x90 && in.limit() >= 0x90 * 2) //查询报文
				{
					TDeviceState  deviceState = new TDeviceState();
					//获取ID
					TDevID id = new TDevID(dataTypeUtil.stringTochars(in.getString(128, charsetDecoder), 128));
					deviceState.setId(id);
					//获取状态
					Long msgStatus = (long)in.getInt();
					if(msgStatus == 0)
						deviceState.setStatus(EnumDeviceStatus.UNKNOWN);
					else if(msgStatus == 1)
						deviceState.setStatus(EnumDeviceStatus.ALARM);
					else if(msgStatus == 2)
						deviceState.setStatus(EnumDeviceStatus.TROUBLE);
					else if(msgStatus == 3)
						deviceState.setStatus(EnumDeviceStatus.ISOLATE);	
					
					message.setMsgInfo(deviceState);
				}
				else
				{
					in.reset();
					return false;
				}
				
				message.setMsgCRC(in.getShort());
				if(dataTypeUtil.getCheckCRC(buffer) == message.getMsgCRC() )
				{
					out.write(message);
					System.out.println("接收到了一个报文");
				}
				else
				{
					logger.info("接收到的报文效验码不对...............");
					session.write(new MessageModel((short)0x10, message.getMsgSequence(), (short)0x01, (short)0x66,EnumResult.FAILURE));
				}
				
				if(in.hasRemaining())
					return true;
				
			}
			else
			{
				logger.info("接收到的报文不符合协议格式");
				while(in.hasRemaining()) //通过读取的方式清除格式不对的报文
					in.get();
				return false;
				
			}
			
		}
		return false;
	}
	
}


/*
public class MessageDecoder extends ProtocolDecoderAdapter
{
	
	private final Logger logger = LoggerFactory.getLogger(MessageDecoder.class);
	
	private final CharsetDecoder charsetDecoder = Charset.forName(PropertiesEditer.getValue("encoding")).newDecoder();  ;

	private DataTypeUtil dataTypeUtil = new DataTypeUtil();

	@Override
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception
	{
		MessageModel message = new MessageModel();
		
		short msgCRC = 0;
		Long msgSequence = 0L;
		
		in.order(ByteOrder.BIG_ENDIAN);
		
		if(in.remaining() >= 12) //报文长度至少要大于等于心跳包才解析
		{
			String buffer = in.getHexDump();
			logger.info("接收到报文de 是 : " + buffer);
			//获取报文长度
			short msgLength = in.getShort();
			message.setMsgLength(msgLength);
			
			//获取报文序列
			msgSequence = (long)in.getInt();
			message.setMsgSequence(msgSequence);
			//报文业务
			message.setMsgSystem(in.getShort());
			//报文类型
			message.setMsgType(in.getShort());
			
			if(msgLength == 0x0C)
			{
				//nothing todo
				//logger.info("接收到心跳报文");
			}
			else if(msgLength == 0x10 && in.limit() >=16) //确认报文
			{
				Long msgStatus = (long)in.getInt();
				if(msgStatus == 0)
				{
					message.setMsgInfo(EnumResult.SUCCESS);
				}
				else
				{
					message.setMsgInfo(EnumResult.FAILURE);			 
				}
			}
			else if(msgLength == 0x120 && in.limit() >= 0x120) //报警报文
			{
				TDeviceEvent deviceEvent = new TDeviceEvent();
				//获取id
				TDevID id = new TDevID(in.getString(128, charsetDecoder).toCharArray());
				deviceEvent.setId(id);
				//获取状态
				Long msgStatus = (long)in.getInt();
				if(msgStatus == 0)
					deviceEvent.setAlarmState(EnumAlarmStatus.NOALARM);
				else if(msgStatus == 1)
					deviceEvent.setAlarmState(EnumAlarmStatus.FATAL);
				else if(msgStatus == 2)
					deviceEvent.setAlarmState(EnumAlarmStatus.MAIN);
				else if(msgStatus == 3)
					deviceEvent.setAlarmState(EnumAlarmStatus.NORMAL);
					
				//获取时间
				deviceEvent.setDateTime(in.getString(16, charsetDecoder).toCharArray());
					
				//获取描述
				deviceEvent.setDescription(in.getString(128, charsetDecoder).toCharArray());
					
				message.setMsgInfo(deviceEvent);
					
			}
			else if(msgLength == 0x8C && in.limit() >= 0x8C) //设置消防报文
			{
				TDevID id = new TDevID(in.getString(128, charsetDecoder).toCharArray());
				message.setMsgInfo(id);
			}
			else if(msgLength == 0x90 && in.limit() >= 0x90 * 2) //查询报文
			{
				TDeviceState  deviceState = new TDeviceState();
				//获取ID
				TDevID id = new TDevID(in.getString(128, charsetDecoder).toCharArray());
				deviceState.setId(id);
				//获取状态
				Long msgStatus = (long)in.getInt();
				if(msgStatus == 0)
					deviceState.setStatus(EnumDeviceStatus.UNKNOWN);
				else if(msgStatus == 1)
					deviceState.setStatus(EnumDeviceStatus.ALARM);
				else if(msgStatus == 2)
					deviceState.setStatus(EnumDeviceStatus.TROUBLE);
				else if(msgStatus == 3)
					deviceState.setStatus(EnumDeviceStatus.ISOLATE);	
				
				message.setMsgInfo(deviceState);
			}
			//获取效验CRC
			msgCRC = in.getShort();
			message.setMsgCRC(msgCRC);
			
			if(dataTypeUtil.getCheckCRC(buffer) == msgCRC )
				out.write(message);
			else
			{
				logger.info("接收到的报文效验码不对...............");
				session.write(new MessageModel((short)0x10, msgSequence, (short)0x01, (short)0x66,EnumResult.FAILURE));
			}
			//读取报文完整后将多余的清除掉
			if(in.hasRemaining())
			{
				in.getString(in.remaining(),charsetDecoder);
			}
			
		}
		else
		{
			logger.info("服务端接收到的报文为 : " + in.getHexDump());
			in.getString(in.remaining(),charsetDecoder);
			logger.info("该类型报文格式不对,无法解析");
		}
		
	}
	
}

*/