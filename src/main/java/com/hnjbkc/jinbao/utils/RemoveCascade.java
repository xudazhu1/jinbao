package com.hnjbkc.jinbao.utils;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 去除级联对象
 *
 * @author xudaz
 * @date 2019/3/18
 */
public class RemoveCascade {


    public static <T> void removeCascade4List(List<T> list, String... keeps) {
        for (T t : list) {
            removeCascade4Object(t , keeps);
        }
    }


    public static void removeCascade4Object(Object object, String... keeps) {
        if ( object == null ) {
            return;
        }
        Method[] methods = object.getClass().getMethods();
        loop:
        for (Method method : methods) {
            String methodName = method.getName();
            if ( "wait".equals(methodName) || "equals".equals(methodName) ) {
                continue ;
            }


            if ( keeps != null &&  keeps.length > 0 ) {
                for (String keep : keeps) {
                    if (keep.toLowerCase().equals(method.getName().toLowerCase().substring(3))) {
                            continue loop;
                    }
                }
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length > 0) {
                String className = parameterTypes[0].getName();

                if ( className.equals(List.class.getName()) || className.equals(Set.class.getName()) ) {
                    try {
                        method.invoke(object , (Object) null);
                        continue;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                if (!className.contains("java.lang")
                        && !className.equals(Date.class.getName()) ) {
                    try {
                        method.invoke(object, parameterTypes[0].newInstance());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
