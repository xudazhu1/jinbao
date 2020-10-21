package com.hnjbkc.jinbao.project.earningscompany;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 12
 * @Date 2019-08-13
 */
@Getter
@Setter
@Entity
@Table(name = "earnings_company")
public class EarningsCompanyBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer earningsCompanyId;

    /**
     *  收益单位名称
     */
    private String earningsCompanyName;

    /**
     *  收益单位标识符
     */
    private String earningsCompanyTag;

    /**
     * 当前选中 1 是选中 true
     */
    private Boolean earningsCompanyStatus;
}
