package com.itheima.reggie.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的ID值到ThreadLocal中
     * 该方法用于在当前线程上下文中存储一个Long类型的ID值，确保线程间数据隔离
     * @param id 要设置的ID值，用于标识当前线程的上下文
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }


    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}