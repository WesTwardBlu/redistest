package com.redis;

import redis.clients.jedis.Jedis;

public class TestString {
	public static void main(String[] args) {
		//���ӱ��ص�redis����
		Jedis jedis= new Jedis("localhost");
		System.out.println("connecting redis success");
		//����redis�ַ�������
		jedis.set("mystr", "hello world");
		//��ȡredis�ַ���
		System.out.println("get str:"+ jedis.get("mystr"));;
	}
}
