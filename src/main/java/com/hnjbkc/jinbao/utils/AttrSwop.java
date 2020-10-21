package com.hnjbkc.jinbao.utils;

import java.lang.reflect.Method;

/**
 * @author linjianf
 */
public class AttrSwop {

    /**
     * 把第二个对象的属性值 赋值给第一个对象的属性值  两个对象一样类型
     * @param saveBean saveBean
     * @param tempBean saveBean
     */
    public static <T> void onAttrSwop(T  saveBean, T tempBean)  {
            try {
                Method[] methods1 = saveBean.getClass().getMethods();
                Method[] methods2 = tempBean.getClass().getMethods();
                for (Method method : methods1) {
                    String methodName1 = method.getName().toLowerCase();

                    for (Method method2 : methods2) {
                        String methodName2 = method2.getName().toLowerCase();
                        if(methodName1.startsWith("set")&&methodName2.startsWith("get")&&!methodName2.equals("getclass")&&!methodName2.equals("setoperator")
                                &&!methodName2.equals("getoperator")&&methodName1.substring(3, methodName1.length()).equals(methodName2.substring(3, methodName2.length()))){

                            String type = method.getParameterTypes()[0].getName();

                            if(method2.invoke(tempBean) != null && !method2.invoke(tempBean).equals("")){
                                Object value =  method2.invoke(tempBean);
                                System.out.println(method2.getName());
                                System.out.println(method.getName());
                                method.invoke(saveBean,value);

                            }
                        }
                    }
                }
            } catch ( Exception e) {
                e.printStackTrace();
            }


    }
}
