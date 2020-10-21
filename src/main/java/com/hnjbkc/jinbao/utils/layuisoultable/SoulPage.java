package com.hnjbkc.jinbao.utils.layuisoultable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装table查询数据
 *
 * @author Yujie Yang
 * @date 2018/7/17 18:42
 */
@Data
public class SoulPage {

    /**
     * 查询条件
     */
    @JsonIgnore
    private List<FilterSo> filterSoses = new ArrayList<>();

    /**
     * 当前页 从1开始
     */
    @JsonIgnore
    private Integer page = 1;

    /**
     * 页大小
     */
    @JsonIgnore
    private Integer limit = Integer.MAX_VALUE;


    /**
     * 表名
     */
    private String tableName;

    /**
     * join 表们
     */
    @JsonIgnore
    private String joins;

    /**
     *  select  字段们
     */
    @JsonIgnore
    private String selectFields;

    /**
     * 排序信息
     */
    @JsonIgnore
    private String field;

    /**
     * 全局搜索
     */
    private String globalSearch;

    /**
     * 分组 xxx , xxx $分配 xxx$xxx
     */
    private String groupBy;

    /**
     * 自定义条件 $ 分割
     */
    private String customWhere;

    @JsonIgnore
    private String order = "asc";

    public SoulPage() {
    }



}
