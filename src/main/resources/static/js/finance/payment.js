$(function () {
    getPageDataUtils(
        {
            pageable: { //分页配置
                pageNum: 1, //当前页
                pageSize: 10, // 每页数量
                sortField: ["disburseId"], //排序字段 可以多个
                sort: "false" // 排序正反
            },
            fixed : {
                fixedTh : true ,  //首行固定 默认 true
                fixedTd : true ,  // 固定列 默认 false
                fixedTdLength : 1 ,  // 固定列数 默认0
            } ,
            tableName: "disburse",
            rollback: hideDepartment,
            elem: $("#myTable"),
            rowspanLength: "bankCardAllotBeans",
            cols: [
                {name: "付款申请编号", field: "disburseNum",  search : true},
                {name: "项目编号", field: "implementBean.projectBean.projectNum",  search : true  },
                {name: "项目名称", field: "implementBean.projectBean.projectName",  search : true},
                {name: "内容", field: "disburseExpenseAccountContent"},
                {name: "费用类型明细", field: "disburseDetailBean.disburseDetailName",  search : true  , multipleSelection: true},
                {name: "所属费用类型(中类)", field: "disburseDetailBean.disburseTypeBean.disburseTypeName",  search : true  , multipleSelection: true},
                {name: "付款单位", field: "secondPartyBean.secondPartyName", search : true  , multipleSelection: true},
                {name: "付款金额", field: "disbursePaymentAmount",  search : true},
                {name: "付款方式", field: "disburseMode",  search : true , multipleSelection: true},
                {name: "支出时间", field: "disburseTime",  search : true,type: "date"},
                {name: "收款单位", field: "disbursePayee",  search : true},
                {name: "收款开户行", field: "disburseCollectionBank",  search : true},
                {name: "收款银行账号", field: "financeShroffAccountNumber",  search : true},
                {name: "是否已开票", field: "disburseHaveTicket",  search : true , multipleSelection: true },
                {name: "经办人", field: "disburseResponsiblePerson",  search : true , multipleSelection: true},
                {name: "申请部门", field: "implementBean.departmentBean.departmentName"},
                {name: "申请部门", field: "departmentBean.departmentName"},

                {name: "收益单位", field: "earningsCompanyBean.earningsCompanyName",search : true ,multipleSelection: true},
                {name: "备注", field: "disburseRemarks", search : true},
                {name: "进度状态", field: "paymentStatusBean.paymentStatusName", search: true, multipleSelection: true},
                {name: "支付银行卡", field: "bankCardAllotBeans[n].bankCardBean.bankCardName", search: true, multipleSelection: true},
                {name: "支付银支付金额", field: "bankCardAllotBeans[n].bankCardAllotBankCardMoney", search: true, multipleSelection: true},
                {name: "支付日期", field: "bankCardAllotBeans[n].bankCardAllotTime", search: true, multipleSelection: true},
                // {name: "审计金额", field: "managementAuditAmount", type: "float",  search : true , fixed: 2},
                // {name: "操作人", field: "createUserBean.userName",  search : true , multipleSelection: true},
                // {name: "操作时间", field: "managementCreateTime",  search : true , type: "date"}
            ],
            buttons: [
                {
                    buttonName: "待确认",  //按钮名称
                    buttonPath: "payment/payment-confirm.html?id=${disburseId}&way=confirm&category=${disburseDetailBean.disburseTypeBean.disburseCategoryBean.disburseCategoryName}",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "paymentStatusBean.paymentStatusId",  //条件key
                            value: 1,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "退回/待确认",  //按钮名称
                    buttonPath: "payment/payment-return-confirm.html?id=${disburseId}&way=return-confirm&category=${disburseDetailBean.disburseTypeBean.disburseCategoryBean.disburseCategoryName}",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "paymentStatusBean.paymentStatusId",  //条件key
                            value: 2,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "待审核",  //按钮名称
                    buttonPath: "payment/payment-audit.html?id=${disburseId}&way=audit&category=${disburseDetailBean.disburseTypeBean.disburseCategoryBean.disburseCategoryName}",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "paymentStatusBean.paymentStatusId",  //条件key
                            value: 3,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "退回/待审核",  //按钮名称
                    buttonPath: "payment/payment-return-audit.html?id=${disburseId}&way=return-audit&category=${disburseDetailBean.disburseTypeBean.disburseCategoryBean.disburseCategoryName}",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "paymentStatusBean.paymentStatusId",  //条件key
                            value: 4,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "待支付",  //按钮名称
                    buttonPath: "payment/payment-pay.html?id=${disburseId}&way=pay&category=${disburseDetailBean.disburseTypeBean.disburseCategoryBean.disburseCategoryName}",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "paymentStatusBean.paymentStatusId",  //条件key
                            value: 5,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "退回/待支付",  //按钮名称
                    buttonPath: "payment/payment-add.html?id=${disburseId}&way=return-pay&category=${disburseDetailBean.disburseTypeBean.disburseCategoryBean.disburseCategoryName}",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "paymentStatusBean.paymentStatusId",  //条件key
                            value: 6,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "已支付",  //按钮名称
                    buttonPath: "payment/payment-paid.html?id=${disburseId}&way=paid&category=${disburseDetailBean.disburseTypeBean.disburseCategoryBean.disburseCategoryName}",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "paymentStatusBean.paymentStatusId",  //条件key
                            value: 7,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "退回/已支付",  //按钮名称
                    buttonPath: "payment/payment-return-paid.html?id=${disburseId}&way=paid&category=${disburseDetailBean.disburseTypeBean.disburseCategoryBean.disburseCategoryName}",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "paymentStatusBean.paymentStatusId",  //条件key
                            value: 10,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "审核通过",  //按钮名称
                    buttonPath: "payment/payment-complete.html?id=${disburseId}&way=paid&category=${disburseDetailBean.disburseTypeBean.disburseCategoryBean.disburseCategoryName}",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "paymentStatusBean.paymentStatusId",  //条件key
                            value: 8,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "详情",  //按钮名称`
                    buttonPath : "payment/payment-detail.html?id=${disburseId}&category=${disburseDetailBean.disburseTypeBean.disburseCategoryBean.disburseCategoryName}" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        // {
                        //     key: "managementId",  //条件key
                        //     value: 0,  //条件 value
                        //     comparisons: "gt"
                        // }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "打印",  //按钮名称`
                    buttonPath : "payment/payment-print.html?id=${disburseId}&type=${disburseDetailBean.disburseTypeBean.disburseTypeName}" +
                        "&broad=${disburseDetailBean.disburseTypeBean.disburseCategoryBean.disburseCategoryName}" +
                        "&subclass=${disburseDetailBean.disburseDetailName}" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        // {
                        //     key: "managementId",  //条件key
                        //     value: 0,  //条件 value
                        //     comparisons: "gt"
                        // }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "已作废",  //按钮名称
                    buttonType: "other" ,
                    buttonPath : "" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "paymentStatusBean.paymentStatusId",  //条件key
                            value: 9,  //条件 value
                            comparisons: "eq"
                        },   //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                }
                ,
                {
                    buttonName: "作废",  //按钮名称
                    buttonType: "other" ,
                    buttonPath : "${disburseId}" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "paymentStatusBean.paymentStatusId",  //条件key
                            value:9,  //条件 value
                            comparisons: "lt"
                        }   //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    isDelete: true,
                    buttonName: "删除",  //按钮名称
                    // buttonPath : "management/management-add.html?id=${managementId}" ,  //按钮路径 ${field} 支持查出值用于拼接路径
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "disburseId",  //条件key  如果是删除 把主键写在这儿
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
            $.post( "/payment" ,  {"_method" : "PUT" , "disburseId" : $(this).attr("data-href") ,
                "paymentStatusBean.paymentStatusId" : 9 } , function (data) {
                if (data["paymentStatusBean"]["paymentStatusId"] === 9 ) {
                    alert("作废成功");
                    tableConfig.functions.flush();
                } else {
                    alert("操作失败");
                }
            }  , "json" );
        }
    });
});

function hideDepartment() {
    //当 页面 是 其他付款的时候 只展示 其中一个 部门td 如果是 项目付款 展示 实施部门td
    $("[data-name='departmentBean.departmentName-th']").remove();

    $(".no-copy-show-altrowstable tbody").find("tr").each(function (index, tr) {
        let departmentTr = false;
        let implementTr = false;
        $(tr).find("[data-name='implementBean.departmentBean.departmentName-td']").each(function () {
            if ($(this).children(".table-utils-td-text").text() === "") {
                implementTr = true;
            }
        });

        $(tr).find("[data-name='departmentBean.departmentName-td']").each(function () {
            if ($(this).children(".table-utils-td-text").text() === "") {
                departmentTr = true;
            }
        });
        if (departmentTr && implementTr){
            $(tr).find("[data-name='departmentBean.departmentName-td']").remove()
        }else if (departmentTr && !implementTr){
            $(tr).find("[data-name='departmentBean.departmentName-td']").remove()
        }else if (!departmentTr && implementTr){
            $(tr).find("[data-name='implementBean.departmentBean.departmentName-td']").remove()
        }else if (!departmentTr && !implementTr){
            $(tr).find("[data-name='implementBean.departmentBean.departmentName-td']").remove()
        }
    });
}

