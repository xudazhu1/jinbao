package com.hnjbkc.jinbao.organizationalstructure.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author siliqiang
 * @date 2019.7.11
 */
@Repository
public interface JobDao extends JpaRepository<JobBean,Integer> {

    /**
     * 为角色去除与权限的多对多关系
     * @param jobId id
     */
    @Modifying
    @Query(value = "delete from permission_job_middle where permission_job_middle_job_id=?1" , nativeQuery = true)
    void  deletePermissionConnectionByRoleId(Integer jobId);
}
