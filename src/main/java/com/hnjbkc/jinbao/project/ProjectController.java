package com.hnjbkc.jinbao.project;

import com.hnjbkc.jinbao.utils.PageableUtils;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 12
 * @Date 2019-08-08
 */
@RestController
@RequestMapping("project")
public class ProjectController {

    private ProjectServiceImpl projectServiceImpl;

    @Autowired
    public void setProjectServiceImpl(ProjectServiceImpl projectServiceImpl) {
        this.projectServiceImpl = projectServiceImpl;
    }

    @PostMapping
    public synchronized Map add(ProjectBean projectBean)  {
        String projectNum = projectServiceImpl.add(projectBean).getProjectNum();
        Map<String, Object> map = new HashMap<>();
        map.put("projectNum",projectNum);
        return map;
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return projectServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(ProjectBean projectBean){
        return projectServiceImpl.update(projectBean) != null;
    }



    /**
     * 查询并且分页的方法(模糊)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping("s")
    public Page searchUsers(@RequestParam Map<String, Object> propMap) {
        return projectServiceImpl.search(propMap, PageableUtils.producePageable4Map(propMap, "projectId"));
    }
    /**
     * 查询并且分页的方法
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping
    public Page getUsers(@RequestParam Map<String, Object> propMap) {
        return projectServiceImpl.get(propMap, PageableUtils.producePageable4Map(propMap, "projectId"));
    }

    /**
     * 通过id 获取项目
     * @return 项目对象
     */
    @GetMapping("project_id")
    public ProjectBean getProjectById(Integer id){
        return projectServiceImpl.getProjectById(id);
    }

    /**
     * 通过传入 ProjectBean 属性 查找数据库
     * @param projectBean ProjectBean
     * @return 返回list
     */
    @GetMapping("project_num_name")
    public List<ProjectBean> findProjectName(ProjectBean projectBean){
        return projectServiceImpl.findProjectNumName(projectBean);
    }

    /**
     * 获取项目编号 和 项目名称 用来填充下拉框
     * @return 返回map
     */
    @GetMapping("project_num_and_name")
    public List<Map<String, Object>> getProjectNumAndProjectName(){
        return projectServiceImpl.getProjectNumAndProjectName();
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    @GetMapping("get_single_properties")
    public List getSingleProperties(String property) {
        return projectServiceImpl.getSingleProperty(property);
    }



    @GetMapping("schedule")
    public Map<String, Object>  findSchedule(@RequestParam Map<String, Object> propMap) {
        return projectServiceImpl.findSchedule(propMap);
    }

    @GetMapping("amount")
    public Map<String, Object> getProjectAmount(@RequestParam Map<String, Object> propMap){
        return projectServiceImpl.getProjectAmount(propMap);
    }
}
