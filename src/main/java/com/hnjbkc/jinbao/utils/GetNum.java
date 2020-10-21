package com.hnjbkc.jinbao.utils;

/**
 * 生成最大的编号
 * @author siliqiang
 * @date 2019.9.2
 */
public class GetNum {
     public static String getMaxnum(String maxSerialNum){
         maxSerialNum = maxSerialNum == null ? "00000" : maxSerialNum;
         StringBuilder s = new StringBuilder(Integer.parseInt(maxSerialNum) + 1 + "");
         System.out.println(s);
         int a = 5;
         for (int i = s.length(); i < a; i++) {
             s.insert(0, "0");
         }
         return "" + s;

    }
}
