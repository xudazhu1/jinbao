let id = getParamForUrl("id");
let way = getParamForUrl("way");
let type = getParamForUrl("category");
let disburse = null;
var state = "";
var paymentAmount = "";
var implementId = "";
$(function () {
    $("body").addClass(way);
    //如果有 id 铺数据(待确认/退回待确认/待审核/退回待审核/待支付/退回待支付/已支付/详情)
    if (id != null) {

        $(".form-header").text(type);//标题
        if (type === "项目支出") {
            $(".project-hidden").hide();
        } else if (type === "公司支出" || type === "部门支出") {
            $(".projectNum-div").hide();
            $(".projectName-div").hide();

        }
        $("#radioDiv").hide();// 最上面单选框div隐藏
        $("#inputForm").show();
        showDisburseDetail($("#disburse_detail"), type, "付款申请单");//铺费用明细
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
        $.get("/table_utils/info",
            {disburseId: id, "table_utils.tableName": "disburse"}, function (data) {
                disburse = data.content[0];
                implementId = disburse.implementBean.implementId;
                let departmentInfo = disburse["departmentBean"]["departmentId"];// 组织架构的部门id
                let paymentStatusId = disburse["paymentStatusBean"]["paymentStatusId"];//支付状态
                var bankCardAllotBeans = data.content[0].bankCardAllotBeans;
                showUser(disburse["disburseResponsiblePerson"]);//经办人
                changeType(type, departmentInfo);//铺数据
                showData4Object(disburse);//铺其他文本框数据,也把select框的数据给铺上去了
                $("disburseTime").val(disburse["disburseTime"]);
                // 铺所有数据完成,可能会有延时
                // 待确认时,展示信息的需要转化成text
                let disburseApprovalRemark = $(".disburseApprovalRemark");
                if(paymentStatusId == "1" || paymentStatusId == "3" || paymentStatusId == "5" || paymentStatusId == "7" || paymentStatusId == "8"|| paymentStatusId == "10"){
                     if(type == "项目支出"){
                        $(".layui-ItemNumber").closest(".layui-input-block").html('<p>' + disburse.implementBean.projectBean.projectNum + '</p>');
                        $(".layui-entryName").closest(".layui-input-block").html('<p style="height: 29px; overflow: hidden;">' + disburse.implementBean.projectBean.projectName + '</p>');
                        $(".layui-Application-Department").closest(".layui-input-block").html('<p>' + disburse.implementBean.departmentBean.departmentName + '</p>');
                     }
                }
                if(way === "return-audit" || way === "return-pay" || way === "confirm" || way === "audit" || way === "pay" || way === "paid" || way === "return-confirm"){
                    if(paymentStatusId == "1"){
                        $(".layui-return").remove();
                        $(".disburseApprovalRemark").val("");
                        if(type == "部门支出"){
                             $(".layui-Application-Department").closest(".layui-input-block").html('<p>' + disburse.departmentBean.departmentName + '</p>');
                        }
                    }else if(paymentStatusId == "2"){
                        $(".layui-return").remove();
                        $('.projectName').val(); // 获取下拉的name值，用val渲染
                        $(".implement").append( '<option>'+ disburse.implementBean.departmentBean.departmentName+'</option>');
                        layui.use(['form'], function () {
                            var form = layui.form;
                            $('select[name="implementBean.projectBean.projectId"]').val(disburse.implementBean.projectBean.projectId); // 获取下拉的name值，用val渲染
                            $('select[name="implementBean.projectBean.projectName"]').val(disburse.implementBean.projectBean.projectName);
                            form.render('select');
                        });
                        state = 1;
                    }else if(paymentStatusId == "3"){
                        $(".layui-return").remove();
                        $(".disburseApprovalRemark2").val("");
                        if(type == "部门支出"){
                             $(".layui-Application-Department").closest(".layui-input-block").html('<p>' + disburse.departmentBean.departmentName + '</p>');
                        }
                    }else if(paymentStatusId == "4"){
                        $(".layui-return").remove();
                        state = 2;
                    }else if(paymentStatusId == "5"){
                        $(".layui-return").remove();
                        $(".disburseApprovalRemark3").val("");
                        $(".layui-paymentAudit").remove();
                        if(type == "部门支出"){
                             $(".layui-Application-Department").closest(".layui-input-block").html('<p>' + disburse.departmentBean.departmentName + '</p>');
                        }
                        document.getElementById("layui-select").classList.remove("need-input"); //删除class
                        document.getElementById("layui-money").classList.remove("need-input");
                        document.getElementById("layui-time").classList.remove("need-input");
                    }else if(paymentStatusId == "6"){
                        $(".layui-return").remove();
                        state = 2;
                    }if(paymentStatusId == "7"){
                          $(".layui-return").remove();
                          convertTextNum($(".disburseApprovalRemark3"));
                          payInnerDiv.html('<p>已支付</p>');
                         document.getElementById("layui-Bank").classList.remove("add-implement-btn");
                         $(".bank-card,.add-implement-btn,.layui-paymentAudit-add").remove();
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
                         if(type == "部门支出"){
                              $(".layui-Application-Department").closest(".layui-input-block").html('<p>' + disburse.departmentBean.departmentName + '</p>');
                         }
                     }else if(paymentStatusId == "8"){
                        convertTextNum($(".disburseApprovalRemark3"));
                        payInnerDiv.html('<p>已支付</p>');
                        $(".layui-return").remove();
                        $(".layui-examineAdopt").html('<p>审核通过</p>');
                        $(".submit-btn").remove();
                        $(".bank-card,.add-implement-btn,.layui-paymentAudit-add").remove();
                        convertTextNum($(".disburseApprovalRemark4"));
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
                     }else if(paymentStatusId == "10"){
                          convertTextNum($(".disburseApprovalRemark5"));
                          $(".pay-inner-return").html('<p>退回</p>')
                          $(".layui-paymentAudit").remove();
                          var implementReturn = $(".add-implement-return");
                          implementReturn.empty();
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
                          $(".add-implement-return").append(html);
                     }
                }
                if (way === "confirm" || way === "audit" || way === "pay" || way === "paid" || way === null) {
                    convertText();
                    convertTextNum($(".disburseNum"));//付款申请编号
                    disburseApprovalRemark.removeAttr("readonly");// 确认/审核备注
                    if (way === "audit" || way === "pay" || way === "paid") {
                        confirmInnerDiv.html('<p>已确认</p>');
                        if (way === "pay" || way === "paid") {
                            auditInnerDiv.html('<p>审核通过</p>');
                            convertTextNum($(".disburseApprovalRemark2"));
                            if (way === "paid") {
                                convertTextNum($(".disburseApprovalRemark5"));
                                //$(".submit-btn").remove();
                            }
                        }
                        convertTextNum($(".disburseApprovalRemark"));
                    }
                    // 详情页
                    if (way === null) {
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
                                let bankCardId = $(".bankCardId");
                                if (bankCardId.val() === "") {
                                    bankCardId.closest(".layui-input-block").html("");
                                } else {
                                    bankCard();
                                }
                                $(".bankCard-div").hide();
                            } else if (paymentStatusId === 6) {
                                payInnerDiv.html('<p>退回</p>');
                                bankCard();
                                $(".bankCard-div").hide();
                            } else {
                                payInnerDiv.html('<p>已支付</p>');
                            }
                        }
                        convertTextNum($(".disburseApprovalRemark"));
                        convertTextNum($(".disburseApprovalRemark2"));
                        convertTextNum($(".disburseApprovalRemark3"));
                        convertTextNum($(".disburseApprovalRemark4"));
                        convertTextNum($(".disburseApprovalRemark5"));
                        $(".submit-btn").remove();
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
                        $(".bankCard-div").hide();
                    }
                    convertTextNum($(".disburseApprovalRemark"));
                    convertTextNum($(".disburseApprovalRemark2"));
                    convertTextNum($(".disburseApprovalRemark3"));
                    convertTextNum($(".disburseApprovalRemark4"));
                }
            }, "json");
    }

    // 3-4 铺实施部数据
    implement($(".bankCardId"));

    // 1 年月选择器 radio单选
    PaymentTime();
    function PaymentTime() {
        layui.use(['laydate', 'form','table'], function () {
            //  监听销售员select下拉
            var form = layui.form;
            var laydate = layui.laydate;
            var now = new Date();
            // 年月日选择器
            laydate.render({
                elem: '#test-laydate-normal-cn',
                type: 'datetime', // 可选择：年、月、日、时、分、秒
                format: 'yyyy-MM-dd HH:mm:ss', //指定时间格式
                trigger: 'click',
                done: function (value, date, endDate) {
                    // 选中回调
                }
            });
            lay('.test-item').each(function () {
                laydate.render({
                    elem: this
                    , trigger: 'click'
                });
            });

            var form = layui.form;
            //  监听select下拉
            form.on('select(text)', function(data){
                state = data.value
            });
            layui.form.render('select');
            var disburseAttributionDepartment = $(".disburseAttributionDepartment");
            //1 付款申请类型选择
            setTimeout(function () {
                form.on('radio', function (data) {
                    $("#inputForm").hide(100);
                    $(".form-header").text(data.value);//标题
                    $("#sumCard input:not(.expect-input)").val("");
                    $("#sumCard textarea").val("");
                    // 5 铺报销总编号
                    // $.get("/number", {}, function (data) {
                    //     $(".disburseNum").val("FK-" + data["numberName"]);
                    // }, "json");
                    // 4 铺经办人数据(默认当前登录用户,可以选择)
                    if (id === null) {
                        showUser(window.top.userTemp["userName"])
                        // if (window.top.userTemp === undefined) {
                        //     $.get("/user/get_session_user", function (data) {
                        //         window.top.userTemp = data;
                        //         $(".userName").val(window.top.userTemp["userName"]);
                        //     }, "json");
                        // } else {
                        //     $(".userName").val(window.top.userTemp["userName"]);
                        // }
                    }
                    changeType(data.value);
                });

            });

            setTimeout(function () {
                form.on('select(disburseDetail)', function (data) {
                    var data = data.value;
                    if(data == "44"){
                        $(".layui-str").show();
                        $(".payment-amount").attr("disabled","disabled").attr('placeholder',"");
                        $(".payment-money").append('<input value="'+ paymentAmount +'" name="disbursePaymentAmount" hidden="hidden">');
                    }else{
                        $(".layui-str").hide();
                        $(".payment-amount").removeAttr("disabled").attr('placeholder',"请输入金额");
                    }
                    let text = $("#disburse_detail").find("option:selected").attr("data-type");
                    let disburseLeaderProfitPerson = $("#div-disburseLeaderProfitPerson");
                    if (text === '班组费') {
                        disburseLeaderProfitPerson.show();
                        $("#disburseLeaderProfitPerson").addClass("need-input");
                    } else {
                        disburseLeaderProfitPerson.hide();
                        $("#disburseLeaderProfitPerson").removeClass("need-input");
                    }
                });

            });

             //实时监听金额输入框值变化
            $('.input-form :input').bind('input propertychange', function(){
                //获取.input-form下的所有 <input> 元素,并实时监听用户输入
                var commission = $(".business-commission").val();
                var taxation = $(".Taxation").val();
                var theSum = Number(commission) + Number(taxation);
                $('input[name="disbursePaymentAmount"]').val(theSum);//获取输入框的值
                paymentAmount = theSum
             })
            // 2 铺费用归属部门(一一对应)
            // 1 当选择项目编号时,项目名称就对自动填上去
            setTimeout(function () {
                form.on('select(implement)', function () {
                    var implementName = $(this).closest(".layui-input-block").find(".implement option:selected").attr("data-name");
                    disburseAttributionDepartment.val(implementName);
                });
                form.on('select(department)', function () {
                    var departmentName = $(this).closest(".layui-input-block").find(".department option:selected").attr("data-name");
                    disburseAttributionDepartment.val(departmentName);
                });
            }, 200);

        });
    }
    // 4 提交
    let status = false;
    $(document).on("click", ".submit-btn", function () {
        let type = $('#expense_radio').find(".layui-form-radioed").prev("input").val();
        if (type === "项目支出") {
            // 内容清空
            // let earningsCompany_div = $(".earningsCompany-div");
            // earningsCompany_div.find("select").val("");
            // earningsCompany_div.find("select").removeClass("need-input");
            let content_div = $(".content-div");
            content_div.find("textarea").val("");
            content_div.find("textarea").removeClass("need-input");
        }
        // ①表单校验
        // 此处校验基本信息  引用 xudazhu.js
        if (!formChecking() || status) {
            return false;
        }
        status = true;
        if ($(".implement").val() === '' && $(".department").val() === '') {
           $(".implement").val(implementId);
        }
        if(state == ""){
            state = 1;
        }
        let method = getParamForUrl("id") === null ? "POST" : "PUT";

        var projectNum = $("#inputForm").find(".layui-card-body").find(".projectNum");
        var projectName = $("#inputForm").find(".layui-card-body").find(".projectName");
        projectNum.remove(); // 删除projectNum
        projectName.remove(); // projectName paymentAmount
        let itemData = $.param({"_method": method,"paymentStatusBean.paymentStatusId":state,}) + "&" + $("#inputForm").serialize();
        $.post("/payment", itemData, function (data) {
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
                status = false;
            }
        }, "json").fail(function (res) {
            layer.msg('数据提交失败 请刷新重试', {icon: 5});
            console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
            status = false;
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

    // 录入 id 为null way="add"
    if (id === null && way === "add") {
        $(".paymentStatus").val("1");
        $(".confirm-select").remove();
        $(".audit-div").remove();
        $(".pay-div").remove();
        $(".confirm-div").hide();
    }

    //  添加银行卡
    $(document).on("click", ".add-implement-btn", function () {
        console.log(11);
        var Bank = $(this).attr("data-name"); //要打开页面的地址
        if(Bank == "Bank"){
            var bankStr = ".layui-paymentAudit-add";
        }else if(Bank == "information"){
            var bankStr = ".add-implement-div";
        }
        // 添加实施部 限制
        var length = $(".add-implement-div").children(".layui-form-item").length;
        var idTemp = Math.ceil(Math.random() * 10000);
        $(this).parent().children(bankStr).append(
            '<div class="layui-row layui-col-space5 layui-form-item">'
            + '<div class="layui-col-sm4 bankCard-div">'
            + '<label class="layui-form-label" style="width: 104px;padding: 9px 2px;">*银行卡：</label>'
            + '<div class="layui-input-block">'
            + '<select id="select-' + idTemp + '" class="bankCardId need-input" name="bankCardAllotBeans[' + length + '].bankCardBean.bankCardId" title="" lay-filter="bankCardId" autocomplete="off"  lay-search>'
            + '<option></option>'
            + '</select>'
            + '<input type="text" name="bankCardAllotBeans[' + length + '].bankCardAllotId" hidden="hidden">'
            + '</div>'
            + '</div>'
            + '<div class="layui-col-sm4 bankCard-div">'
            + '<label class="layui-form-label" style="width: 104px;padding: 9px 2px;">*支付金额：</label>'
            + '<div class="layui-input-block">'
            //+ '<select id="implementHead-' + idTemp + '" name="implementBeans[' + length + '].implementImplementHead" title="" class="userName implementImplementHead" lay-filter="userName" lay-search></select>'
            + '<input name="bankCardAllotBeans[' + length + '].bankCardAllotBankCardMoney" autocomplete="off" placeholder="请输入" class="layui-input need-input" >'
            + '</div>'
            + '</div>'
            + '<div class="layui-col-sm4 bankCard-div">'
            + '<label class="layui-form-label" style="width: 104px;padding: 9px 2px;">*支付时间：</label>'
            + '<div class="layui-input-block">'
            //+ '<select id="implementHead-' + idTemp + '" name="implementBeans[' + length + '].implementImplementHead" title="" class="userName implementImplementHead" lay-filter="userName" lay-search></select>'
            + '<input type="text" name="bankCardAllotBeans[' + length + '].bankCardAllotTime" placeholder="点击选择时间"  class="test-item layui-input need-input">'
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
    $(document).on("click", ".add-implement-div .layui-icon-delete,.layui-paymentAudit-add .layui-icon-delete", function () {
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

   // 班组老板数据
   $.ajax({
       url: '/squad_group_fee/amount',
       type: "get",
       dataType: "json",
       success: function (obj) {
             var data = obj.data;
             htmlBook = "";
             for (var i = 0; i < data.length; i++) {  //
                htmlBook+='<option value='+data[i].squadGroupFeeId+'>'+data[i].squadGroupFeeName+'</option>'
             }
              $(".layui-teamBoss").append(htmlBook); //计量单位
//              if(professionUnitId){
//                  $('select[name="professionUnitBean.professionUnitId"]').val(professionUnitId); // 获取下拉的name值，用val渲染
//               }
              layui.use(['form'], function () {
                  var form = layui.form;
                  layui.form.render('select');
              });
       }
    });
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
});

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

        renderFix("bankCardId");
    }
}

// 方法0 改变付款申请类型时,发生的变化
function changeType(type, departmentInfo) {
    var inputForm = $("#inputForm");
    inputForm.show(100);
    showDisburseDetail($("#disburse_detail"), type, "付款申请单");//铺费用明细
    var projectNum = $(".projectNum-div");
    var projectName = $(".projectName-div");
    var content = $(".content-div");
    var earningsCompany = $(".earningsCompany-div");

    var implementSelect = $(".implement");
    var implement = implementSelect.next(".layui-form-select");
    var departmentSelect = $(".department");
    var department = departmentSelect.next(".layui-form-select");
    if (type === "部门支出" || type === "公司支出") {
        projectNum.hide();
        projectName.hide();
        implement.hide();

        department.show();
        departmentSelect.removeClass("department-hidden");// 组织架构的部门显示
        implementSelect.addClass("implement-hidden");//实施部(即子项目)隐藏
        content.show();
        earningsCompany.show();
    } else if (type === "项目支出") {
        projectNum.show();
        projectName.show();
        implement.show();
        departmentSelect.hide();
        departmentSelect.addClass("department-hidden");// 组织架构的部门隐藏
        implementSelect.removeClass("implement-hidden");//实施部(即子项目)显示
        content.hide();
        // earningsCompany.hide();
    }
    // 2 铺付款单位
    showSecondParty($("#secondParty"));
    // 3 铺项目信息
    showProjectInfo(inputForm);
    // 6 铺收益单位
    showEarningsCompany($("#earningsCompany"));
    // 8 铺组织架构的部门
    showdepartment(departmentSelect, departmentInfo);
}

// 方法1 铺费用明细
function showDisburseDetail(dom, disburseCategoryName, disburseDetailSource) {
    $.get("/disburse_detail", {
        "disburseTypeBean.disburseCategoryBean.disburseCategoryName": disburseCategoryName,
        "disburseDetailSource": disburseDetailSource
    }, function (data) {
        dom.empty();
        dom.append(
            '<option></option>'
        );
        $(data["content"]).each(function (index, content) {
            let type = content["disburseTypeBean"] !== null ? content["disburseTypeBean"]["disburseTypeName"] : "";
            dom.append(
                '<option data-type="'+type+'" value="' + content["disburseDetailId"] + '">' + content["disburseDetailName"] + '</option>'
            )
        });
        renderFix("disburseDetail");
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
        renderFix("secondPartyId");
    }, "json");
}

// 3 项目编号 项目名称
let projectInfo = null;

function showProjectInfo(divDom) {
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
                showTemp();
            });
    } else {
        // 调用 铺项目数据 的方法
        showTemp();
    }

    function showTemp() {
        let projectNum = divDom.find("select.projectNum");
        let projectName = divDom.find("select.projectName");
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
        setTimeout(function () {
            $("select.projectNum + .layui-form-select dd , select.projectName + .layui-form-select dd").click(function () {
                let myDDs = $("select.projectNum + .layui-form-select dd"),
                    heDDs = $("select.projectName + .layui-form-select dd");
                if ($(this).closest(".layui-form-select").prev().hasClass("projectName")) {
                    let temp = myDDs;
                    myDDs = heDDs;
                    heDDs = temp;
                }
                // let trDomTemp = $(this).closest("tr");
                let thisTemp = this; // 当前点击的dd
                myDDs.each(function (index, ddTemp) {
                    if (ddTemp === thisTemp) {// 如果遍历出来的ddTemp和当前点击的thisTemp相等
                        heDDs.get(index).click();// 另外一个dd(下标相同)则点击
                        let selectTemp = divDom.find("select.implement");//找到需要铺实施部门的select框
                        selectTemp.empty();
                        let projectIdTemp = divDom.find("select.projectNum").val();// 当前当前点击的项目的id
                        let earningsCompanyId = $("select.projectNum option:selected").attr("data-earningsCompanyId");// d当前点击项目的收益单位
                        // 铺对应收益单位数据
                        $(".earnings_company").next(".layui-form-select").find("dd[lay-value='" + earningsCompanyId + "']").click();
                        selectTemp.append("<option></option>");
                        $(projectInfo).each(function (index, projectTemp) {// 对所有获取到的项目信息做个遍历
                            if (projectTemp[0] === parseInt(projectIdTemp)) { //projectTemp[0]为项目id
                                let deptIds = projectTemp[3].split("$,"); // deptIds为所有实施部的id  ["3", "2$"]
                                let deptNames = projectTemp[4].split("$,");// deptIds为所有实施部的名字 ["勘察部", "咨询部$"]
                                // 铺该项目对应的所有实施部
                                $(deptIds).each(function (index, deptId) {
                                    selectTemp.append("<option value='" + deptId.replace(/[$]/g, "") + "' data-name='" + deptNames[index].replace(/[$]/g, "") + "'>" + deptNames[index].replace(/[$]/g, "") + "</option>");
                                });
                            }
                        });
                        renderFix("implement");
                    }
                });
            });
            showData4Object(disburse);

            if (id !== null) {
                if (way === "audit" || way === "confirm" || way === "pay" || way === "paid" || way === null) {
                    convertSelectToText();
                    convertProjectText(projectNum);
                    convertProjectText(projectName);
                    $(".myClass").find(".layui-form-select").each(function () {
                        if ($(this).css("display") !== "none") {
                            let value = $(this);
                            value.after("<p>" + value.find("dd.layui-this").text() + "</p>");
                            value.remove();
                        }
                    });
                    if (way === "paid") {
                        bankCard()
                    }
                }
            }
        }, 1000);
    }
}
// 方法5 铺收益单位
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
        renderFix("earningsCompany");
    }, "json");
}

// 方法6 铺组织架构的部门
function showdepartment(dom, departmentInfo) {
    $.get("/department/company_department", {}, function (data) {
        dom.empty();
        dom.append(
            '<option></option>'
        );
        $(data).each(function (index, content) {
            if (content["departmentName"] === "实施部") {
                return;
            }
            dom.append(
                '<option value="' + content["departmentId"] + '" data-name="' + content["departmentName"] + '">' + content["departmentName"] + '</option>'
            )
        });

        renderFix("department", function () {
            dom.next(".layui-form-select").find("dd[lay-value='" + departmentInfo + "']").click();
            if (id !== null) {
                if (way === "confirm" || way === "audit" || way === "pay" || way === "paid" || way === null) {
                    $(".myClass").find(".layui-form-select").each(function () {
                        if ($(this).css("display") === "block") {
                            let value = $(this);
                            if(value.find("dd.layui-this").text() !== ""){
                                value.after("<p>" + value.find("dd.layui-this").text() + "</p>");
                                value.remove();
                            }
                        }
                    });
                    if (way === null) {
                        bankCard();
                    }
                }

            }
        });
    }, "json");
}

// 方法7 转化成text(仅限不需要铺数据的select和简单的input和textarea)
function convertText() {
    $("#sumCard .show-text").each(function () {
        let thisVal = $(this).val();
        let thisParent = $(this).closest(".layui-input-block");
        thisParent.empty();
        thisParent.append('<p></p>');
        thisParent.find("p").text(thisVal);
    });
}

// 转化成text(仅限于铺数据的select)
function convertSelectToText() {
    $("#sumCard select[name]").each(function (index3, dom) {
        if ($(dom).hasClass("department") || $(dom).hasClass("implement")) {
            return;
        }
        let thisVal = $(dom).next(".layui-form-select").find("dd.layui-this").text();
        let thisParent = $(this).closest(".layui-input-block");
        thisParent.empty();
        thisParent.append('<p></p>');
        thisParent.find("p").text(thisVal);
    })
}

// 转化成text(仅限项目编号和项目名称)
function convertProjectText(dom) {
    let thisVal = dom.next(".layui-form-select").find("dd.layui-this").text();
    let thisParent = dom.closest(".layui-input-block");
    thisParent.empty();
    thisParent.append('<p></p>');
    thisParent.find("p").text(thisVal);
}

function convertTextNum(dom) {
    let thisValue = dom.val();
    dom.after('<p></p>');
    dom.next("p").text(thisValue);
    dom.remove();
}

function bankCard() {
    // 铺付款银行卡数据
    let bankCardId = $(".bankCardId");
    if (bankCardId.val() !== "") {
        let tempVal = bankCardId.next(".layui-form-select").find("dd.layui-this").text();
        bankCardId.closest(".layui-input-block").html('<p>' + tempVal + '</p>')
    }
}

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
        if (way === "audit" || way === "detail" || way === "print") {//审核 详情 打印
            userName.closest(".layui-input-block").html('<p>' + defaultValue + '</p>');
        }
    }

}
