package com.hnjbkc.jinbao.disburse.salary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 日结实施人员工资表
 * @author 12
 * @Date 2019-10-03
 */
@Getter
@Setter
@Entity
@Table(name = "salary")
public class SalaryBean  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer	salaryId;

    /**
     * 人员每月日期
     */
    @DateTimeFormat(pattern = "yyyy-MM")
    private Date salaryDate;

    /**
     * 每月工资
     */
    Double	salaryDailyCost;

    /**
     * user
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_user_id", referencedColumnName = "userId")
    @JsonIgnoreProperties(value = {"roleBeans","jobBean","staffBean","supervisorBean","captainBean"})
    private UserBean userBean;

    /**
     * 部门备注
     */
    String salaryRemark;

}
