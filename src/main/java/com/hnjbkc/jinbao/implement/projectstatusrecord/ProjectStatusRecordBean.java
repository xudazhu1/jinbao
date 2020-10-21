package com.hnjbkc.jinbao.implement.projectstatusrecord;

import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.implement.ImplementBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 12
 * @Date 2019-08-13
 */
@Getter
@Setter
@Entity
@Table(name = "project_status_record")
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "implementBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST)
})
public class ProjectStatusRecordBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer projectStatusRecordId;

    /**
     *  项目状态名称
     */
    private String projectStatusRecordName;

    /**
     * 记录操作人
     */
    private String projectStatusRecordOperator;

    /**
     * 记录时间
     */
    private Date projectStatusRecordTime;

    /**
     * 跟实施是一对多 一个实施部 有多个项目状态的历史记录
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_status_record_impl_id",referencedColumnName = "implementId")
    private ImplementBean implementBean;

}
