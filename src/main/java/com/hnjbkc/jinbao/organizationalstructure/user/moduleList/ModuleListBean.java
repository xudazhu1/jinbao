package com.hnjbkc.jinbao.organizationalstructure.user.moduleList;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 页面下拉框模块
 * @author 12
 * @Date 2019-10-18
 */
@Getter
@Setter
@Entity
@Table(name = "module_list")
public class ModuleListBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer	moduleListId;

    /**
     * 模块类型
     */
    private String	moduleListType;

    /**
     * 模块详情
     */
    private String	moduleListDetail;

}
