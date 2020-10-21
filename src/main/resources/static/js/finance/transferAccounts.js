$(function () {
    getPageDataUtils(
        {
            pageable: { //分页配置
                pageNum: 1, //当前页
                pageSize: 10, // 每页数量
                sortField: ["transferAccountsId"], //排序字段 可以多个
                sort: "false" // 排序正反
            },
            fixed: {
                fixedTh: true,  //首行固定 默认 true
                fixedTd: true,  // 固定列 默认 false
                fixedTdLength: 1,  // 固定列数 默认0
            },
            tableName: "transfer_accounts",
            elem: $("#myTable"),
            cols: [
                {name: "转账编号", field: "transferAccountsNum"},
                {name: "日期", field: "transferAccountsDate"},
                {name: "金额", field: "transferAccountsMoney", search: true},
                {name: "转出账户", field: "comeBankCardBean.bankCardName", search: true, multipleSelection: true},
                {name: "转入账户", field: "enterBankCardBean.bankCardName", search: true, multipleSelection: true},
                {name: "经办人", field: "transferAccountsResponsiblePerson", search: true, multipleSelection: true},
                {name: "备注", field: "transferAccountsRemark"},
                {name: "付款日期", field: "transferAccountsPayDate"},
                {name: "审核状态", field: "paymentStatusBean.paymentStatusName", search: true, multipleSelection: true},
            ],
            buttons: [
                {
                    buttonName: "待审核",  //按钮名称
                    buttonPath: "transferAccounts/transferAccounts-audit.html?id=${transferAccountsId}&way=audit",  //  删除不要写path
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
                    buttonPath: "transferAccounts/transferAccounts-return-audit.html?id=${transferAccountsId}&way=return-audit",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "paymentStatusBean.paymentStatusId",  //条件key
                            value: 4,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                }, {
                    buttonName: "打印",  //按钮名称`
                    buttonPath : "transferAccounts/transferAccounts-print.html?id=${transferAccountsId}" ,  //  删除不要写path
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
                    buttonPath : "${transferAccountsId}" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "paymentStatusBean.paymentStatusId",  //条件key
                            value:9,  //条件 value
                            comparisons: "lt"
                        }   //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                }

            ]
        }

    );
    $(document).on( "click" , "button[title='作废']" , function () {
        if ( confirm( "确定要作废吗?" )  ) {
            // alert($(this).attr("data-href"));
            $.post( "/transfer_accounts" ,  {"_method" : "PUT" , "transferAccountsId" : $(this).attr("data-href") ,
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
