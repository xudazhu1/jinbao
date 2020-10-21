package com.hnjbkc.jinbao.workload.captain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 12
 * @Date 2019-09-06
 */
@RestController
@RequestMapping("captain")
public class CaptainController {

    private CaptainServiceImpl captainPostServiceImpl;

    @Autowired
    public void setCaptainPostServiceImpl(CaptainServiceImpl captainPostServiceImpl) {
        this.captainPostServiceImpl = captainPostServiceImpl;
    }

    @PostMapping
    public Boolean add(CaptainBean captainPostBean){
        return captainPostServiceImpl.add(captainPostBean) != null;
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return captainPostServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(CaptainBean captainPostBean){
        return captainPostServiceImpl.update(captainPostBean) != null;
    }

    @GetMapping
    public List<CaptainBean> findCaptainPosts(CaptainBean captainPostBean){
        List<CaptainBean> captainPosts = captainPostServiceImpl.findCaptainPosts(captainPostBean);
        return captainPosts;
    }
}
