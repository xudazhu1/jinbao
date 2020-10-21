package com.hnjbkc.jinbao.disburse.expenseaccount;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.disburse.DisburseBean;
import com.hnjbkc.jinbao.disburse.expenseaccount.borrowmoney.BorrowMoneyServiceImpl;
import com.hnjbkc.jinbao.utils.CancellationUtil;
import com.hnjbkc.jinbao.utils.MyBeanUtils;
import com.hnjbkc.jinbao.utils.tableutils.TableUtilsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author siliqiang
 * @date 2019.8.29
 */
@Service
public class ExpenseAccountServiceImpl implements BaseService<ExpenseAccountBean> {

    private ExpenseAccountDao expenseAccountDao;

    @Autowired
    public void setExpenseAccountDao(ExpenseAccountDao expenseAccountDao) {
        this.expenseAccountDao = expenseAccountDao;
    }
    private TableUtilsDao tableUtilsDao;

    @Autowired
    public void setTableUtilsDao(TableUtilsDao tableUtilsDao) {
        this.tableUtilsDao = tableUtilsDao;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ExpenseAccountBean add(ExpenseAccountBean expenseAccountBean) {
//        List<DisburseBean> disburseBeans = expenseAccountBean.getDisburseBeans();
//        disburseBeans.forEach(disburseBean -> disburseBean.setExpenseAccountBean(expenseAccountBean));
//        return expenseAccountDao.save(expenseAccountBean);
        ExpenseAccountBean updateExpenseAccountBean = tableUtilsDao.update(expenseAccountBean);
        if (updateExpenseAccountBean != null ) {
            if ( updateExpenseAccountBean.getExpenseAccountNum() == null ||  "".equals(updateExpenseAccountBean.getExpenseAccountNum()) )  {
                MyBeanUtils.createNumber(updateExpenseAccountBean, "BX", "expenseAccountNum", "disburseBeans.disburseNum");
            } else {
                //如果子编号不全 修补子编号
                boolean need = false;
                if ( updateExpenseAccountBean.getDisburseBeans() != null ) {
                    for (DisburseBean disburseBean : updateExpenseAccountBean.getDisburseBeans()) {
                        if (disburseBean.getDisburseNum() == null || disburseBean.getDisburseNum().length() < updateExpenseAccountBean.getExpenseAccountNum().length()) {
                            need = true;
                            break;
                        }
                    }
                }
                if ( need ) {
                    MyBeanUtils.createNumber(updateExpenseAccountBean, "BX", "expenseAccountNum", "disburseBeans.disburseNum");
                }
            }
        }
        return updateExpenseAccountBean;

    }

    @Override
    public Boolean delete(Integer id) {
        return null;
    }

    @Override
    public ExpenseAccountBean update(ExpenseAccountBean expenseAccountBean) {
        Optional<ExpenseAccountBean> byId = expenseAccountDao.findById(expenseAccountBean.getExpenseAccountId());
        if (!byId.isPresent()) {
            return null;
        }
        return null;
    }

    Map<String, List<Object[]>> getPrint(Integer id) {
        //根据id查询
        Optional<ExpenseAccountBean> byId = expenseAccountDao.findById(id);
        //判断查出来的是否为空,如果为空则返回一个空的map对象
        if (!byId.isPresent()) {
            return new HashMap<>(0);
        }
        ExpenseAccountBean expenseAccountBean = byId.get();
        //准备一个map对象
        HashMap<String, List<Object[]>> stringListHashMap = new HashMap<>(5);
        //判断报销类型是不是属于项目报销,如果属于项目报销的话调用项目报销的dao,不是的话则调用其他类型报销统计的dao
        List<Object[]> print;
        String type = "项目报销";
        if (expenseAccountBean.getExpenseAccountType().equals(type)) {
            print = expenseAccountDao.getProjectPrint(id);
        } else {
            print = expenseAccountDao.getPrint(id);
        }
        //对查出来的list进行一个遍历
        for (Object[] objects : print) {
            //根据objects第二个下标作为key去查有没有这个value值 (第二个下标即为收益单位)
            String unit = (String) objects[2];
            List<Object[]> list = stringListHashMap.get(unit);
            //创建一个长度为7的object数组
            Object[] object = new Object[7];
            //给每对应的下标数组赋值
            object[0] = objects[0];
            object[1] = objects[1];
            object[2] = objects[3];
            object[3] = objects[4];
            object[4] = objects[5];
            object[5] = objects[6];
            object[6] = objects[7];
            //如果根据收益单位没有找到对应的list即创建一个新的list
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(object);
            //将处理完的数据put到map中
            stringListHashMap.put((String) objects[2], list);
        }
        //返回这个map对象
        return stringListHashMap;
    }

    /**
     * 作废的方法
     * @param expenseAccountBean
     * @return
     */
    public ExpenseAccountBean obsolete(ExpenseAccountBean expenseAccountBean){
        ExpenseAccountBean obsolete = CancellationUtil.obsolete(expenseAccountBean);
       return tableUtilsDao.update(obsolete);
    }

    private BorrowMoneyServiceImpl borrowMoneyService;

    @Autowired
    public void setBorrowMoneyService(BorrowMoneyServiceImpl borrowMoneyService) {
        this.borrowMoneyService = borrowMoneyService;
    }

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 获取个人的报销月份统计
     * @param userName  姓名
     * @param date 年月 ( yyyy-MM )
     * @return 数据 { }
     */
    Object getStatistics4MonthAndUser(String userName, String date) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        //拿到数据
        String hql = "select " +
                "  ( case when dis.departmentBean.departmentName is null then dis.disburseAttributionDepartment else  dis.departmentBean.departmentName end ) as dept , " +
                "dis.expenseAccountBean.expenseAccountType , " +
                "sum( dis.disbursePaymentAmount ) , " +
                "sum(dis.disburseInvoiceMoney) , " +
                "dis.earningsCompanyBean.earningsCompanyName " +
                "from DisburseBean dis " +
                "left join  dis.expenseAccountBean " +
                "left join  dis.earningsCompanyBean " +
                "left join  dis.departmentBean " +
                "left join  dis.expenseAccountBean.approvalStatusBean " +
                "where date_format( dis.expenseAccountBean.expenseAccountMonth , '%Y-%m' ) = '" + date + "' " +
                "and  dis.expenseAccountBean.expenseAccountUserName   = '" + userName + "' " +
                "and  dis.expenseAccountBean.approvalStatusBean.approvalStatusId = 2    " +
                "group by dis.disburseAttributionDepartment , dis.departmentBean.departmentName , dis.expenseAccountBean.expenseAccountType , dis.earningsCompanyBean.earningsCompanyName ";
        Query query = entityManager.createQuery(hql);
        List resultList = query.getResultList();

        ArrayList<Object> objects1 = new ArrayList<>();
        //发票总计
        double countInvoice = 0;
        //报销总计
        double countExpense = 0;
        //准备报销数据
        for (Object o : resultList) {
            Object [] objects = (Object[]) o;
            HashMap<String, Object> objectObjectHashMap = new HashMap<>(5);
            objectObjectHashMap.put("department" , objects[0] );
            objectObjectHashMap.put("content" , objects[1] );
            objectObjectHashMap.put("expenseMoney" , objects[2] );
            countExpense += objects[2] == null ? 0 : (Double) objects[2] ;
            objectObjectHashMap.put("invoiceMoney" , objects[3] );
            countInvoice += (Double) objects[3];
            objectObjectHashMap.put("earningsCompany" , objects[4] );
            objects1.add(objectObjectHashMap);
        }

        //拿到月初金额, 借款金额, 结算金额
        Map<String, Map<String, Object[]>> borrowMoney = borrowMoneyService.getBorrowMoney(userName);
        Map<String , Object> map = new HashMap<>(3);
        //准备借款数据
        if ( borrowMoney.size() > 0 ) {
            Map<String , Object[] > treeMap =  new HashMap(5);
            treeMap.putAll( borrowMoney.get(userName) );
            if ( treeMap.containsKey( date ) ) {
                Double []  doubles = (Double[]) treeMap.get(date);
                map.put("lastMonth" , doubles[0]);
                map.put("borrowing" , doubles[1]);
                map.put("theMonth" , doubles[0] + doubles[1] - countExpense );
            } else {
                map.put("lastMonth" , 0);
                map.put("borrowing" , 0);
                map.put("theMonth" , 0);
            }
        } else {
            map.put("lastMonth" , 0);
            map.put("borrowing" , 0);
            map.put("theMonth" , 0);
        }

        map.put("countInvoice" , countInvoice);
        map.put("countExpense" , countExpense);
        map.put("data" , objects1);


        return map;
    }
}
