package com.hnjbkc.jinbao.management;

import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import lombok.Data;

import javax.persistence.*;

/**
 * 合伙人表
 *
 * @author xudaz
 * @date 2019/9/29
 */
@SuppressWarnings("WeakerAccess")
@Data
@Entity
@Table(name = "management_partners")
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "userBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
        @MyGraphIgnoreInfo( fieldPath = "refereesUserBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
})
public class ManagementPartnersBean {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer managementPartnersId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_partners_user_id", referencedColumnName = "userId")
    private UserBean userBean;

    /**
     * 身份标识(外部合伙人 | 内部合伙人 | 合作伙伴)
     */
    private String managementPartnersIdentity;
    /**
     * 推荐人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_partners_referees_user_id" , referencedColumnName = "userId")
    private UserBean refereesUserBean;
    /**
     * 初始等级
     */
    private String managementPartnersRefereesInitLevel;
    /**
     * 等级
     */
    private String managementPartnersRefereesLevel;

}
