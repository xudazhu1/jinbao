
//JQ正则选择器扩展
jQuery.expr[':'].regex = function(elem, index, match) {
    // noinspection ES6ConvertVarToLetConst
    var matchParams = match[3].split(','),
        validLabels = /^(data|css):/,
        attr = {
            method: matchParams[0].match(validLabels) ?
                matchParams[0].split(':')[0] : 'attr',
            property: matchParams.shift().replace(validLabels,'')
        },
        regexFlags = 'ig',
        regex = new RegExp(matchParams.join('').replace(/^\s+|\s+$/g,''), regexFlags);
    return regex.test(jQuery(elem)[attr.method](attr.property));
} ;

//创建通知的js  一般使用在XX的添加 | 编辑页
import("/js/xudazhu.js");
$(function () {




    matchFlowNode();

    $(document).on("change" , "[name]" , function () {
    // $("[name]").change(function () {
        if (!$.isEmptyObject(flowNodeBean)) {
            createObjectUsers();
        }
    });
    $(document).on("blur" , "[name]" , function () {
    // $("[name]").change(function () {
        if (!$.isEmptyObject(flowNodeBean)) {
            createObjectUsers();
        }
    });

    layui.use(['element', 'layer', 'form'], function () {
        form = layui.form;
        form.on('select', function () {
            setTimeout(function () {
                createObjectUsers();
            }, 100 );
        });
        // });
    });

        if ( getParamForUrl("fromNotice") === "true" ) {
           setTimeout(function () {
               // showDataTemp();
           } , "300" );
        }


    //3 选择通知人 代办人
    $(document).on('focus', '.select-tree-btn', function () {
        let href = $(this).attr("data-href");
        let type = $(this).attr("data-type");
        thisTree = $(this);
        layer.ready(function () {
            layerIndex = layer.open({
                type: 2,
                title: "添加" + type ,
                maxmin: false,
                area: ['700px', '450px'],
                content: href
            });
        });
    });

    // 4 一键清空通知人或代办人
    $(document).on("click", ".all-remove", function () {
        $(this).parent(".user-div").children().remove();
    });

    // 5 通知人或代办人删除事件
    $(document).on("click", ".user-div i", function () {
        $(this).parent("button").next("input").remove();
        $(this).parent("button").remove();
    });



});
let form;

//方法1 用户树
// noinspection JSUnusedLocalSymbols
function userTreeSelected(ids) {
    $.get("/user", {"$D.userId": ids}, function (data) {
        if (typeof(getSelectedUser) === "function" ) {
            getSelectedUser(data);
        }
        // 经营主负责人 部门负责人
        if(thisTree.hasClass("head")){
            thisTree.val(data.content[0]["userName"])
        }else { // 通知人 待办人
            let choose_wait = thisTree.closest(".choose-parent").find(".choose-wait");
            choose_wait.children(":not(.not-remove-notice)").remove();
            $(data.content).each(function (index, user) {
                choose_wait.append(
                    '<button class="layui-btn layui-btn-sm layui-btn-primary" type="button">' + user.userName + ''
                    + '<i class="layui-icon layui-icon-close"></i>'
                    + '</button>'
                    + '<input type="hidden" class="notice-users-id" data-name="userBeans[' + index + '].userId" value="' + user.userId + '">'
                )
            })
        }
        createObjectUsers();

    }, "json").fail(function (res) {
        layer.msg("数据获取失败 请刷新重试",{icon:5});
        console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据获取失败 请刷新重试");
    });
}

//后台需要的数据
//拿到所选择的本页节点
// String thisFlowSubId = request.getParameter("thisFlowNodeId");   √
// //本待办Id
// String thisFlowNoticeMiddleId = request.getParameter("thisFlowNoticeId"); √
// //被通知人Id []
// String[] noticeObjectUserIds = request.getParameterValues("noticeObjectUserIds");
// //下节点封装数据 []
// String[] nextToDoData = request.getParameterValues("nextToDoData");


//思路
//进入页面后 根据权限标识符向后台发请求拿到匹配上的待办或者节点数据 可能有多条  多条的话由用户选择是操作的哪个节点

//如果拿到的是节点数据 ( flowNodeBeans.length > 0 && FlowNoticeMiddleBeans.length === 0 )
// 如果由多条让用户选一条 flowSubTemp = 用户选择的

//如果拿到的是待办表数据 ( flowNodeBeans.length === 0 && FlowNoticeMiddleBeans.length >= 0 )
// 如果由多条让用户选一条 (选择 : FlowNoticeMiddleBean.nextFlowSubBean)
// 然后flowSubTemp = 用户选择的待办数据的流程 FlowNoticeMiddleBean.nextFlowSubBean

//拿到flowSubTemp的下级流程的集合 , 然后拿到每个下级所对应的自定义条件customizeNotificationConditionsBeans 遍历这个条件集合

// $("[name='"+ 条件key +"']").change(function() {
//       if ( $(this).val() === 条件value ) {
//                这个下级节点的代办人 就等于 这个 条件bean里的通知(待办)人  userBean
// });

let flowNodeBean;
let flowNodeBeans;
let tagId;
let flowNotice;

//匹配当前页的流程节点
function matchFlowNode() {
    $.get("/flow_node/match", function (data) {
        flowNodeBeans = data;
        //如果只有一个 直接给节点赋值
        try {
            let thisFlowNodeId = $("[name='thisFlowNodeId']");
            if (data.length === 1) {
                flowNodeBean = data[0];
                matchTodo();
                thisFlowNodeId.empty().append("<option value='" + data[0]["flowNodeId"] + "'>" + data[0]["flowNodeName"] + "</option>");
                // $(".flow-select").hide();
            } else {
                //铺出节点让用户选
                thisFlowNodeId.append("<option></option>");
                $(data).each(function (index, flowNode) {
                    thisFlowNodeId.append("<option title='" + flowNode["flowNodeDescribe"] + "' value='" + flowNode["flowNodeId"] + "'>" + flowNode["flowNodeName"] + "</option>");
                })
            }
            if ( renderFix === undefined ) {
                jQuery.getScript("/js/xudazhu.js").done(renderFix("thisFlowNodeId") );
            } else {
                renderFix("thisFlowNodeId");
            }
            setTimeout(function () {
                form.on('select(thisFlowNodeId)', function (data) {
                    $(flowNodeBeans).each(function (index, flowNode) {
                        if (flowNode["flowNodeId"] === parseInt(data)) {
                            flowNodeBean = flowNode;
                            matchTodo();
                        }
                    });
                });
            } , 500 );



            thisFlowNodeId.change(function () {
                let selectTemp = $(this);
                $(flowNodeBeans).each(function (index, flowNode) {
                    if (flowNode["flowNodeId"] === parseInt(selectTemp.val())) {
                        flowNodeBean = flowNode;
                        matchTodo();
                    }
                });
            });
        } catch (e) {
            console.log("匹配流程出错");
            console.log(e.name + ": " + e.message);
        }


    }, "json").fail(function (data) {

    }, "json");

}


//选择完节点 铺出下级节点 并且对自定义条件进行事件绑定及判断
function createObjectUsers()   {
    try {
        let todoList = $(".todo-list");
        todoList.empty();
        // 生成待办
        $(flowNodeBean["flowNodeMiddleNextBeans"]).each(function (index, flowNodeMiddleNextBean) {
            let flowNodeName = flowNodeMiddleNextBean["flowNodeNextBean"]["flowNodeName"];
            let flowNodeId = flowNodeMiddleNextBean["flowNodeMiddleId"];

            todoList.append(
                '<div data-next-flow-id="' + flowNodeId + '"  class="layui-col-sm4 choose-parent flow-todo">\n' +
                '    <fieldset class="layui-elem-field">\n' +
                '        <legend>\n' +
                '            <button class="layui-btn layui-btn-xs layui-btn-normal select-tree-btn" data-type="待办" data-href="../../set/user/user-select.html?" type="button" >' + flowNodeName + '</button>\n' +
                '        </legend>\n' +
                //   todoUsers.toString() +
                '    </fieldset>\n' +
                '</div>'
            );
            let TempDiv = $("[data-next-flow-id='"+flowNodeId+"'] fieldset");
            TempDiv.append("<div class='layui-field-box default-user-div'></div>");
            // noinspection JSValidateTypes
            let todoUsers = TempDiv.children(".default-user-div");
            //根据条件生成被通知人
            $(flowNodeMiddleNextBean["customizeNotificationConditionsBeans"]).each(function (index, customizeNotificationConditionsBean) {
                let nameTemp = customizeNotificationConditionsBean["customizeNotificationConditionsName"];
                let valueTemp = customizeNotificationConditionsBean["customizeNotificationConditionsValue"];
                if ($("[name='" + nameTemp + "']").val() === valueTemp || $("[data-notice-path='" + nameTemp + "'] option:selected").text() === valueTemp) {
                    todoUsers.append(
                        '<div class="default-wait todo-user-id" data-todo-user-id="' + customizeNotificationConditionsBean["userBean"]["userId"] + '">\n' +
                        '    <button class="layui-btn layui-btn-sm layui-btn-primary" type="button">' + customizeNotificationConditionsBean["userBean"]["userName"] + '</button>\n' +
                        '</div>\n');
                }
            });
            todoUsers.append('<div class="choose-wait user-div"> </div>\n');
        });

        //生成通知
        //根据条件生成被通知人
        let notice_users = $(".notice-users");
        // noinspection JSValidateTypes
        notice_users.children(".not-remove-notice").remove();
        $(flowNodeBean["customizeNotificationConditionsBeans"]).each(function (index, customizeNotificationConditionsBean) {
            let nameTemp = customizeNotificationConditionsBean["customizeNotificationConditionsName"];
            //有数组 使用正则来匹配
            if ( nameTemp.indexOf("[") !== -1  ) {
                // let regex = "\\\\[[\\\\d|n]+\\\\]";
                // nameTemp =  nameTemp.replace( /\[[\d|n]+\]/g , regex )  ;
                let valueTemp = customizeNotificationConditionsBean["customizeNotificationConditionsValue"];
                // console.log($("[name]:regex(name," + nameTemp.replace( /\[[\d|n]+\]/g , "\\[[\\d|n]+\\]" ) + ")").val() + " == > " + valueTemp );
                // console.log(nameTemp + " ==> " + valueTemp );
                if ($("[name]:regex(name," + nameTemp.replace( /\[[\d|n]+\]/g , "\\[[\\d|n]+\\]" ) + ")").val() === valueTemp ) {
                    notice_users.append(
                        '<button class="layui-btn layui-btn-sm layui-btn-primary not-remove-notice " type="button">'+customizeNotificationConditionsBean["userBean"]["userName"]+'</button>' +
                        '<input type="hidden" class="notice-users-id not-remove-notice " data-name="userBeans[0].userId" value="'+customizeNotificationConditionsBean["userBean"]["userId"]+'">'
                    );
                }
            } else {
                //没有
                let valueTemp = customizeNotificationConditionsBean["customizeNotificationConditionsValue"];
                if ($("[name='" + nameTemp + "']").val() === valueTemp || $("[data-notice-path='" + nameTemp + "'] option:selected").text() === valueTemp) {
                    notice_users.append(
                        '<button class="layui-btn layui-btn-sm layui-btn-primary not-remove-notice " type="button">'+customizeNotificationConditionsBean["userBean"]["userName"]+'</button>' +
                        '<input type="hidden" class="notice-users-id not-remove-notice " data-name="userBeans[0].userId" value="'+customizeNotificationConditionsBean["userBean"]["userId"]+'">'
                    );
                }
            }
        });


        //生成通知待办数据往form里添加
        let formTemp = $("form");
        formTemp.find(".notice-elements").remove();
        //本节点Id
        formTemp.append("<input type='hidden' class='notice-elements' name='thisFlowNodeId' value='"+flowNodeBean["flowNodeId"]+"' />");
        //本待办Id
        if ( ! $.isEmptyObject(flowNotice) ) {
            formTemp.append("<input type='hidden' class='notice-elements' name='thisFlowNoticeId'  value='"+flowNotice["flowNoticeId"]+"' />");
        }
        // 被通知人Ids
        $(".notice-users-id").each(function (index, elem) {
            formTemp.append("<input type='hidden' class='notice-elements' name='noticeObjectUserIds'  value='"+elem.value+"' />");
        });
        //待办id+代办人id  "走向Id$待办人Id&待办人Id&待办人Id" nextToDoData
        $("[data-next-flow-id]").each(function (index , nextFlowTemp) {
            //待办id
            let flowTodoId = $(nextFlowTemp).attr("data-next-flow-id");
            //代办人Id
            let todoUserIds = [];
            $(nextFlowTemp).find("[data-todo-user-id]").each(function (index, userId) {
                todoUserIds.push( $(userId).attr("data-todo-user-id") );
            });
            if (! $.isEmptyObject(todoUserIds) ) {
                let nextToDoData = flowTodoId + "$" + (todoUserIds.toString().replace(/,/g , "&") );
                formTemp.append("<input type='hidden' class='notice-elements' name='nextToDoData'  value='"+nextToDoData+"' />");
            }
        });
    } catch (e) {
        // console.log("匹配流程出错");
        // console.log(e.name + ": " + e.message);
    }

}

//根据用户选择的节点 找到flowNodeMiddleFlowNodeNextTagPath 找不到就是没有上级节点 也就是没有待办
function matchTodo() {
    createObjectUsers();

    if ($.isEmptyObject(flowNodeBean["flowNodeMiddleBeforeBeans"])) {
        return false;
    }

    let todoId = getParamForUrl("todoId");
    if ( todoId !== null ) {
        tagId = todoId;
    } else {
        let tagIdElem = $("[name='" + flowNodeBean["flowNodeMiddleBeforeBeans"][0]["flowNodeMiddleFlowNodeNextTagPath"] + "']");
        tagId = tagIdElem.val();
    }
    //找到待办
    if ( ! $.isEmptyObject(tagId))
    $.get("/flow_node/match_todo", {tagId: tagId}, function (data) {
        if (data.length > 0) {
            flowNotice = data[0];
        }
    }, "json");

}

