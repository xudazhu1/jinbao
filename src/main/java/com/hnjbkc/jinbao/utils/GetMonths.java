package com.hnjbkc.jinbao.utils;

import java.util.Calendar;
import java.util.Date;

public class GetMonths {
    /**
     * 计算两个日期之间相差的月数
     *
     * @param date1 被减的月数
     * @param date2 减数的月数
     * @return 返回的是几个月
     */
    public static int getMonths(Date date1, Date date2) {
        int iMonth = 0;
        int flag = 0;
        try {
            Calendar objCalendarDate1 = Calendar.getInstance();
            objCalendarDate1.setTime(date1);

            Calendar objCalendarDate2 = Calendar.getInstance();
            objCalendarDate2.setTime(date2);

            if (objCalendarDate2.equals(objCalendarDate1)) {
                return 0;
            }
            if (objCalendarDate1.after(objCalendarDate2)) {
                Calendar temp = objCalendarDate1;
                objCalendarDate1 = objCalendarDate2;
                objCalendarDate2 = temp;
            }
            if (objCalendarDate2.get(Calendar.DAY_OF_MONTH) < objCalendarDate1.get(Calendar.DAY_OF_MONTH)) {
                flag = 1;
            }

            if (objCalendarDate2.get(Calendar.YEAR) > objCalendarDate1.get(Calendar.YEAR)) {
                iMonth = ((objCalendarDate2.get(Calendar.YEAR) - objCalendarDate1.get(Calendar.YEAR))
                        * 12 + objCalendarDate2.get(Calendar.MONTH) - flag)
                        - objCalendarDate1.get(Calendar.MONTH);
            } else {
                iMonth = objCalendarDate2.get(Calendar.MONTH)
                        - objCalendarDate1.get(Calendar.MONTH) - flag;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iMonth;
    }
}
