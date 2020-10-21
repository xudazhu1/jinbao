package com.hnjbkc.jinbao.workload.userwork;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.organizationalstructure.department.DepartmentBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.workload.post.PostBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户工作关联表
 * @author 12
 * @Date 2019-10-17
 */
@Getter
@Setter
@Entity
@Table(name = "user_work")
public class UserWorkBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer	userWorkId;

    /**
     * 对应用户
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_work_user_id", referencedColumnName = "userId")
    private UserBean userBean;

    /**
     * 用户干活的部门 只能是实施部
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_work_user_department_id", referencedColumnName = "departmentId")
    private DepartmentBean departmentBean;


    /**
     * 所对应的职位
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "user_post_staff_middle" ,
            joinColumns = {@JoinColumn(name = "user_post_staff_middle_user_work_id" , referencedColumnName = "userWorkId")} ,
            inverseJoinColumns = {@JoinColumn(name = "user_post_staff_middle_post_id" ,referencedColumnName = "postId")})
    @JsonIgnoreProperties(value = {"userBeanList" , "postBean"   , "professionBean" , "supervisorUserList" , "captainUserList"})
    private PostBean staffPostBean;

    /**
     * 所对应的职位
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "user_post_supervisor_middle" ,
            joinColumns = {@JoinColumn(name = "user_post_supervisor_middle_user_work_id" , referencedColumnName = "userWorkId")} ,
            inverseJoinColumns = {@JoinColumn(name = "user_post_supervisor_middle_post_id" ,referencedColumnName = "postId")})
    @JsonIgnoreProperties(value = {"userBeanList" , "postBean"   , "professionBean" , "staffUserList" , "captainUserList"})
    private PostBean supervisorPostBean;

    /**
     * 所对应的职位
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "user_post_captain_middle" ,
            joinColumns = {@JoinColumn(name = "user_post_captain_middle_user_work_id" , referencedColumnName = "userWorkId")} ,
            inverseJoinColumns = {@JoinColumn(name = "user_post_captain_middle_post_id" ,referencedColumnName = "postId")})
    @JsonIgnoreProperties(value = {"userBeanList" , "postBean"   , "professionBean", "staffUserList", "supervisorUserList"})
    private PostBean captainPostBean;
}
