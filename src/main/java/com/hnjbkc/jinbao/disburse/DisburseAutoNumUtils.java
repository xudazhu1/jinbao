package com.hnjbkc.jinbao.disburse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * 自动生产财务日常 付款申请单的编号
 * @author xudaz
 */
@Component
public  class DisburseAutoNumUtils {

    private RedisTemplate redisTemplate;

    @Autowired
    public void setStringRedisTemplate( RedisTemplate redisTemplate ) {
        this.redisTemplate = redisTemplate;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public void setRedisTemplate(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @SuppressWarnings("unchecked")
    public synchronized Integer getNum(String type ) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Integer s = (Integer)valueOperations.get(type);
        //拿不到 生成默认值 并且赋值给s
        if ( s == null ) {
            Query nativeQuery = entityManager.createNativeQuery("SELECT MAX( SUBSTRING( disburse_num ,  8 , 5 ) ) FROM disburse disburse WHERE disburse_affiliation IN ( '财务日常' , '付款申请单' ) ");
            String s1 = (String) nativeQuery.getSingleResult();
            Integer singleResult = Integer.valueOf(s1);
            valueOperations.set("财务日常" , singleResult );
            valueOperations.set("付款申请单" , singleResult );
            s = singleResult;
        }
        valueOperations.set(type , s + 1 );
        return s;


    }





}
