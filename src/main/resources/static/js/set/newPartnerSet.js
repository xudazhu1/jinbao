$(function () {
    getPageDataUtils(
        {
            pageable: { //分页配置
                pageNum: 1, //当前页
                pageSize: 10, // 每页数量
                sortField: ["managementPartnersId"], //排序字段 可以多个
                sort: "false" // 排序正反
            },
            fixed : {
                fixedTh : true ,  //首行固定 默认 true
                fixedTd : false ,  // 固定列 默认 false
                fixedTdLength : 0 ,  // 固定列数 默认1 最小1
            } ,
            tableName: "management_partners",
            elem: $("#myTable"),
            // rowspanLength: "projectBean.implementBeans",
            cols: [
                {name: "Id", field: "managementPartnersId",  search : true , multipleSelection: false},
                {name: "姓名", field: "userBean.userName",  search : true ,  multipleSelection: false},
                {name: "身份", field: "managementPartnersIdentity" ,  search : true },
                {name: "推荐人", field: "refereesUserBean.userName" ,    search : true },
                // {name: "权限", field: "permissionBeans[n].permissionName", width : "40%"  ,  search : true , multipleSelection: false},
            ],
            buttons: [
                {
                    buttonName: "编辑",  //按钮名称
                    windowWidth : "600px",
                    windowHeight : "400px",
                    // buttonType : "other" ,
                    buttonPath : "role/role-inner.html?id=${managementPartnersId}" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        // {
                        //     key: "permissionDataPermission",  //条件key
                        //     value: "false",  //条件 value
                        //     comparisons: "eq"
                        // }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                //     isDelete: true,
                //     buttonName: "删除",  //按钮名称
                //     // buttonPath : "management/management-add.html?id=${managementId}" ,  //按钮路径 ${field} 支持查出值用于拼接路径
                //     condition: [ // 按钮出现条件 可以是多个
                //         {
                //             key: "managementId",  //条件key  如果是删除 把主键写在这儿
                //             // value : 0 ,  //条件 value 不要写value
                //             comparisons: "gt"
                //         }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                //     ]
                // }
            ]
        }
    );


});
