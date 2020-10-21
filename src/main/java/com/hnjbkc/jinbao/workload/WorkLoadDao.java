package com.hnjbkc.jinbao.workload;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author 12
 * @Date 2019-08-27
 */
@Repository
public interface WorkLoadDao extends JpaRepository<WorkLoadBean,Integer> {

    /**
     * 那工作量日期 去人员工资表 找队友日期工资
     * @param list 工作量id list
     * @return List<List> index[0] 是id index[1] 是工资
     */
    @Query(value = "select wl.work_load_id ,(s.salary_daily_cost * wl.work_load_duration) as personnelCommission from work_load wl\n" +
            "left join `user` u on u.user_id = wl.work_load_staff_id\n" +
            "left join salary s on s.salary_user_id = u.user_id\n" +
            "where DATE_FORMAT(s.salary_date,'%Y-%m') = DATE_FORMAT(wl.work_load_date,'%Y-%m') and work_load_id in (?1)",nativeQuery = true)
    List<List> getPersonnelCommission(Collection list);
}
