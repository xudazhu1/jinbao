package com.hnjbkc.jinbao.organizationalstructure.user;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.organizationalstructure.company.CompanyBean;
import com.hnjbkc.jinbao.organizationalstructure.department.DepartmentBean;
import com.hnjbkc.jinbao.organizationalstructure.job.JobServiceImpl;
import com.hnjbkc.jinbao.organizationalstructure.role.RoleBean;
import com.hnjbkc.jinbao.permission.PermissionBean;
import com.hnjbkc.jinbao.utils.AttrExchange;
import com.hnjbkc.jinbao.utils.MyTreeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.transaction.Transactional;
import java.util.*;

/**
 * @author xudaz
 * @date 2019/7/11
 */
@SuppressWarnings("WeakerAccess")
@Service
public class UserServiceImpl implements BaseService<UserBean> {
    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserBean add(UserBean userBean) {
        //根据用户名查询这个用户名是否被占用
        UserBean userBeanByUserName = userDao.findUserBeanByUserName(userBean.getUserName());
        if (userBeanByUserName != null) {
            throw new RuntimeException("用户名被占用");
        } else {
            //给现在有的密码进行加密处理
            String encrpytedPassword = getEncrpytedPassword(userBean.getUserPassword());
            userBean.setUserPassword(encrpytedPassword);
            //保存用户
            return userDao.save(userBean);
        }
    }

    /**
     * 用户登录的方法
     *
     * @param name     用户名
     * @param password 用户的密码
     * @return r
     */
    UserBean login(String name, String password) {
        //给密码进行加密处理
        String encrpytedPassword = getEncrpytedPassword(password);
        UserBean userBean = new UserBean();
        userBean.setUserPassword(encrpytedPassword);
        userBean.setUserName(name);
        //根据用户名和密码查找用户信息
        Optional<UserBean> user = userDao.findOne(Example.of(userBean));
        return user.orElse(null);

    }

    @Override
    public Boolean delete(Integer id) {
        if (id != null) {
            userDao.deleteById(id);
            return true;
        }
        return false;

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserBean update(UserBean userBean) {
//        return null;
        String userPassword = userBean.getUserPassword();
        String encrpytedPassword = getEncrpytedPassword(userPassword);
        userBean.setUserPassword("".equals( userPassword ) ? null : encrpytedPassword );
        userDao.deleteRoleConnectionByUserId(userBean.getUserId());
        Optional<UserBean> byId = userDao.findById(userBean.getUserId());
        if (!byId.isPresent()) {
            return null;
        }
        UserBean saveBean = byId.get();
        AttrExchange.onAttrExchange(saveBean, userBean);
        UserBean save;
        try {
            save = userDao.save(saveBean);
        } catch (Exception e) {
            return null;
        }
        return save;
    }

    public Boolean update(String newPassword, String name, String oldPassword) {
        if (name != null && newPassword != null) {
            String newEncrpytedPassword = getEncrpytedPassword(newPassword);
            String oldEncrpytedPassword = "123456";
            if (oldPassword == null) {
                oldEncrpytedPassword = getEncrpytedPassword(oldEncrpytedPassword);
            } else {
                oldEncrpytedPassword = getEncrpytedPassword(oldPassword);
            }
            int i = userDao.updatePassword(newEncrpytedPassword, name, oldEncrpytedPassword);
            return i == 1;
        }
        return false;

    }

    /**
     * 查询并且分页的方法(不模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    public Page<UserBean> get(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.getAllByCustomProps(UserBean.class, propMap, pageRequest);
    }

    /**
     * 查询并且分页的方法(模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    public Page<UserBean> search(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.searchAllByCustomProps(UserBean.class, propMap, pageRequest);
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    List getSingleProperty(String property) {
        return sqlUtilsDao.getSingleProperties(UserBean.class, property);
    }

    /**
     * 获取加密后的密码
     *
     * @param src 原始密码
     * @return 加密后的密码
     * @see #md5(String)
     */
    private String getEncrpytedPassword(
            String src) {
        // 将原密码加密
        String result = md5(src);
        // 将以上结果再加密5次
        for (int i = 0; i < 5; i++) {
            result = md5(result);
        }
        // 返回
        return result;
    }

    /**
     * 使用MD5算法对数据进行加密
     *
     * @param src 原文
     * @return 密文
     */
    private String md5(String src) {
        if (src == null) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(src.getBytes()).toUpperCase();
    }


    private JobServiceImpl jobService;

    @Autowired
    public void setJobService(JobServiceImpl jobService) {
        this.jobService = jobService;
    }

    /**
     * 获取树 用以选择
     *
     * @param propMap     搜索参数
     * @param pageRequest 分页??
     * @return r
     */
    List<MyTreeBean> getTree(Map<String, Object> propMap, Pageable pageRequest) {
        return jobService.getTree( propMap , pageRequest , true );
    }

    /**
     * 获取该用户的所有可用权限
     *
     * @param userId 用户Id
     * @return 权限集合
     */
    public Set<PermissionBean> getPermissions(Integer userId) {
        Optional<UserBean> byId = userDao.findById(userId);
        Set<PermissionBean> permissionBeans = new HashSet<>();
        if (byId.isPresent()) {

            UserBean userBean = byId.get();
            //拿取角色权限
            for (RoleBean roleBean : userBean.getRoleBeans()) {
                permissionBeans.addAll(roleBean.getPermissionBeans());
            }
            //拿取职位的权限
            permissionBeans.addAll(userBean.getJobBean().getPermissionBeans());
            //拿取部门&公司的权限
            getDeptPermissions4DepartmentBean(userBean.getJobBean().getDepartmentBean(), permissionBeans);
        }
        return permissionBeans;

    }

    private void getDeptPermissions4DepartmentBean(DepartmentBean departmentBean, Set<PermissionBean> permissionBeans) {
        permissionBeans.addAll(departmentBean.getPermissionBeans());
        if (departmentBean.getParentDepartmentBean() != null) {
            getDeptPermissions4DepartmentBean(departmentBean.getParentDepartmentBean(), permissionBeans);
        } else {
            permissionBeans.addAll(departmentBean.getCompanyBean().getPermissionBeans());
        }
    }

    public CompanyBean getCompanyByUser(UserBean userBean) {
        Optional<UserBean> byId = userDao.findById(userBean.getUserId());
        if (byId.isPresent()) {
            DepartmentBean departmentBeanTemp = byId.get().getJobBean().getDepartmentBean();
            while (departmentBeanTemp.getParentDepartmentBean() != null) {
                departmentBeanTemp = departmentBeanTemp.getParentDepartmentBean();
            }
            return departmentBeanTemp.getCompanyBean();
        }
        return new CompanyBean();
    }

    @Transactional(rollbackOn = Exception.class)
    public Boolean addPostUser(UserBean userBean) {
        Optional<UserBean> byId = userDao.findById(userBean.getUserId());
        if (byId.isPresent()) {
            UserBean userDbBean = byId.get();
            userDao.save(userDbBean);
            return true;
        }
        return null;
    }


    /**
     * 为用户移除推荐人角色
     *
     * @param id 用户 id
     * @return r
     */
    @Transactional(rollbackOn = Exception.class)
    public Boolean removeReferee4UserId(Integer id) {
        Optional<UserBean> byId = userDao.findById(id);
        if (!byId.isPresent()) {
            return false;
        }
        UserBean userBean = byId.get();
        userBean.getRoleBeans().removeIf(roleBean -> "推荐人".equals(roleBean.getRoleName()));
        return update(userBean) != null;
    }

    public List findAll(){
        return userDao.getUserAll();
    }

    @SuppressWarnings("unused")
    public Page getUsers4Id(Map<String, Object> propMap, Pageable userId) {
        Page<UserBean> userBeans = get(propMap, userId);
        List<UserBean> content = userBeans.getContent();
        for (UserBean userBean : content) {
            for (RoleBean roleBean : userBean.getRoleBeans()) {
                //触发角色的懒加载
                Integer roleId = roleBean.getRoleId();
            }
        }
        return userBeans;
    }
}
