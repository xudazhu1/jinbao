package com.hnjbkc.jinbao.workload.captain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author 12
 * @Date 2019-09-06
 */
@Repository
public interface CaptainDao extends JpaRepository<CaptainBean,Integer> {

    @Modifying
    @Query(value = "delete from captain where captain_id = ?1",nativeQuery = true)
    @Override
    void deleteById(Integer integer);

}
