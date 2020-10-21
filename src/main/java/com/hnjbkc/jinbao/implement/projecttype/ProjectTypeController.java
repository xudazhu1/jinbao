package com.hnjbkc.jinbao.implement.projecttype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 12
 * @Date 2019-08-08
 */
@RestController
@RequestMapping("project_type")
public class ProjectTypeController {

    private ProjectTypeServiceImpl projectTypeServiceImpl;

    @Autowired
    public void setProjectTypeServiceImpl(ProjectTypeServiceImpl projectTypeServiceImpl) {
        this.projectTypeServiceImpl = projectTypeServiceImpl;
    }

    @PostMapping
    public boolean add(ProjectTypeBean projecttypeBean){
        return projectTypeServiceImpl.add(projecttypeBean) != null ;
    }

    @DeleteMapping
    public boolean delete(Integer id) {
        return projectTypeServiceImpl.delete(id);
    }

    @PutMapping
    public boolean update(ProjectTypeBean projecttypeBean){
        return projectTypeServiceImpl.update(projecttypeBean) != null;
    }

    @GetMapping
    public List<ProjectTypeBean> getProjectTypes(){
        return projectTypeServiceImpl.getProjectTypes();
    }
}
