package com.hnjbkc.jinbao.workload.staff;

import com.hnjbkc.jinbao.utils.AttrExchange;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author 12
 * @Date 2019-09-05
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class StaffServiceImpl implements BaseService<StaffBean> {

    private StaffDao staffPostDao;

    @Autowired
    public void setStaffPostDao(StaffDao staffPostDao ) {
        this.staffPostDao  = staffPostDao;
    }

    /**
     * 注入实体管理器,执行持久化操作
     */
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public StaffBean add(StaffBean staffBean){
        return staffPostDao.save(staffBean);
    }


    @Override
    public Boolean delete(Integer id){
        staffPostDao.deleteById(id);
        return true;
    }

    @Override
    public StaffBean update(StaffBean staffBean){
        Optional<StaffBean> byId = staffPostDao.findById(staffBean.getStaffId());
        if(byId.isPresent()){
            StaffBean staffPostDbBean = byId.get();
            AttrExchange.onAttrExchange(staffPostDbBean, staffBean);
            return staffPostDao.save(staffPostDbBean);
        }
       return null;
    }

    public List<StaffBean> findStaffPostBeans(StaffBean staffBean) {
        return staffPostDao.findAll(Example.of(staffBean), Sort.by(Sort.Order.desc("staffId")));
    }
}
