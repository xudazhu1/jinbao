package com.hnjbkc.jinbao.config.entitygraph;

import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * @author xudaz
 */
@RestController
@RequestMapping("test")
public class TestController {



    UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }


    @GetMapping
    public Object get(UserBean userBean ) {
        return userDao.findAll(Example.of( userBean ) );
    }

}
