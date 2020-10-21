package com.hnjbkc.jinbao.implement.projectstatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 12
 * @Date 2019-08-07
 */
@Repository
public interface ProjectStatusDao extends JpaRepository<ProjectStatusBean,Integer> {

}
