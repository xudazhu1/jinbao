package com.hnjbkc.jinbao.workload.supervisor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.hqldao.annotations.HasOneToManyList;
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
@Table(name = "supervisor")
@HasOneToManyList(hasClasses = {

})
public class SupervisorBean  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer	supervisorId;

    /**
     * 主管岗位名称
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_post_id",referencedColumnName = "postId")
    private PostBean postBean;

    /**
     * 主管岗位单价
     */
    private Double	supervisorPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_profession_id",referencedColumnName = "professionId")
    @JsonIgnoreProperties({"supervisorBeanList" , "captainPostBeanList" , "staffBeanList" , "createJobBean" , "createUserBean"})
    private ProfessionBean professionBean;

//    /**
//     * 员工岗位
//     */
//    @OneToMany(mappedBy = "supervisorBean",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
//    @JsonIgnoreProperties(value = {"roleBeans","jobBean","staffBean","supervisorBean","captainBean"})
//    private List<UserBean> userBeanList;

    /**
     * 说明
     */
    private String supervisorExplain;
}
