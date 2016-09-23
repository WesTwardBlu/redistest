package com.redis;

import java.util.List;

import redis.clients.jedis.Jedis;

/**
 * jedis²Ù×÷list
 * <br>lpush ,lrange
 * */
public class TestRedisList {
	public static void main(String[] args) {
		Jedis jedis= new Jedis("localhost");
		System.out.println(jedis.ping());
		jedis.lpush("mylist", "hello");
		jedis.lpush("mylist", "world");
		jedis.lpush("mylist", "okok");
		List<String> list = jedis.lrange("mylist", 0, -1);
		for (String string : list) {
			
			System.out.println(string);
		}
		
	}
}
