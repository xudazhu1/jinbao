$(function () {
    // layui 初始化
    layui.use(['layer'], function () {
        // noinspection JSUnusedLocalSymbols
        let layer = layui.layer;
        let form = layui.form;
        layui.form.render('select');

        setTimeout(function () {
            form.on('select(disburseCategoryName)', function (data) {
                $(this).closest("tr").find(".display-input").val(data.value);
            });
        }, 100);

    });

    // 2 双击可编辑该td
    $(document).on("dblclick", "#management_partners td", function () {
        showToEditTr($(this).closest("tr"));
    });
    // 3 单击编辑按钮可编辑该tr
    $(document).on("click", ".edit-btn", function () {
        showToEditTr($(this).closest("tr"));
    });

    // 4 确认添加
    $(document).on("click", ".ok-btn", function () {
        let thisTemp = $(this);
        let data_name = $(this).closest("table").attr("data-table");
        let trTemp = $(this).closest("tr");
        let itemData = getJson4Dom(trTemp);// 将需要编辑的这一行tr序列化成json
        itemData["tableName"] = data_name;
        $.post("/table_utils", itemData, function (data) {
            if (data) {
                if (thisTemp.hasClass("new-add")) {
                    layer.msg("添加成功", {icon: 1});
                    showTable();
                } else {
                    editToShow(trTemp);
                    layer.msg("修改成功", {icon: 1});
                }
            } else {
                layer.msg("添加失败", {icon: 1});
            }
        } , "json").fail(function (res) {
            layer.msg('数据提交失败 请刷新重试', {icon: 5});
            console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
        } , "json" ) ;
    });

    // 6 删除数据
    $(document).on("click", ".del-btn", function () {
        let table_name = $(this).closest("table").attr("data-table");
        let data_id = $(this).attr("data-id");
        layer.confirm('确认删除吗？', {
            btn: ['确认', '取消'] //按钮
        }, function () {
            layer.close(layer.index);
            $.post("/table_utils",
                {"_method": "DELETE", "id": data_id , tableName :table_name}, function (data) {
                if (data) {
                    layer.msg("删除成功", {icon: 1});
                    showTable();
                } else {
                    layer.msg("删除失败,请刷新重试!", {icon: 5})
                }
            }, "json").fail(function (res) {
                layer.msg('数据提交失败 请刷新重试', {icon: 5});
                console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
            }, "json");
        })
    });

    // 6 删除推荐人
    $(document).on("click", ".delete-referee", function () {
        let data_id = $(this).attr("data-id");
        layer.confirm('确认删除吗？', {
            btn: ['确认', '取消'] //按钮
        }, function () {
            layer.close(layer.index);
            $.post("/user/remove_referee",
                {"_method": "PUT", "id": data_id }, function (data) {
                if (data) {
                    layer.msg("删除成功", {icon: 1});
                    showTable(function () {
                        myRenderReferees("refereesUserBean.userId" , true );
                    });
                } else {
                    layer.msg("删除失败,请刷新重试!", {icon: 5})
                }
            }, "json").fail(function (res) {
                layer.msg('数据提交失败 请刷新重试', {icon: 5});
                console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
            }, "json");
        });
    });

    // 7 点击取消,恢复成原来的状态
    $(document).on("click", ".close-btn", function () {
        returnTr($(this).closest("tr"));
    });


    showUsers4Select("userBean.userId" , false , "" , function () {
        showUsers4Select("userBean.userIdA" , false  , "" , function () {
            
        });
    });
    // 5 铺表格数据,仅适用于输入框
    myRenderReferees("refereesUserBean.userId" , false , "" , function () {
        showTable();
    } );
    renderFix("managementPartnersIdentity" , function () {
        $("[lay-filter='managementPartnersIdentity']").attr("lay-ignore" , "lay-ignore").hide();
        let refereesTemp = $("[lay-filter='refereesUserBean.userId']");
        $("[lay-filter='managementPartnersIdentity'] + .layui-form-select dd").click(function () {
            if ( this.innerText === "内部合伙人" ) {
                refereesTemp.val("");
                renderFix("refereesUserBean.userId");
                refereesTemp.attr("readonly" , "readonly" );
            } else {
                refereesTemp.removeAttr("readonly"  );
            }
        });
    });


    //找到推荐人角色的Id 赋给name属性
    $.get("/table_utils" ,
        {"table_utils.tableName" : "role" ,
            "table_utils.fields" :
                "roleId"
        , "$D.roleName" : "推荐人" } ,
        function (data) {
            refereesId =  data.content ;
            $("#refereesId").val(refereesId);
        } , "json" );

});

let refereesId = null;

// 方法1 将这一行tr变成可以编辑的状态
function showToEditTr(trDom) {
    trDom.find(".edit-td").each(function (index, td) {
        let textTemp = td.innerText;
        let name = $(td).attr("data-name");
        $(td).empty();
        $(td).append("<input class='layui-input' type='text' name='" + name + "' value='" + textTemp + "' >");
    });
    trDom.find("select[readonly]").removeAttr("readonly");
    let doTd = trDom.find(".do-td");
    let idTemp = doTd.find(".del-btn").attr("data-id");
    doTd.empty();
    doTd.append(
        '<span class="layui-icon layui-icon-ok ok-btn" style="margin-right: 10px;color: #009688" title="确认"></span>'
        + '<span class="layui-icon layui-icon-close close-btn" data-id="'+idTemp+'" title="取消"></span>'
    );
}

// 方法2 将这一行tr恢复成原来的状态
function editToShow(trDom) {
    trDom.find(".edit-td").each(function (index, td) {
        // noinspection JSValidateTypes
        let val = $(td).children("input").val();
        $(td).text(val);
    });
    trDom.find("select").attr("readonly" , "readonly");
    let doTd = trDom.find(".do-td");
    doTd.empty();
    doTd.append(
        '<span class="layui-icon layui-icon-edit edit-btn" style="margin-right: 10px;color: orange" title="编辑"></span>'
        + '<span class="layui-icon layui-icon-delete del-btn" style="color: #ff4646" title="删除"></span>'
    );
}

let referees = null ;
// 方法3 铺表格数据
function showTable( rollback ) {
    $.get("/table_utils" ,
        {"table_utils.tableName" : "user" ,
            "table_utils.fields" :
                "userId"
                + "$userName" ,
            "$D.roleBeans.roleName" : "推荐人"
        } ,
        showTemp , "json" );

    function showTemp(data) {
        referees = data;
        let referees_users = $("#referees_users");
        referees_users.empty();
        $(referees.content).each(function (index , referee) {
            referees_users.append("<tr>" +
                "<td>" + referee[0] + "</td>" +
                "<td>" + referee[1] + "</td>" +
                '<td><span class="layui-icon layui-icon-delete delete-referee" style="margin-right: 10px;color: orange" data-id="'+referee[0]+'" title="删除"></span></td>' +
                "</tr>");
        });
        if ( typeof ( rollback ) === "function" ) {
            rollback(referees);
        }
    }
    $.get("/table_utils" ,
        {"table_utils.tableName" : "management_partners" ,
            "table_utils.fields" :
                "userBean.userId"
                + "$managementPartnersIdentity"
                + "$refereesUserBean.userId"
                + "$managementPartnersId"
                + "$managementPartnersRefereesInitLevel"
                + "$managementPartnersRefereesLevel"
        } ,
        showPartnersTemp , "json" );
    function showPartnersTemp( data ) {
        let management_partners = $("#management_partners");
        management_partners.empty();
        $(data.content ).each(function (index, management_partner) {
            management_partners.append (
                " <tr >\n" +
                "   <td></td>\n" +
                "   <td>\n" +
                "       <input type='hidden' name='managementPartnersId' value='"+management_partner[3]+"' >" +
                "       <!--suppress HtmlUnknownAttribute -->\n" +
                "       <select readonly='readonly' title=\"\" class=\"userId\" name='userBean.userId' lay-filter=\"userBean.userId"+index+"\">\n" +
                $("[name='userBean.userId']")[0].innerHTML +
                "       </select>\n" +
                "   </td>\n" +
                "   <td>\n" +
                "       <!--suppress HtmlUnknownAttribute -->\n" +
                "       <select readonly='readonly' title=\"\" name='managementPartnersIdentity' class=\"managementPartnersIdentity\"\n" +
                "               lay-filter=\"managementPartnersIdentity"+index+"\">\n" +
                "           <option></option>\n" +
                "           <option value=\"内部合伙人\">内部合伙人</option>\n" +
                "           <option value=\"外部合伙人\">外部合伙人</option>\n" +
                "           <option value=\"合作伙伴\">合作伙伴</option>\n" +
                "       </select>\n" +
                "   </td>\n" +
                "   <td>\n" +
                "       <!--suppress HtmlUnknownAttribute -->\n" +
                "<select readonly='readonly' title=\"\" class=\"refereesUserId\"\n" +
                "          name='refereesUserBean.userId'     lay-filter=\"refereesUserBean.userId"+index+"\">\n" +
                $("[name='refereesUserBean.userId']")[0].innerHTML +
                "       </select>\n" +
                "   </td>\n" +
                "<td>\n" +
                "    <!--suppress HtmlUnknownAttribute -->\n" +
                "    <select readonly='readonly' title=\"\" class=\"managementPartnersRefereesInitLevel\" name=\"managementPartnersRefereesInitLevel\"\n" +
                "            lay-filter=\"managementPartnersRefereesInitLevel"+index+"\">\n" +
                "        <option></option>\n" +
                "        <option value=\"P1\">P1</option>\n" +
                "        <option value=\"P2\">P2</option>\n" +
                "        <option value=\"P3\">P3</option>\n" +
                "        <option value=\"P4\">P4</option>\n" +
                "        <option value=\"P5\">P5</option>\n" +
                "        <option value=\"P6\">P6</option>\n" +
                "        <option value=\"P7\">P7</option>\n" +
                "        <option value=\"P8\">P8</option>\n" +
                "        <option value=\"P9\">P9</option>\n" +
                "        <option value=\"P10\">P10</option>\n" +
                "    </select>\n" +
                "</td>" +
                "<td>\n" +
                "    <!--suppress HtmlUnknownAttribute -->\n" +
                "    <select readonly='readonly' title=\"\" class=\"managementPartnersRefereesLevel\" name=\"managementPartnersRefereesLevel\"\n" +
                "            lay-filter=\"managementPartnersRefereesLevel"+index+"\">\n" +
                "        <option></option>\n" +
                "        <option value=\"P1\">P1</option>\n" +
                "        <option value=\"P2\">P2</option>\n" +
                "        <option value=\"P3\">P3</option>\n" +
                "        <option value=\"P4\">P4</option>\n" +
                "        <option value=\"P5\">P5</option>\n" +
                "        <option value=\"P6\">P6</option>\n" +
                "        <option value=\"P7\">P7</option>\n" +
                "        <option value=\"P8\">P8</option>\n" +
                "        <option value=\"P9\">P9</option>\n" +
                "        <option value=\"P10\">P10</option>\n" +
                "    </select>\n" +
                "</td>" +
                "   <td class='do-td'>\n" +
                '<span class="layui-icon layui-icon-edit edit-btn" style="margin-right: 10px;color: orange" title="编辑"></span>' +
                '<span class="layui-icon layui-icon-delete del-btn" style="margin-right: 10px;color: orange" data-id="'+management_partner[3]+'" title="删除"></span>' +
                "   </td>\n" +
                "</tr>"
            );
            // showUsers4Select("userBean.userId" + index , true , management_partner[0]);
            // myRenderReferees("refereesUserBean.userId" + index , true , management_partner[2] );
            $("[lay-filter='userBean.userId"+index+"']").val( management_partner[0] );
            $("[lay-filter='refereesUserBean.userId"+index+"']").val( management_partner[2] );
            $("[lay-filter='managementPartnersIdentity"+index+"']").val( management_partner[1] );
            $("[lay-filter='managementPartnersRefereesInitLevel"+index+"']").val( management_partner[4] );
            $("[lay-filter='managementPartnersRefereesLevel"+index+"']").val( management_partner[5] );
        });
        layui.form.render('select');
    }

}
// 方法4 如果不需要做更改,则将该tr恢复到原来的数据
function returnTr(trDom) {
    let val = trDom.find(".display-td").attr("data-value");
    trDom.find(".edit-td").text(val);
    trDom.find("select").attr("readonly" , "readonly" );
    let doTd = trDom.find(".do-td");
    let idTemp = doTd.find(".close-btn").attr("data-id");
    doTd.empty();
    doTd.append('<span class="layui-icon layui-icon-edit edit-btn" style="margin-right: 10px;color: orange" title="编辑"></span>'
        + '<span class="layui-icon layui-icon-delete del-btn" data-id="'+idTemp+'" style="color: #ff4646" title="删除"></span>'
    );
}

let users = null;
function showUsers4Select(layFilter , isReadonly ,  defaultValue ,  rollback) {
    let rollback1 = rollback;
    if ( users === null ) {
        $.ajax({
            type: "get",
            url: "/table_utils",
            data: {"table_utils.tableName" : "user" ,
                "table_utils.fields" :
                    "userId"
                    + "$userName"
                // + "$userBean.userName"
                // + "$refereesUserBean.userName"
            },
            dataType : "json" ,
            async: true,
            success: showTemp
        });
        // $.get("/table_utils" ,
        //     {"table_utils.tableName" : "user" ,
        //         "table_utils.fields" :
        //             "userId"
        //             + "$userName"
        //             // + "$userBean.userName"
        //             // + "$refereesUserBean.userName"
        //     } ,
        //     showTemp , "json" );
    } else {
        showTemp(users);
    }

    function showTemp(data) {
        users = data;
        let managementPartnerSelect = $("select[lay-filter='" + layFilter + "']");
        // console.log(layFilter + " = " + isReadonly );
        if ( ! isReadonly ) {
            managementPartnerSelect.removeAttr("readonly");
        } else {
            managementPartnerSelect.attr("readonly" , "readonly" );
        }
        managementPartnerSelect.empty();
        managementPartnerSelect.append("<option></option>");
        $(data.content).each(function (index, managementPartnerTemp) {
            managementPartnerSelect.append(
                "<option value='" + managementPartnerTemp[0] + "' >" + managementPartnerTemp[1] + "</option>"
            );
        });
        renderFix(layFilter , function () {
            if ( defaultValue !== undefined ) {
                managementPartnerSelect.next(".layui-form-select").find("dd[lay-value='"+defaultValue+"']").click();
            }
            if ( typeof ( rollback1 ) === "function"  ) {
                rollback1();
            }
        });
    }
}

//渲染推荐人
function myRenderReferees(layFilter , isReadonly ,  defaultValue , rollback ) {

    if ( referees === null ) {
        $.ajax({
            type: "get",
            url: "/table_utils",
            data: {"table_utils.tableName" : "user" ,
                "table_utils.fields" :
                    "userId"
                    + "$userName" ,
                "$D.roleBeans.roleName" : "推荐人"
            },
            dataType : "json" ,
            async: true,
            success: showTemp
        });
        // //设置为同步
        // $.ajaxSettings.async = false;
        // $.get("/table_utils" ,
        //     {"table_utils.tableName" : "user" ,
        //         "table_utils.fields" :
        //             "userId"
        //             + "$userName" ,
        //         "$D.roleBeans.roleName" : "推荐人"
        //     } ,
        //     showTemp , "json" );
    } else {
        showTemp(referees);
    }

    function showTemp(data) {
        referees = data;
        let dom = $("select[lay-filter='" + layFilter + "']");
        if ( ! isReadonly ) {
            dom.removeAttr("readonly" );
        } else {
            dom.attr("readonly" , "readonly" );
        }
        dom.empty();
        let options = "<option></option>";
        $(referees.content).each(function (index , referee) {
            options += "<option value='"+referee[0]+"'>"+referee[1]+"</option>";
        });
        dom.append(options);

        renderFix(layFilter , function () {
            if ( defaultValue !== undefined ) {
                dom.next(".layui-form-select").find("dd[lay-value='"+defaultValue+"']").click();
            }
            if ( typeof ( rollback) === "function"  ) {
                rollback();
            }
        });
    }

}