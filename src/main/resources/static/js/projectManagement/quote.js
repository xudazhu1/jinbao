$(function () {
    getPageDataUtils(
        {
            pageable: { //分页配置
                pageNum: 1, //当前页
                pageSize: 10, // 每页数量
                sortField: ["quoteId"], //排序字段 可以多个
                sort: "false" // 排序正反
            },
            fixed : {
                fixedTh : true ,  //首行固定 默认 true
                fixedTd : true ,  // 固定列 默认 false
                fixedTdLength : 2 ,  // 固定列数 默认0
            } ,
            tableName: "quote",
            elem: $("#myTable"),
            // rowspanLength: "projectBean.implementBeans",
            cols: [
                {name: "报价编号", field: "quoteNum" , search : true ,},
                {name: "报价项目名称", field: "quoteName"  ,search : true },
                {name: "报价单位", field: "quotePlace" ,search : true  ,multipleSelection: true},
                {name: "项目内容", field: "quoteProjectContent",   search : true  },
                {name: "报价金额", field: "quoteMoney", type:"float", search : true , multipleSelection: true},
                {name: "报价内容", field: "quoteContent",  search : true  },
                {name: "报价人", field: "quotePeople",  search : true , multipleSelection: true },
                {name: "报价状态", field: "quoteStatus",  search : true , multipleSelection: true},
            ],
            buttons: [
                {
                    buttonName: "编辑",  //按钮名称
                    buttonPath : "quote/quote-add.html?id=${quoteId}" ,  //  删除不要写path
                    // condition: [ // 按钮出现条件 可以是多个
                    // {
                    //     key: "managementId",  //条件key
                    //     value: 0,  //条件 value
                    //     comparisons: "gt"
                    // }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    // ]
                },
                {
                    isDelete: true,
                    buttonName: "删除",  //按钮名称
                    // buttonPath : "management/management-add.html?id=${managementId}" ,  //按钮路径 ${field} 支持查出值用于拼接路径
                    // condition: [ // 按钮出现条件 可以是多个
                    // {
                    //     key: "managementId",  //条件key  如果是删除 把主键写在这儿
                    //     // value : 0 ,  //条件 value 不要写value
                    //     comparisons: "gt"
                    // }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    // ]
                }
            ]
        }
    );


});

