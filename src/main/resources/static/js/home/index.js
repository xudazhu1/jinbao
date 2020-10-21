// noinspection ES6ConvertVarToLetConst
var userTemp;
// noinspection ES6ConvertVarToLetConst
var permissionsTemp;

$(function () {
    // 1 获取当前登录的用户
    $.ajax({
        url:"/user/get_session_user",
        data:"",
        type:"GET",
        dataType:"json",
        "success":function (data) {
            if($.isEmptyObject(data)){
                $(window).attr('location', "../login/login.html");
            }else {
                $(".userName").text(data.userName);
            }
            userTemp = data;

        }
    });
    // 1 获取当前登录的用户的权限
    $.ajax({
        url:"/user/permissions",
        data:"",
        type:"GET",
        dataType:"json",
        "success":function (data) {
            permissionsTemp = data;
            //权限
            showPermissionsElem( $( "body") );
        }
    });

    // 2 左侧导航栏点击事件
    $(".layui-layout-left i").click(function () {
        if($(this).hasClass('layui-icon-shrink-right')){
            $(".layui-side").css('width','60px').css('transition','ease-out 0.3s');
            $(".layui-side span").hide();
            $(".layui-body").css('left','60px').css('transition','ease-out 0.3s');
            $(".layui-layout-left").css('left','60px').css('transition','ease-out 0.3s');
            $(this).removeClass('layui-icon-shrink-right').addClass('layui-icon-spread-left');
            $(".layui-nav-item").removeClass('layui-nav-itemed');
        }else if($(this).hasClass('layui-icon-spread-left')){
            $(".layui-side span").show();
            $(".layui-side").css('width','200px');
            $(".layui-body").css('left','200px');
            $(".layui-layout-left").css('left','200px');
            $(this).removeClass('layui-icon-spread-left').addClass('layui-icon-shrink-right')
        }

    });

    // 3 当左侧导航栏收起来后,出现小提示
    var tips;
    $('.layui-side li').on({
        mouseenter:function(){
            var nav_text = $(this).find('span').eq(0).text();
            var nav_i = $(this).find('i');
            if( $(".layui-side").css('width') === '60px'){
                tips =layer.tips(nav_text,nav_i,{tips:2});
            }
        },
        mouseleave:function(){
            layer.close(tips);
        }
    });

    // 4 左侧导航栏处于收缩状态时,触发的点击事件,此时小提示会消失,左侧导航栏展开
    $(".layui-nav-item").click(function () {
        layer.close(tips);
        $(".layui-side span").show();
        $(".layui-side").css('width','200px');
        $(".layui-body").css('left','200px');
        $(".layui-layout-left").css('left','200px');
        $(".layui-layout-left i").removeClass('layui-icon-spread-left').addClass('layui-icon-shrink-right')
    });

    $.get("/flow_notice/num" , function (data) {
        $(".all-notice-num").text(data);
    });
     setInterval(function () {
        $.get("/flow_notice/num" , function (data) {
            $(".all-notice-num").text(data);
        });
    } , 10000);

     //登出事件
    $(".logout").click(function () {
        $.post("/user/logout" , function () {
            window.location.href = $(".logout").attr("data-href");
        } , "json");
    });



});


function showPermissionsElem( bodyDom ) {
    bodyDom.find(".permissions-elem").each(function ( index , elem) {
        if (  hasPermission( $(elem).attr( "data-href" ) ) || hasPermission( $(elem).attr("data-url") ) ) {
            // console.log("有权限 + ");
            // console.log( elem );
            $( elem ).removeClass("permissions-elem");
        }
    });
}


function hasPermission( href ) {
    if ( typeof href === "undefined" || href === null ) {
        return false;
    }
    href = href.replace(/[.]+\//g , "");
    href = href.substring( 0 , href.indexOf( ".html" ) + 5 );
    let has = false;
    $( permissionsTemp ).each( function ( index , permission) {
        try {
            if ( permission["permissionTag"].indexOf( href ) !== -1 || href.indexOf( permission["permissionTag"] ) !== -1  ) {
                has = true;
            }
        } catch (e) {
            
        }
    });
    return has;
}

//验证当前登录密码 从 window.localStorage.getItem("password-md5") 取出数据进行对比
function checkPasswordCheck( password ) {
    return hex_md5( password ) === window.localStorage.getItem("password-md5");

}