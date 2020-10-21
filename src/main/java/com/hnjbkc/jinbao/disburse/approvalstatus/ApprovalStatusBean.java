package com.hnjbkc.jinbao.disburse.approvalstatus;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 审核状态的pojo类
 * @author siliqiang\
 * @date 2019.9.4
 */
@Data
@Entity
@Table(name = "approval_status")
public class ApprovalStatusBean implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /**
     * 状态表的id
     */
    private Integer approvalStatusId;

    /**
     * 状态名称
     */
    private  String approvalStatusName;

}
