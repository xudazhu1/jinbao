
let id = getParamForUrl("id");
let way = getParamForUrl("way");
let income = null;
$(function () {
    $("body").addClass(way);
    fixWay(way);
    //项目名称只读
    $("[lay-filter='projectName']").attr("readonly" , "readonly");

    if ( way === "add" ) {

        $(document).on("keyup" , "[name]" , function () {
            let invoiceAuditStatusSelector = $("[name='invoiceBean.invoiceAuditStatus']" );
            invoiceAuditStatusSelector.val("0");
            renderFix("invoiceAuditStatus");
            invoiceAuditStatusSelector.next(".layui-form-select").find("input.layui-unselect").val("提交后待审核");
        });

        $(document).on("blur" , ".lay_date" , function () {
            let invoiceAuditStatusSelector = $("[name='invoiceBean.invoiceAuditStatus']" );
            invoiceAuditStatusSelector.val("0");
            renderFix("invoiceAuditStatus");
            invoiceAuditStatusSelector.next(".layui-form-select").find("input.layui-unselect").val("提交后待审核");
        });
    }

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
                elem: this ,  //指定元素
                type: 'datetime' ,
            });
        });
        layui.form.render('select');

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
            renderFix("projectNum");
            renderFix("projectName" , function () {
                $("select.projectNum + .layui-form-select dd  ").click(function () {
                // $("select.projectNum + .layui-form-select dd  , select.projectName + .layui-form-select dd").click(function () {
                    let myDDs = $("select.projectNum + .layui-form-select dd") , heDDs = $("select.projectName + .layui-form-select dd");
                    if ( $(this).closest(".layui-form-select").prev().hasClass("projectName") ) {
                        let temp = myDDs;
                        myDDs = heDDs;
                        heDDs = temp;
                    }
                    let thisTemp = this;
                    myDDs.each(function (index , ddTemp) {
                        if ( ddTemp === thisTemp ) {
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
                showInfo();
            });

        } , "json");

    //获取收益单位
    $.get("/table_utils" ,
        {"table_utils.tableName" : "earnings_company" , "table_utils.fields" : "earningsCompanyId$earningsCompanyName"} ,
        function (data) {
            let earningsCompanySelect = $("select[lay-filter='earningsCompanyId']");
            earningsCompanySelect.empty();
            earningsCompanySelect.append(
                "<option   ></option>"
            );
            $(data.content).each(function (index, earningsCompanyTemp) {
                earningsCompanySelect.append(
                    "<option value='"+earningsCompanyTemp[0]+"' >"+earningsCompanyTemp[1]+"</option>"
                );
            });
            renderFix("earningsCompanyId");
            // showInfo();
        } , "json" );

    //获取收款单位
    $.get("/table_utils" ,
        {"table_utils.tableName" : "second_party" , "table_utils.fields" : "secondPartyId$secondPartyName"} ,
        function (data) {
            let secondPartySelect = $("select[lay-filter='secondPartyId']");
            secondPartySelect.empty();
            secondPartySelect.append("<option></option>");
            $(data.content).each(function (index, secondPartyTemp) {
                secondPartySelect.append(
                    "<option value='"+secondPartyTemp[0]+"' >"+secondPartyTemp[1]+"</option>"
                );
            });
            renderFix("secondPartyId");
            // showInfo();
        } , "json" );



    //提交
    $(".confirm-btn").click(function () {
        let dataR = $.param({ tableName : "income" , "_method": "POST" , "invoiceBean.createUserBean.userId" : window.top.userTemp.userId ,
            // "managementCreateTime" : formatDate(new Date() , 8 ) ,
            "invoiceBean.createJobBean.jobId" : window.top.userTemp["jobBean"]["jobId"] }) + "&" + $("#inputForm").serialize();
        $.post("/income" , dataR , function (data) {
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




});

function showInfo() {
    //如果有 id 铺数据
    if ( id != null ) {
        if ( income !== null ) {
            showData4Object(income);
        } else {
            $.get("/table_utils/info" ,
                {incomeId : id , "table_utils.tableName" : "income" } , function (data) {
                    income = data.content[0];
                    showData4Object(income);
                    //如果是补开票 将不需要开票按钮显示出来
                    if ( income["invoiceBean"]["invoiceAuditStatus"] === "" || income["invoiceBean"]["invoiceAuditStatus"] === null  ) {
                        $(".no-invoice").show().click(function () {
                            //如果提交不需要
                            let dataR = { tableName : "income" , "_method": "POST" , "invoiceBean.createUserBean.userId" : window.top.userTemp.userId ,
                                // "managementCreateTime" : formatDate(new Date() , 8 ) ,
                                "invoiceBean.createJobBean.jobId" : window.top.userTemp["jobBean"]["jobId"] };
                            //拼接incomeId 和不需要状态
                            dataR["incomeId"] = $("[name='incomeId']").val();
                            dataR["invoiceBean.invoiceAuditStatus"] = "3";
                            //铺项目编号项目名称
                            $.post("/table_utils" , dataR , function (data) {
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
                    }
                } , "json" );
        }
    }
}


function renderFixed(layFilter) {
    if ( way === "add" ) {
        setTimeout(function () {
            $("select[lay-filter='"+layFilter+"'] + .layui-form-select dd").click(function () {
                let invoiceAuditStatusSelector = $("[name='invoiceBean.invoiceAuditStatus']" );
                invoiceAuditStatusSelector.val("0");
                renderFix("invoiceAuditStatus");
                invoiceAuditStatusSelector.next(".layui-form-select").find("input.layui-unselect").val("提交后待审核");
            });
        } , 200);
    }
}
