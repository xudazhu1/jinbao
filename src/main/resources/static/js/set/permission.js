$(function () {
    getPageDataUtils(
        {
            pageable: { //分页配置
                pageNum: 1, //当前页
                pageSize: 10, // 每页数量
                sortField: ["permissionId"], //排序字段 可以多个
                sort: "false" // 排序正反
            },
            fixed : {
                fixedTh : true ,  //首行固定 默认 true
                fixedTd : false ,  // 固定列 默认 false
                fixedTdLength : 0 ,  // 固定列数 默认1 最小1
            } ,
            tableName: "permission",
            elem: $("#myTable"),
            // rowspanLength: "projectBean.implementBeans",
            cols: [
                {name: "Id", field: "permissionId",  search : true , multipleSelection: false},
                {name: "类型", field: "permissionType",  search : true , multipleSelection: true},
                {name: "权限标识符(URL)", field: "permissionTag",  search : true },
                {name: "权限所属分类", field: "permissionClass" , search : true },
                {name: "权限名", field: "permissionName",  search : true , multipleSelection: false},
                {name: "备注", field: "permissionRemark",  search : true , multipleSelection: false},
            ],
            buttons: [
                {
                    buttonName: "标记为数据权限",  //按钮名称
                    buttonType : "other" ,
                    // buttonPath : "management/management-add.html?id=${managementId}" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "permissionDataPermission",  //条件key
                            value: "false",  //条件 value
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                {
                    buttonName: "已是数据权限",  //按钮名称
                    buttonType : "other" ,
                    // buttonPath : "management/management-add.html?id=${managementId}" ,  //  删除不要写path
                    condition: [ // 按钮出现条件 可以是多个
                        {
                            key: "permissionDataPermission",  //条件key
                            value: "true",  //条件 value
                            comparisons: "eq"
                        }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                    ]
                },
                // {
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


    $(document).on( "click" , "[data-title='标记为数据权限']" , function () {
        let permissionId =  $(this).closest("tr").find("[data-name='permissionId-td'] .table-utils-td-text").text() ;
        $.post( "/table_utils" , {"_method" : "PUT" , tableName : "permission" , permissionId : permissionId , permissionDataPermission : "true" } , function (data) {
            if ( data ) {
                alert('成功');
                flush();
            }
        } , "json" );
    });
    $(document).on( "click" , "[data-title='已是数据权限']" , function () {
        let permissionId =  $(this).closest("tr").find("[data-name='permissionId-td'] .table-utils-td-text").text() ;
        $.post( "/table_utils" , {"_method" : "PUT" , tableName : "permission" , permissionId : permissionId , permissionDataPermission : "false" } , function (data) {
            if ( data ) {
                alert('取消成功');
                flush();
            }
        } , "json" );
    });
});
