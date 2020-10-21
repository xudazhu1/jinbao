package com.hnjbkc.jinbao.permission;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.utils.AttrExchange;
import com.hnjbkc.jinbao.utils.MyTreeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author siliqiang
 * @date 2019/7/15
 * 权限的业务层
 */

@Service
public class
PermissionServiceImpl implements BaseService<PermissionBean> {

    private PermissionDao permissionDao;

    @Autowired
    public void setPermissionDao(PermissionDao permissionDao) {
        this.permissionDao = permissionDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Override
    public PermissionBean add(PermissionBean permissionBean) {
        return permissionDao.save(permissionBean);
    }

    @Override
    public Boolean delete(Integer id) {
        return null;
    }

    @Override
    public PermissionBean update(PermissionBean permissionBean) {
        Optional<PermissionBean> byId = permissionDao.findById(permissionBean.getPermissionId());
        if(byId.isPresent()){
            PermissionBean permissionDbBean = byId.get();
            AttrExchange.onAttrExchange(permissionDbBean,permissionBean);
            return permissionDao.save(permissionDbBean);
        }
       return null;
    }

    public Page<PermissionBean> get(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.getAllByCustomProps(PermissionBean.class, propMap, pageRequest);
    }

    public Page<PermissionBean> search(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.searchAllByCustomProps(PermissionBean.class, propMap, pageRequest);
    }

    List getSingleProperties(String property) {
        return sqlUtilsDao.getSingleProperties(PermissionBean.class, property);
    }

    List<MyTreeBean> getPermissionsTree(Map<String, Object> propMap, Pageable pageRequest) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String[] customWhere = request.getParameterValues("customWhere");
        propMap.remove("customWhere");
        if ( customWhere == null ) {
            customWhere = new String[0];
        }
        Page<PermissionBean> permissionBeanPage = sqlUtilsDao.getAllByCustomProps(PermissionBean.class , propMap, pageRequest , customWhere);
        //准备大集合
        List<MyTreeBean> myTreeBeanList = new ArrayList<>();

        //生成类型树
        permissionBeanPage.getContent().forEach(permissionBean -> {
            String typeTemp = permissionBean.getPermissionType();
            String classTemp = permissionBean.getPermissionClass();

            boolean containsType = false;
            boolean containsClass = false;

            for (MyTreeBean myTreeBean : myTreeBeanList) {
                if (myTreeBean.getName() != null && myTreeBean.getName().equals(typeTemp)) {
                    containsType = true;
                    for (MyTreeBean treeBean : myTreeBean.getSubObject()) {
                        if (treeBean.getName() != null && treeBean.getName().equals(classTemp)) {
                            containsClass = true;
                            treeBean.getSubObject().add(
                                    new MyTreeBean(
                                            permissionBean.getPermissionId(),
                                            permissionBean.getPermissionName(),
                                            permissionBean.getPermissionRemark(),
                                            "permission",
                                            new ArrayList<>()));
                        }
                    }
                }
            }

            //不包含
            if (!containsType) {
                //类型
                MyTreeBean myTreeBean = new MyTreeBean(
                        new Random().nextInt(),
                        typeTemp,
                        "",
                        //分类
                        new ArrayList<>(
                                Collections.singletonList(new MyTreeBean(new Random().nextInt(), permissionBean.getPermissionClass(),
                                        "",
                                        //具体
                                        new ArrayList<>(
                                                Collections.singletonList(
                                                        new MyTreeBean(
                                                                permissionBean.getPermissionId(),
                                                                permissionBean.getPermissionName(),
                                                                permissionBean.getPermissionRemark(),
                                                                "permission",
                                                                new ArrayList<>())
                                                )
                                        ))
                                )));

                myTreeBeanList.add(myTreeBean);
            } else if (!containsClass) {
                for (MyTreeBean myTreeBean : myTreeBeanList) {
                    if (myTreeBean.getName() != null && myTreeBean.getName().equals(permissionBean.getPermissionType())) {
                        myTreeBean.getSubObject().add(
                                new MyTreeBean(
                                        new Random().nextInt(),
                                        permissionBean.getPermissionClass(),
                                        "",
                                        //具体
                                        new ArrayList<>(
                                                Collections.singletonList(
                                                        new MyTreeBean(
                                                                permissionBean.getPermissionId(),
                                                                permissionBean.getPermissionName(),
                                                                permissionBean.getPermissionRemark(),
                                                                "permission",
                                                                new ArrayList<>())
                                                )
                                        ))
                        );
                    }
                }
            }
        });
        return myTreeBeanList;
    }
}
