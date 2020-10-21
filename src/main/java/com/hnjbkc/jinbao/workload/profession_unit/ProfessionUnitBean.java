package com.hnjbkc.jinbao.workload.profession_unit;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 12
 * @Date 2019-09-16
 */
@Getter
@Setter
@Entity
@Table(name = "profession_unit")
public class ProfessionUnitBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer	professionUnitId;

    private String	professionUnitName;

}
