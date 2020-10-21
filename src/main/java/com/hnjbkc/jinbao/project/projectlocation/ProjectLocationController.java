package com.hnjbkc.jinbao.project.projectlocation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 12
 * @Date 2019-08-12
 */
@RestController
@RequestMapping("project_location")
public class ProjectLocationController {

    private ProjectLocationServiceImpl projectLocationServiceImpl;

    @Autowired
    public void setProjectLocationServiceImpl(ProjectLocationServiceImpl projectLocationServiceImpl) {
        this.projectLocationServiceImpl = projectLocationServiceImpl;
    }

    @PostMapping
    public boolean add(ProjectLocationBean projectLocationBean){
        return projectLocationServiceImpl.add(projectLocationBean) != null ;
    }

    @DeleteMapping
    public boolean delete(Integer id) {
        return projectLocationServiceImpl.delete(id);
    }

    @PutMapping
    public boolean update(ProjectLocationBean projectLocationBean){
        return projectLocationServiceImpl.update(projectLocationBean) != null ;
    }

    @GetMapping
    public List<ProjectLocationBean> getProjectLocationBeans(){
        return  projectLocationServiceImpl.getProjectLocationBeans();
    }
}
