package com.hnjbkc.jinbao.implement.projectstatus;

import com.hnjbkc.jinbao.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 12
 * @Date 2019-08-07
 */
@Service
public class ProjectStatusServiceImpl implements BaseService<ProjectStatusBean> {

    private ProjectStatusDao projectStatusDao;

    @Autowired
    public void setProjectStatusDao(ProjectStatusDao projectStatusDao ) {
        this.projectStatusDao  = projectStatusDao;
    }

    @Override
    public ProjectStatusBean add(ProjectStatusBean projectStatusBean){
        return projectStatusDao.save(projectStatusBean) ;
    }

    @Override
    public Boolean delete(Integer id){
        projectStatusDao.deleteById(id);
        return true;
    }

    @Override
    public ProjectStatusBean update(ProjectStatusBean projectStatusBean){
        return projectStatusDao.save(projectStatusBean);
    }

    public List<ProjectStatusBean> getProjectStatusList() {
        return projectStatusDao.findAll();
    }
}
