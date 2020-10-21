package com.hnjbkc.jinbao.flow.flownotice;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Map;

/**
 * @author xudaz
 * @date 2019/8/18
 */
@Service
public class FlowNoticeServiceImpl implements BaseService<FlowNoticeBean> {

    private FlowNoticeDao flowNoticeDao;

    @Autowired
    public void setFlowNoticeDao(FlowNoticeDao flowNoticeDao) {
        this.flowNoticeDao = flowNoticeDao;
    }
    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Override
    public FlowNoticeBean add(FlowNoticeBean flowNoticeBean)  {
        return null;
    }

    @Override
    public Boolean delete(Integer id) {
        return null;
    }

    @Override
    public FlowNoticeBean update(FlowNoticeBean flowNoticeBean) {
        return null;
    }


    Integer getAllNum4Me(HttpSession session) {
        Object user = session.getAttribute("user");
        if ( user == null ) {
            return 0;
        }
        return flowNoticeDao.countByObjectUserBeanUserIdAndFlowNoticeStatusIn( ( (UserBean)user).getUserId()  , Arrays.asList("未读" , "待办"));
    }

    public Page<FlowNoticeBean> get(Map<String, Object> propMap, HttpServletRequest request) {
        Pageable pageable = PageableUtils.producePageable4Map(propMap, "flowNoticeId");
        UserBean userBean = (UserBean) request.getSession().getAttribute("user");
        Object iIs = propMap.remove("iIs");
        if ( iIs == null  ) {
            propMap.put("objectUserBean.userId" , userBean.getUserId());
        }  else {
            propMap.put(iIs + ".userId" ,userBean.getUserId());
        }
        return sqlUtilsDao.getAllByCustomProps(FlowNoticeBean.class, propMap, pageable);
    }
}
