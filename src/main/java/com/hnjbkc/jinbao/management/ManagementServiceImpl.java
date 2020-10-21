package com.hnjbkc.jinbao.management;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.contract.ContractDao;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.invoiceandmoneyback.income.IncomeDao;
import com.hnjbkc.jinbao.project.ProjectDao;
import com.hnjbkc.jinbao.utils.MyBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xudaz
 */
@Service
public class ManagementServiceImpl implements BaseService<ManagementBean> {

    private ManagementDao managementDao;

    @Autowired
    public void setManagementDao(ManagementDao managementDao) {
        this.managementDao = managementDao;
    }

    @Override
    public ManagementBean add(ManagementBean managementBean) {
        if (managementBean != null && managementBean.getManagementId() != null) {
            throw new RuntimeException("请使用update方法");
        }
        assert managementBean != null;
        return managementDao.save(managementBean);

    }

    @Override
    public Boolean delete(Integer id) {
        managementDao.deleteById(id);
        return true;
    }

    @Override
    public ManagementBean update(ManagementBean managementBean) {
        if (managementBean.getManagementId() == null) {
            throw new RuntimeException("请使用add方法");
        }
        return managementDao.save(managementBean);

    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    /**
     * 查询的方法(模糊)
     *
     * @param propMap     前端传过来的分页参数
     * @param pageRequest p
     * @return r
     */
    public Page<ManagementBean> search(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.searchAllByCustomProps(ManagementBean.class, propMap, pageRequest);
    }

    /**
     * 查询的方法 (非模糊)
     *
     * @param propMap     前端传过来的分页参数
     * @param pageRequest p
     * @return r
     */
    public Page<ManagementBean> get(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.getAllByCustomProps(ManagementBean.class, propMap, pageRequest);
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    List getSingleProperty(String property) {
        return sqlUtilsDao.getSingleProperties(ManagementBean.class, property);
    }

    /**
     * 获取内部&外部合伙人
     *
     * @return r
     */
    Map<String, Object> getPartners(String tableNames) {
        String split = "[$]";
        String[] split1 = tableNames.split(split);
        Map<String, Object> dataMap = new HashMap<>(split1.length);
        for (String tableName : split1) {
            Class bean4TableName = MyBeanUtils.getBean4TableName(tableName);
            assert bean4TableName != null;
            @SuppressWarnings("unchecked")
            Page allManagementInnerPartnerBeans = sqlUtilsDao.getAllByCustomProps(
                    bean4TableName,
                    new HashMap<>(0), PageRequest.of(0, Integer.MAX_VALUE));
            Table annotation = (Table) bean4TableName.getAnnotation(Table.class);
            assert annotation != null;
            dataMap.put(annotation.name(), allManagementInnerPartnerBeans.getContent());
        }
        return dataMap;
    }

    /**
     * 获取预估收入表
     */

    private ProjectDao projectDao;

    @Autowired
    public void setProjectDao(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    private ContractDao contractDao;

    @Autowired
    public void setContractDao(ContractDao contractDao) {
        this.contractDao = contractDao;
    }

    private IncomeDao incomeDao;

    @Autowired
    public void setIncomeDao(IncomeDao incomeDao) {
        this.incomeDao = incomeDao;
    }

    /**
     * 得到预估收入
     */
    public Page<Map<String, Object>> getEstimatedIncome( Integer pageNum , Integer pageSize ) {
        ArrayList<Map<String, Object>> estimatedIncome = new ArrayList<>();
        List<Object[]> projectNumAndName = projectDao.getProjectNumAndName(  (pageNum - 1) * pageSize , pageSize  );
        List<Object[]> managementMoney = managementDao.getManagementMoney();
        List<Object[]> contractMoney = contractDao.getContractMoney();
        List<Object[]> incomeMoney = incomeDao.getIncomeMoney();

        //处理汇款金额
        HashMap<Object, Object[]> incomeMoneyMap = new HashMap<>();
        for (Object[] objects : incomeMoney) {
            incomeMoneyMap.put(objects[0], objects);
        }

        HashMap<String, List<Object[]>> contractMoneyMap = new HashMap<>();
        for (Object[] objects : contractMoney) {
            List<Object[]> objects2 = contractMoneyMap.get((String) objects[0]);
            if (objects2 == null) {
                objects2 = new ArrayList<>();
            }
            objects2.add(objects);
            contractMoneyMap.put((String) objects[0], objects2);

        }

        HashMap<String, Object[]> auditMap = new HashMap<>();
        for (Object[] objects : managementMoney) {
            auditMap.put((String) objects[0], objects);
        }
        for (Object[] objects : projectNumAndName) {
            HashMap<String, Object> objectObjectHashMap = new HashMap<>();
            objectObjectHashMap.put("项目编号", objects[0]);
            objectObjectHashMap.put("项目名称", objects[1]);
            objectObjectHashMap.put("报价金额", objects[2]);
            objectObjectHashMap.put("项目类型", objects[3]);

            objectObjectHashMap.put("审计金额", auditMap.get(objects[0])[1]);
            objectObjectHashMap.put("财评金额", auditMap.get(objects[0])[2]);
            objectObjectHashMap.put("预算金额", auditMap.get(objects[0])[3]);
            //处理合同金额
            List<Object[]> objects1 = contractMoneyMap.get((String) objects[0]);
            Double contMoney = (Double) objects1.get(0)[3];
            if (contMoney == null) {
                contMoney = 0.00;
            }
            objectObjectHashMap.put("合同金额", contMoney);
            //处理分配金额 和 部门
            List<Double> implMoney = new ArrayList<>();
            List<String> implDeptName = new ArrayList<>();
            List<Double> implProgressName = new ArrayList<>();
            for (Object[] objects2 : objects1) {
                implMoney.add((Double) objects2[2]);
                implDeptName.add((String) objects2[1]);
                implProgressName.add((Double) objects2[4]);

            }
            objectObjectHashMap.put("分配部门", implDeptName);
            objectObjectHashMap.put("分配金额", implMoney);
            objectObjectHashMap.put("部门进度", implProgressName);
            if (incomeMoneyMap.get(objects[0]) == null) {
                objectObjectHashMap.put("已回款金额", 0.00);
            } else {
                objectObjectHashMap.put("已回款金额", incomeMoneyMap.get(objects[0])[1]);

            }
            estimatedIncome.add(objectObjectHashMap);
        }

        return new PageImpl<>(estimatedIncome ,PageRequest.of( pageNum - 1 , pageSize ) ,projectDao.getProjectNumAndName() );
    }

}
