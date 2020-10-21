//上传进度回调函数：
function progressHandlingFunction(e) {
    if (e.lengthComputable) {
        $('#progress').attr({value: e.loaded, max: e.total}); //更新数据到进度条
        var percent = e.loaded / e.total * 100;
        $('#progress').html(
            // e.loaded + "/" + e.total + " bytes. " +
            percent.toFixed(2) + "%");
        $('#progress').css('width', percent.toFixed(2) + "%");
    }
}

function showData(json) {
    $(".remaks").val(json.datumRemaks);
    $("#itemNumbering").val(($.isEmptyObject(json) ? "" : json.implementBean.projectBean.projectNum))
    $("#itemName").val(($.isEmptyObject(json) ? "" : json.implementBean.projectBean.projectName))
    $("#data_cont").val(($.isEmptyObject(json) ? "" : json.implementBean.projectBean.contractBean.contractScannedExists))
    if (datumId !== "null") {
        $("#datumDepartment").append("<option value=" + ($.isEmptyObject(json) ? "" : json.implementBean.implementId) + ">" + ($.isEmptyObject(json) ? "" : json.implementBean.departmentBean.departmentName) + "</option>")
        $(".info").prop("readonly", true);
    }
    for (var key in json) {
        $("." + key).val(json[key])
    }
    var contTr = $(".type_name[data-type='合同']").closest("tr");
    contTr.closest("tr").find("td").find(".update").remove();
    $(".addForm").trigger("change")
    // if (json !== null) {
    $(json.datumFileBeans).each(function (index, datu) {
        console.log(datu.datumFileType+"类型");
        $(".type_name[data-type='" + datu.datumFileType + "']").next().find(".type_div").append("<div>" + datu.datumFilePath + "</div>")
        $(".type_name[data-type='" + "*" + datu.datumFileType + "']").next().find(".type_div").append("<div>" + datu.datumFilePath + "</div>")

    })

    $(json.implementBean.projectBean.contractBean.materialBeans).each(function (index, cont) {
        console.log(cont.materialOldName);
        $(".type_name[data-type='合同']").next().find(".type_div").append("<p>" + cont.materialOldName + "</p>")
    })

    if ($(json.implementBean.projectBean.contractBean.materialBeans).length> 0) {
        $(".hi").show();
    } else {
        $(".hi").hide();
    }
    /*限制合同不能修改有无*/
    contTr.find("#data_cont").prop("disabled", true);
    /*删除掉合同的点击上传按钮*/
    contTr.find(".weui-uploader__input-box").remove();
}

/*根据页面的部门显示不一样的提交不同的信息*/
function deptChange() {
    var dept = $("#datumDepartment");
    $(".altrowstable-data th").not(".cont").hide();
    $(".altrowstable-data td").not(".cont").hide();
    var valOption = dept.find("option[value='" + dept.val() + "']").text();
    $("#itemNumbering").val()
    $("#Depart").val(valOption)
    console.log(valOption + "*****");

    if (valOption === "勘察部") {
        $(".kc-data").show();

    } else if (valOption === "咨询部") {
        $(".zx-data").show();
    }
    else if (valOption === "测量部") {
        $(".cl-data").show();

    }


    if (datumId === "null") {
        var url = "/datum/findAll"
        var data = {
            "implementBean.projectBean.projectNum": $("#itemNumbering").val(),
            "implementBean.departmentBean.departmentName": valOption
        }
        $.ajax({
            "url": url,
            "data": data,
            "type": "GET",
            "dataType": "json",
            "success": function (json) {
                console.log($(json).length + "长度");
                if ($(json).length > 0) {
                    if (confirm("该资料已存在!是否要进行修改或添加操作？")) {
                        $("#datum_id").val(json[0].datumId)
                        showData(json[0]);
                    } else {

                        window.parent.layer.close(parent.layerIndex)
                    }

                } else {
                    $("#datum_id").val("")
                    $(".type_name[data-type='合同']").next().find(".update").remove();
                    $(".cont").hide();
                }

            }
        })
    }
}

var url = decodeURIComponent(window.location.href);

var datumId = url.indexOf("id") === -1 ? "null" : url.substring(url.indexOf("id") + 3);

$(function () {
    /*详情修改拿数据以及铺数据*/
    if (datumId != "null") {
        console.log("datumId !== 'null'");
        $("#datum_id").val(datumId);
        var url = "/datum/find_By_Id"
        var data = {"id": datumId}
        $.ajax({
            "url": url,
            "data": data,
            "type": "GET",
            "dataType": "json",
            "success": function (json) {
                showData(json);
                deptChange();
            }
        })
    }


    // 追加上传文件的div
    $(".addForm").on("change", function () {
        var name1 = $(this).parent("td").prev("th").text();
        var checkText = $(this).find("option:selected").val(); //获取Select选择的值
        if (checkText === '有') { //如果选择的值为“有”的时候则追加div，否则移除该div
            /* <input  type="file" class="head_img_file" multiple="multiple" name="' + name1 + '">*/
            $(this).parent().append('<div class = "update"> <div id="uploadDiv" class="weui-uploader__input-box" style="border-radius: 5px;cursor:pointer;color: #FFF;font-weight: bold; background-color: #519ef1;font-family:Microsoft YaHei;padding: 4px;margin: 2px;width: 324px;text-align: center;">选择上传文件</div>' + '<div id="fileDiv" style="display: none;border: 1px solid yellow; width: 20px;height: 20px"  ></div>' +
                '<div class="shou_file"><p style="margin-top: 35px"></p></div>' +
                '<div class="type_div" style="margin-top: -3px"><p></p></div></div>');

        } else {
            $(this).parent().children('.update').remove();
        }
    });

    /*点击触发文件上传*/
    $(document).on("click", "#uploadDiv", function () {
        var name1 = $(this).parent().parent("td").prev("th").text();
        var temp = Math.ceil(Math.random() * 1000);
        var uploadFile = '<input name=' + name1 + ' id="uploaderInput' + temp + '" class="weui-uploader__input upload-input" type="file" accept="image/!*" multiple/>';
        $(this).next().append($(uploadFile));
        // $("#uploaderInput").bind("change", function () {
        //     console.log($(this).filename + "kkk");
        //     //可以做一些其他的事，比如图片预览
        //     $(this).removeAttr("id");
        // });
        $("#uploaderInput" + temp).click();
    })

    /*显示上传的名字*/
    $(document).on("change", ".upload-input", function () {
        var shou_file1 = $(this).closest(".update").find(".shou_file");
        shou_file1.find("p").remove();
        showFileName($(this).closest(".update"));
    });

    $(document).on("click", ".delete-file", function () {
        var p = $(this).closest("p");
        var indexTemp = p.attr("data-file-index");
        var idTemp = p.attr("data-input-id");
        $("#" + idTemp).remove();
        var update = $(this).closest(".update");
        showFileName(update);
    });

    function showFileName(update) {
        update.find("p").remove();
        update.find(".upload-input").each(function () {
            var inputTemp = $(this);
            var shou_file = $(this).closest(".update").find(".shou_file");
            var files = this.files;
            console.log("1 = aa ")
            $(files).each(function (index, file) {
                console.log(" add pp ")
                shou_file.append("<p class='ppp' data-file-index='" + index + "' data-input-id='" + inputTemp.prop("id") + "' >已添加: " + this.name + "<button type='button' class='delete-file'>删除</button></p>")
            });
            shou_file.append("<p>---------------------------------</p>")
        });
    }

    /*上传文件的改变事件*/
    $(".weui-uploader__input upload-input").change(function () {

    })
    $(document).on("change", ".weui-uploader__input upload-input", function () {
        var name = $(this).attr("name");
        var path = $(this).val()
        var index = path.lastIndexOf("\\")
        var path1 = path.substring(index + 1);
        console.log(path1);
        var url = "/datum_file"
        var data = {"path": path1, "name": name}
        $.ajax({
            "url": url,
            "data": data,
            "type": "GET",
            "dataType": "json",
            "success": function (json) {
                if (json.data) {
                    alert("文件名称重复!请修改文件名称,否则会替换原有文件")
                } else {
                }
            }
        })
    })

    /*进来之后查询所有的项目名称*/
    var url = "/project/project_num_and_name";
    var data = "";
    $.ajax({
        "url": url,
        "data": data,
        "type": "GET",
        "dataType": "json",
        "success": function (json) {
            $(json).each(function (index, project) {
                // $("#itemNumberlist").empty();
                 // $("#itemNamelist").empty();
                $("#itemNumberlist").append("<option class='itemNumberings' data-itemId='" + project["projectId"] + "' data-item_name='" + project.projectName + "'>" + project.projectNum + "</option>\n");
                $("#itemNamelist").append("<option class='itemNames' data-itemId='" + project["projectId"] + "'  data-item_numbering='" + project["projectNum"] + "'>" + project["projectName"] + "</option>\n");
            })
        }
    })

    /*根据项目编号的改变对应到相应的项目名称*/
    $("#itemNumbering").change(function () {
        var itemNumberingTemp = this.value;
        console.log(itemNumberingTemp + "llll");
        $(this).attr("title", itemNumberingTemp);
        $(".itemNumberings").each(function (index, option) {
            if (option.value === itemNumberingTemp) {
                var itemNameJQ = $("#itemName");
                itemNameJQ.val($(option).attr("data-item_name"));
                itemNameJQ.attr("title", $(option).attr("data-item_name"));
                showImpl($(option).attr("data-itemId"));
            }
        })

    })

    /*根据对应的id来找到对应的部门*/
    function showImpl(projectId) {
        var url = "/project/project_id"
        var data = {"id": projectId}
        $("#datumDepartment")
        $.ajax({
            "url": url,
            "data": data,
            "type": "GET",
            "dataType": "json",
            "success": function (json) {
                $("#datumDepartment").empty();
                $(json["implementBeans"]).each(function (index, implementBean) {
                    $("#datumDepartment").append("<option value='" + implementBean["implementId"] + "'>" + implementBean["departmentBean"]["departmentName"] + "</option>");
                })
                deptChange();
            }
        })

    }


    /*部门发生改变时清空所有的内容并且切换到不同的提交内容*/
    $("#datumDepartment").change(function () {
        deptChange()
        $("body .altrowstable-data select").not(".cont-select").val("");
        $(".update").not(".cont .update").remove();

    });

    /*提交数据*/
    $("#submit").click(function () {
        if (confirm("确定提交么？")) {
            $(".container").show()
            $("#progress").show()
            var url = "/datum"
            var data = new FormData($("#update_datum")[0]);
            $.ajax({
                "url": url,
                "data": data,
                "type": "POST",
                "dataType": "json",
                "contentType": false,
                "processData": false,
                xhr: function () { //获取ajaxSettings中的xhr对象，为它的upload属性绑定progress事件的处理函数
                    myXhr = $.ajaxSettings.xhr();
                    if (myXhr.upload) { //检查upload属性是否存在
                        //绑定progress事件的回调函数
                        myXhr.upload.addEventListener('progress', progressHandlingFunction, false);
                    }
                    return myXhr; //xhr对象返回给jQuery使用
                },
                "success": function (json) {
                    if (json) {
                        alert("提交成功")
                        if (!$.isEmptyObject(window.parent.pageDataA)) {
                           // window.parent.getAPageExpense(window.parent.pageDataA.pageNum, window.parent.pageDataA.pageSize);
                            window.location.reload();

                        } else {
                            //window.parent.layer.close(parent.layerIndex);
                        }
                    } else {
                        alert("提交失败,该资料已存在!")
                        /*  window.parent.layer.close(parent.layerIndex);*/
                    }
                }
            })
        }
    })

})