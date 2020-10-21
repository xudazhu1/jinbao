package com.hnjbkc.jinbao.disburse.paymentstatus;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 12
 * @Date 2019-09-18
 */
@Getter
@Setter
@Entity
@Table(name = "payment_status")
public class PaymentStatusBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentStatusId;


    private String paymentStatusName;

}
