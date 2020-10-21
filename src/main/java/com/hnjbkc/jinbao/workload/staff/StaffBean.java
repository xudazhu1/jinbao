package com.hnjbkc.jinbao.workload.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.workload.post.PostBean;
import com.hnjbkc.jinbao.workload.profession.ProfessionBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 员工岗位
 * @author 12
 * @Date 2019-09-05
 */
@Getter
@Setter
@Entity
@Table(name = "staff")
public class StaffBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer	staffId;

    /**
     * 员工岗位名称
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_post_id",referencedColumnName = "postId")
    private PostBean postBean;

    /**
     * 员工岗位单价
     */
    private Double	staffPrice;

    /**
     * 工种
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_profession_id",referencedColumnName = "professionId")
    @JsonIgnoreProperties({"supervisorBeanList" , "captainPostBeanList" , "staffBeanList" , "createJobBean" , "createUserBean"})
    private ProfessionBean professionBean;


    /**
     * 说明
     */
    private String staffExplain;
}
