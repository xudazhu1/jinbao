let id = getParamForUrl("id");
let way = getParamForUrl("way");
$(function () {
    layui.use(['laydate'], function () {
        var laydate = layui.laydate;
        // 年月日选择器
        laydate.render({
            elem: '.borrowMoneyDate',
            type: 'datetime',
        });
        lay('.test-item').each(function () {
            laydate.render({
                elem: this
                , trigger: 'click'
            });
        });
    });
    // 录入
    if (id === null) {
        $(".paymentStatus").val("1");
        $(".confirm-select").remove();
        $(".audit-div").remove();
        $(".pay-div").remove();
        $(".confirm-div").hide();
        showPersonalBankCard();
    }
    if (id != null) {
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
        if(way == "paid"){
            $(".test-item").attr("disabled","disabled").css("border","none");
        }
        $(".paymentStatus-1").remove();
        $.get("/table_utils/info",
            {borrowMoneyId: id, "table_utils.tableName": "borrow_money"}, function (data) {
                showData4Object(data["content"][0]);//铺数据
                showPersonalBankCard(data["content"][0]["personalBankCardBean"]["personalBankCardId"]);//铺借款人
                let disburseApprovalRemark = $(".disburseApprovalRemark");
                if (way === "confirm" || way === "audit" || way === "pay" || way === "paid" || way === null) {
                    convertTextNum($(".borrowMoneyDate"));//借款日期
                    convertTextNum($(".borrowMoney"));// 借款金额
                    convertTextNum($(".borrowMoneyRemark"));//借款备注

                    disburseApprovalRemark.removeAttr("readonly");// 确认/审核备注
                    if (way === "audit" || way === "pay" || way === "paid") {
                        confirmInnerDiv.html('<p>已确认</p>');
                        if (way === "pay" || way === "paid") {
                            auditInnerDiv.html('<p>审核通过</p>');
                            convertTextNum($(".disburseApprovalRemark2"));
                            // 如果是待支付和已支付状态,则把借款人卡号显示出来
                            $(".none-element").show();

                            if (way === "paid") {
                                convertTextNum($(".disburseApprovalRemark3"));
                                payInnerDiv.html('<p>已支付</p>');
                                $(".submit-btn").remove();
                               $(".bankCard-div").html('<p>'+data["content"][0]["bankCardBean"]["bankCardName"]+'</p>')
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
                    if (way === "return-pay") {
                        confirmInnerDiv.html('<p>已确认</p>');
                        auditInnerDiv.html('<p>审核通过</p>');
                        payInnerDiv.html('<p>退回</p>');
                        payInnerDiv.append('<input type="hidden" name="paymentStatusBean.paymentStatusId" class="layui-input"  placeholder="" value="5">');
                        let bankCardId = $(".bankCardId");
                        if (bankCardId.val() !== "") {
                            let tempVal = bankCardId.next(".layui-form-select").find("dd.layui-this").text();
                            bankCardId.closest(".layui-input-block").html('<p>' + tempVal + '</p>')
                        } else {
                            convertTextNum(bankCardId)
                        }
                    }
                    convertTextNum($(".disburseApprovalRemark"));
                    convertTextNum($(".disburseApprovalRemark2"));
                    convertTextNum($(".disburseApprovalRemark3"));
                }
            }, "json");
    }

    // 铺转出账户数据
    $.get("/table_utils/info", {"table_utils.tableName": "bank_card"}, function (data) {
        let bankCardId = $(".bankCardId");
        bankCardId.empty();
        bankCardId.append('<option></option>');
        $(data["content"]).each(function (index, content) {
            bankCardId.append('<option value="' + content["bankCardId"] + '">' + content["bankCardName"] + '</option>')
        });
        renderFix("bankCardId");
    });


    // 提交
    $(document).on("click", ".submit-btn", function () {
        // 此处校验基本信息  引用 xudazhu.js
        if (!formChecking()) {
            return false;
        }
        let method = getParamForUrl("id") === null ? "POST" : "PUT";
        let itemData = $.param({"_method": method, "tableName": "borrow_money"}) + "&" + $("#inputForm").serialize();
        $.post("/borrow_money", itemData, function (data) {
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
                layer.msg("提交失败", {icon: 5});
            }
        }, "json").fail(function (res) {
            layer.msg('数据提交失败 请稍后重试', {icon: 5});
            console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请稍后重试");
        }, "json");
    })

});
// 铺借款人数据
let personalBankCard = null;

function showPersonalBankCard(cardId) {
    if (personalBankCard === null) {
        $.get("/table_utils/info", {"table_utils.tableName": "personal_bank_card"}, showTemp);
    } else {
        showTemp(personalBankCard);
    }

    function showTemp(data) {
        personalBankCard = data;
        let personalBankCardId = $(".personalBankCardId");
        personalBankCardId.empty();
        personalBankCardId.append('<option></option>');
        $(data["content"]).each(function (index, content) {
            personalBankCardId.append('<option value="' + content["personalBankCardId"] + '">' + content["userBean"]["userName"] + '</option>')
        });

        renderFix("personalBankCardId" , function () {
            if (id !== null) {
                personalBankCardId.next(".layui-form-select").find("dd[lay-value='" + cardId + "']").click();
                if (way === "confirm" || way === "audit" || way === "pay" || way === "paid") {
                    let value = personalBankCardId.next(".layui-form-select");
                    value.after("<p>" + value.find("dd.layui-this").text() + "</p>");
                    value.remove();
                }
            }
        });
    }
}

function convertTextNum(dom) {
    let thisValue = dom.val();
    dom.after('<p></p>');
    dom.next("p").text(thisValue);
    dom.remove();
}
