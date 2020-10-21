package com.hnjbkc.jinbao.disburse;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.disburse.expenseaccount.ExpenseAccountDao;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.project.ProjectBean;
import com.hnjbkc.jinbao.utils.GetNum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author siliqiang
 * @date 2019.9.2
 */
@Service
public class DisburseServiceImpl implements BaseService<DisburseBean> {
    private ExpenseAccountDao expenseAccountDao;

    @Autowired
    public void setExpenseAccountDao(ExpenseAccountDao expenseAccountDao) {
        this.expenseAccountDao = expenseAccountDao;
    }

    private DisburseDao disburseDao;

    @Autowired
    public void setDisburseDao(DisburseDao disburseDao) {
        this.disburseDao = disburseDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }
    @Override
    public DisburseBean add(DisburseBean disburseBean) {
       return disburseDao.save(disburseBean);
    }

    @Override
    public Boolean delete(Integer id) {
        return null;
    }

    @Override
    public DisburseBean update(DisburseBean disburseBean) {
        return null;
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    List getSingleProperty(String property) {
        return sqlUtilsDao.getSingleProperties(DisburseBean.class, property);
    }
}
