var id = getParamForUrl("id");
$(function () {
    let status = false;
    layui.use(['form', 'layedit'], function(){
        var form = layui.form
            ,layedit = layui.layedit
            ,layer = layui.layer;
        // 铺实施部
        $.get("/department/implement_department",function(data){
            // $(".implementName option:selected").each(function () {
            //     console.log($(this).val());
            // })
            var t =$(".implementName").val();//获取下拉框的值
            $(data).each(function (index,item) {

                //console.log(item.departmentId)
                var i =item.departmentId;
                if( t !== ""+i ){
                    $(".implementName").append('<option value="'+item.departmentId+'">'+item.departmentName+'</option>');
                    console.log(""+i)
                }
                layui.form.render('select');
            })
        }, "json");
        status = true;
        if (id !== null && status) {
           // document.getElementById("add-num").style.display = "block";
            $.get("/table_utils/info", {postId: id,"table_utils.tableName":"post"}, function (data) {
                var   management = data.content[0];
                var test =  data.content[0].postLevel;
                //console.log(data);
                var value = data.content[0].departmentBean.departmentId;
                showData4Object(management);
                $(".implementName").prepend('<option value="'+value+'">'+data.content[0].departmentBean.departmentName+'</option>');
                $(".implementName option[value='']").remove(); //删除Select中Value='3'的Option
                  $(".implementName").attr("value", value);
                $(".postLevel").attr("value", test);
                layui.form.render('select');
            }, "json");
        }

        form.on('submit(sub)', function(data){
            let url= "/post";
            let useid=window.top.userTemp.userId;
            let itemData;
            if (id === null) {
                itemData = $.param({"_method": "POST"}) + "&" + $("#teamForm").serialize()+"&" + $.param({"userBean.userId":useid});
            }else {
                itemData = $.param({"_method": "put"}) + "&" + $("#teamForm").serialize()+"&" + $.param({"userBean.userId":useid})+"&" +$.param({"postId":id});
            }
            status = true;
            debugger;
            $.post(url , itemData, function (data) {
                if (data) {
                    parent.layer.closeAll();
                    parent.layer.msg('修改成功',{time : 1500, icon : 1});
                    location.reload();
                } else {
                    layer.msg('添加失败',{icon:5});
                    status = false;
                }
            }, "json").fail(function (res) {
                layer.msg('数据提交失败 请刷新重试',{icon:5});
                console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
                status = false;
            }, "json");
        });
        //点击提交按钮，将表单数据发送到服务器
        /*
        $("#save").click(function () {

*/
    });

    layui.use('laydate', function(){
        var laydate = layui.laydate;

        //执行一个laydate实例
        laydate.render({
            elem: '#dateTime' //指定元素
            ,type: 'datetime'
        });
    });


        // if (id === null) {
        // showUser(window.top.userTemp["userName"]);
    // }
});

