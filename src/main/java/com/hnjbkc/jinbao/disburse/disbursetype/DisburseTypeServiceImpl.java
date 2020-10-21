package com.hnjbkc.jinbao.disburse.disbursetype;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.utils.AttrSwop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * @author siliqiang
 * @date 2019.8.28
 */
@Service
public class DisburseTypeServiceImpl implements BaseService<DisburseTypeBean> {
    private DisburseTypeDao disburseTypeDao;

    @Autowired
    public void setDisburseTypeDao(DisburseTypeDao disburseTypeDao) {
        this.disburseTypeDao = disburseTypeDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Override
    public DisburseTypeBean add(DisburseTypeBean disburseTypeBean) {
        return disburseTypeDao.save(disburseTypeBean);
    }

    @Override
    public Boolean delete(Integer id) {
        disburseTypeDao.deleteById(id);

        return true;
    }

    @Override
    public DisburseTypeBean update(DisburseTypeBean disburseTypeBean) {
        Optional<DisburseTypeBean> byId = disburseTypeDao.findById(disburseTypeBean.getDisburseTypeId());
        if (!byId.isPresent()) {
            return null;
        }
        DisburseTypeBean saveBean = byId.get();
        AttrSwop.onAttrSwop(saveBean, disburseTypeBean);
        DisburseTypeBean save;
        try {
            save = disburseTypeDao.save(saveBean);
        } catch (Exception e) {
            return null;
        }
        return save;
    }


    /**
     * 查询并且分页的方法(不模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    public Page<DisburseTypeBean> get(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.getAllByCustomProps(DisburseTypeBean.class, propMap, pageRequest);
    }

    /**
     * 查询并且分页的方法(模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    public Page<DisburseTypeBean> search(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.searchAllByCustomProps(DisburseTypeBean.class, propMap, pageRequest);
    }
}
