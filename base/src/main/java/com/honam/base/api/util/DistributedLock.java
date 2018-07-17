package com.honam.base.api.util;

import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * 简单的分布式锁
 * @author honamxu
 */
public class DistributedLock {

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";//毫秒
    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 各默认时间, 如需自定义参数请使用方法lock(JedisPool jedisPool, String lockKey, String requestId, long expireTimeMs, long timeoutMs, long sleepMillis)
     */
    private static final long LOCK_TIMEOUT_MS = 2000;//获取锁最长等待时间 2秒
    private static final long PER_LOOP_WAIT_SLEEP_MS =  500;//轮询获取锁的时间间隔 0.5秒
    private static final int LOCK_CACHE_TIMEOUT_MS = 2 * 60 * 1000;//锁缓存超时时间, 120s, 应避免程序处理耗时比这个时间长


    private static void validParam(JedisPool jedisPool, String lockKey, String requestId, long expireTime) {
        if (null == jedisPool) {
            throw new IllegalArgumentException("jedisPool obj is null");
        }

        if (StringUtils.isBlank(lockKey)) {
            throw new IllegalArgumentException("lock key  is blank");
        }

        if (StringUtils.isBlank(requestId)) {
            throw new IllegalArgumentException("requestId is blank");
        }

        if (expireTime < 0) {
            throw new IllegalArgumentException("expireTime is not allowed less zero");
        }
    }

    /**
     * 尝试一次加锁
     * @param jedisPool
     * @param lockKey
     * @param requestId
     * @param expireTimeMs
     * @return
     * @throws Exception
     */
    private static boolean tryLockWithoutCheck(JedisPool jedisPool, String lockKey, String requestId, long expireTimeMs) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTimeMs);
            if (LOCK_SUCCESS.equals(result)) {
                return true;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
        return false;
    }

    /**
     * 尝试一次加锁
     * @param jedisPool
     * @param lockKey
     * @param requestId
     * @param expireTimeMs
     * @return
     * @throws Exception
     */
    public static boolean tryLock(JedisPool jedisPool, String lockKey, String requestId, long expireTimeMs) throws Exception {
        validParam(jedisPool, lockKey, requestId, expireTimeMs);
        return tryLockWithoutCheck(jedisPool, lockKey, requestId, expireTimeMs);
    }

    /**
     * 加锁操作, 默认等待时间, 轮询时间, 缓存超时时间
     * @param jedisPool
     * @param lockKey
     * @param requestId
     * @return
     */
    public static boolean lock(JedisPool jedisPool, String lockKey, String requestId) throws Exception {
        return lock(jedisPool, lockKey, requestId, LOCK_CACHE_TIMEOUT_MS, LOCK_TIMEOUT_MS, PER_LOOP_WAIT_SLEEP_MS);
    }

    /**
     * 加锁操作, 自定义等待时间
     * @param jedisPool
     * @param lockKey
     * @param requestId
     * @param timeout
     * @return
     * @throws Exception
     */
    public static boolean lock(JedisPool jedisPool, String lockKey, String requestId, long timeout) throws Exception {
        return lock(jedisPool, lockKey, requestId, LOCK_CACHE_TIMEOUT_MS, timeout, PER_LOOP_WAIT_SLEEP_MS);
    }

    /**
     * 加锁操作, 自定义 等待时间, 轮询时间, 缓存超时时间
     * @param jedisPool
     * @param lockKey
     * @param requestId
     * @param expireTimeMs  锁缓存超时时间 毫秒
     * @param timeoutMs  加锁等待时间 ms
     * @param sleepMillis  每次轮询等待时间
     * @return
     * @throws Exception
     */
    public static boolean lock(JedisPool jedisPool, String lockKey, String requestId, long expireTimeMs, long timeoutMs, long sleepMillis) throws Exception {
        validParam(jedisPool, lockKey, requestId, expireTimeMs);
        long msTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - msTime < timeoutMs){
            if (tryLockWithoutCheck(jedisPool, lockKey, requestId, expireTimeMs)) {
                return true;
            }
            Thread.sleep(sleepMillis);
        }
        return false;

    }

    /**
     * 解锁
     * @param jedisPool
     * @param lockKey
     * @param requestId
     * @return
     * @throws Exception
     */
    public static boolean unLock(JedisPool jedisPool, String lockKey, String requestId) throws Exception {
        validParam(jedisPool, lockKey, requestId, 1);
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Object result = jedis.eval(script, Collections.singletonList(lockKey),
                    Collections.singletonList(requestId));
            if (RELEASE_SUCCESS.equals(result)) {
                return true;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
        return false;
    }


    /**
     * 加锁和解锁业务
     * @param jedisPool
     */
    private static void doSth(JedisPool jedisPool) {
        Random random = new Random();
        try{
            if(lock(jedisPool, "test-key", "requestId", 10000)){//上锁
                System.out.println(Thread.currentThread().getId() + "--获取锁成功");
                Thread.sleep(1000 + random.nextInt(500));//模拟处理业务
            }else{
                System.out.println(Thread.currentThread().getId() + "--获取锁失败");
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try {
                System.out.println(Thread.currentThread().getId() + "--结束");
                unLock(jedisPool, "test-key", "requestId");//解锁
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 500个线程并发执行
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        //设置jedispool
        JedisPoolConfig config = new JedisPoolConfig();
        //配置最大jedis实例数
        config.setMaxTotal(1000);
        //配置资源池最大闲置数
        config.setMaxIdle(200);
        //等待可用连接的最大时间
        config.setMaxWaitMillis(10000);
        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的
        config.setTestOnBorrow(true);
        final JedisPool jedisPool = new JedisPool(config, "10.100.112.111",6379, 6000, "123456", 1);

        int threadCount = 500;//500条线程执行
        final CountDownLatch startCount = new CountDownLatch(1);
        Thread[] threads = new Thread[threadCount]; //起500个线程
        for(int i= 0;i < threadCount;i++){
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    try {
                        System.out.println("挂起线程:"+Thread.currentThread().getId());
                        startCount.await();//等待在一个信号量上，挂起
                        doSth(jedisPool);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            threads[i].start();
        }
        Thread.sleep(5000);
        startCount.countDown();//500个线程同时执行
    }



}
