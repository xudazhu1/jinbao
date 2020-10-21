package com.hnjbkc.jinbao.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * @author 12
 * @Date 2019-08-06
 */
@Repository
@Transactional(rollbackOn = Exception.class)
public interface ProjectDao extends JpaRepository<ProjectBean, Integer> {

    /**
     * @return 返回 对应收益单位 编号的 最大编号
     */
    @Query(value = "select max(substring(project_num , 9 , 5))as num from project",nativeQuery = true)
    String findMaxNumber();

    /**
     * 判断当前 编号是否存在(弃用)
     *
     * @param year 时间 19
     * @param num  编号 0394
     * @return 项目对象
     */
    @Query(value = "from ProjectBean where substring(projectNum,4,2) = ?1 and substring(projectNum,9,4) = ?2")
    ProjectBean findNumberExist(String year, String num);

    /**
     * 通过项目名称查找项目
     *
     * @param projectName 项目名称
     * @return 返回集合
     */
    List<ProjectBean> findAllByProjectName(String projectName);

    /**
     * 获取所有的 id,项目编号，项目名称
     *
     * @return 返回一个map 封装 ProjectBean 对象 id 属性项目编号和项目名称
     */
    @Query(value = "select project_id as projectId , project_num as projectNum , project_name as projectName  from project;", nativeQuery = true)
    List<Map<String, Object>> findAllProjectNumAndProjectName();

    /**
     * 根据项目编号分组
     *
     * @return
     */
    @Query(value = "SELECT p.project_num,p.project_name,q.quote_money,p.project_belongs_type FROM project p \n" +
            "LEFT OUTER JOIN quote q ON q.quote_id=p.project_quote_id limit ?1 , ?2", nativeQuery = true)
    List<Object[]> getProjectNumAndName(Integer pageNum, Integer pageSize);

    /**
     * 根据项目编号分组
     *
     * @return
     */
    @Query(value = "SELECT count(p.project_num) FROM project p \n" +
            "LEFT OUTER JOIN quote q ON q.quote_id=p.project_quote_id ", nativeQuery = true)
    Long getProjectNumAndName();

    /**
     * 获取收入金额
     *
     * @param list 传入项目Id list
     * @return 返回数组
     */
    @Query(value = "select \n" +
            "p.project_id,\n" +
            "sum( case when i.income_audit_status = 1 then i.income_money else 0 end ) ,\n" +
            "sum( case when ic.invoice_audit_status = 1 then ic.invoice_money else 0 end ) \n" +
            "from project p\n" +
            "            left join income i ON p.project_id = i.income_project_id\n" +
            "            left join contract c on c.contract_project_id = p.project_id\n" +
            "            left join invoice ic on ic.invoice_id = i.income_invoice_id\n" +
            "            where p.project_id in(?1)  GROUP BY p.project_id", nativeQuery = true)
    List<List> findSchedule4ProjectIdList(List list);

    /**
     * 预估收入需要的数据
     *
     * @param list
     * @return
     */
    @SuppressWarnings("SqlNoDataSourceInspection")
    @Query(value = "select a.project_belongs_type, \n" +
            "                              a.management_audit_amount,\n" +
            "                              a.management_goods_evaluation_amount,\n" +
            "                              a.management_settlement_amount,\n" +
            "                              a.contract_money,\n" +
            "                              a.quote_money,\n" +
            "(case when  co='需要算' then \n" +
            "sum(bi/bili * implement_progress )\n" +
            "else SUM(co) end ),\n" +
            "                              a.project_id,\n" +
            "                              a.contract_estimate_money \n" +
            "                       from (\n" +
            "select p.project_belongs_type,\n" +
            "                                    m.management_audit_amount,\n" +
            "                                    m.management_goods_evaluation_amount,\n" +
            "                                    m.management_settlement_amount,\n" +
            "                                    c.contract_money,\n" +
            "                                    q.quote_money,\n" +
            "                                    i.implement_progress,\n" +
            "(case when dept.department_name='勘察部' then 7\n" +
            "when dept.department_name='测量部' then 3 \n" +
            "when dept.department_name='咨询部' then 3  end ) as bi,\n" +
            "dept.department_name,\n" +
            "                                    p.project_id,\n" +
            "                                    c.contract_estimate_money,\n" +
            "                                    cd.contract_department_money_distribution_money,\n" +
            "(\n" +
            "case when c.contract_state ='不需签' OR c.contract_state='未签' then   '需要算'  else (cd.contract_department_money_distribution_money/c.contract_money * i.implement_progress) end ) as co , \n" +
            "sumbi.bili FROM project  p\n" +
            "                                              LEFT OUTER JOIN contract c ON c.contract_project_id=p.project_id\n" +
            "                                              LEFT OUTER JOIN management m ON m.management_project_id=p.project_id\n" +
            "                                              LEFT OUTER JOIN quote q ON p.project_quote_id=q.quote_id \n" +
            "                                              LEFT OUTER JOIN contract_department_money cd ON cd.contract_department_money_contract_id=c.contract_id\n" +
            "                                              LEFT OUTER JOIN implement i on cd.contract_department_money_implement_department_id = i.implement_id\n" +
            "LEFT OUTER JOIN department dept on i.implement_department_id = dept.department_id\n" +
            "                                               , ( \n" +
            "select \n" +
            "pA.project_id , \n" +
            "sum(case when deptA.department_name='勘察部' then 7  \n" +
            "when deptA.department_name='测量部' then 3 \n" +
            "when deptA.department_name='咨询部' then 3  end ) as bili\n" +
            "FROM project  pA\n" +
            "LEFT OUTER JOIN contract cA ON cA.contract_project_id=pA.project_id\n" +
            "LEFT OUTER JOIN management mA ON mA.management_project_id=pA.project_id\n" +
            "LEFT OUTER JOIN quote qA ON pA.project_quote_id=qA.quote_id \n" +
            "LEFT OUTER JOIN contract_department_money cdA ON cdA.contract_department_money_contract_id=cA.contract_id\n" +
            "LEFT OUTER JOIN implement iA on cdA.contract_department_money_implement_department_id = iA.implement_id\n" +
            "LEFT OUTER JOIN department deptA on iA.implement_department_id = deptA.department_id\n" +
            "WHERE pA.project_id in(?1) \n" +
            "GROUP BY pA.project_id\n" +
            " ) as sumbi WHERE p.project_id =sumbi.project_id\n" +
            ") a GROUP BY  a.project_id,a.project_belongs_type", nativeQuery = true)
    List<Object[]> getEstimatedIncome(List list);

    @Query(value = "select count(*) from project", nativeQuery = true)
    Long getCount();
}
