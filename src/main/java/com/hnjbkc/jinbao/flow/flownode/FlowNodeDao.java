package com.hnjbkc.jinbao.flow.flownode;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author xudaz
 */
@Repository
public interface FlowNodeDao extends JpaRepository<FlowNodeBean,Integer> {
    /**
     * 为节点去除与角色的多对多关系
     * @param flowSubId id
     */
    @Modifying
    @Query(value = "delete from flow_sub_permission where flow_sub_permission_flow_sub_id=?1" , nativeQuery = true)
    void  deleteRoleConnectionByFlowSubId(Integer flowSubId);

    /**
     * 为节点去除与职位的多对多关系
     * @param flowSubId id
     */
    @Modifying
    @Query(value = "delete from flow_sub_permission where flow_sub_permission_flow_sub_id=?1" , nativeQuery = true)
    void  deleteJobConnectionByFlowSubId(Integer flowSubId);

    /**
     * 根据页面路径来匹配流程节点
     * @param permissionTag 页面路径
     * @return 节点 的集合
     */
    @EntityGraph
    List<FlowNodeBean> getAllByEditPageBeanPermissionTag(String permissionTag);



    /**
     * 通过表名和字段名 获取数据库该字段的注释 Comment
     * @param tableName 表名
     * @param propertyName 字段
     * @return 返回注释
     */
    @SuppressWarnings({"SpringDataRepositoryMethodReturnTypeInspection", "SqlResolve"})
    @Query(value = "SELECT  column_comment FROM  Information_schema.columns " +
            "WHERE " +
            " table_Name=?1 " +
            " AND column_name=?2 " , nativeQuery = true)
     List<String> getCommentByTableAndColumn(String tableName, String propertyName);

}
