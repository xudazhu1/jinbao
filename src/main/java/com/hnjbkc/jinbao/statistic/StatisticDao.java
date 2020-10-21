package com.hnjbkc.jinbao.statistic;

import com.hnjbkc.jinbao.project.ProjectBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author 12
 * @Date 2019-09-21
 */
@Repository
public interface StatisticDao extends JpaRepository<ProjectBean,Integer> {

    /**
     * 立项图表统计
     */
    @Query(value = "SELECT \n" +
            "DATE_FORMAT(p.project_approval_time, \"%Y-%m-%d\" ) AS timerange, \n" +
            "d.department_name As char_name ,\n" +
            "COUNT(d.department_id) AS char_count \n" +
            "FROM project p \n" +
            "LEFT JOIN implement i on p.project_id = i.implement_project_id\n" +
            "LEFT JOIN department d on d.department_id = i.implement_department_id\n" +
            "where p.project_approval_time >= ?1 and p.project_approval_time<= ?2\n" +
            "GROUP BY DATE_FORMAT( p.project_approval_time, \"%Y-%m-%d\" ),d.department_name ",nativeQuery = true)
    List<Map<String,Object>> projectApproval(String begin,String emd);

    /**
     * 合同签订时间图表
     * @param begin
     * @param emd
     * @return
     */
    @Query(value = "SELECT\n" +
            "DATE_FORMAT(p.project_approval_time, '%Y-%m-%d' ) AS timerange,  \n" +
            "d.department_name As char_name , \n" +
            "COUNT(d.department_id) AS char_count  \n" +
            "FROM project p  \n" +
            "LEFT JOIN implement i on p.project_id = i.implement_project_id \n" +
            "LEFT JOIN contract c on p.project_id = c.contract_project_id\n" +
            "LEFT JOIN department d on d.department_id = i.implement_department_id \n" +
            "where p.project_approval_time >= '2018-10-02 00:00:00' and p.project_approval_time<= '2019-10-19 00:00:00' \n" +
            "and c.contract_state = '已签'\n" +
            "GROUP BY DATE_FORMAT( p.project_approval_time, '%Y-%m-%d' ),d.department_name ",nativeQuery = true)
    List<Map<String,Object>> contractSigning(String begin,String emd);

    /**
     * 获取部门分组
     * @return
     */
    @Query(value = "select d.department_name from project p \n" +
            "left join implement i on p.project_id = i.implement_project_id\n" +
            "left join department d on implement_department_id = d.department_id\n" +
            "GROUP BY d.department_name",nativeQuery = true)
    List<String> getDepartmentGroup();

    /**
     * 合同状态分组
     * @return
     */
    @Query(value = "select c.contract_state from contract c GROUP BY c.contract_state",nativeQuery = true)
    List<String> getContractStateGroup();

    /**
     * 获取项目实施部 和 项目状态 分组 图表
     * @return
     */
    @Query(value = "select ps.project_status_name,d.department_name , COUNT(ps.project_status_id) from implement i\n" +
            "LEFT JOIN project_status ps on ps.project_status_id = i.implement_project_status_id\n" +
            "LEFT JOIN department d on d.department_id = i.implement_department_id\n" +
            "where ps.project_status_name is not null\n" +
            "GROUP BY ps.project_status_name,d.department_name",nativeQuery = true)
    List<Object[]> getChartGroupByImplStatus();

    /**
     *
     * @return
     */
    @Query(value = "select contract_state as `name`,count(contract_state)as `value` from contract GROUP BY contract_state",nativeQuery = true)
    List<Map<String,Object>> getGroupContractStatus();


}
