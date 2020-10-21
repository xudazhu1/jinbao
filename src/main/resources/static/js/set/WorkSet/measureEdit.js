var id = getParamForUrl("id");
$(function () {
    layui.use('form', function(){
        var form = layui.form;
        layui.form.render('select');
        // 2编辑页铺数据
        if (id !== null) {
            // $(".paymentStatus-1").remove();
            // let tBodyTemp = $("#num");
            // tBodyTemp.append(
            //    '<div class="layui-form-label">'
            //     +'<label>编号</label>'
            //     +'</div>'
            //     +'<div class="layui-input-block">'
            //     +'<input type="text" name="squadGroupFeeNum" required  lay-verify="required" placeholder="" autocomplete="off" class="layui-input">'
            //     +'</div>'
            // );
           // document.getElementById("add-num").style.display = "block";
            $.get("/table_utils/info", {professionUnitId: id,"table_utils.tableName":"profession_unit"}, function (data) {
                var   management = data.content[0];
                //console.log(data);
                 //console.log(data.content[0].squadGroupFeeStatusBean.squadGroupFeeStatusId);
                showData4Object(management);
                //console.log(test);
               // $(".squadGroupFeeStatusBean.squadGroupFeeStatusId").val("123");
                layui.form.render('select');
            }, "json");
        }
        //点击提交按钮，将表单数据发送到服务器
        form.on('submit(save)',function (data) {
            let url= "/profession_unit";
            let useid=window.top.userTemp.userId;
            let itemData;
            if (id === null) {
                itemData = $.param({"_method": "POST"}) + "&" + $("#teamForm").serialize()+"&" + $.param({"userBean.userId":useid});
            }else {
                itemData = $.param({"_method": "put"}) + "&" + $("#teamForm").serialize()+"&" + $.param({"userBean.userId":useid})+"&" +$.param({"professionUnitId":id});
            }
            if (!formChecking() || status) {
                return false;
            }
            $.post(url , itemData, function (data) {
                if (data) {
                    parent.layer.closeAll();
                    parent.layer.msg('修改成功',{time : 1500, icon : 1});
                    location.reload();
                } else {
                    layer.msg('添加失败',{icon:5});
                }
            }, "json").fail(function (res) {
                layer.msg('数据提交失败 请刷新重试',{icon:5});
                console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
            }, "json");
        })
    });
    layui.use('layedit', function(){
        var layedit = layui.layedit
            ,$ = layui.jquery;

        //构建一个默认的编辑器
        var index = layedit.build('LAY_demo1');

        //编辑器外部操作
        var active = {
            content: function(){
                alert(layedit.getContent(index)); //获取编辑器内容
            }
            ,text: function(){
                alert(layedit.getText(index)); //获取编辑器纯文本内容
            }
            ,selection: function(){
                alert(layedit.getSelection(index));
            }
        };

        $('.site-demo-layedit').on('click', function(){
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });

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

