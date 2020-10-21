package com.hnjbkc.jinbao.disburse.salary;

import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.project.ProjectBean;
import com.hnjbkc.jinbao.utils.AttrExchange;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 12
 * @Date 2019-10-03
 */
@Service
public class SalaryServiceImpl implements BaseService<SalaryBean> {

    private SalaryDao salaryDao;

    @Autowired
    public void setSalaryDao(SalaryDao salaryDao ) {
        this.salaryDao  = salaryDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Override
    public  SalaryBean add(SalaryBean salaryBean){
        List<SalaryBean> all = salaryDao.findAll(Example.of(salaryBean));
        if (all.size() > 0){
            throw new RuntimeException("当前月份 用户录入重复");
        }
        return salaryDao.save(salaryBean);
    }

    @Override
    public Boolean delete(Integer id){
        salaryDao.deleteById(id);
        return true;
    }

    @Override
    public SalaryBean update(SalaryBean salaryBean){
        Optional<SalaryBean> byId = salaryDao.findById(salaryBean.getSalaryId());
        if (byId.isPresent()){
            SalaryBean salaryDbBean = byId.get();
            AttrExchange.onAttrExchange(salaryDbBean,salaryBean);
            return salaryDao.save(salaryDbBean);
        }
       return null;
    }



    /**
     * 查询并且分页的方法(模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    public Page<SalaryBean> search(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.searchAllByCustomProps(SalaryBean.class, propMap, pageRequest);
    }
}
