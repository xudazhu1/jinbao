package com.hnjbkc.jinbao.utils.restresponse;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 分页结果集的封装
 * @author qiuh
 */
@Data
public class PagingData<T> implements Serializable{
    private static final long serialVersionUID = 3709009993334556792L;
    /**
     * 总页数
     */
    private int totalPage;
    /**
     * 当前页码
     */
    private int currentPage;
    /**
     * 总记录数
     */
    private long totalRows;
    /**
     * 每页记录数
     */
    private int pagePerSize;
    /**
     * 数据集
     */
    private List<Map> rows;

    public PagingData(){
        this.rows = new ArrayList<>();
    }
}
