var id = getParamForUrl("id");
$(function () {
    let status = false;
    layui.use(['form', 'layedit'], function(){
        var form = layui.form
            ,layedit = layui.layedit
            ,layer = layui.layer;
        //创建一个编辑器
        var editIndex = layedit.build('LAY_demo_editor');
        layui.form.render('select');
        form.on('submit(demo1)', function(data){
            console.log(111111)
            layer.alert(JSON.stringify(data.field), {
                title: '最终的提交信息'
            })
            status = false;
            return  false;
        });

        //自定义验证规则
        form.verify({
            title: function(value){

                    return '标题至少得5个字符啊';

            }
            ,pass: [
                /^[\S]{6,12}$/
                ,'密码必须6到12位，且不能出现空格'
            ]
            ,content: function(value){
                layedit.sync(editIndex);
            }
        });
        status = true;
        if (id !== null && status) {
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
            document.getElementById("add-num").style.display = "block";
            $.get("/table_utils/info", {squadGroupFeeId: id,"table_utils.tableName":"squad_group_fee"}, function (data) {
                var   management = data.content[0];
                var test =  $(".squadGroupFeeStatusBean").val(value);
                //console.log(data);
                 var value = data.content[0].squadGroupFeeStatusBean.squadGroupFeeStatusId;
                 //console.log(data.content[0].squadGroupFeeStatusBean.squadGroupFeeStatusId);
                showData4Object(management);
                //console.log(test);
                 if(value !=1 || value!=2){
                     $(".squadGroupFeeStatusBean").prepend("<option value=''>请选择</option>");
                 }else {
                     $(".squadGroupFeeStatusBean").attr("value", value);
                    // $(".squadGroupFeeStatusBean").val(value);
                 }
               // $(".squadGroupFeeStatusBean.squadGroupFeeStatusId").val("123");
                layui.form.render('select');
            }, "json");
        }


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
    // 2编辑页铺数据


    //点击提交按钮，将表单数据发送到服务器

        $("#teamManageSubmit").click(function () {
            let url= "/squad_group_fee";
            let useid=window.top.userTemp.userId;
            let itemData;
            if (id === null) {
                itemData = $.param({"_method": "POST"}) + "&" + $("#teamForm").serialize()+"&" + $.param({"userBean.userId":useid});
            }else {
                itemData = $.param({"_method": "put"}) + "&" + $("#teamForm").serialize()+"&" + $.param({"userBean.userId":useid})+"&" +$.param({"squadGroupFeeId":id});
            }
            if (!formChecking() || status) {
                return false;
            }
            status = true;
            $.post(url , itemData, function (data) {
                if (data) {
                    layer.msg('操作成功 2秒后自动刷新', {
                        time: 2000 //2秒关闭（如果不配置，默认是3秒）
                    } ,  function () {
                        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                        parent.layer.close(index); //再执行关闭
                        window.parent.location.reload(); //刷新父页面
                    });
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

        // if (id === null) {
        // showUser(window.top.userTemp["userName"]);
    // }
});

