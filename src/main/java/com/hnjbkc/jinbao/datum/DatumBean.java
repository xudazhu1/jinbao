package com.hnjbkc.jinbao.datum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.datum.datumfile.DatumFileBean;
import com.hnjbkc.jinbao.hqldao.annotations.HasOneToManyList;
import com.hnjbkc.jinbao.hqldao.annotations.OneToManyListInfo;
import com.hnjbkc.jinbao.implement.ImplementBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 合同bean
 * @author siliqiang
 * @date 2019.8.15
 */
@Entity
@Table(name = "datum")
@Getter
@Setter
@HasOneToManyList(hasClasses = {
        @OneToManyListInfo( propertyClass = DatumFileBean.class , propertyName = "datumFileBeans" )
})
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "implementBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST ,fieldList = {"projectBean" , "departmentBean"}),
        @MyGraphIgnoreInfo( fieldPath = "implementBean.projectBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST ,fieldList = {"contractBean" }),
        @MyGraphIgnoreInfo( fieldPath = "implementBean.projectBean.contractBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST),
        @MyGraphIgnoreInfo( fieldPath = "implementBean.departmentBean " , fetchType = MyGraphIgnoreInfoType.WHITE_LIST)
})

public class DatumBean  implements Serializable {
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   Integer datumId;
    /**
     * 资料部门
     */
    String datumDepartment;
    /**
     * 甲方提供资料
     */
    String datumParafile;
    /**
     * 概算
     */
    String datumRough;
    /**
     * 预算
     */
    String datumBudget;
    /**
     * 结算
     */
    String datumClose;
    /**
     *合同
     */
    String datumContfile;
    /**
     * 财评
     */
    String datumEvalua;
    /**
     * 审计
     */
    String datumAudit;
    /**
     * 资料成果
     */
    String datumResults;
    /**
     * 中标通知书
     */
    String datumAdvicenote;
    /**
     * 备注
     */
    String datumRemaks;
    /**
     * 勘察方案
     */
    String datumSursche;
    /**
     * 监测方案
     */
    String datumSupervise;
    /**
     * 报告文本
     */
    String datumReporttext;
    /**
     * 	批复文件
     */
    String datumWrittenfile;
    /**
     *测量方案
     */
    String datumMeasure;
    /**
     * 是否存在
     */
    Boolean datumExist=true;

    /**
     * 外键关联项目主键
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "datum_implement_id",referencedColumnName = "implementId")
    private ImplementBean implementBean;

    /**
     * 多扫描件
     */
    @OneToMany(fetch = FetchType.LAZY , mappedBy = "datumBean" ,cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"datumBean"})
    private List<DatumFileBean> datumFileBeans = new ArrayList<>();

    @Override
    public String toString() {
        return "DatumBean{" +
                "datumId='" + datumId + '\'' +
                ", datumDepartment='" + datumDepartment + '\'' +
                ", datumParafile='" + datumParafile + '\'' +
                ", datumRough='" + datumRough + '\'' +
                ", datumBudget='" + datumBudget + '\'' +
                ", datumClose='" + datumClose + '\'' +
                ", datumContfile='" + datumContfile + '\'' +
                ", datumEvalua='" + datumEvalua + '\'' +
                ", datumAudit='" + datumAudit + '\'' +
                ", datumResults='" + datumResults + '\'' +
                ", datumAdvicenote='" + datumAdvicenote + '\'' +
                ", datumRemaks='" + datumRemaks + '\'' +
                ", datumSursche='" + datumSursche + '\'' +
                ", datumSupervise='" + datumSupervise + '\'' +
                ", datumReporttext='" + datumReporttext + '\'' +
                ", datumWrittenfile='" + datumWrittenfile + '\'' +
                ", datumMeasure='" + datumMeasure + '\'' +
                ", datumExist=" + datumExist +
                '}';
    }
}
