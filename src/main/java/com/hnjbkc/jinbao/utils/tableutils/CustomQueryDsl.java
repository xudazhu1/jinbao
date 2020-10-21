package com.hnjbkc.jinbao.utils.tableutils;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.AbstractJPAQuery;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.jpa.repository.support.Querydsl;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;

/**
 * @author xudaz
 */
public class CustomQueryDsl extends Querydsl {


    public CustomQueryDsl(EntityManager em, PathBuilder<?> builder) {
        super(em, builder);
    }


    @Override
    public AbstractJPAQuery<Object, JPAQuery<Object>> createQuery(EntityPath<?>... paths) {
        AbstractJPAQuery<Object, JPAQuery<Object>> jpaQuery = createQuery();
        for(EntityPath<?> path:paths) {
            jpaQuery = jpaQuery.leftJoin(path);
            Class<?> clazz = path.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for(Field field:fields) {
                Class<?> type = field.getType();
                if(EntityPath.class.isAssignableFrom(type)&&!type.equals(path.getClass())) {
                    try {
                        EntityPath<?> newPath = (EntityPath<?>)field.get(path);
                        jpaQuery = jpaQuery.leftJoin(newPath);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return jpaQuery;
    }


}
