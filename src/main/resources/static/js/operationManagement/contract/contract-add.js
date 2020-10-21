var thisTree;
var tempId;
var del_file_ids;

let id = getParamForUrl("id");

$(function () {

    // 1 铺项目编号
    let ajax1 = $.get("/project/project_num_and_name", {}, function (data) {
        $(data).each(function (index, item) {
            var projectId = $(".projectId");
            projectId.append(
                '<option value="' + item["projectId"] + '"  data-name="' + item["projectName"] + '">' + item["projectNum"] + '</option>'
            )
        });
        layui.use(['form'], function () {
            var form = layui.form;
            layui.form.render('select');
        });
    }, "json");

    // 1 项目编号规则生成
    let projectNum;
    let type = null; //经营类型
    let location = null; //地点
    let company = null; //收益单位

    // 0 layui配置
    //注意：折叠面板 依赖 element 模块，否则无法进行功能性操作
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
            form.on('select(projectId)', function (data) {
                var projectId = data.value;
                if (projectId !== "") {
                    $.get("/table_utils/info", {"table_utils.tableName" : "contract" , "projectBean.projectId": projectId }, function (contractPage) {
                        $.get("/table_utils/info", {"table_utils.tableName" : "project" , "projectId": projectId }, function (projectPage) {
                            console.log(projectPage);
                            let data = projectPage.content[0];

                            projectNum = data["projectNum"];

                            $("[name]:not(.projectId)").val("");
                            // data["contractBean"] = contractPage.content[0];
                            // data["contractBean"]["projectBean"] = data;
                            // showData4Object(data["contractBean"]);
                            // otherDataShow(data["contractBean"]);
                        } , "json" );
                    }, "json");
                } else {
                    $("input").val("");//输入框清空
                    $("textarea").val("");//多行输入框清空
                    var select = 'dd[lay-value=""]';//将选择框清空
                    $('.project-relate').siblings("div.layui-form-select").find('dl').find(select).click();
                    $(".download_file").empty();
                    $(".quota").empty();//实施部清空
                }
            });
        }, 100);

        // 当合同状态contractState改成已签时,则签订时间增加 .need-input 这个类,表示必须填写签订时间,否则删掉这个类
        form.on('select(contractState)', function (data) {
            var contractSigningDate = $(".contractSigningDate");
            if (data.value === "已签") {
                contractSigningDate.addClass("need-input")
            } else {
                contractSigningDate.removeClass("need-input")
            }
        });

        // 0-1-3 项目地点 编号
        setTimeout(function () {
            form.on('select(projectLocationName)', function () {
                location = $(".projectLocationName option:selected").attr("data-parent");
                $.when(ajax1).done(function () {
                    if(location) {
                        location = Mtils.utils.makePy(location, true);
                        console.log(location);
                        projectNum = projectNum.substring(0,5) + location + projectNum.substring(7);
                        $("#projectNum").val(projectNum);
                    }else {
                        $(".projectNum").val("");
                    }
                });

            });
        },100);

        // 0-1-2 经营类型 编号
        setTimeout(function () {
            form.on('select(projectManagementType)', function (data) {
                type = Mtils.utils.makePy(data.value, true);
                $.when(ajax1).done(function () {
                    console.log(projectNum+ '-->');
                    if(type) {
                        projectNum = type + projectNum.substring(2) ;

                        $("#projectNum").val(projectNum);
                    }else {
                        $("#projectNum").val("");
                    }
                });
            });
        },100);

        // 0-1-4 收益单位 编号
        setTimeout(function () {
            form.on('select(earningsCompanyName)', function () {
                $.when(ajax1).done(function () {
                    company = $(".earningsCompanyName option:selected").attr("data-num");
                    $.get("/project_num", {"tag": company}, function (data) {
                        if(company) {
                            $("#projectNum").val(projectNum.substring(0,2) + company +projectNum.substring(5,7) + data)
                        }else {
                            $("#projectNum").val("");
                        }
                    });

                });

            });
        },100);
    });

    // 1 通知人或代办人删除事件
    $(document).on("click", ".user-div i", function () {
        $(this).parent("button").remove();
    });

    // 2 一键清空通知人或代办人
    $(document).on("click", ".all-remove", function () {
        $(this).parent(".user-div").children().remove();
    });

    //3 扫描件上传-----------------------------------------------------

    //3-1 点击附件上传按钮的时候，触发file的click事件
    $(document).on('click', '#btn_file_upload', function () {
        var temp = Math.ceil(Math.random() * 100000);
        var file_upload = '<input type="file" name="' + temp + '"  class="file_upload" id="file_upload_' + temp + '" style="display: none">';
        $(this).next().append(file_upload);
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
        var added_file = $('#uploaded_file');
        // 文件上传大小限制
        var allSize = 0;
        $(this).closest("#uploaded_file").children("input").each(function (index2, file2) {
            allSize += file2["files"][0].size;
        });

        // 上传文件大小最大为10MB
        if (allSize > maxFileSize) {
            // 只有一个文件
            if ($(this).closest("#uploaded_file").children("input").length === 1) {
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

    //4 选择通知人 待办人
    $(document).on('click', '.select-tree-btn', function () {
        var href = $(this).attr("data-href");
        layer.ready(function () {
            layerIndex = layer.open({
                type: 2,
                title: "添加通知人",
                maxmin: false,
                area: ['700px', '450px'],
                content: href
            });
        });
    });



    // 6 铺项目地点数据
    projectLocation();
    function projectLocation(projectLocationId){
         $.get("/project_location", {}, function (data) {
            $(data).each(function (index, item) {
                var projectLocationName = $(".projectLocationName");
                let itemTemp = item;
                while (!$.isEmptyObject(itemTemp["projectLocationParent"])) {
                    itemTemp = itemTemp["projectLocationParent"];
                }
                let itemName = itemTemp["projectLocationName"];
                if(itemName === undefined){
                    itemName = item["projectLocationName"];
                }
                projectLocationName.append(
                    '<option value="' + item["projectLocationId"] + '" data-parent="' + itemName + '">' + item["projectLocationName"] + '</option>'
                )
                if(projectLocationId){
                  $('select[name="projectBean.projectLocationBean.projectLocationId"]').val(projectLocationId); // 获取下拉的name值，用val渲染
                }
            });
            layui.use(['form'], function () {
                var form = layui.form;
                layui.form.render('select');
            });

        }, "json");
    }

    // 7 铺收益单位
    revenueUnit();
    function revenueUnit(earningsCompanyId){
        $.get("/earnings_company", {}, function (data) {
            $(data).each(function (index, item) {
                var earningsCompanyName = $(".earningsCompanyName");
                earningsCompanyName.append(
                    '<option value="' + item["earningsCompanyId"] + '" data-num="' + item["earningsCompanyTag"] + '">' + item["earningsCompanyName"] + '</option>'
                )
                if(earningsCompanyId){
                  $('select[name="projectBean.earningsCompanyBean.earningsCompanyId"]').val(earningsCompanyId); // 获取下拉的name值，用val渲染
                }
            });
            layui.use(['form'], function () {
                var form = layui.form;
                layui.form.render('select');
            });
        }, "json");
    }
    //8 选择 经营主负责人 通知人 代办人
    $(document).on('focus', '.select-tree-btn', function () {
        var href = $(this).attr("data-href");
        var type = $(this).attr("data-type");
        thisTree = $(this);
        layer.ready(function () {
            layerIndex = layer.open({
                type: 2,
                title: "添加" + type + "人",
                maxmin: false,
                area: ['700px', '450px'],
                content: href
            });
        });
    });

    //9 提交合同上传信息
    $("#contract_submit").click(function () {
        // 表单校验 引用 xudazhu.js
        if (!formChecking()) {
            return false;
        }
        // console.log(del_file_ids);
        //询问框
        layer.confirm('确认提交合同信息吗？', {
            btn: ['确认', '取消'] //按钮
        }, function () {
            layer.close(layer.index);
            var url = "/contract";
            var data = new FormData($("#contract_form")[0]);
            // data["del_file_ids"] = del_file_ids;
            $.ajax({
                "url": url,
                "data": data,
                "type": "PUT",
                "dataType": "json",
                "contentType": false,
                "processData": false,
                xhr: function (){  //获取ajaxSettings中的xhr对象，为它的upload属性绑定progress事件的处理函数
                    var myXhr = $.ajaxSettings.xhr();
                    if (myXhr.upload) { //检查upload属性是否存在
                        //绑定progress事件的回调函数
                        // 进度条提示判断
                        if ($("#uploaded_file").children("input").length > 0) {
                            myXhr.upload.addEventListener('progress', progressHandlingFunction, false);
                        }
                    }
                    return myXhr; //xhr对象返回给jQuery使用
                },
                "success": function () {
                    layer.msg('操作成功 2秒后自动关闭', {
                        time: 2000 //2秒关闭（如果不配置，默认是3秒）

                    }, function () {
                        //window.parent.layer.close( window.parent.layerIndex );
                        // window.location.reload();
                        // window.parent.getPageDate( window.parent.pageDataA["pageNum"] ,  window.parent.pageDataA["pageSize"]);
                        // var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                        // parent.layer.close(index); //再执行关闭
                        //window.location.reload();
                        window.parent.flush();
                        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                        parent.layer.close(index); //再执行关闭
                    });
                }
            })
        });
    });

    // 10 合同金额分配提醒以及限制
    $(document).on("keyup", ".allot-money", allotMoney);
    $(document).on("click", ".allot-money", allotMoney);

    // 11 合同编辑
    let contractId = getParamForUrl("id");
    if (contractId !== null) {
        $(".projectId").removeClass("need-input");
        showDataTemp("contract");
        // 铺合同附件、以及实施部带的合同分配金额
        $.get("/contract", {id: contractId}, function (data) {
            var projectLocationId = data.content[0].projectBean.projectLocationBean.projectLocationId;
            var earningsCompanyId = data.content[0].projectBean.earningsCompanyBean.earningsCompanyId;
            projectLocation(projectLocationId);
            revenueUnit(earningsCompanyId);
            otherDataShow(data["content"][0]);
            projectNum = data["content"][0]["projectBean"]["projectNum"];

            let projectId = $(".projectId");
            projectId.attr("disabled","disabled");
            projectId.parent().append("<input id='projectNum' name='projectBean.projectNum' style='border: none' value='" + projectNum + "'>");
            projectId.parent().append("<input  name='projectBean.projectId' style='display: none' value='" + data["content"][0]["projectBean"]["projectId"] + "'>");
            renderFix("projectId");
            console.log(projectNum);
        })
    }

    //12 删除已经上传好的扫描件
    var file_ids_arr = [];
    $(document).on("click", ".delete-upload-file", function () {
        var that = $(this);
        var data_id = $(this).attr("data-id");
        layer.confirm('确认删除该合同吗？', {
            btn: ['确认', '取消'] //按钮
        }, function () {
            layer.close(layer.index);
            that.parent('p').remove();
            file_ids_arr.push(data_id);
            del_file_ids = file_ids_arr.join("$");
            $(".delFileIds").val(del_file_ids);
        });
    });

    // 13 判断url是否带有edit为true,如果不是,则为详情页,详情页页面应该精简一些
    let isEdit = getParamForUrl("edit");
    if(isEdit !== "true") {
        $("input").attr("readonly","readonly").css("border","none");
        $("textarea").attr("readonly","readonly").css("border","none").attr("placeholder","").css("resize","none");
        $(".file-div").css("border","none");
        $("#btn_file_upload").remove();
        $(".submit-item").remove();
        $(".layui-progress").remove();
        $(".add-people").remove();
        setTimeout(function () {
           $("label").css("color","#47a3dc");
           $(".delete-upload-file ").remove();
           $(".layui-input").each(function (index,input) {
               if($(this).attr("type") !== "hidden") {
                   var inputValue = $(input).val();
                   $(input).after(
                       '<span class="newSpan">'+inputValue+'</span>'
                   );
                   $(input).siblings("i").remove();
                   $(input).remove();
               }
            })
       },150)
    }

    $(document).on("keyup",".contractMoney",function () {
        if($(".quota").find(".allot-money").length === 1){
            $(".quota").find(".allot-money").val($(this).val());
        }
    })
});

function allotMoney() {
    var contractMoney;
    var contractMoneyVal = $(".contractMoney").val();
    if (contractMoneyVal === "") {
        contractMoney = 0;
    } else {
        contractMoney = parseFloat(contractMoneyVal);
    }

    var allMoneyArr = [];
    $(this).closest(".layui-row").children(".layui-col-sm6").each(function (index, col) {
        var tempMoney;
        if ($(col).find(".allot-money").val() === "") {
            tempMoney = 0;
        } else {
            tempMoney = parseFloat($(col).find(".allot-money").val());
        }
        allMoneyArr.push(tempMoney);
    });
    var allMoney = 0;
    for (var i = 0; i < allMoneyArr.length; i++) {
        allMoney += allMoneyArr[i];
    }

    var tipsMoney = contractMoney - allMoney;
    if (tipsMoney < 0) {
        layer.tips("分配金额超过合同金额", this, {tips: 1});
        var money2 = allMoney - parseFloat($(this).val());
        var money3 = contractMoney - money2;
        $(this).val(money3)
    } else {
        layer.tips("剩余分配金额为" + tipsMoney, this, {tips: 1});
    }
}

function progressHandlingFunction(e) {
    var progress_bar = $(".layui-progress-bar");
    if (e.lengthComputable) {
        progress_bar.attr({value: e.loaded, max: e.total}); //更新数据到进度条
        var percent = e.loaded / e.total * 99;
        progress_bar.css('width', percent.toFixed(2) + "%").html(
            (parseInt(percent.toFixed(2)) + 1) + "%");
    }
}

//方法2 用户树
function userTreeSelected(ids) {
    $.get("/user", {"$D.userId": ids}, function (data) {
        // 经营主负责人 部门负责人
        if (thisTree.hasClass("head")) {
            thisTree.val(data.content[0]["userName"])
        } else { // 通知人 待办人
            var choose_wait = thisTree.closest(".choose-parent").find(".choose-wait");
            choose_wait.empty();
            $(data.content).each(function (index, user) {
                choose_wait.append(
                    '<button class="layui-btn layui-btn-sm layui-btn-primary" type="button">' + user.userName + ''
                    + '<i class="layui-icon layui-icon-close"></i>'
                    + '</button>'
                    + '<input type="hidden" class="beans-id" data-name="userBeans[' + index + '].userId" value="' + user.userId + '">'
                )
            })
        }

    }, "json").fail(function (res) {
        layer.msg("数据获取失败 请刷新重试", {icon: 5});
        console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据获取失败 请刷新重试");
    });
}

// 方法3 铺实施部,合同分配金额,
function otherDataShow(data) {
    //铺实施部
    var quota = $(".quota");
    quota.empty();
    $(data["projectBean"]["implementBeans"]).each(function (index, implement) {//实施部
        tempId = Math.ceil(Math.random() * 10000);
        quota.append(
            '<div class="layui-col-sm6">'
            + '<label class="layui-form-label">' + implement["departmentBean"]["departmentName"] + '：</label>'
            + '<div class="layui-input-block">'
            + '<input type="number" id="implementDepartmentId' + tempId + '"  name="contractDepartmentMoneyBeans[' + index + '].contractDepartmentMoneyDistributionMoney"  placeholder="" autocomplete="off" class="layui-input project-relate allot-money">'
            + '<input type="hidden" name="contractDepartmentMoneyBeans[' + index + '].implementBean.implementId" id="dept' + implement["implementId"] + '" value="' + implement["implementId"] + '" placeholder="" autocomplete="off" class="layui-input project-relate">'
            + '</div>'
            + '</div>'
        )
    });
    // 铺部门分配金额
    $(data["contractDepartmentMoneyBeans"]).each(function (index, contractDepartmentMoneyBean) {
        $("#dept" + contractDepartmentMoneyBean["implementBean"]["implementId"]).prev().val(contractDepartmentMoneyBean["contractDepartmentMoneyDistributionMoney"]);
    });
   // 还未有分配合同金额 ,如果合同金额不为空 ,则默认分配合同金额等于合同金额
    if($(data["contractDepartmentMoneyBeans"]).length === 0){
        let contractMoney = $(".contractMoney");
        if(contractMoney.val() !== ""){
            $(".allot-money").val(contractMoney.val());
        }
    }

    // 铺已经上传好的合同
    var download_file = $(".download_file");
    var urlPath = window.document.location.href;
    var x=urlPath.indexOf('/');
    for(var i=0;i<2;i++){
        x=urlPath.indexOf('/',x+1);
    }
    urlPath=urlPath.substring(0,x);
    console.log(urlPath+"地址");
    download_file.empty();
    $(data["materialBeans"]).each(function (index, contractAccessory) {
        console.log(contractAccessory["contractAccessoryNewName"]);
        let href = "/download_file?fileName="+"D://contractFile//" +  (contractAccessory["contractAccessoryNewName"]);
        // href = encodeURIComponent(href);

        download_file.append(
            '<p style="color: #2c2c2c">'
            + '<span>' + contractAccessory["contractAccessoryOldName"] + '</span>'
            + '<button type="button" class="layui-btn layui-btn-xs layui-btn-normal">'
            + '<a download="' + contractAccessory["contractAccessoryOldName"] + '" href="'+href+'" style="color: #fff">下载</a>'
            + '</button>'
            + '<button type="button" class="delete-upload-file layui-btn layui-btn-danger layui-btn-xs" style="margin-left: 5px;" data-id="' + contractAccessory["contractAccessoryId"] + '">删除</button>'
            + '</p>'
        )
    })

}




