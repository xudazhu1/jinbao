package com.hnjbkc.jinbao.productioncosts;

import com.hnjbkc.jinbao.disburse.DisburseBean;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.implement.ImplementBean;
import com.hnjbkc.jinbao.invoiceandmoneyback.income.IncomeBean;
import com.hnjbkc.jinbao.management.ManagementBean;
import com.hnjbkc.jinbao.project.ProjectBean;
import com.hnjbkc.jinbao.project.ProjectDao;
import com.hnjbkc.jinbao.workload.WorkLoadBean;
import com.hnjbkc.jinbao.workload.post.PostBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;

import javax.persistence.*;
import java.util.*;

/**
 * @author 12
 * @Date 2019-09-29
 */
@Service
public class ProductionCostsServiceImpl implements BaseService<ProductionCostsBean> {

    private ProductionCostsDao productionCostsDao;

    @Autowired
    public void setProductionCostsDao(ProductionCostsDao productionCostsDao ) {
        this.productionCostsDao  = productionCostsDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public ProductionCostsBean add(ProductionCostsBean productionCostsBean){
        return productionCostsDao.save(productionCostsBean);
    }

    @Override
    public Boolean delete(Integer id){
        productionCostsDao.deleteById(id);
        return true;
    }

    @Override
    public ProductionCostsBean update(ProductionCostsBean productionCostsBean){
        return productionCostsDao.save(productionCostsBean);
    }

    public Map<String,Object> getProductionInfo(Integer id) {
        Map<String,Object> map = new HashMap<>(5);
        Double projectDisburse = getProjectDisburse(id);
        Double taxes = getIncomeTaxesOrManagementCost(id, 0);
        Double managementCost = getIncomeTaxesOrManagementCost(id, 1);
        Double staffCost = getWorkCost(id, 0);
        Double technologyCommission = getWorkCost(id, 1);
        map.put("项目花销",projectDisburse);
        map.put("税费",taxes);
        map.put("管理费",managementCost);
        map.put("人员成本",staffCost);
        map.put("技术提成",technologyCommission);
        return map;
    }

    /**
     * 获取 项目支出
     * @param id 传入 实施部Id
     * @return 返回费用
     */
    public Double getProjectDisburse(Integer id){
        double moeny = 0.0;
        String hql = " select d from DisburseBean d " +
                "left join  d.implementBean  " +
                "left join d.disburseDetailBean.disburseTypeBean  " +
                "left join d.paymentStatusBean  " +
                "left join d.approvalStatusBean  " +
                "where" +
                " d.disburseDetailBean.disburseTypeBean.disburseTypeName = '项目花销' " +
                "and ( d.paymentStatusBean.paymentStatusId = 7 or d.approvalStatusBean.approvalStatusId = 2 ) " +
                "and d.implementBean.implementId = "+id;
        List<DisburseBean> list = (List<DisburseBean>)sqlUtilsDao.exSqlCustom(DisburseBean.class, hql, null);
        for (DisburseBean disburseBean : list) {
            moeny += disburseBean.getDisbursePaymentAmount() == null ? 0 :disburseBean.getDisbursePaymentAmount();
            moeny += disburseBean.getDisburseExpenseAccountMoney() == null ? 0 :disburseBean.getDisburseExpenseAccountMoney();
        }
        return moeny;
    }

    public Page<ImplementBean> getProjectDisburse(Collection<String> list){
        return null;
    }

    /**
     * 获取 税费 或者 管理费
     * @param id 传入 实施 Id
     * @param type 传入 0||1  0税费  1管理费
     * @return 返回费用
     */
    public double getIncomeTaxesOrManagementCost(Integer id , Integer type){
        double moeny = 0.0;
        String hql = "select ic FROM IncomeImplMoneyBean im " +
                "LEFT JOIN im.implementBean i " +
                "LEFT JOIN im.incomeBean ic " +
                "where ic.incomeAuditStatus = '1' and i.implementId = " + id;
        List<IncomeBean> list = (List<IncomeBean>)sqlUtilsDao.exSqlCustom(IncomeBean.class, hql, null);
        for (IncomeBean incomeBean : list) {
            if(type == 0){
                double managementRate = incomeBean.getProjectBean().getManagementBean().getManagementRate()
                        == null ? 0 : incomeBean.getProjectBean().getManagementBean().getManagementRate();
                double incomeCountMoneyBackMoney = incomeBean.getIncomeCountMoneyBackMoney() == null ? 0 : incomeBean.getIncomeCountMoneyBackMoney();
                moeny += (managementRate * incomeCountMoneyBackMoney);
            }
            if(type == 1){
                double incomeCountMoneyBackMoney = incomeBean.getIncomeCountMoneyBackMoney() == null ? 0 : incomeBean.getIncomeCountMoneyBackMoney();
                moeny += (0.03 * incomeCountMoneyBackMoney);
            }

        }
        return moeny;
    }

    public Map<String, Object> getProjectAndProductionInfo(Map<String, Object> propMap) {
        String hql = "select p from ProjectBean p " +
                "inner join ImplementBean i " +
                "on i.projectBean.projectId = p.projectId";
        //设备使用费
        Double equipmentMoneySum = 0.0;
        //班组费
        Double bossMoneySum = 0.0;
        //技术提成
        Double technologyCommission = 0.0;
        List<ProjectBean> list = (List<ProjectBean>) sqlUtilsDao.exSqlCustom(ProjectBean.class, hql, null);
        for (ProjectBean projectBean : list) {
            List<ImplementBean> implementBeans = projectBean.getImplementBeans();
            for (ImplementBean implementBean : implementBeans) {
                List<ProductionCostsBean> productionCostsBeans = implementBean.getProductionCostsBeans();
                for (ProductionCostsBean productionCostsBean : productionCostsBeans) {
                    if("设备使用费".equals(productionCostsBean.getProductionCostsDetailBean().getProductionCostsDetailType())){
                        Double productionCostsMoney = productionCostsBean.getProductionCostsMoney();
                        equipmentMoneySum += productionCostsMoney;
                    }else if("班组费".equals(productionCostsBean.getProductionCostsDetailBean().getProductionCostsDetailType())){
                        Double productionCostsMoney = productionCostsBean.getProductionCostsMoney();
                        bossMoneySum += productionCostsMoney;
                    }

                }
                List<WorkLoadBean> workLoadBeans = implementBean.getWorkLoadBeans();
                for (WorkLoadBean workLoadBean : workLoadBeans) {
                    Double workLoadAmountStaff = workLoadBean.getWorkLoadAmountStaff();
                    Double workLoadAmountManage = workLoadBean.getWorkLoadAmountManage();
                    Double workLoadAmountCaptain = workLoadBean.getWorkLoadAmountCaptain();
                    workLoadAmountStaff = workLoadAmountStaff == null ? 0 : workLoadAmountStaff;
                    workLoadAmountManage = workLoadAmountManage == null ? 0 : workLoadAmountManage;
                    workLoadAmountCaptain = workLoadAmountCaptain == null ? 0 : workLoadAmountCaptain;
                    technologyCommission += (workLoadAmountStaff + workLoadAmountManage + workLoadAmountCaptain);
                }
            }
        }
        System.out.println(equipmentMoneySum);
        System.out.println(bossMoneySum);
        System.out.println(technologyCommission);
        return null;
    }

    /**
     *
     * @param id 传入实施 id 获取 人员成本/技术提成
     * @param type 如果传入0 则 是人员成本 如果是 1则是 技术提成
     * @return 返回费用
     */
    public Double getWorkCost(Integer id , Integer type){
        double moeny = 0.0;
        String hql = "select wl FROM WorkLoadBean wl " +
                "LEFT JOIN wl.implementBean i " +
                "LEFT JOIN wl.workLoadStatusBean ws " +
                "where ws.workLoadStatusId != 3 and  i.implementId = " + id;
        List<WorkLoadBean> list = (List<WorkLoadBean>)sqlUtilsDao.exSqlCustom(WorkLoadBean.class, hql, null);
        for (WorkLoadBean workLoadBean : list) {
            if(type == 0){
                moeny += workLoadBean.getWorkLoadLaborCost() == null ? 0 : workLoadBean.getWorkLoadLaborCost();
            }else if(type == 1){
                moeny += workLoadBean.getWorkLoadAmountStaff() == null ? 0 : workLoadBean.getWorkLoadAmountStaff();
                moeny += workLoadBean.getWorkLoadAmountManage() == null ? 0 : workLoadBean.getWorkLoadAmountManage();
                moeny += workLoadBean.getWorkLoadAmountCaptain() == null ? 0 : workLoadBean.getWorkLoadAmountCaptain();
            }
        }
        return moeny;
    }

    /**
     *
     * @param id 传入实施部Id 获取 他的 设备使用费 和 班组费
     * @param type  0 是设备使用费  1  是班组费
     * @return
     */
    public double getProductionSum(Integer id, Integer type){
        double moeny = 0.0;
        String hql = "select pc FROM ProductionCostsBean pc " +
                "LEFT JOIN pc.implementBean i " +
                "where i.implementId = " + id;
        List<ProductionCostsBean> list = (List<ProductionCostsBean>)sqlUtilsDao.exSqlCustom(ProductionCostsBean.class, hql, null);
        for (ProductionCostsBean productionCostsBean : list) {
            if(type == 0){
                if ("设备使用费".equals(productionCostsBean.getProductionCostsDetailBean().getProductionCostsDetailType())){
                    moeny += productionCostsBean.getProductionCostsMoney() == null ? 0 : productionCostsBean.getProductionCostsMoney();;
                }
            }else if(type == 1){
                if ("班组费".equals(productionCostsBean.getProductionCostsDetailBean().getProductionCostsDetailType())){
                    moeny += productionCostsBean.getProductionCostsMoney() == null ? 0 : productionCostsBean.getProductionCostsMoney();;
                }
            }
        }
        return moeny;
    }
    public Object findAll(Map<String, Object> propMap) {
        String pageNum = (String)propMap.get("pageNum");
        String pageSize = (String)propMap.get("pageSize");
        List<Object> implementIdList = new ArrayList<>();
        List<List<Object>> projectAndProduction = productionCostsDao.findProjectAndProduction(Integer.parseInt(pageNum)-1,Integer.parseInt(pageSize));
        for (List<Object> list : projectAndProduction) {
            Integer implementId = (Integer)list.get(5);
            System.out.println(implementId);
            implementIdList.add(implementId);
        }
        return projectAndProduction;
    }

    public List<Map<String, Object>> getCommissionByProjectId(String id) {
        String hql = "select new Map(" +
                "pc.productionCostsId as productionCostsId,"+
                "pc.productionCostsMoney as productionCostsMoney, "+
                "pc.squadGroupFeeBean.squadGroupFeeName as squadGroupFeeName, "+
                "pc.productionCostsDay as productionCostsDay, "+
                "pc.implementBean.departmentBean.departmentName as departmentName, "+
                "pc.productionCostsDetailBean.productionCostsDetailName as productionCostsDetailName, "+
                "pc.productionCostsDetailBean.productionCostsDetailType as productionCostsDetailType, "+
                "pc.productionCostsRemark as productionCostsRemark "+
                ") "+
                "from ProductionCostsBean pc " +
                "left join pc.squadGroupFeeBean " +
                "left join pc.implementBean " +
                "left join pc.implementBean.departmentBean " +
                "left join pc.implementBean.projectBean " +
                "left join pc.productionCostsDetailBean " +
                "where pc.implementBean.projectBean.projectId = " + id
                ;
        List<Map<String, Object>> list = sqlUtilsDao.exHqlResultMap(ProductionCostsBean.class, hql);
        return list;
    }
}
