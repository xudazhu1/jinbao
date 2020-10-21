let id = getParamForUrl("id");
let way = getParamForUrl("way");
$(function () {
    layui.use(['laydate'], function () {
        var laydate = layui.laydate;
        // 年月日选择器
        laydate.render({
            elem: '.transferAccountsDate',
            type: 'datetime',
        });
        lay('.test-item').each(function () {
            laydate.render({
                elem: this
                , trigger: 'click'
            });
        });
    });

    // 提交
    $(document).on("click", ".submit-btn", function () {
        // 此处校验基本信息  引用 xudazhu.js
        if (!formChecking()) {
            return false;
        }
        let method = getParamForUrl("id") === null ? "POST" : "PUT";
        let itemData = $.param({
            "_method": method,
            "tableName": "transfer_accounts"
        }) + "&" + $("#inputForm").serialize();
        $.post("/transfer_accounts", itemData, function (data) {
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
            layer.msg('数据提交失败 请稍后重试', {icon: 5});
            console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请稍后重试");
        }, "json");
    });
    showBankCard($(".bankCardId"));

    // 4 铺经办人数据(默认当前登录用户,可以选择)
    if (id === null) {
        if (window.top.userTemp === undefined) {
            $.get("/user/get_session_user", function (data) {
                window.top.userTemp = data;
                $(".userName").val(window.top.userTemp["userName"]);
            }, "json");
        } else {
            $(".userName").val(window.top.userTemp["userName"]);
        }

        $(".audit-select").remove();
        $(".audit-div").hide();
    }
    if (id !== null) {
        $(".paymentStatus-1").remove();
        $.get("/table_utils/info",
            {transferAccountsId: id, "table_utils.tableName": "transfer_accounts"}, function (data) {
                showData4Object(data["content"][0]);//铺数据
                showBankCard($(".comeBankCard"), data["content"][0]["comeBankCardBean"]["bankCardId"]);//转出账户
                showBankCard($(".enterBankCard"), data["content"][0]["enterBankCardBean"]["bankCardId"]);//转入账户
                if (way === "audit") {
                    $("#sumCard .show-text").each(function () {
                        convertTextNum($(this))
                    });
                    $(".show-select-text").each(function () {
                        let tempVal = $(this).val();
                        $(this).closest(".layui-input-block").html("<p>"+tempVal+"</p>")
                    })
                }
                //转出类型


                let auditInnerDiv = $(".audit-inner-div");
                if (way === "return-audit") {
                    auditInnerDiv.html('<p>退回</p>');
                    auditInnerDiv.append('<input type="hidden" name="paymentStatusBean.paymentStatusId" class="layui-input"  placeholder="" value="3">');
                    convertTextNum($(".disburseApprovalRemark2"));
                }

            }, "json")
    }

});
let bankCard = null;

function showBankCard(dom, bankCardId) {
    if (bankCard === null) {
        // 铺转出账户数据
        $.get("/table_utils/info", {"table_utils.tableName": "bank_card"}, showTemp);
    } else {
        showTemp(bankCard)
    }

    function showTemp(data) {
        bankCard = data;
        dom.empty();
        dom.append('<option></option>');
        $(data["content"]).each(function (index, content) {
            dom.append('<option value="' + content["bankCardId"] + '">' + content["bankCardName"] + '</option>')
        });
        dom.attr("lay-filter", dom.attr("lay-filter") + Math.ceil(Math.random() * 100000));
        renderFix(dom.attr("lay-filter"), function () {
            if (id !== null) {
                dom.next(".layui-form-select").find("dd[lay-value='" + bankCardId + "']").click();
                if (way === "audit") {
                    let tempText = dom.next(".layui-form-select").find("dd.layui-this").text();
                    if (tempText !== "") {
                        dom.closest(".layui-input-block").html('<p>' + tempText + '</p>');
                    }


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