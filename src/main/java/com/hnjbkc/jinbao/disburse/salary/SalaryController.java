package com.hnjbkc.jinbao.disburse.salary;

import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 12
 * @Date 2019-10-03
 */
@RestController
@RequestMapping("salary")
public class SalaryController {

    private SalaryServiceImpl salaryServiceImpl;

    @Autowired
    public void setSalaryServiceImpl(SalaryServiceImpl salaryServiceImpl) {
        this.salaryServiceImpl = salaryServiceImpl;
    }

    @PostMapping
    public Boolean add(SalaryBean salaryBean){

        return salaryServiceImpl.add(salaryBean) != null;
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return salaryServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(SalaryBean salaryBean){
        return salaryServiceImpl.update(salaryBean) != null;
    }

    /**
     * 查询并且分页的方法(模糊)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping
    public Page findAll(@RequestParam Map<String, Object> propMap) {
        return salaryServiceImpl.search(propMap, PageableUtils.producePageable4Map(propMap, "salaryId"));
    }

}
