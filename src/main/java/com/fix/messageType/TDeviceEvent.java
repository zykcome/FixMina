package com.fix.messageType;

import java.io.Serializable;

/**
 * 
* @ClassName: TDeviceEvent 
* @Description: 消防事件的结构
* @author zhouyoukuan
* @date 2015年10月29日 下午5:01:06 
*
 */
public class TDeviceEvent implements Serializable {

	
	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 3640795768104256398L;
	
	private TDevID id; //消防设备ID
	//消防设备的告警状态
	private EnumAlarmStatus alarmState;
	//时间为2010-11-8 11:25:39，则数据值为“20101108112539”
	private char[] dateTime = new char[16];
	//事件内容描述，ascii码字符流，内容如需分项，用<tab键>分隔
	private char[] description = new char[128];
	
	
	public TDeviceEvent(){}
	public TDeviceEvent(TDevID id,  EnumAlarmStatus alarmState, char[] dateTime,char[] description )
	{
		this.id = id;
		this.dateTime = dateTime;
		this.description = description;
		this.alarmState = alarmState;
	}
	
	
	public TDevID getId() {
		return id;
	}
	public void setId(TDevID id) {
		this.id = id;
	}
	public EnumAlarmStatus getAlarmState() {
		return alarmState;
	}
	public void setAlarmState(EnumAlarmStatus alarmState) {
		this.alarmState = alarmState;
	}
	public char[] getDateTime() {
		return dateTime;
	}
	public void setDateTime(char[] dateTime) {
		this.dateTime = dateTime;
	}
	public char[] getDescription() {
		return description;
	}
	public void setDescription(char[] description) {
		this.description = description;
	}
	
	
	
}
