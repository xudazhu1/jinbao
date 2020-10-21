$(function () {
    $(document).on("click", ".login-btn", function () {
        $(".alert-div").hide()
        if ($(".userName").val().replace(/(^\s+)|(\s+$)/g, "") === "") {
            $(".alert-div").show();
            return false;
        }
        if ($(".password").val().replace(/(^\s+)|(\s+$)/g, "") === "") {
            $(".alert-div").show();
            return false;
        }
        var url = "/user/login";
        var username = $("#inputEmail3").val();
        var password = $("#inputPassword3").val();
        $.ajax({
            "url": url,
            "data": {"name": username, "password": password},
            "type": "POST",
            "dataType": "json",
            "success": function (json) {
                if (json) {
                    //登陆成功 存入MD5加密后的密码
                    window.localStorage.setItem("password-md5" ,  hex_md5( $("[name='userPassword']").val() ) );
                    window.location.href = "/pages/index.html";
                }
            },
            "error":function (res) {
                $(".alert-div").show();
            }
        })
    })
    document.getElementById("inputPassword3").onkeydown = function (event) {
        var e = event || window.event || arguments.callee.caller.arguments[0];
        if (e && e.keyCode === 13) { // enter 键
           $(".login-btn").trigger("click");
        }
    };
})