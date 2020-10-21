package com.hnjbkc.jinbao.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NextMonth {
    public  static Date getNextMonth(String date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd");
        Date parse = new Date();
        try {
            parse = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date1 = new Date(parse.getTime() + 1000L * 60 * 60 * 24 * 32);
        return new Date(parse.getTime() + 1000L * 60 * 60 * 24 * (32 - (Integer.parseInt(simpleDateFormat2.format(date1)) - 1)));
    }
}
