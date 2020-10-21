$(function () {
    getPageDataUtils(
        {
            pageable: { //分页配置
                pageNum: 1, //当前页
                pageSize: 10, // 每页数量
                sortField: ["projectId"], //排序字段 可以多个
                sort: "false" // 排序正反
            },
            fixed : {
                fixedTh : true ,  //首行固定 默认 true
                fixedTd : true ,  // 固定列 默认 false
                fixedTdLength : 1 ,  // 固定列数 默认0
            } ,
            tableName: "project",
            elem: $("#myTable"),
            rowspanLength: "implementBeans",
            cols: [
                {name: "项目编号", field: "projectNum" , search : true },
                {name: "项目名称", field: "projectName"  ,  search : true },
                {name: "收益单位", field: "earningsCompanyBean.earningsCompanyName",  search : true , multipleSelection: true},
                {name: "立项时间", field: "projectApprovalTime", type:"date" ,  search : true},
                {name: "经营类型", field: "projectManagementType"  },
                {name: "企业类型", field: "projectBelongsType"  },
                {name: "项目地点", field: "projectLocationBean.projectLocationName",  search : true , multipleSelection: true},
                {name: "甲方名称", field: "projectFirstPartyName" ,  search : true},
                {name: "工作内容", field: "projectWorkContent" ,  search : true},
                {name: "经营部负责人", field: "managementBean.managementMainHead",  search : true , multipleSelection: true},
                {name: "乙方单位", field: "implementBeans[n].secondPartyBean.secondPartyName" ,  search : true , multipleSelection: true},
                {name: "实施部门", field: "implementBeans[n].departmentBean.departmentName", search : true , multipleSelection: true},
                {name: "实施负责人", field: "implementBeans[n].implementImplementHead" , search : true , multipleSelection: true},
            ],
            buttons: [
                {
                    buttonName: "编辑",  //按钮名称
                    buttonPath : "project/project-add.html?id=${projectId}" ,  //  删除不要写path
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
                            key: "projectId",  //条件key  如果是删除 把主键写在这儿
                            // value : 0 ,  //条件 value 不要写value
                            comparisons: "gt"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                }
            ]
        }
    );
})


// function getPageDate(pageNum,pageSize) {
//     var formTemp = $("#searchForm");
//     var params = $.param({"pageNum": pageNum, "pageSize": pageSize })+ '&' + formTemp.serialize();
//     $.get("/project/s", params, function (data) {
//         var items = $('#items');
//         items.empty();
//         $(data.content).each(function (index, project) {
//             var rightTable = "";
//             var leftTable;
//             var tail;
//             var rowSpan = project["implementBeans"].length > 1 ? project["implementBeans"].length : 1;
//             //左边表格
//             leftTable =
//                 '<tr>'
//                 + '<td rowspan="'+rowSpan+'">' + project.projectNum + '</td>'//项目编号
//                 + '<td rowspan="'+rowSpan+'">' + project.projectName + '</td>'//项目名称
//                 + '<td rowspan="'+rowSpan+'">' + project.projectApprovalTime + '</td>'//立项时间
//                 + '<td rowspan="'+rowSpan+'">' + project.projectManagementType + '</td>'//经营类型
//                 + '<td rowspan="'+rowSpan+'">' + project["projectLocationBean"]["projectLocationName"]+ '</td>'//项目地点
//                 + '<td rowspan="'+rowSpan+'">' + project.projectFirstPartyName + '</td>'//甲方名称
//                 + '<td rowspan="'+rowSpan+'">' + project.projectWorkContent + '</td>'//工作内容
//                 + '<td rowspan="'+rowSpan+'">' + project["managementBean"]["managementMainHead"] + '</td>';//经营主负责人
//
//             tail = '<td rowspan="'+rowSpan+'">'
//                 + '<button type="button" class="btn btn-warning btn-xs edit-btn " style="margin-right: 5px" title="编辑" data-title="编辑项目" data-width="840px" data-height="700px" data-href="project/project-add.html?id='+project["projectId"]+'">编辑</button>'
//                 + '<button type="button" class="btn btn-danger btn-xs delete-btn" data-id="'+project.projectId+'">删除</button>'
//                 + '</td>'
//                 + '</tr>';
//
//             // 右边表格
//             $(project["implementBeans"]).each(function (index,implementBeans) {
//                 var implName = implementBeans["departmentBean"]["departmentName"];//实施部名字
//                 var implementImplementHead = implementBeans["implementImplementHead"];//实施部负责人
//                 if(index === 0) {
//                     rightTable =
//                     "<td>"+implName +"</td>" +
//                     "<td>"+implementImplementHead+"</td>";
//                 }else {
//                     tail +=
//                         "<tr>"+
//                         "<td>"+implName +"</td>" +
//                         "<td>"+implementImplementHead+"</td>"+
//                         "</tr>";
//                 }
//             });
//             items.append(leftTable+rightTable+tail);
//         });
//
//
//
//         showPageButtuns(
//             {"pageNum":data.number + 1 , "countPage": data["totalPages"] ,"pageSize" : data["size"] ,  "countNum": data["totalElements"] } ,
//             $("#page") ,
//             getPageDate);
//     },"json").fail(function (res) {
//         // sweetAlert('数据提交失败 请刷新重试 ');
//         console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
//     });
// }

$(function () {
    // 1 铺数据

    var label_text;
    var th_text;
    //列的隐藏
    $(".th-checkbox input[type='checkbox']:not(:checked)").each(function () {
        label_text = $(this).next().text();
        $("#operating_record_table th").each(function (index, th) {
            // noinspection JSValidateTypes
            th_text = $(th).children('span').text();
            if (label_text === th_text) {
                $("#operating_record_table tbody tr").each(function () {
                    // noinspection JSValidateTypes
                    $(this).children().eq(index).text(); //  ???????
                    // noinspection JSValidateTypes
                    $(this).children().eq(index).hide();
                });
                $(this).hide();
            }
        })
    })

    // 2 自营和合作的同步
    var managementType = $("[name='$D.projectManagementType']");
    $(document).on("click",".zy",function () {
        if($(".hz").prop("checked") === false && $(this).prop("checked") === false){
            managementType.val("合作");
            $('.hz').prop('checked',!$(this).prop("checked"));
        }else if($(".hz").prop("checked") === true && $(this).prop("checked") === false){
            managementType.val("合作");
        }else if ($(".hz").prop("checked") === true && $(this).prop("checked") === true){
            managementType.val("自营$合作")
        }else {
            managementType.val("自营");
        }

        $(".input-group-btn").trigger("click");

    });

    $(document).on("click",".hz",function () {
        if($(".zy").prop("checked") === false && $(this).prop("checked") === false){
            managementType.val("自营");
            $('.zy').prop('checked',!$(this).prop("checked"));
        }else if($(".zy").prop("checked") === true && $(this).prop("checked") === false){
            managementType.val("自营");
        }else if ($(".zy").prop("checked") === true && $(this).prop("checked") === true) {
            managementType.val("自营$合作");
        }else {
            managementType.val("合作");
        }

        $(".input-group-btn").trigger("click");
    });
});
