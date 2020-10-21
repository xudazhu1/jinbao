package com.hnjbkc.jinbao.disburse.disbursedetail;

import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author siliqiang
 * @date 2019.8.27
 */
@RestController
@RequestMapping("disburse_detail")
public class DisburseDetailController {
    private DisburseDetailServiceImpl disburseDetailService;

    @Autowired
    public void setDisburseDetailService(DisburseDetailServiceImpl disburseDetailService) {
        this.disburseDetailService = disburseDetailService;
    }

    /**
     * 费用明细的增加
     *
     * @param disburseDetailBean 费用明细的对象
     * @return 返回的是一个布尔值
     */
    @PostMapping
    public DisburseDetailBean addDisburseDetail(DisburseDetailBean disburseDetailBean) {
        return disburseDetailService.add(disburseDetailBean);
    }

    /**
     * 查询并且分页的方法(不模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping
    public Page getDisburseDetail(@RequestParam Map<String, Object> propMap) {
        return disburseDetailService.get(propMap, PageableUtils.producePageable4Map(propMap, "disburseDetailId"));
    }

    /**
     * 查询并且分页的方法(模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping("s")
    public Page searchDisburseDetail(@RequestParam Map<String, Object> propMap) {
        return disburseDetailService.search(propMap, PageableUtils.producePageable4Map(propMap, "disburseDetailId"));
    }

    /**
     * 删除的方法
     *
     * @param id 该条数据的id
     * @return 返回的布尔值
     */
    @DeleteMapping
    public Boolean deleteDisburseDetail(Integer id) {
        return disburseDetailService.delete(id);
    }

    /**
     * 修改的方法
     *
     * @param disburseDetailBean 费用明细的对象
     * @return 布尔值
     */
    @PutMapping
    public Boolean updateDisburseDetail(DisburseDetailBean disburseDetailBean) {
        return disburseDetailService.update(disburseDetailBean) != null;
    }

    /**
     * 拿到不同报销类型明细
     *
     * @param s
     * @return
     */
    @GetMapping("detail")
    public List getDepartmentDetail(String s) {
        return disburseDetailService.getDepartmentDetail(s);
    }

    /**
     * 拿到除了报销类型明细
     *
     * @param
     * @return
     */
    @GetMapping("particulars")
    public List getParticulars(DisburseDetailBean disburseDetailBean) {
        return disburseDetailService.getParticulars(disburseDetailBean);
    }

}
