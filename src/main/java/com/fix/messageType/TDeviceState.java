package com.fix.messageType;

import java.io.Serializable;

/**
 * 
* @ClassName: TDeviceState 
* @Description: 消防设备当前状态的结构
* @author zhouyoukuan
* @date 2015年10月29日 下午4:35:07 
*
 */
public class TDeviceState  implements Serializable
{
	
	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 4064489480445167817L;
	private TDevID  id;
	private EnumDeviceStatus status;
	
	public TDeviceState(){}
	public TDeviceState(TDevID id, EnumDeviceStatus status)
	{
		this.id = id;
		this.status = status;
	}
	
	
	public TDevID getId() {
		return id;
	}
	public void setId(TDevID id) {
		this.id = id;
	}
	
	public EnumDeviceStatus getStatus() {
		return status;
	}
	public void setStatus(EnumDeviceStatus status) {
		this.status = status;
	}
	
}
