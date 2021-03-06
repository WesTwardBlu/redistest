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
		StringOperate();
		listOperate();
		setOperate();
		sortedsetOperate();
		hashOperate();
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
		System.out.println("---------------------------------------string2--------------------------------");
		System.out.println("=============新增键值对时防止覆盖原先值=============");
		System.out.println("原先key301不存在时，新增key301："+ shardedJedis.setnx("key301", "value301"));
		System.out.println("原先key302不存在时，新增key302："+ shardedJedis.setnx("key302", "value302"));
		System.out.println("当key302存在时，尝试新增key302："+ shardedJedis.setnx("key302", "value302_2"));
		System.out.println("获取key301对应的值："+ shardedJedis.get("key301"));
		System.out.println("获取key302对应的值："+ shardedJedis.get("key302"));
		System.out.println("=============超过有效期键值对被删除=============");
		// 设置key的有效期，并存储数据 
		System.out.println("新增key303，并指定过期时间为2秒"+ shardedJedis.setex("key303", 2, "value303_2s"));
		System.out.println("获取key303对应的值："+ shardedJedis.get("key303"));
		try {
			Thread.sleep(3000l);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("3s之后，再取key303:"+ shardedJedis.get("key303"));
		System.out.println("=============获取原值，更新为新值一步完成=============");
		System.out.println("key302原值："+ shardedJedis.getSet("key302", "value302_getset"));
		System.out.println("key302新值："+ shardedJedis.get("key302"));
		System.out.println("=============获取子串=============");
		System.out.println("获取key302对应值中的子串："+ shardedJedis.getrange("key302", 3, 6));
	} 
	
	private void listOperate() {
		System.out.println("======================list=========================="); 
		System.out.println("=============增=============");
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
		System.out.println("所有元素-list1："+ shardedJedis.lrange("list1", 0, -1));
		System.out.println("所有元素-list2："+ shardedJedis.lrange("list2", 0, -1));
		System.out.println("=============删=============");
		// 删除列表指定的值 ，第二个参数为删除的个数（有重复时），后add进去的值先被删，类似于出栈
		System.out.println("成功删除指定元素个数-list1："+ shardedJedis.lrem("list1", 2, "Vector"));
		System.out.println("删除指定元素之后-list1："+ shardedJedis.lrange("list1", 0, -1));
		// 删除区间以外的数据
		System.out.println("除下标0-3区间之外的元素："+ shardedJedis.ltrim("list1", 0, 3));
		System.out.println("删除指定区间之外元素后-list1："+ shardedJedis.lrange("list1", 0, -1));
		// 列表元素出栈
		System.out.println("出栈元素："+ shardedJedis.lpop("list1"));
		System.out.println("元素出栈后-list1："+ shardedJedis.lrange("list1", 0, -1));
		System.out.println("=============改=============");
		// 修改列表中指定下标的值
		shardedJedis.lset("list1", 0, "Vector_update");
		System.out.println("下标为0的值修改后-list1："+ shardedJedis.lrange("list1", 0, -1));
		System.out.println("=============查=============");
		// 数组长度 
		System.out.println("长度-stringlists："+ shardedJedis.llen("list1"));
		System.out.println("长度-stringlists："+ shardedJedis.llen("list2"));
		// 排序
		/*
         * list中存字符串时必须指定参数为alpha，如果不使用SortingParams，而是直接使用sort("list")，
         * 会出现"ERR One or more scores can't be converted into double"
         */
		SortingParams sortingParams = new SortingParams();
		sortingParams.alpha();
		sortingParams.limit(0, 3);
		System.out.println("返回排序后的结果-list1："+ shardedJedis.sort("list1", sortingParams));
		System.out.println("返回排序后的结果-list2："+ shardedJedis.sort("list2"));
		// 子串：  start为元素下标，end也为元素下标；-1代表倒数一个元素，-2代表倒数第二个元素
		System.out.println("子串-第二个开始到结束："+ shardedJedis.lrange("list1", 1, -1));
		// 获取列表指定下标的值
		System.out.println("获取下标为2的元素："+ shardedJedis.lindex("list1", 2));
		
	}
	
	/**
	 * 对set的操作
	 * */
	private void setOperate() {
		System.out.println("======================set=========================="); 
		System.out.println("=============增=============");
		System.out.println("向set1集合中加入元素element001："+ jedis.sadd("set1", "element001"));
		System.out.println("向set1集合中加入元素element002："+ jedis.sadd("set1", "element002"));
		System.out.println("向set1集合中加入元素element003："+ jedis.sadd("set1", "element003"));
		System.out.println("向set1集合中加入元素element004："+ jedis.sadd("set1", "element004"));
		System.out.println("查看set1集合中的所有元素:"+ jedis.smembers("set1"));
		
		System.out.println("=============删=============");
		System.out.println("集合set1中删除元素element003："+ jedis.srem("set1", "element004"));
		System.out.println("查看set1集合中的所有元素:"+ jedis.smembers("set1"));
		System.out.println("set1集合中任意位置的元素出栈："+ jedis.spop("set1"));
		System.out.println("查看set1集合中的所有元素:"+ jedis.smembers("set1"));
		System.out.println("=============改=============");
		
		System.out.println("=============查=============");
		System.out.println("判断element001是否在集合set1中："+ jedis.sismember("set1", "element001"));
		System.out.println("循环查询获取set1中的每个元素：");
		Set<String> set = jedis.smembers("set1");
		for (String string : set) {
			System.out.println(string);
		}
		
		System.out.println("=============集合运算=============");
		System.out.println("set2中添加元素element001："+ jedis.sadd("set2", "element001")); 
		System.out.println("set2中添加元素element002："+ jedis.sadd("set2", "element002"));
		System.out.println("set2中添加元素element003："+ jedis.sadd("set2", "element003"));
		System.out.println("set2中添加元素element004："+ jedis.sadd("set2", "element004"));
		System.out.println("set2中添加元素element005："+ jedis.sadd("set2", "element005"));
		System.out.println("set2中添加元素element006："+ jedis.sadd("set2", "element006"));
		System.out.println("查看set1集合中的所有元素:"+ jedis.smembers("set2"));
		System.out.println("查看set2集合中的所有元素:"+ jedis.smembers("set2"));
		System.out.println("sets1和sets2交集："+ jedis.sinter("set1","set2"));
		System.out.println("sets1和sets2并集："+ jedis.sunion("set1","set2"));
		System.out.println("sets1和sets2差集："+ jedis.sdiff("set1","set2"));//差集：set1中有，set2中没有的元素
		
	}
	
	private void sortedsetOperate() {
		System.out.println("======================zset=========================="); 
		System.out.println("=============增=============");
		System.out.println("zset1中添加元素element001："+ shardedJedis.zadd("zset1", 7.0, "element001"));
		System.out.println("zset1中添加元素element002："+ shardedJedis.zadd("zset1", 4.0, "element002"));
		System.out.println("zset1中添加元素element003："+ shardedJedis.zadd("zset1", 3.0, "element003"));
		System.out.println("zset1中添加元素element004："+ shardedJedis.zadd("zset1", 2.0, "element004"));
		System.out.println("zset1中添加元素element005："+ shardedJedis.zadd("zset1", 1.0, "element005"));
		System.out.println("zset1集合中的所有元素："+ shardedJedis.zrange("zset1", 0, -1));//按照权重值排序  这里的权重：下标
		
		System.out.println("=============删=============");
		System.out.println("zset1中删除元素element002："+ shardedJedis.zrem("zset1", "element004"));
		System.out.println("zset1集合中的所有元素："+ shardedJedis.zrange("zset1", 0, -1));
		
		System.out.println("=============改=============");
		
		System.out.println("=============查=============");
		System.out.println("统计zset集合中的元素中个数："+ shardedJedis.zcard("zset1"));
		System.out.println("统计zset集合中权重某个范围内（1.0——5.0），元素的个数："+ shardedJedis.zcount("zset1", 1.0, 4.0));
		System.out.println("查看zset集合中element004的权重："+ shardedJedis.zscore("zset1", "element004"));
		System.out.println("查看下标1到2范围内的元素值："+ shardedJedis.zrange("zset", 1, 2));
		
	}
	
	private void hashOperate() {
		System.out.println("=============增=============");
		System.out.println("hashs中添加key001和value001键值对："+ shardedJedis.hset("hash1", "key001", "value001"));
		System.out.println("hashs中添加key001和value001键值对："+ shardedJedis.hset("hash1", "key002", "value002"));
		System.out.println("hashs中添加key003和value003键值对："+ shardedJedis.hset("hash1", "key003", "value003"));
		System.out.println("新增key004和4的整型键值对："+ shardedJedis.hincrBy("hash1", "key004", 41));
		System.out.println("hashs中的所有值："+ shardedJedis.hvals("hash1"));
		
		System.out.println("=============删=============");
		System.out.println("hashs中删除key002键值对："+ shardedJedis.hdel("hash1", "key002"));
		System.err.println("hashs中的所有值："+ shardedJedis.hvals("hash1"));
		
		System.out.println("=============改=============");
		System.out.println("key004整型键值的值增加100："+ shardedJedis.hincrBy("hash1", "key004", 100L));
		System.err.println("hashs中的所有值："+ shardedJedis.hvals("hash1"));
		
		System.out.println("=============查=============");
		System.out.println("判断key003是否存在："+ shardedJedis.hexists("hash1", "key003"));
		System.out.println("获取key004对应的值："+ shardedJedis.hget("hash1", "key004"));
		System.out.println("批量获取key001和key003对应的值："+ shardedJedis.hmget("hash1", "key001","key002"));
		System.out.println("获取hashs中所有的key："+ shardedJedis.hkeys("hash1"));
		System.out.println("获取hashs中所有的value："+ shardedJedis.hvals("hash1"));
		
	}
}
