$(function () {
    getPageDataUtils(
        {
            pageable: { //分页配置
                pageNum: 1, //当前页
                pageSize: 10, // 每页数量
                sortField: ["invoiceId"], //排序字段 可以多个
                sort: "false" // 排序正反
            },
            rollback : invoiceRollback ,
            fixed : {
                fixedTh : true ,  //首行固定 默认 true
                fixedTd : true ,  // 固定列 默认 false
                fixedTdLength : 1   // 固定列数 默认0
            } ,
            tableName: "invoice",
            elem: $("#myTable"),
            // rowspanLength: "projectBean.implementBeans",
            cols: [
                {name: "流水编号", field: "incomeBean.incomeNum",  search : true , multipleSelection: false},
                {name: "项目编号", field: "incomeBean.projectBean.projectNum",  search : true , multipleSelection: false},
                {name: "项目名称", field: "incomeBean.projectBean.projectName",  search : true , width : "250px" },
                {name: "申请时间", field: "invoiceApplyDate" , type: "date" , search :true },
                {name: "开票编号", field: "invoiceNumber",  search : true },
                {name: "开票种类", field: "invoiceType",  search : true , multipleSelection: true},
                {name: "甲方单位", field: "invoicePartyFirst",  search : true },
                {name: "开票内容", field: "invoiceContent",  search : true },
                {name: "开票金额", field: "invoiceMoney", count : true ,   search : true , type : "float" },
                {name: "开票通知人", field: "invoiceNoticeMans",  search : true , multipleSelection: true },
                {name: "收款单位", field: "secondPartyBean.secondPartyName",  search : true , multipleSelection: true },
                {name: "录入人", field: "createUserBean.userName",  search : true , multipleSelection: true },
                {name: "录入职位", field: "createJobBean.jobName",  search : true , multipleSelection: true },
                {name: "开票状态", field: "invoiceAuditStatus",  search : false , multipleSelection: false }
                // {name: "操作人", field: "createUserBean.userName",  search : true , multipleSelection: true},
                // {name: "操作时间", field: "managementCreateTime",  search : true , type: "date"}
            ],
            buttons: [
                {
                    buttonName: "被退回",  //按钮名称
                    buttonPath : "invoice/invoice-insert.html?id=${incomeBean.incomeId}&way=add" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "invoiceAuditStatus",  //条件key
                            value: 2,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "待审核",  //按钮名称
                    buttonPath : "invoice/invoice-audit.html?id=${incomeBean.incomeId}&way=audit" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "invoiceAuditStatus",  //条件key
                            value: 0,  //条件 value 必填 任何比较条件
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "补开票",  //按钮名称
                    buttonPath : "invoice/invoice-insert.html?id=${incomeBean.incomeId}&way=add" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "invoiceAuditStatus",  //条件key
                            value: 0,  //条件 value
                            comparisons: "isNull"
                        }  , //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                        {
                            key: "incomeBean.incomeAuditStatus",  //条件key
                            value: 1,  //条件 value
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "不需要",  //按钮名称
                    buttonType: "other" ,
                    buttonPath : "" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "invoiceAuditStatus",  //条件key
                            value: 3,  //条件 value
                            comparisons: "eq"
                        }   //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "详情",  //按钮名称
                    buttonPath : "invoice/invoice-info.html?id=${incomeBean.incomeId}" ,  //  删除不要写path
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
                            key: "invoiceAuditStatus",  //条件key
                            value: 4,  //条件 value
                            comparisons: "eq"
                        },   //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                        {
                            key: "incomeBean.incomeAuditStatus",  //条件key
                            value: 4,  //条件 value
                            comparisons: "eq"
                        }   //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                }
                ,
                {
                    buttonName: "作废",  //按钮名称
                    buttonType: "other" ,
                    buttonPath : "${incomeBean.incomeId}" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "invoiceAuditStatus",  //条件key
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
                            key: "invoiceId",  //条件key  如果是删除 把主键写在这儿
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


function invoiceRollback() {
    $("[data-name='invoiceAuditStatus-td']").each(function (index, td) {
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
        } else  if (  text==="3"  ) {
            span.text("不需要");
        } else {
            span.text("补开票");
        }
    });

}
