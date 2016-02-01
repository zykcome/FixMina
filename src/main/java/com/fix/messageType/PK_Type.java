package com.fix.messageType;


/**
 * 
* @ClassName: PK_Type 
* @Description: 报文类型定义类
* @author zhouyoukuan
* @date 2015年10月29日 下午3:40:36 
*
 */
public class PK_Type {

	//心跳报文
	public static final short HEART_BEAT = 101;
	
	//应答
	public static final short ACK = 102;
	
	//消防设备火灾告警
	public static final short DEVICE_ALARM = 201;
	
	//消防设备故障告警
	public static final short DEVICE_TROUBLE = 202;
	
	//消防设备备电故障
	public static final short DEVICE_BATTERY_TROUBLE = 203;
	
	//消防设备主电故障
	public static final short DEVICE_AC_TROUBLE = 204;
	
	//消防设备隔离
	public static final short DEVICE_ISOLATE = 205;
	
	//消防设备在线状态
	public static final short DEVICE_LOST = 206;
	
	//请求消防设备当前状态
	public static final short GET_DEVICE_STATE = 207;
	
	//请求消防设备当前状态响应
	public static final short SET_DEVICE_STATE = 208;
	
	//复位消防设备请求
	public static final short REQ_DEVICE_RESET = 301;
	
	//复位消防设备请求响应
	public static final short REQ_DEVICE_RESET_ACK = 302;
	
	//消防设备消音请求
	public static final short REQ_DEVICE_SILENCE = 303;
	
	//消防设备消音请求响应
	public static final short REQ_DEVICE_SILENCE_ACK = 304;
	
	//消防设备报警恢复请求
	public static final short REQ_ALARM_OFF = 305;
	
	//消防设备报警恢复请求响应
	public static final short REQ_ALARM_OFF_ACK = 306;
	
}
