package com.fix.util;

/**
 * 
* @ClassName: MsgTransformation 
* @Description: 将报文转换成16进制的工具类
* @author zhouyoukuan
* @date 2015年11月9日 上午10:07:48 
*
 */
public class DataTypeUtil {

	public DataTypeUtil(){}
	
	/**
	 * 
	* @Title: toHexString 
	* @Description: 报文转换成16进制的工具类
	* @param @param src
	* @param @param len
	* @param @return    
	* @return String   
	* @throws
	 */
	public String toHexString(int src, int len)
	{
		String hexStr = String.format("%1$02X", src);
		while(hexStr.length() < len)
			hexStr = "0" + hexStr;
		return hexStr;
	}
	
	public String toHexString(long src, int len)
	{
		String hexStr = String.format("%1$02X", src);
		while(hexStr.length() < len)
			hexStr = "0" + hexStr;
		return hexStr;
	}
	
	public String toHexString(short src, int len)
	{
		String hexStr = String.format("%1$02X", src);
		while(hexStr.length() < len)
			hexStr = "0" + hexStr;
		return hexStr;
	}
	
	public String toHexString(char src, int len)
	{
		String hexStr = String.format("%1$02X", src);
		while(hexStr.length() < len)
			hexStr = "0" + hexStr;
		return hexStr;
	}
	
	public char hexToChar(String hexStr)
	{
		int hex = Integer.valueOf(hexStr.trim(), 16);
		String Str = String.format("%c", hex);
		return Str.charAt(0);
	}
	
	/**
	 * 
	* @Title: stringTochars 
	* @Description: 将字符串转换成指定长度的数组,数组长度比字符串大时,用'\0'填充数组剩余的元素
	* @param @param src
	* @param @return    
	* @return char[]   
	* @throws
	 */
	public char[] stringTochars(String src , int len)
	{
		char[] array = new char[len];
		for(int i=0; src.length() > i; i++)
			array[i] = src.charAt(i);
		for(int i = src.length() ; i < len; i++ )
			array[i] = '\0';
		return array;
	}
	
	
	/**
	 * 
	* @Title: charsToString 
	* @Description: 将字符数组转成字符串,遇到'\0'则结束
	* @param @param chars
	* @param @return    
	* @return String   
	* @throws
	 */
	public String charsToString(char[] chars)
	{
		StringBuffer buffer = new StringBuffer();
		for(int size = 0; size < chars.length; size++)
		{
			if(chars[size] == '\0')
				break;
			buffer.append(chars[size]);
		}
		return buffer.toString();
	}
	
	/**
	 * 
	* @Title: getMsgCRC 
	* @Description: 编码计算报文的CRC
	* @param @param hex
	* @param @return    
	* @return short   
	* @throws
	 */
	public short getMsgCRC(String hex)
	{
		short CRC = 0;
		String[] hexArray = hex.split(" ");
		for(int i= 2; i< hexArray.length; i++)
		{
			if(CRC < Short.MAX_VALUE)
				CRC += Short.valueOf(hexArray[i].trim(),16);
		}
		return CRC;
	}
	
	/**
	 * 
	* @Title: getCheckCRC 
	* @Description: 解码,计算接收报文的CRC
	* @param @param hexDump
	* @param @return    
	* @return short   
	* @throws
	 */
	public short getCheckCRC(String hexDump)
	{
		short CRC = 0;
		String[] hexArray = hexDump.split(" ");
		short msgLength = Short.valueOf(hexArray[0] + hexArray[1], 16);
		for(int i = 2; i < msgLength - 2; i++)
		{
			if(CRC < Short.MAX_VALUE)
				CRC += Short.valueOf(hexArray[i], 16);
		}
		return CRC;
	}
	
	
	
	
}
