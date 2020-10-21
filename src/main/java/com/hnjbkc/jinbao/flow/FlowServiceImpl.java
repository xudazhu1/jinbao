package com.hnjbkc.jinbao.flow;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.utils.AttrSwop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 12
 */
@Service
public class FlowServiceImpl implements BaseService<FlowBean> {

    private FlowDao flowDao;

    @Autowired
    public void setFlowDao(FlowDao flowDao) {
        this.flowDao = flowDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Override
    public FlowBean add(FlowBean flowBean) {
        return flowDao.save(flowBean)  ;
    }

    public FlowBean add(FlowBean flowBean, HttpSession session) {
        UserBean user = (UserBean) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("用户没有登录");
        }
        flowBean.setUserBean(user);
        return flowDao.save(flowBean);
    }

    @Override
    public Boolean delete(Integer id) {
        flowDao.deleteById(id);
        return true;
    }

    @Override
    public FlowBean update(FlowBean flowBean) {
        Optional<FlowBean> byId = flowDao.findById(flowBean.getFlowId());
        if (!byId.isPresent()) {
            return null;
        }
        //属性交换工具
        AttrSwop.onAttrSwop(byId.get(), flowBean);
        return flowDao.save(byId.get())  ;
    }

    public List<FlowBean> getFlow() {
        return flowDao.findAll();
    }



    public Page<FlowBean> get(Map<String, Object> propMap, Pageable pageable) {
        return sqlUtilsDao.getAllByCustomProps(FlowBean.class , propMap , pageable);
    }
}
