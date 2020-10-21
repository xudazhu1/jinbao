
let id = getParamForUrl("id");
let way = getParamForUrl("way");
let income = null;

let secondPartyBeans = null;

function showSecondPartyBeans( layFilter , rollback ) {
    //获取收款单位
    if ( secondPartyBeans === null ) {
        $.get("/table_utils" ,
            {"table_utils.tableName" : "second_party" , "table_utils.fields" : "secondPartyId$secondPartyName"} ,
            showTemp , "json" );
    } else {
        showTemp(secondPartyBeans);
    }

    function showTemp(data) {
        secondPartyBeans = data;
        let secondPartySelect = $("select[lay-filter='"+layFilter+"']");
        secondPartySelect.empty();
        secondPartySelect.append("<option></option>");
        $(data.content).each(function (index, secondPartyTemp) {
            secondPartySelect.append(
                "<option value='"+secondPartyTemp[0]+"' >"+secondPartyTemp[1]+"</option>"
            );
        });
        renderFix(layFilter , rollback );
    }

}
let bankCardBeans = null;
function showBankCardId( layFilter , rollback ) {
    //获取银行卡
    if ( bankCardBeans === null ) {
        $.get("/table_utils" ,
            {"table_utils.tableName" : "bank_card" , "table_utils.fields" : "bankCardId$bankCardName"} ,
            showTemp , "json" );
    } else {
        showTemp(bankCardBeans);
    }

    function showTemp ( data ) {
        bankCardBeans = data;
        let bankCardBeanSelect = $("select[lay-filter='"+layFilter+"']");
        bankCardBeanSelect.empty();
        bankCardBeanSelect.append("<option></option>");
        $(data.content).each(function (index, bankCardBeanTemp) {
            bankCardBeanSelect.append(
                "<option value='"+bankCardBeanTemp[0]+"' >"+bankCardBeanTemp[1]+"</option>"
            );
        });
        renderFix(layFilter , rollback);
    }

}


$(function () {
    $("body").addClass(way);
    fixWay(way);
    //项目名称只读
    $("[lay-filter='projectName']").attr("readonly" , "readonly");
    //如果是详情 移除添加实际回款按钮
    if (way === null || way === "info" ) {
        $(".add_actual_income").remove();
    }

    if ( way === "add" ) {
        $(document).on("keyup" , "[name]" , function () {
            let invoiceAuditStatusSelector = $("[name='incomeAuditStatus']" );
            invoiceAuditStatusSelector.val("0");
            renderFix("income");
            invoiceAuditStatusSelector.next(".layui-form-select").find("input.layui-unselect").val("提交后待审核");
        });

    }

    //获取银行卡
    showBankCardId("a");

    layui.use(['element', 'laydate', 'layer', 'form'], function () {
        // noinspection JSUnusedLocalSymbols
        let element = layui.element;
        let laydate = layui.laydate;
        // noinspection JSUnusedLocalSymbols
        let layer = layui.layer,
            $ = layui.jquery,
            form = layui.form;
        $(".lay_date").each(function () {
            laydate.render({
                elem: this , //指定元素
                type: 'datetime' ,
                done : function () {
                    if ( way === "add" ) {
                        let incinomeAuditStatusSelector = $("[name='incomeAuditStatus']" );
                        incinomeAuditStatusSelector.val("0");
                        renderFix("income");
                        incinomeAuditStatusSelector.next(".layui-form-select").find("input.layui-unselect").val("提交后待审核");
                    }
                }
            });
        });
        layui.form.render('select');
        //如果选通过 把所有子回款通过
        $("[name='incomeAuditStatus'] + .layui-form-select dd").click(function () {
            if ( $(this).attr("lay-value") === "1" ) {
                $(".moneyBackAuditStatus + .layui-form-select dd[lay-value='1']").click();
            }
        });
        if ( way === "add" ) {
            layui.form.on('select', function(){
                let invoiceAuditStatusSelector = $("[name='incomeAuditStatus']" );
                invoiceAuditStatusSelector.val("0");
                renderFix("incomeAuditStatus");
                invoiceAuditStatusSelector.next(".layui-form-select").find("input.layui-unselect").val("提交后待审核");
            });
        }

        let tips;
        $(document).on("mouseenter" , "[name='invoiceBean.invoiceRate']" , function () {
            tips =layer.tips("<span style='color: #2E2D3C;'>税率请输入小数:如 5%为0.05 , 12%为0.12 , 100%为1</span>",this,{tips:[1,'#CBCBD3']});
        });
        $(document).on("mouseleave" , "[name='invoiceBean.invoiceRate']" , function () {
            layer.close(tips);
        });

    });

    //铺项目编号项目名称
    $.get("/table_utils" ,
        {"table_utils.tableName" : "project" , "table_utils.fields" : "projectId$projectNum$projectName"} ,
        function (data) {
            let projectInfo = data.content;
            let projectNum = $("select.projectNum");
            let projectName = $("select.projectName");
            projectNum.empty();
            projectName.empty();
            projectNum.append("<option></option>");
            projectName.append("<option></option>");
            $(projectInfo).each(function (index , projectTemp) {
                projectNum.append("<option value='"+projectTemp[0]+"'>"+projectTemp[1]+"</option>");
                projectName.append("<option value='"+projectTemp[2]+"'>"+projectTemp[2]+"</option>");
            });
            if ( income !== null ) {
                try {
                    projectNum.val( income.projectBean.projectId );
                    projectName.val( income.projectBean.projectName );
                } catch (e) {
                }
            }
            renderFix("projectNum");
            renderFix("projectName" , function () {
                // $("select.projectNum + .layui-form-select dd  ").click(function () {
                layui.form.on("select(projectNum)" , function (  ) {

                    let myDDs = $("select.projectNum + .layui-form-select dd") , heDDs = $("select.projectName + .layui-form-select dd");
                    // if ( $(this).closest(".layui-form-select").prev().hasClass("projectName") ) {
                    //     let temp = myDDs;
                    //     myDDs = heDDs;
                    //     heDDs = temp;
                    // }
                    myDDs.each(function (index , ddTemp) {
                        if ( $(ddTemp).hasClass("layui-this")  ) {
                            heDDs.get(index).click();
                        }
                    });
                    //获取收益单位
                    if ( id === null || id === "null") {
                        $.get("/table_utils" ,
                            {"table_utils.tableName" : "project" , "table_utils.fields" : "earningsCompanyBean.earningsCompanyId" , "$D.projectId" : $("[name='projectBean.projectId']").val() } ,
                            function (data) {
                                let earningsCompanySelect = $("select[lay-filter='earningsCompanyId']");
                                earningsCompanySelect.next().find("dd[lay-value='"+data.content[0]+"']").click();
                            } , "json" );
                    }

                });
                // noinspection JSValidateTypes
                $("select.projectNum + .layui-form-select dd.layui-this").click();
                // noinspection JSValidateTypes
                // heDDs.closest("dl").children("dd.layui-this").click();
            } );
        } , "json");

    //提交
    $(".confirm-btn").click(function () {

        // //如果是退回到开票审核
        // if ( $("[name='invoiceBean.invoiceAuditStatus']").val() === "0" ) {
        //     $.post("/table_utils" ,
        //         { tableName : "income" , "_method": "POST" ,
        //             incomeId: $("[name='incomeId']").val()
        //         , "invoiceBean.invoiceAuditStatus" : "0"
        //         , "invoiceBean.invoiceId" : $("[name='invoiceBean.invoiceId']").val( ) } , function (data) {
        //         if (data) {
        //             try {
        //                 window.parent.flush();
        //             } catch (e) {
        //             }
        //             layer.msg('退回成功 ', {
        //                 time: 2000 //2秒关闭（如果不配置，默认是3秒）
        //             } ,  function () {
        //                 // window.location.reload();
        //                 try {
        //                     window.parent.layer.close( window.parent.layerIndex );
        //                 } catch (e) {
        //                 }
        //             });
        //         } else {
        //             layer.msg('操作失败',{icon:5});
        //         }
        //     }, "json").fail(function (res) {
        //         layer.msg('数据提交失败 请刷新重试',{icon:5});
        //         console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
        //     }, "json");
        //
        //     return;
        // }

        //先删除
        if ( removeMoneyBackIds.length > 0 ) {
            $.post("/table_utils/id" , { tableName : "money_back" , "_method": "DELETE" , "ids" : removeMoneyBackIds.toString() } , function (data) {
                if ( data ) {
                    layer.msg('删除回款成功');
                }
            } , "json" );
        }

        let dataR = $.param({ tableName : "income" , "_method": "POST" , "createUserBean.userId" : window.top.userTemp.userId ,
            // "managementCreateTime" : formatDate(new Date() , 8 ) ,
            "createJobBean.jobId" : window.top.userTemp["jobBean"]["jobId"] }) + "&" + $("#inputForm").serialize();
        $.post("/income" , dataR , function ( data ) {
            if (data) {
                try {
                    window.parent.flush();
                } catch (e) {
                }
                layer.msg('操作成功 ', {
                    time: 2000 //2秒关闭（如果不配置，默认是3秒）
                } ,  function () {
                    // window.location.reload();
                    try {
                        window.parent.layer.close( window.parent.layerIndex );
                    } catch (e) {
                    }
                });
            } else {
                layer.msg('操作失败',{icon:5});
            }
        }, "json").fail(function (res) {
            layer.msg('数据提交失败 请刷新重试',{icon:5});
            console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
        }, "json");
    });

    //如果是录入 注册收入类型事件
    renderFix("incomeType" , function () {
        $("select.incomeType + .layui-form-select dd").click(function () {
            let value = $(this).attr("lay-value");
            if ( value !== "项目收入-项目收入" && value !== "其他收入-回款亏损费" && value !== "其他收入-施工亏损费" ) {
                $(".projectNum-elem").hide();
                $("#my-projectNum").val("");
                $("#my-projectName").val("");
                renderFix("projectNum");
                renderFix("projectName");
            } else {
                $(".projectNum-elem").show();
            }
            if ( id == null ) {
                $("#actual_income_body").empty();
            }
            if ( value !== "项目收入-项目收入" ) {
                if ( id == null ) {
                    $(".add_actual_income").hide().click();
                }
                // $(this).closest(".layui-card").find("[name]:not(.projectNum , .incomeType , [name='incomeRemark'] )").attr("readonly" , "readonly");
                $(".income-type-other-hide").show();
                $( "form input.add-temp" ).remove();
            } else {
                $( "form" ).append(
                    "<input type='hidden' class='add-temp' name='invoiceBean.invoiceAuditRemark' value=' / ' >"
                );
                $(".add_actual_income").show();
                // $(this).closest(".layui-card").find("[name]:not(.projectNum , .incomeType , [name='incomeRemark'] )").removeAttr("readonly");
                $(".income-type-other-hide").show();
            }

        });
    });

    if (  id != null  &&  id !== "null" ) {
        //如果有 id 铺数据
        $.get("/table_utils/info" ,
        {incomeId : id , "table_utils.tableName" : "income" } , function (data) {
                income = data.content[0];
                showData4Object(income);

                //收益单位
                try {
                    if ( $.isEmptyObject( income.earningsCompanyBean ) || income.earningsCompanyBean === ""  ) {
                        //如果没有收益单位 填入开票的收益单位
                        showEarningsCompanyId( income["invoiceBean"].earningsCompanyBean.earningsCompanyId );
                    } else {
                        showEarningsCompanyId( income.earningsCompanyBean.earningsCompanyId );
                    }
                } catch (e) {
                }
                //如果没有收款单位 填入开票的收款单位
                // 收款单位
                try {
                    showSecondPartyBeans("secondPartyId" , function () {
                        if ( $.isEmptyObject( income.secondPartyBean ) ||  income.secondPartyBean === "" ) {
                            //如果没有收益单位 填入开票的收益单位
                            $("[lay-filter='secondPartyId'] + .layui-form-select dd[lay-value='"+income["invoiceBean"].secondPartyBean.secondPartyId+"'] ").click();
                        } else {
                            $("[lay-filter='secondPartyId'] + .layui-form-select dd[lay-value='"+income.secondPartyBean.secondPartyId+"'] ").click();
                        }
                    });
                } catch (e) {
                }

                let incomeTypeDom = $("select.incomeType");
                let value = incomeTypeDom.val();
                if ( value !== "项目收入-项目收入" && value !== "其他收入-回款亏损费" && value !== "其他收入-施工亏损费" ) {
                    $(".projectNum-elem").hide();
                    $("#my-projectNum + .layui-form-select .layui-select-tips").click();
                    $("#my-projectName + .layui-form-select .layui-select-tips").click();
                } else {
                    $(".projectNum-elem").show();
                }
                // $("#actual_income_body").empty();
                if ( value !== "项目收入-项目收入" ) {
                    // $(".add_actual_income").hide().click();
                    // $(this).closest(".layui-card").find("[name]:not(.projectNum , .incomeType , [name='incomeRemark'] )").attr("readonly" , "readonly");
                    $(".income-type-other-hide").show();
                    $( "form input.add-temp" ).remove();
                    $(".invoice-info").hide();
                } else {
                    incomeTypeDom.attr( "readonly" , "readonly" );
                    $( "form" ).append(
                        "<input type='hidden' class='add-temp' name='invoiceBean.invoiceAuditRemark' value=' / ' >"
                    );
                    $(".add_actual_income").show();
                    // $(this).closest(".layui-card").find("[name]:not(.projectNum , .incomeType , [name='incomeRemark'] )").removeAttr("readonly");
                    $(".income-type-other-hide").show();
                }


            // if ( id  === null  || id === "null" ||  income.incomeNum === "" || income.incomeNum.length < 7 ) {
            //     getIncomeNum();
            // }
            if ( id != null  &&  id !== "null") {
                $(income["moneyBackBeans"]).each(function (index , moneyBackBean) {
                    createMoneyBack(moneyBackBean);
                });
            }

        } , "json" );
    } else {
        //收益单位
        showEarningsCompanyId();
        // 收款单位
        showSecondPartyBeans("secondPartyId");
        // getIncomeNum();
        $(".invoice-info").hide();
    }


    //实际回款的添加
    $(".add_actual_income").click(function () {
        createMoneyBack();
    });

    //统计总实际回款
    $(document).on("keyup" , ".moneyBackMoney" , function () {
        let countMoneyBackMoney = 0.00;
        $(".moneyBackMoney").each(function () {
            if ( this.value !== "" ) {
                countMoneyBackMoney += parseFloat(this.value);
            }
        });
        $("[name='incomeCountMoneyBackMoney']").val(countMoneyBackMoney);
        //如果回款金额没填或者小于总实际回款 帮忙填上总额
        let incomeMoney = $("[name='incomeMoney']");
        if ( incomeMoney.val() === "" || parseInt( incomeMoney.val() ) < countMoneyBackMoney ) {
            incomeMoney.val( countMoneyBackMoney.toFixed(2) );
        }
        let incomeType = $(".incomeType").val();
        if ( incomeType !== "项目收入-项目收入" ) {
            incomeMoney.val( countMoneyBackMoney.toFixed(2) );
        }
    });

    //删除实际回款按钮
    $(document).on("click" , ".money-back-del-btn" , function () {
    // $(".money-back-del-btn").click(function () {
        let moneyBackIdTemp = $(this).closest("tr").find(".moneyBackId").val();
        if ( moneyBackIdTemp !== "" ) {
            removeMoneyBackIds.push(moneyBackIdTemp);
        }
        $(this).closest("tr").remove();
        renderIndex4MoneyBack();
    });

});
let removeMoneyBackIds = [];



//获取收益单位的方法
let earningsCompany = null;
function showEarningsCompanyId( defaultValue  , rollback ) {
    if ( earningsCompany == null ) {
        $.get("/table_utils" ,
            {"table_utils.tableName" : "earnings_company" , "table_utils.fields" : "earningsCompanyId$earningsCompanyName"} ,
            showTemp , "json" );
    } else {
        showTemp( earningsCompany );
    }
    function showTemp( data ) {
        earningsCompany = data;
        let earningsCompanySelect = $("select[lay-filter='earningsCompanyId']");
        earningsCompanySelect.empty();
        earningsCompanySelect.append("<option></option>");
        $(data.content).each(function (index, earningsCompanyTemp) {
            earningsCompanySelect.append(
                "<option value='"+earningsCompanyTemp[0]+"' >"+earningsCompanyTemp[1]+"</option>"
            );
        });
        if ( typeof defaultValue !== "undefined"  ) {
            earningsCompanySelect.val( defaultValue );
        }
        renderFix("earningsCompanyId" , rollback );
    }
}

//获取流水编号
// function getIncomeNum() {
//     $.get("/auto_num/income" , function (data) {
//         $("[name='incomeNum']").val("SR" + new Date().getFullYear().toString().substring(1) + data);
//     });
// }

function createMoneyBackNum() {
    let maxNum = 0;
    $(".moneyBackNum").each(function (index, moneyBackNum) {
        let val = $(moneyBackNum).val();
        let numTemp = parseInt( val.substring(val.indexOf("-")+1 ) );
        if ( numTemp > maxNum ) {
            maxNum = numTemp;
        }
    });
    let returnData = (maxNum + 1).toString();
    return returnData.length === 1 ? "0" + returnData : returnData;
    
}


function createMoneyBack(moneyBackBean) {
    if (moneyBackBean === undefined ) moneyBackBean = {};
    let incomeNum = $("input[name='incomeNum']").val();
    if ( $.isEmptyObject(moneyBackBean) ) {
        moneyBackBean.moneyBackNum = incomeNum === "" ? "自动" : incomeNum + "-" + createMoneyBackNum();
        moneyBackBean.moneyBackId = "";
        moneyBackBean.moneyBackDate = "";
        moneyBackBean.moneyBackType = "";
        moneyBackBean.moneyBackMoney = "";
        moneyBackBean["secondPartyBean"] = {};
        moneyBackBean["secondPartyBean"].secondPartyId = "";
        moneyBackBean["bankCardBean"] = {};
        moneyBackBean["bankCardBean"].bankCardId = "";
        moneyBackBean.moneyBackRemark = "";
        moneyBackBean.moneyBackAuditRemark = "";
    }

    let tempId = Math.ceil(Math.random()*100000);

    let tBodyTemp = $("#actual_income_body");
    tBodyTemp.append(
        '<tr>' +
        '<td>' +
            '<span><input class="layui-input moneyBackNum" readonly name="moneyBackBeans[0].moneyBackNum" value="'+moneyBackBean.moneyBackNum+'" ></span>' +
            '<input class="layui-input moneyBackId" type="hidden" name="moneyBackBeans[0].moneyBackId" value="'+moneyBackBean.moneyBackId+'"  />' +
        '</td>\n' +
        '<td>' +
        '<!--suppress HtmlUnknownAttribute -->' +
        '<select class="money_back_type" name="moneyBackBeans[0].moneyBackType" lay-filter="type'+tempId+'" >' +
        '<option value="">选择回款方式</option>' +
        '<option value="公对公">公对公</option>\n' +
        '<option value="公对私">公对私</option>\n' +
        '<option value="私对公">私对公</option>\n' +
        '<option value="私对私">私对私</option>' +
        '</select>' +
        '</td>\n' +
        '<td>' +
        '   <input name="moneyBackBeans[0].moneyBackDate"  value="'+moneyBackBean.moneyBackDate+'"  type="text" autocomplete="off" class="lay_date layui-input">' +
        '   <!--suppress HtmlUnknownAttribute -->' +
        '</td>\n' +
        '<td>' +
        '<input name="moneyBackBeans[0].moneyBackMoney" value="'+moneyBackBean.moneyBackMoney+'" type="number" class="layui-input moneyBackMoney" placeholder="" />' +
        '</td>\n' +
        '<td>' +
        '<!--suppress HtmlUnknownAttribute -->' +
        '<select class="moneyBackSecondPartyId" name="moneyBackBeans[0].secondPartyBean.secondPartyId" lay-filter="secondPartyId'+tempId+'" >' +
        '<option value=""></option>' +
        '</select>' +
        '</td>\n' +
        '<td>' +
        '<!--suppress HtmlUnknownAttribute -->' +
        '<select class="moneyBackBankCard" name="moneyBackBeans[0].bankCardBean.bankCardId" lay-filter="bankCardId'+tempId+'" >' +
        '<option value=""></option>' +
        '</select>' +
        '</td>\n' +
        '<td>' +
        '<input name="moneyBackBeans[0].moneyBackRemark" value="'+moneyBackBean.moneyBackRemark+'" type="text" class="layui-input " placeholder="" />' +
        '</td>\n' +
        '<td class="actual_income_audit_status hidden-dom" >' +
        '<input class="layui-input moneyBackAuditRemark audit-only-write" readonly value="'+moneyBackBean.moneyBackAuditRemark+'" name="moneyBackBeans[0].moneyBackAuditRemark"  >' +
        '</td>\n' +
        '<td class="actual_income_audit_remark hidden-dom" >' +
        '<!--suppress HtmlUnknownAttribute -->' +
        '<select class="moneyBackAuditStatus audit-only-write" readonly="readonly" name="moneyBackBeans[0].moneyBackAuditStatus" lay-filter="moneyBackAuditStatus'+tempId+'" >' +
        '<option></option>' +
        '<option value="1">通过</option>' +
        '<option value="2">退回</option>' +
        '</select> ' +
        '</td>\n' +
        '<td  class="do-td add-only-show">\n' +
        // '    <button>\n' +
        // '        <span class="layui-icon layui-icon-ok ok-btn new-add" title="添加" style="margin-right: 10px;color: #009688"></span>\n' +
        // '    </button>\n' +
        '    <button type="button" class="money-back-del-btn">\n' +
        '        <span class="layui-icon layui-icon-delete " data-id="2" style="color: #ff4646" title="删除"></span>\n' +
        '    </button>\n' +
        '</td>' +
        '</tr>'
    );
    $("[lay-filter='type"+tempId+"']").val(moneyBackBean.moneyBackType);
    $("[lay-filter='moneyBackAuditStatus"+tempId+"']").val(moneyBackBean["moneyBackAuditStatus"]);
    renderFix("type" +tempId , function (layFilter) {
        // $("[lay-filter='"+layFilter+"'] + .layui-form-select dd ").click(function () {
        //     let incomeWay =  $("[name='incomeWay']");
        //     if ( incomeWay.val() === "" ) {
        //         incomeWay.next().find("dd[lay-value='"+$(this).attr("lay-value")+"']").click();
        //     }
        //     let incomeType = $(".incomeType").val();
        //     if ( incomeType !== "项目收入-项目收入" ) {
        //         incomeWay.next().find("dd[lay-value='"+$(this).attr("lay-value")+"']").click();
        //     }
        // });
    });
    renderFix("moneyBackAuditStatus" +tempId , function () {
        $("[lay-filter='moneyBackAuditStatus"+tempId+"'] + .layui-form-select dd").click(function () {
            if ( $(this).attr( "lay-value" ) === "2"  ) {
                $("[name='incomeAuditStatus'] + .layui-form-select dd[lay-value='2']").click();
            }
        });
    });

    showBankCardId("bankCardId" +tempId , function () {
        $("[lay-filter='bankCardId"+tempId+"'] + .layui-form-select dd[lay-value='"+moneyBackBean.bankCardBean.bankCardId+"']").click();
    });
    showSecondPartyBeans("secondPartyId" +tempId , function () {
        $("[lay-filter='secondPartyId"+tempId+"'] + .layui-form-select dd[lay-value='"+moneyBackBean.secondPartyBean.secondPartyId+"']").click();
    });
    renderFix("estimation" +tempId);
    renderIndex4MoneyBack();
    fixWay(way);
    layui.use([ 'layer', 'laydate' ,  'form'],function () {
        let layDate = layui.laydate;
        $("#actual_income_body .lay_date").each(function () {
            layDate.render({
                elem: this , //指定元素
                type: 'datetime' ,
                done: function(value ){
                    // console.log(value); //得到日期生成的值，如：2017-08-18
                    // console.log(date); //得到日期时间对象：{year: 2017, month: 8, date: 18, hours: 0, minutes: 0, seconds: 0}
                    // console.log(endDate); //得结束的日期时间对象，开启范围选择（range: true）才会返回。对象成员同上。
                    //同步回款时间
                    // let incomeDate  = $("[name='incomeDate']");
                    // if ( incomeDate.val().length < 10  ) {
                    //     incomeDate.val( value );
                    // }
                    // let incomeType = $(".incomeType").val();
                    // if ( incomeType !== "项目收入-项目收入" ) {
                    //     incomeDate.val( value );
                    // }
                    // if ( way === "add" ) {
                    //     let invoiceAuditStatusSelector = $("[name='incomeAuditStatus']" );
                    //     invoiceAuditStatusSelector.val("0");
                    //     renderFix("income");
                    //     invoiceAuditStatusSelector.next(".layui-form-select").find("input.layui-unselect").val("提交后待审核");
                    // }
                }
            });
        });
    });
}

function renderIndex4MoneyBack() {
    // let moneyBackMoney = $(".moneyBackMoney");
    $(".moneyBackMoney").each(function (index , moneyBackMoney ) {
        let trTemp = $(moneyBackMoney).closest("tr");
        trTemp.find("[name]").each(function () {
            let nameTemp = $(this).prop("name");
            if ( nameTemp.indexOf("[") !== -1 ) {
                // noinspection RegExpRedundantEscape
                $(this).prop("name" , nameTemp.replace(/\[\d+\]/ , "["+index+"]"));
            }
        });

    });
}

// function renderFixed(layFilter) {
//     if ( way === "add" ) {
//         setTimeout(function () {
//             $("select[lay-filter='"+layFilter+"'] + .layui-form-select dd").click(function () {
//                 let invoiceAuditStatusSelector = $("[name='incomeAuditStatus']" );
//                 invoiceAuditStatusSelector.val("0");
//                 renderFix("incomeAuditStatus");
//                 invoiceAuditStatusSelector.next(".layui-form-select").find("input.layui-unselect").val("提交后待审核");
//             });
//         } , 200);
//     }
// }