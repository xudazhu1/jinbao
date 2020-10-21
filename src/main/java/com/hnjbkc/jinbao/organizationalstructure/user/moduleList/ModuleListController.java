package com.hnjbkc.jinbao.organizationalstructure.user.moduleList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 12
 * @Date 2019-10-18
 */
@RestController
@RequestMapping("module_list")
public class ModuleListController {

    private ModuleListServiceImpl moduleListServiceImpl;

    @Autowired
    public void setModuleListServiceImpl(ModuleListServiceImpl moduleListServiceImpl) {
        this.moduleListServiceImpl = moduleListServiceImpl;
    }

    @PostMapping
    public Boolean add(ModuleListBean moduleListBean){
        return moduleListServiceImpl.add(moduleListBean) != null;
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return moduleListServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(ModuleListBean moduleListBean){
        return moduleListServiceImpl.update(moduleListBean) != null;
    }

}
