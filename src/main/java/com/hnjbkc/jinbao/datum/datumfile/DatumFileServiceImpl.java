package com.hnjbkc.jinbao.datum.datumfile;

import com.hnjbkc.jinbao.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author siliqiang
 * @date 2019.8.15
 */
@Service
public class DatumFileServiceImpl implements BaseService<DatumFileServiceImpl> {
    private DatumFileDao datumFileDao;

    @Autowired
    public void setDatumFileDao(DatumFileDao datumFileDao) {
        this.datumFileDao = datumFileDao;
    }

    @Override
    public DatumFileServiceImpl add(DatumFileServiceImpl datumFileService) {
        return null;
    }

    @Override
    public Boolean delete(Integer id) {
        return null;
    }

    @Override
    public DatumFileServiceImpl update(DatumFileServiceImpl datumFileService) {
        return null;
    }

    public Boolean select(DatumFileBean datumFileBean) {
        List<DatumFileBean> all = datumFileDao.findAll(Example.of(datumFileBean));
        if (all.size()!=0){
            return true;
        }
        return false;
    }
}
