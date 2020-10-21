let id = getParamForUrl("id");
$(function () {
    // 1 form表单渲染/购买时间
    layui.use(["laydate", "form"], function () {
        var laydate = layui.laydate;
        // 年月日选择器
        laydate.render({
            elem: '#test-laydate-normal-cn'
        });

        var form = layui.form;
        layui.form.render('select');

        // 选择对应付款编号,得到原值
        setInterval(function () {
            form.on('select(disburseId)', function (data) {
                $("[name]:not('.disburseNum ')").val("");
                $(".depreciationMethod").val("按月平均折旧");
                $(".netValue").val("");
                $(".disbursePaymentAmount").val($(".disburseNum option:selected").attr("data-money"));
                $.get("/payment/list_property", {"disburseId":data.value}, function (data) {
                    if(data["content"][0]["propertyBean1"] === ""){
                        return false;
                    }else {
                        showData4Object(data["content"][0]["propertyBean1"]);
                        countMoney();
                        id = data["content"][0]["propertyBean1"]["propertyId"]
                    }
                }, "json");
            });
        }, 100);
    });

    // 2 折旧期限提示和限制只能为正数
    $(document).on("keyup", ".propertyDeadline", function () {
        if (parseInt($(this).val()) < 1) {
            $(this).val("");
            layer.msg("只能填入正整数")
        }
    });

    // 残值率正则表达式校验
    $(document).on("keyup", ".propertyResidual", function () {
        let propertyResidual = $(this).val();//残值率
        if (propertyResidual === "0" || propertyResidual === "0.") {
            $(".netValue").val("");
            return false;
        }
        let pat = new RegExp(/^(100|[1-9]?\d(\.\d\d?\d?)?)%$|0$/);//百分数
        let result = pat.test(propertyResidual);
        let decimal = new RegExp(/^[0]+.?[0-9]*$/);//小数
        let result2 = decimal.test(propertyResidual);
        let money = $(".disbursePaymentAmount").val();
        if (result || result2) {
            layer.tips('格式正确', this, {
                tips: [1, '#72bdcc'],
                time: 2000
            });
            if(result){// 百分数
                propertyResidual = propertyResidual.replace("%","");
                propertyResidual = propertyResidual/100;
            }
            if (money !== "") {
                $(".netValue").val(parseFloat(propertyResidual) * parseFloat(money))
            }
            return true;
        } else {
            layer.tips('格式为 百分数 或 小数', this, {
                tips: [1, '#72bdcc'],
                time: 2000
            });
            $(".netValue").val("");
            return false;
        }
    });

    // 提交
    $(document).on("click", ".submit-btn", function () {
        // 表单校验 引用 xudazhu.js
        if (!formChecking()) {
            return false;
        }


        let propertyResidual = $(".propertyResidual");//残值率
        let pat = new RegExp(/^(100|[1-9]?\d(\.\d\d?\d?)?)%$|0$/);//百分数
        let result = pat.test(propertyResidual.val());
        if(result){
            propertyResidual.val(((propertyResidual.val()).replace("%",""))/100);
        }
        let method = $(".propertyId").val() === "" ? "POST" : "PUT";
        let itemData = $.param({"_method": method}) + "&" + $("#inputForm").serialize();
        $.post("/property", itemData, function (data) {
            if (data) {
                layer.msg('操作成功 2秒后自动关闭', {
                    time: 2000 //2秒关闭（如果不配置，默认是3秒）
                }, function () {
                    window.location.reload();
                    window.parent.getPageDate();
                    window.parent.layer.close(window.parent.layerIndex);
                });
            } else {
                layer.msg('添加失败', {icon: 5});
            }
        }, "json").fail(function (res) {
            layer.msg('数据提交失败 请稍后重试', {icon: 5});
            console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
        }, "json");
    });

    // 如果id不为空,铺资产数据
    if (id !== null) {
        $.get("/table_utils/info", {disburseId: id, "table_utils.tableName": "disburse"}, function (data) {
            let content = data["content"][0]["propertyBean1"];
            delete content["disburseBeans"];
            showData4Object(content);
            showDisburseNum(data["content"][0]["disburseId"]);// 付款编号
            $(".disbursePaymentAmount").val(data["content"][0]["disbursePaymentAmount"]);//原值
        }, "json")
    }
    showDisburseNum();// 调用铺付款编号的方法
});
// 铺付款编号
let disburseInfo = null;

function showDisburseNum(disburseId) {
    if (disburseInfo === null) {
        $.get("/payment/list_property", {}, function (data) {
            disburseInfo = data;
            showTemp();
        }, "json");
    } else {
        showTemp();
    }

    function showTemp() {
        let disburseNum = $(".disburseNum");
        disburseNum.empty();
        disburseNum.append('<option></option>');
        $(disburseInfo["content"]).each(function (index, disburse) {
            disburseNum.append(
                '<option value="' + disburse["disburseId"] + '" data-money="' + disburse["disbursePaymentAmount"] + '">' + disburse["disburseNum"] + '</option>'
            );
        });
        renderFix("disburseId", function () {
            if (id !== null) {
                disburseNum.val(disburseId);
                renderFix("disburseId");
                countMoney();
            }
        });
    }
}

function countMoney() {
    let radio = $(".propertyResidual").val();
    let money = $(".disbursePaymentAmount").val();
    if (radio !== "" && money !== "") {
        $(".netValue").val((radio * money).toFixed(2));
    }
}
//
// function showProperty(id) {
//
// }