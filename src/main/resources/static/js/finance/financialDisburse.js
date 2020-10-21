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
            rollback: hideDepartment,
            elem: $("#myTable"),
             rowspanLength : "bankCardAllotBeans" ,
            cols: [
                {name: "支出编号", field: "disburseNum", search: true},
                {name: "日期", field: "disburseTime", type: "date", search: true},
                {name: "费用明细", field: "disburseDetailBean.disburseDetailName", search: true},
                {name: "付款单位", field: "secondPartyBean.secondPartyName", search: true,multipleSelection: true},
                {
                    name: "支出类别",
                    field: "disburseDetailBean.disburseTypeBean.disburseCategoryBean.disburseCategoryName",
                    search: true
                },
                {
                    name: "支出类型",
                    field: "disburseDetailBean.disburseTypeBean.disburseTypeName",
                    search: true,multipleSelection: true
                },
                {name: "项目编号", field: "implementBean.projectBean.projectNum", search: true},
                {name: "项目名称", field: "implementBean.projectBean.projectName", search: true},
                {name: "报销内容", field: "disburseExpenseAccountContent"},
                {name: "支出费用", field: "disbursePaymentAmount", search: true, count : true},
                {name: "付款方式", field: "disburseMode", search: true,multipleSelection: true},
                {name: "费用归属单位", field: "departmentBean.departmentName"},
                {name: "费用归属单位", field: "implementBean.departmentBean.departmentName",},
                {name: "付款方式", field: "disburseMode", search: true},
                {name: "支付银行卡", field: "bankCardAllotBeans[n].bankCardBean.bankCardName", search: true, multipleSelection: true},
                {name: "支付银支付金额", field: "bankCardAllotBeans[n].bankCardAllotBankCardMoney", search: true, multipleSelection: true , count : true},
                // {name: "费用归属单位", field: "departmentBean.departmentName", search: true},
                // {name: "费用归属单位", field: "implementBean.departmentBean.departmentName", search: true},
                {name: "费用来源", field: "disburseAffiliation", search: true, multipleSelection: true},
                {name: "收益单位", field: "earningsCompanyBean.earningsCompanyName", search: true,multipleSelection: true},
                {name: "备注", field: "disburseRemarks", search: false},
                {name: "状态1", field: "paymentStatusBean.paymentStatusId", search: false},
                {name: "状态2", field: "expenseAccountBean.approvalStatusBean.approvalStatusId", search: false},

            ],
            //buttons: [
            //{
            //buttonName: "详情",  //按钮名称
            //buttonPath: "disburseExpense/disburseExpense-add.html?id=${expenseAccountId}&way=detail",
            //},
            //]
        }
    );


});


function hideDepartment( tableDom ) {
    //当 页面 是 其他付款的时候 只展示 其中一个 部门td 如果是 项目付款 展示 实施部门td
    $("[data-name='departmentBean.departmentName-th']").remove();

    tableDom.find("tr").each(function (index, tr) {
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
    fixWidth4ThOnLoad();

}

