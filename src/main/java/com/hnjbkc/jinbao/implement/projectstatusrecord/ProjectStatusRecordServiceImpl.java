package com.hnjbkc.jinbao.implement.projectstatusrecord;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;

/**
 * @author 12
 * @Date 2019-08-13
 */
@Service
public class ProjectStatusRecordServiceImpl implements BaseService<ProjectStatusRecordBean> {

    private ProjectStatusRecordDao projectStatusRecordDao;

    @Autowired
    public void setProjectStatusRecordDao(ProjectStatusRecordDao projectStatusRecordDao ) {
        this.projectStatusRecordDao  = projectStatusRecordDao;
    }

    @Override
    public ProjectStatusRecordBean add(ProjectStatusRecordBean projectStatusRecordBean){
        return projectStatusRecordDao.save(projectStatusRecordBean);
    }

    @Override
    public Boolean delete(Integer id){
        projectStatusRecordDao.deleteById(id);
        return true;
    }

    @Override
    public ProjectStatusRecordBean update(ProjectStatusRecordBean projectStatusRecordBean){
        return projectStatusRecordDao.save(projectStatusRecordBean);
    }

}
