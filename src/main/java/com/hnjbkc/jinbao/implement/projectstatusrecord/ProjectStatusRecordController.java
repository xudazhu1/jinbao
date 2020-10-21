package com.hnjbkc.jinbao.implement.projectstatusrecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 12
 * @Date 2019-08-13
 */
@RestController
@RequestMapping("project_status_record")
public class ProjectStatusRecordController {

    private ProjectStatusRecordServiceImpl projectStatusRecordServiceImpl;

    @Autowired
    public void setProjectStatusRecordServiceImpl(ProjectStatusRecordServiceImpl projectStatusRecordServiceImpl) {
        this.projectStatusRecordServiceImpl = projectStatusRecordServiceImpl;
    }

    @PostMapping
    public Boolean add(ProjectStatusRecordBean projectStatusRecordBean){
        return projectStatusRecordServiceImpl.add(projectStatusRecordBean) != null;
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return projectStatusRecordServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(ProjectStatusRecordBean projectStatusRecordBean){
        return projectStatusRecordServiceImpl.update(projectStatusRecordBean) != null;
    }

}
