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

	private void keyOperate() {
		System.out.println("==============================key====================");
		//�������
//		System.out.println("��տ�����������"+ jedis.flushDB());
		//�ж�key�Ƿ����
		System.out.println("�ж�key999���Ƿ����"+ shardedJedis.exists("key999"));
		System.out.println("����key001"+ shardedJedis.set("key001", "value001"));
		System.out.println("�ж�key001���Ƿ����"+ shardedJedis.exists("key001"));
		//���ϵͳ�����е�key
		System.out.println("����key002"+ shardedJedis.set("key002", "value002"));
		System.out.println("ϵͳ�����е�key:"+ jedis.keys("*"));
		//ɾ��ĳ��key,��key�����ڣ�����Դ�����
		System.out.println("ɾ��key002:"+ jedis.del("key002"));
		System.out.println("�ж�key002�Ǵ���:"+ shardedJedis.exists("key002"));
		//����key001�Ĺ���ʱ��
		System.out.println("����key001�Ĺ���ʱ��:"+ jedis.expire("key001", 5));
		try {
			Thread.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// �鿴ĳ��key��ʣ������ʱ��,��λ���롿.����������߲����ڵĶ�����-1
		System.out.println("�鿴key001��ʣ������ʱ��:"+ jedis.ttl("key001"));
		//�Ƴ�ĳ��key������ʱ��
		System.out.println("�Ƴ�key001������ʱ��:"+ jedis.persist("key001"));
		System.out.println("�鿴key001��ʣ������ʱ��:"+ jedis.ttl("key001"));
		//�鿴key�洢��ֵ������
		System.out.println("�鿴key001��ֵ����:"+ jedis.type("key001"));
		/*
         * һЩ����������1���޸ļ�����jedis.rename("key6", "key0");
         *             2������ǰdb��key�ƶ���������db���У�jedis.move("foo", 1)
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
		System.out.println("��ԭ���������ϸ��ǣ�"+ jedis.set("key002", "key002_new"));
		System.out.println("key002.value:"+ jedis.get("key002"));
		System.out.println("��Դkey��׷�ӣ�"+ jedis.append("key002", "+new2"));
		System.out.println("key002.value:"+ jedis.get("key002"));
		
		System.out.println("=============����ɾ���飨�����=============");
		/**
		 * mset,mget,ͬʱ�������key��value
		 * */
		System.out.println("һ��������key201,key202,key203,key204�����Ӧֵ��"+ jedis.mset("key201","value201","key202","value202","key203","value203","key204","value204"));
		System.out.println("һ���Ի�ȡkey201,key202,key203,key204���Զ�Ӧ��ֵ��"+ jedis.mget("key201","key202","key203","key204"));
		System.out.println("һ����ɾ��key201,key202:"+ jedis.del(new String[]{"key201","key202"}));
		System.out.println("һ���Ի�ȡkey201,key202,key203,key204���Զ�Ӧ��ֵ��"+ jedis.mget("key201","key202","key203","key204"));
		//jedis�߱��Ĺ���shardedJedis��Ҳ��ֱ��ʹ�ã��������һЩǰ��û�ù��ķ���
	} 
}
