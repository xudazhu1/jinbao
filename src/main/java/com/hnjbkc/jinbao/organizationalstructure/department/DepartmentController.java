package com.hnjbkc.jinbao.organizationalstructure.department;

import com.hnjbkc.jinbao.organizationalstructure.company.CompanyBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @author siliqiang
 * @date 2019.7.11
 */
@RestController
@RequestMapping("department")
public class DepartmentController {
    private DepartmentServiceImpl departmentServiceImpl;

    @Autowired
    public void setDepartmentServiceImpl(DepartmentServiceImpl departmentServiceImpl) {
        this.departmentServiceImpl = departmentServiceImpl;
    }

    /**
     * 添加的方法
     *
     * @param departmentBean 部门的对象
     * @return 返回的是一个布尔值
     */
    @PostMapping
    public Boolean addDepartment(DepartmentBean departmentBean) {
        return departmentServiceImpl.add(departmentBean) != null;
    }

    /**
     * 删除的方法
     *
     * @param id id
     * @return 布尔值
     */
    @DeleteMapping
    public Boolean deleteDepartment(Integer id) {
        return departmentServiceImpl.delete(id);
    }

    /**
     * 修改的方法
     *
     * @param department 公司的对象
     * @return 布尔值
     */
    @PutMapping
    public Boolean updateDepartment(DepartmentBean department) {
        return departmentServiceImpl.update(department) != null;
    }

    /**
     * 查询并且分页的方法
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping()
    public Page getDepartments(@RequestParam Map<String, Object> propMap) {
        return departmentServiceImpl.get(propMap, PageableUtils.producePageable4Map(propMap, "departmentId"));
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    @GetMapping("get_single_properties")
    public List getSingleProperties(String property) {
        return departmentServiceImpl.getSingleProperty(property);
    }

    /**
     * 根据部门id 获取其下 所有的 用户
     *
     * @param id
     * @return
     */
    @GetMapping("user_list")
    public List<UserBean> getUserList(Integer id) {
        return departmentServiceImpl.getUserList(id);
    }

    /**
     * 根据部门名称  获取 部门
     *
     * @param departmentBean
     * @return
     */
    @GetMapping("department_name")
    public List<DepartmentBean> findDepartmentName(DepartmentBean departmentBean) {
        return departmentServiceImpl.findDepartmentName(departmentBean);
    }

    /**
     * 获取 当前登录用户 对应公司 对应的实施部 旗下的 所有 部门  (常用于 获取 实施部)
     * @param propMap 可加条件
     * @param request 请求
     * @return 返回部门List
     */
    @GetMapping("implement_department")
    public List<DepartmentBean> getImplementDepartment(@RequestParam Map<String, Object> propMap, HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserBean user = (UserBean) session.getAttribute("user");
        Integer companyId = getCompanyId(user.getJobBean().getDepartmentBean());
        propMap.put("companyBean.companyId", companyId);
        propMap.put("departmentName", "实施部");
        return departmentServiceImpl.getImplementDepartment(propMap);
    }

    /**
     * 获取 当前登录用户 对应公司 旗下的 所有部门  (常用于 财务 铺部门中)
     * @param propMap 可加条件
     * @param request 请求
     * @return 返回部门List
     */
    @GetMapping("company_department")
    public List<DepartmentBean> getCompanyDepartment(@RequestParam Map<String, Object> propMap, HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserBean user = (UserBean) session.getAttribute("user");
        Integer companyId = getCompanyId(user.getJobBean().getDepartmentBean());
        propMap.put("companyBean.companyId", companyId);
        return departmentServiceImpl.getCompanyDepartment(propMap, PageableUtils.producePageable4Map(propMap, "departmentId"));
    }

    /**
     * 传入 部门bean  获取 该部门(向上传递获取父部门) 是哪个公司的
     * @param departmentBean 传入部门
     * @return 返回公司id
     */
    private Integer getCompanyId(DepartmentBean departmentBean) {
        if (departmentBean.getCompanyBean() == null) {
            CompanyBean companyBean = departmentBean.getParentDepartmentBean().getCompanyBean();
            if (companyBean == null) {
                getCompanyId(departmentBean.getParentDepartmentBean());
            } else {
                return companyBean.getCompanyId();
            }
        }
        return departmentBean.getCompanyBean().getCompanyId();
    }

    @GetMapping("user_for_department")
    public List<UserBean> findUserForDepartment(DepartmentBean departmentBean){
        return departmentServiceImpl.findUserForDepartment(departmentBean);
    }

}
