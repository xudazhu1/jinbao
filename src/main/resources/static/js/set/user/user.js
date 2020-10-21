// 方法一 展示用户数据
function getAPageData(pageNum , pageSize ) {

    var formTemp = $("#searchForm");
    var keys = [];
    //拼接任意条件的name(key)值
    formTemp.find("input[name]").each(function () {
        var nameTemp = $(this).prop("name");
        if ( nameTemp.indexOf("$D.") === -1 && $(this).closest("th").css("display") !== "none" ) {
            keys.push(nameTemp);
        }
    });
    $(".multiple-key").val(keys.toString().replace(/,/g , "$"));

    var params = $.param({ "pageNum": pageNum, "pageSize": pageSize  })
        + '&' + formTemp.serialize();

    $.get("/user/s", params ,function (data) {
        var items = $("#items");
        items.empty();
        $(data.content).each(function (index,content) {

            var roles = [];
            $(content["roleBeans"]).each(function (index, role) {
                roles.push(role["roleName"]);
            });

            let deptTemp = content["jobBean"]["departmentBean"];
            while ( !  $.isEmptyObject(deptTemp["parentDepartmentBean"]) )  {
                deptTemp = deptTemp["parentDepartmentBean"];
            }
            
            items.append(
                '<tr>'
                +'<td>'+content.userName+'</td>'
                +'<td>'+content.userPassword+'</td>'
                +'<td>'+content.userTelephone+'</td>'
                +'<td>'+content["jobBean"].jobName+'</td>'
                +'<td>'+content["jobBean"]["departmentBean"]["departmentName"]+'</td>'
                +'<td>'+deptTemp["companyBean"]["companyName"]+'</td>'
                +'<td>'+roles.toString()+'</td>'
                +'<td>'
                +'<button type="button" class="btn btn-warning btn-xs edit-btn " data-href="user/user-inner.html?id='+content.userId+'">编辑</button>'
                +'<button type="button" class="btn btn-danger btn-xs delete-btn" data-id="'+content.userId+'">删除</button>'
                +'</td>'
                +'</tr>'
            );

        });


        showPageButtuns(
            {"pageNum":data.number + 1 , "countPage": data["totalPages"] ,"pageSize" : data["size"] ,  "countNum": data["totalElements"] } ,
            $("#page") ,
            getAPageData);


    },"json").fail(function (res) {
        sweetAlert('数据提交失败 请刷新重试 ');
        console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
    });
}
$(function () {
    // // 1 添加用户
    // $(document).on("click",".add-div",function () {
    //     layer.ready(function () {
    //         layerIndex = layer.open({
    //             type: 2,
    //             title: false,
    //             maxmin: false,
    //             area: ['100%', '100%'],
    //             content: 'user/user-inner.html'
    //         });
    //     });
    //     layer.full(layerIndex);
    // })
    //
    // // 2 编辑用户信息
    // $(document).on("click",".edit-btn",function () {
    //     var userId = $(this).attr("data-id");
    //     layer.ready(function () {
    //         layerIndex = layer.open({
    //             type: 2,
    //             title: false,
    //             maxmin: false,
    //             area: ['100%', '100%'],
    //             content: 'user/user-inner.html?userId='+userId
    //         });
    //     });
    //     layer.full(layerIndex);
    // })

    // 3 iframe宽度自适应
    $(window).resize(function () {
        var iframe = $(".layui-layer-iframe");
        iframe.css("width" , Math.round(iframe.width() / $(window).width() * 100 ) +"%");
    });

    // // 4 删除用户
    // $(document).on("click",".delete-btn",function () {
    //     var detele_btn = $(this);
    //     var userId = $(this).attr("data-id");
    //     var user_name = $(this).parent().siblings().eq(0).text()
    //     swal({
    //             title: "确认删除该用户?" ,
    //             text:"用户名: "+ user_name,
    //             type: "warning",
    //             showCancelButton: true,
    //             confirmButtonColor: "#DD6B55",
    //             confirmButtonText: "Yes, delete it!",
    //             closeOnConfirm: false
    //         },
    //         function(){
    //             $.ajax({
    //                 type : 'delete',
    //                 url : "/user?id="+userId ,
    //                 contentType : 'application/json;charset=UTF-8',
    //                 dataType : 'json',
    //                 success:function (data) {
    //                     if(data) {
    //                         detele_btn.parent().parent().remove();
    //                         swal("Deleted!", "", "success");
    //                     }else {
    //                         swal("error!", "删除失败", "error");
    //                     }
    //                 },
    //                 error:function (res) {
    //                     layer.msg('数据提交失败 请刷新重试 ');
    //                     console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
    //                 }
    //             })
    //         });
    // })

    // 5 铺用户数据
    getAPageData(1 , 10);

    $(document).on("click" , ".btn-confirm1" , function () {
        getAPageData(pageDataA["pageNum"] , pageDataA["pageSize"]);
    });
});



