$(function () {
    getPageDataUtils(
        {
            pageable: { //分页配置
                pageNum: 1, //当前页
                pageSize: 10, // 每页数量
                sortField: ["disburseId"], //排序字段 可以多个
                sort: "false" // 排序正反
            },
            fixed: {
                fixedTh: true,  //首行固定 默认 true
                fixedTd: true,  // 固定列 默认 false
                fixedTdLength: 1,  // 固定列数 默认0
            },
            tableName: "disburse",
            elem: $("#myTable"),
            rowspanLength: "bankCardAllotBeans",
            cols: [
                {name: "财务日常编号", field: "disburseNum", search: true},
                {name: "费用类型明细", field: "disburseDetailBean.disburseDetailName", search: true, multipleSelection: true},
                {name: "付款单位", field: "secondPartyBean.secondPartyName",search: true},
                {name: "付款金额", field: "disbursePaymentAmount", search: true},
                {name: "付款方式", field: "disburseMode", search: true, multipleSelection: true},
                {name: "支出时间", field: "disburseTime", search: true, type: "date"},
                {name: "收款单位", field: "disbursePayee", search: true},
                {name: "收款开户行", field: "disburseCollectionBank", search: true},
                {name: "收款银行账号", field: "financeShroffAccountNumber", search: true},
                {name: "是否已开票", field: "disburseHaveTicket", search: true, multipleSelection: true},
                {name: "经办人", field: "disburseResponsiblePerson", search: true, multipleSelection: true},
                {name: "申请部门", field: "departmentBean.departmentName", search: true, multipleSelection: true},
                {name: "收益单位", field: "earningsCompanyBean.earningsCompanyName", search: true, multipleSelection: true},
                {name: "备注", field: "disburseRemarks", search: true},
                {name: "进度状态", field: "paymentStatusBean.paymentStatusName", search: true, multipleSelection: true},
                {name: "支付银行卡", field: "bankCardAllotBeans[n].bankCardBean.bankCardName", search: true, multipleSelection: true},
                {name: "支付银支付金额", field: "bankCardAllotBeans[n].bankCardAllotBankCardMoney", search: true, multipleSelection: true},
                {name: "支付日期", field: "bankCardAllotBeans[n].bankCardAllotTime", search: true, multipleSelection: true},
                // {name: "操作人", field: "createUserBean.userName",  search : true , multipleSelection: true},
                // {name: "操作时间", field: "managementCreateTime",  search : true , type: "date"}
            ],
            buttons: [
                {
                    buttonName: "待确认",  //按钮名称
                    buttonPath: "financialDaily/financialDaily-confirm.html?id=${disburseId}&way=confirm",  //  删除不要写path
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
                    buttonPath: "financialDaily/financialDaily-return-confirm.html?id=${disburseId}&way=return-confirm",  //  删除不要写path
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
                    buttonPath: "financialDaily/financialDaily-audit.html?id=${disburseId}&way=audit",  //  删除不要写path
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
                    buttonPath: "financialDaily/financialDaily-return-audit.html?id=${disburseId}&way=return-audit",  //  删除不要写path
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
                    buttonPath: "financialDaily/financialDaily-pay.html?id=${disburseId}&way=pay",  //  删除不要写path
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
                    buttonPath: "financialDaily/financialDaily-add.html?id=${disburseId}&way=return-pay",  //  删除不要写path
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
                    buttonPath: "financialDaily/financialDaily-paid.html?id=${disburseId}&way=paid",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "paymentStatusBean.paymentStatusId",  //条件key
                            value: 7,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "详情",  //按钮名称
                    buttonPath: "financialDaily/financialDaily-detail.html?id=${disburseId}&way=details",  //  删除不要写path
                },
                // {
                //     isDelete: true,
                //     buttonName: "删除",  //按钮名称
                //     buttonPath: "financialDaily/financialDaily-add.html?id=${disburseId}",  //按钮路径 ${field} 支持查出值用于拼接路径
                //     condition: [ // 按钮出现条件 可以是多个
                //         {
                //             key: "disburseId",  //条件key  如果是删除 把主键写在这儿
                //             // value : 0 ,  //条件 value 不要写value
                //             comparisons: "gt"
                //         }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                //     ]
                // }
                {
                    buttonName: "打印",  //按钮名称
                    buttonPath: "financialDaily/financialDaily-print.html?id=${disburseId}&way=print",
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
            $.post( "/finance" ,  {"_method" : "PUT" , "disburseId" : $(this).attr("data-href") ,
                "paymentStatusBean.paymentStatusId" : 9 } , function (data) {
                if (data) {
                    alert("作废成功");
                    tableConfig.functions.flush();
                } else {
                    alert("操作失败");
                }
            }  , "json" );
        }
    });
});
