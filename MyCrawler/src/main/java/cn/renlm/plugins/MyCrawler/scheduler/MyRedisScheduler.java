package cn.renlm.plugins.MyCrawler.scheduler;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

/**
 * 分布式Url调度
 * 
 * @author Renlm
 *
 */
public class MyRedisScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover, MyDuplicateVerify {

	protected JedisPool pool;

	private static final String QUEUE_PREFIX = "queue_";

	private static final String SET_PREFIX = "set_";

	private static final String ITEM_PREFIX = "item_";

	private static final String VERIFY_PREFIX = "verify_";

	public MyRedisScheduler(JedisPool pool) {
		this.pool = pool;
		setDuplicateRemover(this);
	}

	@Override
	public boolean verifyDuplicate(Request request, Task task) {
		Jedis jedis = pool.getResource();
		try {
			return jedis.sadd(getVerifyKey(task), request.getUrl()) == 0;
		} finally {
			jedis.close();
		}
	}

	@Override
	public void resetDuplicateCheck(Task task) {
		Jedis jedis = pool.getResource();
		try {
			jedis.del(getSetKey(task));
		} finally {
			jedis.close();
		}
	}

	@Override
	public boolean isDuplicate(Request request, Task task) {
		Jedis jedis = pool.getResource();
		try {
			return jedis.sadd(getSetKey(task), request.getUrl()) == 0;
		} finally {
			jedis.close();
		}
	}

	@Override
	protected void pushWhenNoDuplicate(Request request, Task task) {
		Jedis jedis = pool.getResource();
		try {
			jedis.rpush(getQueueKey(task), request.getUrl());
			if (checkForAdditionalInfo(request)) {
				String field = DigestUtils.sha1Hex(request.getUrl());
				String value = JSON.toJSONString(request);
				jedis.hset((ITEM_PREFIX + task.getUUID()), field, value);
			}
		} finally {
			jedis.close();
		}
	}

	private boolean checkForAdditionalInfo(Request request) {
		if (request == null) {
			return false;
		}

		if (!request.getHeaders().isEmpty() || !request.getCookies().isEmpty()) {
			return true;
		}

		if (StringUtils.isNotBlank(request.getCharset()) || StringUtils.isNotBlank(request.getMethod())) {
			return true;
		}

		if (request.isBinaryContent() || request.getRequestBody() != null) {
			return true;
		}

		if (request.getExtras() != null && !request.getExtras().isEmpty()) {
			return true;
		}
		if (request.getPriority() != 0L) {
			return true;
		}

		return false;
	}

	@Override
	public synchronized Request poll(Task task) {
		Jedis jedis = pool.getResource();
		try {
			String url = jedis.lpop(getQueueKey(task));
			if (url == null) {
				return null;
			}
			String key = ITEM_PREFIX + task.getUUID();
			String field = DigestUtils.sha1Hex(url);
			byte[] bytes = jedis.hget(key.getBytes(), field.getBytes());
			if (bytes != null) {
				Request o = JSON.parseObject(new String(bytes), Request.class);
				return o;
			}
			Request request = new Request(url);
			return request;
		} finally {
			jedis.close();
		}
	}

	protected static final String getSetKey(Task task) {
		return SET_PREFIX + task.getUUID();
	}

	protected static final String getQueueKey(Task task) {
		return QUEUE_PREFIX + task.getUUID();
	}

	protected static final String getItemKey(Task task) {
		return ITEM_PREFIX + task.getUUID();
	}

	protected static final String getVerifyKey(Task task) {
		return VERIFY_PREFIX + task.getUUID();
	}

	@Override
	public int getLeftRequestsCount(Task task) {
		Jedis jedis = pool.getResource();
		try {
			Long size = jedis.llen(getQueueKey(task));
			return size.intValue();
		} finally {
			jedis.close();
		}
	}

	@Override
	public int getTotalRequestsCount(Task task) {
		Jedis jedis = pool.getResource();
		try {
			Long size = jedis.scard(getSetKey(task));
			return size.intValue();
		} finally {
			jedis.close();
		}
	}
}