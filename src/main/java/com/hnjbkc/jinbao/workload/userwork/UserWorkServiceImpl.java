package com.hnjbkc.jinbao.workload.userwork;

import com.hnjbkc.jinbao.common.CommonPage;
import com.hnjbkc.jinbao.common.CommonResult;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.project.ProjectBean;
import com.hnjbkc.jinbao.utils.AttrExchange;
import com.hnjbkc.jinbao.workload.profession_unit.ProfessionUnitBean;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;

import java.util.*;

/**
 * @author 12
 * @Date 2019-10-17
 */
@Service
public class UserWorkServiceImpl implements BaseService<UserWorkBean> {

    private UserWorkDao userWorkDao;

    @Autowired
    public void setUserWorkDao(UserWorkDao userWorkDao ) {
        this.userWorkDao  = userWorkDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Override
    public  UserWorkBean add(UserWorkBean userWorkBean){
        return userWorkDao.save(userWorkBean);
    }

    @Override
    public Boolean delete(Integer id){
        userWorkDao.deleteById(id);
        return true;
    }

    @Override
    public UserWorkBean update(UserWorkBean userWorkBean){
        Optional<UserWorkBean> byId = userWorkDao.findById(userWorkBean.getUserWorkId());
        if(byId.isPresent()){
            UserWorkBean userWorkDbBean = byId.get();
            List<String> list = new ArrayList<>();
            list.add("staffPostBean");
            list.add("supervisorPostBean");
            list.add("captainPostBean");
            AttrExchange.onAttrExchange(userWorkDbBean, userWorkBean,list);
            return userWorkDao.save(userWorkDbBean) ;
        }
        return null;
    }

    public List<UserBean> findNotPostUserList() {
        String hql = "from UserBean u ";
        List<UserBean> list = (List<UserBean>)sqlUtilsDao.exSqlCustom(UserBean.class, hql, null);
        return list;
    }

    public List<UserWorkBean> findAll(UserWorkBean workBean) {
        return userWorkDao.findAll(Example.of(workBean), Sort.by(Sort.Order.desc("userWorkId")));
    }

    public CommonResult search(Map<String, Object> propMap, Pageable pageRequest) {
        String resultMap = "new Map(" +
                "userWorkId as userWorkId, " +
                "userBean.userName as userName," +
                "departmentBean.departmentName as departmentName," +
                "staffPostBean.postName as staffName," +
                "supervisorPostBean.postName as supervisorName," +
                "captainPostBean.postName as captainPostName " +
                ")";
        Page<Map<String,Object>> postBeans = sqlUtilsDao.resultMapByCustomProps(UserWorkBean.class, resultMap, propMap, pageRequest);
        return CommonResult.success(CommonPage.restPage(postBeans));
    }

    public CommonResult searchUserWork(Map<String, Object> propMap, Pageable pageRequest) {
        Page<UserWorkBean> projectBeans = sqlUtilsDao.searchAllByCustomProps(UserWorkBean.class, propMap, pageRequest);
        List<UserWorkBean> content = projectBeans.getContent();
        List<Map<String,Object>> list = new ArrayList();
        for (UserWorkBean userWorkBean : content) {
            Map<String,Object> userWorkMap = new HashMap<>();
            userWorkMap.put("UserWorkId",userWorkBean.getUserWorkId());
            userWorkMap.put("userName",userWorkBean.getUserBean().getUserName());
            userWorkMap.put("departmentName",userWorkBean.getDepartmentBean().getDepartmentName());
            if(userWorkBean.getStaffPostBean() != null){
                userWorkMap.put("staff",userWorkBean.getStaffPostBean().getPostName());
            }
            if (userWorkBean.getSupervisorPostBean() != null) {
                userWorkMap.put("supervisor",userWorkBean.getSupervisorPostBean().getPostName());
            }
            if(userWorkBean.getCaptainPostBean() != null){
                userWorkMap.put("captain",userWorkBean.getCaptainPostBean().getPostName());
            }

            list.add(userWorkMap);
        }
        return CommonResult.success(CommonPage.restPage(new PageImpl<>(list, pageRequest, projectBeans.getTotalElements())));
    }

}
