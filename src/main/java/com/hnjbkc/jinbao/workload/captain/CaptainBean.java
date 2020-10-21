package com.hnjbkc.jinbao.workload.captain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.workload.post.PostBean;
import com.hnjbkc.jinbao.workload.profession.ProfessionBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 12
 * @Date 2019-09-06
 */
@Getter
@Setter
@Entity
@Table(name = "captain")
public class CaptainBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer	captainId;

    /**
     * 员工岗位名称
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "captain_post_id",referencedColumnName = "postId")
    private PostBean postBean;

    /**
     * 队长岗位单价
     */
    private Double	captainPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "captain_profession_id",referencedColumnName = "professionId")
    @JsonIgnoreProperties({"supervisorBeanList" , "captainPostBeanList" , "staffBeanList" , "createJobBean" , "createUserBean"})
    private ProfessionBean professionBean;

    /**
     * 说明
     */
    private String captainExplain;
}
