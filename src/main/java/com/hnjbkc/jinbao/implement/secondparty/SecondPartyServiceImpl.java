package com.hnjbkc.jinbao.implement.secondparty;

import com.hnjbkc.jinbao.base.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author 12
 * @Date 2019-08-07
 */
@Service
public class SecondPartyServiceImpl implements BaseService<SecondPartyBean> {

    private SecondPartyDao secondPartyDao;

    @Autowired
    public void setSecondPartyDao(SecondPartyDao secondPartyDao ) {
        this.secondPartyDao  = secondPartyDao;
    }

    @Override
    public SecondPartyBean add(SecondPartyBean secondPartyBean){
        return secondPartyDao.save(secondPartyBean) ;
    }

    @Override
    public Boolean delete(Integer id){
        secondPartyDao.deleteById(id);
        return true;
    }

    @Override
    public SecondPartyBean update(SecondPartyBean secondPartyBean){
        return secondPartyDao.save(secondPartyBean);
    }

    public List<SecondPartyBean> getSecondPartyList() {
        return secondPartyDao.findAll();
    }
}
