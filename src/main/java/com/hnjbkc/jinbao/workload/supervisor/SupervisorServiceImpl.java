package com.hnjbkc.jinbao.workload.supervisor;

import com.hnjbkc.jinbao.utils.AttrExchange;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author 12
 * @Date 2019-09-06
 */
@Service
public class SupervisorServiceImpl implements BaseService<SupervisorBean> {

    private SupervisorDao supervisorPostDao;

    @Autowired
    public void setSupervisorPostDao(SupervisorDao supervisorPostDao ) {
        this.supervisorPostDao  = supervisorPostDao;
    }

    @Override
    public SupervisorBean add(SupervisorBean supervisorBean){
        return supervisorPostDao.save(supervisorBean);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean delete(Integer id){
        supervisorPostDao.deleteById(id);
        return true;
    }

    @Override
    public SupervisorBean update(SupervisorBean supervisorBean){
        Optional<SupervisorBean> byId = supervisorPostDao.findById(supervisorBean.getSupervisorId());
        if (byId.isPresent()){
            SupervisorBean supervisorPostDbBean = byId.get();
            AttrExchange.onAttrExchange(supervisorPostDbBean, supervisorBean);
            return supervisorPostDao.save(supervisorPostDbBean);
        }
      return null;
    }

    public List<SupervisorBean> findSupervisorPostBeans(SupervisorBean supervisorBean) {
        return supervisorPostDao.findAll(Example.of(supervisorBean),Sort.by(Sort.Order.desc("supervisorId")));
    }
}
