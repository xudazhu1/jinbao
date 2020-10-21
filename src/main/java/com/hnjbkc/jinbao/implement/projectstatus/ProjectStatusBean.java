package com.hnjbkc.jinbao.implement.projectstatus;

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
@Table(name = "project_status")
public class ProjectStatusBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer projectStatusId;

    /**
     * 项目状态
     */
    private String projectStatusName;
}
