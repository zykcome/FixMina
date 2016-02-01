package com.fix.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * 
* @ClassName: XfXtCodecFactory 
* @Description: 数据包的解码和编码工厂
* @author zhouyoukuan
* @date 2015年10月28日 下午3:07:30 
*
 */
public class FixCodecFactory implements ProtocolCodecFactory{

	private ProtocolEncoder encoder;
	private ProtocolDecoder decoder;
	
	public FixCodecFactory()
	{
		this.encoder = new MessageEncoder();
		this.decoder = new MessageDecoder();
	}
	
	public ProtocolEncoder getEncoder() {
		return encoder;
	}

	public void setEncoder(ProtocolEncoder encoder) {
		this.encoder = encoder;
	}

	public ProtocolDecoder getDecoder() {
		return decoder;
	}

	public void setDecoder(ProtocolDecoder decoder) {
		this.decoder = decoder;
	}

	
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return this.decoder;
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return this.encoder;
	}

}
