let id = getParamForUrl("id");
$(function () {
    // 提交
    $(document).on("click",".submit-btn",function () {
        // 此处校验基本信息  引用 xudazhu.js
        if (!formChecking()) {
            return false;
        }
        let method = getParamForUrl("id") === null ? "POST" : "PUT";
        let itemData = $.param({"_method": method,"tableName":"salary"}) + "&" + $("#inputForm").serialize();
        $.post("/table_utils",itemData,function (data) {
            if (data) {
                layer.msg("添加成功", {icon: 1});

            } else {
                layer.msg("添加失败", {icon: 5});
            }
        },"json").fail(function (res) {
            layer.msg('数据提交失败 请稍后重试', {icon: 5});
            console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请稍后重试");
        }, "json");
    });
});
function getSelectedUser(data) {
    $(".userId").val(data["content"][0]["userId"]);
}