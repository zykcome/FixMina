package com.fix.message;

import java.io.Serializable;

/**
 * 
* @ClassName: MessageModel 
* @Description: 传递消息的数据格式 报文长度（Length+报文序号（Sequence)+报文业务（System）+
*      	报文类型（PK_Type）+报文内容（Info)+校验（CRC）
* @author zhouyoukuan
* @date 2015年10月28日 下午3:11:00 
*
 */
public class MessageModel implements Serializable 
{ 
	private static final long serialVersionUID = -2319486072972095761L;
	
	//报文长度指从报文长度字段到校验字段的字节总数
	private short msgLength;
	//报文的序列号
	private long msgSequence;
	//报文的业务
	private short msgSystem;
	//报文的类型
	private short msgType;
	//报文内容
	private Object msgInfo;
	/*
	报文的校验数据为：报文序号（Sequence）、报文业务（System）、
	报文类型（PK_Type）、报文内容（Info）中所有字节的累加和。
	*/
	private short msgCRC;
	
	public MessageModel(){}
	
	public MessageModel(short msgLength, long msgSequence, short msgSystem,
			short msgType, Object msgInfo) 
	{
		this.msgLength = msgLength;
		this.msgSequence = msgSequence;
		this.msgSystem = msgSystem;
		this.msgType = msgType;
		this.msgInfo = msgInfo;
	}
	
	public short getMsgLength() {
		return msgLength;
	}
	public void setMsgLength(short msgLength) {
		this.msgLength = msgLength;
	}
	public long getMsgSequence() {
		return msgSequence;
	}
	public void setMsgSequence(long msgSequence) {
		this.msgSequence = msgSequence;
	}
	public short getMsgSystem() {
		return msgSystem;
	}
	public void setMsgSystem(short msgSystem) {
		this.msgSystem = msgSystem;
	}
	public short getMsgType() {
		return msgType;
	}
	public void setMsgType(short msgType) {
		this.msgType = msgType;
	}
	public Object getMsgInfo() {
		return msgInfo;
	}
	public void setMsgInfo(Object msgInfo) {
		this.msgInfo = msgInfo;
	}
	public short getMsgCRC() {
		return msgCRC;
	}
	public void setMsgCRC(short msgCRC) {
		this.msgCRC = msgCRC;
	}
}
