package com.fix.client;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fix.message.MessageModel;
import com.fix.messageType.EnumAlarmStatus;
import com.fix.messageType.EnumResult;
import com.fix.messageType.PK_Type;
import com.fix.messageType.TDevID;
import com.fix.messageType.TDeviceEvent;
import com.fix.util.DataTypeUtil;

public class TestMessage
{
	
	private DataTypeUtil dataTypeUtil = new DataTypeUtil();
	
	//确认报文
	public MessageModel getAckMessage(MessageModel msg)
	{
		MessageModel message = new MessageModel((short)0x10, msg.getMsgSequence(), (short)0x01, (short)0x66,EnumResult.SUCCESS);
		return message;
	}
	
	//告警报文
	public MessageModel getAlarmMessage()
	{
		TDeviceEvent event = new TDeviceEvent();
		event.setAlarmState(EnumAlarmStatus.NOALARM);
		
		String now = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		char[] ch = dataTypeUtil.stringTochars(now, 16);
		event.setDateTime(ch);
		
		TDevID id = new TDevID(dataTypeUtil.stringTochars("20001107D108",128));
		event.setId(id);
		
		String descr= "测试火灾 this is a test";
		event.setDescription(dataTypeUtil.stringTochars(descr,128));
		
		MessageModel message = new MessageModel((short)0x120,1L, (short)0x01,PK_Type.DEVICE_ALARM,event);
		
		return message;
	}
	
	
	//查询报文
	public MessageModel getQueryMessage()
	{
		MessageModel msg = new MessageModel();
		msg.setMsgLength((short)0x8C);
		msg.setMsgSequence(10L);
		msg.setMsgSystem((short)0x01);
		msg.setMsgType(PK_Type.REQ_DEVICE_RESET);
		msg.setMsgInfo(new TDevID(dataTypeUtil.stringTochars("20001107D110",128)));
		
		return msg;	
				
	}
	
	//查询返回的报文
	public MessageModel getStatusMessage(MessageModel msg)
	{
		MessageModel message = new MessageModel();
		message.setMsgLength((short)0x10);
		message.setMsgSequence(msg.getMsgSequence());
		message.setMsgSystem((short)0x01);
		message.setMsgType(PK_Type.REQ_DEVICE_RESET_ACK);
		message.setMsgInfo(EnumResult.SUCCESS);
		return message;
	}
	
	
}
