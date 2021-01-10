package cn.renlm.plugins;

import org.junit.Test;

import cn.hutool.db.nosql.redis.RedisDS;
import redis.clients.jedis.Jedis;

/**
 * 爬虫
 * 
 * @author Renlm
 *
 */
public class MyCrawlerTest {

	@Test
	public void run() {
		Jedis jedis = RedisDS.create().getJedis();
		Long i = jedis.hset("jedis", "test", "false");
		System.out.println(i);
		System.out.println(jedis.hget("jedis", "test"));
	}
}