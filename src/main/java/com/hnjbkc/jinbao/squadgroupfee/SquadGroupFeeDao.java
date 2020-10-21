package com.hnjbkc.jinbao.squadgroupfee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SquadGroupFeeDao extends JpaRepository<SquadGroupFeeBean,Integer> {
    @Query(value = "select count(*) from squad_group_fee", nativeQuery = true)
    Long getCount();
}
