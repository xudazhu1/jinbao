package com.hnjbkc.jinbao.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xudaz
 * @date 2018/2/29
 */
public class MapUtils {


    public  static Map<String , Object> createMap(String[] keys , Object[] values) {
        if ( keys == null || values == null ) {
            return new HashMap<>(0);
        }
        Map<String , Object> map = new HashMap<>(keys.length);
        for (int i = 0; i < keys.length && i < values.length; i++) {
            map.put(keys[i] , values[i] );
        }
        return map;
    }
    
//    public static  Map<String, Object>  fomatRequestMap(Map<String, Object[]> map ) {
//        List<Map<Object , Object>> list = new ArrayList<>();
//
//
//        for (Entry<String, Object[]> entry : map.entrySet()) {
//            String key1 = entry.getKey();
//            if ( ! "pageNum".equals(key1) && ! "pageSize".equals(key1) && ! "sort".equals(key1)  && ! "sortFieid".equals(key1) ) {
//
//                Map<Object, Object> map1 = new HashMap<>(5);
//                if ( key1.startsWith("$")) {
//                    for (Object value : entry.getValue()) {
//                        if ( key1.startsWith("$S.") && value.contains("~") ) {
//                            String[] split = key1.substring(3).split("~");
//
//                        }
//                        System.out.println(key1 +  ":" + value  );
//                        String key = entry.getKey();
//                        if ( key.contains("_") ) {
//                            key = toUpperCaseFirst(key);
//                        }
//                        if ( key.endsWith("[]") ) {
//                            key = key.substring(0, key.length()-2 ) ;
//                            map1.put(key, value );
//                        } else {
//                            map1.put(key , value );
//                        }
//                    }
//
//                } else {
//                    map1.put(entry.getKey() , entry.getValue());
//                }
//
//                list.add(map1);
//            }
//        }
//        System.out.println("formatMap = " + map1);
//        return map1;
//    }
//
//
//    /**
//     * 把str "_" 后的首位转换成大写
//     * @param str 要转换的str
//     * @return 转换后的结果
//     */
//    public static String toUpperCaseFirst(String str) {
//        if ( str == null || "".equals(str) ) {
//            return  null;
//        }
//        String[] strings = str.split("_");
//        StringBuilder temp = new StringBuilder(strings[0]);
//        for (int i = 1; i < strings.length; i++) {
//            char a  = 'a' , z = 'z';
//            if ( strings[i].charAt(0) >= a && strings[i].charAt(0) <= z ) {
//                char[] chars = strings[i].toCharArray();
//                chars[0] = (char)(chars[0] - 32);
//                strings[i] =   new String(chars);
//            } else {
//                strings[i] =   str;
//            }
//            temp.append(strings[i]);
//
//        }
//        return temp.toString();
//
//
//
//    }


}
