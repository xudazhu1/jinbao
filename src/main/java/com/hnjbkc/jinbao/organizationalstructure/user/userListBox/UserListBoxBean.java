package com.hnjbkc.jinbao.organizationalstructure.user.userListBox;

import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.organizationalstructure.user.moduleList.ModuleListBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 12
 * @Date 2019-10-18
 */
@Getter
@Setter
@Entity
@Table(name = "user_list_box")
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "userBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST )
})
public class UserListBoxBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer	userListBoxId;

    @ManyToOne
    @JoinColumn(name = "user_list_box_module_list_id",referencedColumnName = "moduleListId")
    private ModuleListBean moduleListBean;

    /**
     * 用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_list_box_user_id",referencedColumnName = "userId")
    private UserBean userBean;

}
