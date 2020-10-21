let  date;
$(function () {
    url="/expense_account/get_time"
    var data="";
    $.ajax({
        "url": url,
        "data": data,
        "type": "GET",
        "dataType": "json",
        "success": function (json) {
            date=json
        }
    })
    getPageDataUtils(
        {
            pageable: { //分页配置
                pageNum: 1, //当前页
                pageSize: 20, // 每页数量
                sortField: ["expenseAccountId"], //排序字段 可以多个
                sort: "false" // 排序正反
            },
            fixed: {
                fixedTh: true,  //首行固定 默认 true
                fixedTd: true,  // 固定列 默认 false
                fixedTdLength: 1,  // 固定列数 默认0
            },
            tableName: "expense_account",
            rollback: hideDepartment,
            elem: $("#myTable"),
            rowspanLength: "disburseBeans[n]",
            cols: [
                {name: "报销总编号", field: "expenseAccountNum", search: true},
                {name: "报销月份", field: "expenseAccountMonth", search: true , type : "date" , multipleSelection : true },
                {name: "报销时间", field: "expenseAccountTime", search: true , type : "date" },
                // {name: "报销子编号", field: "disburseBeans[n].disburseNum", search: true},
                // {name: "支出时间", field: "disburseBeans[n].disburseTime", search: true, type: "date" },
                // {name: "项目编号", field: "disburseBeans[n].implementBean.projectBean.projectNum", search: true},
                // {
                //     name: "项目名称",
                //     field: "disburseBeans[n].implementBean.projectBean.projectName",
                //     search: true,
                //     width: '200px'
                // },
                // {name: "内容", field: "disburseBeans[n].disburseExpenseAccountContent", search: true},
                // {
                //     name: "费用明细",
                //     field: "disburseBeans[n].disburseDetailBean.disburseDetailName",
                //     search: true,
                //     multipleSelection: true
                // },
                // {name: "费用归属部门", field: "disburseBeans[n].departmentBean.departmentName"},
                // {name: "费用归属公司", field: "disburseBeans[n].implementBean.departmentBean.departmentName", search: false},
                // // {
                // //     name: "项目费用归属部门",
                // //     field: "disburseBeans[n].implementBean.departmentBean.departmentName",
                // //     search: false
                // // },
                // {
                //     name: "收益单位",
                //     field: "disburseBeans[n].earningsCompanyBean.earningsCompanyName",
                //     search: true,
                //     multipleSelection: true
                // },
                {name: "报销金额", field: "disburseBeans[n].disbursePaymentAmount", search: true},
                {name: "发票金额", field: "disburseBeans[n].disburseInvoiceMoney", search: true},
                {name: "发票总金额", field: "expenseAccountId", search: false},
                {name: "报销总金额", field: "expenseAccountRemark", search: false},
                // {name: "备注", field: "disburseBeans[n].disburseRemarks", search: true, width: '200px'},
                {name: "报销人", field: "expenseAccountUserName", search: true},
                {name: "进度状态", field: "approvalStatusBean.approvalStatusName", search: true, multipleSelection: true},
                // {name: "操作人", field: "createUserBean.userName",  search : true , multipleSelection: true},
                // {name: "操作时间", field: "managementCreateTime",  search : true , type: "date"}
            ],
            buttons: [
                // {
                //     buttonName: "被退回",  //按钮名称
                //     buttonPath: "disburseExpense/disburseExpense-return.html?id=${expenseAccountId}&way=add",  //  删除不要写path
                //     condition: [ // 按钮出现条件 可以是多个
                //         {
                //             key: "approvalStatusBean.approvalStatusId",  //条件key
                //             value: 3,  //条件 value 必填 任何比较条件
                //             comparisons: "eq"
                //         }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                //     ]
                // },
                {
                    buttonName: "待财务审核",  //按钮名称
                    buttonPath: "disburseExpense/disburseExpense-audit.html?id=${expenseAccountId}&way=audit",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "approvalStatusBean.approvalStatusId",  //条件key
                            value: 1,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },{
                    buttonName: "待主管审核",  //按钮名称
                    buttonPath: "disburseExpense/disburseExpense-supervisor-audit.html?id=${expenseAccountId}&way=audit",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "approvalStatusBean.approvalStatusId",  //条件key
                            value: 4,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                }
                ,
                {
                    buttonName: "待总经办审核",  //按钮名称
                    buttonPath: "disburseExpense/disburseExpense-general-manager-office-audit.html?id=${expenseAccountId}&way=audit",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "approvalStatusBean.approvalStatusId",  //条件key
                            value: 5,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                }
                ,
                {
                    buttonName: "待终审",  //按钮名称
                    buttonPath: "disburseExpense/disburseExpense-audit.html?id=${expenseAccountId}&way=audit",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "approvalStatusBean.approvalStatusId",  //条件key
                            value: 9,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                }
                ,
                {
                    buttonName: "详情",  //按钮名称
                    buttonPath: "disburseExpense/disburseExpense-detail.html?id=${expenseAccountId}&way=detail",
                },
                // {
                //     buttonName: "打印",  //按钮名称
                //     buttonPath: "disburseExpense/disburseExpense-print.html?id=${expenseAccountId}&way=print",
                // },
                {
                    buttonName: "已作废",  //按钮名称
                    buttonType: "other" ,
                    buttonPath : "" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "approvalStatusBean.approvalStatusId",  //条件key
                            value: 8,  //条件 value
                            comparisons: "eq"
                        },   //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                }
                ,
                {
                    buttonName: "作废",  //按钮名称
                    buttonType: "other" ,
                    buttonPath : "${expenseAccountId}" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "approvalStatusBean.approvalStatusId",  //条件key
                            value:8,  //条件 value
                            comparisons: "lt"
                        }   //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空) | not ( 不等于 )
                    ]
                },
                {
                    isDelete: true,
                    buttonName: "删除",  //按钮名称
                    // buttonPath : "management/management-add.html?id=${managementId}" ,  //按钮路径 ${field} 支持查出值用于拼接路径
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "expenseAccountId",  //条件key  如果是删除 把主键写在这儿
                            // value : 0 ,  //条件 value 不要写value
                            comparisons: "gt"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                }
            ]
        }
    );

    $(document).on( "click" , "button[title='作废']" , function () {
        if ( confirm( "确定要作废吗?" )  ) {
            // alert($(this).attr("data-href"));
            $.post( "/expense_account" ,  {"_method" : "PUT" , "expenseAccountId" : $(this).attr("data-href") ,
                "approvalStatusBean.approvalStatusId" : 8 } , function (data) {
                if (data["approvalStatusBean"]["approvalStatusId"] === 8 ) {
                    alert("作废成功");
                    tableConfig.functions.flush();
                } else {
                    alert("操作失败");
                }
            }  , "json" );
        }
    });

});

function hideDepartment(tableDom) {
    $("[data-name='disburseBeans.companyBean.companyName-th'] , [data-name='disburseBeans.implementBean.departmentBean.departmentName-th']").remove();
    tableDom.find("tbody").find("tr").each(function (index, tr) {
        $(tr).find("[data-name='disburseBeans.companyBean.companyName-td'],[data-name='disburseBeans.departmentBean.departmentName-td'],[data-name='disburseBeans.implementBean.departmentBean.departmentName-td'] ").each(function () {
            // noinspection JSValidateTypes
            if ($(this).children(".table-utils-td-text").text() === "") {
                $(this).remove();
            }
        });
    });


    tableDom.find("[data-name='expenseAccountId-td']").each(function () {
        let rowSpan = parseFloat($(this).attr("rowspan"));
        let trTemp = $(this).closest("tr");
        let countDisburseInvoiceMoney = 0.00;
        let countdisbursePaymentAmount = 0.00;
        for (let i = 0; i < rowSpan; i++) {
            let value = trTemp.find("[data-name='disburseBeans.disburseInvoiceMoney-td']").find(".table-utils-td-text").text();
            let disbursePaymentAmount = trTemp.find("[data-name='disburseBeans.disbursePaymentAmount-td']").find(".table-utils-td-text").text();
            countDisburseInvoiceMoney += parseFloat(value);
            countdisbursePaymentAmount += parseFloat(disbursePaymentAmount);
            trTemp = trTemp.next("tr");
        }
        $(this).find(".table-utils-td-text").text(countDisburseInvoiceMoney.toFixed(2));
        $(this).next("td").find(".table-utils-td-text").text(countdisbursePaymentAmount.toFixed(2));
    });


    // $(".glyphicon").click(function () {
    //     if (date > 5) {
    //         alert("请在每月5号之前提交报销")
    //         return false
    //     }
    //     return true
    // })


}

