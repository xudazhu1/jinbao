$(function () {
    // layui 初始化
    layui.use(['layer'], function () {
        var layer = layui.layer;
        var form = layui.form;
        layui.form.render('select');

        // 1 选择费用类别
        setTimeout(function () {
            form.on('select(disburseCategoryName)', function (data) {
                $(this).closest("tr").find(".display-input").val(data.value);//?????
            });
        }, 100);

        // 2 选择费用类型时,隐藏域填值
        setTimeout(function () {
            form.on('select(disburseTypeName)', function () {
                var disburseCategory = $(this).closest("td").find(".disburseTypeName option:selected").attr("data-parent");
                if (disburseCategory) {
                    $(this).closest("tr").find(".show-td").text(disburseCategory);
                } else {
                    $(this).closest("tr").find(".show-td").text("");
                }

            });
        }, 100);

    });

    // 2 双击可编辑该td(input)
    $(document).on("dblclick", ".data-td", function () {
        showToEditTr($(this).closest("tr"));
        okAndClose($(this).closest("tr"))
    });

    // 2-1 双击可编辑该td(select)
    $(document).on("dblclick", ".select-td", function () {
        showToEditTrSelect($(this).closest("tr"));
        okAndClose($(this).closest("tr"))
    });

    // 3 单击编辑按钮可编辑该td
    $(document).on("click", ".edit-btn", function () {
        showToEditTr($(this).closest("tr"));
        showToEditTrSelect($(this).closest("tr"));
        okAndClose($(this).closest("tr"))
    });

    // 4 确认添加
    $(document).on("click", ".ok-btn", function () {
        var thisTemp = $(this);
        var tableTemp = $(this).closest("table");
        var data_name = $(this).closest("table").attr("data-table");
        var trTemp = $(this).closest("tr");
        // 表单校验
        var check = true;
        $(this).closest("tr").find(".layui-input").each(function (index, input) {
            if ($(input).val() === "") {
                let that = this;
                layer.tips('值不能为空', that, {
                    tips: [1, '#3595CC'],
                    time: 4000
                });
                return check = false;
            }
        });
        if (!check) {
            return false;
        }

        var itemData = getJson4Dom(trTemp);// 将需要编辑的这一行tr序列化成json
        if (!thisTemp.hasClass("new-add")) {
            itemData["_method"] = "PUT";
        }
        itemData["tableName"] = data_name;

        $.post("/table_utils", itemData, function (data) {
            if (data) {
                if (thisTemp.hasClass("new-add")) {
                    layer.msg("添加成功", {icon: 1});
                    showTable(tableTemp);
                } else {
                    editToShow(trTemp);//将这一行tr恢复成原来的状态(input)
                    editAndDelete(trTemp);//修改成功后,操作框变成编辑和删除
                    layer.msg("修改成功", {icon: 1});
                }
                if (data_name === "disburse_category") {
                    showSelectData($("#disburse_category_select"));
                } else if (data_name === "disburse_type") {
                    showSelectData($("#disburse_type_select"));
                }
            } else {
                layer.msg("添加失败", {icon: 5});
            }
        }, "json").fail(function (res) {
            layer.msg('数据提交失败 请刷新重试', {icon: 5});
            console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
        }, "json");
    });

    // 5 铺表格数据,仅适用于输入框
    showTable($(".disburse_category"));//大类
    showTable($(".disburse_type"));//中类
    showTable($(".disburse_detail"));//明细
    showTable($(".bank_card"));//银行卡(公司)
    showTable($(".personal_bank_card"));//银行卡(个人)

    // 6 删除数据
    $(document).on("click", ".del-btn", function () {
        let data_id = $(this).closest("tr").find(".display-td").find("input").val();
        let tableTemp = $(this).closest("table");
        let table_name = $(this).closest("table").attr("data-table");
        layer.confirm('确认删除吗？', {
            btn: ['确认', '取消'] //按钮
        }, function () {
            layer.close(layer.index);
            $.post("/table_utils/id" , {"_method": "DELETE","tableName" : table_name , "ids": data_id}, function (data) {
                if (data) {
                    layer.msg("删除成功", {icon: 1});
                    showTable(tableTemp);
                    if (table_name === "disburse_category") {
                        showSelectData($("#disburse_category_select"));
                    } else if (table_name === "disburse_type") {
                        showSelectData($("#disburse_type_select"));
                    }
                } else {
                    layer.msg("删除失败,请刷新重试!", {icon: 5})
                }
            }, "json").fail(function (res) {
                layer.msg('数据提交失败 请刷新重试', {icon: 5});
                console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
            }, "json");
        })
    });

    // 7 点击取消,恢复成原来的状态
    $(document).on("click", ".close-btn", function () {
        returnTr($(this).closest("tr"));
    });

    // 8 铺已经录入的费用类别数据到费用类型的表格里
    showSelectData($("#disburse_category_select"));

    // 9 铺已经录入的费用类型数据到费用明细的表格里
    showSelectData($("#disburse_type_select"));

    // 10 收款银行账号 空格
    $(document).on("keyup", ".cardSpace", function () {
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

    $(window).resize(function () {
        fixedHead($(".disburse_detail"));
    });

});

// 方法1 将这一行tr变成可以编辑的状态(input)
function showToEditTr(trDom) {
    trDom.find(".edit-td").each(function (index, td) {
        let textTemp = td.innerText;
        let name = $(td).attr("data-name");
        $(td).empty();
        $(td).append("<input class='layui-input' type='text' name='" + name + "' value='" + textTemp + "' >");
        if ($(td).attr("data-name") === "bankCardNum" || $(td).attr("data-name") === "personalBankCardNum") {
            $(td).find("input").addClass("cardSpace");
            $(td).find("input").attr("maxlength", "24")
        }
        if($(td).attr("data-name") === "userBean.userName"){
            $(td).find("input").attr("name","");
            $(td).find("input").addClass("head");
            $(td).find("input").addClass("select-tree-btn");
            $(td).find("input").attr("data-href","user/user-select.html?num=1");
            $(td).find("input").attr("data-type","用户");
            $(td).find("input").attr("readonly","readonly")
        }
        if($(td).attr("data-name") === "userBean.userId"){
            $(td).find("input").addClass("userId");
        }
    });
}

// 方法1-1 变成可编辑状态后,操作框变成确认和取消
function okAndClose(trDom) {
    let doTd = trDom.find(".do-td");
    doTd.empty();
    doTd.append(
        '<span class="layui-icon layui-icon-ok ok-btn" style="margin-right: 10px;color: #009688" title="确认"></span>'
        + '<span class="layui-icon layui-icon-close close-btn" title="取消"></span>'
    );
}

// 方法1-2 将这一行tr变成可以编辑的状态------------(select)
function showToEditTrSelect(trDom) {
    trDom.find(".select-td").each(function (index, td) {
        let selectVal = $(td).attr("data-value");
        let selectId = $(td).attr("data-id");
        let name = $(td).attr("data-name");
        let name2 = $(td).attr("data-name2");
        let name3 = $(td).attr("data-name3");
        let way = $(td).attr("data-way");
        let name4 = $(td).attr("data-parent");
        var idTemp = Math.ceil(Math.random() * 10000);
        $(td).empty();
        //如果没有接口
        if (way === undefined) {
            if (name === "bankCardType") {
                $(td).append(
                    '<select title="" name="bankCardType" lay-filter="bankCardType">'
                    + '<option></option>'
                    + '<option>公户卡</option>'
                    + '<option>私户卡</option>'
                    + '<option>现金</option>'
                    + '</select>'
                );
                $(td).find("select").val(selectVal)
            }
            renderFix("bankCardType");
            return;

        }
        $(td).append(
            '<select  title="" name="' + name + '" class="' + name2 + '" lay-filter="' + name2 + '" id="idTemp' + idTemp + '" data-select-way="' + way + '" data-name3="' + name3 + '" data-parent="' + name4 + '">'
            + '<option></option>'
            + '</select>'
        );
        showSelectData($("#idTemp" + idTemp), selectId);
        layui.use(['form'], function () {
            var form = layui.form;
            layui.form.render('select');
        });
    });
}

// 方法1-3 变成select之后给select铺数据
function showSelectData(selectDom, value) {
    var way = selectDom.attr("data-select-way");
    var name3 = selectDom.attr("data-name3");
    var name4 = selectDom.attr("data-parent");
    $.get("/" + way, {}, function (data) {
        selectDom.empty();
        selectDom.append('<option></option>');
        $(data["content"]).each(function (index, item) {
            var a;
            if (name4 === undefined || name4 === "undefined") {
                a = "";
            } else {
                a = item["" + name4 + "Bean"]["" + name4 + "Name"]
            }
            selectDom.append(
                '<option value="' + item["" + name3 + "Id"] + '" data-parent="' + a + '">' + item["" + name3 + "Name"] + '</option>'
            )
        });
        layui.use(['form'], function () {
            var form = layui.form;
            layui.form.render('select');
            setTimeout(function () {
                selectDom.next(".layui-form-select").find("dd[lay-value='" + value + "']").click();
            }, 100);
        });
    }, "json")
}

// 方法1-4 变成可编辑状态后,操作框变成确认和取消
function editAndDel(trDom) {
    let doTd = trDom.find(".do-td");
    doTd.empty();
    doTd.append(
        '<span class="layui-icon layui-icon-edit edit-btn" style="margin-right: 5px;color: orange" title="编辑"></span>'
        + '<span class="layui-icon layui-icon-delete del-btn" style="color: #ff4646" title="删除"></span>'
    );
}

// 方法2 将这一行tr恢复成原来的状态(input)
function editToShow(trDom) {
    trDom.find(".edit-td").each(function (index, td) {
        let val = $(td).children("input").val();
        $(td).attr("data-value", val);
        $(td).text(val);
    });
    trDom.find(".select-td").each(function (index2, td2) {
        let val = $(td2).find("input").val();
        $(td2).attr("data-value", val);
        var lay_value = $(td2).find(".layui-this").attr("lay-value");
        $(td2).attr("data-id", lay_value);
        $(td2).text(val);
    });
    trDom.find(".show-td").each(function (index3, td3) {
        let val = $(td3).text();
        $(td3).attr("data-value", val);
    })
}

// 方法2-2 变成确认状态后,操作框变成编辑合删除
function editAndDelete(trDom) {
    let doTd = trDom.find(".do-td");
    doTd.empty();
    doTd.append(
        '<span class="layui-icon layui-icon-edit edit-btn" style="margin-right: 10px;color: orange" title="编辑"></span>'
        + '<span class="layui-icon layui-icon-delete del-btn" style="color: #ff4646" title="删除"></span>'
    );
}

// 方法3 铺表格数据
function showTable(tableDom) {
    var table_name = $(tableDom).attr("data-table");
    // 费用类别
    if (table_name === "disburse_category") {
        $.get("/" + table_name, {}, function (data) {
            var dataTr = $(tableDom).find(".dataTr");
            dataTr.empty();
            $(data["content"]).each(function (index, content) {
                dataTr.append(
                    '<tr>'
                    + '<td>' + (index + 1) + '</td>'
                    + '<td class="edit-td data-td " data-name="disburseCategoryName" data-value="' + content["disburseCategoryName"] + '">' + content["disburseCategoryName"] + '</td>'
                    + '<td style="display: none" class="display-td" data-value="' + content["disburseCategoryName"] + '">' +
                    '<input type="text" name="disburseCategoryId" value="' + content["disburseCategoryId"] + '"  >' +
                    '</td> '
                    + '<td class="do-td">'
                    + '<span class="layui-icon layui-icon-edit edit-btn" style="margin-right: 5px;color: orange" title="编辑"></span>'
                    + '<span class="layui-icon layui-icon-delete del-btn" style="color: #ff4646" title="删除"></span>'
                    + '</td>'
                    + '</tr>'
                )
            })
        });
        // 费用类型
    } else if (table_name === "disburse_type") {
        $.get("/" + table_name, {}, function (data) {
            var dataTr = $(tableDom).find(".dataTr");
            dataTr.empty();
            $(data["content"]).each(function (index, content) {
                dataTr.append(
                    '<tr>'
                    + '<td>' + (index + 1) + '</td>'
                    + '<td class="edit-td data-td " data-name="disburseTypeName" data-value="' + content["disburseTypeName"] + '">' + content["disburseTypeName"] + '</td>'
                    + '<td style="display: none" class="display-td" data-value="' + content["disburseTypeName"] + '" data-value-select="' + content["disburseCategoryBean"]["disburseCategoryName"] + '">'
                    + '<input type="text" name="disburseTypeId" value="' + content["disburseTypeId"] + '"  >'
                    + '</td>'
                    + '<td class="select-td" data-value="' + content["disburseCategoryBean"]["disburseCategoryName"] + '"  data-name="disburseCategoryBean.disburseCategoryId" data-name2="disburseCategoryName" data-name3="disburseCategory" data-way="disburse_category" data-id="' + content["disburseCategoryBean"]["disburseCategoryId"] + '">' + content["disburseCategoryBean"]["disburseCategoryName"] + '</td>'
                    + '<td class="do-td">'
                    + '<span class="layui-icon layui-icon-edit edit-btn" style="margin-right: 5px;color: orange" title="编辑"></span>'
                    + '<span class="layui-icon layui-icon-delete del-btn" style="color: #ff4646" title="删除"></span>'
                    + '</td>'
                    + '</tr>'
                )
            });
            layui.use(['form'], function () {
                var form = layui.form;
                layui.form.render('select');
            });
        })
    } else if (table_name === "disburse_detail") {//费用明细
        $.get("/" + table_name, {}, function (data) {
            var dataTr = $(tableDom).find(".dataTr");
            dataTr.empty();
            $(data["content"]).each(function (index, content) {
                dataTr.append(
                    '<tr>'
                    + '<td>' + (index + 1) + '</td>'
                    + '<td class="edit-td data-td " data-name="disburseDetailName" data-value="' + content["disburseDetailName"] + '">' + content["disburseDetailName"] + '</td>'
                    + '<td style="display: none" class="display-td" >'
                    + '<input type="text" name="disburseDetailId" value="' + content["disburseDetailId"] + '"  >'
                    + '</td>'
                    + '<td class="edit-td data-td" data-name="disburseDetailRemarks" data-value="' + content["disburseDetailRemarks"] + '">' + content["disburseDetailRemarks"] + '</td>'
                    + '<td class="edit-td data-td" data-name="disburseDetailSource" data-value="' + content["disburseDetailSource"] + '">' + content["disburseDetailSource"] + '</td>'
                    + '<td class="select-td" data-value="' + content["disburseTypeBean"]["disburseTypeName"] + '" data-name="disburseTypeBean.disburseTypeId" data-name2="disburseTypeName" data-name3="disburseType" data-parent="disburseCategory" data-way="disburse_type" data-id="' + content["disburseTypeBean"]["disburseTypeId"] + '">' + content["disburseTypeBean"]["disburseTypeName"] + '</td>'
                    + '<td class="show-td"  data-value="' + content["disburseTypeBean"]["disburseCategoryBean"]["disburseCategoryName"] + '">' + content["disburseTypeBean"]["disburseCategoryBean"]["disburseCategoryName"] + '</td>'
                    + '<td class="do-td">'
                    + '<span class="layui-icon layui-icon-edit edit-btn" style="margin-right: 5px;color: orange" title="编辑"></span>'
                    + '<span class="layui-icon layui-icon-delete del-btn" style="color: #ff4646" title="删除"></span>'
                    + '</td>'
                    + '</tr>'
                )
            });
            layui.use(['form'], function () {
                var form = layui.form;
                layui.form.render('select');
            });
            fixedHead($(".disburse_detail"))
        })
    } else if (table_name === "bank_card") {// 银行卡账号(公司)
        $.get("/table_utils/info", {"table_utils.tableName": "bank_card"}, function (data) {
            var dataTr = $(tableDom).find(".dataTr");
            dataTr.empty();
            $(data["content"]).each(function (index, content) {
                dataTr.append(
                    '<tr>'
                    + '<td>' + (index + 1) + '</td>'
                    + '<td class="select-td" data-name="bankCardType" data-value="' + content["bankCardType"] + '">' + content["bankCardType"] + '</td>'
                    + '<td class="edit-td data-td" data-name="bankCardName" data-value="' + content["bankCardName"] + '">' + content["bankCardName"] + '</td>'
                    + '<td class="edit-td data-td" data-name="bankCardNum" data-value="' + content["bankCardNum"] + '">' + content["bankCardNum"] + '</td>'
                    + '<td class="edit-td data-td" data-name="bankCardInitialValue" data-value="' + content["bankCardInitialValue"] + '">' + content["bankCardInitialValue"] + '</td>'
                    + '<td style="display: none" class="display-td">'
                    + '<input type="text" name="bankCardId" value="' + content["bankCardId"] + '"  >'
                    + '</td>'
                    + '<td class="do-td">'
                    + '<span class="layui-icon layui-icon-edit edit-btn" style="margin-right: 5px;color: orange" title="编辑"></span>'
                    + '<span class="layui-icon layui-icon-delete del-btn" style="color: #ff4646" title="删除"></span>'
                    + '</td>'
                    + '</tr>'
                )
            });
            layui.use(['form'], function () {
                var form = layui.form;
                layui.form.render('select');
            });
        })
    } else if (table_name === "personal_bank_card") {// 银行卡账号(个人)
        $.get("/table_utils/info",{"table_utils.tableName":"personal_bank_card"},function (data) {
            var dataTr = $(tableDom).find(".dataTr");
            dataTr.empty();
            $(data["content"]).each(function (index, content) {
                dataTr.append(
                    '<tr>'
                    + '<td>' + (index + 1) + '</td>'
                    + '<td class="edit-td data-td" data-name="userBean.userName" data-value="'+content["userBean"]["userName"]+'">' + content["userBean"]["userName"] + '</td>'
                    + '<td style="display: none" class="edit-td data-td" data-name="userBean.userId" data-value="'+content["userBean"]["userId"]+'">' + content["userBean"]["userId"] + '</td>'
                    + '<td class="edit-td data-td" data-name="personalBankCardName" data-value="' + content["personalBankCardName"] + '">' + content["personalBankCardName"] + '</td>'
                    + '<td class="edit-td data-td" data-name="personalBankCardNum" data-value="' + content["personalBankCardNum"] + '">' + content["personalBankCardNum"] + '</td>'
                    + '<td class="edit-td data-td" data-name="personalBankCardInitialValue" data-value="' + content["personalBankCardInitialValue"] + '">' + content["personalBankCardInitialValue"] + '</td>'
                    + '<td style="display: none" class="display-td">'
                    + '<input type="text" name="personalBankCardId" value="' + content["personalBankCardId"] + '"  >'
                    + '<td class="do-td">'
                    + '<span class="layui-icon layui-icon-edit edit-btn" style="margin-right: 5px;color: orange" title="编辑"></span>'
                    + '<span class="layui-icon layui-icon-delete del-btn" style="color: #ff4646" title="删除"></span>'
                    + '</td>'
                    + '</tr>'
                )
            });
        })
    }
}

// 方法4 如果不需要做更改,则将该tr恢复到原来的数据
function returnTr(trDom) {
    trDom.find("td:not(.do-td , .display-td)").each(function (index, td) {
        $(this).text($(td).attr("data-value"))
    });
    editAndDel(trDom);
}

// 方法5 获取到用户数据
function getSelectedUser(data) {
    thisTree.closest("tr").find(".userId").val(data["content"][0]["userId"]);
}
