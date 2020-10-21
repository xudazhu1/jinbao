package com.hnjbkc.jinbao.quote;

import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.utils.PageableUtils;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author 12
 * @Date 2019-07-31
 */
@RestController
@RequestMapping("quote")
public class QuoteController {

    private QuoteServiceImpl quoteServiceImpl;

    @Autowired
    public void setQuoteServiceImpl(QuoteServiceImpl quoteServiceImpl) {
        this.quoteServiceImpl = quoteServiceImpl;
    }


    @PostMapping
    public Boolean add(QuoteBean quoteBean,HttpServletRequest request){
        Collection<Part> parts;
        try {
             parts = request.getParts();
        } catch (Exception e) {
            System.out.println("请求格式不对 不能读取到上传的文件");
           return false;
        }
        return quoteServiceImpl.add(quoteBean,request,parts) != null;
    }

    @DeleteMapping
    public Boolean delete(Integer id){
        return quoteServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(QuoteBean quoteBean,HttpServletRequest request){
        Collection<Part> parts;
        try {
            parts = request.getParts();
        } catch (Exception e) {
            System.out.println("请求格式不对 不能读取到上传的文件");
            return false;
        }
        return quoteServiceImpl.update(quoteBean,request,parts) != null;
    }


    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    @GetMapping("get_single_properties")
    public List getSingleProperties(String property) {
        return quoteServiceImpl.getSingleProperty(property);
    }

    /**
     *
     * @param propMap 接受页面传过的参数 查询 quote 报价 通过 SqlUtil 拼接sql 实现 多范围 多条件 查询
     * @return 返回page 分页对象
     */
    @GetMapping
    public Page getQuotes(@RequestParam Map<String, Object> propMap){
        return quoteServiceImpl.getQuotes(propMap, PageableUtils.producePageable4Map(propMap, "quoteId"));
    }

    /**
     * 查询并且分页的方法
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping("s")
    public Page searchUsers(@RequestParam Map<String, Object> propMap) {
        return quoteServiceImpl.search(propMap, PageableUtils.producePageable4Map(propMap, "quoteId"));
    }

    /**
     *
     * @param id 报价 Id
     * @return 返回报价对象
     */
    @GetMapping("quote_id")
    public QuoteBean getQuoteById(Integer id){
        return quoteServiceImpl.getQuoteById(id);
    }

    /**
     * 获取所有报价 下拉框
     * @return 返回 键值对
     */
    @GetMapping("select_box")
    public List<Map<String, Object>> getSelectBox(){
        return quoteServiceImpl.getSelectBox();
    }

    /**
     * 获取报价 项目编号
     * @return 编号
     */
    @GetMapping("quote_max_num")
    public String getQuoteMaxNum(){
        return quoteServiceImpl.findMaxNumber();
    }


}
