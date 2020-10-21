$(function () {
    getPageDataUtils(
        {
            pageable: { //分页配置
                pageNum: 1, //当前页
                pageSize: 10, // 每页数量
                sortField: ["incomeId"], //排序字段 可以多个
                sort: "false" // 排序正反
            },
            fixed : {
                fixedTh : true ,  //首行固定 默认 true
                fixedTd : true ,  // 固定列 默认 false
                fixedTdLength : 1   // 固定列数 默认0
            } ,
            rollback : incomeRollback,
            tableName: "income",
            elem: $("#myTable"),
            rowspanLength: "moneyBackBeans[n]",
            cols: [
                {name: "回款编号", field: "incomeNum",  search : true , multipleSelection: false},
                {name: "项目编号", field: "projectBean.projectNum",  search : true , multipleSelection: false},
                {name: "项目名称", field: "projectBean.projectName", width : "200px" ,   search : true },
                {name: "回款类型", field: "incomeType",  search : true , multipleSelection: true },
                {name: "开票时间", field: "invoiceBean.invoiceDate" , type: "date" , search :true },
                {name: "开票编号", field: "invoiceBean.invoiceNumber",  search : true },
                {name: "开票种类", field: "invoiceBean.invoiceType",  search : true , multipleSelection: true},
                {name: "甲方单位", field: "invoiceBean.invoicePartyFirst",  width : "200px" ,  search : true },
                {name: "开票内容", field: "invoiceBean.invoiceContent",  search : true },
                {name: "开票金额", field: "invoiceBean.invoiceMoney",  count: true ,  search : true , type : "float" },
                {name: "地址", field: "invoiceBean.invoiceSite",  width : "200px" ,   search : true  },
                {name: "开票通知人", field: "invoiceBean.invoiceNoticeMans",  search : true , multipleSelection: true },
                {name: "回款金额", field: "incomeMoney", type: "float" , count: true , search : true , multipleSelection: false},
                {name: "回款时间", field: "incomeDate", type: "date", search : true , multipleSelection: false},
                {name: "总实际回款", field: "incomeCountMoneyBackMoney",  count: true , type: "float" , search : true , multipleSelection: false},
                {name: "收款单位", field: "secondPartyBean.secondPartyName",  search : true , multipleSelection: true },
                {name: "实际回款", field: "moneyBackBeans[n].moneyBackMoney",  count: true , type : "float" ,  search : true },
                {name: "实际回款时间", field: "moneyBackBeans[n].moneyBackDate", type: "date" ,  search : true },
                {name: "实际回款方式", field: "moneyBackBeans[n].moneyBackType",  search : true , multipleSelection: true},
                {name: "实际收款单位", field: "moneyBackBeans[n].secondPartyBean.secondPartyName",  search : true , multipleSelection: true} ,
                {name: "回款银行卡", field: "moneyBackBeans[n].bankCardBean.bankCardName",  search : true , multipleSelection: true} ,
                {name: "状态", field: "incomeAuditStatus",  search : false , multipleSelection: false}
                // {name: "操作时间", field: "managementCreateTime",  search : true , type: "date"}
            ],
            buttons: [
                {
                    buttonName: "加回款",  //按钮名称  ${userName}
                    buttonPath : "income/income-add-money-back.html?id=${incomeId}&way=add" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "incomeAuditStatus",  //条件key
                            value: 1,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "被退回",  //按钮名称
                    buttonPath : "income/income-insert.html?id=${incomeId}&way=add" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "incomeAuditStatus",  //条件key
                            value: 2,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "待审核",  //按钮名称
                    buttonPath : "income/income-audit.html?id=${incomeId}&way=audit" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "incomeAuditStatus",  //条件key
                            value: 0,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "待回款",  //按钮名称
                    buttonPath : "income/income-insert.html?id=${incomeId}&way=add" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "invoiceBean.invoiceAuditStatus",  //条件key
                            value: 1,  //条件 value
                            comparisons: "eq"
                        }  , //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                        {
                            key: "incomeAuditStatus",  //条件key
                            value: 0,  //条件 value
                            comparisons: "isNull"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)

                    ]
                },
                {
                    buttonName: "详情",  //按钮名称
                    buttonPath : "income/income-add.html?id=${incomeId}" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        // {
                        //     key: "managementId",  //条件key
                        //     value: 0,  //条件 value
                        //     comparisons: "gt"
                        // }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                }
                ,
                {
                    buttonName: "已作废",  //按钮名称
                    buttonType: "other" ,
                    buttonPath : "" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "invoiceBean.invoiceAuditStatus",  //条件key
                            value: 4,  //条件 value
                            comparisons: "eq"
                        },   //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                        {
                            key: "incomeAuditStatus",  //条件key
                            value: 4,  //条件 value
                            comparisons: "eq"
                        }   //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                }
                ,
                {
                    buttonName: "作废",  //按钮名称
                    buttonType: "other" ,
                    buttonPath : "${incomeId}" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "incomeAuditStatus",  //条件key
                            value:4,  //条件 value
                            comparisons: "lt"
                        }   //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                }
                ,
                {
                    isDelete: true,
                    buttonName: "删除",  //按钮名称
                    // buttonPath : "management/management-add.html?id=${managementId}" ,  //按钮路径 ${field} 支持查出值用于拼接路径
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "incomeId",  //条件key  如果是删除 把主键写在这儿
                            // value : 0 ,  //条件 value 不要写value
                            comparisons: "gt"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                }
            ]
        }
    );


    $(document).on( "change" , ".incomeAuditStatus-select" , function () {
        let checkedDom = $(".incomeAuditStatus-select:checked:not( #incomeAuditStatus-select-all )");
        if ( checkedDom.length < 4 ) {
            $("#incomeAuditStatus-select-all").prop("checked" , false );
        } else {
            $("#incomeAuditStatus-select-all").prop("checked" , true );
        }
        let hiddenInput = $("#top-audit-checkbox");
        let where = [];
        $(".incomeAuditStatus-select:checked").each( function ( index , dom) {
            where.push( dom.value ) ;
        });
        if ( where.length ===  0  ) {
            where.push( "1 = 1" );
        }
        hiddenInput.val( where.toString().replace(/,/g, " or ") );
        $(".item-num-search-xudaz .btn-confirm").click();
    } );

    $("#incomeAuditStatus-select-all").click(function () {
        $(".incomeAuditStatus-select:not( #incomeAuditStatus-select-all )").prop("checked" , $(this).prop("checked") );
    });


    $(document).on( "click" , "button[title='作废']" , function () {
        if ( confirm( "确定要作废吗?" )  ) {
            // alert($(this).attr("data-href"));
            $.post( "/income" ,  {"_method" : "PUT" , "incomeId" : $(this).attr("data-href") ,
                "incomeAuditStatus" : 4 , "invoiceBean.invoiceAuditStatus" : 4 } , function (data) {
                if (data.incomeAuditStatus === 4 || data.incomeAuditStatus === "4" ) {
                    alert("作废成功");
                    tableConfig.functions.flush();
                } else {
                    alert("操作失败");
                }
            }  , "json" );
        }

    });

});


function incomeRollback() {
    $("[data-name='incomeAuditStatus-td']").each(function (index, td) {
        var span = $(td).find(".table-utils-td-text");
        var text = span.text();
        if (  text==="1"  ) {
            span.text("已通过");
        } else if (  text==="0"  ) {
            span.text("待审核");
        } else if (  text==="2"  ) {
            span.text("被退回");
        } else  if (  text==="4"  ) {
            span.text("已作废");
        } else {
            span.text("待回款");
        }
    });
    
}
