// noinspection ES6ConvertVarToLetConst
var layerIndex;
function  treeSelected( ids){
    $.get("/permission" , {"$D.permissionId": ids} , function (data) {
        var tBodyTemp = $("#permissions");
        // tBodyTemp.empty();
        $(data["content"]).each(function (index , permission) {
            permission_ids.push( permission["permissionId"] );
            tBodyTemp.append('<tr>\n' +
                '    <td>\n' +
                '        <input type="checkbox" lay-skin="primary">\n' +
                '    </td>\n' +
                '    <td>' +
                '<span>'+permission["permissionId"]+'</span>' +
                '<input type="hidden" class="beans-id permission-id" name="permissionBeans['+index+'].permissionId" value="'+permission["permissionId"]+'">' +
                '</td>\n' +
                '    <td>'+permission["permissionName"]+'</td>\n' +
                '    <td>'+permission["permissionRemark"]+'</td>\n' +
                '    <td>\n' +
                '        <button type="button" class="layui-btn layui-btn-xs layui-btn-danger delete-btn">删除</button>\n' +
                '    </td>\n' +
                '</tr>');
        });
        serializeCustomData();

        //重新渲染\
        layui.form.render('checkbox');
        //修复角标
        renderIndex4Tbody(tBodyTemp);

    } , "json").fail(function (res) {
        sweetAlert('数据提交失败 请刷新重试 ');
        console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
    });
}


//修复权限角标
function renderIndex4Tbody( tBody ) {
    // let moneyBackMoney = $(".moneyBackMoney");
    tBody.find("tr").each(function (index , trTemp ) {
        $( trTemp ).find("[name]").each(function () {
            let nameTemp = $(this).prop("name");
            if ( nameTemp.indexOf("[") !== -1 ) {
                // noinspection RegExpRedundantEscape
                $(this).prop("name" , nameTemp.replace(/\[\d+\]/ , "["+index+"]"));
            }
        });

    });
}

function  userTreeSelected( ids){
    $.get("/user" , {"$D.userId": ids} , function (data) {
        var tBodyTemp = $("#users");
        tBodyTemp.empty();
        $(data["content"]).each(function (index , user) {
            tBodyTemp.append('<tr>\n' +
                '    <td>\n' +
                '        <input type="checkbox" lay-skin="primary">\n' +
                '    </td>\n' +
                '    <td>' +
                '<span>'+user["userId"]+'</span>' +
                '<input type="hidden" class="beans-id" name="userBeans['+index+'].userId" value="'+user["userId"]+'">' +
                '</td>\n' +
                '    <td>'+user["userName"]+'</td>\n' +
                '    <td>\n' +
                '        <button type="button" class="layui-btn layui-btn-xs layui-btn-danger delete-btn">删除</button>\n' +
                '    </td>\n' +
                '</tr>');
        });

        //重新渲染\
        layui.form.render('checkbox');

    } , "json").fail(function (res) {
        sweetAlert('数据获取失败 请刷新重试 ');
        console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据获取失败 请刷新重试");
    });
}
let user_ids = [];

//拿用户
function getUserBeans(  id ) {
    let dataTemp = {"table_utils.tableName" : "user"
        , "table_utils.fields" : "userId$userName" };
    dataTemp["$D.jobBean.jobId"] = id;
    $.get("/table_utils" , dataTemp , function (data) {
        let tBodyTemp = $("#users");
        tBodyTemp.empty();
        $(data.content).each( function (index , user) {
            user_ids.push(user[0]);
            tBodyTemp.append('<tr>\n' +
                '<td>\n' +
                '    <input type="checkbox" lay-skin="primary">\n' +
                '</td>\n' +
                '<td>' +
                '   <span>'+user[0]+'</span>' +
                '   <input type="hidden" class="beans-id user-id" name="userBeans['+index+'].userId" value="'+user[0]+'">' +
                '</td>\n' +
                '    <td>'+user[1]+'</td>\n' +
                '    <td>\n' +
                '        <button type="button" class="layui-btn layui-btn-xs layui-btn-danger delete-btn">删除</button>\n' +
                '    </td>\n' +
                '</tr>');
        });
        serializeCustomData();

    } , "json" );

}

// noinspection ES6ConvertVarToLetConst
var permission_ids = [], dataRequest;
let spanType = getParamForUrl("spanType");
let id = getParamForUrl("spanId");
function showData(){
    // var id = getParamForUrl("spanType") +"Id";
    if ( spanType === "job" && id !== null ) {
        $("li.permission-data-elem").show();
        getPermissionDataBeans(id);
    } else {
        $("li.permission-data-elem").hide();
    }
    $.get("/" + spanType , { id : id} , function (data) {
        //根据类型与id拿到权限
        getPermissionBeans(spanType , id);
        //如果是职位 显示旗下用户 显示用户
        if ( spanType === "job" ) {
            getUserBeans( id);
        }


        dataRequest = data;
        var userBody = $("#users");
        userBody.empty();
        $(data["content"][0]["userBeans"]).each(function (index , user) {
            userBody.append('<tr>\n' +
                '    <td>\n' +
                '        <input type="checkbox" lay-skin="primary">\n' +
                '    </td>\n' +
                '    <td>' +
                '<span>'+user["userId"]+'</span>' +
                '<input type="hidden" class="beans-id" name="userBeans['+index+'].userId" value="'+user["userId"]+'">' +
                '</td>\n' +
                '    <td>'+user["userName"]+'</td>\n' +
                '    <td>\n' +
                '        <button type="button" class="layui-btn layui-btn-xs layui-btn-danger delete-btn">删除</button>\n' +
                '    </td>\n' +
                '</tr>');
        });


        //重新渲染\
        // 2 开启layui的form表单,可以实现复选框的功能
        layui.use(['form', 'layedit', 'laydate'], function () {
            var form = layui.form;
            form.render("checkbox");
        });
        // permission_ids = permission_ids_temp.toString().replace(/,/g , "$");
    } , "json").fail(function (res) {
        sweetAlert('数据获取失败 请刷新重试 ');
        console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据获取失败 请刷新重试");
    });
}

let mapping4permission = {"job": "jobBeans.jobId","department" : "departmentBeans.departmentId" , "company" : "companyBeans.companyId"  };

//拿权限
function getPermissionBeans( tableName , id ) {
    let keyName = mapping4permission[tableName];
    let dataTemp = {"table_utils.tableName" : "permission"
        , "table_utils.fields" : "permissionId$permissionName$permissionRemark" };
    dataTemp[ keyName ] = id;
    $.get("/table_utils" , dataTemp , function (data) {
        let tBodyTemp = $("#permissions");
        tBodyTemp.empty();
        $(data.content).each( function (index , permission) {
            permission_ids.push(permission[0]);
            tBodyTemp.append('<tr>\n' +
                '    <td>\n' +
                '        <input type="checkbox" lay-skin="primary">\n' +
                '    </td>\n' +
                '    <td>' +
                '<span>'+permission[0]+'</span>' +
                '<input type="hidden" class="beans-id permission-id" name="permissionBeans['+index+'].permissionId" value="'+permission[0]+'">' +
                '</td>\n' +
                '    <td>'+permission[1]+'</td>\n' +
                '    <td>'+permission[2]+'</td>\n' +
                '    <td>\n' +
                '        <button type="button" class="layui-btn layui-btn-xs layui-btn-danger delete-btn">删除</button>\n' +
                '    </td>\n' +
                '</tr>');
        });
        serializeCustomData();

    } , "json" );

}



// noinspection JSUnusedGlobalSymbols
function  selectedPermissionData( ids){
    $.get("/permission" , {"$D.permissionId": ids} , function (data) {
        showPermissionData(data.content);
        //重新渲染\
        layui.form.render('checkbox');
    } , "json").fail(function (res) {
        sweetAlert('数据提交失败 请刷新重试 ');
        console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
    });
}

function getPermissionDataBeans(jobId) {
    let dataR = { "table_utils.tableName" : "permission_data" ,  "jobBean.jobId" : jobId  };
    //铺项目编号项目名称
    $.get("/table_utils/info" , dataR , function (data) {
        showPermissionData(data.content);
    }, "json");

}

function showPermissionData(permissionDataBeans){
    has_permission_data_ids = [];
    let tBodyTemp = $("#permissionData");
    tBodyTemp.empty();
    $(permissionDataBeans).each(function (index, permission) {
        if ( typeof permission["permissionDataId"] !== "undefined" ) {
            permission["permissionName"] = permission["permissionBean"]["permissionName"];
            permission["permissionRemark"] = permission["permissionBean"]["permissionRemark"];
            permission["permissionName"] = permission["permissionBean"]["permissionName"];
            permission["permissionId"] = permission["permissionBean"]["permissionId"];
            has_permission_data_ids.push(permission["permissionBean"]["permissionId"]);
        } else {
            permission["permissionDataId"] = "";
            permission["permissionDataLevel"] = 1;
            has_permission_data_ids.push(permission["permissionId"] );
        }
        tBodyTemp.append('<tr>\n' +
            '<td>\n' +
            '    <input type="checkbox" lay-skin="primary">\n' +
            '    <input type="hidden" name="permissionDataBeans[0].permissionBean.permissionId" class="permission-id" value="'+permission["permissionId"]+'" >\n' +
            '    <input type="hidden" name="permissionDataBeans[0].permissionDataId" value="'+permission["permissionDataId"]+'" >\n' +
            '</td>\n' +
            '<td>' +
            '<span>'+permission["permissionRemark"]+'</span>' +
            '<td>\n' +
            '    <input title="仅自己" class="permissionDataLevel" type="radio"  name="permissionDataBeans['+index+'].permissionDataLevel" value="1" >\n' +
            '</td>\n' +
            '<td>\n' +
            '    <input title="同职位" class="permissionDataLevel" type="radio" name="permissionDataBeans['+index+'].permissionDataLevel" value="2" >\n' +
            '</td>\n' +
            '<td>\n' +
            '    <input title="同部门" class="permissionDataLevel" type="radio" name="permissionDataBeans['+index+'].permissionDataLevel" value="3" >\n' +
            '</td>\n' +
            '<td>\n' +
            '    <input title="同公司" class="permissionDataLevel" type="radio" name="permissionDataBeans['+index+'].permissionDataLevel" value="4" >\n' +
            '</td>\n' +
            '<td>\n' +
            '    <input title="全集团" class="permissionDataLevel" type="radio" name="permissionDataBeans['+index+'].permissionDataLevel" value="5" >\n' +
            '</td>\n' +
            '<td>\n' +
            '    <button type="button"\n' +
            '            class="layui-btn layui-btn-xs layui-btn-danger delete-btn">删除\n' +
            '    </button>\n' +
            '</td>' +
            '</tr>');

        $("[name='permissionDataBeans["+index+"].permissionDataLevel']").each(function () {
            if (  parseInt(this.value) === permission["permissionDataLevel"] ) {
                $(this).attr("checked" , "checked");
            }
        });
    });
    serializeCustomData();

    layui.form.render('checkbox');
    layui.form.render('radio');

    renderIndex4PermissionData();

}

function renderIndex4PermissionData() {
    // let moneyBackMoney = $(".moneyBackMoney");
    $("#permissionData tr").each(function (index , trTemp ) {
        $(trTemp).find("[name]").each(function () {
            let nameTemp = $(this).prop("name");
            if ( nameTemp.indexOf("[") !== -1 ) {
                // noinspection RegExpRedundantEscape
                $(this).prop("name" , nameTemp.replace(/\[\d+\]/ , "["+index+"]"));
            }
        });

    });
}

//重新计算 permission_ids 和 has_permission_data_ids
function fixIds() {
    console.log("ad");
    permission_ids = [-1];
    $("#permissions .permission-id").each( function ( index , inputDom) {
        permission_ids.push( $(inputDom).val() );
    });
    has_permission_data_ids = [-1];
    $("#permissionData .permission-id").each( function ( index , inputDom) {
        permission_ids.push( $(inputDom).val() );
    });
}



let has_permission_data_ids = [0];
// let customWhere = ["permissionId not in ( " + has_permission_data_ids + " ) " , " 1=1 " ];
//数据权限搜索条件
// noinspection JSUnusedGlobalSymbols
// noinspection ES6ConvertVarToLetConst
var permissionDataCondition = { };
// let permissionCustomWhere = ["permissionId not in ( " + permission_ids + " ) " , " 1=1 " ];
//数据权限搜索条件
// noinspection JSUnusedGlobalSymbols
// noinspection ES6ConvertVarToLetConst
var permissionCondition = { };

//组织权限窗的条件
function serializeCustomData() {
    fixIds();
    let customWhere = ["permissionId not in ( " + has_permission_data_ids + " ) " , " 1=1 " ];
//数据权限搜索条件
// noinspection JSUnusedGlobalSymbols
// noinspection ES6ConvertVarToLetConst
    let permissionCustomWhere = ["permissionId not in ( " + permission_ids + " ) " , " 1=1 " ];
//数据权限搜索条件
// noinspection JSUnusedGlobalSymbols
// noinspection ES6ConvertVarToLetConst
    let formTemp = $("<form></form>");
    $(customWhere).each(function () {
        formTemp.append("<input name='customWhere' value='"+this+"' >");
    });
    permissionDataCondition = $.param( { permissionDataPermission : "true"} ) + "&" + formTemp.serialize();
    let formTemp1 = $("<form></form>");
    $( permissionCustomWhere ).each(function () {
        formTemp1.append("<input name='customWhere' value='"+this+"' >");
    });
    permissionCondition = $.param({}) + "&" + formTemp1.serialize();

}

//删除后重新统计id
function deleteThis() {
    serializeCustomData();
}
//删除后重新统计id
function deleteAll() {
    serializeCustomData();
}


$(function () {

    serializeCustomData();

    showData();
    //1 Tab的切换功能
    //引用share.js

    // 2 开启layui的form表单,可以实现复选框的功能
    //引用share.js

    // 3复选框全选
    //引用share.js

    // 4 tbody里面的复选框选择事件
    //引用share.js

    // 5单次删除
    //引用share.js
    // $(document).on("click" , ".delete-btn" , function () {
    //     setTimeout(function () {
    //         fixIds();
    //     } , 10000 );
    // });
    // $(document).on("click" , ".all_delete_btn" , function () {
    //     setTimeout(function () {
    //         fixIds();
    //     } , 10000 );
    // });

    // 6批量删除
    //引用share.js

    // 7关闭弹出层
    $(document).on('click', '.cancel-btn', function () {
        window.parent.layer.close(window.parent.layerIndex);
    });


    // 8铺数据
    var this_name = $(".this-name");
    this_name.val(getParamForUrl("thisName"));
    $(".parent-name").text(getParamForUrl("parentName"));

    //9 如果是职位,则显示权限分配和用户管理,如果是分公司和部门,则只显示权限分配
    $("." + getParamForUrl("spanType") + "-show").css("display", "inline-block");

    // 10 name动态变化
    this_name.prop("name", getParamForUrl("spanType") + "Name");
    var this_name_hidden = $(".this-name-hidden");
    this_name_hidden.prop("name", getParamForUrl("spanType") + "Id");
    this_name_hidden.val(getParamForUrl("spanId"));

    // 10 提交编辑过后的数据
    $(document).on("click", ".confirm-btn", function () {
        if (this_name.val() === "") {
            swal("错误", "名称不能为空", "error");
            return;
        }
        var itemData = $.param({"_method": "PUT"}) + "&" + $("#inputForm").serialize();
        $.post("/" + getParamForUrl("spanType"), itemData, function (data) {
            if (data) {
                swal({
                    title: "修改成功!",
                    text: "I will close in 2 seconds.",
                    timer: 2000
                });
                window.parent.showStructure();
            } else {
                sweetAlert("错误", "添加失败!", "error");
            }
        }, "json").fail(function (res) {
            sweetAlert('数据提交失败 请刷新重试 ');
            console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
        })
    });

    //数据权限
    $(document).on("click" , "[name=th-radio] + .layui-form-radio" , function () {
        // $("[name=th-radio] + .layui-form-radio").click(function () {
        let allValue = $(this).prev().val() ;
        $("#permissionData tr td .permissionDataLevel[value="+allValue+"] + .layui-form-radio ").each(function () {
            if ( $(this).closest("tr").find("td:nth-child(1) .layui-form-checked").length > 0 ) {
                $(this).click();
            }
        });
    });


    //选择权限|用户
    $(document).on('click', '.select-tree-btn', function () {
        var href = $(this).attr("data-href");
        layer.ready(function () {
            layerIndex = layer.open({
                type: 2,
                title: false,
                maxmin: false,
                area: ['700px', '450px'],
                content: href
            });
        });
    });
});
