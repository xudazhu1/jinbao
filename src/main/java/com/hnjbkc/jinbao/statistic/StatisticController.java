package com.hnjbkc.jinbao.statistic;

import com.hnjbkc.jinbao.disburse.property.PropertyBean;
import com.hnjbkc.jinbao.project.ProjectBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 12
 * @Date 2019-09-21
 */
@RestController
@RequestMapping("statistic")
public class StatisticController {

    private StatisticServiceImpl statisticServiceImpl;

    @Autowired
    public void setStatisticServiceImpl(StatisticServiceImpl statisticServiceImpl) {
        this.statisticServiceImpl = statisticServiceImpl;
    }

    @GetMapping("count")
    public Map<String, Object> listProjectByTime(){
        return statisticServiceImpl.listProjectByTime();
    }

    @GetMapping("statistics_by_time")
    public Map<String, Object> listProjectByTimeChar(){
        return statisticServiceImpl.listProjectByTimeChar();
    }

    @GetMapping("statistics_by_status")
    public Map<String, Object> listProjectByStatusChar(){
        return statisticServiceImpl.listStatusChar();
    }
}
