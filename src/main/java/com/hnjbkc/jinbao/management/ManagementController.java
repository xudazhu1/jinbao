package com.hnjbkc.jinbao.management;

import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author xudaz
 */
@RestController
@RequestMapping("management")
public class ManagementController {
    private ManagementServiceImpl managementService;

    @Autowired
    public void setManagementService(ManagementServiceImpl managementService) {
        this.managementService = managementService;
    }

    /**
     * 添加的方法
     *
     * @param managementBean 岗位对象
     * @return 返回的是一个布尔值
     */
    @PostMapping
    public Boolean addRole(ManagementBean managementBean) {
        return managementService.add(managementBean) != null ;
    }


    /**
     * 删除的方法
     *
     * @param id id
     * @return 布尔值
     */
    @DeleteMapping
    public Boolean deleteRole(Integer id) {
        return managementService.delete(id);
    }

    /**
     * 修改的方法
     *
     * @param managementBean 岗位的对象
     * @return 布尔值
     */
    @PutMapping
    public Boolean updateRole(ManagementBean managementBean) {
        return managementService.update(managementBean) != null;
    }

    /**
     * 查询并且分页的方法
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping
    public Page getRole(@RequestParam Map<String, Object> propMap) {
        return managementService.get(propMap, PageableUtils.producePageable4Map(propMap, "managementId"));
    }

    /**
     * 查询并且分页的方法(模糊)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping("s")
    public Page searchRole(@RequestParam Map<String, Object> propMap) {
        return managementService.search(propMap, PageableUtils.producePageable4Map(propMap, "managementId"));
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    @GetMapping("get_single_properties")
    public List getSingleProperties(String property) {
        return managementService.getSingleProperty(property);
    }

    @GetMapping("partner")
    public Map<String , Object> getPartners(String tableNames) {
        return managementService.getPartners(tableNames);
    }


    @GetMapping("estimated_income")
    public Page<Map<String, Object>> getEstimatedIncome( Integer pageNum , Integer pageSize  ){
       return managementService.getEstimatedIncome( pageNum , pageSize );
    }
}
