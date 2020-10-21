let id = getParamForUrl("id");//
let way = getParamForUrl("way");
let removeDisburseIds = [];
var state = "";
var approvalStatusId = "";
var expenseAccountType = "";
$(function () {
    var userId = window.top.userTemp.userId; //用户ID
    var userName = window.top.userTemp.userName; //用户ID
    $(".generalManager-people,.finance-people,.executiveDirector-people,.finalJudgment-people").val(userName);
    let  creator_id;
    let  creator_job_id;
    if (id === null) {
        // 1-1 铺报销总编号
        // $.get("/number", {}, function (data) {
        //     $(".expenseAccountNum").val("BX-" + data["numberName"]);
        // }, "json");
        // 2 铺报销人数据(默认当前登录用户,可以选择)
        if (window.top.userTemp === undefined) {
            $.get("/user/get_session_user", function (data) {
                window.top.userTemp = data;
                $(".userName").val(window.top.userTemp["userName"])
            }, "json");
        } else {
            creator_id=window.top.userTemp.userId
            creator_job_id=window.top.userTemp.jobBean.jobId
            $(".userName").val(window.top.userTemp["userName"])
        }
        //报销汇总去除
        $(".summary-div").remove();

        // 汇总金额隐藏
        $(".money-div").hide();
    }

    // 3 年月选择器 radio单选
    layui.use(['laydate', 'form'], function () {
        var laydate = layui.laydate;
        var form = layui.form;
        lay('.test-item').each(function(){
            laydate.render({
                elem: this
                ,trigger: 'click'
                ,type: 'datetime'
                ,format: 'yyyy-MM-dd HH:mm:ss' //指定时间格式
                ,value: new Date() // 指定值
            });
        });
        lay('.expenseAccountTime-time').each(function(){
            laydate.render({
                elem: this
                ,trigger: 'click'
                ,type: 'datetime'
                ,format: 'yyyy-MM-dd HH:mm:ss' //指定时间格式
                ,value: new Date() // 指定值
            });
        });

        //  监听select下拉
        form.on('select(text)', function(data){
            state = data.value
        });

        laydate.render({
            elem: '#test-laydate-type-month',
            type: 'month',
        });
        //年月选择器
        if (id === null || way === "add") {// 详情
            if (way === null || way === "add") {// 录入 //编辑

            }
        }
        var form = layui.form;
        layui.form.render('select');
        // 2 选择费用归属部门时,隐藏域归属部门(disburse_attribution_department)填值
        setTimeout(function () {
            // 费用归属部门
            form.on('select(disburse_department)', function () {
                var disburse_department = $(this).closest("td").find(".disburse_department option:selected").attr("data-name");
                $(this).closest("tr").find(".disburse_attribution_department").val(disburse_department);
            });

            // 费用归属公司
            form.on('select(disburse_company)', function () {
                var disburse_company = $(this).closest("td").find(".disburse_company option:selected").attr("data-name");
                $(this).closest("tr").find(".disburse_attribution_department").val(disburse_company);
            });

            // 项目对应实施部门
            form.on('select(implementDepartmentId)', function () {
                var implementDepartmentName = $(this).closest("td").find(".implementDepartmentId option:selected").attr("data-name");
                $(this).closest("tr").find(".disburse_attribution_department").val(implementDepartmentName);
            });
        }, 100);

        //3 报销类型选择
        form.on('radio', function (data) {
            $("#inputForm").hide(100);
            $(".form-header-detail").text(data.value + "明细");
            $(".form-header-summary").text(data.value + "汇总");
            aab(data.value);
        });

         //  监听select下拉
        form.on('select(userName)', function(data){
             var value = data.value;
             var name = window.top.userTemp.userName; //获取姓名
             if(value != name){
                $('select[name="expenseAccountUserName"]').val(name);
                layer.msg('报销人必须是本人！',{time : 1500, icon : 2});
                layui.form.render('select'); //刷新select标签
                return;
             }
        });
    });

    // 8 追加单个报销
    $(document).on("click", ".add-btn", function () {
        showTrData();
    });

    // 9 删除单个报销
    $(document).on("click", ".del-btn", function () {
        var that = $(this);
        layer.confirm('确认删除吗？', {
            btn: ['确认', '取消'] //按钮
        }, function () {
            layer.close(layer.index);
            if (id === null) {
                // 重新拼接name值
                that.closest("tr").nextAll().each(function (index, tr) {
                    $(tr).find(".changeName").each(function (index2, changeName) {
                        var oldName = $(changeName).prop("name");
                        var b1 = oldName.substring(0, (oldName.indexOf("[") + 1));
                        var b2 = parseInt(oldName.substring(oldName.indexOf("[") + 1, oldName.indexOf("]"))) - 1;// 减一之后重新拼接
                        var b3 = oldName.substring(oldName.indexOf("]"), oldName.length);
                        $(changeName).attr("name", b1 + b2 + b3);
                    });
                    var oldValue = $(tr).find(".disburseNum").val();
                    var d1 = oldValue.substring(0, (oldValue.lastIndexOf("-") + 1));
                    var d2 = parseInt(oldValue.substring(oldValue.lastIndexOf("-") + 1, oldValue.length)) - 1;
                    $(tr).find(".disburseNum").val(d1 + d2);
                });
            }
            let disburseIdTemp = that.closest("tr").find(".disburseId").val();
            if (disburseIdTemp !== "") {
                removeDisburseIds.push(disburseIdTemp);
            }
            that.parent().parent("tr").remove();
            if (way === "add") {
                layer.msg("点击立即提交可删除生效")
            }
        });
    });

    // 10 提交
    $(document).on("click", ".submit-btn", function () {
        if(approvalStatusId != "0"){
             var approvalStatus = $(".approvalStatus").val();
             if(approvalStatus == ""){
                 layer.msg('审核状态不能为空',{time : 1500, icon : 2});
                 return false;
             }
        }
        // ①表单校验
        // 此处校验基本信息  引用 xudazhu.js
        if (!formChecking()) {
            return false;
        }
        // 此处校验表格信息
        var check = true;
        $(".tbody").find(".layui-input").each(function (index, input) {
            if ($(this).closest("td").css("display") === "none") {
                return;
            }
            var tdIndex = $(this).closest("td").index();
            if (tdIndex > 8) {
                tdIndex = (tdIndex - 2);
            }
            var thText = $(this).closest("table").find("thead").find("th").eq(tdIndex).text();
            if(thText != "报销单号"){
                 if ($(input).val() === "") {
                    let that = this;
                    layer.tips(thText + '不能为空', that, {
                        tips: [1, '#3595CC'],
                        time: 3000
                    });
                    return check = false;
                 }
            }
        });
        if (!check) {
            return false;
        }

        // 备注空字符串处理
        $(".disburseRemarks").each(function () {
            if ($(this).val() === "") {
                $(this).val(" ");
                console.log($(this).val());
            }
        });

        //先删除
        if (removeDisburseIds.length > 0) {
            console.log(removeDisburseIds);
            $.post("/table_utils/id", {
                tableName: "disburse",
                "_method": "DELETE",
                "ids": removeDisburseIds.toString()
            }, function (data) {
                if (data) {
                    layer.msg('删除子报销成功');
                }
            }, "json");
        }
        if(state == ""){
            state = 4;
        }
        let method = getParamForUrl("id") === null ? "POST" : "PUT";
        let itemData = $.param({"_method": method, "tableName": "expense_account","userBean.userId":creator_id,"jobBean.jobId":creator_job_id,"approvalStatusBean.approvalStatusId":state,}) + "&" + $("#inputForm").serialize();
        $.post("/expense_account", itemData, function (data) {
            if (data) {
                layer.msg('操作成功 2秒后自动刷新', {
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
            layer.msg('数据提交失败 请刷新重试', {icon: 5});
            console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
        }, "json");
    });

    // 删除草稿
    $(document).on("click", ".layui-draft", function () {
        // ①表单校验
        // 此处校验基本信息  引用 xudazhu.js
        if (!formChecking()) {
            return false;
        }
        // 此处校验表格信息
        var check = true;
        $(".tbody").find(".layui-input").each(function (index, input) {
            if ($(this).closest("td").css("display") === "none") {
                return;
            }
            var tdIndex = $(this).closest("td").index();
            if (tdIndex > 8) {
                tdIndex = (tdIndex - 2);
            }
            var thText = $(this).closest("table").find("thead").find("th").eq(tdIndex).text();
            if ($(input).val() === "") {
                let that = this;
                layer.tips(thText + '不能为空', that, {
                    tips: [1, '#3595CC'],
                    time: 3000
                });
                return check = false;
            }
        });
        if (!check) {
            return false;
        }

        // 备注空字符串处理
        $(".disburseRemarks").each(function () {
            if ($(this).val() === "") {
                $(this).val(" ");
                console.log($(this).val());
            }
        });

        //先删除
        if (removeDisburseIds.length > 0) {
            console.log(removeDisburseIds);
            $.post("/table_utils/id", {
                tableName: "disburse",
                "_method": "DELETE",
                "ids": removeDisburseIds.toString()
            }, function (data) {
                if (data) {
                    layer.msg('删除子报销成功');
                }
            }, "json");
        }

        var stateTr = 0; //传回后台状态
        let method = getParamForUrl("id") === null ? "POST" : "PUT";
        let itemData = $.param({"_method": method, "tableName": "expense_account","userBean.userId":creator_id,"jobBean.jobId":creator_job_id,"approvalStatusBean.approvalStatusId":stateTr,}) + "&" + $("#inputForm").serialize();
        $.post("/table_utils", itemData, function (data) {
            if (data) {
                layer.msg('操作成功 2秒后自动刷新', {
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
            layer.msg('数据提交失败 请刷新重试', {icon: 5});
            console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
        }, "json");
    });


    //11 获取审核状态
    $.get("/approval_status", {}, function (data) {
        let statusSelect = $("select[lay-filter='approvalStatus']");
        statusSelect.empty();
        statusSelect.append('<option></option>');
        $(data.content).each(function (index, statusTemp) {
            statusSelect.append(
                "<option value='" + statusTemp["approvalStatusId"] + "' >" + statusTemp["approvalStatusName"] + "</option>"
            );
        });
        renderFix("approvalStatus");
    }, "json");

    // 待审核和详情和被退回状态
    if (id !== null) {
        $("body").addClass(way);
        $("#typeDiv").hide();
        $("#inputForm").show();
        $(".add-btn").hide();
        //如果有 id 铺数据
        $.get("/table_utils/info",
            {"expenseAccountId": id, "table_utils.tableName": "expense_account"}, function (data) {
                let expense = data.content[0];
                let type = expense["expenseAccountType"];
                approvalStatusId = data.content[0].approvalStatusBean.approvalStatusId; //支付状态
                expenseAccountType = data.content[0].expenseAccountType; //监听什么报销
                // $(".form-header").text(type);
                $(".form-header-detail").text(type + "明细");
                $(".form-header-summary").text(type + "汇总");
                basic(type);
                // 渲染当前选中的报销类型(单选框)
                $("input[name='type']").each(function (index1, typeD) {
                    if (typeD.value === type) {-
                        $(typeD).attr("checked", true);
                        layui.use(["form"], function () {
                            var form = layui.form;
                            form.render("radio");
                        });
                    }
                });
                // 铺总报销数据
                showData4Object(expense);

                // 铺经办人数据
                showUser(expense["expenseAccountUserName"]);
                // 铺子报销数据
                let allDisbursePaymentAmount = 0;//报销总金额
                let allDisburseInvoiceMoney = 0; //发票总金额

                $(expense["disburseBeans"]).each(function (index, disburse) {
                    allDisburseInvoiceMoney += parseFloat(disburse["disburseInvoiceMoney"]);
                    allDisbursePaymentAmount += parseFloat(disburse["disbursePaymentAmount"]);

                    // 审核 详情 打印 直接铺数据
                    if (way === "audit" || way === "detail" || way === "print") {
                        showTrData2(disburse, expense["expenseAccountType"])
                    } else {
                        showTrData(disburse, type);
                    }

                });
                $(".allDisbursePaymentAmount").html('<p>' + allDisbursePaymentAmount.toFixed(2) + '</p>');
                $(".allDisburseInvoiceMoney").html('<p>' + allDisburseInvoiceMoney.toFixed(2) + '</p>');

                // 报销月份字符串修改
                $(".expenseAccountMonth").each(function () {
                    let tempVal = $(this).val();
                    tempVal = tempVal.substring(0, 7);
                    $(this).val(tempVal);
                });

                if (way !== "print") {
                    $(".summary-div").remove()
                }

                if( way == 'add'){
                    if(approvalStatusId == '3'){
                        $(".layui-draft").hide();
                    }else if(approvalStatusId == '6'){
                        $(".layui-draft").hide();
                    }else if(approvalStatusId == '7'){
                        $(".layui-draft").hide();
                    }
                }
                //审核 详情 打印
                if (way === "audit" || way === "detail" || way === "print" || way === "add") {
                    $(".sum-card [name]").each(function () {
                        if ($(this).attr("type") === "hidden" || $(this).hasClass("expenseAccountNum") || $(this).hasClass("userName")) {
                            return
                        }
                        convertText($(this).closest(".layui-input-block"), $(this).val());
                    });
                    let expenseAccountNum = $(".expenseAccountNum");
                    expenseAccountNum.after('<p>' + expenseAccountNum.val() + '</p>');
                    expenseAccountNum.remove();
                    if (way == "audit" || way === "add") {
                        // 判断审核步骤
                        if(approvalStatusId == 1){ //财务审核
                            $(".layui-executiveDirector,.layui-finance").show();
                            $(".layui-draft").hide();
                            $(".executiveDirector-approvalStatus").closest(".layui-input-block").html('<p>' + '通过' + '</p>'); // 主管审核状态
                            var executiveDirectorp = $(".layui-executiveDirector-p"); //主管备注渲染
                            executiveDirectorp.empty();
                            $(".layui-executiveDirector-p").append('<p>' + data.content[0].expenseAccountSupervisorAuditRemark + '</p>')
                            $(".executiveDirector-people").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountSupervisorAuditUserBean.userName + '</p><input type="hidden" name="expenseAccountAuditUserBean.userId" value='+ userId +'>');
                            $(".executiveDirector-time").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountSupervisorAuditTime + '</p>');
                            $(".finance-userId").append('<input type="hidden" name="expenseAccountAuditUserBean.userId" value='+ userId +'>');
                        }else if(approvalStatusId == 4){ //主管审核
                            $(".layui-executiveDirector").show();
                            $(".layui-draft").hide();
                            $(".executiveDirector-userId").append('<input type="hidden" name="expenseAccountSupervisorAuditUserBean.userId" value='+ userId +'>');
                        }else if(approvalStatusId == 5){ //总经办审核
                            $(".layui-executiveDirector,.layui-generalManager,.layui-finance").show();
                            $(".layui-draft").hide();
                            $(".executiveDirector-approvalStatus").closest(".layui-input-block").html('<p>' + '通过' + '</p>');
                            $(".finance-approvalStatus").closest(".layui-input-block").html('<p>' + '通过' + '</p>');
                            var executiveDirectorp = $(".layui-executiveDirector-p"); //主管备注渲染
                            executiveDirectorp.empty();
                            $(".layui-executiveDirector-p").append('<p>' + data.content[0].expenseAccountSupervisorAuditRemark + '</p>')
                            var financep = $(".layui-finance-p"); //财务渲染
                            financep.empty();
                            $(".layui-finance-p").append('<p>' + data.content[0].expenseAccountRemark + '</p>')
                            $(".executiveDirector-people").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountSupervisorAuditUserBean.userName + '</p>');
                            $(".executiveDirector-time").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountSupervisorAuditTime + '</p>');
                            $(".finance-people").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountAuditUserBean.userName + '</p>');
                            $(".finance-time").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountAuditTime + '</p>');
                            $(".generalManager-userId").append('<input type="hidden" name="expenseAccountGeneralManagerOfficeAuditUserBean.userId" value='+ userId +'>');
                        }else if(approvalStatusId == 9){ //待财务终审信息
                            $(".layui-executiveDirector,.layui-generalManager,.layui-finance,.layui-finalJudgment").show();
                            $(".layui-draft").hide();
                            $(".executiveDirector-approvalStatus").closest(".layui-input-block").html('<p>' + '通过' + '</p>');
                            $(".finance-approvalStatus").closest(".layui-input-block").html('<p>' + '通过' + '</p>');
                            $(".generalManager-approvalStatus").closest(".layui-input-block").html('<p>' + '通过' + '</p>');
                            var executiveDirectorp = $(".layui-executiveDirector-p"); //主管备注渲染
                            executiveDirectorp.empty();
                            $(".layui-executiveDirector-p").append('<p>' + data.content[0].expenseAccountSupervisorAuditRemark + '</p>')
                            var financep = $(".layui-finance-p"); //财务渲染
                            financep.empty();
                            $(".layui-finance-p").append('<p>' + data.content[0].expenseAccountRemark + '</p>')
                            var generalManagerp = $(".layui-generalManager-p"); //总经办备注渲染
                            generalManagerp.empty();
                            $(".layui-generalManager-p").append('<p>' + data.content[0].expenseAccountGeneralManagerOfficeAuditRemark + '</p>')
                            $(".executiveDirector-people").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountSupervisorAuditUserBean.userName + '</p>');
                            $(".executiveDirector-time").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountSupervisorAuditTime + '</p>');
                            $(".finance-people").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountAuditUserBean.userName + '</p>');
                            $(".finance-time").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountAuditTime + '</p>');
                            $(".generalManager-people").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountAuditUserBean.userName + '</p>');
                            $(".generalManager-time").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountGeneralManagerOfficeAuditTime + '</p>');
                            $(".finalJudgment-userId").append('<input type="hidden" name="expenseAccountFinalJudgmentUserBean.userId" value='+ userId +'>');
                         }else if(approvalStatusId == 3){ //财务被退回
                            $(".layui-executiveDirector,.layui-finance").show();
                            $(".layui-draft").hide();
                            $(".executiveDirector-approvalStatus").closest(".layui-input-block").html('<p>' + '通过' + '</p>');
                            var executiveDirectorp = $(".layui-executiveDirector-p"); //主管备注渲染
                            executiveDirectorp.empty();
                            $(".layui-executiveDirector-p").append('<p>' + data.content[0].expenseAccountSupervisorAuditRemark + '</p>;')
                            $(".executiveDirector-people").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountSupervisorAuditUserBean.userName + '</p>');
                            $(".executiveDirector-time").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountSupervisorAuditTime + '</p>');
                            $(".finance-approvalStatus").closest(".layui-input-block").html('<p>' + data.content[0].approvalStatusBean.approvalStatusName + '</p>');
                            $(".layui-finance-p").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountRemark + '</p>');
                            $(".finance-people").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountAuditUserBean.userName + '</p>');
                            $(".finance-time").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountAuditTime + '</p>');
                            $(".finance-userId").append('<input type="hidden" name="expenseAccountAuditUserBean.userId" value='+ userId +'>');
                        }else if(approvalStatusId == 6){ //主管被退回
                            $(".layui-executiveDirector").show();
                            $(".layui-draft").hide();
                            $(".executiveDirector-approvalStatus").closest(".layui-input-block").html('<p>' + data.content[0].approvalStatusBean.approvalStatusName + '</p>');
                            $(".layui-executiveDirector-p").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountSupervisorAuditRemark + '</p>');
                            $(".executiveDirector-people").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountSupervisorAuditUserBean.userName + '</p>');
                            $(".executiveDirector-time").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountSupervisorAuditTime + '</p>');
                            $(".executiveDirector-userId").append('<input type="hidden" name="expenseAccountSupervisorAuditUserBean.userId" value='+ userId +'>');
                        }else if(approvalStatusId == 7){ //总经办被退回
                            $(".layui-executiveDirector,.layui-generalManager,.layui-finance").show();
                            $(".layui-draft").hide();
                            $(".executiveDirector-approvalStatus").closest(".layui-input-block").html('<p>' + '通过' + '</p>');
                            $(".finance-approvalStatus").closest(".layui-input-block").html('<p>' + '通过' + '</p>');
                            $(".generalManager-approvalStatus").closest(".layui-input-block").html('<p>' + data.content[0].approvalStatusBean.approvalStatusName + '</p>');
                            var executiveDirectorp = $(".layui-executiveDirector-p"); //主管备注渲染
                            executiveDirectorp.empty();
                            $(".layui-executiveDirector-p").append('<p>' + data.content[0].expenseAccountSupervisorAuditRemark + '</p>')
                            var financep = $(".layui-finance-p"); //财务渲染
                            financep.empty();
                            $(".layui-finance-p").append('<p>' + data.content[0].expenseAccountRemark + '</p>')
                            $(".layui-generalManager-p").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountGeneralManagerOfficeAuditRemark + '</p>');
                            $(".executiveDirector-approvalStatus").closest(".layui-input-block").html('<p>' + data.content[0].approvalStatusBean.approvalStatusName + '</p>');
                            $(".layui-executiveDirector-p").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountSupervisorAuditRemark + '</p>');
                            $(".executiveDirector-people").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountSupervisorAuditUserBean.userName + '</p>');
                            $(".executiveDirector-time").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountSupervisorAuditTime + '</p>');
                            $(".finance-people").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountAuditUserBean.userName + '</p>');
                            $(".finance-time").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountAuditTime + '</p>');
                            $(".generalManager-people").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountAuditUserBean.userName + '</p>');
                            $(".generalManager-time").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountGeneralManagerOfficeAuditTime + '</p>');
                            $(".generalManager-userId").append('<input type="hidden" name="expenseAccountGeneralManagerOfficeAuditUserBean.userId" value='+ userId +'>');
                        }else if(approvalStatusId == 10){ //待财务终审退回
                            $(".layui-executiveDirector,.layui-generalManager,.layui-finance,.layui-finalJudgment").show();
                            $(".layui-draft").hide();
                             $(".userName").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountAuditUserBean.userName + '</p>');
                            $(".executiveDirector-approvalStatus").closest(".layui-input-block").html('<p>' + '通过' + '</p>');
                            $(".finance-approvalStatus").closest(".layui-input-block").html('<p>' + '通过' + '</p>');
                            $(".generalManager-approvalStatus").closest(".layui-input-block").html('<p>' + '通过' + '</p>');
                            $(".finalJudgment-approvalStatus").closest(".layui-input-block").html('<p>' + data.content[0].approvalStatusBean.approvalStatusName + '</p>');
                            var executiveDirectorp = $(".layui-executiveDirector-p"); //主管备注渲染
                            executiveDirectorp.empty();
                            $(".layui-executiveDirector-p").append('<p>' + data.content[0].expenseAccountSupervisorAuditRemark + '</p>')
                            var financep = $(".layui-finance-p"); //财务渲染
                            financep.empty();
                            $(".layui-finance-p").append('<p>' + data.content[0].expenseAccountRemark + '</p>')
                            var generalManagerp = $(".layui-generalManager-p"); //总经办渲染
                            generalManagerp.empty();
                            $(".layui-generalManager-p").append('<p>' + data.content[0].expenseAccountGeneralManagerOfficeAuditRemark + '</p>')
                            $(".layui-finalJudgment-p").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountFinalJudgmentRemark + '</p>');
                            $(".executiveDirector-approvalStatus").closest(".layui-input-block").html('<p>' + data.content[0].approvalStatusBean.approvalStatusName + '</p>');
                            $(".layui-executiveDirector-p").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountSupervisorAuditRemark + '</p>');
                            $(".executiveDirector-people").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountSupervisorAuditUserBean.userName + '</p>');
                            $(".executiveDirector-time").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountSupervisorAuditTime + '</p>');
                            $(".finance-people").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountAuditUserBean.userName + '</p>');
                            $(".finance-time").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountAuditTime + '</p>');
                            $(".generalManager-people").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountAuditUserBean.userName + '</p>');
                            $(".generalManager-time").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountGeneralManagerOfficeAuditTime + '</p>');
                            $(".finalJudgment-people").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountAuditUserBean.userName + '</p>');
                            $(".finalJudgment-time").closest(".layui-input-block").html('<p>' + data.content[0].expenseAccountFinalJudgmentTime + '</p>');
                            $(".finalJudgment-userId").append('<input type="hidden" name="expenseAccountFinalJudgmentUserBean.userId" value='+ userId +'>');
                        }
                    }

                    // 详情
                    if (way === "detail") {
                        $(".submit-btn,.layui-draft").hide();
                        /*$(".submit-div").hide();//提交按钮隐藏
                        $(".approvalStatus").closest(".layui-input-block").html('<p>' + expense["approvalStatusBean"]["approvalStatusName"] + '</p>');
                        let expenseAccountRemark = $(".expenseAccountRemark");//审核备注
                        expenseAccountRemark.after('<p>' + expenseAccountRemark.val() + '</p>');
                        expenseAccountRemark.remove();*/
                    }
                    // 打印
                    if (way === "print") {
                        // 装订整体右移
                        $(".offset3-div").addClass("layui-col-xs-offset3 ");
                        $(".audit-div").hide();
                        $(".submit-div").hide();//提交按钮隐藏
                        $(".summary-div").hide();
                        $("#advice-div").hide();
                        $(".advice-div").show();
                        $(".print-btn").show();
                        $(".summary-money").show();

                        $(".disburseNum-th").width(40);//报销单号宽度更改
                        $(".disbursePaymentAmount-th").width(70);//报销金额宽度更改
                        $(".disburseInvoiceMoney-th").width(70);//发票金额宽度更改
                        $(".earningsCompany-th").width(70);//收益单位
                        $(".department-th").width(70);//部门
                        $(".disburseDetail-th").width(110);//费用明细
                        $(".disburseRemarks-th").width(220);
                        $(".projectName-th").width(160);//项目名称
                        $(".projectNum-th").width(80);//项目编号
                        $(".disburseTime-th").width(100);//支出时间
                    }
                }
                // 退回重新修改
                if (way === "add") {
                    $(".approvalStatus").closest(".layui-input-block").html(`<p>被退回</p><input type="hidden" name="approvalStatusBean.approvalStatusId" value="1" placeholder="">`);
                    let expenseAccountRemark = $(".expenseAccountRemark");//审核备注
                    expenseAccountRemark.after('<p>' + expenseAccountRemark.val() + '</p>');
                    expenseAccountRemark.remove();
                }
                $.get("/expense_account/print", {"expenseAccountId": id}, function (data) {
                    let summaryTable = $(".summary-table");
                    summaryTable.find("tbody").remove();
                    let i = 0;
                    for (let key in data) {
                        i++;
                        let idTemp = Math.ceil(Math.random() * 10000);
                        let rowspan = data[key].length;
                        summaryTable.append(
                            '<tbody class="summary-tbody" id="summaryTbody-' + idTemp + '">'
                            + '<tr>'
                            + '<td rowspan="' + rowspan + '">' + i + '</td>'
                            + '<td rowspan="' + rowspan + '">' + key + '</td>'
                            + '<td rowspan="' + rowspan + '" id="summaryDisbursePaymentAmount' + idTemp + '"></td>'
                            + '</tr>'
                            + '</tbody>'
                        );
                        let summaryDisbursePaymentAmount = 0;//报销总金额

                        $(data[key]).each(function (index, value) {
                            if (index === 0) {
                                $("#summaryTbody-" + idTemp).find("tr").append(
                                    '<td class="type-project">' + value[5] + '</td>'
                                    + '<td class="type-project">' + value[6] + '</td>'
                                    + '<td class="type-department">' + value[2] + '</td>'
                                    + '<td>' + value[3] + '</td>'
                                    + '<td>' + value[4] + '</td>'
                                )
                            } else if (index > 0) {
                                $("#summaryTbody-" + idTemp).append(
                                    '<tr>'
                                    + '<td class="type-project">' + value[5] + '</td>'
                                    + '<td class="type-project">' + value[6] + '</td>'
                                    + '<td class="type-department">' + value[2] + '</td>'
                                    + '<td>' + value[3] + '</td>'
                                    + '<td>' + value[4] + '</td>'
                                    + '</tr>'
                                )
                            }
                            summaryDisbursePaymentAmount += value[3];
                        });
                        $("#summaryDisbursePaymentAmount" + idTemp).text(summaryDisbursePaymentAmount)

                    }
                    if (type === "部门报销") {
                        $(".type-project").hide();
                    }
                    if (type === "项目报销") {
                        $(".type-department").hide();
                    }

                }, "json")

            }, "json");

    }

    if (id === null||id==="") {

        showUser(window.top.userTemp["userName"]);
        // $(".disburseNum-th").hide()
        // $(".disburseNum-td").hide()
        $("body").addClass("hideNum");
    }

    //打印
    $(".print-btn").on('click', function () {
        $("#advice-div").hide();
        $("#inputForm").print({
            globalStyles: true,
            mediaPrint: true,
        })
    });

});

let users = null;

function showUser(defaultValue) {
    // 获取到所有用户数据,并且录入默认项是登陆者
    if (users === null) {
        $.get("/table_utils/info", {"table_utils.tableName": "user"}, showTemp);
    } else {
        showTemp(users);
    }

    function showTemp(data) {
        users = data;
        let userName = $(".userName");
        userName.empty();
        userName.append('<option></option>');
        $(data["content"]).each(function (index, content) {
            userName.append('<option>' + content["userName"] + '</option>')
        });
        // 铺经办人数据(默认当前登录用户,可以选择)
        userName.val(defaultValue);
        renderFix("userName");
        console.log(way);
        if (way === "audit" || way === "detail" || way === "print" || way === "print") {//审核 详情 打印
            userName.closest(".layui-input-block").html('<p>' + defaultValue + '</p>');
        }
    }

}

function renderFixed() {
    if (id === null) {
        $("select[lay-filter='approvalStatus']").next(".layui-form-select").find("dd[lay-value=1]").click();
    }
}

// 铺表格数据代码(被退回)
function showTrData(disburseBean, typeC) {
    if (disburseBean === undefined) {
        disburseBean = {};
    }
    if(approvalStatusId == '0' || approvalStatusId == '3' || approvalStatusId == '6' || approvalStatusId == '7'){
        var type = expenseAccountType;
    }else{
        var type = $('#expense_radio').find(".layui-form-radioed").prev("input").val();
    }
    var idTemp = Math.ceil(Math.random() * 10000);
    var tbody = $(".tbody");
    var length = tbody.children().length;
    if ($.isEmptyObject(disburseBean)) {
        disburseBean["disburseNum"] = "";//报销单号
        disburseBean["disburseId"] = "";// 报销单号Id
        disburseBean["disburseTime"] = "";//支出时间

        disburseBean["implementBean"] = {};//实施信息
        disburseBean["implementBean"]["projectBean"] = {};//项目信息
        disburseBean["implementBean"]["projectBean"]["projectId"] = "";//项目id

        disburseBean["disburseDetailBean"] = {};
        disburseBean["disburseDetailBean"]["disburseDetailId"] = "";//费用明细

        disburseBean["departmentBean"] = {};
        disburseBean["departmentBean"]["departmentId"] = "";//费用归属部门

        disburseBean["companyBean"] = {};
        disburseBean["companyBean"]["companyId"] = "";//费用归属公司

        disburseBean["earningsCompanyBean"] = {};
        disburseBean["earningsCompanyBean"]["earningsCompanyId"] = "";//收益单位

        disburseBean["disburseExpenseAccountContent"] = "";//报销内容
        disburseBean["disbursePaymentAmount"] = "";//报销金额
        disburseBean["disburseInvoiceMoney"] = "";//发票金额
        disburseBean["disburseRemarks"] = "";// 备注
    }
    tbody.append(
        '<tr id="project_tr_' + idTemp + '">'
        // 报销单号
        + '<td style="min-width: 120px" class="disburseNum-td">'
        + '<input type="text" name="disburseBeans[' + length + '].disburseNum" value="' + disburseBean["disburseNum"] + '" class="layui-input disburseNum changeName text-show" id="disburse_num_' + idTemp + '" title="" placeholder="自动生成" >'
        + '<input type="hidden" name="disburseBeans[' + length + '].disburseAffiliation" title="" value="报销" class="changeName">'
        + '<input type="hidden" name="disburseBeans[' + length + '].disburseId" value="' + disburseBean["disburseId"] + '" class="disburseId">'
        + '</td>'
        // 项目编号
        + '<td class="project-td" style="min-width: 120px">'
        + '<select id="projectNum_' + idTemp + '" class="projectNum" lay-search lay-filter="projectNum"  title="" >'
        + '<option></option>'
        + '</select>'
        + '</td>'
        // 项目名称
        + '<td class="project-td" style="min-width: 220px">'
        + '<select id="projectName_' + idTemp + '" class="projectName" lay-search lay-filter="projectName" title="">'
        + '<option></option>'
        + '</select>'
        + '</td>'
        // 支出时间
        + '<td style="min-width: 120px" class="disburseTime-td">'
        + '<input type="text" name="disburseBeans[' + length + '].disburseTime" value="' + disburseBean["disburseTime"] + '" class="layui-input changeName lay_date text-showb" id="test-laydate-normal-' + idTemp + '" name="disburseTimeBean[' + length + ']" title="" placeholder="请选择..."  autocomplete="off">'
        + '</td>'
        // 内容
        + '<td class="content-td" style="max-width: 200px">'
        + '<textarea name="disburseBeans[' + length + '].disburseExpenseAccountContent" id="textarea_content_' + idTemp + '" placeholder="请输入..." class="disburseExpenseAccountContent layui-textarea layui-input changeName" style="min-height: 80px"></textarea>'
        + '</td>'
        // 费用明细
        + '<td>'
        + '<select name="disburseBeans[' + length + '].disburseDetailBean.disburseDetailId" lay-filter="disburseDetail"  title="" id="disburse_detail_' + idTemp + '" lay-filter="disburseDetail" class="changeName disburseDetail" lay-search>'
        + '<option></option>'
        + '</select>'
        + '</td>'
        // ①费用归属部门
        + '<td class="department-td">'
        + '<select name="disburseBeans[' + length + '].departmentBean.departmentId" lay-filter="department"  title="" id="department_' + idTemp + '" lay-filter="disburse_department" class="disburse_department changeName">'
        + '<option></option>'
        + '</select>'
        + '</td>'
        // ②费用归属公司
        + '<td class="company-td">'
        + '<select name="disburseBeans[' + length + '].companyBean.companyId" lay-filter="company"  title="" id="company_' + idTemp + '" class="changeName disburse_company" lay-filter="disburse_company">'
        + '<option></option>'
        + '</select>'
        // ③项目报销 项目带来实施部门
        + '<td class="project-td">'
        + '<select name="disburseBeans[' + length + '].implementBean.implementId" title="" id="project_a" class="changeName implementDepartmentId" lay-filter="implementDepartmentId">'
        + '<option></option>'
        + '</select>'
        + '</td>'
        // 费用归属(隐藏)
        + '</td>'
        + '<td style="display: none">'
        + '<input name="disburseBeans[' + length + '].disburseAttributionDepartment"  type="text" class="disburse_attribution_department changeName" title="">'
        + '</td>'
        // 收益单位
        + '<td>'
        + '<select name="disburseBeans[' + length + '].earningsCompanyBean.earningsCompanyId" lay-filter="earningsCompany"  title="" id="earnings_company_' + idTemp + '" class="changeName earningsCompany">'
        + '<option></option>'
        + '</select>'
        + '</td>'
        // 报销金额
        + '<td>'
        + '<input name="disburseBeans[' + length + '].disbursePaymentAmount" value="' + disburseBean["disbursePaymentAmount"] + '" type="number" class="layui-input changeName text-show" title="" placeholder="请输入...">'
        + '</td>'
        // 发票金额
        + '<td>'
        + '<input name="disburseBeans[' + length + '].disburseInvoiceMoney" value="' + disburseBean["disburseInvoiceMoney"] + '" type="number" class="layui-input changeName text-show" title="" placeholder="请输入...">'
        + '</td>'
        // 备注
        + '<td style="max-width: 200px;min-width: 70px">'
        + '<textarea name="disburseBeans[' + length + '].disburseRemarks" id="textarea_remark_' + idTemp + '" class="layui-textarea changeName disburseRemarks" placeholder="请输入..." class="layui-textarea" style="min-height: 80px"></textarea>'
        + '</td>'
        // 删除
        + '<td style="text-align: center;width: 60px" class="to-do">'
        + '<span class="layui-icon layui-icon-delete del-btn" style="color: #ff4646" title="删除"></span>'
        + '</td>'
        + '</tr>'
    );
    // 铺内容
    $("#textarea_content_" + idTemp).val(disburseBean["disburseExpenseAccountContent"]);
    // 铺备注
    $("#textarea_remark_" + idTemp).val(disburseBean["disburseRemarks"]);

    let project_td = $(".project-td");//项目
    let department_td = $(".department-td");//部门
    let company_td = $(".company-td");//公司
    let content_td = $(".content-td");//内容

    if (type === "" || type === undefined) {
        type = typeC;
    }
    // ①调用铺报销单号的方法
    if (id === null) {
        showDisburseNum($("#disburse_num_" + idTemp));
    }
    if (id === null || way === "add") {//详情
        if (way === null || way === "add") {//录入
            //⑥铺支出时间
            var startTime;
            var now = new Date();
            layui.use('laydate', function(){
                var laydate = layui.laydate;
                startTime= laydate.render({
                    elem: '#test-laydate-normal-' + idTemp,
                    type: 'datetime',
                    format: 'yyyy-MM-dd HH:mm:ss', //指定时间格式
                    ready: function(date){
                        //可以自定义时分秒
                        this.dateTime.hours=now.getHours();
                        this.dateTime.minutes=now.getMinutes();
                        this.dateTime.ss=now.getMilliseconds();
                    }
                });
            });
        }
    }

    // 保存草稿时
    if(approvalStatusId == '0'){
        $(".add-btn").show();
        $(".money-div").hide();
        //⑥铺支出时间
        var startTime;
        var now = new Date();
        layui.use('laydate', function(){
            var laydate = layui.laydate;
            startTime= laydate.render({
                elem: '#test-laydate-normal-' + idTemp,
                type: 'datetime',
                format: 'yyyy-MM-dd HH:mm:ss', //指定时间格式
                ready: function(date){
                    //可以自定义时分秒
                    this.dateTime.hours=now.getHours();
                    this.dateTime.minutes=now.getMinutes();
                    this.dateTime.ss=now.getMilliseconds();
                }
            });
        });
    }else if(approvalStatusId == '6'){
         $(".add-btn").show();
         $(".money-div").hide();
    }else if(approvalStatusId == '3'){
          $(".add-btn").show();
          $(".money-div").hide();
    }else if(approvalStatusId == '7'){
          $(".add-btn").show();
          $(".money-div").hide();
    }
    // ②调用铺费用明细的方法
    if (type === "部门报销") {
        showDisburseDetail($("#disburse_detail_" + idTemp), "部门支出", disburseBean["disburseDetailBean"]["disburseDetailId"]);
    }
    if (type === "项目报销") {
        showDisburseDetail($("#disburse_detail_" + idTemp), "项目支出", disburseBean["disburseDetailBean"]["disburseDetailId"]);
    }


    // ③调用铺费用归属部门的方法
    showdepartment($("#department_" + idTemp), disburseBean["departmentBean"]["departmentId"]);

    // ④调用铺收益单位方法
    showEarningsCompany($("#earnings_company_" + idTemp), disburseBean["earningsCompanyBean"]["earningsCompanyId"]);


    if (type === "部门报销") {
        project_td.hide();// 这个方法失效过
        company_td.hide();// 这个方法失效过
    } else if (type === "公司报销") {
        project_td.hide();
        department_td.hide();
    } else if (type === "项目报销") {
        content_td.hide();
        department_td.hide();
        company_td.hide();
        // ⑦调用铺项目的方法
        showProjectInfo($("#project_tr_" + idTemp), disburseBean["implementBean"]["projectBean"]["projectId"]);
    }
    // 被退回状态
    if (way === "add") {
        changeName();
    }

}

// 铺表格数据代码(审核/详情/打印)
function showTrData2(disburseBean, type) {
    // 最后一行操作框隐藏
    $(".to-do").hide();
    let tbody = $(".tbody");
    let idTemp = Math.ceil(Math.random() * 10000);
    if (type === "部门报销") {
        disburseBean["implementBean"] = {};//实施信息
        disburseBean["implementBean"]["projectBean"] = {};//项目信息
        disburseBean["implementBean"]["projectBean"]["projectName"] = "";//项目名称
        disburseBean["implementBean"]["projectBean"]["projectNum"] = "";//项目编号
        disburseBean["implementBean"]["departmentBean"] = {};
        disburseBean["implementBean"]["departmentBean"]["departmentName"] = "";//实施部
    }
    tbody.append(
        '<tr>'
        // 报销单号
        + '<td style="min-width: 120px" class="disburseNum-td"><p id="disburseNum-p'+idTemp+'">' + disburseBean["disburseNum"] + '</p></td>'
        // 项目编号
        + '<td class="project-td" style="min-width: 120px"><p>' + disburseBean["implementBean"]["projectBean"]["projectNum"] + '</p></td>'
        // 项目名称
        + '<td class="project-td" style="min-width: 220px"><p>' + disburseBean["implementBean"]["projectBean"]["projectName"] + '</p></td>'
        // 支出时间
        + '<td style="min-width: 120px" class="disburseTime-td"><p id="disburseTime-p'+idTemp+'">' + disburseBean["disburseTime"] + '</p></td>'
        // 内容
        + '<td class="content-td" style="max-width: 200px"><p>' + disburseBean["disburseExpenseAccountContent"] + '</p></td>'
        // 费用明细
        + '<td><p>' + disburseBean["disburseDetailBean"]["disburseDetailName"] + '</p></td>'
        // ①费用归属部门
        + '<td class="department-td"><p>' + disburseBean["departmentBean"]["departmentName"] + '</p></td>'
        // ③项目报销 项目带来实施部门
        + '<td class="project-td"><p>' + disburseBean["implementBean"]["departmentBean"]["departmentName"] + '</p></td>'
        // 收益单位
        + '<td><p>' + disburseBean["earningsCompanyBean"]["earningsCompanyName"] + '</p></td>'
        // 报销金额
        + '<td><p>' + disburseBean["disbursePaymentAmount"] + '</p></td>'
        // 发票金额
        + '<td><p>' + disburseBean["disburseInvoiceMoney"] + '</p></td>'
        // 备注
        + '<td style="max-width: 200px;min-width: 70px"><p>' + disburseBean["disburseRemarks"] + '</p></td>'
        + '</tr>'
    );
    let project_td = $(".project-td");//项目
    let department_td = $(".department-td");//部门
    let company_td = $(".company-td");//公司
    let content_td = $(".content-td");//内容

    if (type === "部门报销") {
        project_td.hide();// 这个方法失效过
        company_td.hide();// 这个方法失效过
    } else if (type === "公司报销") {
        project_td.hide();
        department_td.hide();
    } else if (type === "项目报销") {
        content_td.hide();
        department_td.hide();
        company_td.hide();
    }


    if(way === "print"){
        // 报销单号精简
        let disburseNumTemp = $("#disburseNum-p"+idTemp);
        let tempText = disburseNumTemp.text();
        tempText = tempText.substring(tempText.lastIndexOf("-")+1,tempText.length);
        disburseNumTemp.text(tempText);
        // 支出时间精简
        let disburseTimeTemp = $("#disburseTime-p"+idTemp);
        let tempText2 = disburseTimeTemp.text();
        tempText2 = tempText2.substring(0,10);
        disburseTimeTemp.text(tempText2);

    }


}


function aab(checkedValue) {
    $("#inputForm").show(100);
    // 铺不同报销的费用明细
    $("select[id^='disburse_detail_']").each(function (index, select) {
        showDisburseDetail($(select), checkedValue);
    });
     $("select[id^='projectNum']").each(function (index, select) {
            //showProjectNum($(select), checkedValue);
        });
    $(".expenseAccountType").val(checkedValue);

    // 将tbody清空
    $(".tbody").empty();
    basic(checkedValue);
    showTrData();
}

function basic(checkedValue) {
    var project_th = $(".project-th");
    var content_th = $(".content-th");
    var change_th = $(".change-th");

    if (checkedValue === "部门报销") {
        change_th.text("*归属部门");//修改th
        project_th.hide();
        content_th.show();
    } else if (checkedValue === "公司报销") {
        change_th.text("*费用归属公司");//修改th
        project_th.hide();
        content_th.show();
    } else if (checkedValue === "项目报销") {
        change_th.text("*归属部门");//修改th
        project_th.show();
        content_th.hide();
    }
}

// 方法1 铺费用明细
function showDisburseDetail(dom, category, detailId) {
    $.get("/disburse_detail/detail", {"s": category}, function (data) {
        dom.empty();
        dom.append(
            '<option></option>'
        );
        $(data).each(function (index, content) {
            dom.append(
                '<option value="' + content["0"] + '">' + content["1"] + '</option>'
            )
        });
        renderFix("disburseDetail", function () {
            dom.next(".layui-form-select").find("dd[lay-value='" + detailId + "']").click();
        });
    }, "json");
}


// 方法2 铺费用归属部门
function showdepartment(dom, departmentId) {
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
        renderFix("department", function () {
            dom.next(".layui-form-select").find("dd[lay-value='" + departmentId + "']").click();
        });

    }, "json");
}

// 方法3 铺收益单位
function showEarningsCompany(dom, earningsCompanyId) {
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
        renderFix("earningsCompany", function () {
            dom.next(".layui-form-select").find("dd[lay-value='" + earningsCompanyId + "']").click();
        });
    }, "json");
}

// 方法4 铺自动生成的报销单号
function showDisburseNum(dom) {
    var tbody = $(".tbody");
    var length = tbody.children().length;
    dom.val($(".expenseAccountNum").val() + "-" + length);
}

let projectInfo = null;

function showProjectInfo(trDom, projectId2) {
    if (projectInfo === null) {
        $.get("/table_utils",
            {
                "table_utils.tableName": "project",
                "table_utils.fields":
                    "projectId$projectNum" +
                    "$projectName" +
                    "$implementBeans[n].implementId" +
                    "$implementBeans[n].departmentBean.departmentName" +
                    "$earningsCompanyBean.earningsCompanyId"
            },
            function (data) {
                projectInfo = data.content;
                // 调用 铺项目数据 的方法
                showTemp(projectId2);
            });
    } else {
        // 调用 铺项目数据 的方法
        showTemp(projectId2);
    }

    function showTemp(projectId2) {
        let projectNum = trDom.find("select.projectNum");
        let projectName = trDom.find("select.projectName");
        projectNum.empty();
        projectName.empty();
        projectNum.append("<option></option>");
        projectName.append("<option></option>");
        $(projectInfo).each(function (index, projectTemp) {
            projectNum.append("<option value='" + projectTemp[0] + "' data-earningsCompanyId='" + projectTemp[5] + "'>" + projectTemp[1] + "</option>");
            projectName.append("<option value='" + projectTemp[2] + "' data-earningsCompanyId='" + projectTemp[5] + "'>" + projectTemp[2] + "</option>");
        });
        renderFix("projectNum");
        renderFix("projectName");
        $("select.projectNum + .layui-form-select dd  , select.projectName + .layui-form-select dd").click(function () {
            let myDDs = $("select.projectNum + .layui-form-select dd"),
                heDDs = $("select.projectName + .layui-form-select dd");
            if ($(this).closest(".layui-form-select").prev().hasClass("projectName")) {
                let temp = myDDs;
                myDDs = heDDs;
                heDDs = temp;
            }
            let trDomTemp = $(this).closest("tr");
            let thisTemp = this; // 当前点击的dd

            myDDs.each(function (index, ddTemp) {
                if (ddTemp === thisTemp) {// 如果遍历出来的ddTemp和当前点击的thisTemp相等
                    heDDs.get(index).click();// 另外一个dd(下标相同)则点击
                    let selectTemp = trDomTemp.find("select.implementDepartmentId");//找到需要铺实施部门的select框
                    let attributionDepartmentTemp = trDomTemp.find("input.disburse_attribution_department");
                    selectTemp.empty();
                    let projectIdTemp = trDomTemp.find("select.projectNum").val();// 当前当前点击的项目的id
                    let earningsCompanyId = trDomTemp.find("select.projectNum option:selected").attr("data-earningsCompanyId");// d当前点击项目的收益单位
                    // 铺对应收益单位数据
                    trDomTemp.find(".earningsCompany").next(".layui-form-select").find("dd[lay-value='" + earningsCompanyId + "']").click();
                    $(projectInfo).each(function (index, projectTemp) {// 对所有获取到的项目信息做个遍历
                        if (projectTemp[0] === parseInt(projectIdTemp)) { //projectTemp[0]为项目编号
                            let deptIds = projectTemp[3].split("$,"); // deptIds为所有实施部的id  ["3", "2$"]
                            let deptNames = projectTemp[4].split("$,");// deptIds为所有实施部的名字 ["勘察部", "咨询部$"]
                            // 铺该项目对应的所有实施部
                            $(deptIds).each(function (index, deptId) {
                                selectTemp.append("<option value='" + deptId.replace(/[$]/g, "") + "' data-name='" + deptNames[index].replace(/[$]/g, "") + "'>" + deptNames[index].replace(/[$]/g, "") + "</option>");
                            });
                            // 给隐藏域(另一个费用归属部门)填入出现的第一个的实施部的名字
                            attributionDepartmentTemp.val(deptNames[0].replace(/[$]/g, ""));
                        }
                    });
                    renderFix("implementDepartmentId");
                }
            });
        });
        projectNum.next(".layui-form-select").find("dd[lay-value='" + projectId2 + "']").click();
    }
}

function changeName() {
    let approvalStatusSelector = $(".approvalStatus");
    approvalStatusSelector.val("1");
    renderFix("approvalStatus");
    approvalStatusSelector.next(".layui-form-select").find("input.layui-unselect").val("提交后待审核");
}

function convertText(dom, showText) {
    dom.empty();
    dom.append('<p></p>');
    dom.find("p").text(showText);
}

function newDateTime(date) {
    var y = date.getFullYear();
    var m = date.getMonth() + 1;
    m = m < 10 ? ('0' + m) : m;
    var d = date.getDate();
    d = d < 10 ? ('0' + d) : d;
    var h = date.getHours();
    h = h < 10 ? ('0' + h) : h;
    var minute = date.getMinutes();
    minute = minute < 10 ? ('0' + minute) : minute;
    var second = date.getSeconds();
    second = second < 10 ? ('0' + second) : second;
    return y + '-' + m + '-' + d + ' ' + h + ':' + minute + ':' + second;
}
