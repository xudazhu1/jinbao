package com.hnjbkc.jinbao.utils;


import java.lang.reflect.Field;

public class CancellationUtil {

    public static <T> T obsolete(T bean) {
        try {
            Field primaryKey = getPrimaryKey(bean);
            primaryKey.set(bean, 7);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public static Field getPrimaryKey(Object o) {
        try {
            Class<?> aClass = o.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                if (declaredField.getAnnotation(Cancellation.class) != null) {
                    return declaredField;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }

    }
}
