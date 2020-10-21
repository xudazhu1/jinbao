package com.hnjbkc.jinbao.workload.userwork;

import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 12
 * @Date 2019-10-17
 */
@Repository
public interface UserWorkDao extends JpaRepository<UserWorkBean,Integer> {

}
