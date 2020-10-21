function showOrHide() {
    showHide();
    fixWidth4ThOnLoad();
}

function show() {
    getPageDataUtils(
        {
            pageable: { //分页配置
                pageNum: 1, //当前页
                pageSize: 10, // 每页数量
                sortField: ["datumId"], //排序字段 可以多个
                sort: "true" // 排序正反
            },
            fixed: {
                fixedTh: true,  //首行固定 默认 true
                fixedTd: true,  // 固定列 默认 false
                fixedTdLength: 1,  // 固定列数 默认0
            },
            rollback: showOrHide,
            tableName: "datum",
            elem: $("#myTable"),
            //rowspanLength: "projectBean.implementBeans",
            cols: [
                {name: "对应编号", field: "implementBean.projectBean.projectNum", search: true, multipleSelection: true},
                {name: "项目名称", field: "implementBean.projectBean.projectName", search: true},
                {name: "项目类型", field: "implementBean.projectBean.projectBelongsType", search: true,multipleSelection: true},
                {name: "甲方提供资料", field: "datumParafile"},
                {name: "概算", field: "datumRough", search: true, multipleSelection: true},
                {name: "预算", field: "datumBudget", search: true, multipleSelection: true},
                {name: "结算", field: "datumClose", search: true, multipleSelection: true},
                {
                    name: "合同",
                    field: "implementBean.projectBean.contractBean.contractScannedExists",
                    search: true,
                    multipleSelection: true
                },
                {name: "勘察方案", field: "datumSursche", search: true, multipleSelection: true},
                {name: "资料成果", field: "datumResults", search: true, multipleSelection: true},
                {name: "中标通知书", field: "datumAdvicenote", search: true, multipleSelection: true},
                {name: "监测方案", field: "datumSupervise", search: true, multipleSelection: true},
                {
                    name: "报告文本",
                    field: "datumReporttext",
                    type: "float",
                    search: true,
                    multipleSelection: true,
                    fixed: 2
                },
                {name: "批复文件", field: "datumWrittenfile", type: "float", search: true, fixed: 2},
                {name: "测量方案", field: "datumMeasure", type: "float", search: true, fixed: 2},
                //{name: "资料成果", field: "datumResults", type: "float",  search : true , fixed: 2},
                {name: "财评", field: "datumEvalua", search: true, multipleSelection: true},
                {name: "审计", field: "datumAudit", search: true, type: "date"},
                {name: "部门", field: "implementBean.departmentBean.departmentName", search: true,multipleSelection: true},
                {name: "实施负责人", field: "implementBean.implementImplementHead", search: true,multipleSelection: true}
            ],
            buttons: [
                {
                    buttonName: "编辑",  //按钮名称
                    buttonPath: "data/data-add.html?id=${datumId}",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        // {
                        //     key: "managementId",  //条件key
                        //     value: 0,  //条件 value
                        //     comparisons: "gt"
                        // }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    isDelete: true,
                    buttonName: "删除",  //按钮名称
                    // buttonPath : "management/management-add.html?id=${managementId}" ,  //按钮路径 ${field} 支持查出值用于拼接路径
                    condition: [ // 按钮出现条件 可以是多个
                        // {
                        //     key: "managementId",  //条件key  如果是删除 把主键写在这儿
                        //     // value : 0 ,  //条件 value 不要写value
                        //     comparisons: "gt"
                        // }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                }
            ]
        }
    );
}


$(function () {
    show()
    $(".patternInput").click(function () {
        showHide();
    })


});
function showHide() {
    var item = $(":radio:checked");
    var len = item.length;
    if (len > 0) {
        var department = $("[name='abc']:checked").val();
        if (department === "勘察部") {
            /*报告文本*/
            $("table .datumReporttext-th").hide()
            $("table .datumReporttext").hide()
            /*监测方案*/
            $("table .datumSupervise-th").hide()
            $("table .datumSupervise").hide()
            /* 批复文件*/
            $("table .datumWrittenfile-th").hide()
            $("table .datumWrittenfile").hide()
            /*测量方案*/
            $("table .datumMeasure-th").hide()
            $("table .datumMeasure").hide()
            /*概算*/
            $("table .datumRough-th:not(.copy-first-column .datumRough-th)").show()
            $("table .datumRough:not(.copy-first-column .datumRough)").show();
            /*勘察方案*/
            $("table .datumSursche-th:not(.copy-first-column .datumSursche-th)").show()
            $("table .datumSursche:not(.copy-first-column .datumSursche)").show()
            /*中标通知书*/
            $("table .datumAdvicenote-th:not(.copy-first-column .datumAdvicenote-th)").show()
            $("table .datumAdvicenote:not(.copy-first-column .datumAdvicenote)").show()
            /*资料成果*/
            $("table .datumResults-th:not(.copy-first-column .datumResults-th)").show()
            $("table .datumResults:not(.copy-first-column .datumResults)").show()
            fixWidth4ThOnLoad();
        }
        if (department === "咨询部") {
            /*概算*/
            $("table .datumRough-th").hide()
            $("table .datumRough").hide()
            /*勘察方案*/
            $("table .datumSursche-th").hide()
            $("table .datumSursche").hide()
            /*测量方案*/
            $("table .datumMeasure-th").hide()
            $("table .datumMeasure").hide()
            /*中标通知书*/
            $("table .datumAdvicenote-th").hide()
            $("table .datumAdvicenote").hide()
            /*资料成果*/
            $("table .datumResults-th").hide()
            $("table .datumResults").hide()
            /*监测方案*/
            $("table .datumSupervise-th:not(.copy-first-column .datumSupervise-th)").show()
            $("table .datumSupervise:not(.copy-first-column .datumSupervise)").show()
            /*报告文本*/
            $("table .datumReporttext-th:not(.copy-first-column .datumReporttext-th)").show()
            $("table .datumReporttext:not(.copy-first-column .datumReporttext)").show()
            /*批复文件*/
            $("table .datumWrittenfile-th:not(.copy-first-column .datumWrittenfile-th)").show()
            $("table .datumWrittenfile:not(.copy-first-column .datumWrittenfile)").show()
            fixWidth4ThOnLoad();
        }
        if (department === "测量部") {
            /*勘察方案*/
            $("table .datumSursche-th").hide()
            $("table .datumSursche").hide()
            /*监测方案*/
            $("table .datumSupervise-th").hide()
            $("table .datumSupervise").hide()
            /*报告文本*/
            $("table .datumReporttext-th").hide()
            $("table .datumReporttext").hide()
            /*  批复文件*/
            $("table .datumWrittenfile-th").hide()
            $("table .datumWrittenfile").hide()
            /*概算*/
            $("table .datumRough-th:not(.copy-first-column .datumRough-th)").show()
            $("table .datumRough:not(.copy-first-column .datumRough)").show()
            /*资料成果*/
            $("table .datumResults-th:not(.copy-first-column .datumResults-th)").show()
            $("table .datumResults:not(.copy-first-column .datumResults)").show()
            /*测量方案*/
            $("table .datumMeasure-th:not(.copy-first-column .datumMeasure-th)").show()
            $("table .datumMeasure:not(.copy-first-column .datumMeasure)").show()
            /*中标通知书*/
            $("table .datumAdvicenote-th:not(.copy-first-column .datumAdvicenote-th)").show()
            $("table .datumAdvicenote:not(.copy-first-column .datumAdvicenote)").show()
            fixWidth4ThOnLoad();
        }
    }
    fixWidth4ThOnLoad();
}


