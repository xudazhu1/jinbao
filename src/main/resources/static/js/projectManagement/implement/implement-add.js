var thisTree;
let implementId = getParamForUrl("id");

function show() {
    if(implementId != null){

        var ajax6 =$.get("/department/implement_department",{},function (data) {
            var selectDom = $(".departmentId");
            selectDom.empty();
            selectDom.append(
                '<option></option>'
            );
            $(data).each(function (index, item) {
                selectDom.append('<option  value="' + item["departmentId"] + '" >' + item["departmentName"] + '</option>');
            });
            layui.use(['form'], function () {
                var form = layui.form;
                layui.form.render('select');
            });
        });
        $.when(ajax6).done(function () {
            //所做操作
            $.get("/implement/implement_id",{"id":implementId},function (data) {
                showData4Object(data);

                var statusStr = "";
                if(data["projectStatusRecordBeanList"].length > 0){
                    statusStr += "<tbody><tr><th>操作时间</th><th>项目状态</th><th>操作人</th></tr>";
                    for(var statusRecord of data["projectStatusRecordBeanList"]){
                        statusStr += "<tr>" +
                            "                 <td >"+statusRecord["projectStatusRecordTime"]+"</td>" +
                            "                 <td >"+statusRecord["projectStatusRecordName"]+"</td>" +
                            "                 <td >"+statusRecord["projectStatusRecordOperator"]+"</td>" +
                            "         </tr>"
                    }
                    $(".statusTable").append(statusStr)
                }

                var implStr = "";
                if(data["implementRecordBeanList"].length > 0){
                    implStr += "<tbody><tr><th>操作时间</th><th>实施内容</th><th>操作人</th></tr>";
                    for(var implRecord of data["implementRecordBeanList"]){
                        implStr += "<tr>" +
                            "                 <td >"+implRecord["implementRecordTime"]+"</td>" +
                            "                 <td >"+implRecord["implementRecordContent"]+"</td>" +
                            "                 <td >"+implRecord["implementRecordOperator"]+"</td>" +
                            "         </tr>"
                    }
                    $(".implTable").append(implStr)
                }

            },"json");
        });

    }
}

$(function () {
    //查看状态历史记录
    $(".status-btn").click(function () {
        $(".statusRecord").fadeToggle();
        return false;
    });
    $(".impl-btn").click(function () {
        $(".implRecord").fadeToggle();
        return false;
    });
    $(document).on("click","html",function () {
        let temp = $(".recordDiv");
        temp.each(function () {
            if (! inner($(this))) {
                //如果鼠标不在该div里面,则收起来该div
                $(this).fadeOut();
            }
        });
    });
    // 0 layui配置
    //注意：折叠面板 依赖 element 模块，否则无法进行功能性操作
    layui.use(['element','laydate','layer','form'], function(){
        var element = layui.element;
        var laydate = layui.laydate;
        laydate.render({
            elem: '#test1' //指定元素
        });
        var layer = layui.layer,
            $ = layui.jquery,
            form = layui.form;
        layui.form.render('select');


    });

    // 1 铺项目类型 和 项目状态
    $.get("/implement/impl_attrs",{},function (data) {
        $(data).each(function (index, item) {
            $(item["projectTypeBean"]).each(function (index, projectTypeBean) {
                $(".projectType").append(
                    "<option value='"+projectTypeBean["projectTypeId"]+"'>"+projectTypeBean["projectTypeName"]+"</option>"
                )
            });

            $(item["projectStatusBean"]).each(function (index, projectStatusBean) {
                $(".projectStatus").append(
                    "<option value='"+projectStatusBean["projectStatusId"]+"'>"+projectStatusBean["projectStatusName"]+"</option>"
                )
            });

            $(item["secondPartyBean"]).each(function (index, secondPartyBean) {
                $(".secondParty").append(
                    "<option value='"+secondPartyBean["secondPartyId"]+"'>"+secondPartyBean["secondPartyName"]+"</option>"
                )
            });

            // $(item["implementDepartmentBean"]).each(function (index, implementDepartmentBean) {
            //     $(".implementDepartment").append(
            //         "<option value='"+implementDepartmentBean["implementDepartmentId"]+"'>"+implementDepartmentBean["implementDepartmentName"]+"</option>"
            //     )
            // })
        });
        layui.use(['form'], function () {
            var form = layui.form;
            layui.form.render('select');
        });
        show();
    }, "json");

    // 1 通知人或代办人删除事件
    $(document).on("click",".user-div i",function () {
        $(this).parent("button").remove();
    });

    // 2 一键清空通知人或代办人
    $(document).on("click",".all-remove",function () {
        $(this).parent(".user-div").children().remove();
    })

    //4 选择通知人 待办人
    $(document).on('click', '.select-tree-btn', function () {
        var href = $(this).attr("data-href");
        layer.ready(function () {
            layerIndex = layer.open({
                type: 2,
                title: "添加通知人",
                maxmin: false,
                area: ['700px', '450px'],
                content: href
            });
        });
    });





    //7 选择 经营主负责人 通知人 代办人
    $(document).on('focus', '.select-tree-btn', function () {
        var href = $(this).attr("data-href");
        var type = $(this).attr("data-type");
        thisTree = $(this);
        layer.ready(function () {
            layerIndex = layer.open({
                type: 2,
                title: "添加" + type + "人",
                maxmin: false,
                area: ['700px', '450px'],
                content: href
            });
        });
    });

    let status = false;
    $(document).on("click",".confirm-btn",function () {
        // 表单校验 引用 xudazhu.js
        if ( ! formChecking() || status) {
            return false;
        }
        status = true;
        let itemData = $.param({"_method": "PUT" , "createUserBean.userId" : window.top.userTemp.userId ,"createUserBean.userName" : window.top.userTemp.userName ,
            "projectCreateTime" : formatDate(new Date() , 8 ) , "createJobBean.jobId" : window.top.userTemp["jobBean"]["jobId"] }) + "&" + $("#inputForm").serialize();
        $.post("/implement" , itemData, function (data) {
            if (data) {
                layer.msg('操作成功 2秒后自动刷新', {
                    time: 2000 //2秒关闭（如果不配置，默认是3秒）
                } ,  function () {
                    window.location.reload();
                    window.parent.flush();
                });
            } else {
                layer.msg('添加失败',{icon:5});
                status = false;
            }
        }, "json").fail(function (res) {
            layer.msg( res["responseJSON"].message,{icon:5});
            console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
            status = false;
        }, "json");
    });
});

//方法2 用户树
function userTreeSelected(ids) {
    $.get("/user", {"$D.userId": ids}, function (data) {
        // 经营主负责人 部门负责人
        if(thisTree.hasClass("head")){
            thisTree.val(data.content[0]["userName"])
        }else { // 通知人 待办人
            var choose_wait = thisTree.closest(".choose-parent").find(".choose-wait");
            choose_wait.empty();
            $(data.content).each(function (index, user) {
                choose_wait.append(
                    '<button class="layui-btn layui-btn-sm layui-btn-primary" type="button">' + user.userName + ''
                    + '<i class="layui-icon layui-icon-close"></i>'
                    + '</button>'
                    + '<input type="hidden" class="beans-id" data-name="userBeans[' + index + '].userId" value="' + user.userId + '">'
                )
            })
        }

    }, "json").fail(function (res) {
        layer.msg("数据获取失败 请刷新重试",{icon:5});
        console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据获取失败 请刷新重试");
    });
}


