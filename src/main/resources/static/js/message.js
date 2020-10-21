$(function () {
    // 1当点击标为已读按钮时
    $(document).on("click", "#someRead", function () {
        // 1-1 如果用户未选中表格中的一行,则提示未选中,并结束该方法
        // 表格中选择复选框的长度
        let checked_length = $(this).closest(".layui-tab-item").find("tbody").children("tr").children("td").children("input[type='checkbox']:checked").length;
        if (checked_length < 1) {
            layer.msg('未选中行');
        } else {
            layer.msg('标记已读成功', {icon: 1});
        }
    });

    // 2 当点击 标为全部已读时
    $(document).on("click", "#allRead", function () {
        layer.msg('全部消息:全部已读', {icon: 1});
    });

    layui.use(['element', 'layer', 'form'], function () {
        let form = layui.form;
        form.on("radio(filter-form)", function () {
            setTimeout(function () {
                getNotice();
                getTodo();
            }, "100");
        });
    });

    $(document).on('click','.notice-href',function () {
        let href = $(this).attr("data-inner-href");
        let layerTitle = $(this).attr("data-title");
        layerTitle = layerTitle === "undefined"  ? false:layerTitle;
        let width = $(this).attr("data-width");
        let height = $(this).attr("data-height");
        width = width === undefined ? '100%':width;
        height = height === undefined ? '100%':height;

        layer.ready(function () {
            layerIndex = layer.open({
                type: 2,
                title: layerTitle,
                maxmin: false,
                area: [width, height],
                content: href
            });
        });
    });


    getNotice();
    getTodo();


});

//获取通知
function getNotice() {
    $.get("/flow_notice",
        {
            iIs: "objectUserBean",
            "$S.flowNodeBean.flowNodeId": "1~2147483647",
            "flowNoticeStatus": $("[name='noticeStatus']:checked").val()
        },
        function (data) {
            $(".notice-num").text(data["totalElements"]);
            let todos = data["content"];
            let noticeData = $(".notice-data");
            noticeData.empty();
            $(todos).each(function (inedx, flowNotice) {
                noticeData.append(
                    '<div class="layui-card">\n' +
                    '    <div class="layui-card-body">\n' +
                    '        <span class="notice-title">' + flowNotice["flowNodeBean"]["flowNodeName"] +'</span>\n' +
                    '        <span class="notice-time">'+flowNotice["flowNoticeTime"]+'</span>\n' +
                    '        <div class="notice-content">\n' +
                    flowNotice["flowNoticeContent"] +
                    '            <span class="notice-href" ' +
                                'data-href="'+flowNotice["flowNodeBean"]["showPageBean"]["permissionTag"]+'"  ' +
                                'data-inner-href="'+(flowNotice["flowNodeBean"]["editPageBean"]["permissionTag"] + "?fromNotice=true&id="+ flowNotice["flowNoticeTag"])+'"  ' +
                    ' >查看</span>\n' +
                    '        </div>\n' +
                    '    </div>\n' +
                    '</div>'
                );
            });
        }, "json");
}

//获取待办
function getTodo() {
    $.get("/flow_notice",
        {
            iIs: "objectUserBean",
            "$S.flowNodeMiddleBean.flowNodeMiddleId": "1~2147483647",
            "flowNoticeStatus": $("[name='todoStatus']:checked").val()
        },
        function (data) {
        $(".todo-num").text(data["totalElements"]);
            let todos = data["content"];
            let todoData = $(".todo-data");
            todoData.empty();
            $(todos).each(function (inedx, flowNotice) {
                todoData.append(
                    '<div class="layui-card">\n' +
                    '    <div class="layui-card-body">\n' +
                    '        <span class="notice-title">' + flowNotice["flowNodeMiddleBean"]["flowNodeNextBean"]["flowNodeDescribe"] +'</span>\n' +
                    '        <span class="notice-time">'+flowNotice["flowNoticeTime"]+'</span>\n' +
                    '        <div class="notice-content">\n' +
                    flowNotice["flowNoticeContent"] +
                    '            <span class="notice-href" ' +
                    'data-href="'+flowNotice["flowNodeMiddleBean"]["flowNodeNextBean"]["showPageBean"]["permissionTag"]+'"  ' +
                    'data-inner-href="'+ ( flowNotice["flowNodeMiddleBean"]["flowNodeNextBean"]["editPageBean"]["permissionTag"] + "?todoId=" + flowNotice["flowNoticeTag"] )+'"  ' +
                    ' >去处理</span>\n' +
                    '        </div>\n' +
                    '    </div>\n' +
                    '</div>'
                );
            });
        }, "json");
}