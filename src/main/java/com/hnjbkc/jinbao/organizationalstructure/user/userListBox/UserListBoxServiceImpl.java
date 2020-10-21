package com.hnjbkc.jinbao.organizationalstructure.user.userListBox;

import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 12
 * @Date 2019-10-18
 */
@Service
public class UserListBoxServiceImpl implements BaseService<UserListBoxBean> {

    private UserListBoxDao userListBoxDao;

    @Autowired
    public void setUserListBoxDao(UserListBoxDao userListBoxDao ) {
        this.userListBoxDao  = userListBoxDao;
    }

    @Override
    public  UserListBoxBean add(UserListBoxBean userListBoxBean){
        return userListBoxDao.save(userListBoxBean);
    }

    @Override
    public Boolean delete(Integer id){
        userListBoxDao.deleteById(id);
        return true;
    }

    @Override
    public UserListBoxBean update(UserListBoxBean userListBoxBean){
        return userListBoxDao.save(userListBoxBean);
    }

    public List<UserBean> findAll(UserListBoxBean userListBoxBean) {
        List<UserListBoxBean> all = userListBoxDao.findAll(Example.of(userListBoxBean));
        List<UserBean> userBeanList = new ArrayList<>();
        for (UserListBoxBean listBoxBean : all) {
            userBeanList.add(listBoxBean.getUserBean());
        }
        return userBeanList;
    }
}
