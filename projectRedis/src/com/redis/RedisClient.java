package com.redis;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * redis的客户端操作
 * <br>
 * <br>知识为追寻她的人加冕...
 * */
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

	
}