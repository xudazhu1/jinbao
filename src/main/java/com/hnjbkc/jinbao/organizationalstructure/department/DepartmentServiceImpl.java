package com.hnjbkc.jinbao.organizationalstructure.department;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.hqldao.ManyAndOneToOneAndOne;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.organizationalstructure.job.JobBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.utils.AttrSwop;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author siliqiang
 * @date
 */
@Service
public class DepartmentServiceImpl implements BaseService<DepartmentBean> {

    private DepartmentDao departmentDao;

    @Autowired
    public void setDepartmentDao(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    ManyAndOneToOneAndOne manyAndOneToOneAndOne;

    @Autowired
    public void setManyAndOneToOneAndOne(ManyAndOneToOneAndOne manyAndOneToOneAndOne) {
        this.manyAndOneToOneAndOne = manyAndOneToOneAndOne;
    }

    @Override
    public DepartmentBean add(DepartmentBean departmentBean) {
        return departmentDao.save(departmentBean) ;
    }

    @Override
    public Boolean delete(Integer id) {
        departmentDao.deleteById(id);
        return true;
    }

    @Override
    public DepartmentBean update(DepartmentBean departmentBean) {
        Optional<DepartmentBean> byId = departmentDao.findById(departmentBean.getDepartmentId());
        if (!byId.isPresent()) {
            return null;
        }
        DepartmentBean saveBean = byId.get();
        AttrSwop.onAttrSwop(saveBean, departmentBean);
        DepartmentBean save ;
        try {
            save = departmentDao.save(saveBean);
        } catch (Exception e) {
            return null;
        }
        return save;
    }

    /**
     * 查询并且分页的方法
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    public Page<DepartmentBean> get(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.getAllByCustomProps(DepartmentBean.class, propMap, pageRequest);
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    public List getSingleProperty(String property) {
        return sqlUtilsDao.getSingleProperties(DepartmentBean.class, property);
    }

    public List<UserBean> getUserList(Integer id) {
        Optional<DepartmentBean> byId = departmentDao.findById(id);
        if(byId.isPresent()){
            DepartmentBean departmentBean = byId.get();

            List<UserBean> userList = getUserList(departmentBean);
            return userList;
        }
        return null;
    }

    /**
     *  给个部门 获取里面的 所有User
     * @param departmentBean 部门
     * @return UserList
     */
    public List<UserBean> getUserList(DepartmentBean departmentBean){
        List<UserBean> userList = new ArrayList<>();
        if (departmentBean.getJobBeans().size() > 0){
            List<JobBean> jobBeans = departmentBean.getJobBeans();
            for (JobBean jobBean : jobBeans) {
                List<UserBean> userBeans = jobBean.getUserBeans();
                for (UserBean userBean : userBeans) {
                    userList.add(userBean);
                }
            }
        }
        if(departmentBean.getNextDepartmentBeans() .size() >0){
            List<DepartmentBean> nextDepartmentBeans = departmentBean.getNextDepartmentBeans();
            for (DepartmentBean nextDepartmentBean : nextDepartmentBeans) {
                List<UserBean> userNextList = getUserList(nextDepartmentBean);
                for (UserBean userBean : userNextList) {
                    userList.add(userBean);
                }
            }

        }
        return userList;
    }

    /**
     * findAll
     * @param departmentBean 部门条件
     * @return 返回部门List
     */
    public List<DepartmentBean> findDepartmentName(DepartmentBean departmentBean) {
        Map<String, Object> propMap = new HashMap<>(1);
        propMap.put("departmentName",departmentBean.getDepartmentName());
        Page<DepartmentBean> allByCustomProps = sqlUtilsDao.getAllByCustomProps(DepartmentBean.class, propMap, PageRequest.of(0,Integer.MAX_VALUE));
        return allByCustomProps.getContent();
    }


    /**
     * 获取实施部
     * @param propMap 可加条件
     * @return List<DepartmentBean>
     */
    public List<DepartmentBean> getImplementDepartment(Map<String, Object> propMap) {
        Page<DepartmentBean> allByCustomProps = sqlUtilsDao.getAllByCustomProps(DepartmentBean.class, propMap, PageRequest.of(0,Integer.MAX_VALUE));
        List<DepartmentBean> content = allByCustomProps.getContent();
        DepartmentBean departmentBean = content.get(0);
        List<DepartmentBean> nextDepartmentBeans = departmentBean.getNextDepartmentBeans();
        try {
            manyAndOneToOneAndOne.getCascades(nextDepartmentBeans);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(departmentBean);
        return nextDepartmentBeans;
    }

    /**
     * 获取公司旗下所有部门
     * @param propMap 可加条件
     * @param pageRequest 请求
     * @return List<DepartmentBean>
     */
    public List<DepartmentBean> getCompanyDepartment(Map<String, Object> propMap, Pageable pageRequest) {
        Page<DepartmentBean> allByCustomProps = sqlUtilsDao.getAllByCustomProps(DepartmentBean.class, propMap, pageRequest);
        List<DepartmentBean> content = allByCustomProps.getContent();
        List<DepartmentBean> list = new ArrayList<>();
        return getCompanyDepartment(list, content);
    }

    /**
     *
     * @param list 传入 页面需要返回的部门List
     * @param content 传入 数据库查询出来的部门list
     * @return 返回 该公司下的所有部门
     */
    private List<DepartmentBean> getCompanyDepartment(List<DepartmentBean> list,List<DepartmentBean> content){
        for (DepartmentBean departmentBean : content) {
            if(departmentBean.getNextDepartmentBeans().size() > 0){
                List<DepartmentBean> nextDepartmentBeans = departmentBean.getNextDepartmentBeans();
                list.add(departmentBean);
                getCompanyDepartment(list,nextDepartmentBeans);
            }else {
                list.add(departmentBean);
            }

        }
        return list;
    }

    public List<UserBean> findUserForDepartment(DepartmentBean departmentBean) {
        List<DepartmentBean> all = departmentDao.findAll(Example.of(departmentBean));
        List<UserBean> userForDepartment = getUserForDepartment(all.get(0));
        return userForDepartment;
    }

    private List<UserBean> getUserForDepartment(DepartmentBean departmentBean){
        List<UserBean> userBeanList = new ArrayList<>();
        List<JobBean> jobBeans = departmentBean.getJobBeans();
        for (JobBean jobBean : jobBeans) {
            List<UserBean> userBeans = jobBean.getUserBeans();
            userBeanList.addAll(userBeans);
        }
        List<DepartmentBean> nextDepartmentBeans = departmentBean.getNextDepartmentBeans();
        for (DepartmentBean nextDepartmentBean : nextDepartmentBeans) {
            List<UserBean> userForDepartment = getUserForDepartment(nextDepartmentBean);
            userBeanList.addAll(userForDepartment);
        }
        return userBeanList;
    }
}
