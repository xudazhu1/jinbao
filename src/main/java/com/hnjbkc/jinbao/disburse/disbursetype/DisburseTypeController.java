package com.hnjbkc.jinbao.disburse.disbursetype;

import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author siliqiang
 * @date 2019.8.28
 */
@RestController
@RequestMapping("disburse_type")
public class DisburseTypeController {
    private DisburseTypeServiceImpl disburseTypeService;

    @Autowired
    public void setDisburseTypeService(DisburseTypeServiceImpl disburseTypeService) {
        this.disburseTypeService = disburseTypeService;
    }

    /**
     * 费用类型的增加
     *
     * @param disburseTypeBean 费用明细的对象
     * @return 返回的是一个布尔值
     */
    @PostMapping
    public DisburseTypeBean addDisburseType(DisburseTypeBean disburseTypeBean) {
        return disburseTypeService.add(disburseTypeBean) ;
    }

    /**
     * 查询并且分页的方法(不模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping
    public Page getDisburseType(@RequestParam Map<String, Object> propMap) {
        return disburseTypeService.get(propMap, PageableUtils.producePageable4Map(propMap, "disburseTypeId"));
    }

    /**
     * 查询并且分页的方法(模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping("s")
    public Page searchDisburseType(@RequestParam Map<String, Object> propMap) {
        return disburseTypeService.search(propMap, PageableUtils.producePageable4Map(propMap, "disburseTypeId"));
    }

    /**
     * 删除的方法
     *
     * @param id 该条数据的id
     * @return 返回的布尔值
     */
    @DeleteMapping
    public Boolean deleteDisburseType(Integer id) {
        return disburseTypeService.delete(id);
    }

    /**
     * 修改的方法
     *
     * @param disburseTypeBean 支付类型的对象
     * @return 返回一个布尔值
     */
    @PutMapping
    public Boolean updateDisburseType(DisburseTypeBean disburseTypeBean) {
        return disburseTypeService.update(disburseTypeBean) != null;
    }

}
