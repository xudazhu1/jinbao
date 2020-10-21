package com.hnjbkc.jinbao.implement.projectstatusrecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 12
 * @Date 2019-08-13
 */
@Repository
public interface ProjectStatusRecordDao extends JpaRepository<ProjectStatusRecordBean,Integer> {

}
