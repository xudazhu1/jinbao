package com.hnjbkc.jinbao.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author siliqiang
 * @date 2019.10.06
 */
@RestController
@RequestMapping("statistics")
public class StatisticsController {
    private StatisticsService statisticsService;

    @Autowired
    public void setStatisticsService(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

   @GetMapping
    public Map<Object, Map<String, Object[]>> getStatistics() {
       return statisticsService.getStatistics();
    }

    @GetMapping("month")
    public Map<String, List<Object[]>> getMonthStatistical(String date){
        return statisticsService.getMonthStatistical(date);
    }
}
