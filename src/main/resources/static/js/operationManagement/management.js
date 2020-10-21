$(function () {
    getPageDataUtils(
        {
            pageable: { //分页配置
                pageNum: 1, //当前页
                pageSize: 10, // 每页数量
                sortField: ["managementId"], //排序字段 可以多个
                sort: "false" // 排序正反
            },
            fixed : {
                fixedTh : true ,  //首行固定 默认 true
                fixedTd : true ,  // 固定列 默认 false
                fixedTdLength : 1    // 固定列数 默认0
            } ,
            tableName: "management",
            elem: $("#myTable"),
            rowspanLength: "projectBean.implementBeans",
            cols: [
                {name: "项目编号", field: "projectBean.projectNum",  search : true , multipleSelection: true},
                {name: "项目名称", field: "projectBean.projectName",  search : true , width : "300px" },
                {name: "经营类型", field: "projectBean.projectManagementType"},
                {name: "合同金额", field: "projectBean.contractBean.contractMoney" , search : true , type: "float" },
                {name: "主负责人", field: "managementMainHead",  search : true , multipleSelection: true},
                {name: "提成模式", field: "managementCommissionMode",  search : true , multipleSelection: true},
                {name: "经营负责人", field: "projectBean.implementBeans[n].implementImplementHead",  search : true , multipleSelection: true},
                {name: "实施部", field: "projectBean.implementBeans[n].departmentBean.departmentName",  search : true , multipleSelection: true },
                {name: "业务人", field: "managementPartnersBean.userBean.userName",  search : true , multipleSelection: true },
                {name: "业务人身份", field: "managementPartnersIdentity",  search : true , multipleSelection: true },
                {name: "推荐人", field: "refereeUserBean.userName",  search : true , multipleSelection: true },
                {name: "业务介绍人", field: "managementSponsor",  search : true , multipleSelection: true },
                // {name: "内部合伙人", field: "managementInnerPartnerBean.managementInnerPartnerName",  search : true , multipleSelection: true },
                // {name: "合作伙伴", field: "managementCooperativePartnerBean.managementCooperativePartnerName",  search : true , multipleSelection: true },
                {name: "协调/作费", field: "managementCoordinateFee",  search : true , multipleSelection: true},
                {name: "税率", field: "managementRate", type: "float",  search : true ,  multipleSelection: true ,  fixed: 2},
                {name: "管理费率", field: "managementFee", type: "float",  search : true ,  multipleSelection: true ,  fixed: 2},
                {name: "财评金额", field: "managementGoodsEvaluationAmount", type: "float",  search : true , fixed: 2},
                {name: "预算金额", field: "managementSettlementAmount", type: "float",  search : true , fixed: 2},
                {name: "审计金额", field: "managementAuditAmount", type: "float",  search : true , fixed: 2},
                {name: "操作人", field: "createUserBean.userName",  search : true , multipleSelection: true},
                {name: "操作时间", field: "managementCreateTime",  search : true , type: "date"}
            ],
            buttons: [
                {
                    buttonName: "编辑",  //按钮名称
                    buttonPath : "management/management-add.html?id=${managementId}" ,  //  删除不要写path
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
                            key: "managementId",  //条件key  如果是删除 把主键写在这儿
                            // value : 0 ,  //条件 value 不要写value
                            comparisons: "gt"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                }
            ]
        }
    );


    // 2 自营和合作的同步
    let managementType = $("[name='$D.projectBean.projectManagementType']");
    $(document).on("click",".zy",function () {
        let hzDom = $(".hz");
        if( hzDom.prop("checked") === false && $(this).prop("checked") === false){
            managementType.val("合作");
            hzDom.prop('checked',!$(this).prop("checked"));
        }else if( hzDom.prop("checked") === true && $(this).prop("checked") === false){
            managementType.val("合作");
        }else if ( hzDom.prop("checked") === true && $(this).prop("checked") === true){
            managementType.val("自营$合作")
        }else {
            managementType.val("自营");
        }

        $(".input-group-btn").trigger("click");

    });

    $(document).on("click",".hz",function () {
        let zyDom = $(".zy");
        if( zyDom.prop("checked") === false && $(this).prop("checked") === false){
            managementType.val("自营");
            zyDom.prop('checked',!$(this).prop("checked"));
        }else if( zyDom.prop("checked") === true && $(this).prop("checked") === false){
            managementType.val("自营")
        }else if ( zyDom.prop("checked") === true && $(this).prop("checked") === true) {
            managementType.val("自营$合作")
        }else {
            managementType.val("合作");
        }

        $(".input-group-btn").trigger("click");
    });


});
