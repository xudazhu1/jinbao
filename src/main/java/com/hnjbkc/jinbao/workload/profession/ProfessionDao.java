package com.hnjbkc.jinbao.workload.profession;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author 12
 * @Date 2019-08-27
 */
@Repository
public interface ProfessionDao extends JpaRepository<ProfessionBean,Integer> {

    @Query(value = "select project_id as projectId , project_num as projectNum , project_name as projectName  from project;",nativeQuery = true)
    String[] findByUnitGroup();
}
