package com.hnjbkc.jinbao.base;

/**
 * @author siliqiang
 * @date 2019.7.12
 */
public interface BaseService<T> {
    /**
     * 添加数据的方法
     *
     * @param t 前端传过来的对象
     * @return 返回的是一个布尔值
     */
    T add(T t);

    /**
     * 删除数据的方法
     *
     * @param id 需要删除数据的id
     * @return 返回的是一个布尔值
     */
    Boolean delete(Integer id);

    /**
     * 更新数据的方法
     *
     * @param t 更新数据的对象
     * @return 放回的是一个布尔值
     */
    T update(T t);

}
