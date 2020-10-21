package com.hnjbkc.jinbao.workload.workloadstatus;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;

/**
 * @author 12
 * @Date 2019-09-20
 */
@Service
public class WorkLoadStatusServiceImpl implements BaseService<WorkLoadStatusBean> {

    private WorkLoadStatusDao workLoadStatusDao;

    @Autowired
    public void setWorkLoadStatusDao(WorkLoadStatusDao workLoadStatusDao ) {
        this.workLoadStatusDao  = workLoadStatusDao;
    }

    @Override
    public WorkLoadStatusBean add(WorkLoadStatusBean workLoadStatusBean){
        return workLoadStatusDao.save(workLoadStatusBean);
    }

    @Override
    public Boolean delete(Integer id){
        workLoadStatusDao.deleteById(id);
        return true;
    }

    @Override
    public WorkLoadStatusBean update(WorkLoadStatusBean workLoadStatusBean){
        return workLoadStatusDao.save(workLoadStatusBean);
    }

}
