let id = getParamForUrl("id");
let management;
$(function () {
    layui.use(['layer', 'form'], function () {
        // noinspection JSUnusedLocalSymbols
        let layer = layui.layer,
            $ = layui.jquery,
            form = layui.form;
        // showManagementPartner("managementPartner");

        let tips;
        $(document).on("mouseenter", ".double-input", function () {
            tips = layer.tips("<span style='color: #2E2D3C;'>百分比请输入小数:如 5%为0.05 , 12%为0.12 , 100%为1</span>", this, {tips: [1, '#CBCBD3']});
        });
        $(document).on("mouseleave", ".double-input", function () {
            layer.close(tips);
        });

    });

    renderFix('managementCommissionMode', function () {

        $("[name='managementCommissionMode'] + .layui-form-select dd").click(function () {
            showManagementPartner("managementPartner", "");
            let mode = $(this).attr("lay-value");
            //如果是劳务 , 承包 ,  配合 显示合作费
            let managementCooperationFee = $(".managementCooperationFee");
            if (mode === "劳务" || mode === "承包" || mode === "配合") {
                managementCooperationFee.show();
            } else {
                managementCooperationFee.hide().val("");
            }
            //如果是劳务   显示公司利润
            let managementCorporateProfits = $(".managementCorporateProfits");
            if (mode === "劳务") {
                managementCorporateProfits.show();
            } else {
                managementCorporateProfits.hide().val("");
            }
            //如果是承包   显示承包费
            let managementContractFee = $(".managementContractFee");
            if (mode === "承包") {
                managementContractFee.show();
            } else {
                managementContractFee.hide().val("");
            }

            //是否是初始 去掉身份标识  和推荐人
            if (mode === "初始") {
                $(".managementPartnersIdentity-dom , .managementRefereesName").remove();
            } else {
                // managementContractFee.hide().val("");
            }
        });

        if (id !== null) {
            $.get("/management", {id: id}, function (data) {
                management = data.content[0];
                showData4Object(management);
                implementHead($(".managementMainHead") , management.managementMainHead );

                let dl = $("[name='managementCommissionMode']").next().find("dl");
                //如果不是1910后 只显示初始
                if ( management.projectBean.earningsCompanyBean["earningsCompanyName"] === "1910后"
                || management.projectBean.earningsCompanyBean["earningsCompanyId"] === 3  ) {
                    //隐藏初始
                    dl.children("[lay-value='初始']").hide();
                } else {
                    //隐藏不是初始的
                    dl.children(":not([lay-value='初始']) ").hide();
                }

                if ($("input.projectManagementType").val() === "自营") {
                    dl.children("[lay-value='配合']").hide();
                    // $(".managementCooperativePartnerName").hide();
                } else {
                    // $(".managementPartner").hide();
                    dl.children("[lay-value='牛逼(特殊)']").hide();
                    dl.children("[lay-value='牛逼(普通)']").hide();
                    dl.children("[lay-value='小挣']").hide();
                    dl.children("[lay-value='劳务']").hide();
                    dl.children("[lay-value='承包']").hide();
                }
                try {
                    showManagementPartner("managementPartner", management["managementPartnersBean"]["managementPartnersId"]);
                } catch (e) {
                }
            }, "json");
        }
    });
    $("select[lay-filter='managementCommissionMode']").attr("lay-ignore", "lay-ignore").next().find("dd").click(function () {
        showManagementPartner("managementPartner", "");
    });

    //预估回款条件添加
    $(".add_management_money_back_estimation").click(function () {

        let TempId = Math.ceil(Math.random() * 100000);

        let tBodyTemp = $("#management_money_back_estimation_tBody");
        tBodyTemp.append(
            '<tr>' +
            '<td>' +
            '<!--suppress HtmlUnknownAttribute -->' +
            '<select class="money_back_type" lay-filter="type' + TempId + '" >' +
            '<option>选择回款方式</option>' +
            '<option>按时间回款</option>' +
            '<option>按条件回款</option>' +
            '</select>' +
            '</td>\n' +
            '<td>' +
            '<input style="display: none;" type="text" autocomplete="none" class="lay-date layui-input">' +
            '   <!--suppress HtmlUnknownAttribute -->' +
            '   <select style="display: none" class="money_back_estimation select-hidden"  data-select-hidden="data-select-hidden"  lay-filter="estimation' + TempId + '">' +
            '       <option>条件1</option>' +
            '       <option>条件2</option>' +
            '       <option>条件3</option>' +
            '   </select>' +
            '</td>\n' +
            '<td>' +
            '<input name="" class="layui-input " placeholder="预估金额" />' +
            '</td>\n' +
            '<td>/</td>\n' +
            '<td>/</td>\n' +
            '<td>/</td>\n' +
            '<td class="do-td">\n' +
            '    <button>\n' +
            '        <span class="layui-icon layui-icon-ok ok-btn new-add" title="添加" style="margin-right: 10px;color: #009688"></span>\n' +
            '    </button>\n' +
            '    <button>\n' +
            '        <span class="layui-icon layui-icon-delete del-btn" data-id="2" style="color: #ff4646" title="删除"></span>\n' +
            '    </button>\n' +
            '</td>' +
            '</tr>'
        );
        renderFix("type" + TempId);
        renderFix("estimation" + TempId);
        layui.use(['layer', 'laydate', 'form'], function () {
            let layDate = layui.laydate;
            layDate.render({
                elem: '.lay-date' //指定元素
            });
        });

        setTimeout(function () {
            $("select.money_back_type + .layui-form-select dd").click(function () {
                console.log("aa");
                let dateTemp = $(this).closest("tr").find(".lay-date");
                if (this.innerText === "按时间回款") {
                    dateTemp.show();
                    dateTemp.next().attr("data-select-hidden", "data-select-hidden");
                    dateTemp.next().hide().next().hide();
                } else {
                    dateTemp.hide();
                    dateTemp.next().removeAttr("data-select-hidden");
                    dateTemp.next().show().next().show();
                }
            });
        }, 100);


    });

    //给select框绑事件
    // $(document).on( "click" ,  "select.money_back_type + .layui-form-select  dd" , function () {
    //     console.log("a");
    //     let dateTemp = $(this).closest("tr").find(".lay-date");
    //     if ( this.innerText === "按时间回款" ) {
    //         dateTemp.show();
    //         dateTemp.next().hide().next().hide();
    //     } else {
    //         dateTemp.hide();
    //         dateTemp.next().show().next().show();
    //     }
    // } );
    // $("select.money_back_type + .layui-form-select dd").click(function () {
    //     console.log("aa");
    //     let dateTemp = $(this).closest("tr").find(".lay-date");
    //     if ( this.innerText === "按时间回款" ) {
    //         dateTemp.show();
    //         dateTemp.next().hide().next().hide();
    //     } else {
    //         dateTemp.hide();
    //         dateTemp.next().show().next().show();
    //     }
    // });


    //提交
    $(".confirm-btn").click(function () {
        let dataR = $.param({
            tableName: "management",
            "_method": "PUT",
            "createUserBean.userId": window.top.userTemp.userId,
            "managementCreateTime": formatDate(new Date(), 8),
            "createJobBean.jobId": window.top.userTemp["jobBean"]["jobId"]
        }) + "&" + $("#inputForm").serialize();

        $.post("/table_utils", dataR, function (data) {
            if (data) {
                try {
                    window.parent.flush();
                } catch (e) {
                }
                layer.msg('操作成功 2秒后自动刷新', {
                    time: 2000 //2秒关闭（如果不配置，默认是3秒）
                }, function () {
                    window.location.reload();
                });
            } else {
                layer.msg('添加失败', {icon: 5});
            }
        }, "json").fail(function (res) {
            layer.msg('数据提交失败 请刷新重试', {icon: 5});
            console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
        }, "json");
    });

    // 铺经营主负责人
    implementHead($(".managementMainHead"));
});

// 铺经营主负责人
function implementHead(dom, tempHead) {
    // 获取到所有用户数据,并且录入默认项是登陆者
    $.get("/table_utils/info", {"table_utils.tableName": "user"}, function (data) {
        dom.empty();
        dom.append('<option></option>');
        $(data["content"]).each(function (index, content) {
            dom.append('<option>' + content["userName"] + '</option>')
        });
        renderFix("userName", function () {
            if (tempHead !== undefined) {
                dom.next(".layui-form-select").find("dd[lay-value='" + tempHead + "']").click();
            }
        });
    });
}

// //铺已有数据的合伙人 合作伙伴 推荐人
// function showMyPartner() {
//     if ( ( ! $.isEmptyObject(referees)) && ( ! $.isEmptyObject( management ) )  ) {
//         //如果是内部合伙人
//         let idLeft = $.isEmptyObject(management["managementOuterPartnerBean"] ) || management["managementOuterPartnerBean"]=== ""
//             ? "inner-" + management["managementInnerPartnerBean"]["managementInnerPartnerId"]
//             :  "outer-" + management["managementOuterPartnerBean"]["managementOuterPartnerId"];
//         $("select.managementPartner").next().find("dd[lay-value='"+idLeft+"']").click();
//         let managementCommissionMode = $("select.managementCommissionMode");
//         managementCommissionMode.next().find("dd[lay-value='"+managementCommissionMode.val()+"']").click();
//
//     }
// }


// noinspection NonAsciiCharacters
let mapping = {
    "牛逼(特殊)": "内部合伙人"
    , "牛逼(普通)": "内部合伙人"
    , "小挣": "内部合伙人"
    , "劳务": "外部合伙人"
    , "承包": "外部合伙人 , 合作伙伴"
    , "配合": "外部合伙人 , 合作伙伴"
    , "初始": "外部合伙人 , 合作伙伴 , 内部合伙人"
};

let partnerReferee = {};
let partnerRefereeName = {};
let managementPartnersIdentityMapping = {};

let managementPartner = null;

function showManagementPartner(layFilter, value, rollback) {
    if (managementPartner === null) {
        $.get("/table_utils",
            {
                "table_utils.tableName": "management_partners",
                "table_utils.fields":
                    "managementPartnersId"
                    + "$managementPartnersIdentity"
                    + "$userBean.userName"
                    + "$refereesUserBean.userId"
                    + "$refereesUserBean.userName"
            },
            showTemp, "json");
    } else {
        showTemp(managementPartner);
    }

    function showTemp(data) {
        managementPartner = data;
        let managementPartnerSelect = $("select[lay-filter='" + layFilter + "']");
        managementPartnerSelect.empty();
        managementPartnerSelect.append("<option></option>");
        partnerReferee = {};
        $(data.content).each(function (index, managementPartnerTemp) {
            //根据提成模式 决定铺哪儿些合伙人
            partnerReferee[managementPartnerTemp[0]] = managementPartnerTemp[3];
            partnerRefereeName[managementPartnerTemp[0]] = managementPartnerTemp[4];
            managementPartnersIdentityMapping[managementPartnerTemp[0]] = managementPartnerTemp[1];
            let managementCommissionMode = $("[lay-filter='managementCommissionMode']");
            let mode = managementCommissionMode.val();
            // console.log("根据提成模式 决定铺哪儿些合伙人" + mapping[mode] + "## " + managementPartnerTemp[1]);
            if (typeof mapping[mode] !== "undefined" && mapping[mode].indexOf(managementPartnerTemp[1]) !== -1) {
                managementPartnerSelect.append(
                    "<option value='" + managementPartnerTemp[0] + "' >" + managementPartnerTemp[2] + "</option>"
                );
            }
        });
        renderFix(layFilter, function () {
            managementPartnerSelect.next().find("dd").click(function () {
                // console.log(parseInt($(this).attr("lay-value")));
                //推荐人输入框
                let refereeInputHidden = $("[name='refereesUserBean.userId']");
                refereeInputHidden.val(partnerReferee[parseInt($(this).attr("lay-value"))]);
                let refereeInput = $("[data-notice-path='refereesUserBean.userName']");
                refereeInput.val(partnerRefereeName[parseInt($(this).attr("lay-value"))]);
                //身份标识符
                let managementPartnersIdentityInput = $("[name='managementPartnersIdentity']");
                managementPartnersIdentityInput.val(managementPartnersIdentityMapping[parseInt($(this).attr("lay-value"))]);
            });
            //点击默认值
            managementPartnerSelect.next().find("dd[lay-value='" + value + "']").click();
            if (typeof rollback === "function") {
                rollback();
            }
        });
    }

}
