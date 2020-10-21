$(function () {
    getPageDataUtils(
        {
            pageable: { //分页配置
                pageNum: 1, //当前页
                pageSize: 10, // 每页数量
                sortField: ["contractId"], //排序字段 可以多个
                sort: "false" // 排序正反
            },
            fixed: {
                fixedTh: true,  //首行固定 默认 true
                fixedTd: true,  // 固定列 默认 false
                fixedTdLength: 1,  // 固定列数 默认0
            },
          // rollback: updateMaterial,
            tableName: "contract",
            elem: $("#myTable"),
            rowspanLength: "contractDepartmentMoneyBeans",
            cols: [
                {name: "项目编号", field: "projectBean.projectNum", search: true, multipleSelection: true},
                {name: "项目名称", field: "projectBean.projectName", search: true, multipleSelection: true},
                {name: "甲方名称", field: "projectBean.projectFirstPartyName", search: true, multipleSelection: true},
                {name: "乙方单位", field: "projectBean.implementBeans.secondPartyBean.secondPartyName", search: true, multipleSelection: true},
                {name: "项目类型", field: "projectBean.implementBeans.projectTypeBean.projectTypeName", search: true, multipleSelection: true},
                {name: "经营类型 ", field: "projectBean.projectManagementType", search: true, multipleSelection: true},
                {name: "签订时间 ", field: "contractSigningDate", search: true},
                {name: "合同状态 ", field: "contractState", search: true , type:"date",multipleSelection: true},
                {name: "概算金额 ", field: "contractEstimateMoney", search: true, multipleSelection: true,type:'float'},
                {name: "合同金额", field: "contractMoney", search: true, multipleSelection: true,type:'float'},
                //{name: "合同分配金额", field: "contractDepartmentMoneyBeans.contractDepartmentMoneyDistributionMoney",  search : true , multipleSelection: true },
                //{name: "合同分配金额", field: "projectBean.implementBeans[n].departmentBean.departmentName",  search : true , multipleSelection: true},
                //{name: "实施部", field: "projectBean.implementBeans[n].departmentBean.departmentName",  search : true , multipleSelection: true},
                {
                    name: "已分配实施部",
                    field: "contractDepartmentMoneyBeans[n].implementBean.departmentBean.departmentName",
                    search: true,
                    multipleSelection: true
                },
                {
                    name: "分配金额",
                    field: "contractDepartmentMoneyBeans[n].contractDepartmentMoneyDistributionMoney",
                    search: true,
                    multipleSelection: true
                    ,type:'float'
                },
                {name: "签订时间", field: "contractSigningDate", search: true, multipleSelection: true,type:'date'},
                {name: "原件数 ", field: "contractRawFileCount", search: true, multipleSelection: true},
                {name: "副件数 ", field: "contractAccessoryCount", search: true, multipleSelection: true},
                {name: "扫描件 ", field: "contractScannedExists", search: true, multipleSelection: true},
            ],
            buttons: [
                {
                    buttonName: "编辑",  //按钮名称
                    buttonPath: "contract/contract-add.html?id=${contractId}&edit=true",  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        // {
                        //     key: "managementId",  //条件key
                        //     value: 0,  //条件 value
                        //     comparisons: "gt"
                        // }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "详情",  //按钮名称
                    buttonPath: "contract/contract-add.html?id=${contractId}",  //  删除不要写path
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
                            key: "contractId",  //条件key  如果是删除 把主键写在这儿
                            // value : 0 ,  //条件 value 不要写value
                            comparisons: "gt"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                }
            ]
        }
    );
})

// function getPageDate(pageNum, pageSize) {
//     var formTemp = $("#searchForm");
//     var params = $.param({"pageNum": pageNum, "pageSize": pageSize })+ '&' + formTemp.serialize();
//     $.get("/contract/s", params, function (data) {
//         var items = $('#items');
//         items.empty();
//         $(data.content).each(function (index, content) {
//             var rightTable = "";
//             var leftTable;
//             var tail="";
//             var deptTemp = Math.ceil(Math.random()*10000 );
//
//             var rowSpan = content["projectBean"]["implementBeans"].length > 1 ? content["projectBean"]["implementBeans"].length : 1;
//             var isMaterial = content["materialBeans"].length > 0 ? "有": "无";
//             //左边表格
//             leftTable =
//                 '<tr>'
//                 + '<td rowspan="'+rowSpan+'">' + content["projectBean"].projectNum + '</td>'//项目编号
//                 + '<td rowspan="'+rowSpan+'">' + content["projectBean"].projectName + '</td>'//项目名称
//                 + '<td rowspan="'+rowSpan+'">' + content["projectBean"]["projectManagementType"] + '</td>'//经营类型
//                 + '<td rowspan="'+rowSpan+'">' + content["contractState"] + '</td>'//合同状态
//                 + '<td rowspan="'+rowSpan+'">' + content["contractEstimateMoney"]+ '</td>'//预估金额
//                 + '<td rowspan="'+rowSpan+'">' + content["contractMoney"] + '</td>';//合同总金额
//
//             tail =
//                 '<td rowspan="'+rowSpan+'">' + content["contractSigningDate"] + '</td>'//签订时间
//                 +'<td rowspan="'+rowSpan+'">' + content["contractRawFileCount"]+ '</td>'//原件数
//                 +'<td rowspan="'+rowSpan+'">' + content["contractAccessoryCount"] + '</td>'//副件数
//                 +'<td rowspan="'+rowSpan+'">'+isMaterial+'</td>'
//                 +'<td rowspan="'+rowSpan+'" style="padding: 0 3px ">'
//                 + '<button type="button" class="btn btn-warning btn-xs edit-btn " style="margin-right: 5px" title="编辑" data-title="编辑合同" data-href="contract/contract-add.html?id='+content["contractId"]+'&edit=true">编辑</button>'
//                 + '<button type="button" class="btn btn-primary btn-xs edit-btn " style="margin-right: 5px" title="详情" data-title="合同详情" data-href="contract/contract-add.html?id='+content["contractId"]+'">详情</button>'
//                 + '</td>'
//                 + '</tr>';
//
//             // 右边表格
//             $(content["projectBean"]["implementBeans"]).each(function (index,implementBeans) {
//                 var implName = implementBeans["departmentBean"]["departmentName"];//实施部
//                 if(index === 0) {
//                     rightTable =
//                         "<td>"+"</td>"+
//                         "<td id='dept-"+deptTemp+"-"+implementBeans["departmentBean"]["departmentId"]+"'>"+implName +"</td>"//实施部
//                 }else {
//                     tail +=
//                         "<tr>"+
//                         "<td>"+"</td>"+
//                         "<td id='dept-"+deptTemp+"-"+implementBeans["departmentBean"]["departmentId"]+"'>"+implName +"</td>" + //实施部
//                         "</tr>";
//                 }
//             });
//             items.append(leftTable+rightTable+tail);
//
//             // 铺部门分配金额
//             $(content["contractDepartmentMoneyBeans"]).each(function (index, contractDepartmentMoneyBean) {
//                 $("#dept-" + deptTemp + "-" + contractDepartmentMoneyBean["implementBean"]["departmentBean"]["departmentId"] ).prev().text(contractDepartmentMoneyBean["contractDepartmentMoneyDistributionMoney"]);
//             });
//
//         });
//         showPageButtuns({
//             "pageNum": data.number + 1,
//             "countPage": data["totalPages"],
//             "pageSize": data["size"],
//             "countNum": data["totalElements"]
//         }, $("#page"), getPageDate)
//     },"json").fail(function (res) {
//         console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
//     });
// }

function updateMaterial() {
    let url="/contract/update_material"
    let data=""
    $.ajax({
        "url": url,
        "data": data,
        "type": "PUT",
        "dataType": "json",
        "success": function (json) {


        }
    })
}


$(function () {

    // getPageDate(1, 10);
    // $(document).on("click", ".btn-contract", function () {
    //     getPageDate(pageDataA["pageNum"], pageDataA["pageSize"]);
    // });
    // var label_text;
    // var th_text;
    // $(".th-checkbox input[type='checkbox']:not(:checked)").each(function () {
    //     label_text = $(this).next().text();
    //     $("#operating_record_table th").each(function (index, th) {
    //         th_text = $(th).children('span').text();
    //         if (label_text === th_text) {
    //             $("#operating_record_table tbody tr").each(function () {
    //                 $(this).children().eq(index).text();
    //                 $(this).children().eq(index).hide();
    //             });
    //             $(this).hide();
    //         }
    //     })
    // });

    // 2 自营和合作的同步
    $(document).on("click", ".hz", function () {
        let _this = $(this);
        console.log();
        if (_this.attr("property") === "now") {
            $("#item_id_691").click();
            getPageDate(1, 10);
        } else {
            $('.hz').each(function (i, e) {
                $(e).prop('checked', _this.prop("checked"));
            })
        }
        if ($(".zy").prop("checked") === false && _this.prop("checked") === false) {
            $('.zy').each(function (i, e) {
                $(e).prop('checked', !_this.prop("checked"));
            })
        }
    });
    $(document).on("click", ".zy", function () {
        let _this = $(this);
        if (_this.attr("property") === "now") {
            $("#item_id_501").click();
            getPageDate(1, 10);
        } else {
            $('.zy').each(function (i, e) {
                $(e).prop('checked', _this.prop("checked"));
            })
        }
        if ($(".hz").prop("checked") === false && _this.prop("checked") === false) {
            $('.hz').each(function (i, e) {
                $(e).prop('checked', !_this.prop("checked"));
            })
        }
    });
});
