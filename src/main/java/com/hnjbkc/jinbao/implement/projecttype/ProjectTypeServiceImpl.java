package com.hnjbkc.jinbao.implement.projecttype;

import com.hnjbkc.jinbao.base.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author 12
 * @Date 2019-08-07
 */
@Service
public class ProjectTypeServiceImpl implements BaseService<ProjectTypeBean> {

    private ProjectTypeDao projectTypeDao;

    @Autowired
    public void setProjectTypeDao(ProjectTypeDao projectTypeDao ) {
        this.projectTypeDao  = projectTypeDao;
    }

    @Override
    public ProjectTypeBean add(ProjectTypeBean projectTypeBean){
        return projectTypeDao.save(projectTypeBean)  ;
    }

    @Override
    public Boolean delete(Integer id){
        projectTypeDao.deleteById(id);
        return true;
    }

    @Override
    public ProjectTypeBean update(ProjectTypeBean projectTypeBean){
        return projectTypeDao.save(projectTypeBean)  ;
    }

    public List<ProjectTypeBean> getProjectTypes() {
        return projectTypeDao.findAll();
    }
}
