var id = getParamForUrl("id");
$(function () {
    layui.use('form', function(){
        var form = layui.form;
        layui.form.render('select');
        // 2编辑页铺数据
        if (id !== null) {
            //document.getElementById("add-num").style.display = "block";
            $.get("/table_utils/info", {userWorkId: id,"table_utils.tableName":"user_work"}, function (data) {

                var   management = data.content[0];
                //showData4Object(management);
                var test =  data.content[0].postLevel;
                //console.log(data);
                var value = data.content[0].departmentBean.departmentId;
                s(value);
                //铺实施部
                $(".implementName").prepend('<option value="'+value+'">'+data.content[0].departmentBean.departmentName+'</option>');
                $(".implementName option[value='']").remove(); //删除Select中Value='3'的Option
                $(".implementName").attr("value", value);
                //铺用户名
                $(".userName").prepend('<option value="'+data.content[0].userBean.userId+'">'+data.content[0].userBean.userName+'</option>');
                $(".userName option[value='']").remove(); //删除Select中Value='3'的Option
                $(".userName").attr("value", data.content[0].userBean.userId);

                //铺员工
                if(data.content[0].staffPostBean.postId !== undefined){
                    $(".staff").prepend('<option value="'+data.content[0].staffPostBean.postId+'">'+data.content[0].staffPostBean.postName+'</option>');
                    $(".staff option[value='']").remove(); //删除Select中Value='3'的Option
                    $(".staff").attr("value", data.content[0].staffPostBean.postId);
                }else {
                    $(".staff").prepend('<option value="">请选择</option>');
                }
                //铺队长
                if(data.content[0].captainPostBean.postId !== undefined){
                    $(".captain").prepend('<option value="'+data.content[0].captainPostBean.postId+'">'+data.content[0].captainPostBean.postName+'</option>');
                    $(".captain option[value='']").remove(); //删除Select中Value='3'的Option
                    $(".captain").attr("value", data.content[0].captainPostBean.postId);
                }else {
                    $(".captain").prepend('<option value="">请选择</option>');
                }

                //铺主管
                if(data.content[0].supervisorPostBean.postId !== undefined){
                    $(".supervisor").prepend('<option value="'+data.content[0].supervisorPostBean.postId+'">'+data.content[0].supervisorPostBean.postName+'</option>');
                    $(".supervisor option[value='']").remove(); //删除Select中Value='3'的Option
                    $(".supervisor").attr("value", data.content[0].supervisorPostBean.postId);
                }else{
                    $(".supervisor").prepend('<option value="">请选择</option>');
                }

                layui.form.render('select');
            }, "json");
        }

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
                }
                layui.form.render('select');
            })
        }, "json");

        //用户名
        $.get("/user_work/select_user",function(data){
            // $(".implementName option:selected").each(function () {
            //     console.log($(this).val());
            // })
            var t =$(".userName").val();//获取下拉框的值
            $(data).each(function (index,item) {

                //console.log(item.departmentId)
                var i =item.userId;
                if( t !== ""+i ){
                    $(".userName").append('<option value="'+item.userId+'">'+item.userName+'</option>');
                }
                layui.form.render('select');
            })
        }, "json");

        //铺员工、主管、队长下拉框
        function s(t){
            $.get("/post",{'departmentBean.departmentId':t,'postLevel':'队长'},function(data){
                // $(".implementName option:selected").each(function () {
                //     console.log($(this).val());
                // })
                var t =$(".captain").val();//获取下拉框的值
                $(data).each(function (index,item) {

                    //console.log(item.departmentId)
                    var i =item.post_name;
                    if( t !== ""+i && item.post_level === '队长' ){
                        $(".captain").append('<option value="'+item.post_Id+'">'+item.post_name+'</option>');
                    }
                    layui.form.render('select');
                })
            }, "json");
            $.get("/post",{'departmentBean.departmentId':t,'postLevel':'主管'},function(data){
                // $(".implementName option:selected").each(function () {
                //     console.log($(this).val());
                // })
                var t =$(".supervisor").val();//获取下拉框的值
                $(data).each(function (index,item) {

                    //console.log(item.departmentId)
                    var i =item.post_name;
                    if( t !== ""+i && item.post_level === '主管' ){
                        $(".supervisor").append('<option value="'+item.post_Id+'">'+item.post_name+'</option>');
                    }
                    layui.form.render('select');
                })
            }, "json");
            $.get("/post",{'departmentBean.departmentId':t,'postLevel':'员工'},function(data){
                // $(".implementName option:selected").each(function () {
                //     console.log($(this).val());
                // })
                var t =$(".staff").val();//获取下拉框的值
                $(data).each(function (index,item) {

                    //console.log(item.departmentId)
                    var i =item.post_name;
                    if( t !== ""+i && item.post_level === '员工' ){
                        $(".staff").append('<option value="'+item.post_Id+'">'+item.post_name+'</option>');
                    }
                    layui.form.render('select');
                })
            }, "json");

        }
        //监听员工、主管、队长下拉框
            form.on('select(dep)',function (data) {
                var t = $(".implementName").find("option:selected").val();
                s(t);
            })

        //点击提交按钮，将表单数据发送到服务器
        form.on('submit(save)',function (data) {
            let url= "/user_work";
            let useid=window.top.userTemp.userId;
            let itemData;
            if (id === null) {
                itemData = $.param({"_method": "POST"}) + "&" + $("#teamForm").serialize();
            }else {
                itemData = $.param({"_method": "put"}) + "&" + $("#teamForm").serialize()+"&" +$.param({"userWorkId":id});
            }
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

