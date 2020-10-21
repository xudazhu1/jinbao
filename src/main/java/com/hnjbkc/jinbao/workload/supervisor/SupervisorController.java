package com.hnjbkc.jinbao.workload.supervisor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 12
 * @Date 2019-09-06
 */
@RestController
@RequestMapping("supervisor")
public class SupervisorController {

    private SupervisorServiceImpl supervisorPostServiceImpl;

    @Autowired
    public void setSupervisorPostServiceImpl(SupervisorServiceImpl supervisorPostServiceImpl) {
        this.supervisorPostServiceImpl = supervisorPostServiceImpl;
    }

    @PostMapping
    public Boolean add(SupervisorBean supervisorBean){
        return supervisorPostServiceImpl.add(supervisorBean) != null;
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return supervisorPostServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(SupervisorBean supervisorBean){
        return supervisorPostServiceImpl.update(supervisorBean) != null;
    }

    @GetMapping
    public List<SupervisorBean> findSupervisorPostBeans(SupervisorBean supervisorBean){
        return supervisorPostServiceImpl.findSupervisorPostBeans(supervisorBean);
    }

}
