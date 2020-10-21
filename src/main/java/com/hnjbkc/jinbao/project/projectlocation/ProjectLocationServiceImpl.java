package com.hnjbkc.jinbao.project.projectlocation;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;

import java.util.List;

/**
 * @author 12
 * @Date 2019-08-12
 */
@Service
public class ProjectLocationServiceImpl implements BaseService<ProjectLocationBean> {

    private ProjectLocationDao projectLocationDao;

    @Autowired
    public void setProjectLocationDao(ProjectLocationDao projectLocationDao ) {
        this.projectLocationDao  = projectLocationDao;
    }

    @Override
    public ProjectLocationBean add(ProjectLocationBean projectLocationBean){
        return projectLocationDao.save(projectLocationBean);
    }

    @Override
    public Boolean delete(Integer id){
        projectLocationDao.deleteById(id);
        return true;
    }

    @Override
    public ProjectLocationBean update(ProjectLocationBean projectLocationBean){
        return projectLocationDao.save(projectLocationBean) ;
    }

    public List<ProjectLocationBean> getProjectLocationBeans() {
        return projectLocationDao.findAll();
    }
}
