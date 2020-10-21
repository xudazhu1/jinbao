package com.hnjbkc.jinbao.implement.implementrecord;

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
 * @Date 2019-08-21
 */
@Getter
@Setter
@Entity
@Table(name = "implement_record")
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "implementBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST)
})
public class ImplementRecordBean implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer	implementRecordId;

    /**
     * 实施记录内容
     */
    String	implementRecordContent;

    /**
     * 实施记录人
     */
    String	implementRecordOperator;

    /**
     * 实施记录时间
     */
    Date implementRecordTime;

    /**
     * 跟实施是一对多 一个实施部 有多个实施记录
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "implement_record_impl_id",referencedColumnName = "implementId")
    private ImplementBean implementBean;



}
