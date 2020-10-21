package com.hnjbkc.jinbao.organizationalstructure.job;

import com.hnjbkc.jinbao.utils.MyTreeBean;
import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author siliqiang
 * @date 2019/7/18
 */
@RestController
@RequestMapping("job")
public class JobController {
    private JobServiceImpl jobService;

    @Autowired
    public void setJobService(JobServiceImpl jobService) {
        this.jobService = jobService;
    }

    /**
     * 添加的方法
     *
     * @param jobBean 岗位对象
     * @return 返回的是一个布尔值
     */
    @PostMapping
    public Boolean addJob(JobBean jobBean) {
        return jobService.add(jobBean) != null ;
    }

    /**
     * 删除的方法
     *
     * @param id id
     * @return 布尔值
     */
    @DeleteMapping
    public Boolean deleteJob(Integer id) {
        return jobService.delete(id);
    }

    /**
     * 修改的方法
     *
     * @param jobBean 岗位的对象
     * @return 布尔值
     */
    @PutMapping
    public Boolean updateJob(JobBean jobBean) {
        return jobService.update(jobBean) != null;
    }

    /**
     * 查询所有并且获取分页(精确查询)
     *
     * @param propMap 分页的参数
     * @return r
     */
    @GetMapping
    public Page getJob(@RequestParam Map<String, Object> propMap) {
        return jobService.getJob(propMap, PageableUtils.producePageable4Map(propMap, "jobId"));
    }
    /**
     * 查询所有并且获取分页(模糊查询)
     *
     * @param propMap 分页的参数
     * @return r
     */
    @GetMapping("s")
    public Page searchJob(@RequestParam Map<String, Object> propMap) {
        return jobService.searchJob(propMap, PageableUtils.producePageable4Map(propMap, "jobId"));
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    @GetMapping("get_single_properties")
    public List getSingleProperties(String property) {
        return jobService.getSingleProperty(property);
    }


    /**
     * 获取树 用以选择
     * @param propMap 搜索参数
     * @return R
     */
    @GetMapping("tree")
    public List<MyTreeBean> getTree(@RequestParam Map<String, Object> propMap){
        Pageable pageRequest = PageableUtils.producePageable4Map(propMap , "companyId");
        return jobService.getTree(propMap, pageRequest);
    }

}
