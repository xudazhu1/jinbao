package com.hnjbkc.jinbao.implement.projecttype;

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
@Table(name = "project_type")
public class ProjectTypeBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer projectTypeId;

    /**
     * 项目类型
     */
    private String projectTypeName;
}
