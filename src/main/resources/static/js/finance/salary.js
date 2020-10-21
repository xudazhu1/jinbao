$(function () {
    let src = "<option></option>";
    //渲染日期工具 和 选择框
    let form;
    layui.use(['laydate', 'form'], function () {
        var laydate = layui.laydate;

        // 年月日选择器
        laydate.render({
            elem: '.salaryDate'
            ,type: 'month'

        });
        laydate.render({
            elem: '.salaryDate1'
            ,type: 'month'

        });
        form = layui.form;
        layui.form.render('select');
    });


    let userAjax = $.get("/user/user_all",{},function (data) {
        let userBean = $(".userBean");
        let searchUser = $(".search-user");
        $(data).each(function (i,ele) {
            src += "<option value='"+ele[0]+"'>"+ele[1]+"</option>"
        });
        userBean.append(src);
        searchUser.append(src);
        form.render('select');
    },"json");

    let status = false;
    $(document).on("click",".ok-btn",function () {
        let _this = $(this);
        layer.confirm('确认提交吗？', {
            btn: ['确认','取消'] //按钮
        }, function(){
            if (status) {
                return false;
            }
            status = true;
            let id = _this.attr("data-id");
            let _method = "POST";
            if(id !== undefined){
                _method = "PUT";
            }else {
                id = null;
            }

            let salaryDate = _this.parent().parent().find(".salaryDate").val();

            let userBean = _this.parent().parent().find(".userBean").val();
            let salaryDailyCost = _this.parent().parent().find(".salaryDailyCost").val();
            let salaryRemark = _this.parent().parent().find(".salaryRemark").val();
            let itemData = $.param({
                "_method": _method,
                "salaryId": id,
                "salaryDate": salaryDate,
                "userBean.userId": userBean,
                "salaryDailyCost": salaryDailyCost,
                "salaryRemark":salaryRemark
            });
            $.post("/salary", itemData, function (data) {
                if (data) {
                    layer.msg('操作成功 1秒后自动关闭', {
                        time: 1000 //1秒关闭（如果不配置，默认是3秒）
                    }, function () {
                        window.location.reload()
                    });
                } else {
                    layer.msg('添加失败', {icon: 5});
                    status = false;
                }
            }, "json").fail(function (res) {
                layer.msg(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试", {icon: 5});
                status = false;
            }, "json");
        }, function(){

        });

    });



    $.when(userAjax).done(function () {
        getPageDate(1,10);
    });

    $(document).on("click",".del-btn",function () {
        let _this = $(this);
        layer.confirm('确认删除吗？', {
            btn: ['确认','取消'] //按钮
        }, function(){
            let id = _this.attr("data-id");
            $.post("/salary",{id,"_method":"DELETE"},function (data) {
                if (data) {
                    layer.msg('操作成功 1秒后自动关闭', {
                        time: 1000 //1秒关闭（如果不配置，默认是3秒）
                    }, function () {
                        window.location.reload()
                    });
                } else {
                    layer.msg('添加失败', {icon: 5});
                    status = false;
                }
            },"json").fail(function (res) {
                layer.msg('数据提交失败 请刷新重试', {icon: 5});
                console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
                status = false;
            }, "json");
        }, function(){

        });


    });

    $(".search-btn").click(function () {
        getPageDate(1,10);
    })
});


function getPageDate(pageNum,pageSize){
    var formTemp = $("#commit-form");
    var params = $.param({"pageNum": pageNum, "pageSize": pageSize ,"sort": true})+ '&' + formTemp.serialize();
    let userBean = $("#userBean").clone();

    $.get("/salary",params,function (data) {
        $(".dataTr").empty();

        $(data["content"]).each(function (i,ele) {

            userBean.find("[value =" + ele["userBean"]["userId"] + "]").attr("selected","selected");
            userBean.attr("id","");
            $(".dataTr").append("<tr class='newTr'>\n" +
                "                                                <td>"+(i+1)+"</td>\n" +
                "                                                <td>\n" +
                "                                                    <input type='text' class='layui-input salaryDate' id='salaryDate"+i+"' name='salaryDate' title='日期'\n" +
                "                                                           autocomplete='off'  placeholder='选择日期......' value='"+ele["salaryDate"].substring(0,ele["salaryDate"].length - 12)+"'>\n" +
                "                                                </td>\n" +
                "                                                <td>\n" +
                userBean.prop("outerHTML")+
                "                                                </td>\n" +
                "                                                <td>\n" +
                "                                                    <input class='layui-input salaryDailyCost' name='salaryDailyCost' title=''\n" +
                "                                                           autocomplete='off' placeholder='请输入......' value='"+ele["salaryDailyCost"]+"'>\n" +
                "                                                </td>" +
                "                                                <td>\n" +
                "                                                    <input class='layui-input salaryRemark' name='salaryRemark' title=''\n" +
                "                                                           autocomplete='off' placeholder='请输入......' value='"+ele["salaryRemark"]+"'>" +
                "                                                </td>\n" +
                "                                                <td>\n" +
                "                                                    <span class='layui-icon layui-icon-ok ok-btn' title='添加' style='margin-right: 10px;color: #009688;float: left;' data-id='"+ele["salaryId"]+"'></span>" +
                "                                                    <span class='layui-icon layui-icon-delete del-btn' style='color: #ff4646;float: left;' title='删除' data-id='"+ele["salaryId"]+"'></span>" +
                "                                                </td>\n" +
                "                                            </tr>");

            layui.use(['laydate','form'], function () {
                var laydate = layui.laydate;
                // 年月日选择器
                laydate.render({
                    elem: '#salaryDate'+i
                    ,type: 'month'
                });
            });
            userBean.find("option").attr("selected",false);

        });
        layui.use(['form'], function () {
            var form = layui.form;
            layui.form.render('select');
        });
        showPageButtuns({
            "pageNum": data.number + 1,
            "countPage": data["totalPages"],
            "pageSize": data["size"],
            "countNum": data["totalElements"]
        }, $("#page"), getPageDate)
    },"json")
}




