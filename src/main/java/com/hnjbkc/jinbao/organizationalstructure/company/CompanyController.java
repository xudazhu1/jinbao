package com.hnjbkc.jinbao.organizationalstructure.company;

import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author siliqiang
 * @date 2019.7.11
 */
@RestController
@RequestMapping("company")
public class CompanyController {
    private CompanyServiceImpl companyServiceImpl;

    @Autowired
    public void setCompanyServiceImpl(CompanyServiceImpl companyServiceImpl) {
        this.companyServiceImpl = companyServiceImpl;
    }

    /**
     * 添加公司的方法
     *
     * @param companyBean 公司的对象
     * @return 返回的是一个布尔值
     */
    @PostMapping
    public Boolean addCompany(CompanyBean companyBean) {
        return companyServiceImpl.add(companyBean) != null;
    }

    /**
     * 删除公司的方法
     *
     * @param id id
     * @return 布尔值
     */
    @DeleteMapping
    public Boolean deleteCompany(Integer id) {
        return companyServiceImpl.delete(id);
    }

    /**
     * 修改的方法
     *
     * @param companyBean 公司的对象
     * @return 布尔值
     */
    @PutMapping
    public Boolean updateCompany(CompanyBean companyBean) {
        return companyServiceImpl.update(companyBean) != null;
    }

    /**
     * 查询并且分页的方法
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping()
    public Page getCompanies(@RequestParam Map<String, Object> propMap) {
        return companyServiceImpl.get(propMap, PageableUtils.producePageable4Map(propMap, "companyId"));
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    @GetMapping("get_single_properties")
    public List getSingleProperties(String property) {
        return companyServiceImpl.getSingleProperty(property);
    }

}
