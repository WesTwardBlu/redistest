package com.redis;

import redis.clients.jedis.Jedis;

public class TestRedisConn {
	public static void main(String[] args) {
		//���ӱ��ص�redis����
		Jedis jedis= new Jedis("localhost");
		System.out.println("connection redis successfully");
		System.out.println("server is running:"+ jedis.ping());
	}
}
