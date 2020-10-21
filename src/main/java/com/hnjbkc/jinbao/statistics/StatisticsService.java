package com.hnjbkc.jinbao.statistics;

import com.hnjbkc.jinbao.disburse.DisburseDao;
import com.hnjbkc.jinbao.invoiceandmoneyback.moneyback.MoneyBackDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author siliqiang
 * @date 2019.10.6
 */
@Service
public class StatisticsService {
    private MoneyBackDao moneyBackDao;

    @Autowired
    public void setMoneyBackDao(MoneyBackDao moneyBackDao) {
        this.moneyBackDao = moneyBackDao;
    }

    private DisburseDao disburseDao;

    @Autowired
    public void setDisburseDao(DisburseDao disburseDao) {
        this.disburseDao = disburseDao;
    }

    /**
     * 财务汇总表
     * @return
     */
    public Map<Object, Map<String, Object[]>> getStatistics() {
        List<Object[]> statistics = moneyBackDao.getStatistics();
        List<Object[]> statisticsCompany = disburseDao.getStatistics();
        //准备最外层的日期为key的map
        HashMap<Object, Map<String, Object[]>> statisticsMap = new HashMap<>();
        for (Object[] statistic : statistics) {
            //准备里面收益单位的map
            Object[] objects = new Object[5];
            objects[0] = statistic[2];
            objects[1] = 0.0;
            objects[2] = 0.0;
            objects[3] = 0.0;
            Map<String, Object[]> stringMap = statisticsMap.get(statistic[0]);
            if (stringMap == null) {
                stringMap = new HashMap<>(5);
            }
            stringMap.put((String) statistic[1], objects);
            statisticsMap.put(statistic[0], stringMap);
        }
        //准备不同的支出类型以及金额
        HashMap<String, Double> disburseTyprMap = new HashMap<>();
        for (Object[] objects : statisticsCompany) {
            disburseTyprMap.put((String) objects[3] + objects[0] + objects[1], (Double) objects[2]);
        }
        //处理项目部门公司的支出
        for (Object[] objects1 : statisticsCompany) {
            if (statisticsMap.get(objects1[0]) == null) {
                Object[] objects = new Object[5];
                objects[0] = 0.0;
                if (disburseTyprMap.get("项目支出" + objects1[0] + objects1[1]) != null) {
                    objects[1] = disburseTyprMap.get("项目支出" + objects1[0] + objects1[1]);
                } else {
                    objects[1] = 0.0;
                }
                if (disburseTyprMap.get("部门支出" + objects1[0] + objects1[1]) != null) {
                    objects[2] = disburseTyprMap.get("部门支出" + objects1[0] + objects1[1]);
                } else {
                    objects[2] = 0.0;
                }
                if (disburseTyprMap.get("公司支出" + objects1[0] + objects1[1]) != null) {
                    objects[3] = disburseTyprMap.get("公司支出" + objects1[0] + objects1[1]);
                } else {
                    objects[3] = 0.0;
                }
                HashMap<String, Object[]> earningsMap = new HashMap<>(5);
                earningsMap.put((String) objects1[1], objects);
                statisticsMap.put(objects1[0], earningsMap);
            } else {
                Map<String, Object[]> earningsMap = statisticsMap.get(objects1[0]);
                if (earningsMap.get(objects1[1]) == null) {
                    Object[] objects = new Object[5];
                    objects[0] = 0.0;
                    if (disburseTyprMap.get("项目支出" + objects1[0] + objects1[1]) != null) {
                        objects[1] = disburseTyprMap.get("项目支出" + objects1[0] + objects1[1]);
                    } else {
                        objects[1] = 0.0;
                    }
                    if (disburseTyprMap.get("部门支出" + objects1[0] + objects1[1]) != null) {
                        objects[2] = disburseTyprMap.get("部门支出" + objects1[0] + objects1[1]);
                    } else {
                        objects[2] = 0.0;
                    }
                    if (disburseTyprMap.get("公司支出" + objects1[0] + objects1[1]) != null) {
                        objects[3] = disburseTyprMap.get("公司支出" + objects1[0] + objects1[1]);
                    } else {
                        objects[3] = 0.0;
                    }
                    earningsMap.put((String) objects1[1], objects);
                } else {
                    if (disburseTyprMap.get("项目支出" + objects1[0] + objects1[1]) != null) {
                        earningsMap.get(objects1[1])[1] = disburseTyprMap.get("项目支出" + objects1[0] + objects1[1]);
                    } else {
                        earningsMap.get(objects1[1])[1] = 0.0;
                    }
                    if (disburseTyprMap.get("部门支出" + objects1[0] + objects1[1]) != null) {
                        earningsMap.get(objects1[1])[2] = disburseTyprMap.get("部门支出" + objects1[0] + objects1[1]);
                    } else {
                        earningsMap.get(objects1[1])[2] = 0.00;
                    }
                    if (disburseTyprMap.get("公司支出" + objects1[0] + objects1[1]) != null) {
                        earningsMap.get(objects1[1])[3] = disburseTyprMap.get("公司支出" + objects1[0] + objects1[1]);
                    } else {
                        earningsMap.get(objects1[1])[3] = 0.0;
                    }
                }

            }
        }

        for (Map.Entry<Object, Map<String, Object[]>> objectMapEntry : statisticsMap.entrySet()) {
            Map<String, Object[]> value = objectMapEntry.getValue();
            value.keySet().removeIf(Objects::isNull);
        }
        statisticsMap.keySet().removeIf(Objects::isNull);

        //计算利润总额
        for (Map.Entry<Object, Map<String, Object[]>> objectMapEntry : statisticsMap.entrySet()) {
            Map<String, Object[]> value = objectMapEntry.getValue();
            for (Map.Entry<String, Object[]> stringEntry : value.entrySet()) {
                Object[] value1 = stringEntry.getValue();
                value1[4] = (Double) value1[0] - (Double) value1[1] - (Double) value1[2] - (Double) value1[3];
            }
        }

        Map<Object, Map<String, Object[]>> sortedMap = new TreeMap<>(new Comparator<Object>() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");

            @Override
            public int compare(Object o1, Object o2) {
                try {
                    Date parse1 = simpleDateFormat.parse((String) o1);
                    Date parse2 = simpleDateFormat.parse((String) o2);
                    return parse1.getTime() > parse2.getTime() ? -1 : 1;
                } catch (Exception e) {
                    return 1;
                }
            }
        });
        sortedMap.putAll(statisticsMap);
        return sortedMap;
    }

    /**
     * 按照月统计
     *
     * @return
     */
    public Map<String, List<Object[]>> getMonthStatistical(String date) {
        List<Object[]> montys = moneyBackDao.getMonty(date);
        List<Object[]> projectMonths = disburseDao.getProjectMonth(date);
        List<Object[]> departmentMonths = disburseDao.getDepartmentMonth(date);
        HashMap<String, List<Object[]>> monthMap = new HashMap<>();
        monthMap.put("收入", new ArrayList<>());
        monthMap.put("公司费用", new ArrayList<>());
        monthMap.put("部门费用", new ArrayList<>());
        monthMap.put("项目支出", new ArrayList<>());
        monthMap.put("利润", new ArrayList<>());
        monthMap.put("利润分配", new ArrayList<>());
        monthMap.put("未分配利润", new ArrayList<>());
        for (Object[] onty : montys) {
            if (onty[0].equals(date)) {
                List<Object[]> income = monthMap.get("收入");
                Object[] object = new Object[3];
                object[0] = onty[1];
                object[1] = onty[3];
                income.add(object);
            }
        }

        for (Object[] projectMonth : projectMonths) {
            if (date.equals(projectMonth[4])) {
                List<Object[]> projectDisburse = monthMap.get("项目支出");
                Object[] object = new Object[3];
                object[0] = projectMonth[2];
                object[1] = projectMonth[3];
                projectDisburse.add(object);
            }
        }

        for (Object[] departmentMonth : departmentMonths) {
            if (date.equals(departmentMonth[4])) {
                List<Object[]> departmentDisburse = monthMap.get("部门费用");
                List<Object[]> companyDisburse = monthMap.get("公司费用");
                List<Object[]> distributionOfProfits = monthMap.get("利润分配");
                if (departmentMonth[0].equals("部门支出")) {
                    Object[] department = new Object[3];
                    department[0] = departmentMonth[1];
                    department[1] = departmentMonth[3];
                    departmentDisburse.add(department);
                }
                if (departmentMonth[0].equals("公司支出")) {
                    Object[] company = new Object[3];
                    company[0] = departmentMonth[1];
                    company[1] = departmentMonth[3];
                    companyDisburse.add(company);
                }
                if (departmentMonth[0].equals("利润分配")) {
                    Object[] profit = new Object[3];
                    profit[1] = departmentMonth[3];
                    distributionOfProfits.add(profit);
                }
            }
        }

        //计算收入单月的总和
        Double incomeTotal = new Double(0);
        List<Object[]> income = monthMap.get("收入");
        for (Object[] objects : income) {
            incomeTotal += (Double) objects[1];
        }
        for (Object[] objects : income) {
            objects[2] = (String) numberFormat(incomeTotal);
        }

        //计算项目单月的所有支出
        double projectTotal = 0.00;
        List<Object[]> projectLists = monthMap.get("项目支出");
        for (Object[] projectList : projectLists) {
            projectTotal += (Double) projectList[1];
        }
        for (Object[] projectList : projectLists) {
            projectList[2] = numberFormat(projectTotal);
        }

        //计算公司费用单月的所有支出
        double companyTotal = 0.00;
        List<Object[]> companyLists = monthMap.get("公司费用");
        for (Object[] companyList : companyLists) {
            companyTotal += (Double) companyList[1];
        }
        for (Object[] companyList : companyLists) {
            companyList[2] = numberFormat(companyTotal);
        }

        //计算部门费用单月的所有支出
        double departmentTotal = 0.00;
        List<Object[]> departmentLists = monthMap.get("部门费用");
        for (Object[] department : departmentLists) {
            departmentTotal += (Double) department[1];
        }

        for (Object[] department : departmentLists) {
            department[2] = numberFormat(departmentTotal);
        }

        //计算利润
        List<Object[]> profits = monthMap.get("利润");
        Object[] profit = new Object[1];
        profits.add(profit);

        profit[0] =numberFormat(incomeTotal - projectTotal - companyTotal - departmentTotal) ;
        System.out.println(profit[0]);

        for (Map.Entry<String, List<Object[]>> stringListEntry : monthMap.entrySet()) {
            List<Object[]> value = stringListEntry.getValue();
            for (Object[] objects : value) {
            }
        }
        return monthMap;
    }

    private String numberFormat(Double num) {
        //格式化设置
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(num);

    }

}
