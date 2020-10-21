package com.hnjbkc.jinbao.project.projectlocation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 12
 * @Date 2019-08-12
 */
@Getter
@Setter
@Entity
@Table(name = "project_location")
public class ProjectLocationBean  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer projectLocationId;

    /**
     *  项目地点名称
     */
    private String projectLocationName;


    /**
     * 归属地点
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_location_parent", referencedColumnName = "projectLocationId")
    @JsonIgnoreProperties({"projectLocationParent"})
    private ProjectLocationBean projectLocationParent;


}
