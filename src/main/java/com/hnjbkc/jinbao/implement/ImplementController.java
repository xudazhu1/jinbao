package com.hnjbkc.jinbao.implement;

import com.hnjbkc.jinbao.common.CommonResult;
import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.*;

/**
 * @author 12
 * @Date 2019-08-08
 */
@RestController
@RequestMapping("implement")
public class ImplementController {

    private ImplementServiceImpl implementServiceImpl;

    @Autowired
    public void setImplementServiceImpl(ImplementServiceImpl implementServiceImpl) {
        this.implementServiceImpl = implementServiceImpl;
    }

    @PostMapping
    public boolean add(ImplementBean implementBean){

        return implementServiceImpl.add(implementBean) != null ;
    }

    @DeleteMapping
    public boolean delete(Integer id) {
        return implementServiceImpl.delete(id);
    }

    @PutMapping
    public boolean update(ImplementBean implementBean){

        return implementServiceImpl.update(implementBean) != null;
    }



    /**
     * 获取实施表的所有动态属性 下拉框
     * @return map
     */
    @GetMapping("impl_attrs")
    public Map<String,Object> getImplementAttributes(){
        return implementServiceImpl.getImplementAttributes();
    }

    @GetMapping("work_count")
    public Page getWorkCount(@RequestParam Map<String, Object> propMap){
        return implementServiceImpl.getWorkCount(propMap, PageableUtils.producePageable4Map(propMap, "projectId"));
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    @GetMapping("get_single_properties")
    public List getSingleProperties(String property) {
        return implementServiceImpl.getSingleProperty(property);
    }

    @GetMapping("implement_id")
    public ImplementBean getImplementById(Integer id){
       return implementServiceImpl.getImplementById(id);
    }

    @GetMapping("impl_production")
    public Page<Object> findAllByProjectBeanProjectNumIn(@RequestParam Map<String, Object> propMap){
        return implementServiceImpl.findAllByProjectBeanProjectNumIn(propMap, PageableUtils.producePageable4Map(propMap, "implementId"));
    }

    @PutMapping("impl_production")
    public CommonResult addProduction(ImplementBean implementBean, HttpServletRequest request){
        Collection<Part> parts = null;
        try {
            parts = request.getParts();
        } catch (Exception e) {
            System.out.println("请求格式不对 不能读取到上传的文件");
        }
        return implementServiceImpl.addProduction(implementBean,parts,request);
    }

    /**
     * 项目成本
     * @param projectNum
     * @return
     */
    @GetMapping("cost")
    public Map<String, Object> findAllByProjectBeanProjectNumIn(String projectNum){
        List<String> objects = new ArrayList<>();
        objects.add(projectNum);
        return implementServiceImpl.findAllByProjectBeanProjectNumIn(objects);
    }

}
