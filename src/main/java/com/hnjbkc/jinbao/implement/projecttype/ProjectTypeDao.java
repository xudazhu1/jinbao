package com.hnjbkc.jinbao.implement.projecttype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 12
 * @Date 2019-08-07
 */
@Repository
public interface ProjectTypeDao extends JpaRepository<ProjectTypeBean,Integer> {

}
