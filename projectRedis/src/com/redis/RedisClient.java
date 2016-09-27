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
 * redis�Ŀͻ��˲���
 * <br>
 * <br>֪ʶΪ׷Ѱ�����˼���...
 * */
public class RedisClient {
	private Jedis jedis;//����Ƭ�� �ͻ�������
	private JedisPool jedisPool;//����Ƭ�� ���ӳ�
	private ShardedJedis shardedJedis;//��Ƭ��ͻ�������
	private ShardedJedisPool shardedJedisPool;//��Ƭ�� ���ӳ�
	
	public RedisClient() {
		initialPool();
		initialShardedPool();
		jedis= jedisPool.getResource();
		shardedJedis= shardedJedisPool.getResource();
	}
	
	/**
	 * ��ʼ������Ƭ��
	 * */
	private void initialPool() {
		//�ػ�������
		JedisPoolConfig config= new JedisPoolConfig();
		config.setMaxActive(20);
		config.setMaxIdle(5);
		config.setMaxWait(1000l);
		config.setTestOnBorrow(false);
		
		jedisPool= new JedisPool(config, "127.0.0.1", 6379);
	}
	
	/**
	 * ��ʼ����Ƭ��
	 * */
	private void initialShardedPool() {
		//�ػ�������
		JedisPoolConfig config= new JedisPoolConfig();
		config.setMaxActive(20);
		config.setMaxIdle(5);
		config.setMaxWait(1000l);
		config.setTestOnBorrow(false);
		
		//slave����
		List<JedisShardInfo> shards= new ArrayList<>();
		shards.add(new JedisShardInfo("127.0.0.1",6379,"master"));		
		
		//�����
		shardedJedisPool= new ShardedJedisPool(config, shards);
	}
	
	public void show() {
		keyOperate();
	}

	
}