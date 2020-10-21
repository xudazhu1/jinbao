package com.hnjbkc.jinbao.implement.secondparty;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 12
 * @Date 2019-08-07
 */
@Getter
@Setter
@Entity
@Table(name = "second_party")
public class SecondPartyBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer secondPartyId;

    /**
     * 乙方名称
     */
    private String secondPartyName;
}
