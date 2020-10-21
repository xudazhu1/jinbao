package com.hnjbkc.jinbao.hqldao;

import com.hnjbkc.jinbao.hqldao.annotations.HasOneToManyList;
import com.hnjbkc.jinbao.hqldao.annotations.OneToManyListInfo;
import com.hnjbkc.jinbao.utils.MyBeanUtils;
import org.hibernate.engine.jdbc.SerializableClobProxy;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 解决jpa|hibernate N+1 的问题 (1+1 IN 查询)
 * @author xudaz
 * @date 2019/4/1
 */
@Component
public class ManyAndOneToOneAndOne {

    /**
     * 注入实体管理器,执行持久化操作
     */
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    /**
     * /@SuppressWarnings("unchecked") 未经检查的安全转换
     * @param list 要整理beanlist的集合
     * @param <T> 泛型
     * @return 整理完的集合
     * @throws Exception NoSuchMethodException , InvocationTargetException , IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getCascades(List<T> list) throws Exception {
        if (list == null || list.size() == 0) {
            return new ArrayList<>();
        }
        //一那边的类的class
        Class<?> aClass = list.get(0).getClass();
        String simpleName = aClass.getSimpleName();
        String propName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);


        HasOneToManyList[] annotationsByType = aClass.getAnnotationsByType(HasOneToManyList.class);
        if (annotationsByType.length == 0) {
            return list;
        }
        OneToManyListInfo[] oneToManyListInfos = annotationsByType[0].hasClasses();
        for (OneToManyListInfo oneToManyListInfo : oneToManyListInfos) {
            //获取mappedBy
            Field declaredField = aClass.getDeclaredField(oneToManyListInfo.propertyName());
            declaredField.setAccessible( true );
            OneToMany oneToMany = declaredField.getAnnotation(OneToMany.class);
            String mappedBy = oneToMany.mappedBy();

            //获取Beans返回的list
            Method method = aClass.getMethod("get" + oneToManyListInfo.propertyName().substring(0, 1).toUpperCase() + oneToManyListInfo.propertyName().substring(1));
            Method setMethod = aClass.getMethod("set" + oneToManyListInfo.propertyName().substring(0, 1).toUpperCase() + oneToManyListInfo.propertyName().substring(1), List.class);
            if (method == null) {
                continue;
            }
            for (Object t1 : list) {
                setMethod.invoke(t1, new ArrayList<>());
            }
            String simpleNameTemp = oneToManyListInfo.propertyClass().getSimpleName();
            String hqlByIn = "from " + simpleNameTemp + " where " + mappedBy + " in ( :propList )";
            System.out.println("In HQL ==> " + hqlByIn);
            TypedQuery<Object> query = entityManager.createQuery(hqlByIn, Object.class);
            query.setParameter("propList", list);

            List<EntityGraph<? super T>> entityGraphs = entityManager.getEntityGraphs(oneToManyListInfo.propertyClass());
            if (entityGraphs != null && entityGraphs.size() > 0) {
                query.setHint("javax.persistence.loadgraph", entityGraphs.get(0));
            }

            List<?> resultList = query.getResultList();
            getCascades(resultList);
            loop:
            for (Object  o : resultList) {
//                //过滤hibernate代理对象
//                if (o instanceof HibernateProxy) {
//                    o =  ((HibernateProxy) o).getHibernateLazyInitializer()
//                            .getImplementation();
//                }

                //遍历的每个新查出来的list对象
                Field declaredField1 = null;
                try {
                    declaredField1 = o.getClass().getDeclaredField(mappedBy);
                } catch ( Exception e) {
                    System.out.println(o.getClass() );
                }
                if ( declaredField == null ) {
                    continue ;
                }
                declaredField1.setAccessible( true );
                Object newObject = declaredField1.get(o);
                for (Object oldObject : list) {
                    //如果双方主键匹配
                    Object newPrimaryKey = MyBeanUtils.getPrimaryKey(newObject);
                    if ( newPrimaryKey == null ) {
                        continue ;
                    }
                    if ( newPrimaryKey.equals( MyBeanUtils.getPrimaryKey( oldObject ) ) ) {
                        List invoke2 = (List) method.invoke(oldObject);
                        invoke2.add(o);
                        continue loop;
                    }
                }
            }
        }
        return list;

    }

}
