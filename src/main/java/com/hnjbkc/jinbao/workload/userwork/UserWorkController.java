package com.hnjbkc.jinbao.workload.userwork;

import com.hnjbkc.jinbao.common.CommonResult;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 12
 * @Date 2019-10-17
 */
@RestController
@RequestMapping("user_work")
public class UserWorkController {

    private UserWorkServiceImpl userWorkServiceImpl;

    @Autowired
    public void setUserWorkServiceImpl(UserWorkServiceImpl userWorkServiceImpl) {
        this.userWorkServiceImpl = userWorkServiceImpl;
    }

    @PostMapping
    public Boolean add(UserWorkBean userWorkBean){

        return userWorkServiceImpl.add(userWorkBean) != null;
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return userWorkServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(UserWorkBean userWorkBean){
        return userWorkServiceImpl.update(userWorkBean) != null;
    }

    @GetMapping("select_user")
    public List<UserBean> findNotPostUserList(){
        return userWorkServiceImpl.findNotPostUserList();
    }

    @GetMapping
    public List<UserWorkBean> findAll(UserWorkBean workBean){
        return userWorkServiceImpl.findAll(workBean);
    }

    @GetMapping("all")
    public CommonResult findAll(@RequestParam Map<String, Object> propMap){
        return userWorkServiceImpl.search(propMap, PageableUtils.producePageable4Map(propMap, "userWorkId"));
    }

    @GetMapping("search")
    public CommonResult searchUserWork(@RequestParam Map<String, Object> propMap){
        return userWorkServiceImpl.searchUserWork(propMap, PageableUtils.producePageable4Map(propMap, "userWorkId"));
    }
}
