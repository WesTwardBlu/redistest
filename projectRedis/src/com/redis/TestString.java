package com.redis;

import redis.clients.jedis.Jedis;

public class TestString {
	public static void main(String[] args) {
		//连接本地的redis服务
		Jedis jedis= new Jedis("localhost");
		System.out.println("connecting redis success");
		//设置redis字符串数据
		jedis.set("mystr", "hello world");
		//获取redis字符串
		System.out.println("get str:"+ jedis.get("mystr"));;
	}
}
