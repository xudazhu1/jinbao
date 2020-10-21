$(function () {
    getPageDataUtils(
        {
            pageable: { //分页配置
                pageNum: 1, //当前页
                pageSize: 10, // 每页数量
                sortField: ["borrowMoneyId"], //排序字段 可以多个
                sort: "false" // 排序正反
            },
            fixed: {
                fixedTh: true,  //首行固定 默认 true
                fixedTd: true,  // 固定列 默认 false
                fixedTdLength: 1,  // 固定列数 默认0
            },
            tableName: "borrow_money",
            elem: $("#myTable"),
            cols: [

                {name: "借款编号", field: "borrowMoneyNum", search: true},
                {name: "借款日期", field: "borrowMoneyDate", search: true, type: "date"},
                {name: "借款人", field: "personalBankCardBean.personalBankCardName", search: true},
                {name: "借款金额", field: "borrowMoneyMoney", search: true, type: "date"},
                {name: "借款用途", field: "borrowMoneyRemark"},
                {name: "付款银行卡", field: "bankCardBean.bankCardName"},
                {name: "付款日期", field: "borrowMoneyPayDate"},
                {name: "状态", field: "paymentStatusBean.paymentStatusName"},
            ],
            buttons: [
                {
                    buttonName: "待确认",  //按钮名称
                    buttonPath: "borrowMoney/borrowMoney-confirm.html?id=${borrowMoneyId}&way=confirm",  //  删除不要写path
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
                    buttonPath: "borrowMoney/borrowMoney-return-confirm.html?id=${borrowMoneyId}&way=return-confirm",  //  删除不要写path
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
                    buttonPath: "borrowMoney/borrowMoney-audit.html?id=${borrowMoneyId}&way=audit",  //  删除不要写path
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
                    buttonPath: "borrowMoney/borrowMoney-return-audit.html?id=${borrowMoneyId}&way=return-audit",  //  删除不要写path
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
                    buttonPath: "borrowMoney/borrowMoney-pay.html?id=${borrowMoneyId}&way=pay",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "paymentStatusBean.paymentStatusId",  //条件key
                            value: 5,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "已支付",  //按钮名称
                    buttonPath: "borrowMoney/borrowMoney-paid.html?id=${borrowMoneyId}&way=paid",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "paymentStatusBean.paymentStatusId",  //条件key
                            value: 7,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "打印",  //按钮名称`
                    buttonPath: "borrowMoney/borrowMoney-print.html?id=${borrowMoneyId}",  //  删除不要写path
                },
                {
                    buttonName: "已作废",  //按钮名称
                    buttonType: "other",
                    buttonPath: "",  //  删除不要写path
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
                    buttonType: "other",
                    buttonPath: "${borrowMoneyId}",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "paymentStatusBean.paymentStatusId",  //条件key
                            value: 9,  //条件 value
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
            $.post( "/borrow_money" ,  {"_method" : "PUT" , "transferAccountsId" : $(this).attr("data-href") ,
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