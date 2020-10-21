package com.hnjbkc.jinbao.disburse.disbursedetail;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.utils.AttrSwop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author siliqiang
 * @date 2019.8.27
 */
@Service
public class DisburseDetailServiceImpl implements BaseService<DisburseDetailBean> {
    private DisburseDetailDao disburseDetailDao;

    @Autowired
    public void setDisburseDetailDao(DisburseDetailDao disburseDetailDao) {
        this.disburseDetailDao = disburseDetailDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Override
    public DisburseDetailBean add(DisburseDetailBean disburseDetailBean) {
        return disburseDetailDao.save(disburseDetailBean);
    }

    @Override
    public Boolean delete(Integer id) {
        disburseDetailDao.deleteById(id);
        return true;
    }

    @Override
    public DisburseDetailBean update(DisburseDetailBean disburseDetailBean) {
        Optional<DisburseDetailBean> byId = disburseDetailDao.findById(disburseDetailBean.getDisburseDetailId());
        if (!byId.isPresent()) {
            return null;
        }
        DisburseDetailBean saveBean = byId.get();
        AttrSwop.onAttrSwop(saveBean, disburseDetailBean);
        DisburseDetailBean save;
        try {
            save = disburseDetailDao.save(saveBean);
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
    public Page<DisburseDetailBean> get(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.getAllByCustomProps(DisburseDetailBean.class, propMap, pageRequest);
    }

    /**
     * 查询并且分页的方法(模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    public Page<DisburseDetailBean> search(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.searchAllByCustomProps(DisburseDetailBean.class, propMap, pageRequest);
    }

    List getDepartmentDetail(String string) {
        ArrayList<Object[]> lists = new ArrayList<>();
        List<Object[]> departmentdetail = disburseDetailDao.getDepartmentdetail(string);
        for (Object[] objects : departmentdetail) {
            lists.add(objects);
        }
        return lists;
    }

    /**
     * 拿到除了报销其他的所有费用明细
     */
    List getParticulars(DisburseDetailBean disburseDetailBean){
       return disburseDetailDao.findAll(Example.of(disburseDetailBean));
    }
}

