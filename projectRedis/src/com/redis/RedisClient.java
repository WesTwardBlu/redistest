package com.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.SortingParams;

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
		StringOperate();
		listOperate();
		setOperate();
		sortedsetOperate();
		hashOperate();
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
		System.out.println("---------------------------------------string2--------------------------------");
		System.out.println("=============������ֵ��ʱ��ֹ����ԭ��ֵ=============");
		System.out.println("ԭ��key301������ʱ������key301��"+ shardedJedis.setnx("key301", "value301"));
		System.out.println("ԭ��key302������ʱ������key302��"+ shardedJedis.setnx("key302", "value302"));
		System.out.println("��key302����ʱ����������key302��"+ shardedJedis.setnx("key302", "value302_2"));
		System.out.println("��ȡkey301��Ӧ��ֵ��"+ shardedJedis.get("key301"));
		System.out.println("��ȡkey302��Ӧ��ֵ��"+ shardedJedis.get("key302"));
		System.out.println("=============������Ч�ڼ�ֵ�Ա�ɾ��=============");
		// ����key����Ч�ڣ����洢���� 
		System.out.println("����key303����ָ������ʱ��Ϊ2��"+ shardedJedis.setex("key303", 2, "value303_2s"));
		System.out.println("��ȡkey303��Ӧ��ֵ��"+ shardedJedis.get("key303"));
		try {
			Thread.sleep(3000l);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("3s֮����ȡkey303:"+ shardedJedis.get("key303"));
		System.out.println("=============��ȡԭֵ������Ϊ��ֵһ�����=============");
		System.out.println("key302ԭֵ��"+ shardedJedis.getSet("key302", "value302_getset"));
		System.out.println("key302��ֵ��"+ shardedJedis.get("key302"));
		System.out.println("=============��ȡ�Ӵ�=============");
		System.out.println("��ȡkey302��Ӧֵ�е��Ӵ���"+ shardedJedis.getrange("key302", 3, 6));
	} 
	
	private void listOperate() {
		System.out.println("======================list=========================="); 
		System.out.println("=============��=============");
		shardedJedis.lpush("list1", "Vector");
		shardedJedis.lpush("list1", "ArrayList");
		shardedJedis.lpush("list1", "Vector");
		shardedJedis.lpush("list1", "linkedlist");
		shardedJedis.lpush("list1", "maplist");
		shardedJedis.lpush("list1", "seriallist");
		shardedJedis.lpush("list1", "hashlist");
		shardedJedis.lpush("list2", "3");
		shardedJedis.lpush("list2", "1");
		shardedJedis.lpush("list2", "5");
		shardedJedis.lpush("list2", "2");
		System.out.println("����Ԫ��-list1��"+ shardedJedis.lrange("list1", 0, -1));
		System.out.println("����Ԫ��-list2��"+ shardedJedis.lrange("list2", 0, -1));
		System.out.println("=============ɾ=============");
		// ɾ���б�ָ����ֵ ���ڶ�������Ϊɾ���ĸ��������ظ�ʱ������add��ȥ��ֵ�ȱ�ɾ�������ڳ�ջ
		System.out.println("�ɹ�ɾ��ָ��Ԫ�ظ���-list1��"+ shardedJedis.lrem("list1", 2, "Vector"));
		System.out.println("ɾ��ָ��Ԫ��֮��-list1��"+ shardedJedis.lrange("list1", 0, -1));
		// ɾ���������������
		System.out.println("���±�0-3����֮���Ԫ�أ�"+ shardedJedis.ltrim("list1", 0, 3));
		System.out.println("ɾ��ָ������֮��Ԫ�غ�-list1��"+ shardedJedis.lrange("list1", 0, -1));
		// �б�Ԫ�س�ջ
		System.out.println("��ջԪ�أ�"+ shardedJedis.lpop("list1"));
		System.out.println("Ԫ�س�ջ��-list1��"+ shardedJedis.lrange("list1", 0, -1));
		System.out.println("=============��=============");
		// �޸��б���ָ���±��ֵ
		shardedJedis.lset("list1", 0, "Vector_update");
		System.out.println("�±�Ϊ0��ֵ�޸ĺ�-list1��"+ shardedJedis.lrange("list1", 0, -1));
		System.out.println("=============��=============");
		// ���鳤�� 
		System.out.println("����-stringlists��"+ shardedJedis.llen("list1"));
		System.out.println("����-stringlists��"+ shardedJedis.llen("list2"));
		// ����
		/*
         * list�д��ַ���ʱ����ָ������Ϊalpha�������ʹ��SortingParams������ֱ��ʹ��sort("list")��
         * �����"ERR One or more scores can't be converted into double"
         */
		SortingParams sortingParams = new SortingParams();
		sortingParams.alpha();
		sortingParams.limit(0, 3);
		System.out.println("���������Ľ��-list1��"+ shardedJedis.sort("list1", sortingParams));
		System.out.println("���������Ľ��-list2��"+ shardedJedis.sort("list2"));
		// �Ӵ���  startΪԪ���±꣬endҲΪԪ���±ꣻ-1������һ��Ԫ�أ�-2�������ڶ���Ԫ��
		System.out.println("�Ӵ�-�ڶ�����ʼ��������"+ shardedJedis.lrange("list1", 1, -1));
		// ��ȡ�б�ָ���±��ֵ
		System.out.println("��ȡ�±�Ϊ2��Ԫ�أ�"+ shardedJedis.lindex("list1", 2));
		
	}
	
	/**
	 * ��set�Ĳ���
	 * */
	private void setOperate() {
		System.out.println("======================set=========================="); 
		System.out.println("=============��=============");
		System.out.println("��set1�����м���Ԫ��element001��"+ jedis.sadd("set1", "element001"));
		System.out.println("��set1�����м���Ԫ��element002��"+ jedis.sadd("set1", "element002"));
		System.out.println("��set1�����м���Ԫ��element003��"+ jedis.sadd("set1", "element003"));
		System.out.println("��set1�����м���Ԫ��element004��"+ jedis.sadd("set1", "element004"));
		System.out.println("�鿴set1�����е�����Ԫ��:"+ jedis.smembers("set1"));
		
		System.out.println("=============ɾ=============");
		System.out.println("����set1��ɾ��Ԫ��element003��"+ jedis.srem("set1", "element004"));
		System.out.println("�鿴set1�����е�����Ԫ��:"+ jedis.smembers("set1"));
		System.out.println("set1����������λ�õ�Ԫ�س�ջ��"+ jedis.spop("set1"));
		System.out.println("�鿴set1�����е�����Ԫ��:"+ jedis.smembers("set1"));
		System.out.println("=============��=============");
		
		System.out.println("=============��=============");
		System.out.println("�ж�element001�Ƿ��ڼ���set1�У�"+ jedis.sismember("set1", "element001"));
		System.out.println("ѭ����ѯ��ȡset1�е�ÿ��Ԫ�أ�");
		Set<String> set = jedis.smembers("set1");
		for (String string : set) {
			System.out.println(string);
		}
		
		System.out.println("=============��������=============");
		System.out.println("set2�����Ԫ��element001��"+ jedis.sadd("set2", "element001")); 
		System.out.println("set2�����Ԫ��element002��"+ jedis.sadd("set2", "element002"));
		System.out.println("set2�����Ԫ��element003��"+ jedis.sadd("set2", "element003"));
		System.out.println("set2�����Ԫ��element004��"+ jedis.sadd("set2", "element004"));
		System.out.println("set2�����Ԫ��element005��"+ jedis.sadd("set2", "element005"));
		System.out.println("set2�����Ԫ��element006��"+ jedis.sadd("set2", "element006"));
		System.out.println("�鿴set1�����е�����Ԫ��:"+ jedis.smembers("set2"));
		System.out.println("�鿴set2�����е�����Ԫ��:"+ jedis.smembers("set2"));
		System.out.println("sets1��sets2������"+ jedis.sinter("set1","set2"));
		System.out.println("sets1��sets2������"+ jedis.sunion("set1","set2"));
		System.out.println("sets1��sets2���"+ jedis.sdiff("set1","set2"));//���set1���У�set2��û�е�Ԫ��
		
	}
	
	private void sortedsetOperate() {
		System.out.println("======================zset=========================="); 
		System.out.println("=============��=============");
		System.out.println("zset1�����Ԫ��element001��"+ shardedJedis.zadd("zset1", 7.0, "element001"));
		System.out.println("zset1�����Ԫ��element002��"+ shardedJedis.zadd("zset1", 4.0, "element002"));
		System.out.println("zset1�����Ԫ��element003��"+ shardedJedis.zadd("zset1", 3.0, "element003"));
		System.out.println("zset1�����Ԫ��element004��"+ shardedJedis.zadd("zset1", 2.0, "element004"));
		System.out.println("zset1�����Ԫ��element005��"+ shardedJedis.zadd("zset1", 1.0, "element005"));
		System.out.println("zset1�����е�����Ԫ�أ�"+ shardedJedis.zrange("zset1", 0, -1));//����Ȩ��ֵ����  �����Ȩ�أ��±�
		
		System.out.println("=============ɾ=============");
		System.out.println("zset1��ɾ��Ԫ��element002��"+ shardedJedis.zrem("zset1", "element004"));
		System.out.println("zset1�����е�����Ԫ�أ�"+ shardedJedis.zrange("zset1", 0, -1));
		
		System.out.println("=============��=============");
		
		System.out.println("=============��=============");
		System.out.println("ͳ��zset�����е�Ԫ���и�����"+ shardedJedis.zcard("zset1"));
		System.out.println("ͳ��zset������Ȩ��ĳ����Χ�ڣ�1.0����5.0����Ԫ�صĸ�����"+ shardedJedis.zcount("zset1", 1.0, 4.0));
		System.out.println("�鿴zset������element004��Ȩ�أ�"+ shardedJedis.zscore("zset1", "element004"));
		System.out.println("�鿴�±�1��2��Χ�ڵ�Ԫ��ֵ��"+ shardedJedis.zrange("zset", 1, 2));
		
	}
	
	private void hashOperate() {
		System.out.println("=============��=============");
		System.out.println("hashs�����key001��value001��ֵ�ԣ�"+ shardedJedis.hset("hash1", "key001", "value001"));
		System.out.println("hashs�����key001��value001��ֵ�ԣ�"+ shardedJedis.hset("hash1", "key002", "value002"));
		System.out.println("hashs�����key003��value003��ֵ�ԣ�"+ shardedJedis.hset("hash1", "key003", "value003"));
		System.out.println("����key004��4�����ͼ�ֵ�ԣ�"+ shardedJedis.hincrBy("hash1", "key004", 41));
		System.out.println("hashs�е�����ֵ��"+ shardedJedis.hvals("hash1"));
		
		System.out.println("=============ɾ=============");
		System.out.println("hashs��ɾ��key002��ֵ�ԣ�"+ shardedJedis.hdel("hash1", "key002"));
		System.err.println("hashs�е�����ֵ��"+ shardedJedis.hvals("hash1"));
		
		System.out.println("=============��=============");
		System.out.println("key004���ͼ�ֵ��ֵ����100��"+ shardedJedis.hincrBy("hash1", "key004", 100L));
		System.err.println("hashs�е�����ֵ��"+ shardedJedis.hvals("hash1"));
		
		System.out.println("=============��=============");
		System.out.println("�ж�key003�Ƿ���ڣ�"+ shardedJedis.hexists("hash1", "key003"));
		System.out.println("��ȡkey004��Ӧ��ֵ��"+ shardedJedis.hget("hash1", "key004"));
		System.out.println("������ȡkey001��key003��Ӧ��ֵ��"+ shardedJedis.hmget("hash1", "key001","key002"));
		System.out.println("��ȡhashs�����е�key��"+ shardedJedis.hkeys("hash1"));
		System.out.println("��ȡhashs�����е�value��"+ shardedJedis.hvals("hash1"));
		
	}
}
