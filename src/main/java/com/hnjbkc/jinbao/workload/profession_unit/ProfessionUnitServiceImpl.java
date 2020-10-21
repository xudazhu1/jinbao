package com.hnjbkc.jinbao.workload.profession_unit;

import com.hnjbkc.jinbao.common.CommonPage;
import com.hnjbkc.jinbao.common.CommonResult;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.workload.post.PostBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author 12
 * @Date 2019-09-16
 */
@Service
public class ProfessionUnitServiceImpl implements BaseService<ProfessionUnitBean> {

    private ProfessionUnitDao professionUnitDao;

    @Autowired
    public void setProfessionUnitDao(ProfessionUnitDao professionUnitDao ) {
        this.professionUnitDao  = professionUnitDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Override
    public ProfessionUnitBean add(ProfessionUnitBean professionUnitBean){
        return professionUnitDao.save(professionUnitBean);
    }

    @Override
    public Boolean delete(Integer id){
        professionUnitDao.deleteById(id);
        return true;
    }

    @Override
    public ProfessionUnitBean update(ProfessionUnitBean professionUnitBean){
        return professionUnitDao.save(professionUnitBean);
    }

    public List listProfessionUnit() {
       return professionUnitDao.findAll();
    }

    public CommonResult search(Map<String, Object> propMap, Pageable pageRequest) {
        String resultMap = "new Map(" +
                "professionUnitId as professionUnitId, " +
                "professionUnitName as professionUnitName " +
                ")";
        Page<Map<String,Object>> postBeans = sqlUtilsDao.resultMapByCustomProps(ProfessionUnitBean.class, resultMap, propMap, pageRequest);
        return CommonResult.success(CommonPage.restPage(postBeans));
    }
}
