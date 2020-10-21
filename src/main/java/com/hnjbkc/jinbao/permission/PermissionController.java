package com.hnjbkc.jinbao.permission;

import com.hnjbkc.jinbao.utils.MyTreeBean;
import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author siliqiang
 * @date 2019/7/15
 */
@RestController
@RequestMapping("permission")
public class PermissionController {
    private PermissionServiceImpl permissionServiceImpl;

    @Autowired
    public void setPermissionServiceImpl(PermissionServiceImpl permissionServiceImpl) {
        this.permissionServiceImpl = permissionServiceImpl;
    }

    @GetMapping("s")
    public Page searchPermissions(@RequestParam Map<String , Object> propMap) throws Exception{
        return permissionServiceImpl.search(propMap , PageableUtils.producePageable4Map(propMap , "permissionId"));
    }
    @GetMapping
    public Page getPermissions(@RequestParam Map<String , Object> propMap) throws Exception{
        return permissionServiceImpl.get(propMap , PageableUtils.producePageable4Map(propMap , "permissionId"));
    }

    @GetMapping("tree")
    public List<MyTreeBean> getPermissionsTree(@RequestParam Map<String , Object> propMap){
        Pageable pageRequest = PageableUtils.producePageable4Map(propMap , "permissionId");
        return permissionServiceImpl.getPermissionsTree(propMap, pageRequest);
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    @GetMapping("get_single_properties")
    public List getSingleProperties( String property ) {
        return permissionServiceImpl.getSingleProperties(property);
    }




}
