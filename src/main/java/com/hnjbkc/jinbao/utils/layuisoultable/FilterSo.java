package com.hnjbkc.jinbao.utils.layuisoultable;

import lombok.Data;

import java.util.List;

/**
 * 表头筛选条件实体类
 *
 * @author Yujie Yang
 * @date 2019-01-02 10:00
 */
@Data
public class FilterSo {

	/**
	 * 唯一id
	 */
	private Long id;
	/**
	 * 前缀 and、or
	 */
	private String prefix;
	/**
	 * 模式 in、condition、date
	 */
	private String mode;
	/**
	 * 字段名
	 */
	private String field;
	/**
	 * 筛选类型
	 */
	private String type;
	/**
	 * 是否有分隔符
	 */
	private String split;
	/**
	 * 筛选值
	 */
	private String value;
	/**
	 * 筛选值
	 */
	private List<String> values;

	/**
	 * 子组数据
	 */
	private List<FilterSo> children;


}
