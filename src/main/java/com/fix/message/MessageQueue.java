package com.fix.message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
* @ClassName: MessageQueue 
* @Description: 消息队列工具类
* @author zhouyoukuan
* @date 2015年11月12日 下午6:02:04 
*
 */
public class MessageQueue
{
		
	private final Logger logger = LoggerFactory.getLogger(MessageQueue.class);
	
	BlockingQueue<MessageModel> msgQueue = new ArrayBlockingQueue<MessageModel>(1);

	public BlockingQueue<MessageModel> getMsgQueue() {
		return msgQueue;
	}

	public void setMsgQueue(BlockingQueue<MessageModel> msgQueue) {
		this.msgQueue = msgQueue;
	} 
	
	public void put(MessageModel message) throws InterruptedException
	{
		msgQueue.put(message);
		logger.info("消息已经加入队列............");
	}
	
	public MessageModel take() throws InterruptedException
	{
		MessageModel message = msgQueue.take();
		logger.info("从队列中取出消息.............");
		return message;
	}
	
	public boolean offer(MessageModel message)
	{
		return msgQueue.offer(message);
	}
	
}
