package com.hnjbkc.jinbao.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionDao extends JpaRepository<PermissionBean,Integer> {

    @Query(value = "from PermissionBean as p where p.permissionTag like %?1 " )
    PermissionBean selectTagIfNull(String tag);
}
