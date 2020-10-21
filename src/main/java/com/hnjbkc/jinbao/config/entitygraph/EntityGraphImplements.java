package com.hnjbkc.jinbao.config.entitygraph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.stereotype.Component;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.OneToOne;
import javax.persistence.Subgraph;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * @author xudaz
 */
@SuppressWarnings("Duplicates")
@Component
public class EntityGraphImplements {

    private static EntityManager entityManager;

    private static String packageName = "com.hnjbkc.jinbao";

    private static Boolean createdGraph = false;

    public static List<Class> beans = new ArrayList<>();


    public static void  createGraphOnLoad(EntityManager entityManager ) {
        if ( createdGraph ) {
            return;
        }
        createdGraph = true;
        EntityGraphImplements.entityManager = entityManager;

        Metamodel metamodel = entityManager.getEntityManagerFactory().getMetamodel();
        Set<EntityType<?>> entities = metamodel.getEntities();
        for (EntityType<?> entity : entities) {
            Class<?> bindableJavaType = entity.getBindableJavaType();
            beans.add(bindableJavaType);
        }

        //先拿到所有Bean
        List<Class> classes = beans;
        //然后为每个bean创建实体图
        for (Class aClass : classes) {
            createGraphByClassAndEntityManager( aClass );
        }
    }

    @SuppressWarnings("unchecked")
    private static void createGraphByClassAndEntityManager(Class aClass) {
        EntityGraph entityGraph = entityManager.createEntityGraph(aClass);
        Field[] declaredFields = aClass.getDeclaredFields();

        List<Integer> integers = new ArrayList<>();
        integers.add(0);

        //拿到所有注解自定义项
        Map<String, MyGraphIgnoreInfo> myGraphIgnoreInfo4Bean = getMyGraphIgnoreInfo4Bean(aClass);

        for (Field declaredField : declaredFields) {
            declaredField.setAccessible( true );
            Class<?> type = declaredField.getType();
            //如果是级联类
            if ( type.getName().contains( packageName ) ) {
                //如果需要自定义
                MyGraphIgnoreInfo myGraphIgnoreInfo = null;
                if ( myGraphIgnoreInfo4Bean.containsKey( declaredField.getName() ) ) {
                    myGraphIgnoreInfo = myGraphIgnoreInfo4Bean.get(declaredField.getName());
                    //如果排除自己
                    if ( myGraphIgnoreInfo.fetchType() == MyGraphIgnoreInfoType.ONLY_SELF  ) {
                        //如果是一对一 放行 并修改 MyGraphIgnoreInfoType
                        OneToOne oneToOne = declaredField.getAnnotation(OneToOne.class);
                        if ( oneToOne == null ||   "".equals( oneToOne.mappedBy() ) ) {
                            continue;
                        } else {
                            try {
                                InvocationHandler h = Proxy.getInvocationHandler(myGraphIgnoreInfo);
                                Field memberValues = h.getClass().getDeclaredField("memberValues");
                                memberValues.setAccessible( true );
                                Map map  = (Map) memberValues.get( h );
                                map.put( "fetchType" , MyGraphIgnoreInfoType.WHITE_LIST );
                            } catch ( Exception e ) {
                                System.out.println("修改MyGraphIgnoreInfoType失败");
                            }
                        }
                    }
                }
                entityGraph.addAttributeNodes( declaredField.getName() );
                integers.set(0 , integers.get( 0 ) + 1 );
                //添加子图
                //已包含的父子图关系
                Map<Class , String > map = new HashMap<>(5);
                map.put( aClass , declaredField.getName()  );
                System.out.print( " 前数量 " +  integers.get(0) );
                addSubGraph4EntityGraph( integers ,  entityGraph.addSubgraph(declaredField.getName(), type) , map , myGraphIgnoreInfo4Bean , myGraphIgnoreInfo , declaredField.getName() ,  getJsonIgnore(declaredField) )   ;
                System.out.print( " 爸爸 " + aClass.getSimpleName() );
                System.out.print( " 儿子 " + declaredField.getName() );
                System.out.println( "数量 " + integers.get(0) );
            }
        }
        entityManager.getEntityManagerFactory().addNamedEntityGraph( aClass.getSimpleName() , entityGraph );
    }

    /**
     * 为子图添加属性或者级联子图
     * @param integers 计数
     * @param subgraph 子图
     * @param added 已经添加过的类
     */
    @SuppressWarnings("unchecked")
    private static void addSubGraph4EntityGraph(List<Integer> integers , Subgraph subgraph , Map<Class , String > added , Map<String, MyGraphIgnoreInfo> myGraphIgnoreInfo4Bean , MyGraphIgnoreInfo myGraphIgnoreInfo1 , String fieldPath , String... jsonIgnoreProperties ) {
        Class classType = subgraph.getClassType();
        if ( added.containsKey( classType ) ) {
            return;
        }
        Field[] declaredFields = classType.getDeclaredFields();
        loop :
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible( true );
            Class<?> type = declaredField.getType();
            for (String jsonIgnoreProperty : jsonIgnoreProperties) {
                if ( jsonIgnoreProperty.equals( declaredField.getName() ) ) {
                    continue loop;
                }
            }
            boolean containsKey = added.containsKey(type);

            //如果是级联类
            if ( type.getName().contains( packageName ) ) {


                //判断本级的排除
                //本级路径
                String newFieldPath = fieldPath + "." + declaredField.getName();
                //如果需要自定义
                MyGraphIgnoreInfo myGraphIgnoreInfo = null;
                if ( myGraphIgnoreInfo4Bean.containsKey( newFieldPath ) ) {
                    myGraphIgnoreInfo = myGraphIgnoreInfo4Bean.get( newFieldPath );
                    //如果排除自己
                    if ( myGraphIgnoreInfo.fetchType() == MyGraphIgnoreInfoType.ONLY_SELF  ) {
                        //如果是一对一 放行 并修改 MyGraphIgnoreInfoType
                        OneToOne oneToOne = declaredField.getAnnotation(OneToOne.class);
                        if ( oneToOne == null ||   "".equals( oneToOne.mappedBy() ) ) {
                            continue;
                        } else {
                            try {
                                InvocationHandler h = Proxy.getInvocationHandler(myGraphIgnoreInfo);
                                Field memberValues = h.getClass().getDeclaredField("memberValues");
                                memberValues.setAccessible( true );
                                Map map  = (Map) memberValues.get( h );
                                map.put( "fetchType" , MyGraphIgnoreInfoType.WHITE_LIST );
                            } catch ( Exception e ) {
                                System.out.println("修改MyGraphIgnoreInfoType失败");
                            }
                        }
                    }
                }

                //是否有来自上级的排除
                if ( myGraphIgnoreInfo1 != null ) {
                    //如果是排除所有 那么放行例外
                    if ( myGraphIgnoreInfo1.fetchType() == MyGraphIgnoreInfoType.WHITE_LIST) {
                        boolean ignore = true;
                        for (String other : myGraphIgnoreInfo1.fieldList()) {
                            if ( other.equals( declaredField.getName() ) ) {
                                ignore = false;
                                break ;
                            }
                        }
                        if ( ignore ) {
                            //如果是一对一 放行 并修改 MyGraphIgnoreInfoType
                            OneToOne oneToOne = declaredField.getAnnotation(OneToOne.class);
                            if ( oneToOne == null ||   "".equals( oneToOne.mappedBy() ) ) {
                                continue;
                            } else {
                                try {
                                    if ( myGraphIgnoreInfo == null ) {
                                        myGraphIgnoreInfo = new MyGraphIgnoreInfoImpl();
                                    } else {
                                        InvocationHandler h = Proxy.getInvocationHandler(myGraphIgnoreInfo );
                                        Field memberValues = h.getClass().getDeclaredField("memberValues");
                                        memberValues.setAccessible( true );
                                        Map map  = (Map) memberValues.get( h );
                                        map.put( "fetchType" , MyGraphIgnoreInfoType.WHITE_LIST );
                                    }
                                } catch ( Exception e ) {
                                    System.out.println("修改MyGraphIgnoreInfoType失败");
                                    e.printStackTrace() ;
                                }
                            }
                        }
                    }
                    //如果是放行所有 那么排除例外
                    if ( myGraphIgnoreInfo1.fetchType() == MyGraphIgnoreInfoType.BLACK_LIST) {
                        boolean ignore = false;
                        for (String other : myGraphIgnoreInfo1.fieldList()) {
                            if ( other.equals( declaredField.getName() ) ) {
                                ignore = true;
                                break ;
                            }
                        }
                        if ( ignore ) {
                            //如果是一对一 放行 并修改 MyGraphIgnoreInfoType
                            OneToOne oneToOne = declaredField.getAnnotation(OneToOne.class);
                            if ( oneToOne == null ||   "".equals( oneToOne.mappedBy() ) ) {
                                continue;
                            } else {
                                try {
                                    if ( myGraphIgnoreInfo == null ) {
                                        myGraphIgnoreInfo = MyGraphIgnoreInfo.class.newInstance();
                                    }
                                    InvocationHandler h = Proxy.getInvocationHandler(myGraphIgnoreInfo );
                                    Field memberValues = h.getClass().getDeclaredField("memberValues");
                                    memberValues.setAccessible( true );
                                    Map map  = (Map) memberValues.get( h );
                                    map.put( "fetchType" , MyGraphIgnoreInfoType.WHITE_LIST );
                                } catch ( Exception e ) {
                                    System.out.println("修改MyGraphIgnoreInfoType失败");
                                }
                            }
                        }
                    }
                }


                Map<Class , String > map = new HashMap<>(5);
                map.putAll( added );
                map.put( classType , declaredField.getName() );
                integers.set(0 , integers.get( 0 ) + 1 );
                //添加子图
                addSubGraph4EntityGraph( integers ,  subgraph.addSubgraph(declaredField.getName(), type) , map , myGraphIgnoreInfo4Bean ,  myGraphIgnoreInfo ,  newFieldPath ,  getJsonIgnore( declaredField ) );
            }
        }

    }


    private static Map<String , MyGraphIgnoreInfo > getMyGraphIgnoreInfo4Bean(Class bean){
        Map<String , MyGraphIgnoreInfo > graphIgnoreInfoMap = new HashMap<>(5);
        if ( bean == null ){
            return graphIgnoreInfoMap;
        }
        MyGraphIgnore myGraphIgnore = (MyGraphIgnore) bean.getDeclaredAnnotation(MyGraphIgnore.class);
        if ( myGraphIgnore == null ) {
            return graphIgnoreInfoMap;
        }
        MyGraphIgnoreInfo[] myGraphIgnoreInfos = myGraphIgnore.ignoreFields();
        for (MyGraphIgnoreInfo myGraphIgnoreInfo : myGraphIgnoreInfos) {
            graphIgnoreInfoMap.put(myGraphIgnoreInfo.fieldPath() ,  myGraphIgnoreInfo );
        }
        return graphIgnoreInfoMap;
    }


    private static String[] getJsonIgnore( @NotNull Field field ) {
        field.setAccessible( true );
        //获得json排除属性 不进行左连接
        JsonIgnoreProperties jsonIgnoreProperties = field.getAnnotation(JsonIgnoreProperties.class);
        String[] value = new String[0];
        if ( jsonIgnoreProperties != null ) {
            value = jsonIgnoreProperties.value();
        }
        return value;
    }

}
