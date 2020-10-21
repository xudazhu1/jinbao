package com.hnjbkc.jinbao.disburse.approvalstatus;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author siliqiang
 * @date 2019.9.16
 */
@Service
public class ApprovalStatusServiceImpl implements BaseService<ApprovalStatusBean> {

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Override
    public ApprovalStatusBean add(ApprovalStatusBean approvalStatusBean) {
        return null;
    }

    @Override
    public Boolean delete(Integer id) {
        return null;
    }

    @Override
    public ApprovalStatusBean update(ApprovalStatusBean approvalStatusBean) {
        return null;
    }

    /**
     * 查询并且分页的方法(不模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    public Page<ApprovalStatusBean> get(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.getAllByCustomProps(ApprovalStatusBean.class, propMap, pageRequest);
    }
}
