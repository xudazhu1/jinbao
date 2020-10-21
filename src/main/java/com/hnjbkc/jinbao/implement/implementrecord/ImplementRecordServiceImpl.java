package com.hnjbkc.jinbao.implement.implementrecord;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;

/**
 * @author 12
 * @Date 2019-08-21
 */
@Service
public class ImplementRecordServiceImpl implements BaseService<ImplementRecordBean> {

    private ImplementRecordDao implementRecordDao;

    @Autowired
    public void setImplementRecordDao(ImplementRecordDao implementRecordDao ) {
        this.implementRecordDao  = implementRecordDao;
    }

    @Override
    public ImplementRecordBean add(ImplementRecordBean implementRecordBean){
        return implementRecordDao.save(implementRecordBean);
    }

    @Override
    public Boolean delete(Integer id){
        implementRecordDao.deleteById(id);
        return true;
    }

    @Override
    public ImplementRecordBean update(ImplementRecordBean implementRecordBean){
        return implementRecordDao.save(implementRecordBean);
    }

}
