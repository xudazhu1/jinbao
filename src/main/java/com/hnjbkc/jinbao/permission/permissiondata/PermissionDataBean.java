package com.hnjbkc.jinbao.permission.permissiondata;

import com.hnjbkc.jinbao.organizationalstructure.job.JobBean;
import com.hnjbkc.jinbao.organizationalstructure.role.RoleBean;
import com.hnjbkc.jinbao.permission.PermissionBean;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 数据权限级别表
 * @author xudaz
 */
@SuppressWarnings("WeakerAccess")
@Data
@Table(name = "permission_data")
@Entity
public class PermissionDataBean implements Serializable {

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer permissionDataId;

    /**
     * 权限级别 1 本人 2 本角色 3 本部门 4 本分公司 5 集团
     */
    private Integer permissionDataLevel;

    /**
     * 所对应的权限页
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_data_permission_id" , referencedColumnName = "permissionId")
    private PermissionBean permissionBean;

    /**
     * 所对应的角色
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_data_role_id" , referencedColumnName = "roleId")
    private RoleBean roleBean;

    /**
     * 所对应的职位
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_data_job_id" , referencedColumnName = "jobId")
    private JobBean jobBean;


}
