package com.hnjbkc.jinbao.squadgroupfee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.squadgroupfee.squadgroupfeestatus.SquadGroupFeeStatusBean;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 班组老板的bean
 * @author siliqiang
 * @date 2019/11/25
 */


@Data
@Entity
@Table(name = "squad_group_fee")
public class SquadGroupFeeBean implements Serializable {
    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer squadGroupFeeId;
    /**
     * 编号
     */
    private String squadGroupFeeNum;
    /**
     * 名称
     */
    private String squadGroupFeeName;
    /**
     * 联系方式
     */
    private String squadGroupFeeTel;
    /**
     * 公司
     */
    private String squadGroupFeeCompany;
    /**
     * 开票类目
     */
    private String squadGroupFeeContent;
    /**
     * 开票类型
     */
    private String squadGroupFeeGenre;
    /**
     * 录入时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date squadGroupFeeTime;
    /**
     * 用户外键
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squad_group_fee_user_id", referencedColumnName = "userId")
    private UserBean userBean;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squad_group_fee_status_id", referencedColumnName = "squadGroupFeeStatusId")
    private SquadGroupFeeStatusBean squadGroupFeeStatusBean;

}
