var thisTree;
var isName = false;
$(function () {
    // 0-1 项目编号规则生成
    var projectNum;
    var type = null; //经营类型
    var location = null; //地点
    var company = null; //收益单位
    let isDdClick = false;
    layui.use(['element', 'laydate', 'layer', 'form'], function () {
        var element = layui.element;
        var laydate = layui.laydate;
        laydate.render({
            type: 'datetime',
            elem: '#test1' //指定元素
        });
        var layer = layui.layer,
            $ = layui.jquery,
            form = layui.form;
        layui.form.render('select');
        // 0-1-2 经营类型 编号
        setTimeout(function () {
            form.on('select(projectManagementType)', function (data) {
                if(!isDdClick){
                    return;
                }
                type = Mtils.utils.makePy(data.value, true);
                if (type === 'ZY') {
                    $(".implementImplementHead").addClass("need-input")
                    implementAddHead($(".managementMainHead"),'经营主负责人','自营',true);
                } else {
                    $(".implementImplementHead").removeClass("need-input");
                    implementAddHead($(".managementMainHead"),'经营主负责人','合作',true);
                }
                projectNum = type + company + location + '-';
                if(type !== null && company !== null && location !== null){
                    $(".projectNum").val(projectNum);
                    $(".tempNum").val(projectNum+"(编号提交后生成)");
                }

            });
        }, 100);


        // 0-1-3 项目地点 编号
        setTimeout(function () {
            form.on('select(projectLocationName)', function () {
                if(!isDdClick){
                    return;
                }
                location = $(".projectLocationName option:selected").attr("data-parent");
                location = Mtils.utils.makePy(location, true);
                projectNum = type + company + location + '-';
                if(type !== null && company !== null && location !== null){
                    $(".projectNum").val(projectNum);
                    $(".tempNum").val(projectNum+"(编号提交后生成)");
                }
            });
        }, 100);


        // 0-1-4 收益单位 编号
        setTimeout(function () {
            form.on('select(earningsCompanyName)', function () {
                company = $(".earningsCompanyName option:selected").attr("data-num");
                projectNum = type + company + location + '-';
                if(type !== null && company !== null && location !== null){
                    $(".projectNum").val(projectNum);
                    $(".tempNum").val(projectNum+"(编号提交后生成)");
                }
            });
        }, 100);
        //点击实施部的时候 需要根据test 铺 对应的实施负责人
        setTimeout(function () {
            form.on('select(implementName)', function (data) {
                let implementName = $(data.elem).find("option:selected").text();
                console.log(implementName);
                implementAddHead($(data.elem).parent().parent().parent().find(".implementImplementHead"),'实施负责人',implementName,true);
            });
        }, 100);

        // 0-2 当选择报价编号时,报价名称就对自动填上去
        setTimeout(function () {
            form.on('select(quoteNum)', function () {
                var quoteName = $(".quoteNum option:selected").attr("data-name");
                if (quoteName) {
                    $(".quoteName").val(quoteName)
                } else {
                    $(".quoteName").val("");
                }

            });
        }, 100);

        setTimeout(function () {
            form.on('select(quoteNum)', function () {
                var quoteName = $(".quoteNum option:selected").attr("data-name");
                if (quoteName) {
                    $(".quoteName").val(quoteName)
                } else {
                    $(".quoteName").val("");
                }
            });
        }, 100);
    });


    // 1 添加实施部
    $(document).on("click", ".add-implement-btn", function () {
        // 添加实施部 限制
        var length = $(".add-implement-div").children(".layui-form-item").length;
        if (length > 3) {
            layer.msg("最多有四个实施部");
            return false;
        }
        var idTemp = Math.ceil(Math.random() * 10000);
        $(this).parent().children(".add-implement-div").append(
            '<div class="layui-row layui-col-space5 layui-form-item">'
            + '<div class="layui-col-sm6">'
            + '<label class="layui-form-label" style="padding: 9px 0">*实施部名称：</label>'
            + '<div class="layui-input-block">'
            + '<select id="select-' + idTemp + '" name="implementBeans[' + length + '].departmentBean.departmentId" class="need-input implementName" lay-filter="implementName">'
            + '<option></option>'
            + '</select>'
            + '</div>'
            + '</div>'
            + '<div class="layui-col-sm6">'
            + '<label class="layui-form-label" style="padding: 9px 0">部门负责人：</label>'
            + '<div class="layui-input-block">'
            + '<select id="implementHead-' + idTemp + '" name="implementBeans[' + length + '].implementImplementHead" title="" class="userName implementImplementHead" lay-filter="userName" lay-search></select>'
            // + '<input type="text" name="implementBeans['+length+'].implementImplementHead"  placeholder="" autocomplete="off" class="layui-input select-tree-btn head select-tree-btn" data-href="../../set/user/user-select.html?num=1" data-type="部门负责">'
            + '</div>'
            + '<i class="layui-icon layui-icon-delete" title="删除实施部"></i>'
            + '</div>'
            + '</div>'
        );
        // 调用铺实施部的方法
        implement($("#select-" + idTemp));
        var form = layui.form;
        form.render();
    });

    // 2 删除实施部
    $(document).on("click", ".add-implement-div .layui-icon-delete", function () {
        let id = $(this).attr("data-id");
        if(id === undefined){
           return $(this).parent().parent().remove();
        }
        layer.confirm('确认删除吗？', {
            btn: ['确认','取消'] //按钮
        }, function(){

            if(id === undefined){
                $(this).parent().parent(".layui-form-item").remove();
            }else {
                $.post("/implement",{"_method":"delete",id},function (data) {
                    if (data){
                        layer.msg('删除成功 2秒后自动刷新', {
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        } ,  function () {
                            window.location.reload();
                            window.parent.flush();
                        });
                    }else {
                        layer.msg('删除失败');
                    }
                },"json").fail(function (res) {
                    layer.msg(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试", {icon: 5});
                }, "json");
            }
        });


    });

    // 3-1 铺地点数据
    $.get("/project_location", {}, function (data) {
        $(data).each(function (index, item) {
            var projectLocationName = $(".projectLocationName");
            let itemTemp = item;
            while ((!$.isEmptyObject(itemTemp["projectLocationParent"])) && (itemTemp["projectLocationParent"] !== "")) {
                itemTemp = itemTemp["projectLocationParent"];
            }
            projectLocationName.append(
                '<option value="' + item["projectLocationId"] + '" data-parent="' + itemTemp["projectLocationName"] + '">' + item.projectLocationName + '</option>'
            )
        });
        layui.use(['form'], function () {
            var form = layui.form;
            layui.form.render('select');
        });

    }, "json");

    // 3-2 铺收益单位
    $.get("/earnings_company", {}, function (data) {
        $(data).each(function (index, item) {
            var earningsCompanyName = $(".earningsCompanyName");
            let selected = "";
            if (item["earningsCompanyStatus"] === true) {
                selected = "selected";
                company = item["earningsCompanyTag"];
            }
            earningsCompanyName.append(
                '<option selected="' + selected + '" value="' + item["earningsCompanyId"] + '" data-num="' + item["earningsCompanyTag"] + '">' + item["earningsCompanyName"] + '</option>'
            )

        });

        layui.use(['form'], function () {
            var form = layui.form;
            layui.form.render('select');
        });

    }, "json");

    // 3-3 铺报价数据
    $.get("/quote/select_box", {}, function (data) {
        $(data).each(function (index, item) {
            var quoteNum = $(".quoteNum");
            if (item["projectId"] === "") {
                quoteNum.append(
                    '<option value="' + item["quoteId"] + '" data-name="' + item["quoteName"] + '">' + item["quoteNum"] + '</option>'
                )
            }
            if (item["projectId"] == projectId) {//三个等于号 以及 一个等于号 不成立
                quoteNum.append(
                    '<option value="' + item["quoteId"] + '" data-name="' + item["quoteName"] + '">' + item["quoteNum"] + '</option>'
                )
            }
        });
        layui.use(['form'], function () {
            var form = layui.form;
            layui.form.render('select');
        });
    }, "json");

    // 3-4 铺实施部数据
    implement($(".implementName"));

    //
    let projectId = getParamForUrl("id");
    // 5如果带有id,则开始铺项目数据
    if (projectId !== null) {
        // showDataTemp();
        // 铺实施部数据
        $.get("/project/project_id", {id: projectId}, function (data) {
            console.log(data);
            showData4Object(data);
            // 如果经营主负责人的信息没有铺上,则调用输经营主负责人的方法
            let managementMainHead = $(".managementMainHead");
            let ajax1;
            if (data["projectManagementType"] === '自营') {
                $("[name='implementBeans[" + 1 + "].implementImplementHead']").addClass("need-input");
                ajax1 = implementAddHead(managementMainHead, '经营主负责人','自营',true,"<option value='"+data["managementBean"]["managementMainHead"]+"'>"+data["managementBean"]["managementMainHead"]+"</option>")
            } else {
                $("[name='implementBeans[" + 1 + "].implementImplementHead']").removeClass("need-input");
                ajax1 = implementAddHead(managementMainHead, '经营主负责人','合作',true,"<option value='"+data["managementBean"]["managementMainHead"]+"'>"+data["managementBean"]["managementMainHead"]+"</option>")
            }

            let add_implement_div = $(".add-implement-div");
            add_implement_div.empty();
            $(data["implementBeans"]).each(function (index, implementBean) {
                let idTemp = Math.ceil(Math.random() * 10000);
                add_implement_div.append(
                    '<div class="layui-row layui-col-space5 layui-form-item">'
                    + '<div class="layui-col-sm6">'
                    + '<label class="layui-form-label" style="padding: 9px 0">*实施部名称：</label>'
                    + '<div class="layui-input-block">'
                    + '<select id="select-' + idTemp + '"  name="implementBeans[' + index + '].departmentBean.departmentId" class="need-input implementName" lay-filter="implementName">'
                    + '<option></option>'
                    + '</select>'
                    + '</div>'
                    + '</div>'
                    + '<div class="layui-col-sm6">'
                    + '<label class="layui-form-label" style="padding: 9px 0">部门负责人：</label>'
                    + '<div class="layui-input-block">'
                    // + '<input type="text" value="'+implementBeans["implementImplementHead"]+'"    autocomplete="off" class="layui-input  head select-tree-btn" data-href="../../set/user/user-select.html?num=1" data-type="部门负责人">'
                    + '<select id="implementHead-' + idTemp + '" title="" name="implementBeans[' + index + '].implementImplementHead"  class="userName implementImplementHead" lay-filter="implementImplementHead" lay-search></select>'
                    + '<input type="hidden" value="' + implementBean["implementId"] + '" name="implementBeans[' + index + '].implementId"    autocomplete="off" class="layui-input">'
                    + '</div>'
                    + '<i class="layui-icon layui-icon-delete delete-btn" data-id="'+implementBean["implementId"]+'" title="删除实施部"></i>'
                    + '</div>'
                    + '</div>'
                );
                // 展示实施部
                let implementDepartmentId = implementBean["departmentBean"]["departmentId"];//实施部id

                // 调用铺实施部的方法
                let selectTemp = $("#select-" + idTemp);
                let implementHeadTemp = $("#implementHead-" + idTemp);

                implement(selectTemp, implementDepartmentId);

                let ajax2 = implementAddHead(implementHeadTemp,"实施负责人", implementBean["departmentBean"]["departmentName"],true,"<option value='"+implementBean["implementImplementHead"]+"'>"+implementBean["implementImplementHead"]+"</option>");
                $.when(ajax2).done(function () {
                    implementHeadTemp.val(implementBean["implementImplementHead"]);
                    renderFix("implementImplementHead");
                });

                setTimeout(function () {
                    info();
                }, 100);
            });

            $.when(ajax1).done(function () {
                showProjectData(data);
                isDdClick = true;
                $("[name='projectApprovalTime']").val(data["projectCreateTime"])
            });
        });

    }else {
        isDdClick = true;
        implementAddHead($(".managementMainHead"),'经营主负责人','自营',true);
    }



    // 7提交
    let status = false;
    $(document).on("click", ".confirm-btn", function () {
        // 表单校验 引用 xudazhu.js
        if (!formChecking() || status) {
            return false;
        }
        status = true;
        // // 判断项目名称
        // if (getParamForUrl("id") === null) {
        //     if (!isName) {
        //         layer.msg("项目名称已存在,请勿重复录入", {icon: 5});
        //         return false;
        //     }
        // }
        let method = getParamForUrl("id") === null ? "POST" : "PUT";
        let itemData = $.param({
            "_method": method,
            "createUserBean.userId": window.top.userTemp.userId,
            "projectCreateTime": formatDate(new Date(), 8),
            "createJobBean.jobId": window.top.userTemp["jobBean"]["jobId"]
        }) + "&" + $("#inputForm").serialize();

        $.post("/project", itemData, function (data) {
            if(getParamForUrl("id") === null){
                layer.alert(data["projectNum"], {
                    title:"项目编号",
                    icon: 1,
                }, function () {
                    window.location.reload();
                    window.parent.flush();
                    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                    parent.layer.close(index); //再执行关闭
                });
            }else {
                layer.alert("编辑成功", {
                    icon: 1,
                }, function () {
                    window.location.reload();
                    window.parent.flush();
                    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                    parent.layer.close(index); //再执行关闭
                });
            }

        }, "json").fail(function (res) {
            layer.msg(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试", {icon: 5});
            status = false;
        }, "json");
    });
    // 铺经营主负责人

});

// 用来添加页面 自定义 模块 用户
function implementAddHead(dom, param1,param2,notId,showData) {
    let ajax1 = $.get("/user_list_box", {"moduleListBean.moduleListType":param1,"moduleListBean.moduleListDetail":param2},function (data) {
        dom.empty();
        dom.append('<option></option>');
        let isExist = false;
        $(data).each(function (index, user) {
            let userName = user["userName"];
            dom.append('<option value="'+ (notId === true ? userName : user["userId"]) +'">' + userName + '</option>')

            if(showData != null && showData.indexOf(userName) !== -1){
                isExist = true;
            }
        });
        if(showData != null && !isExist){
            dom.append(showData);
        }
        renderFix("userName");


        setTimeout(function () {
            renderFix("implementImplementHead");
        },100);




    },"json");
    return ajax1;
}
// 方法1 铺实施部数据
let implementInfo = null;

function implement(selectDom, value) {
    if (implementInfo === null) {
        $.get("/department/implement_department", {}, showImplement)
    } else {
        showImplement(implementInfo);
    }
    function showImplement(data) {
        implementInfo = data;
        selectDom.empty();
        selectDom.append(
            '<option></option>'
        );
        $(data).each(function (index, item) {
            if (value === item["departmentId"]) {
                selectDom.append('<option selected value="' + item["departmentId"] + '" >' + item["departmentName"] + '</option>');
            } else {
                selectDom.append('<option  value="' + item["departmentId"] + '" >' + item["departmentName"] + '</option>');
            }

        });

        renderFix("implementName");
    }
}

// 方法2 将项目编号,项目名称,项目地点,经营类型,收益单位变成可读
function info() {
    $(".project-detail .layui-input").each(function (index, detail) {
        $(detail).closest(".project-detail").prev("label").css("color", "#47a3dc");
        let inputValue = $(detail).val();
        $(detail).after(
            '<span class="newSpan">' + inputValue + '</span>'
        );
        $(detail).siblings("i").remove();
        $(detail).remove();
    });
    $(".project-detail select").each(function (index, detail) {
        $(detail).closest(".project-detail").prev("label").css("color", "#47a3dc");
        let inputValue = $(detail).find("option:selected").text();
        $(detail).append(
            '<span class="newSpan">' + inputValue + '</span>'
        );
        $(detail).siblings("i").remove();
        $(detail).remove();
    })
}
function showProjectData(object ) {
    if ( ! $.isEmptyObject(object) ) {
        $("[lay-filter]").each(function () {
            let nameTemp = $(this).prop("name");
            if(nameTemp === "implementBeans[0].departmentBean.departmentId"){
                return ture;
            }
            if ( nameTemp === "undefined" || nameTemp === "" ) nameTemp = $(this).attr("data-notice-path");
            let valueTemp = getValueByName( object , nameTemp );
            if ( valueTemp !== undefined ) {
                valueTemp = "" + valueTemp;
            }

            $(this).val(valueTemp);
            try {
                $(this).next(".layui-form-select").find("dd").each(function (index , dd) {
                    if ( $(dd).attr("lay-value") === valueTemp ) {
                        return $(dd).click();
                    }
                });
            } catch (e) {
            }
        });


    }

}
