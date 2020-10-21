package com.hnjbkc.jinbao.workload.captain;

import com.hnjbkc.jinbao.utils.AttrExchange;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;

import java.util.List;
import java.util.Optional;

/**
 * @author 12
 * @Date 2019-09-06
 */
@Service
public class CaptainServiceImpl implements BaseService<CaptainBean> {

    private CaptainDao captainPostDao;

    @Autowired
    public void setCaptainPostDao(CaptainDao captainPostDao ) {
        this.captainPostDao  = captainPostDao;
    }

    @Override
    public CaptainBean add(CaptainBean captainPostBean){
        return captainPostDao.save(captainPostBean);
}

    @Override
    public Boolean delete(Integer id){
        captainPostDao.deleteById(id);
        return true;
    }

    @Override
    public CaptainBean update(CaptainBean captainPostBean){
        Optional<CaptainBean> byId = captainPostDao.findById(captainPostBean.getCaptainId());
        if(byId.isPresent()){
            CaptainBean captainPostDbBean = byId.get();
            AttrExchange.onAttrExchange(captainPostDbBean,captainPostBean);
            return captainPostDao.save(captainPostDbBean);
        }
       return null;
    }

    public List<CaptainBean> findCaptainPosts(CaptainBean captainPostBean) {
        return captainPostDao.findAll(Example.of(captainPostBean), Sort.by(Sort.Order.desc("captainId")));
    }
}
