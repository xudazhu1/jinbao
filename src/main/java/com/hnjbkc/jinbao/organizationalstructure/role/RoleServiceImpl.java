package com.hnjbkc.jinbao.organizationalstructure.role;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.utils.AttrExchange;
import com.hnjbkc.jinbao.utils.MyTreeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

/**
 * @author siliqiang
 * @date 2019/7/15
 * 角色的service层
 */
@Service
public class RoleServiceImpl implements BaseService<RoleBean> {
    private RoleDao roleDao;

    @Autowired
    public void setRoleDao(RoleDao roleDao) {

        this.roleDao = roleDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public RoleBean add(RoleBean roleBean) {
        return roleDao.save(roleBean);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean delete(Integer id) {
        roleDao.deleteById(id);
        return true;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public RoleBean update(RoleBean roleBean) {
        roleDao.deletePermissionConnectionByRoleId(roleBean.getRoleId());
        roleDao.deleteUserConnectionByRoleId(roleBean.getRoleId());
        Optional<RoleBean> byId = roleDao.findById(roleBean.getRoleId());
        if (!byId.isPresent()) {
            return null;
        }
        RoleBean saveBean = byId.get();
        AttrExchange.onAttrExchange(saveBean , roleBean);
        RoleBean save;
        try {
            save = roleDao.save(saveBean);
        } catch (Exception e) {
            return null;
        }
        return save ;
    }

    /**
     * 查询的方法(模糊)
     *
     * @param propMap     前端传过来的分页参数
     * @param pageRequest p
     * @return r
     */
    public Page<RoleBean> searchRole(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.searchAllByCustomProps(RoleBean.class, propMap, pageRequest);
    }

    /**
     * 查询的方法 (非模糊)
     *
     * @param propMap     前端传过来的分页参数
     * @param pageRequest p
     * @return r
     */
    public Page<RoleBean> getRole(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.getAllByCustomProps(RoleBean.class, propMap, pageRequest);
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    List getSingleProperty(String property) {
        return sqlUtilsDao.getSingleProperties(RoleBean.class, property);
    }

    List<MyTreeBean> getTree(Map<String, Object> propMap, Pageable pageRequest) {
        Page<RoleBean> userBeans = getRole(propMap, pageRequest);



        List<MyTreeBean> mySubTreeBeanList = new ArrayList<>();

        userBeans.getContent().forEach(roleBean -> mySubTreeBeanList.add(new MyTreeBean(
                roleBean.getRoleId(),
                roleBean.getRoleName(),
                roleBean.getRoleDescribe(),
                new ArrayList<>()
        )));
        return Collections.singletonList( new MyTreeBean( -1 , "全选" , mySubTreeBeanList ) );

    }
}
