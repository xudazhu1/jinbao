package com.hnjbkc.jinbao.invoiceandmoneyback;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 给收入表自动发号用的pojo
 * @author xudaz
 * @date 2019/9/11
 */
@Entity
@Data
@Table(name = "auto_num")
public class AutoNumBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer incomeNum;

}
