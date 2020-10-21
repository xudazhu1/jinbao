$(function () {
    let name;
    var url = "/user/get_session_user"
    var data = "";
    $.ajax({
        "url": url,
        "data": data,
        "type": "GET",
        "dataType": "json",
        "success": function (json) {
            name = json.userName
        }
    })

    $.validator.setDefaults({
        submitHandler: function () {
            var old_password = $("#current_password").val();
            console.log(old_password + "***********");
            var new_password = $("#confirm_password").val()
            var url = "/user/update_password"
            var data = {"newPassword": new_password, "name": name, "oldPassword": old_password}
            $.ajax({
                "url": url,
                "data": data,
                "type": "PUT",
                "dataType": "json",
                "success": function (json) {
                    if (json) {
                        alert("修改成功")
                        window.location.reload()
                    } else {
                        alert(" 密码修改失败!")
                    }

                }
            })
        }
    });
    $().ready(function () {
        // 在键盘按下并释放及提交后验证提交表单
        $("#signupForm").validate({
            rules: {
                current_password: {
                    required: true,
                    rangelength: [6, 16]
                },
                password: {
                    required: true,
                    rangelength: [6, 16]
                },
                confirm_password: {
                    required: true,
                    minlength: 6,
                    equalTo: "#password"
                },
                agree: "required"
            },
            messages: {
                current_password: {
                    required: "请输入密码",
                    minlength: "密码长度为6到16个字符"
                },
                password: {
                    required: "请输入密码",
                    minlength: "密码长度为6到16个字符"
                },
                confirm_password: {
                    required: "请输入密码",
                    minlength: "密码长度为6到16个字符",
                    equalTo: "两次密码输入不一致"
                },
            }
        });
    });
})