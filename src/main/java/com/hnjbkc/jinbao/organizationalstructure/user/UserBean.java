package com.hnjbkc.jinbao.organizationalstructure.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.organizationalstructure.job.JobBean;
import com.hnjbkc.jinbao.organizationalstructure.role.RoleBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author siliqiang
 * @date 2019.7.9
 * 用户的pojo类
 */

@Entity
@Table(name = "user")
@Setter
@Getter
@NamedEntityGraph(name="userBean.Graph",  attributeNodes={
        @NamedAttributeNode(value = "jobBean" , subgraph = "jobBean.Graph"),
})
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "staffPostBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
        @MyGraphIgnoreInfo( fieldPath = "supervisorPostBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
        @MyGraphIgnoreInfo( fieldPath = "captainPostBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
})
public class UserBean implements Serializable {
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户密码
     */
    private String userPassword;
    /**
     * 用户的电话
     */
    private String userTelephone;

    /**
     * 所有角色 多对多
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role_middle" ,
            joinColumns = {@JoinColumn(name = "user_role_middle_user_id" , referencedColumnName = "userId")} ,
            inverseJoinColumns = {@JoinColumn(name = "user_role_middle_role_id" ,referencedColumnName = "roleId")})
    @JsonIgnoreProperties(value = {"userBeans" , "permissionDataBeans"   , "permissionBeans"})
    private List<RoleBean> roleBeans;

    /**
     * 所对应的职位
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_job_id", referencedColumnName = "jobId")
    @JsonIgnoreProperties({"userBeans" , "permissionDataBeans"})
    private JobBean jobBean;



    @Override
    public String toString() {
        return "UserBean{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userTelephone='" + userTelephone + '\'' +
                '}';
    }
}
