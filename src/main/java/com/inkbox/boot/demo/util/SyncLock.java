package com.inkbox.boot.demo.util;

public interface SyncLock {
    /**
     * 加锁
     *
     * @param key   对应的key，需要用于解锁
     * @param value value，防止解锁时误解
     * @return 枷锁成功返回true
     */
    boolean lock(String key, String value);

    /**
     * 加锁
     *
     * @param key 对应的key
     * @return 返回value，可用于解锁
     */
    String lock(String key);

    /**
     * 加锁
     *
     * @param key  对应的key
     * @param time 持有锁的时间，单位毫秒，超时则锁被释放；具体值应该和业务强相关
     * @return
     */
    String lock(String key, long time);

    /**
     * 获取锁，一般是实际使用的方法，time参数需要考虑对应业务的执行时间
     *
     * @param key   对应的key，需要用于解锁
     * @param value value，防止解锁时误解
     * @param time  超时，单位毫秒
     * @return
     */
    boolean lock(String key, String value, long time);

    /**
     * 解锁
     *
     * @param key
     * @param value
     * @return
     */
    boolean unlock(String key, String value);
}
