package com.hnjbkc.jinbao.productioncosts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 12
 * @Date 2019-09-29
 */
@Repository
public interface ProductionCostsDao extends JpaRepository<ProductionCostsBean,Integer> {

    @Query(value = "select p.project_id,p.project_num,p.project_name,p.project_management_type,m.management_main_head,i.implement_id,d.department_name,i.implement_implement_head from project p \n" +
            "left join implement i on i.implement_project_id = p.project_id \n" +
            "left join department d on i.implement_department_id = d.department_id \n" +
            "left join management m on m.management_project_id = p.project_id " +
            "limit ?1 , ?2", nativeQuery = true)
    public List<List<Object>> findProjectAndProduction(Integer pageNum,Integer pageSize);
}
