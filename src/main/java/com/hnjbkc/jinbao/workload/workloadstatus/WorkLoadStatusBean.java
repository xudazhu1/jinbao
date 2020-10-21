package com.hnjbkc.jinbao.workload.workloadstatus;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 12
 * @Date 2019-09-20
 */
@Getter
@Setter
@Entity
@Table(name = "work_load_status")
public class WorkLoadStatusBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer workLoadStatusId;

    private String workLoadStatusName;
}
