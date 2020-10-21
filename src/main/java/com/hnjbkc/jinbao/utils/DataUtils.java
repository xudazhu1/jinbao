package com.hnjbkc.jinbao.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author siliqiang
 * @date 2019.9.26
 */
public class DataUtils {
    /**
     * 拿到当前月的范围
     * @param date
     * @return
     */
    public static String getScope(String date) {
        if (date == null) {
            return "1970-01-01 ~ 2099-01-01";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd");
        Date parse = new Date();
        try {
            parse = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String format = simpleDateFormat1.format(parse);
        Date date1 = new Date(parse.getTime() + 1000L * 60 * 60 * 24 * 32);
        Date date2 = new Date(parse.getTime() + 1000L * 60 * 60 * 24 * (32 - (Integer.parseInt(simpleDateFormat2.format(date1)) - 1)));
        String format1 = simpleDateFormat1.format(date2);
        return (format + " ~ " + format1);
    }
}
