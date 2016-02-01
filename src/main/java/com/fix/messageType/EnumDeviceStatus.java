package com.fix.messageType;

public enum EnumDeviceStatus 
{
	
	UNKNOWN(0), //无法取得消防设备状态
	ALARM(1),    //消防设备报警  
	TROUBLE(2),  //消防设备故障  
	ISOLATE(3);  // 消防设备隔离
	
	private long status;
	
	private EnumDeviceStatus(long status)
	{
		this.status  =  status;
	}
	
	public long getStatus()
	{
		return this.status;
	}
}
