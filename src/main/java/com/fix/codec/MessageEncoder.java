package com.fix.codec;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fix.message.MessageModel;
import com.fix.messageType.EnumResult;
import com.fix.messageType.TDevID;
import com.fix.messageType.TDeviceEvent;
import com.fix.messageType.TDeviceState;
import com.fix.util.DataTypeUtil;

/**
 * 
* @ClassName: MessageEncoder 
* @Description: 要发送报文的编码
* @author zhouyoukuan
* @date 2015年10月28日 下午3:08:31 
*
 */
public class MessageEncoder implements ProtocolEncoder {

	private final Logger logger = LoggerFactory.getLogger(MessageEncoder.class);
	private final DataTypeUtil dataTypeUtil = new DataTypeUtil();
	private final CharsetEncoder charsetEncoder = Charset.forName("UTF-8").newEncoder();
	
	public void encode(IoSession session, Object object, ProtocolEncoderOutput out)
			throws Exception {
		
		
		MessageModel message = (MessageModel)object;
		
		IoBuffer buffer = IoBuffer.allocate(1024);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.setAutoExpand(true);
		
		//报文长度
		buffer.putShort(message.getMsgLength());
		
		//报文序号
		buffer.putInt((int)message.getMsgSequence());

		//报文业务
		buffer.putShort(message.getMsgSystem());
		//报文类型
		buffer.putShort(message.getMsgType());
		
		//报文内容
		Object msgInfo = message.getMsgInfo();
		if(msgInfo instanceof TDeviceEvent)
		{
			TDeviceEvent deviceEvent = (TDeviceEvent) msgInfo;
			//ID
			char[] idArray = deviceEvent.getId().getAddr();
			buffer.putString(String.valueOf(idArray), charsetEncoder);
			//状态
			Long status = deviceEvent.getAlarmState().getStatus();
			buffer.putInt(status.intValue());
			
			//时间
			char[] date = deviceEvent.getDateTime();
			buffer.putString(String.valueOf(date),charsetEncoder);
			//事件描述
			char[] eventDesc= deviceEvent.getDescription();
			IoBuffer buf = IoBuffer.allocate(128);
			for(char desc : eventDesc)
			{
				int len = String.valueOf(desc).getBytes("UTF-8").length;
				if( 3 == len && buf.remaining() > 3 )
				{
					buf.putString(String.valueOf(desc), charsetEncoder);
				}
				else if(len == 1 && buf.remaining() > 0)
					buf.putString(String.valueOf(desc), charsetEncoder);
			}
			while(buf.remaining() > 0) // remaining = limit - position
				buf.put((byte)'\0');
			buf.flip();
			buffer.put(buf);
		
		}
		else if(msgInfo instanceof TDevID)
		{
			char[] idArray = ((TDevID) msgInfo).getAddr();
			buffer.putString(String.valueOf(idArray), charsetEncoder);
			
		}
		else if(msgInfo instanceof TDeviceState)
		{
			
			TDeviceState deviceState = (TDeviceState) msgInfo;
			char[] idArray = deviceState.getId().getAddr();
			buffer.putString(String.valueOf(idArray), charsetEncoder);
			Long status = deviceState.getStatus().getStatus();
			buffer.putInt(status.intValue());
			
		}
		else if(msgInfo instanceof EnumResult)//AKC类的报文
		{
			//logger.info("确认报文的值为 : " + ((EnumResult) msgInfo).getResult());
			buffer.putInt(new Long(((EnumResult) msgInfo).getResult()).intValue());
		}
		
		buffer.flip();
		//报文效验
		String hexDump = buffer.getHexDump();
		buffer.position(buffer.remaining());
		buffer.putShort(dataTypeUtil.getMsgCRC(hexDump));
		//在报文后面添加换行符
		//String lineSeparator = System.getProperty("line.separator", "\n");
		buffer.flip();
		out.write(buffer);
		logger.info("服务端发出的报文为  : " + buffer.getHexDump());
		logger.info("发出报文的长度为 : " + buffer.getHexDump().split(" ").length);
		
	}



	public void dispose(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
