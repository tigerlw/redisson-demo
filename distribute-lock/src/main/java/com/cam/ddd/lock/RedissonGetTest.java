package com.cam.ddd.lock;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedissonGetTest 
{
	
	public static void main(String args[])
	{
		Config config = new Config();

		config.setCodec(new org.redisson.client.codec.StringCodec());
		config.useSingleServer()
		      .setAddress("redis://127.0.0.1:6379");
		
		RedissonClient redisson = Redisson.create(config);
		
		RBucket<String> keyObject =redisson.getBucket("ttt");
		
		while (true) 
		{
			String result = keyObject.get();

			System.out.println(result);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
