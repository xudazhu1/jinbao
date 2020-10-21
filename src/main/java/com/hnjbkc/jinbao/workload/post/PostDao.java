package com.hnjbkc.jinbao.workload.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author 12
 * @Date 2019-09-30
 */
@Repository
public interface PostDao extends JpaRepository<PostBean,Integer> {

    @Query(value = "select p.post_Id,p.post_level,p.post_name from post p where post_level = ?1 and post_department_id = ?2",nativeQuery = true)
    List<Map<String,Object>> finPostList(String postLevel,Integer departmentId);

}
