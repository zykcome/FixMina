package com.fix.messageType;

/**
 * 
* @ClassName: EnumResult 
* @Description: 报文返回的结果
* @author zhouyoukuan
* @date 2015年10月29日 下午4:21:59 
*
 */
public enum EnumResult
{
	SUCCESS(0), FAILURE(1);
	
	private long result;
	
	private EnumResult(long result)
	{
		this.result = result;
	}

	public long getResult() {
		return result;
	}
	
	
}


