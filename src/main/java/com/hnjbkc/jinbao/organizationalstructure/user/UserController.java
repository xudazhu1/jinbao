package com.hnjbkc.jinbao.organizationalstructure.user;

import com.hnjbkc.jinbao.permission.PermissionBean;
import com.hnjbkc.jinbao.permission.datarequest.IgnoreRequest;
import com.hnjbkc.jinbao.utils.MyTreeBean;
import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xudaz
 * @date 2019/7/11
 */
@RestController
@RequestMapping("user")
public class UserController {

    private UserServiceImpl userService;

    @Autowired
    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    /**
     * 添加的方法
     *
     * @param userBean 岗位对象
     * @return 返回的是一个布尔值
     */
    @PostMapping
    public Boolean addUser(UserBean userBean) {
        return userService.add(userBean) != null;
    }

    /**
     * 用户登陆的方法
     *
     * @param name     用户名称
     * @param password 用户的密码
     * @param session  session值
     * @return 返回的是一个用户的对象
     */
    @PostMapping("login")
    @IgnoreRequest
    public UserBean login(String name, String password, HttpSession session) {
        UserBean userBean = userService.login(name, password);
        if (userBean == null) {
            return null;
        }
        Integer userId = userBean.getUserId();
        Set<PermissionBean> permissions = userService.getPermissions(userId);
        session.setAttribute("user", userBean);
        session.setAttribute("permissionBeans", permissions);
        return userBean;
    }

    @PostMapping("logout")
    @IgnoreRequest
    public Boolean logout(HttpSession session) {
        session.removeAttribute("user");
        return true;
    }

    /**
     * 删除的方法
     *
     * @param id 用户id
     * @return 布尔值
     */
    @DeleteMapping
    public Boolean deleteUser(Integer id) {
        return userService.delete(id);
    }

    /**
     * 修改的方法
     *
     * @param userBean 岗位的对象
     * @return 布尔值
     */
    @PutMapping
    public Boolean updateUser(UserBean userBean) {
        return userService.update(userBean) != null;
    }

    @PutMapping("update_password")
    public Boolean updatePassword( @RequestParam("newPassword")String newPassword,@RequestParam("name") String name,@RequestParam("oldPassword") String oldPassword){
        return userService.update(newPassword,name,oldPassword);
    }

    @PutMapping("post")
    public Boolean addPostUser(UserBean userBean) {
        return userService.addPostUser(userBean) != null;
    }

    @PutMapping("remove_referee")
    public Boolean removeReferee4UserId(Integer id) {
        return userService.removeReferee4UserId(id);
    }

    /**
     * 查询并且分页的方法
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping("s")
    public Page searchUsers(@RequestParam Map<String, Object> propMap) {
        return userService.search(propMap, PageableUtils.producePageable4Map(propMap, "userId"));
    }

    /**
     * 查询并且分页的方法(不模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping
    public Page getUsers(@RequestParam Map<String, Object> propMap) {
        return userService.get(propMap, PageableUtils.producePageable4Map(propMap, "userId"));
    }
    @GetMapping("id")
    public Page getUsers4Id(@RequestParam Map<String, Object> propMap) {
        return userService.getUsers4Id(propMap, PageableUtils.producePageable4Map(propMap, "userId"));
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    @GetMapping("get_single_properties")
    public List getSingleProperties(String property) {
        return userService.getSingleProperty(property);
    }

    @GetMapping("tree")
    public List<MyTreeBean> getTree(@RequestParam Map<String, Object> propMap) {
        //此处排序字段特殊
        Pageable pageRequest = PageableUtils.producePageable4Map(propMap, "companyId");
        return userService.getTree(propMap, pageRequest);

    }

    /**
     * 查询当前用户登录的所有信息
     *
     * @param session session里面存的用户信息
     * @return 返回的是一个字符串
     */
    @GetMapping("get_session_user")
    @IgnoreRequest
    public UserBean getSessionUserName(HttpSession session) {
        return (UserBean) session.getAttribute("user");
    }

    @GetMapping("permissions")
    public Object getPermissions(HttpSession session) {
        return session.getAttribute("permissionBeans");
    }

    /**
     * 获取 所有用户
     * @return 返回 id name 数组
     */
    @GetMapping("user_all")
    public List getUserAll(){
        return userService.findAll();
    }
}
