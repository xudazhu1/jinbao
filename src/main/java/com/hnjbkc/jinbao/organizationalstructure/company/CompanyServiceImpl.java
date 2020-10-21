package com.hnjbkc.jinbao.organizationalstructure.company;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.utils.AttrSwop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author siliqiang
 * @date 2019.7.11
 */
@Service

public class CompanyServiceImpl implements BaseService<CompanyBean> {
    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    private CompanyDao companyDao;

    @Autowired
    public void setCompanyDao(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    @Override
    public CompanyBean add(CompanyBean companyBean) {
        return companyDao.save(companyBean) ;
    }

    @Override
    public Boolean delete(Integer id) {
        companyDao.deleteById(id);
        return true;
    }

    @Override
    public CompanyBean update(CompanyBean companyBean) {
        Optional<CompanyBean> byId = companyDao.findById(companyBean.getCompanyId());
        if (!byId.isPresent()) {
            return null;
        }
        CompanyBean saveBean = byId.get();
        AttrSwop.onAttrSwop(saveBean, companyBean);
        CompanyBean save ;
        try {
            save = companyDao.save(saveBean);
        } catch (Exception e) {
            return null;
        }
        return save ;
    }

    /**
     * 查询并且分页的方法
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    public Page<CompanyBean> get(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.getAllByCustomProps(CompanyBean.class, propMap, pageRequest);
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    public List getSingleProperty(String property) {
        return sqlUtilsDao.getSingleProperties(CompanyBean.class, property);
    }
}
