var id = getParamForUrl("id");
$(function () {
    // 1 报价编号生成规则
    $.get("/quote/quote_max_num", {}, function (data) {
        if (!data) {
            layer.msg("获取报价编号失败,请刷新重试", {icon: 5});
            return false;
        }
        $(".quoteNum").val("BJ-" + data);
    });
    // 2编辑页铺数据
    if (id !== null) {
        $.get("/quote", {"id": id}, function (data) {
            showData4Object(data["content"][0]);
            showUser(data["content"][0]["quotePeople"])
        }, "json");
    }

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
    $.get("/get_config_file", {}, function (data) {
        maxFile = data["maxFileSize"];
        maxFileSize = (data["maxFileSize"].substring(0, data["maxFileSize"].length - 2)) * 1024 * 1024;
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
                layer.msg("上传文件大小最大为" + maxFile);
                added_file.children("input:last-child").remove();
                return false;
            } else { // 多个文件
                layer.msg("上传文件大小最大为" + maxFile + ",请分批上传");
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

    //点击提交按钮，将表单数据发送到服务器
    let status = false;
    $("#quoteSubmit").click(function () {
        // 表单校验 引用 xudazhu.js
        if (!formChecking() || status) {
            return false;
        }
        status = true;
        let onlyNum = $(".quoteNum").val();
        layer.confirm('确认提交吗？报价编号为 ' + onlyNum, {
            btn: ['确认', '取消'] //按钮
        }, function () {
            layer.close(layer.index);
            let method = getParamForUrl("id") === null ? "POST" : "PUT";
            // let itemData = $.param({
            //     "createUserBean.userId": window.top.userTemp.userId,
            //     "quoteCreateTime": formatDate(new Date(), 8),
            //     "createJobBean.jobId": window.top.userTemp["jobBean"]["jobId"]
            // }) + "&" + $("#inputForm").serialize();
            let url = "/quote";
            let data =new FormData($("#inputForm")[0]);
            let type = method;
            $.ajax({
                "url": url,
                "data": data,
                "type": type,
                "contentType": false,
                "processData": false,
                "dataType": "json",
                "success": function (data) {
                    if (data) {
                        layer.msg('操作成功 2秒后自动关闭', {
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        }, function () {
                            window.location.reload();
                            window.parent.flush();
                            var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                            parent.layer.close(index); //再执行关闭
                            status = false;
                        });
                    } else {
                        layer.msg('添加失败', {icon: 5});
                        status = false;
                    }
                }
            })


            //     , "json").fail(function (res) {
            //     layer.msg('数据提交失败 请稍后重试', {icon: 5});
            //     console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
            // }, "json");

        })
    });
    if (id === null) {
        showUser(window.top.userTemp["userName"]);
    }

    // 铺已经上传好的报价文件
    var download_file = $(".download_file");
    download_file.empty();
    $(data["quoteFileBeanList"]).each(function (index, contractAccessory) {
        download_file.append(
            '<p style="color: #2c2c2c">'
            + '<span>' + contractAccessory["contractAccessoryOldName"] + '</span>'
            + '<button type="button" class="layui-btn layui-btn-xs layui-btn-normal">'
            + '<a download="' + contractAccessory["quoteFileOldName"] + '" href="/file/' + contractAccessory["quoteFileNewName"] + '" style="color: #fff">下载</a>'
            + '</button>'
            + '<button type="button" class="delete-upload-file layui-btn layui-btn-danger layui-btn-xs" style="margin-left: 5px;" data-id="' + contractAccessory["quoteFileId"] + '">删除</button>'
            + '</p>'
        )
    })

});
// 报价人
let users = null;

function showUser(defaultValue) {
    // 获取到所有用户数据,并且录入默认项是登陆者
    if (users === null) {
        $.get("/table_utils/info", {"table_utils.tableName": "user"}, showTemp);
    } else {
        showTemp(data);
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
    }
}
