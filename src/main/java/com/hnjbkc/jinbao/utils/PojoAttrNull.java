package com.hnjbkc.jinbao.utils;


import java.lang.reflect.Method;

/**
 * @author linjianf
 * 1传入的对象属性如果为‘’ 空字符 那将set为null
 * 2 传入的对象属性中在com.jbkc 下的bean 如果没有id 则set为null
 */
public class PojoAttrNull {

    public static void setAttrVacancy(Object obj) {
        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods) {
                /*循环获取全部方法*/
                String methodName = method.getName();
                /*获取当前返回值字节码*/
                Class<?> returnType = method.getReturnType();
                 /*通过字节码获取类全路径 如果在com.jbkc包下则是bean*/
                if (returnType.getName().contains("com.jbkc")) {
                    try {
                        /*如果返回true 则设置为null*/
                        Boolean aBoolean = setBeanNull(method.invoke(obj));
                        if(aBoolean){
                            /*把get的方法改成set方法 并且传入的obj 调用该方法设置为null*/
                            StringBuilder strBuilder = new StringBuilder(methodName);
                            strBuilder.setCharAt(0, 's');
                            methodName = strBuilder.toString();
                            Method methodSetNull = obj.getClass().getMethod(methodName, returnType);
                            methodSetNull.invoke(obj, new Object[]{null});
                        }
                    } catch (Exception e) {

                    }
                }
                /*获取所有get方法 不包含getClass*/
                boolean existed = methodName.startsWith("get") && !"getclass".equals(methodName);
                Method resmethod;
                if (existed) {
                    try {
                        /*如果get的值是 空字符“” 则设置为Null*/
                        if ("".equals(method.invoke(obj))) {
                            System.out.println(methodName+"属性设置为null");
                            StringBuilder strBuilder = new StringBuilder(methodName);
                            strBuilder.setCharAt(0, 's');
                            methodName = strBuilder.toString();
                            resmethod = obj.getClass().getMethod(methodName, String.class);
                            resmethod.invoke(obj, new Object[]{null});
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    }

    /**
     * 传入对象 判断是否有id
     * @param obj 传入bean
     * @return 是否有id 没有id 返回true 有id 返回false
     */
    public static Boolean setBeanNull(Object obj){
        if(obj==null){
            return true;
        }
        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if(methodName.contains("get")&&methodName.contains("Id")){
                try {
                    Object invoke = method.invoke(obj);
                    if(invoke==null){
                        System.out.println(obj+"级联bean应该设置为null");
                        return true;
                    }
                } catch (Exception e) {

                }
            }
        }
        return false;
    }

}
