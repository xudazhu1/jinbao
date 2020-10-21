package com.hnjbkc.jinbao.workload.workloadstatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 12
 * @Date 2019-09-20
 */
@RestController
@RequestMapping("work_load_status")
public class WorkLoadStatusController {

    private WorkLoadStatusServiceImpl workLoadStatusServiceImpl;

    @Autowired
    public void setWorkLoadStatusServiceImpl(WorkLoadStatusServiceImpl workLoadStatusServiceImpl) {
        this.workLoadStatusServiceImpl = workLoadStatusServiceImpl;
    }

    @PostMapping
    public Boolean add(WorkLoadStatusBean workLoadStatusBean){
        return workLoadStatusServiceImpl.add(workLoadStatusBean) != null;
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return workLoadStatusServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(WorkLoadStatusBean workLoadStatusBean){
        return workLoadStatusServiceImpl.update(workLoadStatusBean) != null;
    }

}
