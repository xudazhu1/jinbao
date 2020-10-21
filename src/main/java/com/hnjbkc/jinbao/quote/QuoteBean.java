package com.hnjbkc.jinbao.quote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.hqldao.annotations.HasOneToManyList;
import com.hnjbkc.jinbao.hqldao.annotations.OneToManyListInfo;
import com.hnjbkc.jinbao.organizationalstructure.job.JobBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.quote.quotefile.QuoteFileBean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author 12
 * @Date 2019-07-31
 */
@Getter
@Setter
@Entity
@Table(name = "quote")
@HasOneToManyList(hasClasses = {
        @OneToManyListInfo( propertyClass = QuoteFileBean.class , propertyName = "quoteFileBeanList" )
})
public class QuoteBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer	quoteId;

    /**
     * 报价编号
     */
    private String	quoteNum;

    /**
     * 报价项目名称
     */
    private String	quoteName;

    /**
     * 报价单位
     */
    private String quotePlace;

    /**
     * 项目内容
     */
    private String quoteProjectContent;


    /**
     * 报价人
     */
    private String quotePeople;

    /**
     * 报价金额
     */
    private Double	quoteMoney;

    /**
     * 报价内容
     */
    private String	quoteContent;

    /**
     * 报价状态
     */
    private String quoteStatus;

    /**
     * 创建人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_create_user_id", referencedColumnName = "userId")
    private UserBean createUserBean;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date quoteCreateTime;

    /**
     * 该数据的 职位权限 可见
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_job_id", referencedColumnName = "jobId")
    private JobBean createJobBean;

    /**
     * 报价文件
     */
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "quoteBean",cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"quoteBean"})
    private List<QuoteFileBean> quoteFileBeanList;
}
