package com.hnjbkc.jinbao.statistic;

import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.utils.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 12
 * @Date 2019-09-21
 */
@Service
public class StatisticServiceImpl implements StatisticService {

    private StatisticDao statisticDao;

    @Autowired
    public void setStatisticDao(StatisticDao statisticDao ) {
        this.statisticDao  = statisticDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Override
    public Map<String,Object> listProjectByTime() {
        Map<String,Object> map = new HashMap<>(2);
        Map<String, List> projectMap = getCountAndRation("ProjectBean", "projectApprovalTime");
        Map<String, List> contractMap = getCountAndRation("ContractBean", "contractSigningDate");
        Map<String, List> incomeMap = getCountAndRation("IncomeBean", "incomeDate"," and incomeAuditStatus = '1'");
        Map<String, List> departmentDisburseMap = getCountAndRation("DisburseBean", "disburseTime"
                ," and disburseDetailBean.disburseTypeBean.disburseCategoryBean.disburseCategoryName = '部门支出'"
                ," and (approvalStatusBean.approvalStatusId = '2' or paymentStatusBean.paymentStatusId = '7')");
        Map<String, List> companyDisburseMap = getCountAndRation("DisburseBean", "disburseTime"
                ," and disburseDetailBean.disburseTypeBean.disburseCategoryBean.disburseCategoryName = '公司支出'"
                ," and (approvalStatusBean.approvalStatusId = '2' or paymentStatusBean.paymentStatusId = '7')");
        Map<String, List> projectDisburseMap = getCountAndRation("DisburseBean", "disburseTime"
                ," and disburseDetailBean.disburseTypeBean.disburseCategoryBean.disburseCategoryName = '项目支出'"
                ," and (approvalStatusBean.approvalStatusId = '2' or paymentStatusBean.paymentStatusId = '7')");

        map.put("项目立项",projectMap);
        map.put("合同签订",contractMap);
        map.put("回款笔数",incomeMap);
        map.put("部门费用",departmentDisburseMap);
        map.put("公司费用",companyDisburseMap);
        map.put("项目费用",projectDisburseMap);
        return map;
    }

    public Map<String,List> getCountAndRation(String className,String propDateName, String... custom){
        Map<String,List> map = new HashMap<>(2);
        Long thisWeek = getBeanCountByTime(className, propDateName, 0,custom);
        Long lastWeek = getBeanCountByTime(className, propDateName, 1,custom);
        String ringRationWeek = getRingRation((double) thisWeek, (double) lastWeek);
        System.out.println(className + "本周 ==> " + thisWeek);
        System.out.println(className + "上周 ==> " + lastWeek);
        System.out.println(className + "本周环比率 ==> " + ringRationWeek);

        Long thisMonth = getBeanCountByTime(className, propDateName, 2,custom);
        Long lastMonth = getBeanCountByTime(className, propDateName, 3,custom);
        String ringRationMonth = getRingRation((double) thisMonth, (double) lastMonth);
        System.out.println(className + "本月 ==> " + thisMonth);
        System.out.println(className + "上月 ==> " + lastMonth);
        System.out.println(className + "本月环比率 ==> " + ringRationMonth);
        List<String> weekList = new ArrayList<>();
        weekList.add(thisWeek+"");
        weekList.add(ringRationWeek);
        List<String> monthList = new ArrayList<>();
        monthList.add(thisMonth+"");
        monthList.add(ringRationMonth);
        map.put("week",weekList);
        map.put("month",monthList);
        return map;
    }
    public Long getBeanCountByTime(String className, String propDateName, int timeHorizon, String... custom){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String beginDayOfWeek = null;
        String endDayOfWeek = null;
        switch (timeHorizon){
            // 0 本周 ,1 上周, 2 本月, 3上月
            case 0 : beginDayOfWeek = sdf.format(DateUtils.getBeginDayOfWeek());endDayOfWeek =sdf.format(DateUtils.getEndDayOfWeek());break;
            case 1 : beginDayOfWeek = sdf.format(DateUtils.getBeginDayOfLastWeek());endDayOfWeek =sdf.format(DateUtils.getEndDayOfLastWeek());break;
            case 2 : beginDayOfWeek = sdf.format(DateUtils.getBeginDayOfMonth());endDayOfWeek =sdf.format(DateUtils.getEndDayOfMonth());break;
            case 3 : beginDayOfWeek = sdf.format(DateUtils.getBeginDayOfLastMonth());endDayOfWeek =sdf.format(DateUtils.getEndDayOfLastMonth());break;
            default:break;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if(custom.length > 0){
            for (String s : custom) {
                stringBuilder.append(s);
            }
        }
        String hqlThisWeek = "from "+className+"  where "+propDateName+" >= '"
                + beginDayOfWeek + "' and "+propDateName+" <= '" + endDayOfWeek +"' " + stringBuilder.toString();
        return sqlUtilsDao.count(hqlThisWeek, null);
    }

    /**
     * 获取环比值
     * @param begin
     * @param end
     * @return
     */
    public String getRingRation(Double begin, Double end){
        if(end == 0){
            return "100%";
        }
        double ringRation = (begin - end)/end * 100;
        return ringRation + "%";
    }


    /**
     * 时间统计图表
     */
    @Override
    public Map<String, Object> listProjectByTimeChar() {
        Map<String, Object> map = new HashMap<>(4);
        List projectChar = getDateChar(0);
        List contractChar = getDateChar(1);
        List departmentGroup = getDepartmentGroup();
        departmentGroup.add(0,"timerange");
        map.put("部门分组",departmentGroup);
        map.put("项目立项",projectChar);
        map.put("合同签订",contractChar);
        return map;
    }

    /**
     * 0 是查询 项目立项日期图表
     * 1 是查询 合同状态日期图表
     * @param select
     * @return
     */
    List getDateChar(int select){
        //获取 现在时间 和 之前时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormatMode = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取现在时间
        Date nowTime = DateUtils.getDayBegin();
        //获取之前时间
        Date beforeTime = DateUtils.getFrontDay(nowTime, 27);
        //转成字符串
        String begin = simpleDateFormatMode.format(beforeTime);
        String emd = simpleDateFormatMode.format(DateUtils.getDayEndTime(nowTime));

        //获取 现在时间 和 之前时间 的 中间所有日期
        List<Date> timeList = DateUtils.getTimeList(beforeTime, nowTime);
        //创建一个 对象 把所有日期都 传进去
        List<Map<String,Object>> approvalList = new ArrayList<>();
        for (Date date : timeList) {
            Map<String, Object> map = new HashMap<>(5);
            map.put("timerange",simpleDateFormat.format(date));
            approvalList.add(map);
        }
        //从数据库 查询出 日期和部门的 分组  返回map  timerange:日期 department_name:部门名称 department_count:部门数量
        //开始时间 和 结束时间的字符串 4周
        List<Map<String,Object>> dbMap = null;
        if(select == 0){
            dbMap =  statisticDao.projectApproval(begin, emd);
        }else if(select == 1){
            dbMap =  statisticDao.contractSigning(begin, emd);
        }

        //遍历所有的日期
        for (Map<String, Object> map : approvalList) {

            for (Map<String, Object> stringObjectMap : dbMap) {
                //如果 数据库查询出来的日期 被包含 则 把 部门名称和 统计 取出来 在 装进去
                if(map.containsValue(stringObjectMap.get("timerange"))){
                    if (map.containsKey(stringObjectMap.get("char_name"))){
                        Integer lateCount = (Integer)stringObjectMap.get("char_count");
                        Integer beforeCount = (Integer)map.get(stringObjectMap.get("char_name"));
                        map.put((String) stringObjectMap.get("char_name"),lateCount+beforeCount);
                    }else {
                        map.put((String) stringObjectMap.get("char_name"),stringObjectMap.get("char_count"));
                    }
                }
            }

        }
        return approvalList;
    }

    /**
     * 获取 实施 部门的 分组
     * @return
     */
    public List getDepartmentGroup(){
        List<String> departmentGroup = statisticDao.getDepartmentGroup();
        return departmentGroup;
    }

    /**
     * 合同状态分组
     * @return
     */
    public List getContractStateGroup(){
        List<String> contractStateGroup = statisticDao.getContractStateGroup();
        contractStateGroup.add(0,"timerange");
        return contractStateGroup;
    }

    /**
     * 获取 部门 和 项目状态图表
     */
    @Override
    public Map<String, Object> listStatusChar(){
        Map<String, Object> mapMap = new HashMap<>();
        List<Object[]> chartGroupByImplStatus = statisticDao.getChartGroupByImplStatus();
        List departmentGroup = getDepartmentGroup();
        Map<String, Object> implStatusMap = new HashMap<>();
        chartStatusSetValueMap(chartGroupByImplStatus,implStatusMap,departmentGroup);
        mapMap.put("部门分组",departmentGroup);
        mapMap.put("项目状态",implStatusMap);

        List<Map<String, Object>> groupContractStatus = statisticDao.getGroupContractStatus();
        mapMap.put("合同状态",groupContractStatus);
        return mapMap;
    }

    /**
     * 做项目状态 和 部门 之间处理
     * @param chartGroupBySubrangeStatus 项目状态和部门 的分组
     * @param subrangeStatusMap 需要插入的 Map
     * @param chartGroupBySubrangeScope 需要赛选的条件
     */
    private void chartStatusSetValueMap(List<Object[]> chartGroupBySubrangeStatus,Map<String, Object> subrangeStatusMap,List<String> chartGroupBySubrangeScope){
        for (Object[] objects : chartGroupBySubrangeStatus) {
            //当主map 如果 包含了 该状态 比如  '未开工'    objects[0]是状态 objects[1]是地区 objects[2]是当前地区有多少记录
            if(subrangeStatusMap.containsKey(objects[0])){
                //获取 当前map 的 value 通过 get(key) 获取 获取 是一个数组
                Object[] on1 = (Object[])subrangeStatusMap.get(objects[0]);
                //循环 上面 的 主地区List
                for (int i = 0; i < chartGroupBySubrangeScope.size(); i++) {
                    //如果 地区 等于 主List的地区
                    if(objects[1].equals(chartGroupBySubrangeScope.get(i))){
                        //则 当前 索引的 值 是 objects[2]
                        on1[i] = objects[2];
                    }
                }
            }else {
                //如果是第一次 创建一个 主地区List长度的 数组
                Object[] on1 =new Object[chartGroupBySubrangeScope.size()];
                //并且循环  要知道 当前地区 是 在数组的 第几个位置 然后 设置 值
                for (int i = 0; i < chartGroupBySubrangeScope.size(); i++) {
                    if(objects[1].equals(chartGroupBySubrangeScope.get(i))){
                        on1[i] = objects[2];
                    }
                }
                //把  key 为 项目状态  value 为 数组里面元素为 对应地区的值 存入map
                subrangeStatusMap.put((String) objects[0],on1);
            }

        }
    }
}
