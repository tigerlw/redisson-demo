package com.cam.ddd.lock;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

public class RedissionTest 
{
	public static void main(String args[])
	{
		Config config = new Config();

		config.setCodec(new org.redisson.client.codec.StringCodec());
		config.useSingleServer()
		      .setAddress("redis://127.0.0.1:6379");
		
		RedissonClient redisson = Redisson.create(config);
		

	/*	RBucket<String> keyObject =redisson.getBucket("liuwei");
		
		System.out.println(keyObject.get());
		
		keyObject.set("333");
		
		keyObject =redisson.getBucket("liuwei");
		
		System.out.println(keyObject.get());*/
		
		
		Executor executor = Executors.newCachedThreadPool();
		
		
		for(int i=0;i<10;i++)
		{
			executor.execute(new ThreadRunner(redisson));
		}
		
		
		while(true)
		{
			System.out.println(ThreadRunner.count);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	   
		
		
		
	}
	
	
	public static class ThreadRunner implements Runnable
	{
		public static int count = 0;
		
		private RedissonClient redisson;
		
		private RLock lock;
		
		public ThreadRunner(RedissonClient redisson)
		{
					
			Config config = new Config();

			config.setCodec(new org.redisson.client.codec.StringCodec());
			config.useSingleServer()
			      .setAddress("redis://127.0.0.1:6379");
			
			RedissonClient redissonC = Redisson.create(config);
			
			lock = redissonC.getLock("anyLock");
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			
			Long wasteTime = System.currentTimeMillis();
			
			for (int i = 0; i < 100; i++) 
			{

				/*try {
					lock.lock();

					count++;

				} finally {
					lock.unlock();
				}*/
				
				//count++;
				
				synchronized(ThreadRunner.class)
				{
					count++;
				}
				
			}
			
			wasteTime = System.currentTimeMillis() - wasteTime;
			
			
			System.out.println(Thread.currentThread().getName()+";wasteTime:"+wasteTime);
			
		}
		
		
	}

}
