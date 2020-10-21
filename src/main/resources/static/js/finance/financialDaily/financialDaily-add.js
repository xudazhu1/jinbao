let id = getParamForUrl("id");
let disburse = null;
let way = getParamForUrl("way");
var state = "";
$(function () {
    // 进去是详情隐藏按钮
    if (way === "details") {
        $(".layui-btn").hide()
    }

    $("body").addClass(way);

    // 0 编辑页
    //如果有 id 铺数据
    if (id != null) {
        let confirmDiv = $(".confirm-div");
        let confirmInnerDiv = $(".confirm-inner-div");
        let auditInnerDiv = $(".audit-inner-div");
        let payInnerDiv = $(".pay-inner-div");
        let auditDiv = $(".audit-div");//审核信息
        let payDiv = $(".pay-div");//支付信息

        if (way === "confirm" || way === "return-confirm") {
            auditDiv.remove();
            payDiv.remove();
        }
        if (way === "audit" || way === "return-audit") {
            payDiv.remove();
        }
        $(".paymentStatus-1").remove();
        showApplyForDepartment($(".applyForDepartment"));
        $.get("/table_utils/info",
            {disburseId: id, "table_utils.tableName": "disburse"}, function (data) {
                var bankCardAllotBeans = data.content[0].bankCardAllotBeans;
                disburse = data.content[0];
                let paymentStatusId = disburse["paymentStatusBean"]["paymentStatusId"];//支付状态
                if(paymentStatusId == 7){
                    $(".add-implement-btn").hide();
                    $(".layui-input, .layui-textarea").css("padding-left","0");
                    $(".layui-input, .layui-select, .layui-textarea").css("border-style","none")
                    var bankCardId = $(".add-implement-div");
                    bankCardId.empty();
                    var html = "";
                    for (var i = 0; i < bankCardAllotBeans.length; i++) {
                        html +='<div class="layui-row layui-col-space5 layui-form-item">'
                        html += '<div class="layui-col-sm4 bankCard-div">'
                        html += '<label class="layui-form-label" style="width: 104px;padding: 9px 2px;">*银行卡：</label>'
                        html += '<div class="layui-input-block">'
                        html += '<p>'+ bankCardAllotBeans[i].bankCardBean.bankCardName +'</p>'
                        html += '</div>'
                        html += '</div>'
                        html += '<div class="layui-col-sm4 bankCard-div">'
                        html += '<label class="layui-form-label" style="width: 104px;padding: 9px 2px;">*支付金额：</label>'
                        html += '<div class="layui-input-block">'
                        html += '<p>'+ bankCardAllotBeans[i].bankCardAllotBankCardMoney +'</p>'
                        html += '</div>'
                        html += '</div>'
                        html += '<div class="layui-col-sm4 bankCard-div">'
                        html += '<label class="layui-form-label" style="width: 104px;padding: 9px 2px;">*支付时间：</label>'
                        html += '<div class="layui-input-block">'
                        html += '<p>' + bankCardAllotBeans[i].bankCardAllotTime +'</p>'
                        html += '</div>'
                        html += '</div>'
                        html += '</div>'
                        html += '</div>'
                    }
                    $(".add-implement-div").append(html);
                }
                showData4Object(disburse);
                console.log(way);
                console.log(paymentStatusId);
                if(way === "confirm" || way === "audit" || way === "return-audit" || way === "pay" || way === "return-pay"){
                    if(paymentStatusId == "1"){
                        $(".disburseApprovalRemark").val("");
                    }else if(paymentStatusId == "2"){
                        state = 1;
                    }else if(paymentStatusId == "3"){
                        $(".disburseApprovalRemark2").val("");
                    }else if(paymentStatusId == "4"){
                        state = 3;
                    }else if(paymentStatusId == "5"){
                        $(".disburseApprovalRemark3").val("");
                    }else if(paymentStatusId == "6"){
                        state = 5;
                    }
                }
                if (way === "confirm" || way === "audit" || way === "pay" || way === "paid" || way === "details" || way === "print") {
                    $(".show-text").each(function () {
                        let valTemp = $(this).val();
                        showText($(this), valTemp)
                    });
                    // 财务日常编号
                    let disburseNum = $(".disburseNum");
                    let disburseNumVal = disburseNum.val();
                    disburseNum.after("<p>" + disburseNumVal + "</p>");
                    disburseNum.remove();

                    // 打印
                    if (way === "print") {
                        confirmDiv.hide();//确认信息隐藏
                        auditDiv.hide();//审核信息隐藏
                        payDiv.hide();//支付信息隐藏
                        $(".sumbit-div").hide();//提交按钮隐藏
                        $(".advice-div").show();// 打印模块显示
                        $(".print-btn").show();//打印按钮显示
                    }
                    // 详情
                    if (way === "details") {
                        if (paymentStatusId <= 2) {//1待确认//2退回/待确认/3已确认
                            auditDiv.remove();
                            payDiv.remove();
                            if (paymentStatusId === 1) {//1待确认
                                confirmInnerDiv.html('<p>待确认</p>');
                            } else if (paymentStatusId === 2) {//退回/待确认
                                confirmInnerDiv.html('<p>退回/待确认</p>')
                            }
                        } else if (paymentStatusId > 2 && paymentStatusId <= 4) {
                            payDiv.remove();
                            confirmInnerDiv.html('<p>已确认</p>');
                            if (paymentStatusId === 3) {
                                auditInnerDiv.html('<p>待审核</p>');
                            } else {
                                auditInnerDiv.html('<p>退回</p>');
                            }
                        } else if (paymentStatusId >= 5) {
                            confirmInnerDiv.html('<p>已确认</p>');
                            auditInnerDiv.html('<p>审核通过</p>');
                            if (paymentStatusId === 5) {
                                payInnerDiv.html('<p>待支付</p>');
                                $(".bankCard-div").hide();//付款银行卡隐藏
                            } else if (paymentStatusId === 6) {
                                payInnerDiv.html('<p>退回</p>');
                                $(".bankCard-div").hide();//付款银行卡隐藏
                            } else {
                                payInnerDiv.html('<p>已支付</p>');
                            }
                        }
                        convertTextNum($(".disburseApprovalRemark"));
                        convertTextNum($(".disburseApprovalRemark2"));
                        convertTextNum($(".disburseApprovalRemark3"));
                    }

                }
                if (way === "add") {
                    let disburseApprovalRemark = $(".disburseApprovalRemark");
                    showText(disburseApprovalRemark, disburseApprovalRemark.val());
                    $(".approvalStatus").closest(".layui-input-block").html(
                        '<p>被退回</p>'
                        + '<input type="hidden" name="approvalStatusBean.approvalStatusId" placeholder="" value="1">'
                    );
                }

                if (way === "confirm" || way === "audit" || way === "pay" || way === "paid" || way === null) {
                    if (way === "audit" || way === "pay" || way === "paid") {
                        confirmInnerDiv.html('<p>已确认</p>');
                        if (way === "pay" || way === "paid") {
                            auditInnerDiv.html('<p>审核通过</p>');
                            convertTextNum($(".disburseApprovalRemark2"));
                            if (way === "paid") {
                                convertTextNum($(".disburseApprovalRemark3"));
                                payInnerDiv.html('<p>已支付</p>');
                                $(".submit-btn").remove();
                            }
                        }
                        convertTextNum($(".disburseApprovalRemark"));
                    }

                }
                if (way === "return-confirm" || way === "return-audit" || way === "return-pay") {
                    if (way === "return-confirm") {
                        confirmInnerDiv.html('<p>退回</p>');
                        confirmInnerDiv.append('<input type="hidden" name="paymentStatusBean.paymentStatusId" class="layui-input"  placeholder="" value="1">');
                    }
                    if (way === "return-audit") {
                        confirmInnerDiv.html('<p>已确认</p>');
                        auditInnerDiv.html('<p>退回</p>');
                        auditInnerDiv.append('<input type="hidden" name="paymentStatusBean.paymentStatusId" class="layui-input"  placeholder="" value="3">');
                    }
                    if (way === "return-pay") {// 退回待支付
                        $(".bankCard-div").remove();//付款银行卡隐藏
                        confirmInnerDiv.html('<p>已确认</p>');
                        auditInnerDiv.html('<p>审核通过</p>');
                        payInnerDiv.html('<p>退回</p>');
                        payInnerDiv.append('<input type="hidden" name="paymentStatusBean.paymentStatusId" class="layui-input"  placeholder="" value="5">');
                    }
                    convertTextNum($(".disburseApprovalRemark"));
                    convertTextNum($(".disburseApprovalRemark2"));
                    convertTextNum($(".disburseApprovalRemark3"));
                }
            }, "json");
    } else {// 没有id的话,是录入,将审核信息隐藏
        $(".audit-div").hide();
    }

    // 1 铺费用类型明细
    showDisburseDetail($("#disburse_detail"), "财务日常");

    //2 铺付款单位
    showSecondParty($("#secondParty"));

    // 3-4 铺实施部数据
    implement($(".bankCardId"));

    // 3 支出时间
    PaymentTime();
    function PaymentTime(){
        layui.use(['laydate', 'form','table'], function () {
            var laydate = layui.laydate;
            var form = layui.form;
            //  监听select下拉
            form.on('select(text)', function(data){
                state = data.value
            });
            // 年月日选择器
            laydate.render({
                elem: '#test-laydate-normal-cn',
                type: 'datetime'
            });
            lay('.test-item').each(function(){
                laydate.render({
                    elem: this
                    ,trigger: 'click'
                });
            });

        });

    }

    // 5铺收益单位
    showEarningsCompany($(".earnings_company"));

    // 6 提交
    $(document).on("click", ".submit-btn", function () {
        // ①表单校验
        // 此处校验基本信息  引用 xudazhu.js
        if (!formChecking()) {
            return false;
        }

        if(state == ""){
            state = 1;
        }

        let method = getParamForUrl("id") === null ? "POST" : "PUT";

        let itemData = $.param({"_method": method, tableName: "disburse","paymentStatusBean.paymentStatusId":state,}) + "&" + $("#inputForm").serialize();
        $.post("/finance", itemData, function (data) {
            console.log(data);
            if (data) {
                layer.msg('操作成功 2秒后自动关闭', {
                    time: 2000 //2秒关闭（如果不配置，默认是3秒）
                }, function () {
                    window.location.reload();
                    window.parent.flush();
                    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                    parent.layer.close(index); //再执行关闭
                });
            } else {
                layer.msg('添加失败', {icon: 5});
            }
        }, "json").fail(function (res) {
            //layer.msg('数据提交失败 请刷新重试', {icon: 5});
        }, "json");
    });

    // 7 收款银行账号 空格
    $("#financeShroffAccountNumber").on("keyup", function () {
        //获取当前光标的位置
        var caret = this.selectionStart;
        //获取当前的value
        var value = this.value;
        //从左边沿到坐标之间的空格数
        var sp = (value.slice(0, caret).match(/\s/g) || []).length;
        //去掉所有空格
        var nospace = value.replace(/\s/g, '');
        //重新插入空格
        var curVal = this.value = nospace.replace(/\D+/g, "").replace(/(\d{4})/g, "$1 ").trim();
        //从左边沿到原坐标之间的空格数
        var curSp = (curVal.slice(0, caret).match(/\s/g) || []).length;
        //修正光标位置
        this.selectionEnd = this.selectionStart = caret + curSp - sp;
    });


    if (id === null) {


        // 铺报销总编号
        // $.get("/number", {}, function (data) {
        //     $(".expenseAccountNum").val("CR-" + data["numberName"]);
        // }, "json");

        // 经办人
        let userName = $(".userName");
        userName.val(window.top.userTemp["userName"]);
        // 铺申请部门
        setTimeout(function () {
            showApplyForDepartment($(".applyForDepartment"), window.top.userTemp["jobBean"]["departmentBean"]["departmentName"]);
        });
    }
    renderFix("paymentStatus", function () {
        if (way === "pay") {
            $("[lay-filter='paymentStatus'] + .layui-form-select dd").click(function () {
                var status = $("[lay-filter='paymentStatus']").val();
                let bankCardId = $(".bankCardId");
                if (status === "6") {
                    $(".bankCard-div").hide();
                    bankCardId.removeClass("need-input");
                    bankCardId.attr("name", "")
                }
                if (status === "7") {
                    $(".bankCard-div").show();
                    bankCardId.addClass("need-input");
                    bankCardId.attr("name", "bankCardBean.bankCardId")
                }
            })
        }
    });

    //  添加银行卡
    $(document).on("click", ".add-implement-btn", function () {
        // 添加实施部 限制
        var length = $(".add-implement-div").children(".layui-form-item").length;
        var idTemp = Math.ceil(Math.random() * 10000);
        $(this).parent().children(".add-implement-div").append(
            '<div class="layui-row layui-col-space5 layui-form-item">'
            + '<div class="layui-col-sm4 bankCard-div">'
            + '<label class="layui-form-label" style="width: 104px;padding: 9px 2px;">*银行卡：</label>'
            + '<div class="layui-input-block">'
            + '<select id="select-' + idTemp + '" class="bankCardId " name="bankCardAllotBeans[' + length + '].bankCardBean.bankCardId" title="" lay-filter="bankCardId" autocomplete="off"  lay-search>'
            + '<option></option>'
            + '</select>'
            + '<input type="text" name="bankCardAllotBeans[' + length + '].bankCardAllotId" hidden="hidden">'
            + '</div>'
            + '</div>'
            + '<div class="layui-col-sm4 bankCard-div">'
            + '<label class="layui-form-label" style="width: 104px;padding: 9px 2px;">*支付金额：</label>'
            + '<div class="layui-input-block">'
            //+ '<select id="implementHead-' + idTemp + '" name="implementBeans[' + length + '].implementImplementHead" title="" class="userName implementImplementHead" lay-filter="userName" lay-search></select>'
             + '<input type="text" name="bankCardAllotBeans[' + length + '].bankCardAllotBankCardMoney" autocomplete="off" placeholder="请输入" class="layui-input" >'
            + '</div>'
            + '</div>'
            + '<div class="layui-col-sm4 bankCard-div">'
            + '<label class="layui-form-label" style="width: 104px;padding: 9px 2px;">*支付时间：</label>'
            + '<div class="layui-input-block">'
            //+ '<select id="implementHead-' + idTemp + '" name="implementBeans[' + length + '].implementImplementHead" title="" class="userName implementImplementHead" lay-filter="userName" lay-search></select>'
             + '<input type="text" name="bankCardAllotBeans[' + length + '].bankCardAllotTime" placeholder="点击选择时间"  class="test-item layui-input">'
            + '</div>'
            + '<i class="layui-icon layui-icon-delete" title="删除银行卡"></i>'
            + '</div>'
            + '</div>'
            + '</div>'
        );
        PaymentTime();
        implement($("#select-" + idTemp));
        var form = layui.form;
        form.render();
    });

    // 2 删除银行卡
    $(document).on("click", ".add-implement-div .layui-icon-delete", function () {
        let id = $(this).attr("data-id");
        if(id === undefined){
            return $(this).parent().parent().remove();
        }
    });

    // 铺付款银行卡数据
    $.get("/table_utils/info", {"table_utils.tableName": "bank_card"}, function (data) {
        let bankCardId = $(".bankCardId");
        bankCardId.empty();
        bankCardId.append('<option></option>');
        $(data["content"]).each(function (index, content) {
            bankCardId.append('<option value="' + content["bankCardId"] + '">' + content["bankCardName"] + '</option>')
        });
        renderFix("bankCardId");
    });

    //打印
    $(".print-btn").on('click', function () {
        $("#inputForm").print({
            globalStyles: true,
            mediaPrint: true,
        })
    });

    // // 获取到所有用户数据,并且录入默认项是登陆者
    // $.get("/table_utils/info", {"table_utils.tableName": "user"}, function (data) {
    //     let userName = $(".userName");
    //     userName.empty();
    //     userName.append('<option></option>');
    //     $(data["content"]).each(function (index, content) {
    //         userName.append('<option>' + content["userName"] + '</option>')
    //     });
    //     // 铺经办人数据(默认当前登录用户,可以选择)
    //     if (id === null) {
    //         userName.val(window.top.userTemp["userName"]);
    //         // 铺申请部门
    //         setTimeout(function () {
    //             showApplyForDepartment($(".applyForDepartment"), window.top.userTemp["jobBean"]["departmentBean"]["departmentName"]);
    //         });
    //     }
    //     renderFix("userName");
    // });


    // 录入 id 为null way="add"
    if (id === null && way === "add") {
        $(".num").hide()
        $(".paymentStatus").val("1");
        $(".confirm-select").remove();
        $(".audit-div").remove();
        $(".pay-div").remove();
        $(".confirm-div").hide();
    }

});

// 方法1 铺费用明细
function showDisburseDetail(dom, type) {
    $.get("/disburse_detail/particulars", {"disburseDetailSource": type}, function (data) {
        dom.empty();
        dom.append(
            '<option></option>'
        );
        $(data).each(function (index, content) {
            dom.append(
                '<option value="' + content["disburseDetailId"] + '">' + content["disburseDetailName"] + '</option>'
            )
        });
        renderFix("disburseDetailId", function () {
        });
    }, "json");
}

// 方法2 铺付款单位
function showSecondParty(dom) {
    $.get("/second_party", {}, function (data) {
        dom.empty();
        dom.append(
            '<option></option>'
        );
        $(data).each(function (index, content) {
            dom.append(
                '<option value="' + content["secondPartyId"] + '">' + content["secondPartyName"] + '</option>'
            )
        });
        renderFix("disburseDetail");
    }, "json");
}

// 方法3 铺申请部门
function showApplyForDepartment(dom, value) {
    $.get("/department", {}, function (data) {
        dom.empty();
        dom.append(
            '<option></option>'
        );
        $(data["content"]).each(function (index, content) {
            dom.append(
                '<option value="' + content["departmentId"] + '" data-name="' + content["departmentName"] + '">' + content["departmentName"] + '</option>'
            )
        });
        renderFix("disburseApplyForDepartment", function () {
            if (value !== undefined) {
                setTimeout(function () {
                    dom.next(".layui-form-select").find("dd[lay-value='" + value + "']").click();
                }, 100);
            } else {
                showData4Object(disburse);
                if (way === "confirm" || way === "audit" || way === "pay" || way === "paid" || way === "details" || way === "print") {
                    // 下拉框转成p
                    $(".info-div select[name]").each(function () {
                        let valTemp = $(this).next(".layui-form-select").find("dd.layui-this").text();
                        showText($(this), valTemp);
                    });
                    if (way === "paid" || way === "details") {
                        // 付款银行卡
                        let bankCardId = $(".bankCardId");
                        if (bankCardId.val() !== "") {
                            let tempVal = bankCardId.next(".layui-form-select").find("dd.layui-this").text();
                            bankCardId.closest(".layui-input-block").html('<p>' + tempVal + '</p>')
                        } else {
                            convertTextNum(bankCardId)
                        }
                    }
                }
            }
        });
    }, "json");
}

// 方法1 铺实银行卡
let implementInfo = null;
function implement(selectDom, value) {
    if (implementInfo === null) {
        $.get("/table_utils/info", {"table_utils.tableName": "bank_card"}, function (data) {
            var data = data.content;
            showImplement(data);
        });
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
            if (value === item["bankCardId"]) {
                selectDom.append('<option selected value="' + item["bankCardId"] + '" >' + item["bankCardName"] + '</option>');
            } else {
                selectDom.append('<option  value="' + item["bankCardId"] + '" >' + item["bankCardName"] + '</option>');
            }

        });

        renderFix("implementName");
    }
}

// 方法4 铺收益单位
function showEarningsCompany(dom) {
    $.get("/earnings_company", {}, function (data) {
        dom.empty();
        dom.append(
            '<option></option>'
        );
        $(data).each(function (index, content) {
            dom.append(
                '<option value="' + content["earningsCompanyId"] + '">' + content["earningsCompanyName"] + '</option>'
            )
        });
        layui.use(['form'], function () {
            var form = layui.form;
            layui.form.render('select');
        });
        showData4Object(disburse);
    }, "json");
}

function showText(dom, val) {
    dom.closest(".layui-input-block").html("<p>" + val + "</p>");
}

function convertTextNum(dom) {
    let thisValue = dom.val();
    dom.after('<p></p>');
    dom.next("p").text(thisValue);
    dom.remove();
}
