package com.hnjbkc.jinbao.organizationalstructure.user.userListBox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 12
 * @Date 2019-10-18
 */
@Repository
public interface UserListBoxDao extends JpaRepository<UserListBoxBean,Integer> {

}