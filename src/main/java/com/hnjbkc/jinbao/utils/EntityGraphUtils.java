package com.hnjbkc.jinbao.utils;

import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoImpl;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import lombok.Data;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.OneToOne;
import javax.persistence.Subgraph;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author xudaz
 */
@SuppressWarnings("Duplicates")
public class EntityGraphUtils {

    private static String packageName = "com.hnjbkc.jinbao";

    @Data
    public class EntityGraphUtilsInfo {

        public EntityGraphUtilsInfo() {
        }

        public EntityGraphUtilsInfo(String fieldPath, MyGraphIgnoreInfoType fetchType, String[] fieldList) {
            this.fieldPath = fieldPath;
            this.fetchType = fetchType;
            this.fieldList = fieldList;
        }

        public EntityGraphUtilsInfo(String fieldPath,  String[] fieldList) {
            this.fieldPath = fieldPath;
            this.fieldList = fieldList;
        }
        public EntityGraphUtilsInfo(String fieldPath,  MyGraphIgnoreInfoType fetchType ) {
            this.fieldPath = fieldPath;
            this.fetchType = fetchType;
        }
        public EntityGraphUtilsInfo(String fieldPath ) {
            this.fieldPath = fieldPath;
        }

        private String fieldPath ;

        /**
         * 实体图排除的类型
         */
        private  MyGraphIgnoreInfoType fetchType  = MyGraphIgnoreInfoType.WHITE_LIST ;

        private String[] fieldList = new String[0];


    }

    private static Map<String , EntityGraphUtilsInfo > entityGraphUtilsInfo2Map(  EntityGraphUtilsInfo[] entityGraphUtilsInfo ) {
        Map<String , EntityGraphUtilsInfo > entityGraphUtilsInfoMap = new HashMap<>(entityGraphUtilsInfo.length);
        for (EntityGraphUtilsInfo graphUtilsInfo : entityGraphUtilsInfo) {
            entityGraphUtilsInfoMap.put( graphUtilsInfo.getFieldPath() , graphUtilsInfo );
        }
        return entityGraphUtilsInfoMap;
    }


    @SuppressWarnings("unchecked")
    public static EntityGraph createGraphByClassAndEntityManager(Class aClass , EntityManager entityManager ,  EntityGraphUtilsInfo[] entityGraphUtilsInfo ) {
        EntityGraph entityGraph = entityManager.createEntityGraph(aClass);
        Field[] declaredFields = aClass.getDeclaredFields();

        List<Integer> integers = new ArrayList<>();
        integers.add(0);

        Map<String, EntityGraphUtilsInfo> entityGraphUtilsInfoMap = entityGraphUtilsInfo2Map(entityGraphUtilsInfo);

        for (Field declaredField : declaredFields) {
            declaredField.setAccessible( true );
            Class<?> type = declaredField.getType();

            if ( type == List.class || type == Set.class ) {
                // 获得field的泛型类型
                Type gType = declaredField.getGenericType();
                // 就把它转换成ParameterizedType对象
                ParameterizedType pType = (ParameterizedType)gType;
                // 获得泛型类型的泛型参数（实际类型参数)
                Type[] tArgs = pType.getActualTypeArguments();
                try {
                    type = Class.forName(tArgs[0].getTypeName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            //如果是级联类
            if ( type.getName().contains( packageName ) ) {
                //如果需要自定义
                EntityGraphUtilsInfo myGraphIgnoreInfo = null ;
                if ( entityGraphUtilsInfoMap.containsKey( declaredField.getName() ) ) {
                    myGraphIgnoreInfo = entityGraphUtilsInfoMap.get(declaredField.getName());
                    //如果排除自己
                    if ( myGraphIgnoreInfo.getFetchType() == MyGraphIgnoreInfoType.ONLY_SELF  ) {
                        //如果是一对一 放行 并修改 MyGraphIgnoreInfoType
                        OneToOne oneToOne = declaredField.getAnnotation(OneToOne.class);
                        if ( oneToOne == null ||   "".equals( oneToOne.mappedBy() ) ) {
                            continue;
                        } else {
                            myGraphIgnoreInfo.setFetchType( MyGraphIgnoreInfoType.WHITE_LIST );
                        }
                    }
                    //如果是白名单就放行
                    if ( myGraphIgnoreInfo.getFetchType() == MyGraphIgnoreInfoType.WHITE_LIST  ) {
                        entityGraph.addAttributeNodes( declaredField.getName() );
                        integers.set(0 , integers.get( 0 ) + 1 );
                        //添加子图
                        //已包含的父子图关系
                        Map<Class , String > map = new HashMap<>(5);
                        map.put( aClass , declaredField.getName()  );
                        System.out.print( " 前数量 " +  integers.get(0) );
                        addSubGraph4EntityGraph( integers ,  entityGraph.addSubgraph(declaredField.getName(), type) , map , entityGraphUtilsInfoMap , myGraphIgnoreInfo , declaredField.getName()  )   ;
                        System.out.print( " 爸爸 " + aClass.getSimpleName() );
                        System.out.print( " 儿子 " + declaredField.getName() );
                        System.out.println( "数量 " + integers.get(0) );
                        continue;
                    }
                }
                //如果是1对1 放行 并且不是外键维护端
                OneToOne oneToOne = declaredField.getAnnotation(OneToOne.class);
                if ( oneToOne != null &&  ! "".equals( oneToOne.mappedBy() ) ) {
                    myGraphIgnoreInfo = new EntityGraphUtils().new EntityGraphUtilsInfo(declaredField.getName() );
                    entityGraph.addAttributeNodes( declaredField.getName() );
                    integers.set(0 , integers.get( 0 ) + 1 );
                    //添加子图
                    //已包含的父子图关系
                    Map<Class , String > map = new HashMap<>(5);
                    map.put( aClass , declaredField.getName()  );
                    System.out.print( " 前数量 " +  integers.get(0) );
                    addSubGraph4EntityGraph( integers ,  entityGraph.addSubgraph(declaredField.getName(), type) , map , entityGraphUtilsInfoMap , myGraphIgnoreInfo , declaredField.getName()  )   ;
                    System.out.print( " 爸爸 " + aClass.getSimpleName() );
                    System.out.print( " 儿子 " + declaredField.getName() );
                    System.out.println( "数量 " + integers.get(0) );
                }

            }
        }
        return entityGraph ;
    }

    /**
     * 为子图添加属性或者级联子图
     * @param integers 计数
     * @param subgraph 子图
     * @param added 已经添加过的类
     */
    private static void addSubGraph4EntityGraph(List<Integer> integers , Subgraph subgraph , Map<Class , String > added , Map<String, EntityGraphUtilsInfo> myGraphIgnoreInfo4Bean , EntityGraphUtilsInfo myGraphIgnoreInfo1 , String fieldPath   ) {
        Class classType = subgraph.getClassType();
        if ( added.containsKey( classType ) ) {
            return;
        }
        Field[] declaredFields = classType.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible( true );
            Class<?> type = declaredField.getType();
            boolean containsKey = added.containsKey(type);
            //如果是级联类
            if ( type.getName().contains( packageName ) ) {
                //判断本级的排除
                //本级路径
                String newFieldPath = fieldPath + "." + declaredField.getName();
                //如果需要自定义
                EntityGraphUtilsInfo myGraphIgnoreInfo = null;
                if ( myGraphIgnoreInfo4Bean.containsKey( newFieldPath ) ) {
                    myGraphIgnoreInfo = myGraphIgnoreInfo4Bean.get( newFieldPath );
                    //如果排除自己
                    if ( myGraphIgnoreInfo.getFetchType() == MyGraphIgnoreInfoType.ONLY_SELF  ) {
                        //如果是一对一 放行 并修改 MyGraphIgnoreInfoType
                        OneToOne oneToOne = declaredField.getAnnotation(OneToOne.class);
                        if ( oneToOne == null ||   "".equals( oneToOne.mappedBy() ) ) {
                            continue;
                        } else {
                            myGraphIgnoreInfo.setFetchType( MyGraphIgnoreInfoType.WHITE_LIST );
                        }
                    }
                }
                //是否有来自上级的排除
                if ( myGraphIgnoreInfo1 != null ) {
                    //如果是排除所有 那么放行例外
                    if ( myGraphIgnoreInfo1.getFetchType() == MyGraphIgnoreInfoType.WHITE_LIST) {
                        boolean ignore = true;
                        for (String other : myGraphIgnoreInfo1.getFieldList() ) {
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
                                if ( myGraphIgnoreInfo == null  ) {
                                    myGraphIgnoreInfo = new EntityGraphUtils().new EntityGraphUtilsInfo(declaredField.getName());
                                }
                                myGraphIgnoreInfo.setFetchType( MyGraphIgnoreInfoType.WHITE_LIST );
                            }
                        }
                    }
                    //如果是放行所有 那么排除例外
                    if ( myGraphIgnoreInfo1.getFetchType() == MyGraphIgnoreInfoType.BLACK_LIST) {
                        boolean ignore = false;
                        for (String other : myGraphIgnoreInfo1.getFieldList()) {
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
                                if ( myGraphIgnoreInfo == null  ) {
                                    myGraphIgnoreInfo = new EntityGraphUtils().new EntityGraphUtilsInfo(declaredField.getName());
                                }
                                myGraphIgnoreInfo.setFetchType( MyGraphIgnoreInfoType.WHITE_LIST );
                            }
                        }
                    }
                }


                Map<Class , String > map = new HashMap<>(5);
                map.putAll( added );
                map.put( classType , declaredField.getName() );
                integers.set(0 , integers.get( 0 ) + 1 );
                //添加子图
                addSubGraph4EntityGraph( integers ,  subgraph.addSubgraph(declaredField.getName(), type) , map , myGraphIgnoreInfo4Bean ,  myGraphIgnoreInfo ,  newFieldPath   );
            }
        }

    }

}
