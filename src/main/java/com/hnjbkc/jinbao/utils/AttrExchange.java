package com.hnjbkc.jinbao.utils;


import org.apache.poi.ss.formula.functions.T;

import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author linjianf
 */
public class AttrExchange {

    /**
     * 基本作用: 把第二个参数对象的属性值 赋值给第一个参数对象的属性值  两个对象类型一致
     * 应用场景: jpa 更新对象的时候 如果页面传送的属性是null 则会把null 属性update至DB (不应该页面不传 就设置为空)  update 之前 一般 先通过ID find
     * 解决问题:
     * 1. Integer, String 等基本类型的 属性交换
     * 2. 属性List场景 如果页面传送的List内元素没有Id 则 添加进saveList , 如果有Id 则 递归
     * 3. 属性Bean场景 如果DB 对象是空 则 页面的Bean 赋值给DBBean 如果 两个Bean Id 一致递归
     * @param saveBean saveBean
     * @param tempBean tempBean
     * @param notJudgeProp 自定义 交换的时候 如果 是这个bean 则 跳过 不 执行
     */
    public static <T> void onAttrExchange(T  saveBean, T tempBean,List<String> notJudgeProp) {
        exchange(saveBean,tempBean,notJudgeProp, null);
    }

    public static <T> void onAttrExchange(T  saveBean, T tempBean) {
        exchange(saveBean,tempBean,null, null);
    }

    public static <T> void onAttrExchange(T  saveBean, T tempBean,List<String> notJudgeProp, List<String> continueProp) {
        exchange(saveBean,tempBean,notJudgeProp, continueProp);
    }

    public static <T> void exchange(T saveBean, T tempBean, List<String> notJudgeProp, List<String> continueProp){
        //获取临时个对象的所有方法
        Method[] tempMethods = tempBean.getClass().getMethods();
        //遍历 第二个临时参数

        try {
            loop :
            for (Method tempMethod : tempMethods) {
                //获取 每个遍历的方法名
                String tempName = tempMethod.getName();
                String substring = tempName.substring(3);
                substring = substring.substring(0,1).toLowerCase()+substring.substring(1);

                //如果该方法是get方法
                if ("get".equals(tempName.substring(0,3)) && !"getClass".equals(tempName)){
                    boolean isContinue1 = false;
                    if(continueProp != null){
                        for (String s : continueProp) {
                            if((("get" + s.substring(0,1).toUpperCase() + s.substring(1)).equals(tempName))){
                                isContinue1 = true;
                            }
                        }
                    }
                    if(isContinue1){
                        continue loop;
                    }
                    Object invoke = tempMethod.invoke(tempBean);
                    //执行的方法如果是空 则退出此次循环 不赋值
                    //获取 属性类型
                    boolean isBean = false;
                    if(invoke != null){
                        Table annotation = invoke.getClass().getAnnotation(Table.class);
                        if(annotation != null){
                            isBean = true;
                        }
                    }
                    //如果 该属性是 集合
                    if( invoke instanceof  Collection ) {
                        Object tempList = tempMethod.invoke(tempBean);
                        String methodName = tempMethod.getName();
                        Method size = tempList.getClass().getMethod("size");
                        Object tempSize = size.invoke(tempList);
                        //如果集合是空 则退出当前循环
                        if((int)tempSize == 0){
                            continue;
                        }
                        //否则 遍历集合 获取里面 对象 的 id
                        for (Object tempObj : (List)tempList) {
                            Object tempId = MyBeanUtils.getPrimaryKey( tempObj );

                            Method saveMethod = saveBean.getClass().getMethod(methodName);
                            Object saveList = saveMethod.invoke(saveBean);
                            //如果id 是空 则添加进   saveList 的集合 也就是从数据库中查询出来的 List 做添加操作
                            Method add = saveList.getClass().getMethod("add",Object.class);
                            if(tempId == null){
                                add.invoke(saveList,tempObj);
                            }else {
                                //否则 遍历 saveList 集合 判断 里面对象 是否跟 临时对象 id 一致 如果一致做替换 所以 递归
                                boolean has = false;
                                for (Object saveObj : (List)saveList) {
                                    Object saveId = MyBeanUtils.getPrimaryKey( saveObj );
                                    if(tempId.equals(saveId)){
                                        if(notJudgeProp != null){
                                            onAttrExchange(saveObj, tempObj,notJudgeProp);
                                        }else {
                                            onAttrExchange(saveObj, tempObj);
                                        }
                                        has = true;
                                    }
                                }
                                if ( ! has ) {
                                    add.invoke(saveList,tempObj);
                                }

                            }

                        }
                        //如果 该属性类型是 Bean 不是基本类型
                    }else if(isBean){
                        Method subSaveMethod = saveBean.getClass().getMethod(tempName);
                        Object subSaveBean = subSaveMethod.invoke(saveBean);
                        Method subTempMethod = tempBean.getClass().getMethod(tempName);
                        Object subTempBean = subTempMethod.invoke(tempBean);
                        //判断 数据库中的Bean 是不是 空 如果是空 则 做 则直接把 临时对象 赋值 save对象
                        if(subSaveBean == null && subTempBean != null){
                            String tempMethodName = tempMethod.getName();
                            String setProperty = "s"+tempMethodName.substring(1,tempMethod.getName().length());
                            Method method = saveBean.getClass().getMethod(setProperty,
                                    subTempBean.getClass());
                            method.invoke(saveBean,subTempBean);
                            //如果 数据库Bean 不是空 获取Id 判断是不是一致 如果不一致 直接 临时对象 赋值 save对象
                        }else if(subSaveBean != null && subTempBean != null){
                            String tempSimpleName = subTempBean.getClass().getSimpleName();
                            String getId = "get" + tempSimpleName.substring(0, tempSimpleName.length() - 4) + "Id";
                            Method tempMethodId = subTempBean.getClass().getMethod(getId);
                            Object tempId = tempMethodId.invoke(subTempBean);
                            Method saveMethodId = subSaveBean.getClass().getMethod(getId);
                            Object saveId = saveMethodId.invoke(subSaveBean);
                            if( ! saveId.equals( tempId )){
                                String setProperty = "s"+tempMethod.getName().substring(1);
                                String getProperty = "g"+tempMethod.getName().substring(1);
                                Method tMethod = tempBean.getClass().getMethod(getProperty);
                                Method sMethod = saveBean.getClass().getMethod(setProperty, tempMethod.getReturnType() );
                                sMethod.invoke(saveBean,tMethod.invoke(tempBean));
                            }else {
                                //如果id 一致 则 递归
                                if(notJudgeProp != null){
                                    onAttrExchange(subSaveBean, subTempBean,notJudgeProp);
                                }else {
                                    onAttrExchange(subSaveBean, subTempBean);
                                }

                            }
                        }else {
                            // 其他情况 id 一致 递归 一遍
                            if(notJudgeProp != null){
                                onAttrExchange(subSaveBean, subTempBean,notJudgeProp);
                            }else {
                                onAttrExchange(subSaveBean, subTempBean);
                            }
                        }
                        //基本属性 情况 Integer String 等 如果 不为 Null 直接 赋值给 save对象属性
                    }else if(tempName.startsWith("get") && !tempName.equals("getClass")
                            && !isBean){
                        boolean isContinue = false;
                        //如果
                        if(invoke == null){
                            isContinue = true;
                        }
                        if(notJudgeProp != null){
                            for (String s : notJudgeProp) {
                                if((s.equals(substring))){
                                    isContinue = false;
                                }
                            }
                        }
                        if(isContinue){
                            continue;
                        }
                        Method saveMethod = saveBean.getClass().getMethod("s" + tempName.substring(1),tempMethod.getReturnType() );
                        saveMethod.invoke(saveBean,tempMethod.invoke(tempBean));
                    }


                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        twoWayBinding(saveBean);

    }

    /**
     * 为springDataJPA(hibernate) 级联保存操作添加双向绑定操作 解决单向级联保存时外键为null的问题
     * @param object Entity
     */
    public static void twoWayBinding( Object object ) {
        try {
            Field[] declaredFields = object.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                String mappedBy = findMappedBy(declaredField);
                if ( mappedBy == null ) {
                    continue;
                }
                declaredField.setAccessible(true);
                Object o = declaredField.get(object);
                if ( o == null ) {
                    continue;
                }
                //如果是集合
                if ( declaredField.getType() == List.class || declaredField.getType() == Set.class) {
                    ArrayList<Object> objects = new ArrayList<>((Collection<?>) o);
                    for (Object object1 : objects) {
                        Field declaredField1 = object1.getClass().getDeclaredField(mappedBy);
                        declaredField1.setAccessible(true);
                        if ( ! MyBeanUtils.objectIsEmpty( object1 ) ) {
                            declaredField1.set(object1 , object);
                        }
                    }
                } else {
                    //那就是bean
                    Field declaredField1 = o.getClass().getDeclaredField(mappedBy);
                    declaredField1.setAccessible(true);
                    if ( ! MyBeanUtils.objectIsEmpty( o ) ) {
                        declaredField1.set(o , object);
                    }
                }
            }
        } catch ( Exception e ) {
            System.out.println("twoWayBinding 数据交换错误");
            e.printStackTrace();
        }
    }

    private static String findMappedBy(Field field ) {
        if ( field == null ) {
            return  null;
        }
        field.setAccessible(true);
        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
        if ( joinColumn != null ) {
            return  null;
        }
        OneToOne oneToOne = field.getAnnotation(OneToOne.class);
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        String mappedBy = null;
        if ( oneToOne != null ) {
            mappedBy = oneToOne.mappedBy();
        }
        if ( oneToMany != null ) {
            mappedBy = oneToMany.mappedBy();
        }
        return mappedBy;

    }



}
