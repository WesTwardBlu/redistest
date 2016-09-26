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
	private Jedis jedis;//非切片额客户端连接
    private JedisPool jedisPool;//非切片连接池
    private ShardedJedis shardedJedis;//切片额客户端连接
    private ShardedJedisPool shardedJedisPool;//切片连接池
    
    public RedisClient() 
    { 
        initialPool(); 
        initialShardedPool(); 
        shardedJedis = shardedJedisPool.getResource(); 
        jedis = jedisPool.getResource(); 
    }

	private void initialShardedPool() {
		// TODO Auto-generated method stub
		// 池基本配置 
        JedisPoolConfig config = new JedisPoolConfig(); 
        config.setMaxActive(20); 
        config.setMaxIdle(5); 
        config.setMaxWait(1000l); 
        config.setTestOnBorrow(false); 
        // slave链接 
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(); 
        shards.add(new JedisShardInfo("127.0.0.1", 6379, "master")); 

        // 构造池 
        shardedJedisPool = new ShardedJedisPool(config, shards); 
	}

	private void initialPool() {
		// TODO Auto-generated method stub
		// 池基本配置 
        JedisPoolConfig config = new JedisPoolConfig(); 
        config.setMaxActive(20); 
        config.setMaxIdle(5); 
        config.setMaxWait(1000l); 
        config.setTestOnBorrow(false); 
        
        jedisPool = new JedisPool(config,"127.0.0.1",6379);
	} 
	
	public void show() {     
        StringOperate(); 
        jedisPool.returnResource(jedis);
        shardedJedisPool.returnResource(shardedJedis);
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
