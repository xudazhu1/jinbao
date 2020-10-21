package com.hnjbkc.jinbao.workload.workloadstatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 12
 * @Date 2019-09-20
 */
@Repository
public interface WorkLoadStatusDao extends JpaRepository<WorkLoadStatusBean,Integer> {

}
