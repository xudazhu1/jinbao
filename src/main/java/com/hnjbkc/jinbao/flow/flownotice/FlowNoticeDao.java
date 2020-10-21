package com.hnjbkc.jinbao.flow.flownotice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 12
 */
@Repository
public interface FlowNoticeDao extends JpaRepository<FlowNoticeBean,Integer> {

    /**
     * 为传入的userId 查找所有为待办或者未读的消息数量
     * @param userId 用户Id
     * @param statusList 待办 or 未读
     * @return 返回数量
     */
    Integer countByObjectUserBeanUserIdAndFlowNoticeStatusIn(Integer userId , List<String> statusList);
}
