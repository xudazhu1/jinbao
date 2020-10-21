package com.hnjbkc.jinbao.disburse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * @author siliqiang
 * @date 2019.8.27
 */
public interface DisburseDao extends JpaRepository<DisburseBean, Integer> {

    /**
     * 获取最大的可用流水号后四位
     *
     * @param year year
     * @return max(Serial)
     */
    @Query(value = "select max(substring(disburseNum , 6 , 5) ) from DisburseBean  where substring(disburseNum , 3 , 3 )=?1 ")
    String getMaxSerialNum(String year);

    @Query(value = "select disburseBean.disburseNum,disburseBean.disburseExpenseAccountContent from DisburseBean disburseBean where  ( disburseDetailBean.disburseDetailSource = '付款申请单' )  and ( disburseDetailBean.disburseTypeBean.disburseTypeName = '资产' )  and ( paymentStatusBean.paymentStatusId = '7' ) ")
    Object[] getPropertyNumAndVal();

    /**
     * 查询范围日期的支出总额
     *
     * @return
     */
    //@Query(value = "select sum(disburseBean.disbursePaymentAmount) from DisburseBean disburseBean join BankCardBean bankCardBean on disburseBean.bankCardBean=bankCardBean where bankCardBean.bankCardId=?1  and ( disburseBean.disburseTime>= ?2 and disburseBean.disburseTime <?3 )")
    @Query(value = "SELECT SUM(BA.bank_card_allot_bank_card_money),BA.bank_card_allot_bank_card_id ,bank.bank_card_name  FROM  bank_card_allot BA \n" +
            "                       LEFT OUTER JOIN bank_card AS bank ON BA.bank_card_allot_bank_card_id=bank.bank_card_id\n" +
            "                       LEFT OUTER JOIN disburse AS d on d.disburse_id=BA.bank_card_allot_disburse_id\n" +
            "                        WHERE d.disburse_payment_status_id='7'\n" +
            "                      GROUP BY BA.bank_card_allot_bank_card_id ", nativeQuery = true)
    List<Object[]> getSumDisburse();

    /**
     * 拿到按照日期和人分组的合计支出金额
     */
//    @Query(value = "SELECT date_format( dis.disburse_time , '%Y-%m') as dateA ,sum(dis.disburse_payment_amount),expe.expense_account_user_name\n" +
//            "FROM disburse as dis\n" +
//            "LEFT OUTER JOIN expense_account as expe ON dis.disburse_expense_account_id= expe.expense_account_id\n" +
//            "WHERE dis.disburse_affiliation='报销' AND expe.expense_account_user_name=?1\n" +
//            "GROUP BY dateA,expe.expense_account_user_name", nativeQuery = true)
//    List<Object[]> getExpenditureSum(String name);

    /**
     * 获取公司按照日期以及所属单位的总金额
     *
     * @return
     */
    @Query(value = "SELECT date_format(dis.disburse_time , '%Y-%m') as dateA,earn.earnings_company_name,SUM(dis.disburse_payment_amount)\n" +
            "FROM disburse dis\n" +
            "LEFT OUTER JOIN disburse_detail drtail ON dis.disburse_disburse_detail_id=drtail.disburse_detail_id\n" +
            "LEFT OUTER JOIN disburse_type type ON type.disburse_type_id=drtail.disburse_detail_type_id\n" +
            "LEFT OUTER JOIN disburse_category category ON category.disburse_category_id=type.disburse_type_category_id\n" +
            "LEFT OUTER JOIN earnings_company earn ON earn.earnings_company_id=dis.disburse_earnings_company_id\n" +
            "WHERE category.disburse_category_name='公司支出'\n" +
            "GROUP BY dateA  , earn.earnings_company_name", nativeQuery = true)
    List<Object[]> getStatisticsCompany();

    /**
     * 获取部门按照日期以及所属单位的总金额
     *
     * @return
     */
    @Query(value = " SELECT date_format(dis.disburse_time , '%Y-%m') as dateA,earn.earnings_company_name,SUM(dis.disburse_payment_amount),category.disburse_category_name\n" +
            " FROM disburse dis\n" +
            " LEFT OUTER JOIN disburse_detail drtail ON dis.disburse_disburse_detail_id=drtail.disburse_detail_id\n" +
            "LEFT OUTER JOIN disburse_type type ON type.disburse_type_id=drtail.disburse_detail_type_id\n" +
            " LEFT OUTER JOIN disburse_category category ON category.disburse_category_id=type.disburse_type_category_id\n" +
            " LEFT OUTER JOIN earnings_company earn ON earn.earnings_company_id=dis.disburse_earnings_company_id\n" +
            "LEFT OUTER JOIN expense_account expe ON expe.expense_account_id=dis.disburse_expense_account_id\n"+
            "WHERE dis.disburse_affiliation!='资产-折旧费'AND disburse_payment_status_id='7' OR expe.expense_account_approval_status_id='2'"+
            " GROUP BY dateA  , earn.earnings_company_name,category.disburse_category_name", nativeQuery = true)
    List<Object[]> getStatistics();

    /**
     * 统计资产所需要的数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Query(value = "SELECT * FROM disburse dis\n" +
            "LEFT OUTER JOIN disburse_detail detail ON detail.disburse_detail_id=dis.disburse_disburse_detail_id\n" +
            "LEFT OUTER JOIN disburse_type type ON type.disburse_type_id=detail.disburse_detail_type_id\n" +
            "LEFT OUTER JOIN disburse_category cate on cate.disburse_category_id=type.disburse_type_category_id\n" +
            "WHERE detail.disburse_detail_name='资产'AND dis.disburse_affiliation='付款申请单'AND dis.disburse_payment_status_id=7 limit ?1 , ?2", nativeQuery = true)
    List<DisburseBean> getProperty(Integer pageNum, Integer pageSize);

    /**
     * 统计资产所需要的数据
     *
     * @return
     */
    @Query(value = "SELECT COUNT(dis.disburse_id)  FROM disburse dis\n" +
            "LEFT OUTER JOIN disburse_detail detail ON detail.disburse_detail_id=dis.disburse_disburse_detail_id\n" +
            "LEFT OUTER JOIN disburse_type type ON type.disburse_type_id=detail.disburse_detail_type_id\n" +
            "LEFT OUTER JOIN disburse_category cate on cate.disburse_category_id=type.disburse_type_category_id\n" +
            "WHERE detail.disburse_detail_name='资产'AND dis.disburse_affiliation='付款申请单'AND dis.disburse_payment_status_id=7", nativeQuery = true)
    Long getProperty();

    /**
     * 统计每月项目支出所需要的数据
     *
     * @return
     */
    @Query(value = "SELECT  cate.disburse_category_name,detail.disburse_detail_name,e.earnings_company_name,\n" +
            "            SUM(d.disburse_payment_amount),DATE_FORMAT(d.disburse_time,'%Y-%m')  FROM disburse d\n" +
            "            LEFT OUTER JOIN earnings_company e ON e.earnings_company_id=d.disburse_earnings_company_id\n" +
            "            LEFT OUTER JOIN disburse_detail detail ON d.disburse_disburse_detail_id=detail.disburse_detail_id\n" +
            "            LEFT OUTER JOIN disburse_type type ON type.disburse_type_id=detail.disburse_detail_type_id\n" +
            "            LEFT OUTER JOIN disburse_category cate ON cate.disburse_category_id=type.disburse_type_category_id\n" +
            "            LEFT OUTER JOIN expense_account expe ON d.disburse_expense_account_id=expe.expense_account_id\n" +
            "            WHERE  DATE_FORMAT(d.disburse_time,'%Y-%m')=?1 AND\n" +
            "            cate.disburse_category_name='项目支出' AND  (disburse_payment_status_id='7' OR expe.expense_account_approval_status_id='2')\n" +
            "            GROUP BY cate.disburse_category_name,e.earnings_company_name", nativeQuery = true)
    List<Object[]> getProjectMonth(String date);

    /**
     * 统计每月公司和部门每月所支出的费用总和数据
     * @param date
     * @return
     */
    @Query(value = "SELECT  cate.disburse_category_name,type.disburse_type_name,e.earnings_company_name,\n" +
            "            SUM(d.disburse_payment_amount),DATE_FORMAT(d.disburse_time,'%Y-%m') FROM disburse d\n" +
            "           LEFT OUTER JOIN earnings_company e ON e.earnings_company_id=d.disburse_earnings_company_id\n" +
            "            LEFT OUTER JOIN disburse_detail detail ON d.disburse_disburse_detail_id=detail.disburse_detail_id\n" +
            "            LEFT OUTER JOIN disburse_type type ON type.disburse_type_id=detail.disburse_detail_type_id\n" +
            "            LEFT OUTER JOIN disburse_category cate ON cate.disburse_category_id=type.disburse_type_category_id\n" +
            "            LEFT OUTER JOIN expense_account expe ON d.disburse_expense_account_id=expe.expense_account_id\n" +
            "            WHERE  DATE_FORMAT(d.disburse_time,'%Y-%m')=?1 AND\n" +
            "            cate.disburse_category_name!='项目支出' AND  (disburse_payment_status_id='7' OR expe.expense_account_approval_status_id='2')\n" +
            "            GROUP BY cate.disburse_category_name,type.disburse_type_name", nativeQuery = true)
    List<Object[]> getDepartmentMonth(String date);

}
