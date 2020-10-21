package com.hnjbkc.jinbao.disburse.payment;

import com.hnjbkc.jinbao.disburse.DisburseBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author 12
 * @date 2019.10.22
 */
public interface PaymentDao extends JpaRepository<DisburseBean, Integer> {
    /**
     * 获取项目下的 所有 已经通过审核的 回款总金额
     * @param id 传入 Disburse 支出 id
     * @return 返回总金额
     */
    @Query(value = "select sum(ic.income_money) from disburse d\n" +
            "left join implement i on i.implement_id = d.disburse_implement_id\n" +
            "left join project p on p.project_id = i.implement_project_id\n" +
            "left join income ic on ic.income_project_id = p.project_id\n" +
            "where ic.income_audit_status = 1  and d.disburse_id = ?1",nativeQuery = true)
    Double projectCascadeIncomeMoney(Integer id);

    /**
     * 已付款的 班组费 不包含本次
     * @param id 付款id
     * @return sum 金额
     */
    @Query(value = "select sum(d.disburse_payment_amount) from project \n" +
            "left join implement i on i.implement_project_id = project_id\n" +
            "left join disburse d on d.disburse_implement_id = i.implement_id\n" +
            "where project_id = \n" +
            "(\n" +
            "select p.project_id from disburse d \n" +
            "left join implement i on implement_id = d.disburse_implement_id\n" +
            "left join project p on p.project_id = implement_project_id  \n" +
            "where disburse_id =  ?1\n" +
            ")\n" +
            "and disburse_id != ?1\n" +
            "and d.disburse_disburse_detail_id = 42 \n" +
            "and disburse_payment_status_id = 7",nativeQuery = true)
    Double paidBossCostByProjectId(Integer id);

    /**
     * 获取项目下的所有 支出
     * @param id 传入 Disburse 支出id
     * @return 返回 0支出id 1支出时间 2支出金额 3支出状态
     */
    @Query(value = "select d.disburse_id,d.disburse_time,d.disburse_payment_amount,ps.payment_status_id from project p\n" +
            "LEFT JOIN implement i on i.implement_project_id = p.project_id\n" +
            "LEFT JOIN disburse d on d.disburse_implement_id = i.implement_id\n" +
            "LEFT JOIN payment_status ps on ps.payment_status_id = d.disburse_payment_status_id\n" +
            "LEFT JOIN disburse_detail dd on dd.disburse_detail_id = d.disburse_disburse_detail_id\n" +
            "LEFT JOIN disburse_type dt on dt.disburse_type_id = dd.disburse_detail_type_id\n" +
            "where project_id = \n" +
            "(select p.project_id from disburse d\n" +
            "left join implement i on i.implement_id = d.disburse_implement_id\n" +
            "left join project p on p.project_id = i.implement_project_id\n" +
            "where  d.disburse_id = ?1 ) and (dt.disburse_type_name = ?2 ) and d.disburse_id is not null ",nativeQuery = true)
    List<List<Object>> projectDisburseAll(Integer id, String type);


    /**
     * 获取项目下的所有 支出
     * @param id 传入 Disburse 支出id
     * @return 返回 0支出id 1支出时间 2支出金额 3支出状态
     */
    @Query(value = "select d.disburse_id,d.disburse_time,d.disburse_payment_amount,ps.payment_status_id from project p\n" +
            "            LEFT JOIN implement i on i.implement_project_id = p.project_id\n" +
            "            LEFT JOIN disburse d on d.disburse_implement_id = i.implement_id\n" +
            "            LEFT JOIN payment_status ps on ps.payment_status_id = d.disburse_payment_status_id\n" +
            "            LEFT JOIN disburse_detail dd on dd.disburse_detail_id = d.disburse_disburse_detail_id\n" +
            "            where project_id = \n" +
            "            (select p.project_id from disburse d\n" +
            "            left join implement i on i.implement_id = d.disburse_implement_id\n" +
            "            left join project p on p.project_id = i.implement_project_id\n" +
            "            where  d.disburse_id = ?1 ) and (dd.disburse_detail_name = ?2 ) and d.disburse_id is not null ",nativeQuery = true)
    List<List<Object>> projectSubclassDisburseAll(Integer id, String type);
}
