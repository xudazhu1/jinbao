package com.hnjbkc.jinbao.organizationalstructure.user.userListBox;

import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 12
 * @Date 2019-10-18
 */
@RestController
@RequestMapping("user_list_box")
public class UserListBoxController {

    private UserListBoxServiceImpl userListBoxServiceImpl;

    @Autowired
    public void setUserListBoxServiceImpl(UserListBoxServiceImpl userListBoxServiceImpl) {
        this.userListBoxServiceImpl = userListBoxServiceImpl;
    }

    @PostMapping
    public Boolean add(UserListBoxBean userListBoxBean){
        return userListBoxServiceImpl.add(userListBoxBean) != null;
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return userListBoxServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(UserListBoxBean userListBoxBean){
        return userListBoxServiceImpl.update(userListBoxBean) != null;
    }

    @GetMapping
    public List<UserBean> findAll(UserListBoxBean userListBoxBean){
        return userListBoxServiceImpl.findAll(userListBoxBean);
    }
}
