package com.hnjbkc.jinbao.squadgroupfee.squadgroupfeestatus;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author siliqiang
 * @date 2019/11/25
 */
@Data
@Entity
@Table(name = "squad_group_fee_status")
public class SquadGroupFeeStatusBean implements Serializable {
    /**
     * 状态id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer squadGroupFeeStatusId;

    /**
     * 状态名称
     */
    private String squadGroupFeeStatusName;
}
