$(function () {
    getPageDataUtils(
        {
            pageable: { //分页配置
                pageNum: 1, //当前页
                pageSize: 10, // 每页数量
                sortField: ["incomeId"], //排序字段 可以多个
                sort: "false" // 排序正反
            },
            fixed: {
                fixedTh: true,  //首行固定 默认 true
                fixedTd: true,  // 固定列 默认 false
                fixedTdLength: 1,  // 固定列数 默认0
            },
            tableName: "income",
            //rollback: hideDepartment,
            elem: $("#myTable"),
            rowspanLength: "moneyBackBeans[n]",
            cols: [
                {name: "收入编号", field: "incomeNum", search: true},
                {name: "收入时间", field: "incomeDate", type: "date", search: true},
                {name: "收入类型", field: "incomeType", search: true},
                {name: "项目编号", field: "projectBean.projectNum", search: true},
                {name: "项目名称", field: "projectBean.projectName", search: true},
                {name: "内容", field: "invoiceBean.invoiceContent", search: true},
                {name: "回款金额", field: "incomeMoney", search: true, multipleSelection: false , count : true},
                {name: "实际回款", field: "moneyBackBeans[n].moneyBackMoney",search: true , count : true},
                {name: "实际回款时间", field: "moneyBackBeans[n].moneyBackDate", type: "date", search: true},
                {name: "回款方式", field: "moneyBackBeans[n].moneyBackType", search: true},
                {name: "实际收款单位", field: "moneyBackBeans[n].secondPartyBean.secondPartyName", search: true},
                {name: "收益单位", field: "earningsCompanyBean.earningsCompanyName", search: true},
                {name: "收入银行卡", field: "moneyBackBeans[n].bankCardBean.bankCardName", search: true},

            ],
        }
    );
})