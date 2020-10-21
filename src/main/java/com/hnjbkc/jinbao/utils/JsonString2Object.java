package com.hnjbkc.jinbao.utils;

import net.sf.json.JSONObject;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 将json格式的字符串转为Bean对象的工具类
 * @author xudaz
 * @date 2019/5/13
 */
public class JsonString2Object {


    public  static <T> List<T> toBean4List(List<String> jsonString , Class<T> tClass ) throws Exception {
        List<T> tList = new ArrayList<>();
        for (String s : jsonString) {
            tList.add(toBean(s , tClass));
        }
        return tList;
    }

    public  static <T> T  toBean( String jsonString , Class<T> tClass ) throws Exception {

        T t =  tClass.newInstance();
        JSONObject jsonObject = JSONObject.fromObject(jsonString , FormatJsonMap.jsonConfig);
        Map<String , JSONObject> map = new HashMap<>(10);
        for (Object o : jsonObject.keySet()) {
            String key = (String) o;
            Object value = jsonObject.get(o);
            if ( key.contains(".")) {
                String substringLeft = key.substring(0 , key.indexOf("."));
                String substringRight = key.substring(key.indexOf(".") + 1);
                if ( map.containsKey(substringLeft)) {
                    JSONObject jsonObject1 = map.get(substringLeft);
                    jsonObject1.put(substringRight , value);
                } else {
                    JSONObject temp = new JSONObject();
                    temp.put(substringRight , value);
                    map.put(substringLeft , temp);
                }
            }
            String upperKey = key.substring(0 , 1).toUpperCase() + key.substring(1);
            Method setMethod = getMethod(tClass,"set" + upperKey );
            Method getMethod = getMethod(tClass , "get" + upperKey);
            if ( setMethod != null && getMethod != null ) {
                Object value1 = formatValue(getMethod.getReturnType() , value.toString());
                try {
                    setMethod.invoke(t , value1);
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }

        }

        map.forEach( ( key , value ) -> {
            if ( "".equals(key) ) {
                return;
            }
            try {
                String upperKey = key.substring(0 , 1).toUpperCase() + key.substring(1);
                Method setMethod = getMethod(tClass ,"set" + upperKey);
                Method getMethod = getMethod(tClass , "get" + upperKey);
                if ( setMethod != null && getMethod != null ) {
                    setMethod.invoke(t , toBean(value.toString() , getMethod.getReturnType()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return t;





    }

    private static Object formatValue( Class returnType , Object value ) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if ( value == null ) {
            return null;
        }
        if ( value.getClass() == returnType ) {
            return value;
        }

//        if ( value.getClass() == String.class ) {

            if ( returnType == Integer.class ) {
                return  Integer.parseInt((String)value );
            }
            if ( returnType == Double.class ) {
                return  Double.parseDouble((String)value );
            }
            if ( returnType == Boolean.class ) {
                return  Boolean.parseBoolean((String)value );
            }
            if ( returnType == Date.class ) {
                try {
                    return  "".equals(value) ? null : simpleDateFormat.parse((String) value);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return null;
                }
            }
//        }


        return value;


    }



    private  static  Method getMethod(Class clazz , String methodName ) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if ( method.getName().equals(methodName) ) {
                return  method;
            }
        }
        return null;
    }

}
