package com.redis;

import java.util.Set;

import redis.clients.jedis.Jedis;

/**
 * jedis keys
 * */
public class TestRedisKeys {
	public static void main(String[] args) {
		Jedis jedis= new Jedis("localhost");
		Set<String> set = jedis.keys("*");
		for (String string : set) {
			System.out.println(string);
		}
		System.out.println(jedis.info());;
	}
}
