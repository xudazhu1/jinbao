package com.hnjbkc.jinbao.statistic;

import java.util.Date;
import java.util.Map;

public interface StatisticService {

    /**
     * 图表页面上边数据
     * @return
     */
    Map<String,Object> listProjectByTime();

    /**
     * 时间图表
     * @return
     */
    Map<String, Object> listProjectByTimeChar();

    Map<String, Object> listStatusChar();
}
