package com.hnjbkc.jinbao.disburse.expenseaccount.borrowmoney;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.utils.GetMonths;
import com.hnjbkc.jinbao.utils.MyBeanUtils;
import com.hnjbkc.jinbao.utils.NextMonth;
import com.hnjbkc.jinbao.utils.tableutils.TableUtilsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author siliqaing
 * @date 2019.929
 */
@Service
public class BorrowMoneyServiceImpl implements BaseService<BorrowMoneyBean> {
    private BorrowMoneyDao borrowMoneyDao;

    @Autowired
    public void setBorrowMoneyDao(BorrowMoneyDao borrowMoneyDao) {
        this.borrowMoneyDao = borrowMoneyDao;
    }

    private TableUtilsDao tableUtilsDao;

    @Autowired
    public void setTableUtilsDao(TableUtilsDao tableUtilsDao) {
        this.tableUtilsDao = tableUtilsDao;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public BorrowMoneyBean add(BorrowMoneyBean borrowMoneyBean) {
        BorrowMoneyBean updateBorrowMoneyBean = tableUtilsDao.update(borrowMoneyBean);
        if (updateBorrowMoneyBean != null && updateBorrowMoneyBean.getBorrowMoneyNum() == null) {
            MyBeanUtils.createNumber(updateBorrowMoneyBean, "JK", "borrowMoneyNum", null);
        }
        return updateBorrowMoneyBean;
    }

    @Override
    public Boolean delete(Integer id) {
        return null;
    }

    @Override
    public BorrowMoneyBean update(BorrowMoneyBean borrowMoneyBean) {
        return null;
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

    public Map<String, Map<String, Object[]>> getBorrowMoney(String name) {
        Query nativeQuery = entityManager.createNativeQuery("SELECT date_format( br.borrow_money_date , '%Y-%m') as dateA , sum(br.borrow_money_money) , u.user_name  , pbc.personal_bank_card_initial_value,br.borrow_money_id " +
                " from borrow_money as br " +
                " LEFT OUTER JOIN personal_bank_card as pbc on pbc.personal_bank_card_id = br.borrow_money_personal_bank_card_id  " +
                " LEFT OUTER JOIN `user` as u on u.user_id = pbc.personal_bank_card_user_id  "
                + ((name == null || "".equals(name)) ? "where 1=1" : (" where  u.user_name='" + name + "' "))
                + " and  borrow_money_payment_status_id='7'"
                + " GROUP BY dateA , br.borrow_money_personal_bank_card_id ORDER BY  br.borrow_money_personal_bank_card_id  , dateA ");
        @SuppressWarnings("unchecked")
        List<Object[]> borrowRenta = nativeQuery.getResultList();

        Query nativeQuery1 = entityManager.createNativeQuery("SELECT date_format( expe.expense_account_month , '%Y-%m') as dateA ,sum(dis.disburse_payment_amount),expe.expense_account_user_name" +
                " from disburse as dis" +
                " LEFT OUTER JOIN expense_account as expe ON dis.disburse_expense_account_id= expe.expense_account_id" +
                " WHERE dis.disburse_affiliation='报销' " +
                ((name == null || "".equals(name)) ? "" : (" AND expe.expense_account_user_name='" + name + "' "))+
                "and expe.expense_account_approval_status_id='2'" +
                " GROUP BY dateA,expe.expense_account_user_name");
        List<Object[]> disburseSum = nativeQuery1.getResultList();

        //准备最外层的map ,key是名字 value是map
        Map<String, Map<String, Object[]>> outerMap = new HashMap<>(5);
        //分组之后查询出来的结果,是一个list的数组
        //遍历查询的结果
        for (Object[] objects : borrowRenta) {
            //拿到查询出来的名字
            String userName = (String) objects[2];
            //准备里层的map,key是时间 ,value是一个object的数组也就是查出来的值
            Map<String, Object[]> innerMap = outerMap.get(userName);
            if (innerMap == null) {
                innerMap = new HashMap<>(5);
            }
            innerMap.put("data" + objects[0], objects);
            innerMap.put("initMoney", new Object[]{objects[3]});
            innerMap.put("id", new Object[]{objects[4]});
            if (userName != null) {
                outerMap.put(userName, innerMap);
            }
        }

        for (Object[] objects : disburseSum) {
            String userName = (String) objects[2];
            Map<String, Object[]> stringMap = outerMap.get(userName);
            if (stringMap == null) {
                continue;
            }
            stringMap.put("bxData" + objects[0], objects);
            // stringMap.put("bxMoney", new Object[]{objects[2]});
            //拿到查询出来的名字

        }

        //获取到当前总共几个月
        String date = "2019-10-01";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM");
        Date startDate = new Date();
        try {
            startDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //拿到初始给的月份到目前为止的总共几个月
        int months = GetMonths.getMonths(startDate, new Date());
        //遍历最外层的map也就是拿到这个人
        for (Map.Entry<String, Map<String, Object[]>> stringMapEntry : outerMap.entrySet()) {
            //String key1 = stringMapEntry.getKey();
            //拿到每一个value值
            Map<String, Object[]> valueMap = stringMapEntry.getValue();
            //获取初始金额
            double initMoney = (double) valueMap.get("initMoney")[0];
            Integer id = (Integer) valueMap.get("id")[0];
            double id1 = id.doubleValue();
            //根据月份进行遍历对每一个人生成所有的月份
            for (int i = 0; i <= months; i++) {
                String month = format1.format(startDate);
                Double[] doubles = new Double[5];
                //計算出本月的值
                Object[] objects = valueMap.get("data" + month);
                if (objects == null) {
                    objects = new Object[4];
                    objects[1] = 0.00;
                }
                Double jq = (Double) objects[1];
                if (jq == null) {
                    jq = 0.00;
                }
                Object[] bxObjects = valueMap.get("bxData" + month);

                if (bxObjects == null) {
                    bxObjects = new Object[4];
                    bxObjects[1] = 0.00;
                }
                Double bx = (Double) bxObjects[1];
                if (bx == null) {
                    bx = 0.00;
                }
                //如果本月从数据库有值 , 进行计算
                doubles[0] = initMoney;
                doubles[1] = jq;
                doubles[2] = bx;
                doubles[3] = doubles[0] + doubles[1] - doubles[2];
                doubles[4] = id1;
                initMoney = doubles[3];
                valueMap.put(month, doubles);
                //本月等于下月
                startDate = NextMonth.getNextMonth(format.format(startDate));
                Date date1 = new Date();
                String date2 = format.format(date1);
                if (startDate.getTime() > date1.getTime()) {
                    try {
                        startDate = format1.parse("2019-10");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            //去除最初给的初始值和当月的借款金额
            valueMap.keySet().removeIf(key -> key.startsWith("data") || "initMoney".equals(key) || key.startsWith("bxData") || key.startsWith("id"));

        }
        //根据里层的map的时间进行排序
        for (Map.Entry<String, Map<String, Object[]>> stringMapEntry : outerMap.entrySet()) {
            Map<String, Object[]> value = stringMapEntry.getValue();
            Map<String, Object[]> sortedMap = new TreeMap<>((o1, o2) -> {
                SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM");
                try {
                    Date parse1 = format2.parse(o1);
                    Date parse2 = format2.parse(o2);
                    return parse1.getTime() > parse2.getTime() ? -1 : 1;
                } catch (ParseException e) {
                    return 0;
                }

            });
            sortedMap.putAll(value);
            outerMap.put(stringMapEntry.getKey(), sortedMap);
        }
        return outerMap;

    }

}
