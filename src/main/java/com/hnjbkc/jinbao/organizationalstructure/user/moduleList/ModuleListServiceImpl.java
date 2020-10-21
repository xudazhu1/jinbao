package com.hnjbkc.jinbao.organizationalstructure.user.moduleList;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;

/**
 * @author 12
 * @Date 2019-10-18
 */
@Service
public class ModuleListServiceImpl implements BaseService<ModuleListBean> {

    private ModuleListDao moduleListDao;

    @Autowired
    public void setModuleListDao(ModuleListDao moduleListDao ) {
        this.moduleListDao  = moduleListDao;
    }

    @Override
    public  ModuleListBean add(ModuleListBean moduleListBean){
        return moduleListDao.save(moduleListBean);
    }

    @Override
    public Boolean delete(Integer id){
        moduleListDao.deleteById(id);
        return true;
    }

    @Override
    public ModuleListBean update(ModuleListBean moduleListBean){
        return moduleListDao.save(moduleListBean);
    }

}