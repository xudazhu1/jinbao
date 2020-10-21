package com.hnjbkc.jinbao.workload.staff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author 12
 * @Date 2019-09-05
 */
@Repository
public interface StaffDao extends JpaRepository<StaffBean,Integer> {

    @Modifying
    @Query(value = "delete from staff where staff_id = ?1",nativeQuery = true)
    @Override
    void deleteById(Integer integer);

}
