package com.hnjbkc.jinbao.number;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 记录支出编号的pojo类
 * @author siliqiang
 * @date 2019.9.3
 */
@Data
@Entity
@Table(name = "number")
public class NumberBean implements Serializable {

    /**
     * 编号的Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer numberId;

    /**
     * 记录编号的名称
     */
    private String numberName;

}
