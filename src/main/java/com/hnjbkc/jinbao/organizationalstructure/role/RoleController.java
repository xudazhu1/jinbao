package com.hnjbkc.jinbao.organizationalstructure.role;

import com.hnjbkc.jinbao.utils.MyTreeBean;
import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author xudaz
 * @date 2019/7/18
 */
@RestController
@RequestMapping("role")
public class RoleController {
    private RoleServiceImpl roleService;

    @Autowired
    public void setRoleService(RoleServiceImpl roleService) {
        this.roleService = roleService;
    }

    /**
     * 添加的方法
     *
     * @param roleBean 岗位对象
     * @return 返回的是一个布尔值
     */
    @PostMapping
    public Boolean addRole(RoleBean roleBean) {
        return roleService.add(roleBean) != null ;
    }


    /**
     * 删除的方法
     *
     * @param id id
     * @return 布尔值
     */
    @DeleteMapping
    public Boolean deleteRole(Integer id) {
        return roleService.delete(id);
    }

    /**
     * 修改的方法
     *
     * @param roleBean 岗位的对象
     * @return 布尔值
     */
    @PutMapping
    public Boolean updateRole(RoleBean roleBean) {
        return roleService.update(roleBean) != null;
    }

    /**
     * 查询并且分页的方法
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping
    public Page getRole(@RequestParam Map<String, Object> propMap) {
        return roleService.getRole(propMap, PageableUtils.producePageable4Map(propMap, "roleId"));
    }

    /**
     * 查询并且分页的方法(模糊)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping("s")
    public Page searchRole(@RequestParam Map<String, Object> propMap) {
        return roleService.searchRole(propMap, PageableUtils.producePageable4Map(propMap, "roleId"));
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    @GetMapping("get_single_properties")
    public List getSingleProperties(String property) {
        return roleService.getSingleProperty(property);
    }


    /**
     * 获取树 用以选择
     * @param propMap 搜索参数
     * @return R
     */
    @GetMapping("tree")
    public List<MyTreeBean> getTree(@RequestParam Map<String, Object> propMap){
        Pageable pageRequest = PageableUtils.producePageable4Map(propMap , "roleId");
        return roleService.getTree(propMap, pageRequest);
    }



}
