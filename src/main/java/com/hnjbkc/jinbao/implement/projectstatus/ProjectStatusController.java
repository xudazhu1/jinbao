package com.hnjbkc.jinbao.implement.projectstatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 12
 * @Date 2019-08-08
 */
@RestController
@RequestMapping("project_status")
public class ProjectStatusController {

    private ProjectStatusServiceImpl projectStatusServiceImpl;

    @Autowired
    public void setProjectStatusServiceImpl(ProjectStatusServiceImpl projectStatusServiceImpl) {
        this.projectStatusServiceImpl = projectStatusServiceImpl;
    }

    @PostMapping
    public boolean add(ProjectStatusBean projectstatusBean){
        return projectStatusServiceImpl.add(projectstatusBean) != null ;
    }

    @DeleteMapping
    public boolean delete(Integer id) {
        return projectStatusServiceImpl.delete(id);
    }

    @PutMapping
    public boolean update(ProjectStatusBean projectstatusBean){
        return projectStatusServiceImpl.update(projectstatusBean) != null;
    }

    @GetMapping
    public List<ProjectStatusBean> getProjectStatusList(){
        return projectStatusServiceImpl.getProjectStatusList();
    }

}
