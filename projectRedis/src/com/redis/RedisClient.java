package com.redis;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class RedisClient {
	private Jedis jedis;//非切片额 客户端连接
	private JedisPool jedisPool;//非切片额 连接池
	private ShardedJedis shardedJedis;//切片额客户端连接
	private ShardedJedisPool shardedJedisPool;//切片额 连接池
	
	public RedisClient() {
		initialPool();
		initialShardedPool();
		jedis= jedisPool.getResource();
		shardedJedis= shardedJedisPool.getResource();
	}
	
	/**
	 * 初始化非切片池
	 * */
	private void initialPool() {
		//池基本配置
		JedisPoolConfig config= new JedisPoolConfig();
		config.setMaxActive(20);
		config.setMaxIdle(5);
		config.setMaxWait(1000l);
		config.setTestOnBorrow(false);
		
		jedisPool= new JedisPool(config, "127.0.0.1", 6379);
	}
	
	/**
	 * 初始化切片池
	 * */
	private void initialShardedPool() {
		//池基本配置
		JedisPoolConfig config= new JedisPoolConfig();
		config.setMaxActive(20);
		config.setMaxIdle(5);
		config.setMaxWait(1000l);
		config.setTestOnBorrow(false);
		
		//slave连接
		List<JedisShardInfo> shards= new ArrayList<>();
		shards.add(new JedisShardInfo("127.0.0.1",6379,"master"));		
		
		//构造池
		shardedJedisPool= new ShardedJedisPool(config, shards);
	}
	
	public void show() {
		keyOperate();
	}

	private void keyOperate() {
		System.out.println("==============================key====================");
		//清空数据
//		System.out.println("清空库中所有数据"+ jedis.flushDB());
		//判断key是否存在
		System.out.println("判断key999键是否存在"+ shardedJedis.exists("key999"));
		System.out.println("新增key001"+ shardedJedis.set("key001", "value001"));
		System.out.println("判断key001键是否存在"+ shardedJedis.exists("key001"));
		//输出系统中所有的key
		System.out.println("新增key002"+ shardedJedis.set("key002", "value002"));
		System.out.println("系统中所有的key:"+ jedis.keys("*"));
		//删除某个key,若key不存在，则忽略此命令
		System.out.println("删除key002:"+ jedis.del("key002"));
		System.out.println("判断key002是存在:"+ shardedJedis.exists("key002"));
		//设置key001的过期时间
		System.out.println("设置key001的过期时间:"+ jedis.expire("key001", 5));
		try {
			Thread.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 查看某个key的剩余生存时间,单位【秒】.永久生存或者不存在的都返回-1
		System.out.println("查看key001的剩余生存时间:"+ jedis.ttl("key001"));
		//移除某个key的生存时间
		System.out.println("移除key001的生存时间:"+ jedis.persist("key001"));
		System.out.println("查看key001的剩余生存时间:"+ jedis.ttl("key001"));
		//查看key存储的值得类型
		System.out.println("查看key001的值类型:"+ jedis.type("key001"));
		/*
         * 一些其他方法：1、修改键名：jedis.rename("key6", "key0");
         *             2、将当前db的key移动到给定的db当中：jedis.move("foo", 1)
         */

	}
	
	private void StringOperate() {
		System.out.println("=======================string operate================");
		System.out.println("====================add================");
		jedis.set("key001", "value001");
		jedis.set("key002", "value002");
		jedis.set("key003", "value003");
		System.out.println(jedis.get("key001"));
		System.out.println(jedis.get("key002"));
		System.out.println(jedis.get("key003"));
		
		System.out.println("====================del================");
		System.out.println("del key003"+ jedis.del("key003"));
		System.out.println("key003.value"+ jedis.get("key003"));
		
		System.out.println("====================update================");
		System.out.println("在原来的数据上覆盖："+ jedis.set("key002", "key002_new"));
		System.out.println("key002.value:"+ jedis.get("key002"));
		System.out.println("在源key上追加："+ jedis.append("key002", "+new2"));
		System.out.println("key002.value:"+ jedis.get("key002"));
		
		System.out.println("=============增，删，查（多个）=============");
		/**
		 * mset,mget,同时操作多个key和value
		 * */
		System.out.println("一次性新增key201,key202,key203,key204及其对应值："+ jedis.mset("key201","value201","key202","value202","key203","value203","key204","value204"));
		System.out.println("一次性获取key201,key202,key203,key204各自对应的值："+ jedis.mget("key201","key202","key203","key204"));
		System.out.println("一次性删除key201,key202:"+ jedis.del(new String[]{"key201","key202"}));
		System.out.println("一次性获取key201,key202,key203,key204各自对应的值："+ jedis.mget("key201","key202","key203","key204"));
		//jedis具备的功能shardedJedis中也可直接使用，下面测试一些前面没用过的方法
	} 
}
