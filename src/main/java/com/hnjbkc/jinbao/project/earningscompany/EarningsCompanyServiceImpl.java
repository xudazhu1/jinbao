package com.hnjbkc.jinbao.project.earningscompany;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;

import java.util.List;

/**
 * @author 12
 * @Date 2019-08-13
 */
@Service
public class EarningsCompanyServiceImpl implements BaseService<EarningsCompanyBean> {

    private EarningsCompanyDao earningsCompanyDao;

    @Autowired
    public void setEarningsCompanyDao(EarningsCompanyDao earningscompanyDao ) {
        this.earningsCompanyDao  = earningscompanyDao;
    }

    @Override
    public EarningsCompanyBean add(EarningsCompanyBean earningscompanyBean){
        return earningsCompanyDao.save(earningscompanyBean);
    }

    @Override
    public Boolean delete(Integer id){
        earningsCompanyDao.deleteById(id);
        return true;
    }

    @Override
    public EarningsCompanyBean update(EarningsCompanyBean earningsCompanyBean){
        return earningsCompanyDao.save(earningsCompanyBean);
    }


    public List<EarningsCompanyBean> getEarningsCompanyList() {
       return earningsCompanyDao.findAll();
    }
}
