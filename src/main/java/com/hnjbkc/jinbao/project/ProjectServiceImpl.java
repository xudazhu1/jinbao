package com.hnjbkc.jinbao.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.contract.ContractBean;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.implement.ImplementBean;
import com.hnjbkc.jinbao.quote.QuoteBean;
import com.hnjbkc.jinbao.quote.QuoteDao;
import com.hnjbkc.jinbao.utils.AttrExchange;
import com.hnjbkc.jinbao.utils.FormatJsonMap;
import com.hnjbkc.jinbao.utils.PageableUtils;
import com.hnjbkc.jinbao.utils.tableutils.TableUtilsDao;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.engine.spi.SessionDelegatorBaseImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 12
 * @date 2019-08-06
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class ProjectServiceImpl implements BaseService<ProjectBean> {

    private ProjectDao projectDao;

    @Autowired
    public void setProjectDao(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    public void setMapper(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
        this.mappingJackson2HttpMessageConverter = mappingJackson2HttpMessageConverter;
    }

    private TableUtilsDao tableUtilsDao;

    @Autowired
    public void setTableUtilsDao(TableUtilsDao tableUtilsDao) {
        this.tableUtilsDao = tableUtilsDao;
    }

    /**
     * 注入实体管理器,执行持久化操作
     */
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * 录入项目的时候 判断编号是否已存在 如果不存在则添加 否则报错
     * 同时 创建 项目 合同 经营管理
     *
     * @param projectBean 项目对象
     * @return 返回 布尔值
     */
    @Override
    public ProjectBean add(ProjectBean projectBean) {
        List<Integer> list = new ArrayList<>();
        for (ImplementBean implementBean : projectBean.getImplementBeans()) {
            if(list.contains(implementBean.getDepartmentBean().getDepartmentId())){
                throw new RuntimeException("实施部名称重复");
            }else {
                list.add(implementBean.getDepartmentBean().getDepartmentId());
            }
        }
        //通过截取编号 获取 时间 和 编号 查找当前编号是否存在
        //把页面传过来的 报价 游离态 编程 持久态 然后跟项目一起保存 做关联
        if (projectBean.getQuoteBean() != null) {
            QuoteBean quoteBean = projectBean.getQuoteBean();
            projectBean.setQuoteBean(quoteBean);
        }
        ContractBean contractBean = new ContractBean();
        contractBean.setContractState("未签");
        contractBean.setProjectBean(projectBean);
        //让经营管理也有项目
        projectBean.getManagementBean().setProjectBean(projectBean);
        //设置合同
        projectBean.setContractBean(contractBean);
        //获取 实施部 并把项目设置进List
        List<ImplementBean> implementBeans = projectBean.getImplementBeans();
        implementBeans.forEach(impl -> impl.setProjectBean(projectBean));
        String maxNumber = projectDao.findMaxNumber();
        if("01433".equals(maxNumber)){
            maxNumber = "2000";
        }
        StringBuilder s = new StringBuilder(Integer.parseInt(maxNumber) + 1 + "");
        int a = 5;
        for (int i = s.length(); i < a ; i++) {
            s.insert(0, "0");
        }
        projectBean.setProjectNum(projectBean.getProjectNum()+s.toString());
        return projectDao.save(projectBean);
    }

    @Override
    public Boolean delete(Integer id) {
        projectDao.deleteById(id);
        return true;
    }

    @Override
    public ProjectBean update(ProjectBean projectBean) {
        Optional<ProjectBean> byId = projectDao.findById(projectBean.getProjectId());
        if (byId.isPresent()) {
            ProjectBean projectDbBean = byId.get();
            AttrExchange.onAttrExchange(projectDbBean, projectBean);
            List<ImplementBean> implementBeans = projectDbBean.getImplementBeans();
            List<Integer> list = new ArrayList<>();
            for (ImplementBean implementBean : implementBeans) {
                if(list.contains(implementBean.getDepartmentBean().getDepartmentId())){
                    throw new RuntimeException("实施部名称重复");
                }else {
                    list.add(implementBean.getDepartmentBean().getDepartmentId());
                }
            }
            return projectDao.save(projectDbBean);
        }
        return null;
    }



    /**
     * 查询并且分页的方法(模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    public Page<ProjectBean> search(Map<String, Object> propMap, Pageable pageRequest) {
        Page<ProjectBean> projectBeans = sqlUtilsDao.searchAllByCustomProps(ProjectBean.class, propMap, pageRequest);
        return projectBeans;
    }

    public ProjectBean getProjectById(Integer id) {
        Optional<ProjectBean> byId = projectDao.findById(id);
        return byId.orElse(null);
    }

    public List<ProjectBean> findProjectNumName(ProjectBean projectBean) {
        return projectDao.findAll(Example.of(projectBean));
    }

    public List<Map<String, Object>> getProjectNumAndProjectName() {
        return projectDao.findAllProjectNumAndProjectName();
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    List getSingleProperty(String property) {
        return sqlUtilsDao.getSingleProperties(ProjectBean.class, property);
    }

    public Page get(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.getAllByCustomProps(ProjectBean.class, propMap, pageRequest);
    }


    /**
     *  jsonArray[16]项目金额 - 回款 = 追回款
     *  jsonArray[17]预估项目收入
     *  jsonArray[18]回款金额
     *  jsonArray[19]未回款金额
     *  jsonArray[20]预估可开票金额（追开票）
     *  jsonArray[21]开票金额
     *  jsonArray[22]开票未回款（追回款）
     *
     * @param map 条件map
     * @return 返回map
     */
    public Map<String, Object> findSchedule(Map<String, Object> map) {
        Pageable pageable = PageableUtils.producePageable4Map(map, "projectBean.projectId");
//        String[] customWhere = new String[]{ " projectBean.managementBean.managementCommissionMode <> '初始' " };
        map.remove("table_utils.custom_where");
        //序号	项目编号	项目名称	项目类型	人员名称	身份	行业	合同金额
        map.put("table_utils.fields",
                "projectId" +
                        "$contractBean.contractMoney" +
                        "$contractBean.contractEstimateMoney" +
                        "$managementBean.managementSettlementAmount" +
                        "$projectNum" +
                        "$projectName" +
                        "$projectBelongsType" +
                        "$projectLocationBean.projectLocationName" +
                        "$projectFirstPartyName"+
                        "$implementBeans[n].departmentBean.departmentName" +
                        "$projectManagementType" +
                        "$managementBean.managementRemark"+
                        "$managementBean.managementPartnersBean.userBean.userName" +
                        "$managementBean.managementSponsor" +
                        "$managementBean.managementMainHead" +
                        "$implementBeans[n].secondPartyBean.secondPartyName" +
                        "$implementBeans[n].implementProgress" +
                        "$implementBeans[n].projectStatusBean.projectStatusName" +
                        "$contractBean.contractState" +
                        "");
        Page<Object[]> page = tableUtilsDao.search4Table(ProjectBean.class, map, pageable);
        List<Object[]> content = page.getContent();
        List<Integer> projectIdList = new ArrayList<>();
        for (Object[] o : content) {
            projectIdList.add((Integer) o[0]);
        }
        Map<Integer, Double> estimatedIncome = getEstimatedIncome(projectIdList);
        JSONArray jsonArray = null;
        try {
            String jsonStr = mappingJackson2HttpMessageConverter.getObjectMapper().writeValueAsString(content);
            jsonStr = jsonStr.replaceAll("[\\\\][r][\\\\][n]","");
            jsonArray = mappingJackson2HttpMessageConverter.getObjectMapper().readValue(jsonStr, JSONArray.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (projectIdList.size() == 0) {
            projectIdList.add(-1);
        }
        List<List> income4ProjectIdList = projectDao.findSchedule4ProjectIdList(projectIdList);
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        for (int i = 0; i < jsonArray.size(); i++) {
            Double projectMoney = 0.0;
            Double incomeMoney = 0.0;
            Double invoiceMoney = 0.0;
            double notYetIncome = 0.0;
            double traceIncome = 0.0;
            Double projectEstimate = 0.0;

            String departmentName = ((String) jsonArray.getJSONArray(i).get(9)).replaceAll("[$]", "").replaceAll("[,]", "、");
            jsonArray.getJSONArray(i).set(9, departmentName);

            String secondPartyName = ((String) jsonArray.getJSONArray(i).get(15)).replaceAll("[$]", "").replaceAll("[,]", "、");
            jsonArray.getJSONArray(i).set(15, secondPartyName);
            Object test = jsonArray.getJSONArray(i).get(16);
            String implementProgress = ((String) jsonArray.getJSONArray(i).get(16)).replaceAll("[$]", "").replaceAll("[,]", "、");
            StringBuilder stringBuilder = new StringBuilder();
            String[] split = implementProgress.split("[、]",-1);
            for (int j = 0; j <split.length; j++) {
                if("".equals(split[j]) || "0".equals(split[j])){
                    stringBuilder.append("0%");
                }else {
                    stringBuilder.append(Double.parseDouble(split[j]) * 100 +"%");
                }
                if(j != split.length-1){
                    stringBuilder.append("、");
                }
            }

            jsonArray.getJSONArray(i).set(16, stringBuilder.toString());

            String projectStatusName = ((String) jsonArray.getJSONArray(i).get(17)).replaceAll("[$]", "").replaceAll("[,]", "、");
            jsonArray.getJSONArray(i).set(17, projectStatusName);
            //项目金额 = 合同金额 > 预算金额 > 预估金额
            if ("".equals(jsonArray.getJSONArray(i).get(1)) || 0 == (Double) jsonArray.getJSONArray(i).get(1)) {
                if ("".equals(jsonArray.getJSONArray(i).get(2)) || 0 == (Double)jsonArray.getJSONArray(i).get(2)) {
                    //预算金额
                    projectMoney = "".equals(jsonArray.getJSONArray(i).get(3)) ? 0 : (Double) jsonArray.getJSONArray(i).get(3);
                }else {
                    //预估金额
                    projectMoney = "".equals(jsonArray.getJSONArray(i).get(2)) ? 0 : (Double) jsonArray.getJSONArray(i).get(2);
                }
            } else {
                //合同金额
                projectMoney = "".equals(jsonArray.getJSONArray(i).get(1)) ? 0 : (Double) jsonArray.getJSONArray(i).get(1);

            }
            for (List list : income4ProjectIdList) {
                if (list.get(0).equals(jsonArray.getJSONArray(i).get(0))) {
                    if (list.get(1) != null) {
                        incomeMoney = (Double) list.get(1);
                    }
                    if (list.get(2) != null) {
                        invoiceMoney = (Double) list.get(2);
                    }
                }
            }
            //项目金额 - 回款 = 追回款
            notYetIncome = projectMoney - incomeMoney;
            //开票金额 - 回款金额
            traceIncome = invoiceMoney - incomeMoney;
            projectEstimate = estimatedIncome.get(jsonArray.getJSONArray(i).get(0));
            //项目金额
            jsonArray.getJSONArray(i).add(Double.valueOf(decimalFormat.format(projectMoney)));
            //预估项目收入
            jsonArray.getJSONArray(i).add(Double.valueOf(decimalFormat.format(projectEstimate)));
            //回款金额
            jsonArray.getJSONArray(i).add(Double.valueOf(decimalFormat.format(incomeMoney)));
            //未回款金额
            jsonArray.getJSONArray(i).add(Double.valueOf(decimalFormat.format(notYetIncome)));
            //预估可开票金额（追开票）
            jsonArray.getJSONArray(i).add(Double.valueOf(decimalFormat.format(projectEstimate - invoiceMoney)));
            //开票金额
            jsonArray.getJSONArray(i).add(Double.valueOf(decimalFormat.format(invoiceMoney)));
            //开票未回款（追回款）
            jsonArray.getJSONArray(i).add(Double.valueOf(decimalFormat.format(traceIncome)));

        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 0);
        resultMap.put("msg", "");
        resultMap.put("count", page.getTotalElements());
        resultMap.put("data", jsonArray);
        return resultMap;
    }



    /**
     * 项目预估收入
     * objects[0]项目类型
     * objects[1]审计金额
     * objects[2]财评
     * objects[3]预算
     * objects[4]合同
     * objects[5]报价
     * objects[6]进度
     * objects[7]项目id
     * objects[8]合同预估金额
     *
     * @param list
     * @return
     */
    public Map<Integer, Double> getEstimatedIncome(List<Integer> list) {
        Map<Integer, Double> objectObjectHashMap = new HashMap<>();
        if (list.size() == 0) {
            return objectObjectHashMap;
        }
        List<Object[]> estimatedIncome = projectDao.getEstimatedIncome(list);

        Double estimate = 0.0;
        for (Integer o : list) {
            objectObjectHashMap.put(o, estimate);
        }
        for (Object[] objects : estimatedIncome) {
            if (objects[6] == null){
                objects[6] = 0.0;
            }
            estimate = objectObjectHashMap.get(objects[7]);
            if ("政府项目".equals(objects[0])) {
                if (objects[1] != null && (Double) objects[1] != 0) {
                    estimate = (Double) objects[1];
                } else if (objects[2] != null && (Double) objects[2] != 0) {
                    estimate = (Double) objects[2] * 0.8 * (Double) objects[6];
                }  else if (objects[4] != null && (Double) objects[4] != 0) {
                    estimate = (Double) objects[4] * 0.7 * (Double) objects[6];
                } else if (objects[3] != null && (Double) objects[3] != 0) {
                    estimate = (Double) objects[3] * 0.7*(Double) objects[6];
                }else if (objects[8] != null) {
                    estimate = (Double) objects[8] * 0.7 * (Double) objects[6];
                } else if (objects[5] != null ) {
                    estimate = (Double) objects[5] * 0.7 * (Double) objects[6];
                }
            } else {
                if (objects[4] != null && (Double) objects[4] !=0 ) {
                    estimate = (Double) objects[4] * (Double) objects[6];
                }else if (objects[3] != null && (Double) objects[3] != 0) {
                    estimate = (Double) objects[3]*(Double) objects[6];
                } else  if (objects[8] != null ) {
                    estimate = (Double) objects[8] * (Double) objects[6];
                } else if (objects[5] != null ) {
                    estimate = (Double) objects[5] * (Double) objects[6];
                }
            }
            objectObjectHashMap.put((Integer) objects[7], estimate);
        }
        return objectObjectHashMap;
    }



    public Map<String, Object> getProjectAmount(Map<String, Object> propMap) {
        StringBuilder sql = new StringBuilder("select \n" +
                "project_id,project_num,project_name,\n" +
                "ROUND(sum(disburse_payment_amount),2) from project\n" +
                "left join implement on implement_project_id = project_id\n" +
                "left join disburse on disburse_implement_id = implement_id\n" +
                "where 1 = 1  ");

        StringBuilder sql2 = new StringBuilder("select \n" +
                "project_id,project_num,project_name,\n" +
                "ROUND(sum(income_money),2),ROUND(sum(invoice_money),2) from project\n" +
                "left join income on income_project_id = project_id\n" +
                "left join invoice on invoice_id = income_invoice_id\n" +
                "where 1 = 1  ");

        String multipleValue = (String)propMap.get("multipleValue");
        String pageNum = "0";
        String pageSize = "10";
        if (propMap.get("pageNum") != null && propMap.get("pageSize") != null) {
            pageNum = "1".equals(propMap.get("pageNum")) ? "0" : (Integer.parseInt((String) propMap.get("pageNum")) - 1) * (Integer.parseInt((String) propMap.get("pageSize"))) + "";
        }
        pageSize = (String) propMap.get("pageSize");

        if(multipleValue != null){
            sql.append("and ( project_num like '%");
            sql.append(multipleValue);
            sql.append("%' ");
            sql.append("or project_name like '%");
            sql.append(multipleValue);
            sql.append("%' )");

            sql2.append("and ( project_num like '%");
            sql2.append(multipleValue);
            sql2.append("%' ");
            sql2.append("or project_name like '%");
            sql2.append(multipleValue);
            sql2.append("%' )");
        }
        sql.append(" GROUP BY project_id ");
        sql2.append(" GROUP BY project_id ");
        if(pageNum != null && pageSize != null){
            sql.append("limit ");
            sql.append(pageNum);
            sql.append(",");
            sql.append(pageSize);

            sql2.append("limit ");
            sql2.append(pageNum);
            sql2.append(",");
            sql2.append(pageSize);
        }

        Query query = entityManager.createNativeQuery(sql.toString());
        List<Object[] > resultList = query.getResultList();


        Query query2 = entityManager.createNativeQuery(sql2.toString());
        List<Object[] > resultList2 = query2.getResultList();
        List<Object> list = new ArrayList<>();

        for (Object[] o : resultList) {
            Map<String, Object> map = new HashMap<>();
            map.put("projectId",o[0]);
            map.put("projectNum",o[1]);
            map.put("projectName",o[2]);
            map.put("disburseMoney",o[3]);
            for (Object[] o2 : resultList2) {
                if(o[0].equals(o2[0])){
                    map.put("incomeMoney",o2[3]);
                    map.put("invoiceMoney",o2[4]);
                    break;
                }
            }
            list.add(map);
        }
        Long count = projectDao.getCount();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("msg", "succeed");
        resultMap.put("count", count);
        resultMap.put("data", list);
        return resultMap;
    }
}
