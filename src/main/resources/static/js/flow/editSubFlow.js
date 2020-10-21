function  treeSelected( ids){
    $.get("/permission" , {id : ids} , function (data) {
        var permission = data.content[0];
        $("#permissionId").val(permission["permissionId"]);
        $("#permissionName").val(permission["permissionName"]);
    } , "json");
}


let job_ids;

function showJobs(jobBeans){
    job_ids = [];
    var tBodyTemp = $("#jobs");
    tBodyTemp.empty();
    $(jobBeans).each(function (index, job) {
        job_ids.push(job["jobId"]);
        tBodyTemp.append('<tr>\n' +
            '    <td>\n' +
            '        <input type="checkbox" lay-skin="primary">\n' +
            '    </td>\n' +
            '    <td>' +
            '<span>'+job["jobId"]+'</span>' +
            '<input type="hidden" class="beans-id" name="jobBeans['+index+'].jobId" value="'+job["jobId"]+'">' +
            '</td>\n' +
            '    <td>'+job["departmentBean"]["companyBean"]["companyName"]+'</td>\n' +
            '    <td>'+job["departmentBean"]["departmentName"]+'</td>\n' +
            '    <td>'+job["jobName"]+'</td>\n' +
            '    <td>\n' +
            '        <button type="button" class="layui-btn layui-btn-xs layui-btn-danger delete-btn">删除</button>\n' +
            '    </td>\n' +
            '</tr>');
    });
}

let role_ids;

function showRoles(roleBeans) {
    role_ids = [];
    let tBodyTemp = $("#roles");
    tBodyTemp.empty();
    $(roleBeans).each(function (index , role) {
        role_ids.push(role.roleId);
        tBodyTemp.append('<tr>\n' +
            '    <td>\n' +
            '        <input title="" type="checkbox" lay-skin="primary">\n' +
            '    </td>\n' +
            '    <td>' +
            '    <span>'+role["roleId"]+'</span>' +
            '    <input type="hidden" class="beans-id" name="roleBeans['+index+'].roleId" value="'+role["roleId"]+'">' +
            '    </td>\n' +
            '    <td>'+role.roleName+'</td>\n' +
            '    <td>'+role.roleDescribe+'</td>\n' +
            '    <td>\n' +
            '        <button type="button"\n' +
            '                class="layui-btn layui-btn-xs layui-btn-danger delete-btn">删除\n' +
            '        </button>\n' +
            '    </td>\n' +
            '</tr>');
    });
}

function  roleTreeSelected( ids){
    $.get("/role" , {"$D.id": ids} , function (data) {
        showRoles(data.content);
        //重新渲染\
        layui.form.render('checkbox');

    } , "json").fail(function (res) {
        sweetAlert('数据获取失败 请刷新重试 ');
        console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据获取失败 请刷新重试");
    });
}
function  jobTreeSelected( ids){
    $.get("/job" , {"$D.id": ids} , function (data) {
        showJobs(data.content);
        //重新渲染\
        layui.form.render('checkbox');
    } , "json").fail(function (res) {
        sweetAlert('数据获取失败 请刷新重试 ');
        console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据获取失败 请刷新重试");
    });
}

role_ids = [];
job_ids = [];

$(function () {

    $("[name='flowBean.flowId']").val(getParamForUrl("flowId"));
    //铺点数据
    if ( getParamForUrl("id") !== null  ) {
        $.get("flow_sub" , {id :getParamForUrl("id") } , function (data) {
            for (const key in data.content[0]) {//遍历json对象的每个key/value对
                if (data.content[0].hasOwnProperty(key)) {
                    let temp = $("[name='" + key + "']");
                    temp.val(data.content[0][key]);
                }
            }
            showJobs(data["content"][0]["jobBeans"]);
            showRoles(data["content"][0]["roleBeans"]);
        } , "json").fail(function (res) {
            alert('数据获取失败 请刷新重试');
            console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
        });
    }
    //1 选择权限|用户
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

    //2 点击确认提交按钮
    $(document).on('click', '.confirm-btn', function () {
        var check = true;
        $(".need-input").each(function () {
            var tipsInfo = $(this).parent().prev().text();
            var tipsInfo2 = tipsInfo.substring(0,tipsInfo.length-1);
            if($(this).css("display") === "none") {
                return
            }
            if ($(this).val().replace(/(^\s+)|(\s+$)/g, "") === "") {
                layer.tips( tipsInfo2 + '不能为空', this, {
                    tips: [1, '#3595CC'],
                    time: 4000
                });
                return check = false;
            }
        });
        if ( ! check ) {
            return false;
        }

        let method = getParamForUrl("id") === null ? "POST" : "PUT";
        let itemData = $.param({"_method": method}) + "&" + $("#inputForm").serialize();
        $.post("/flow_sub", itemData, function (data) {
            if (data) {
                layer.msg('添加成功 2秒后自动关闭', {
                    time: 2000 //2秒关闭（如果不配置，默认是3秒）
                } ,  function () {
                    window.location.reload();
                });
                window.parent.showStructure();
            } else {
                layer.msg('添加失败');
            }
        }, "json").fail(function (res) {
            layer.msg('数据提交失败 请刷新重试 ');
            console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
        }, "json");
    });
})