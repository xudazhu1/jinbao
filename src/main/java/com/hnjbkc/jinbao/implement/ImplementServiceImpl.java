package com.hnjbkc.jinbao.implement;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.common.CommonResult;
import com.hnjbkc.jinbao.disburse.DisburseBean;
import com.hnjbkc.jinbao.disburse.expenseaccount.ExpenseAccountBean;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.implement.implementrecord.ImplementRecordBean;
import com.hnjbkc.jinbao.implement.projectstatus.ProjectStatusBean;
import com.hnjbkc.jinbao.implement.projectstatusrecord.ProjectStatusRecordBean;
import com.hnjbkc.jinbao.implement.projecttype.ProjectTypeBean;
import com.hnjbkc.jinbao.implement.secondparty.SecondPartyBean;
import com.hnjbkc.jinbao.productioncosts.ProductionCostsBean;
import com.hnjbkc.jinbao.productioncosts.productioncostsfile.ProductionCostsFileBean;
import com.hnjbkc.jinbao.productioncosts.productioncostsfile.ProductionCostsFileDao;
import com.hnjbkc.jinbao.project.ProjectBean;
import com.hnjbkc.jinbao.utils.AttrExchange;
import com.hnjbkc.jinbao.workload.WorkLoadBean;
import com.hnjbkc.jinbao.workload.WorkLoadDao;
import com.hnjbkc.jinbao.workload.profession.ProfessionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.transaction.Transactional;
import java.io.File;
import java.util.*;

/**
 * @author 12
 * @Date 2019-08-08
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class ImplementServiceImpl implements BaseService<ImplementBean> {

    private ImplementDao implementDao;

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setImplementDao(ImplementDao implementDao) {
        this.implementDao = implementDao;
    }

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
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

    @Override
    public ImplementBean add(ImplementBean implementBean) {
        Optional<ImplementBean> byId = implementDao.findById(implementBean.getImplementId());
        if (byId.isPresent()) {
            ImplementBean implementDbBean = byId.get();
            AttrExchange.onAttrExchange(implementDbBean, implementBean);
            List<WorkLoadBean> workLoadBeans = implementBean.getWorkLoadBeans();
            for (WorkLoadBean workLoadBean : workLoadBeans) {
                workLoadBean.setWorkLoadCreateDate(new Date());
                workLoadBean.setImplementBean(implementBean);
            }
            return implementDao.save(implementDbBean);
        }
        return null;
    }

    @Override
    @Modifying
    @Transactional(rollbackOn = Exception.class)
    public Boolean delete(Integer id) {
        Optional<ImplementBean> byId = implementDao.findById(id);
        if (byId.isPresent()) {
            ImplementBean implementBean = byId.get();
            List<ImplementBean> implementBeans = implementBean.getProjectBean().getImplementBeans();
            if (implementBeans.size() == 1) {
                throw new IllegalArgumentException("只有一个实施部, 无法删除");
            }
            List<DisburseBean> disburseBeans = implementBean.getDisburseBeans();
            if (disburseBeans.size() > 0) {
                throw new IllegalArgumentException("该实施部存在付款申请单, 无法删除");
            }
            implementDao.deleteImplementById(id);
            return true;
        }
        return false;
    }

    @Override
    public ImplementBean update(ImplementBean implementBean) {
        Optional<ImplementBean> byId = implementDao.findById(implementBean.getImplementId());
        if (byId.isPresent()) {
            ImplementBean implementDbBean = byId.get();
            ProjectBean projectBean = implementDbBean.getProjectBean();
            List<ImplementBean> implementBeans = projectBean.getImplementBeans();
            for (ImplementBean bean : implementBeans) {
                if (!bean.getImplementId().equals(implementBean.getImplementId())) {
                    if (bean.getDepartmentBean().getDepartmentId().equals(implementBean.getDepartmentBean().getDepartmentId())) {
                        throw new RuntimeException("实施部重复 请重新选择");
                    }
                } else {
                    boolean b1 = implementDbBean.getImplementRecordContent() == null && implementBean.getImplementRecordContent() != null;
                    boolean b2 = implementDbBean.getImplementRecordContent() != null && implementBean.getImplementRecordContent() != null;
                    if (b1 || b2 && !implementDbBean.getImplementRecordContent().equals(implementBean.getImplementRecordContent())) {
                        ImplementRecordBean implementRecordBean = new ImplementRecordBean();
                        implementRecordBean.setImplementRecordTime(new Date());
                        implementRecordBean.setImplementRecordOperator(implementBean.getCreateUserBean().getUserName());
                        implementRecordBean.setImplementRecordContent(implementBean.getImplementRecordContent());
                        implementDbBean.getImplementRecordBeanList().add(implementRecordBean);
                        implementRecordBean.setImplementBean(implementDbBean);
                    }
                    boolean saveNullAndTempVal = implementDbBean.getProjectStatusBean() == null && implementBean.getProjectStatusBean() != null;
                    boolean isNotNull = implementDbBean.getProjectStatusBean() != null && implementBean.getProjectStatusBean() != null;
                    if (saveNullAndTempVal || isNotNull && !implementDbBean.getProjectStatusBean().getProjectStatusId()
                            .equals(implementBean.getProjectStatusBean().getProjectStatusId())) {
                        String hql = " from  ProjectStatusBean where projectStatusId = " +
                                implementBean.getProjectStatusBean().getProjectStatusId();
                        List list = (List) sqlUtilsDao.exSqlCustom(ProjectStatusBean.class, hql, null);
                        if (list.size() > 0) {
                            ProjectStatusBean statusBean = (ProjectStatusBean) list.get(0);
                            ProjectStatusRecordBean statusRecordBean = new ProjectStatusRecordBean();
                            statusRecordBean.setProjectStatusRecordName(statusBean.getProjectStatusName());
                            statusRecordBean.setProjectStatusRecordTime(new Date());
                            statusRecordBean.setProjectStatusRecordOperator(implementBean.getCreateUserBean().getUserName());
                            statusRecordBean.setImplementBean(implementDbBean);
                            List<ProjectStatusRecordBean> projectStatusRecordBeanList =
                                    implementDbBean.getProjectStatusRecordBeanList();
                            projectStatusRecordBeanList.add(statusRecordBean);
                        }
                    }

                }

            }

            if (implementDbBean.getCreateUserBean() == null) {
                implementBean.setImplementCreateTime(new Date());
            } else {
                implementBean.setCreateJobBean(null);
                implementBean.setCreateUserBean(null);
            }
            AttrExchange.onAttrExchange(implementDbBean, implementBean);
            return implementDao.save(implementDbBean);
        }
        return null;
    }

    public Map<String, Object> getImplementAttributes() {
        //项目状态
        Object projectStatusBean = sqlUtilsDao.
                exSqlCustom(ProjectStatusBean.class, "from ProjectStatusBean", null);
        //项目类型
        Object projectTypeBean = sqlUtilsDao.
                exSqlCustom(ProjectTypeBean.class, "from ProjectTypeBean", null);
        //乙方
        Object secondPartyBean = sqlUtilsDao.
                exSqlCustom(SecondPartyBean.class, "from SecondPartyBean", null);

        Map<String, Object> attrMap = new HashMap<>(10);
//        attrMap.put("implementDepartmentBean",implementDepartmentBean);
        attrMap.put("projectStatusBean", projectStatusBean);
        attrMap.put("projectTypeBean", projectTypeBean);
        attrMap.put("secondPartyBean", secondPartyBean);
        return attrMap;
    }

    public Page getWorkCount(Map<String, Object> propMap, Pageable pageable) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : propMap.keySet()) {
            boolean b = "".equals(propMap.keySet());
            if ("".equals(propMap.keySet()) || " ".equals(propMap.keySet())) {
                continue;
            }
            stringBuilder.append(" and impl.");
            stringBuilder.append(key);
            stringBuilder.append(" = ");
            stringBuilder.append("'");
            stringBuilder.append(propMap.get(key));
            stringBuilder.append("'");
        }
        String hql = "SELECT DISTINCT  impl from ImplementBean impl " +
                "inner join fetch impl.workLoadBeans wl " +
                "where (1 = 1) " + stringBuilder.toString();
        System.out.println("hql -->" + hql);
        Query query = entityManager.createQuery(hql);

        //设置分页
        query.setMaxResults(pageable.getPageSize());
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        List<ImplementBean> resultList = query.getResultList();
        resultList.forEach(System.out::println);

        String count = "SELECT DISTINCT  impl.implementId from ImplementBean impl " +
                "inner join  impl.workLoadBeans wl " +
                "where (1 = 1) " + stringBuilder.toString();
        List<Integer> countList = entityManager.createQuery(count).getResultList();
        int size = countList.size();
        System.out.println(size);
        List<Map<String, Object>> list = new ArrayList<>();
        for (ImplementBean implementBean : resultList) {
            Map<String, Object> map = new HashMap<>();
            ProjectBean projectBean = implementBean.getProjectBean();
            map.put("projectNum", projectBean.getProjectNum());
            map.put("projectName", projectBean.getProjectName());
            map.put("implementName", implementBean.getDepartmentBean().getDepartmentName());
            double workCount = 0;
            List<WorkLoadBean> workLoadBeans = implementBean.getWorkLoadBeans();
            Map<String, Object> workMap = new HashMap<>();
            for (WorkLoadBean workLoadBean : workLoadBeans) {
                ProfessionBean professionBean = workLoadBean.getProfessionBean();
                workCount += workLoadBean.getWorkLoadWorkLoad();
                workMap.put(professionBean.getProfessionName(), workLoadBean.getWorkLoadWorkLoad());
            }
            map.put("count", workCount);
            map.put("professions", workMap);
            list.add(map);
        }
        return new PageImpl<>(list, pageable, size);
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    List getSingleProperty(String property) {
        return sqlUtilsDao.getSingleProperties(ImplementBean.class, property);
    }

    public ImplementBean getImplementById(Integer id) {
        Optional<ImplementBean> byId = implementDao.findById(id);
        return byId.orElse(null);
    }

    /**
     * 先查询出 所需要的实施 然后 通过实施去找 支出 和 收入 做成本不含税 通过 编号实施id  的 map key 方式 来对应
     *
     * @param propMap     页面参数
     * @param pageRequest pageRequest
     * @return Page<Object>
     */
    public Page<Object> findAllByProjectBeanProjectNumIn(Map<String, Object> propMap, Pageable pageRequest) {
        Page<ImplementBean> projectBeanProjectNumIn = sqlUtilsDao.searchAllByCustomProps(ImplementBean.class, propMap, pageRequest);
        List<ImplementBean> content = projectBeanProjectNumIn.getContent();
        Map<String, Object> cost = cost(content);
        List<Object> list = new ArrayList<>();

        for (ImplementBean implementBean : content) {
            Map<String, Object> map = new HashMap<>(20);
            map.put("项目编号", implementBean.getProjectBean().getProjectNum());
            map.put("项目名称", implementBean.getProjectBean().getProjectName());
            map.put("经营类型", implementBean.getProjectBean().getProjectManagementType());
            map.put("经营部负责人", implementBean.getProjectBean().getManagementBean().getManagementMainHead());
            map.put("实施部", implementBean.getDepartmentBean().getDepartmentName());
            map.put("实施部负责人", implementBean.getImplementImplementHead());
            HashMap costMap = (HashMap) cost.get(implementBean.getProjectBean().getProjectNum() + implementBean.getImplementId());
            map.putAll(costMap);
            list.add(map);
        }
        return new PageImpl<>(list, projectBeanProjectNumIn.getPageable(), projectBeanProjectNumIn.getTotalElements());
    }

    public Map<String, Object> findAllByProjectBeanProjectNumIn(Collection<String> collection) {
        List<ImplementBean> projectBeanProjectNumIn = implementDao.findAllByProjectBeanProjectNumIn(collection);
        Map<String, Object> cost = cost(projectBeanProjectNumIn);
        Map<String, Object> projectMap = new HashMap<>();
        for (ImplementBean implementBean : projectBeanProjectNumIn) {
            Object o = cost.get(implementBean.getProjectBean().getProjectNum() + implementBean.getImplementId());
            if (projectMap.containsKey(implementBean.getProjectBean().getProjectNum())) {
                Map<String, Object> map1 = (Map<String, Object>) projectMap.get(implementBean.getProjectBean().getProjectNum());
                Map<String, Object> map2 = (Map<String, Object>) cost.get(implementBean.getProjectBean().getProjectNum() + implementBean.getImplementId());
                for (String key1 : map1.keySet()) {
                    if(map1.get(key1) instanceof Double && map2.get(key1) instanceof Double){
                        Double o2 = (Double) map1.get(key1);
                        Double o1 = (Double) map2.get(key1);
                        map1.put(key1, o2 + o1);
                        projectMap.put(implementBean.getProjectBean().getProjectNum(), map1);
                    }
                }
            } else {
                projectMap.put(implementBean.getProjectBean().getProjectNum(), o);
            }
        }
        return projectMap;
    }

    private WorkLoadDao workLoadDao;

    @Autowired
    public void setWorkLoadDao(WorkLoadDao workLoadDao) {
        this.workLoadDao = workLoadDao;
    }

    /**
     * 成本不含税
     *
     * @param content
     * @return
     */
    public Map<String, Object> cost(List<ImplementBean> content) {
        List<Object[]> implementBeanIncomeMoney = implementDao.findImplementBeanByIncomeImplMoneyBean(content);
        Map<String, Object> map = new HashMap<>(10);
        //工作量的 id list 去查 人员成本
        List<Integer> worklist = new ArrayList<>();
        for (ImplementBean implementBean : content) {
            List<WorkLoadBean> workLoadBeans = implementBean.getWorkLoadBeans();
            for (WorkLoadBean workLoadBean : workLoadBeans) {
                worklist.add(workLoadBean.getWorkLoadId());
            }
        }
        List<List> personnelCommission = new ArrayList<>();
        if(worklist.size() > 0){
            personnelCommission = workLoadDao.getPersonnelCommission(worklist);
        }

        for (ImplementBean implementBean : content) {
            Map<String, Object> dataMap = new HashMap<>();
            int projectId = implementBean.getProjectBean().getProjectId();
            int implementId = implementBean.getImplementId();
            List<DisburseBean> disburseBeans = implementBean.getDisburseBeans();
            //项目花销
            double projectCost = 0.0;

            for (DisburseBean disburseBean : disburseBeans) {
                if (!"项目花销".equals(disburseBean.getDisburseDetailBean().getDisburseTypeBean().getDisburseTypeName())) {
                    continue;
                }
                int payStatus = disburseBean.getPaymentStatusBean() == null ? 0 : disburseBean.getPaymentStatusBean().getPaymentStatusId();
                int appStatus = disburseBean.getApprovalStatusBean() == null ? 0 : disburseBean.getApprovalStatusBean().getApprovalStatusId();
                int expense = 0;
                ExpenseAccountBean expenseAccountBean = disburseBean.getExpenseAccountBean();
                if(expenseAccountBean != null){
                    expense = expenseAccountBean.getApprovalStatusBean() == null ? 0 : expenseAccountBean.getApprovalStatusBean().getApprovalStatusId();
                }
                if (payStatus == 7 || appStatus == 2 || expense == 2) {
                    projectCost += disburseBean.getDisbursePaymentAmount() == null ? 0 : disburseBean.getDisbursePaymentAmount();
                }
            }
            //回款
            double incomeMoney = 0.0;
            //税率
            double taxRate = implementBean.getProjectBean().getManagementBean().getManagementRate() == null ? 0 : implementBean.getProjectBean().getManagementBean().getManagementRate();
            //管理费率
            double fee = implementBean.getProjectBean().getManagementBean().getManagementFee() == null ? 0 : implementBean.getProjectBean().getManagementBean().getManagementFee();
            for (Object[] incomeMoneys : implementBeanIncomeMoney) {
                if (implementBean.getImplementId().equals(incomeMoneys[0])) {
                    incomeMoney += incomeMoneys[1] == null ? 0 : (double) incomeMoneys[1];
                }
            }
            List<WorkLoadBean> workLoadBeans = implementBean.getWorkLoadBeans();

            //人员成本
            double staffCost = 0.0;
            //技术提成
            double technologyCommission = 0.0;

            //人工天数
            double artificialDays = 0.0;
            for (WorkLoadBean workLoadBean : workLoadBeans) {
                artificialDays += workLoadBean.getWorkLoadDuration() == null ? 0 : workLoadBean.getWorkLoadDuration();
                Double wage = 0.00;
                for (List list : personnelCommission) {
                    if(workLoadBean.getWorkLoadId().equals(list.get(0))){
                        wage = (list.get(1) == null) ? 0 : (Double) list.get(1);
                    }
                }
                staffCost += wage;
                technologyCommission += workLoadBean.getWorkLoadAmountStaff() == null ? 0 : workLoadBean.getWorkLoadAmountStaff();
                technologyCommission += workLoadBean.getWorkLoadAmountManage() == null ? 0 : workLoadBean.getWorkLoadAmountManage();
                technologyCommission += workLoadBean.getWorkLoadAmountCaptain() == null ? 0 : workLoadBean.getWorkLoadAmountCaptain();
            }
            List<ProductionCostsBean> productionCostsBeans = implementBean.getProductionCostsBeans();
            //设备使用费
            double plant = 0.0;
            //班组费
            double boos = 0.0;
            //其他费
            double rests = 0.0;
            for (ProductionCostsBean productionCostsBean : productionCostsBeans) {
                String productionCostsDetailType = productionCostsBean.getProductionCostsDetailBean().getProductionCostsDetailType();
                if ("设备使用费".equals(productionCostsDetailType)) {
                    plant += productionCostsBean.getProductionCostsMoney() == null ? 0 : productionCostsBean.getProductionCostsMoney();
                } else if ("班组费".equals(productionCostsDetailType)) {
                    boos += productionCostsBean.getProductionCostsMoney() == null ? 0 : productionCostsBean.getProductionCostsMoney();
                } else if ("其他费".equals(productionCostsDetailType)) {
                    rests += productionCostsBean.getProductionCostsMoney() == null ? 0 : productionCostsBean.getProductionCostsMoney();
                }
            }
            //项目花销 6
            dataMap.put("项目花销", projectCost);
            //税费 7
            dataMap.put("税费", incomeMoney * taxRate);
            //管理费 8
            dataMap.put("管理费", incomeMoney * fee);
            //人员成本 9
            dataMap.put("人员成本", staffCost);
            //技术提成 10
            dataMap.put("技术提成", technologyCommission);
            //年总奖金
            dataMap.put("年总奖金", technologyCommission * 0.3);
            //人工天数
            dataMap.put("人工天数", artificialDays);
            //设备使用 11
            dataMap.put("设备使用", plant);
            //班组费 12
            dataMap.put("班组费", boos);
            //项目id 13
            dataMap.put("项目id", projectId);
            //实施id 14
            dataMap.put("实施id", implementId);
            //其他费 15
            dataMap.put("其他费", rests);

            map.put(implementBean.getProjectBean().getProjectNum() + implementBean.getImplementId(), dataMap);
        }
        return map;
    }

    private ProductionCostsFileDao productionCostsFileDao;

    @Autowired
    public void setProductionCostsFileDao(ProductionCostsFileDao productionCostsFileDao) {
        this.productionCostsFileDao = productionCostsFileDao;
    }

    /**
     * 生产费添加
     *
     * @param implementBean 实施
     * @return CommonResult
     */
    public CommonResult addProduction(ImplementBean implementBean, Collection<Part> parts, HttpServletRequest request) {
        String delFileIds = request.getParameter("del_file_ids");
        if (delFileIds != null) {
            String split = "[$]";
            for (String s : delFileIds.split(split)) {
                try {
                    productionCostsFileDao.deleteById(Integer.parseInt(s));
                } catch (Exception e) {
                    System.out.println("删除扫描件失败");
                }
            }
        }
        Optional<ImplementBean> byId = implementDao.findById(implementBean.getImplementId());
        if (byId.isPresent()) {
            ImplementBean implementDbBean = byId.get();
            List<ProductionCostsFileBean> uploading = uploading(parts, implementDbBean);
            implementDbBean.setProductionCostsFileBeans(uploading);
            AttrExchange.onAttrExchange(implementDbBean, implementBean);
            return CommonResult.success(implementDao.save(implementDbBean));
        }
        return CommonResult.failed();
    }

    @Value("${filename}")
    private String name;

    /**
     * 文件上传的方法
     */

    public List<ProductionCostsFileBean> uploading(Collection<Part> parts, ImplementBean implementBean) {
        ArrayList<ProductionCostsFileBean> productionCostsFileBeans = new ArrayList<>();
        //遍历传过来的所有文件
        for (Part part : parts) {
            //判断传过来的文件不为空
            if (part.getSubmittedFileName() != null && !"".equals(part.getSubmittedFileName())) {
                //创建一个新的生产费上传对象
                ProductionCostsFileBean productionCostsFileBean = new ProductionCostsFileBean();
                //拿到上传的文件名称
                String oldFileName = part.getSubmittedFileName();
                //断言名称不为空
                assert oldFileName != null;
                String suffixName = oldFileName.substring(oldFileName.lastIndexOf("."));
                //拿到项目编号
                String projectNum = implementBean.getProjectBean().getProjectNum();
                //拿到实施的名称
                String departmentName = implementBean.getDepartmentBean().getDepartmentName();
                //上传后的路径
                String filePath = name + projectNum + departmentName;
                File file = new File(filePath);
                if (!file.exists()) {
                    boolean mkdirs = file.mkdirs();
                    if (mkdirs) {
                        System.out.println("文件创建成功");
                    }
                }
                try {
                    File dest = new File(file + "/" + oldFileName);
                    part.write(dest.toString());
                    productionCostsFileBean.setProductionCostsFileRelativePaths(filePath);
                    productionCostsFileBean.setProductionCostsFileName(oldFileName);
                    productionCostsFileBeans.add(productionCostsFileBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return productionCostsFileBeans;
    }
}
