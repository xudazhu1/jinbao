package com.hnjbkc.jinbao.workload.post;

import com.hnjbkc.jinbao.common.CommonResult;
import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 12
 * @Date 2019-09-30
 */
@RestController
@RequestMapping("post")
public class PostController {

    private PostServiceImpl postServiceImpl;

    @Autowired
    public void setPostServiceImpl(PostServiceImpl postServiceImpl) {
        this.postServiceImpl = postServiceImpl;
    }

    @PostMapping
    public Boolean add(PostBean postBean){
        return postServiceImpl.add(postBean) != null;
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return postServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(PostBean postBean){
        return postServiceImpl.update(postBean) != null;
    }

    @GetMapping
    public List<Map<String, Object>> listPost(PostBean postBean){
        return postServiceImpl.listPost(postBean);
    }

    /**
     * 获取岗位下 同一个 工种的  所有 用户user  所以 必须传 参数 professionId = ?
     * @param propMap
     * @return
     */
    @GetMapping("user_list")
    public List<PostBean> listUser(@RequestParam Map<String, Object> propMap){
        return postServiceImpl.listUser(propMap);
    }

    @GetMapping("all")
    public CommonResult findAll(@RequestParam Map<String, Object> propMap){
        return postServiceImpl.search(propMap, PageableUtils.producePageable4Map(propMap, "postId"));
    }
}
