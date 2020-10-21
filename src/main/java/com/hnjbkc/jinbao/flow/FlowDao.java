package com.hnjbkc.jinbao.flow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author xudaz
 */
@Repository
public interface FlowDao extends JpaRepository<FlowBean,Integer> {

    /**
     * 为节点去除与职位的多对多关系
     * @param flowId id
     */
    @Modifying
    @Query(value = "delete from flow_company_middle where flow_company_middle_flow_id=?1" , nativeQuery = true)
    void  deleteCompConnectionByFlowId(Integer flowId);

}
