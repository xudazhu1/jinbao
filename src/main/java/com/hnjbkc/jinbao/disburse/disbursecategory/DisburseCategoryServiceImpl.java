package com.hnjbkc.jinbao.disburse.disbursecategory;

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
 * @date 2019.8.27
 */
@Service
public class DisburseCategoryServiceImpl implements BaseService<DisburseCategoryBean> {
    private DisburseCategoryDao disburseCategoryDao;

    @Autowired
    public void setDisburseCategoryDao(DisburseCategoryDao disburseCategoryDao) {
        this.disburseCategoryDao = disburseCategoryDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Override
    public DisburseCategoryBean add(DisburseCategoryBean disburseCategoryBean) {
        return disburseCategoryDao.save(disburseCategoryBean);
    }

    @Override
    public Boolean delete(Integer id) {
        disburseCategoryDao.deleteById(id);
        return true;
    }

    @Override
    public DisburseCategoryBean update(DisburseCategoryBean disburseCategoryBean) {
        Optional<DisburseCategoryBean> byId = disburseCategoryDao.findById(disburseCategoryBean.getDisburseCategoryId());
        if (!byId.isPresent()) {
            return null;
        }
        DisburseCategoryBean saveBean = byId.get();
        AttrSwop.onAttrSwop(saveBean, disburseCategoryBean);
        DisburseCategoryBean save ;
        try {
            save = disburseCategoryDao.save(saveBean);
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
    public Page<DisburseCategoryBean> get(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.getAllByCustomProps(DisburseCategoryBean.class, propMap, pageRequest);
    }

    /**
     * 查询并且分页的方法(模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    public Page<DisburseCategoryBean> search(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.searchAllByCustomProps(DisburseCategoryBean.class, propMap, pageRequest);
    }

}
