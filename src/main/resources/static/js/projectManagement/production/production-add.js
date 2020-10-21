let projectId = getParamForUrl("projectId");
let implementId = getParamForUrl("implementId");
let departmentId;
let equipmentType;
let bossType;
let restsType;
let projectBoos;
let index;


let ajaxproject;
$(function () {
    layui.use(['element', 'laydate', 'layer', 'form'], function () {
        var element = layui.element;
        var laydate = layui.laydate;
        laydate.render({
            elem: '#test1' //指定元素
        });
        var layer = layui.layer;
        var $ = layui.jquery,
            form = layui.form;
        layui.form.render('select');
        // 1 当选择项目编号时,项目名称就对自动填上去
        setInterval(function () {
            form.on('select(projectNum)', function (data) {
                $("#equipment_money_back_estimation_tBody").empty();
                $("#boss_money_back_estimation_tBody").empty();
                let projectId = data.value;
                $(".projectName").find("option[value='" + projectId + "']").attr('selected', true);
                renderFix("projectName");
                showImplement();
                $(".basic input").each(function () {
                    $(this).val("")
                });
                emptySum();
            });
            form.on('select(projectName)', function (data) {
                $("#equipment_money_back_estimation_tBody").empty();
                $("#boss_money_back_estimation_tBody").empty();
                let projectId = data.value;
                $(".projectNum").find("option[value='" + projectId + "']").attr('selected', true);
                renderFix("projectName");
                showImplement();
                emptySum();
            });
            form.on('select(implementId)', function (data) {
                $("#equipment_money_back_estimation_tBody").empty();
                $("#boss_money_back_estimation_tBody").empty();
                let implementId = data.value;
                //选择实施部的时候 需要把 组织架构 部门拿出来 因为 要通过 部门id 去寻找 生产费 的明细 类别
                departmentId = $(".implementId").find("option:selected").attr("data-id");
                if (implementId !== "") {
                    console.log(implementId);
                    $.get("/production_costs/production_info", {"id": implementId}, function (data) {
                        for (var key in data) {
                            $("[title='" + key + "']").val(data[key])
                        }

                    }, "json");


                    productionDetail();
                }
                emptySum();
            });
        }, 100);
    });

    //铺项目编号项目名称
    ajaxproject = $.get("/table_utils", {
        "table_utils.tableName": "project",
        "table_utils.fields": "projectId$projectNum$projectName"
    }, function (data) {
        let projectInfo = data.content;
        let projectNum = $("select.projectNum");
        let projectName = $("select.projectName");
        projectNum.empty();
        projectName.empty();
        projectNum.append("<option></option>");
        projectName.append("<option></option>");
        $(projectInfo).each(function (index, projectTemp) {
            projectNum.append("<option value='" + projectTemp[0] + "'>" + projectTemp[1] + "</option>");
            projectName.append("<option value='" + projectTemp[0] + "'>" + projectTemp[2] + "</option>");
        });
        renderFix("projectNum");
        renderFix("projectName");
    }, "json");

    //当选择实施部的时候 铺 设备使用费 和班组费
    function productionDetail(rollback) {
        let a = false;
        let b = false;
        let c = false;
        let d = false;
        $.get("/production_costs_detail", {"productionCostsDetailType": "设备使用费"}, function (data) {
            equipmentType = data;
            a = true;
            back();
        }, "json");

        $.get("/production_costs_detail", {"productionCostsDetailType": "班组费"}, function (data) {
            bossType = data;
            b = true;
            back();
        }, "json");

        $.get("/squad_group_fee/amount", {}, function (data) {
            projectBoos = data["data"];
            c = true;
            back();
        }, "json");

        $.get("/production_costs_detail", {"productionCostsDetailType": "其他费"}, function (data) {
            restsType = data;
            d = true;
            back();
        }, "json");

        function back() {
            if (a && b && c && d) {
                if (typeof rollback === "function") {
                    rollback();
                }
            }
        }


    }

    //铺实施部
    function showImplement() {
        let projectId = $(".projectNum").find("option:selected").val();
        $.get("/project/project_id", {"id": projectId}, function (data) {
            let implementId = $("select.implementId");
            implementId.empty();
            implementId.append("<option></option>");
            $(data["implementBeans"]).each(function (i, ele) {
                //data-id 部门Id
                implementId.append("<option data-id='" + ele["departmentBean"]["departmentId"] + "' value='" + ele["implementId"] + "'>" + ele["departmentBean"]["departmentName"] + "</option>")
            });
            renderFix("implementId");
        }, "json")
    }

    //设备使用费
    $(".add_equipment_money_back_estimation").click(function () {
        if (equipmentType === undefined) {
            return alert("请先选择项目 和 实施部")
        }
        showEquipmentList()
    });

    function showEquipmentList() {
        let TempId = Math.ceil(Math.random() * 100000);
        let tBodyTemp = $("#equipment_money_back_estimation_tBody");
        let len = tBodyTemp.children().length;
        let len2 = $("#boss_money_back_estimation_tBody").children().length;
        let len3 = $("#rests_money_back_estimation_tBody").children().length;
        index = (len + len2 + len3);
        var typeName = "<option></option>";
        console.log(equipmentType);
        $(equipmentType["data"]["rows"]).each(function (i, ele) {
            typeName += "<option value='" + ele["productionCostsDetailId"] + "' data-key='" + ele["productionCostsEquipmentPrice"] + "'>" + ele["productionCostsDetailName"] + "</option>"
        });
        tBodyTemp.append(
            '<tr class="newTr">\n' +
            '                                    <td>' + (len + 1) + '</td>\n' +
            '                                    <td>\n' +
            '                                       <input type="hidden" name="productionCostsBeans[' + index + '].productionCostsCreateTime" value="' + formatDate(new Date(), 8) + '">' +
            '                                       <input type="hidden" name="productionCostsBeans[' + index + '].createUserBean.userId" value="' + window.top.userTemp.userId + '"> ' +
            '                                       <input type="hidden" name="productionCostsBeans[' + index + '].createJobBean.jobId" value="' + window.top.userTemp["jobBean"]["jobId"] + '">' +
            '                                        <select class="need-input" lay-search name="productionCostsBeans[' + index + '].productionCostsDetailBean.productionCostsDetailId" title="生产费类型" lay-filter="type' + TempId + '">\n' +
            typeName +
            '                                        </select>\n' +
            '                                    </td>\n' +
            '                                    <td><input name="productionCostsBeans[' + index + '].productionCostsDay" title="天数" type="number" class="layui-input need-input equipmentDay"></td>\n' +
            '                                    <td><input name="productionCostsBeans[' + index + '].productionCostsMoney" readonly title="金额" type="number" class="layui-input need-input equipmentMoney"></td>\n' +
            '                                    <td><input name="productionCostsBeans[' + index + '].productionCostsRemark"  title="备注" type="text" class="layui-input"></td>\n' +
            '                                    <td class="do-td">\n' +
            '                                        <button type="button" class="delete-btn">\n' +
            '                                            <span class="layui-icon layui-icon-delete del-btn" data-key="equipmentSum" style="color: #ff4646" title="删除"></span>\n' +
            '                                        </button>\n' +
            '                                    </td>\n' +
            '                                </tr>'
        );
        renderFix("type" + TempId);
        renderFix("estimation" + TempId);
        layui.use(['layer', 'laydate', 'form'], function () {
            let layDate = layui.laydate;
            layDate.render({
                elem: '.lay-date' //指定元素
            });
        });

    }

    //班组费
    $(".add_boss_money_back_estimation").click(function () {
        if (equipmentType === undefined) {
            return alert("请先选择项目 和 实施部")
        }

        showBossList();
    });

    function showBossList() {
        let TempId = Math.ceil(Math.random() * 100000);
        let tBodyTemp = $("#boss_money_back_estimation_tBody");
        //获取总条数
        let len = tBodyTemp.children().length;
        let len2 = $("#equipment_money_back_estimation_tBody").children().length;
        let len3 = $("#rests_money_back_estimation_tBody").children().length;
        index = (len + len2 + len3);
        var typeName = "<option></option>";
        var boss = "<option></option>";
        $(bossType["data"]["rows"]).each(function (i, ele) {
            typeName += "<option value='" + ele["productionCostsDetailId"] + "'>" + ele["productionCostsDetailName"] + "</option>"
        });
        $(projectBoos).each(function (i, ele) {
            boss += "<option value='" + ele["squadGroupFeeId"] + "'>" + ele["squadGroupFeeName"] + "</option>"
        });
        tBodyTemp.append(
            '<tr class="newTr">\n' +
            '                                    <td>' + (len + 1) + '</td>\n' +
            '                                    <td>\n' +
            '                                       <input type="hidden" name="productionCostsBeans[' + index + '].productionCostsCreateTime" value="' + formatDate(new Date(), 8) + '">' +
            '                                       <input type="hidden" name="productionCostsBeans[' + index + '].createUserBean.userId" value="' + window.top.userTemp.userId + '"> ' +
            '                                       <input type="hidden" name="productionCostsBeans[' + index + '].createJobBean.jobId" value="' + window.top.userTemp["jobBean"]["jobId"] + '">' +
            '                                       <select class="need-input" lay-search name="productionCostsBeans[' + index + '].productionCostsDetailBean.productionCostsDetailId" title="班组费类型" lay-filter="type' + TempId + '">\n' +
            typeName +
            '                                        </select>\n' +
            '                                    </td>\n' +
            '                                    <td>\n' +
            '                                       <select class="need-input" lay-search name="productionCostsBeans[' + index + '].squadGroupFeeBean.squadGroupFeeId" title="班组老板" lay-filter="type' + TempId + '">\n' +
            boss +
            '                                        </select>\n' +
            '                                    </td>\n' +
            '                                    <td><input  name="productionCostsBeans[' + index + '].productionCostsMoney" title="金额" type="number" class="layui-input need-input bossMoney"></td>\n' +
            '                                    <td><input name="productionCostsBeans[' + index + '].productionCostsRemark"  title="备注" type="text" class="layui-input"></td>\n' +
            '                                    <td class="do-td">\n' +
            '                                        <button type="button" class="delete-btn">\n' +
            '                                            <span class="layui-icon layui-icon-delete del-btn" data-key="bossSum" style="color: #ff4646" title="删除"></span>\n' +
            '                                        </button>\n' +
            '                                    </td>\n' +
            '                                </tr>'
        );
        renderFix("type" + TempId);
        renderFix("estimation" + TempId);
        layui.use(['layer', 'laydate', 'form'], function () {
            let layDate = layui.laydate;
            layDate.render({
                elem: '.lay-date' //指定元素
            });
        });
    }


    //其他费
    $(".add_rests_money_back_estimation").click(function () {
        if (restsType === undefined) {
            return alert("请先选择项目 和 实施部")
        }
        showRestsList();
    });

    function showRestsList() {
        let TempId = Math.ceil(Math.random() * 100000);
        let tBodyTemp = $("#rests_money_back_estimation_tBody");
        let len = tBodyTemp.children().length;
        let len2 = $("#equipment_money_back_estimation_tBody").children().length;
        let len3 = $("#boss_money_back_estimation_tBody").children().length;
        index = (len + len2 + len3);
        var typeName = "<option></option>";
        $(restsType["data"]["rows"]).each(function (i, ele) {
            typeName += "<option value='" + ele["productionCostsDetailId"] + "'>" + ele["productionCostsDetailName"] + "</option>"
        });
        tBodyTemp.append(
            '<tr class="newTr">\n' +
            '                                    <td>' + (len + 1) + '</td>\n' +
            '                                    <td>\n' +
            '                                       <input type="hidden" name="productionCostsBeans[' + index + '].productionCostsCreateTime" value="' + formatDate(new Date(), 8) + '">' +
            '                                       <input type="hidden" name="productionCostsBeans[' + index + '].createUserBean.userId" value="' + window.top.userTemp.userId + '"> ' +
            '                                       <input type="hidden" name="productionCostsBeans[' + index + '].createJobBean.jobId" value="' + window.top.userTemp["jobBean"]["jobId"] + '">' +
            '                                        <select class="need-input" lay-search name="productionCostsBeans[' + index + '].productionCostsDetailBean.productionCostsDetailId" title="生产费类型" lay-filter="type' + TempId + '">\n' +
            typeName +
            '                                        </select>\n' +
            '                                    </td>\n' +
            '                                    <td><input name="productionCostsBeans[' + index + '].productionCostsMoney" title="金额" type="number" class="layui-input need-input restsMoney"></td>\n' +
            '                                    <td><input name="productionCostsBeans[' + index + '].productionCostsRemark"  title="备注" type="text" class="layui-input"></td>\n' +
            '                                    <td class="do-td">\n' +
            '                                        <button type="button" class="delete-btn">\n' +
            '                                            <span class="layui-icon layui-icon-delete del-btn" data-key="restsSum" style="color: #ff4646" title="删除"></span>\n' +
            '                                        </button>\n' +
            '                                    </td>\n' +
            '                                </tr>'
        );
        renderFix("type" + TempId);
        renderFix("estimation" + TempId);
        layui.use(['layer', 'laydate', 'form'], function () {
            let layDate = layui.laydate;
            layDate.render({
                elem: '.lay-date' //指定元素
            });
        });

    }

    // $(document).on("click",".delete-btn",function () {
    //     $(this).parent().parent().remove()
    // })

    //提交
    let status = false;
    $(".confirm-btn").click(function () {
        if (index === undefined) {
            return alert("请录入 生产费!")
        }
        if (!formChecking() || status) {
            return false;
        }
        status = true;

        let data =new FormData($("#inputForm")[0]);
        let  url="/implement/impl_production"
        $.ajax({
            "url": url,
            "data": data,
            "type": "PUT",
            "contentType": false,
            "processData": false,
            "dataType": "json",
            xhr: function () { //获取ajaxSettings中的xhr对象，为它的upload属性绑定progress事件的处理函数

                var myXhr = $.ajaxSettings.xhr();
                console.log("myXhr:"+myXhr)
                if (myXhr.upload) { //检查upload属性是否存在
                    //绑定progress事件的回调函数
                    // 进度条提示判断
                    console.log($("#showFile").children("input").length > 0)
                    if ($("#showFile").children("input").length > 0) {
                        myXhr.upload.addEventListener('progress', progressHandlingFunction, false);
                        console.log(myXhr.upload.addEventListener)
                    }
                }
                return myXhr; //xhr对象返回给jQuery使用
            },
            "success": function (data) {
                        if (data) {
                            layer.msg('操作成功 2秒后自动刷新', {
                                time: 2000 //2秒关闭（如果不配置，默认是3秒）
                            }, function () {
                                window.location.reload();
                                window.parent.getPageDate(window.parent.pageDataA.pageNum, window.parent.pageDataA.pageSize);
                                var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                                parent.layer.close(index); //再执行关闭
                            });
                        } else {
                            layer.msg('添加失败', {icon: 5});
                            status = false;
                        }
             }
        })
    //     $.post("/implement/impl_production", dataR, function (data) {
    //         if (data) {
    //             layer.msg('操作成功 2秒后自动刷新', {
    //                 time: 2000 //2秒关闭（如果不配置，默认是3秒）
    //             }, function () {
    //                 window.location.reload();
    //                 window.parent.getPageDate(window.parent.pageDataA.pageNum, window.parent.pageDataA.pageSize);
    //                 var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
    //                 parent.layer.close(index); //再执行关闭
    //             });
    //         } else {
    //             layer.msg('添加失败', {icon: 5});
    //             status = false;
    //         }
    //     }, "json").fail(function (res) {
    //         layer.msg('数据提交失败 请刷新重试', {icon: 5});
    //         console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
    //         status = false;
    //     }, "json");
     });


    //监听 设备金额 改变 计算 总价
    $(document).on("change", ".equipmentDay", function () {
        let equipmentMoney = 0;
        let day = $(this).val();
        let price = $(this).parent().prev().children("select").find("option:selected").attr("data-key");
        if (price === undefined) {
            $(this).val("");
            return layer.alert('请选择设备', {
                icon: 0,
            })
        }
        $(this).parent().next().children(".equipmentMoney").val(day * price);

        $(".equipmentMoney").each(function (i, ele) {
            equipmentMoney += parseFloat($(ele).val());
        });
        let money = $(".equipmentSum");
        money.val(equipmentMoney);
        aggregate();
    });


    //监听 班组金额 改变 计算 总价
    $(document).on("change", ".bossMoney", function () {
        let bossMoney = 0;
        $(".bossMoney").each(function (i, ele) {
            bossMoney += parseFloat($(ele).val());
        });
        let money = $(".bossSum");
        money.val(bossMoney);
        aggregate();
    });

    //监听其他金额 改变 计算 总价
    $(document).on("change", ".restsMoney", function () {
        let restsMoney = 0;
        $(".restsMoney").each(function (i, ele) {
            restsMoney += parseFloat($(ele).val());
        });
        let money = $(".restsSum");
        money.val(restsMoney);
        aggregate();
    });

    function aggregate() {
        let money1 = parseFloat($(".equipmentSum").val());
        if (isNaN(money1)) {
            money1 = 0
        }
        let money2 = parseFloat($(".bossSum").val());
        if (isNaN(money2)) {
            money2 = 0
        }

        let money3 = parseFloat($(".restsSum").val());
        console.log(money3);
        if (isNaN(money3)) {
            money3 = 0
        }

        let money4 = parseFloat($("[title='项目花销']").val());
        let money5 = parseFloat($("[title='管理费']").val());
        let money6 = parseFloat($("[title='技术提成']").val());
        let money7 = parseFloat($("[title='税费']").val());
        let money8 = parseFloat($("[title='人员成本']").val());
        let money9 = parseFloat($("[title='年总奖金']").val());

        $(".moneySum").val((money1 + money2 + money3 + money4 + money5 + money6 + money7 + money8 + money9).toFixed(2))
    }

    function emptySum() {
        $(".equipmentSum").val("");
        $(".bossSum").val("");
        $(".restsSum").val("");
        $(".moneySum").val("");
    }

    //检查 必填项
    function formChecking() {
        let check = true;
        $(".need-input").each(function () {
            let tipsInfo = $(this).parent().prev().text();
            let tipsInfo2 = $(this).attr("title");
            if ($(this).val() === null || $(this).val().replace(/(^\s+)|(\s+$)/g, "") === "") {
                let that = this;
                layer.tips(tipsInfo2 + '不能为空', that, {
                    tips: [1, '#3595CC'],
                    time: 4000
                });
                return check = false;
            }
        });
        return check;

    }


    if (projectId != null) {
        //1.0 当页面传过来的项目id 不等于空的时候
        $.when(ajaxproject).done(function () {
            //1.1 帮选上项目编号 和 项目名称
            let projectNum = $("select.projectNum");
            let projectName = $("select.projectName");
            //1.2 然后把项目和名称 禁用
            projectNum.find("option[value='" + projectId + "']").attr('selected', true);
            projectNum.attr("readonly", "readonly");
            projectName.find("option[value='" + projectId + "']").attr('selected', true);
            projectName.attr("readonly", "readonly");
            renderFix("projectNum");
            renderFix("projectName");
            //1.3 获取 成本 展示
            $.get("/production_costs/production_info", {"id": implementId}, function (data) {
                for (let key in data) {
                    $("[title='" + key + "']").val(data[key].toFixed(2))
                }
                $(".summary").val((data["技术提成"] * 0.3).toFixed(2))
            }, "json");
            //1.4 通过实施 查询生产费的 设备使用 和班组费
            $.get("/implement/implement_id", {"id": implementId}, function (data) {
                test(data);
                let implementId = $("select.implementId");
                //1.5 情况 实施select 并帮选上
                implementId.attr("readonly", "readonly");
                implementId.empty();
                implementId.append("<option></option>");
                departmentId = data["departmentBean"]["departmentId"];
                implementId.append("<option data-id='" + data["departmentBean"]["departmentId"] + "' selected value='" + data["implementId"] + "'>" + data["departmentBean"]["departmentName"] + "</option>");
                renderFix("implementId");
                aggregate();
                //1.5 回调函数 当 铺了 设备使用和班组费select 的全局变量 后 才执行 回调函数里面的代码
                productionDetail(function () {
                    //1.6 回调 调用 生产费 铺 设备 和班组
                    let e = 0;
                    let m = 0;
                    let r = 0;
                    $(data["productionCostsBeans"]).each(function (i, ele) {
                        if (ele["productionCostsDetailBean"]["productionCostsDetailType"] === '设备使用费') {
                            //1.7 调用 铺 数据方法
                            showEquipmentList();
                            e += ele["productionCostsMoney"];
                            let dayEle = $("[name='productionCostsBeans[" + i + "].productionCostsDay']");

                            dayEle.val(ele["productionCostsDay"]);
                        }
                        if (ele["productionCostsDetailBean"]["productionCostsDetailType"] === '班组费') {
                            //1.7 调用 铺 数据方法
                            showBossList();
                            m += ele["productionCostsMoney"];
                            //1.8 调用 铺 铺班组老板
                            let squadGroupFeeName = "";
                            if(ele["squadGroupFeeBean"] != null){
                                squadGroupFeeName = ele["squadGroupFeeBean"]["squadGroupFeeId"]
                            }
                            console.log(squadGroupFeeName);
                            $("[name='productionCostsBeans[" + i + "].squadGroupFeeBean.squadGroupFeeId']").find("option[value='" + squadGroupFeeName + "']").attr('selected', true);
                        }
                        if (ele["productionCostsDetailBean"]["productionCostsDetailType"] === '其他费') {
                            //1.7 调用 铺 数据方法
                            showRestsList();
                            r += ele["productionCostsMoney"];
                        }
                        $(".equipmentSum").val(e);
                        $(".bossSum").val(m);
                        $(".restsSum").val(r);
                        aggregate();
                        //1.89 调用 类型 和 金额
                        let moneyEle = $("[name='productionCostsBeans[" + i + "].productionCostsMoney']");

                        moneyEle.val(ele["productionCostsMoney"]);
                        $("[name='productionCostsBeans[" + i + "].productionCostsDetailBean.productionCostsDetailId']").find("option[value='" + ele["productionCostsDetailBean"]["productionCostsDetailId"] + "']").attr('selected', true);
                        $("[name='productionCostsBeans[" + i + "].productionCostsRemark']").val(ele["productionCostsRemark"]);
                        //2.0 给 按钮添加 删除 data-id = id
                        moneyEle.parent().parent().children().find(".del-btn").attr("data-id", ele["productionCostsId"]);
                        moneyEle.parent().append("<input style='display: none' name='productionCostsBeans[" + i + "].productionCostsId' value='" + ele["productionCostsId"] + "'>")

                    });
                    layui.use(['form'], function () {
                        layui.form.render('select');
                    });
                });


            }, "json")
            //所做操作
        });



    }


    $(document).on("click", ".del-btn", function () {
        let id = $(this).attr("data-id");
        if (id === undefined) {

            $(this).parent().parent().parent().remove();
            //对应的总计元素
            let eleSum = $("." + $(this).attr("data-key"));
            let sumVal = parseFloat(eleSum.val());
            if (isNaN(sumVal)) {
                sumVal = 0
            }
            let minusVal = parseFloat($($(this).parent().parent().prev().children("input").get(0)).val());
            if (isNaN(minusVal)) {
                minusVal = 0
            }
            let nowMoney = sumVal - minusVal;
            eleSum.val(nowMoney);
            let aggregateEle = $(".moneySum");
            let sum = parseFloat(aggregateEle.val());
            console.log(sum);
            console.log(nowMoney);
            if (isNaN(sum)) {
                sum = 0
            }
            aggregateEle.val(sum - minusVal);
        }else {
            layer.confirm('您是如何看待前端开发？', {
                btn: ['确认删除','取消'] //按钮
            }, function(){
                $.post("/production_costs", {"_method": "Delete", "id": id}, function (data) {
                    if (data) {
                        layer.msg('操作成功 2秒后自动刷新', {
                            time: 2000 //1秒关闭（如果不配置，默认是3秒）
                        }, function () {
                            window.location.reload();
                            window.parent.getPageDate(window.parent.pageDataA.pageNum, window.parent.pageDataA.pageSize);
                        });
                    } else {
                        layer.msg('添加失败', {icon: 5});
                    }
                }, "json");
            })

        }

    });

    //3 扫描件上传-----------------------------------------------------

    //3-1 点击附件上传按钮的时候，触发file的click事件
    $(document).on('click', '#btn_file_upload', function () {

        var temp = Math.ceil(Math.random() * 100000);
        var file_upload = '<input type="file" name="' + temp + '"  class="file_upload" id="file_upload_' + temp + '" style="display: none">';
       // $(this).next().append(file_upload);
        $("#showFile").append(file_upload);
        $("#file_upload_" + temp).click();
    });

    //3-2 当点击附件上传发生变化时，显示不同的文件名称
    var fileName = [];
    var maxFileSize = 0;
    var maxFile = 0;
    $.get("/get_config_file",{},function (data) {
        maxFile = data["maxFileSize"];
        maxFileSize = (data["maxFileSize"].substring(0,data["maxFileSize"].length-2))*1024*1024;
    });

    $(document).on('change', '.file_upload', function () {

        var files = $(this)[0].files;
        var added_file = $('#showFile');
        // 文件上传大小限制
        var allSize = 0;
        $(this).closest("#showFile").children("input").each(function (index2, file2) {
            allSize += file2["files"][0].size;
        });

        // 上传文件大小最大为10MB
        if (allSize > maxFileSize) {
            // 只有一个文件
            if ($(this).closest("#showFile").children("input").length === 1) {
                layer.msg("上传文件大小最大为"+maxFile);
                added_file.children("input:last-child").remove();
                return false;
            } else { // 多个文件
                layer.msg("上传文件大小最大为"+maxFile+",请分批上传");
                added_file.children("input:last-child").remove();
                return false;
            }
        }
        // 重复名提醒
        $(files).each(function () {
            if (fileName.includes(this.name)) {
                layer.msg("该文件名与 " + this.name + " 重复", {icon: 5});
                added_file.children("input:last-child").remove();
            } else {
                added_file.append("<p style='color: #f97767'><span>" + this.name + "</span><button type='button' class='delete-file layui-btn layui-btn-danger layui-btn-xs' style='margin-left: 20px;'>删除</button></p>");
                fileName.push(this.name);
                is_file_name();
            }
        })
    });

    //3-3 当点击删除按钮时,对应的文件删除
    $(document).on('click', '.delete-file', function () {
        var file_name = $(this).parent('p').text();
        var really_file_name = $(this).parent('p').text().substring(0, file_name.length - 2);
        var index = fileName.indexOf(really_file_name);
        if (index > -1) {
            fileName.splice(index, 1);
        }
        $(this).parent('p').prev('input').remove();
        $(this).parent('p').remove();
        is_file_name();
    });

    //3-4 判断是否有扫描件存在
    function is_file_name() {
        if (fileName.length > 0) {
            $("#btn_submit_file").show();
        } else {
            $("#btn_submit_file").hide();
        }
    }

    //12 删除已经上传好的扫描件
    var file_ids_arr = [];
    $(document).on("click", ".delete-upload-file", function () {
        var that = $(this);
        var data_id = $(this).attr("data-id");
        layer.confirm('确认删除该附件吗？', {
            btn: ['确认', '取消'] //按钮
        }, function () {
            layer.close(layer.index);
            that.parent('p').remove();
            file_ids_arr.push(data_id);
            del_file_ids = file_ids_arr.join("$");
            $(".delFileIds").val(del_file_ids);
        });
    });

    // 铺已经上传好的附件
    function test(data) {
      //  debugger;
        console.log(data)
    var download_file = $(".download_file");
    var urlPath = window.document.location.href;
    var x=urlPath.indexOf('/');
    for(var i=0;i<2;i++){
        x=urlPath.indexOf('/',x+1);
    }
    urlPath=urlPath.substring(0,x);
    console.log(urlPath+"地址");
    download_file.empty();
    $(data["productionCostsFileBeans"]).each(function (index, item) {
        console.log(item["productionCostsFileName"]);

        let href = "/download_file?fileName="+item["productionCostsFileRelativePaths"]+"//" +  (item["productionCostsFileName"]);
        // href = encodeURIComponent(href);

        download_file.append(
            '<p style="color: #2c2c2c">'
            + '<span>' + item["productionCostsFileName"] + '</span>'
            + '<button type="button" class="layui-btn layui-btn-xs layui-btn-normal">'
            + '<a download="' + item["productionCostsFileName"] + '" href="'+href+'" style="color: #fff">下载</a>'
            + '</button>'
            + '<button type="button" class="delete-upload-file layui-btn layui-btn-danger layui-btn-xs" style="margin-left: 5px;" data-id="' + item["productionCostsFileId"] + '">删除</button>'
            + '</p>'
        )
    })
    }
//进度条函数
    function progressHandlingFunction(e) {
        var progress_bar = $(".layui-progress-bar");
        if (e.lengthComputable) {
            progress_bar.attr({value: e.loaded, max: e.total}); //更新数据到进度条
            var percent = e.loaded / e.total * 99;
            progress_bar.css('width', percent.toFixed(2) + "%").html(
                (parseFloat(percent.toFixed(2)) + 1) + "%");
        }
    }



});


