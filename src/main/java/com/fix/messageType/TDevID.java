package com.fix.messageType;

/**
 * 
* @ClassName: TDevID 
* @Description: 消防设备标识的结构
* @author zhouyoukuan
* @date 2015年10月29日 下午4:33:06 
*
 */
public class TDevID
{
	//消防设备ID号，ascii码字符流，在第三方系统中可以唯一标识一个消防设备
	private char[] Addr = new char[128];
	
	public TDevID(){}
	public TDevID(char[] addr)
	{
		this.Addr = addr;
	}
	
	public char[] getAddr() {
		return Addr;
	}

	public void setAddr(char[] addr) {
		Addr = addr;
	} 
	
}
