$(function () {
    getPageDataUtils(
        {
            pageable: { //分页配置
                pageNum: 1, //当前页
                pageSize: 10, // 每页数量
                sortField: ["implementId"], //排序字段 可以多个
                sort: "false" // 排序正反
            },
            fixed : {
                fixedTh : true ,  //首行固定 默认 true
                fixedTd : true ,  // 固定列 默认 false
                fixedTdLength : 1 ,  // 固定列数 默认0
            } ,
            rollback: showPercent,
            tableName: "implement",
            elem: $("#myTable"),
            cols: [
                {name: "项目编号", field: "projectBean.projectNum" , search : true },
                {name: "项目名称", field: "projectBean.projectName"  ,  search : true },
                {name: "经营类型", field: "projectBean.projectManagementType" ,  search : true , multipleSelection: true},
                {name: "项目地点", field: "projectBean.projectLocationBean.projectLocationName",  search : true , multipleSelection: true},
                {name: "实施部门", field: "departmentBean.departmentName" ,  search : true , multipleSelection: true},
                {name: "实施负责人", field: "implementImplementHead" ,  search : true , multipleSelection: true},
                {name: "乙方名称", field: "secondPartyBean.secondPartyName",  search : true , multipleSelection: true},
                {name: "项目状态", field: "projectStatusBean.projectStatusName", search : true , multipleSelection: true},
                {name: "实施记录", field: "implementRecordContent" , search : true , multipleSelection: true},
                {name: "实施进度", field: "implementProgress" , search : true , multipleSelection: true},
            ],
            buttons: [
                {
                    buttonName: "编辑",  //按钮名称
                    buttonPath : "implement/implement-add.html?id=${implementId}" ,  //  删除不要写path
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
                        {
                            key: "implementId",  //条件key  如果是删除 把主键写在这儿
                            // value : 0 ,  //条件 value 不要写value
                            comparisons: "gt"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                }
            ]
        }
    );
});
function showPercent() {
    //当 页面 是 其他付款的时候 只展示 其中一个 部门td 如果是 项目付款 展示 实施部门td

    $(".no-copy-show-altrowstable tbody").find("tr").each(function (index, tr) {
        let departmentTr = false;
        let implementTr = false;
        $(tr).find("[data-name='implementProgress-td']").each(function () {
            if($(this).children(".table-utils-td-text").text() !== ""){
                $(this).children(".table-utils-td-text").text(parseInt($(this).children(".table-utils-td-text").text()*100)+'%')
            }
        });


    });

}
