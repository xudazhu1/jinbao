$(function () {
    // 1关闭弹出层
    $(document).on('click', '.cancel-btn', function () {
        window.parent.layer.close(window.parent.layerIndex);
    });

    //添加级联的父级对象的input
    $(".layui-input-block").append("<input type='hidden' name='"+getParamForUrl("parentKey")+"' value='"+getParamForUrl("parentId")+"' />")

    $("." + getParamForUrl("type") + "-input").show();
    // var type = getParamForUrl("type");
    // console.log(getParamForUrl("parentName"));
    $(".type-name").text(getParamForUrl("typeName"))
    $(".parent-name").text(getParamForUrl("parentName"))

    // 2添加结构
    layui.use('layer', function () {
        var layer = layui.layer;
    });
    $(document).on('click', '.confirm-btn', function () {
        var check = true;
        $(".need-input").each(function () {
            var tipsInfo = $(this).parent().prev().text();
            var tipsInfo2 = tipsInfo.substring(0,tipsInfo.length-1);
            if($(this).css("display") === "none") {
                return
            }
            if ($(this).val().replace(/(^\s+)|(\s+$)/g, "") === "") {
                layer.tips( tipsInfo2 + '不能为空', this, {
                    tips: [1, '#3595CC'],
                    time: 4000
                });
                return check = false;
            }
        });
        if ( ! check ) {
            return false;
        }

        var itemData = $("#inputForm").serialize();
        $.post("/" + getParamForUrl("type"), itemData, function (data) {
            if (data) {
                layer.msg('添加成功 2秒后自动关闭', {
                    time: 2000 //2秒关闭（如果不配置，默认是3秒）
                } ,  function () {
                    window.location.reload();
                });
                window.parent.showStructure();
            } else {
                layer.msg('添加失败');
            }
        }, "json").fail(function (res) {
            layer.msg('数据提交失败 请刷新重试 ');
            console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
        }, "json");
    });
})
