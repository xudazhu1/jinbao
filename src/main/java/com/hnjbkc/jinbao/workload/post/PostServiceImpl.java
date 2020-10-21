package com.hnjbkc.jinbao.workload.post;

import com.hnjbkc.jinbao.common.CommonPage;
import com.hnjbkc.jinbao.common.CommonResult;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.project.ProjectBean;
import com.hnjbkc.jinbao.utils.AttrExchange;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 12
 * @Date 2019-09-30
 */
@Service
public class PostServiceImpl implements BaseService<PostBean> {

    private PostDao postDao;

    @Autowired
    public void setPostDao(PostDao postDao ) {
        this.postDao  = postDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Override
    public  PostBean add(PostBean postBean){
        return postDao.save(postBean);
    }

    @Override
    public Boolean delete(Integer id){
        postDao.deleteById(id);
        return true;
    }

    @Override
    public PostBean update(PostBean postBean){
        Optional<PostBean> byId = postDao.findById(postBean.getPostId());
        if (byId.isPresent()){
            PostBean postDbBean = byId.get();
            AttrExchange.onAttrExchange(postDbBean,postBean);
            return postDao.save(postDbBean);
        }
        return null;
    }

    public List<Map<String, Object>> listPost(PostBean postBean) {
        return postDao.finPostList(postBean.getPostLevel(),postBean.getDepartmentBean().getDepartmentId());
    }

    public List<PostBean> listUser(@RequestParam Map<String, Object> propMap) {
        String hql = "select p FROM PostBean p " +
                "LEFT JOIN p.departmentBean d " +
                "LEFT JOIN ProfessionBean pf " +
                "ON d.departmentId = pf.departmentBean.departmentId " +
                "LEFT JOIN UserBean u " +
                "ON p.postId = u.postBean.postId " +
                "where u.userId is not null " +
                "and pf.professionId = " + propMap.get("professionId");
        List<PostBean> list = (List<PostBean>)sqlUtilsDao.exSqlCustom(PostBean.class, hql, null);
        return list;
    }

    public CommonResult search(Map<String, Object> propMap, Pageable pageRequest) {
        String resultMap = "new Map(" +
                "postId as postId, " +
                "postName as postName, " +
                "postLevel as postLevel, " +
                "departmentBean.departmentName as departmentName, " +
                "postCreateTime as postCreateTime" +
                ")";
        Page<Map<String,Object>> postBeans = sqlUtilsDao.resultMapByCustomProps(PostBean.class, resultMap, propMap, pageRequest);
        return CommonResult.success(CommonPage.restPage(postBeans));
    }
}
