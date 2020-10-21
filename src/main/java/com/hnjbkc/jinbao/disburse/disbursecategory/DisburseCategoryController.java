package com.hnjbkc.jinbao.disburse.disbursecategory;

import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author siliqiang
 * @date 2019.8.27
 */
@RestController
@RequestMapping("disburse_category")
public class DisburseCategoryController {
    private DisburseCategoryServiceImpl disburseCategoryService;

    @Autowired
    public void setDisburseCategoryService(DisburseCategoryServiceImpl disburseCategoryService) {
        this.disburseCategoryService = disburseCategoryService;
    }

    /**
     * 费用类别的录入
     * @param disburseCategoryBean 费用类别的对象
     * @return 返回的是布尔值
     */
    @PostMapping
    public DisburseCategoryBean addDisburseCategory(DisburseCategoryBean disburseCategoryBean) {
        return disburseCategoryService.add(disburseCategoryBean);
    }

    /**
     * 查询并且分页的方法(不模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping
    public Page getDisburseCategory(@RequestParam Map<String, Object> propMap) {
        return disburseCategoryService.get(propMap, PageableUtils.producePageable4Map(propMap, "disburseCategoryId"));
    }

    /**
     * 查询并且分页的方法(模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping("s")
    public Page searchDisburseCategory(@RequestParam Map<String, Object> propMap) {
        return disburseCategoryService.search(propMap, PageableUtils.producePageable4Map(propMap, "disburseCategoryId"));
    }

    /**
     * 删除的方法
     * @param id 该条数据的id
     * @return 返回的布尔值
     */
    @DeleteMapping
    public Boolean deleteDisburseCategory(Integer id){
        return disburseCategoryService.delete(id);
    }

    /**
     * 修改的方法
     * @param disburseCategoryBean 类别的对象
     * @return 布尔值
     */
    @PutMapping
    public Boolean updateDisburseCategory(DisburseCategoryBean disburseCategoryBean){
        return disburseCategoryService.update(disburseCategoryBean)!=null;
    }
}
