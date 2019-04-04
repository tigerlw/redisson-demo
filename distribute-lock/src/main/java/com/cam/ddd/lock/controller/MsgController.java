package com.cam.ddd.lock.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.LogManager;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import com.cam.ddd.lock.dao.MsgDao;
import com.cam.ddd.lock.model.MsgModel;

@RestController
public class MsgController {
	
	private final org.apache.logging.log4j.Logger logger = LogManager.getLogger(MsgController.class);
	
	@Autowired
	private MsgDao msgDao;
	
	@Autowired
	private RedissonClient redisson;
	
	@RequestMapping(value = "/addApi" ,method = RequestMethod.GET)
	public String addApi(@RequestParam String apiName)
	{
	    String result = Thread.currentThread().getName();
		List<MsgModel> msgModelList = msgDao.findByMsg(apiName);
		
		if(!msgModelList.isEmpty())
		{
			result = msgModelList.get(0).getMsg();
		}
		
		
		logger.error("addApi=====================;"+result);
		
		
		RBucket<String> keyObject =redisson.getBucket("liuwei");
		
		
		logger.error("redis=====================;"+keyObject.get());
		
		
		return result;
	}
	
	
	@RequestMapping(value = "/addMsg" ,method = RequestMethod.GET)
	public String addMsg(@RequestParam String apiName)
	{
		Long wasteTime  = System.currentTimeMillis();
		
		RLock lock = redisson.getLock("addMsg");
		
		for (int i = 0; i < 100; i++) 
		{
			try {

				lock.lock();
				
				List<MsgModel> msgModelList = msgDao.findByMsg(apiName);

				MsgModel msgModel = null;

				if (!msgModelList.isEmpty()) {
					msgModel = msgModelList.get(0);
				}

				int count = msgModel.getMsgCount();

				msgModel.setMsgCount(++count);

				msgDao.update(msgModel);
				
			} finally {
				lock.unlock();
			}
		}
		
		/*List<MsgModel> msgModelList = msgDao.findByMsg(apiName);

		MsgModel msgModel = null;

		if (!msgModelList.isEmpty()) {
			msgModel = msgModelList.get(0);
		}

		int count = msgModel.getMsgCount();

		msgModel.setMsgCount(++count);

		msgDao.update(msgModel);*/
		
		wasteTime = System.currentTimeMillis() - wasteTime;
		
		logger.error("lock wasteTime:"+wasteTime);

		return Thread.currentThread().getName();
	}
	
	
	@RequestMapping(value = "/addMsgCas" ,method = RequestMethod.GET)
	public String addMsgCas(@RequestParam String apiName)
	{
		Long wasteTime  = System.currentTimeMillis();
		
		RLock lock = redisson.getLock("addMsg");

		RBucket<String> keyObject = redisson.getBucket("cas");

		for (int i = 0; i < 100; i++) {
			boolean finished = false;

			while (!finished) {

				String cas = keyObject.get()!=null?keyObject.get():"";

				List<MsgModel> msgModelList = msgDao.findByMsg(apiName);

				MsgModel msgModel = null;

				if (!msgModelList.isEmpty()) {
					msgModel = msgModelList.get(0);
				}

				int count = msgModel.getMsgCount();

				msgModel.setMsgCount(++count);

				try {

					lock.lock();

					String tmp = keyObject.get();

					if (cas.equals(tmp)) {
						
						msgDao.update(msgModel);

						keyObject.set(String.valueOf(System.currentTimeMillis()));

						finished = true;
					}else
					{
						logger.error("try again =====================================");
					}

				} finally {
					lock.unlock();
				}

			}
		}
		
		wasteTime = System.currentTimeMillis() - wasteTime;
		
		logger.error("cas wasteTime:"+wasteTime);

		return Thread.currentThread().getName();
	}

}
