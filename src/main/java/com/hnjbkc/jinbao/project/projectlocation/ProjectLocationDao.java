package com.hnjbkc.jinbao.project.projectlocation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 12
 * @Date 2019-08-12
 */
@Repository
public interface ProjectLocationDao extends JpaRepository<ProjectLocationBean,Integer> {

    public ProjectLocationBean findAllByProjectLocationName(String name);
}
