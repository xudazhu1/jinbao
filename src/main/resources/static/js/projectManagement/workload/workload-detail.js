let way = getParamForUrl("way");
console.log(way);

function forbidInput(){
    $(".work-content select").each(function (i,ele) {
        $(this).siblings().remove();
        $(this).before("<p>"+$(this).find("option:selected").text()+"</p>");
        $(this).remove();
    })
    $(".work-content input").each(function (i,ele) {
        $(this).before("<p>"+$(this).val()+"</p>");
        $(this).remove();
    })
}
function addProfessionAndUserList(id){

}
$(function() {
    if(way !== "audit"){
        $(".work-audit").remove();
    }
    //获取选中的 select 实施部名称

    var professionId = $("#workBody").children("div:last-child").find(".professionId");
    let staffUserLast = $("#workBody").children("div:last-child").find(".staffUserBean");
    let supervisorUserLast =  $("#workBody").children("div:last-child").find(".supervisorUserBean");
    let captainUserLast =  $("#workBody").children("div:last-child").find(".captainUserBean");
    professionId.empty();
    staffUserLast.empty();
    supervisorUserLast.empty();
    captainUserLast.empty();
    layui.use(['form'], function () {
        var form = layui.form;
        layui.form.render('select');
    });
    professionId.append(
        '<option ></option>'
    );


    layui.use(['element', 'laydate', 'layer', 'form'], function () {
        var $ = layui.jquery,
            form = layui.form;
        layui.form.render('select');

        // 4. 选择 工种类型的时候 获取 工种 对应的所有 岗位 （员工,主管,队长） 每个岗位都有队友的人员，并且每个岗位都有 对应的单价  铺到三个 选择框中
        setTimeout(function () {
            form.on('select(professionId)', function (data) {
                paveProfession($(this))
            });
        }, 100);

        //点击员工 铺单价
        setTimeout(function () {
            form.on('select(staffUserBean)', function (data) {
                let post = $(this).parent().parent().parent().parent().parent().parent();
                let price =  post.children().find(".staffUserBean").find("option:selected").attr('price');
                let salary =  post.children().find(".staffUserBean").find("option:selected").attr('salary');
                post.children().find(".workLoadPriceStaff").val(price);
                let salaryEle = post.children().find(".salary");
                let dayNumEle = post.children().find(".dayNum");
                let dayNum = dayNumEle.val();
                if(dayNum === ""){
                    dayNum = 0;
                }
                salaryEle.val(salary);
                let laborCostEle = post.children().find(".laborCost");
                laborCostEle.val(dayNum * salary);
                let staffPrice = post.children().find(".staffUserBean").find("option:selected").attr("price");
                let this_work = post.children().find(".workLoadWorkLoad").val();
                post.children().find(".workLoadAmountStaff").val((staffPrice * this_work).toFixed(2));
            })
        });
        //点击主管 铺单价
        setTimeout(function () {
            form.on('select(supervisorUserBean)', function (data) {
                let post = $(this).parent().parent().parent().parent().parent().parent();
                let price =  post.children().find(".supervisorUserBean").find("option:selected").attr('price');
                post.children().find(".workLoadPriceManage").val(price);

                let staffPrice = post.children().find(".supervisorUserBean").find("option:selected").attr("price");
                let this_work = post.children().find(".workLoadWorkLoad").val();
                post.children().find(".workLoadAmountManage").val((staffPrice * this_work).toFixed(2));
            })
        });
        //点击队长 铺单价
        setTimeout(function () {
            form.on('select(captainUserBean)', function (data) {
                let post = $(this).parent().parent().parent().parent().parent().parent();
                let price =  post.children().find(".captainUserBean").find("option:selected").attr('price');
                post.children().find(".workLoadPriceCaptain").val(price);

                let staffPrice = post.children().find(".captainUserBean").find("option:selected").attr("price");
                let this_work = post.children().find(".workLoadWorkLoad").val();
                post.children().find(".workLoadAmountCaptain").val((staffPrice * this_work).toFixed(2));
            })
        }, 100);
    });
    //传入工种对象
    function paveProfession(data){
        let divParent = data.parent().parent().parent().parent().parent().parent();
        let staffUserLast1 = divParent.children("div:last-child").find(".staffUserBean");
        let supervisorUserLast1 =  divParent.children("div:last-child").find(".supervisorUserBean");
        let captainUserLast1 =  divParent.children("div:last-child").find(".captainUserBean");
        staffUserLast1.empty();
        supervisorUserLast1.empty();
        captainUserLast1.empty();
        let professionName =  divParent.children("div:last-child").find(".professionId").find("option:selected").text();
        $(".workLoadProfessionName").val(professionName);
        let id = divParent.children("div:last-child").find(".professionId").find("option:selected").val();
        //当选择工种的时候 获取所有的 员工 主管 队长
        $.get("/profession" , {"professionId":id},function (data) {

            let staffStr = "<option></option>";
            let supervisorStr = "<option></option>";
            let captainStr = "<option></option>";
            //铺员工
            $(data[0]["staffBeanList"]).each(function (i,staff) {
                $(staff["postBean"]).each(function (j,post) {
                    $(post["staffUserList"]).each(function (x,user) {
                        let salary = 0;
                        if (user["salaryBean"] !== ""){
                            salary =  user["salaryBean"]["salaryDailyCost"];                                   }
                        staffStr += '<option salary="'+salary+'" price="'+staff["staffPrice"]+'" value="' + user["userId"] + '">' + user["userName"] + '</option>';
                    });
                });

            });
            //铺主管
            $(data[0]["supervisorBeanList"]).each(function (i,supervisor) {
                $(supervisor["postBean"]).each(function (j,post) {
                    $(post["supervisorUserList"]).each(function (x,user) {
                        supervisorStr += '<option price="'+supervisor["supervisorPrice"]+'" value="' + user["userId"] + '">' + user["userName"] + '</option>';
                    });
                });

            });
            //铺队长
            $(data[0]["captainBeanList"]).each(function (i,captain) {
                $(captain["postBean"]).each(function (j,post) {
                    $(post["captainUserList"]).each(function (x,user) {
                        captainStr += '<option price="'+captain["captainPrice"]+'" value="' + user["userId"] + '">' + user["userName"] + '</option>';
                    });
                });

            });
            console.log(captainStr);
            staffUserLast1.append(staffStr);
            supervisorUserLast1.append(supervisorStr);
            captainUserLast1.append(captainStr);
            layui.use(['form'], function () {
                var form = layui.form;
                layui.form.render('select');
            });



        },"json");
    }

    var id = getParamForUrl("id");
    if (id !== null) {
        var auditId = 0;
        var ajax1 = $.get("/work_load/work_load_id", {"id": id}, function (data) {
            auditId = data["workLoadStatusBean"]["workLoadStatusId"];
            for (var key in data) {
                $("."+key).val(data[key]);

            }
            $(".workLoadStatusId").val(auditId);
            let staffUserLast = $(".staffUserBean");
            let supervisorUserLast =  $(".supervisorUserBean");
            let captainUserLast =  $(".captainUserBean");
            $(".projectNum").val(data["implementBean"]["projectBean"]["projectNum"]);
            $(".projectName").val(data["implementBean"]["projectBean"]["projectName"]);
            $("#implementDepartment").val(data["implementBean"]["departmentBean"]["departmentName"]);
            let find = $("#implementDepartment").val();
            //调用铺 工种

            $.get("/department/implement_department", {"departmentName":find}, function (implData) {
                var implIndex = 0;

                $(implData).each(function (i,e) {
                    if(e["departmentName"] === find){
                        professionListData = implData[i]["professionBeanList"];
                        implIndex = i;
                    }
                });
                $(implData[implIndex]["professionBeanList"]).each(function (i,profession) {
                    professionId.append(
                        '<option  value="'+profession["professionId"]+'" >' + profession["professionName"] + '</option>'
                    )
                });
                $(".professionId").val(data["professionBean"]["professionId"]);

                var ajax3 = $.get("/profession" , {"professionId":data["professionBean"]["professionId"]},function (data) {
                    let staffUserLast = $("#workBody").children("div:last-child").find(".staffUserBean");
                    let supervisorUserLast =  $("#workBody").children("div:last-child").find(".supervisorUserBean");
                    let captainUserLast =  $("#workBody").children("div:last-child").find(".captainUserBean");
                    let staffStr = "<option></option>";
                    let supervisorStr = "<option></option>";
                    let captainStr = "<option></option>";
                    //铺员工
                    $(data[0]["staffBeanList"]).each(function (i,staff) {
                        $(staff["postBean"]).each(function (j,post) {
                            $(post["staffUserList"]).each(function (x,user) {
                                let salary = 0;
                                if (user["salaryBean"] !== ""){
                                    salary =  user["salaryBean"]["salaryDailyCost"];                                   }
                                staffStr += '<option salary="'+salary+'" price="'+staff["staffPrice"]+'" value="' + user["userId"] + '">' + user["userName"] + '</option>';
                            });
                        });

                    });
                    //铺主管
                    $(data[0]["supervisorBeanList"]).each(function (i,supervisor) {
                        $(supervisor["postBean"]).each(function (j,post) {
                            $(post["supervisorUserList"]).each(function (x,user) {
                                supervisorStr += '<option price="'+supervisor["supervisorPrice"]+'" value="' + user["userId"] + '">' + user["userName"] + '</option>';
                            });
                        });

                    });
                    //铺队长
                    $(data[0]["captainBeanList"]).each(function (i,captain) {
                        $(captain["postBean"]).each(function (j,post) {
                            $(post["captainUserList"]).each(function (x,user) {
                                captainStr += '<option price="'+captain["captainPrice"]+'" value="' + user["userId"] + '">' + user["userName"] + '</option>';
                            });
                        });

                    });
                    staffUserLast.append(staffStr);
                    supervisorUserLast.append(supervisorStr);
                    captainUserLast.append(captainStr);
                    layui.use(['form'], function () {
                        var form = layui.form;
                        layui.form.render('select');
                    });
                },"json");

                $.when(ajax3).done(function () {
                    staffUserLast.val(data["staffUserBean"]["userId"]);
                    supervisorUserLast.val(data["supervisorUserBean"]["userId"]);
                    captainUserLast.val(data["captainUserBean"]["userId"]);
                    //铺工资
                    let salary = staffUserLast.find("option:selected").attr("salary");
                    //铺时间
                    $(".dayNum").find("option:contains('"+data["workLoadDuration"]+"')").attr('selected',true);
                    //铺人员成本
                    $(".laborCost").val(data["workLoadLaborCost"])
                    console.log(data["workLoadDuration"]);
                    $(".salary").val(salary);
                    layui.use(['form'], function () {
                        var form = layui.form;
                        layui.form.render('select');
                    });
                    //所做操作
                    console.log(auditId);
                    if(way === "audit" && auditId === 1){
                        forbidInput();
                    }
                });

            }, "json");

            layui.use(['form'], function () {
                var form = layui.form;
                layui.form.render('select');
            });



        }, "json");


    }


    let status = false;
    $("#workload_submit_update").click(function () {
        status = true;
        let itemData = $.param({"_method": "PUT"}) + "&" + $("#workloadFormUpdate").serialize();
        $.post("/work_load" , itemData, function (data) {
            if (data) {
                layer.msg('操作成功 2秒后自动刷新', {
                    time: 2000 //2秒关闭（如果不配置，默认是3秒）
                } ,  function () {
                    window.location.reload();
                    window.parent.flush();
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
    })
});
