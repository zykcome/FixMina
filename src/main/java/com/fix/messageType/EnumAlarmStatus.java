package com.fix.messageType;

public enum EnumAlarmStatus 
{
	NOALARM(0),	 //正常状态
	FATAL(1),	 //紧急告警
	MAIN(2),	  //重要告警
	NORMAL(3);	//一般告警
	
	private long status;
	
	private EnumAlarmStatus(long status)
	{
		 this.status = status;
	}

	public long getStatus() {
		return status;
	}
	
}
