let professionClick;
$(function () {
    let way = getParamForUrl("way");
    var id = getParamForUrl("id");
    //如果跳转的参数 不等于 审核 则 删除审核模块
    if(way !== "audit"){
        $(".work-audit").remove();
    }
    layui.use(['laydate'], function () {
        var laydate = layui.laydate;

        // 年月日选择器
        laydate.render({
            type: 'datetime',
            elem: '.workLoadDate',
            trigger: 'click'
        });
    });

    // 1. 铺 下拉框 项目编号
    let numAjax =  $.get("/project/project_num_and_name", {}, function (data) {
        $(data).each(function (index, item) {
            var projectId = $(".projectId");
            projectId.append(
                '<option value="' + item["projectId"] + '"  data-name="' + item["projectName"] + '">' + item["projectNum"] + '</option>'
            )
        });
        layui.use(['form'], function () {
            var form = layui.form;
            layui.form.render('select');
        });
    }, "json");


    // 2. 选择项目编号的时候 获取select 选中的option的属性 set 进 项目名称input
    layui.use(['element', 'laydate', 'layer', 'form'], function () {
        var $ = layui.jquery,
            form = layui.form;
        layui.form.render('select');

        // 2.1 当选择项目编号时,项目名称就对自动填上去
        setTimeout(function () {
            form.on('select(projectId)', function (data) {
                let staffUserBean = $(".staffUserBean");
                let supervisorUserBean = $(".supervisorUserBean");
                let captainUserBean = $(".captainUserBean");
                var dom = $("#implementDepartment");
                dom.empty();
                let find = $(".projectId").find("option:selected").attr("data-name");
                $(".projectName").val(find);
                let id = $(".projectId").find("option:selected").attr("value");
                let implStr = "<option></option>";
                let professionId = $(".professionId");
                // 2.2 铺的时候需要把所有人员 重新赛选
                professionId.empty();
                staffUserBean.empty();
                supervisorUserBean.empty();
                captainUserBean.empty();

                // 2.3 铺部门
                $.get("/project/project_id", {"id":id}, function (data) {
                    $(data["implementBeans"]).each(function (i,implName) {
                        implStr += '<option value="'+implName["implementId"]+'" >' + implName["departmentBean"]["departmentName"] + '</option>'

                    });
                    dom.append(implStr);


                    layui.use(['form'], function () {
                        var form = layui.form;
                        layui.form.render('select');
                    });
                }, "json");


            });
        }, 100);

        // 3. 当选择实施部的时候 会 根据 实施部ID  获取他的 工作类型 然后 铺 其中的 工种
        //    会选 通过实施部名称 去查实施部名称表 关联出 工种
        setTimeout(function () {
            form.on('select(implementDepartment)', function (data) {
                selectImplement();
            });
        }, 100);



        // 4. 选择 工种类型的时候 获取 工种 对应的所有 岗位 （员工,主管,队长） 每个岗位都有队友的人员，并且每个岗位都有 对应的单价  铺到三个 选择框中
        setTimeout(function () {
            form.on('select(professionId)', function (data) {
                //父级元素
                let divParent = $(this).parent().parent().parent().parent().parent().parent();
                console.log(divParent);
                let staffUserLast1 = divParent.children("div:last-child").find(".staffUserBean");
                let supervisorUserLast1 =  divParent.children("div:last-child").find(".supervisorUserBean");
                let captainUserLast1 =  divParent.children("div:last-child").find(".captainUserBean");
                let laborCostEle = divParent.children().find(".laborCost");

                laborCostEle.val(0);
                //价格为空
                let price = divParent.children().find(".price");
                //价格为空
                let workLoadWorkLoad = divParent.children().find(".workLoadWorkLoad");
                workLoadWorkLoad.val(0);
                price.val("");
                staffUserLast1.empty();
                supervisorUserLast1.empty();
                captainUserLast1.empty();
                let id = divParent.children("div:last-child").find(".professionId").find("option:selected").val();
                //当选择工种的时候 获取所有的 员工 主管 队长
                professionClick = $.get("/profession" , {"professionId":id},function (data) {

                        let staffStr = "<option></option>";
                        let supervisorStr = "<option></option>";
                        let captainStr = "<option></option>";
                        //铺员工
                        $(data[0]["staffBeanList"]).each(function (i,staff) {
                            $(staff["postBean"]).each(function (j,post) {
                                let postName = staff["postBean"]["postName"];
                                $(post["staffUserList"]).each(function (x,user) {
                                    //价格是工种对应岗位的价格,  id 是 userBean 的id 和 名称
                                    staffStr += '<option job="'+user["userBean"]["jobBean"]["jobId"]+'"  price="'+staff["staffPrice"]+'" value="' + user["userBean"]["userId"] + '">' + user["userBean"]["userName"] +"("+ postName + "-" + staff["staffPrice"]+")"+'</option>';
                                });
                            });

                        });
                        //铺主管
                        $(data[0]["supervisorBeanList"]).each(function (i,supervisor) {
                            $(supervisor["postBean"]).each(function (j,post) {
                                let postName = supervisor["postBean"]["postName"];
                                $(post["supervisorUserList"]).each(function (x,user) {
                                    supervisorStr += '<option price="'+supervisor["supervisorPrice"]+'" value="' + user["userBean"]["userId"] + '">' + user["userBean"]["userName"] +"(" + postName + supervisor["supervisorPrice"]+")" + '</option>';
                                });
                            });

                        });
                        //铺队长
                        $(data[0]["captainBeanList"]).each(function (i,captain) {
                            $(captain["postBean"]).each(function (j,post) {
                                let postName = captain["postBean"]["postName"];
                                $(post["captainUserList"]).each(function (x,user) {
                                    captainStr += '<option price="'+captain["captainPrice"]+'" value="' + user["userBean"]["userId"] + '">' + user["userBean"]["userName"] + "("+ postName + captain["captainPrice"] +")"+ '</option>';
                                });
                            });

                        });
                        staffUserLast1.append(staffStr);
                        supervisorUserLast1.append(supervisorStr);
                        captainUserLast1.append(captainStr);
                        layui.use(['form'], function () {
                            var form = layui.form;
                            layui.form.render('select');
                        });
                },"json");
                let professionName =  divParent.children("div:last-child").find(".professionId").find("option:selected").text();
                console.log(professionName);
                //设置进 工作量 写死的 string
                $(".workLoadProfessionName").val(professionName);
                //选了 工种 默认 价格清空
                divParent.children(".price").val(0);

            });
        }, 100);


        /*员工 提成金额是 工作量 * 单价 主管跟队长是 员工的提成金额 * 0.3*/
        //点击员工 铺单价
        setTimeout(function () {
            form.on('select(staffUserBean)', function (data) {
                    let post = $(this).parent().parent().parent().parent().parent().parent();
                    let price =  post.children().find(".staffUserBean").find("option:selected").attr('price');
                    if(typeof price === "undefined"){
                        price = 0;
                    }

                    let jobId =  post.children().find(".staffUserBean").find("option:selected").attr('job');
                    post.children().find(".BelongJob").val(jobId);
                    let this_work = post.children().find(".workLoadWorkLoad").val();
                    let s_price =  post.children().find(".supervisorUserBean").find("option:selected").attr('price');
                    let c_price =  post.children().find(".captainUserBean").find("option:selected").attr('price');
                    post.children().find(".workLoadPriceStaff").val(price);
                    post.children().find(".workLoadAmountStaff").val((price * this_work).toFixed(2));
                    let amountPrice = price * this_work;
                    if(typeof s_price === "undefined"){
                        s_price = 0;
                    }
                    if(typeof c_price === "undefined"){
                        c_price = 0;
                    }
                    if(post.children().find(".supervisorUserBean").val() !== ""){
                        post.children().find(".workLoadAmountManage").val((amountPrice * s_price).toFixed(2));
                    }
                    if(post.children().find(".captainUserBean").val() !== "" ){
                        post.children().find(".workLoadAmountCaptain").val((amountPrice * c_price).toFixed(2));
                    }

                })
            });
        //点击主管 铺单价
        setTimeout(function () {
            form.on('select(supervisorUserBean)', function (data) {
                let post = $(this).parent().parent().parent().parent().parent().parent();
                let price =  post.children().find(".workLoadAmountStaff").val();
                let this_work = post.children().find(".workLoadWorkLoad").val();
                let this_price =  post.children().find(".supervisorUserBean").find("option:selected").attr('price');
                if(price === undefined){
                    price = 0;
                }
                if(this_price === undefined){
                    this_price = 0;
                    price = 0;
                }
                post.children().find(".workLoadAmountManage").val((price * this_price).toFixed(2));

            })
        });
        //点击队长 铺单价
        setTimeout(function () {
            form.on('select(captainUserBean)', function (data) {
                let post = $(this).parent().parent().parent().parent().parent().parent();
                let price =  post.children().find(".workLoadAmountStaff").val();
                let this_work = post.children().find(".workLoadWorkLoad").val();
                let this_price =  post.children().find(".captainUserBean").find("option:selected").attr('price');
                if(price === undefined){
                    price = 0;
                }
                if(this_price === undefined){
                    this_price = 0;
                    price = 0;
                }
                post.children().find(".workLoadAmountCaptain").val((price * this_price).toFixed(2));
            })
        }, 100);


    });

    if (id !== null) {
        //把项目编号select 删除 添加成input
        let projectId = $(".projectId");
        let projectParent = projectId.parent();
        projectId.remove();
        projectParent.append("<input class='projectNum need-input layui-input' lay-verify='required' readonly   >");
        let auditId = 0;
        let ajax1 = $.get("/work_load/work_load_id", {"id": id}, function (data) {
            //把实施 删除 添加成input
            let implementDepartmentId = $(".implementDepartment");
            let implementParent = implementDepartmentId.parent();
            implementDepartmentId.remove();
            implementParent.append("<input class='implementDepartment need-input layui-input' lay-verify='required' readonly  " +
                "value='"+data["implementBean"]["departmentBean"]["departmentName"]+"' >");

            implementParent.append("<input type='hidden' name='workLoadId' value='"+data["workLoadId"]+"'>");

            //删除 实施id 上传
            $("#implementId").remove();

            //获取表单元素 把List 类型的name 去掉前缀 重新 赋值name
            $("[name^='workLoadBeans[0]']").each(function (i,ele) {
                let newAttr = $(ele).attr("name").substring(17);
                $(ele).attr("name",newAttr)
            });
            //获取审核id
            auditId = data["workLoadStatusBean"]["workLoadStatusId"];
            //设置 审核状态
            $(".workLoadStatusId").val(auditId);
            //获取 三个 人员表单元素
            let staffUserLast = $(".staffUserBean");
            let supervisorUserLast =  $(".supervisorUserBean");
            let captainUserLast =  $(".captainUserBean");
            //注入项目编号和名称
            $.when(numAjax).done(function () {
                $(".projectNum").val(data["implementBean"]["projectBean"]["projectNum"]);
                $(".projectName").val(data["implementBean"]["projectBean"]["projectName"]);
            });
            //调用铺 人员 函数
            let professionAjax = selectImplement();
            $.when(professionAjax).done(function () {
                let professionId = $(".professionId");
                professionId.val(data["professionBean"]["professionId"]);
                renderFix("professionId");
                setTimeout(function () {
                    $(".professionId").parent().parent().find("dl dd.layui-this").trigger('click');
                    for (var key in data) {
                        $("."+key).val(data[key]);
                    }
                    $.when(professionClick).done(function () {
                        $(".staffUserBean").val(data["staffUserBean"] != "" ? data["staffUserBean"]["userId"] : "");
                        $(".captainUserBean").val(data["captainUserBean"] != "" ? data["captainUserBean"]["userId"] : "");
                        $(".supervisorUserBean").val(data["supervisorUserBean"] != "" ? data["supervisorUserBean"]["userId"] : "");
                        $(".dayNum").val(data["workLoadDuration"]);
                        if(way === "audit" && auditId === 1){
                            forbidInput();
                            $(".workLoadStatusId option[value='1']").remove();
                        }
                        if(way === "audit" && auditId === 3){
                            $(".workLoadStatusId option[value='2']").remove();
                        }
                        //如果 实施部前面没删除 则现在删除
                        implementParent.find(".layui-form-select").remove();

                        layui.use(['form'], function () {
                            var form = layui.form;
                            layui.form.render('select');
                        });

                    });


                }, 100);

            });

            layui.use(['form'], function () {
                var form = layui.form;
                layui.form.render('select');
            });


        }, "json");


    }

    //点击工作量
    $(document).on("change",".workLoadWorkLoad",function () {
        let post = $(this).parent().parent().parent().parent().parent();
        let this_work = this.value;

        let price = post.children().find(".staffUserBean").find("option:selected").attr("price");
        let s_price =  post.children().find(".supervisorUserBean").find("option:selected").attr('price');
        let c_price =  post.children().find(".captainUserBean").find("option:selected").attr('price');
        if(typeof price === "undefined"){
            price = 0;
        }
        if(typeof s_price === "undefined"){
            s_price = 0;
        }
        if(typeof c_price === "undefined"){
            c_price = 0;
        }
        let prop = price * this_work;
        post.children().find(".workLoadAmountStaff").val(prop);

        if(post.children().find(".supervisorUserBean").val() !== ""){
            post.children().find(".workLoadAmountManage").val((prop * s_price).toFixed(2));
        }
        if(post.children().find(".captainUserBean").val() !== ""){
            post.children().find(".workLoadAmountCaptain").val((prop * c_price).toFixed(2));
        }
    });

    //点击提交
    let status = false;
    $("#workload_submit_project").click(function () {
        if (!formChecking() || status) {
            return false;
        }
        status = true;
        let itemData;
        let url;
        if(id === null){
            url = "/implement";
            itemData = $.param({"_method": "POST"}) + "&" + $("#workloadForm").serialize();
        }else {
            url = "/work_load";
            itemData = $.param({"_method": "PUT"}) + "&" + $("#workloadForm").serialize();
        }

        $.post(url , itemData, function (data) {
            if (data) {
                layer.msg('操作成功 2秒后自动刷新', {
                    time: 2000 //2秒关闭（如果不配置，默认是3秒）
                } ,  function () {
                    window.location.reload();
                    window.parent.flush();
                    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                    parent.layer.close(index); //再执行关闭
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

    //删除工作量
    $(document).on("click", ".layui-icon-delete", function () {
        $(this).closest(".layui-quote-nm").remove();
    });

    //新增工种量
    $(document).on("click",".add-workLoad-btn-project",function () {
        let find = $(".implementDepartment").find("option:selected").text();
        if(find === ''){
            return alert("请先选择实施部");
        }
        let idNum = parseInt(Math.random()*50);//获取随机数
        let workBody = $("#workBody");
        let index =workBody.children().length;
        //获取上一个 div 里面的日期
        let date = workBody.children("div:last-child").find(".workLoadDate").val();
        //复制工种
        let profession = workBody.children("div:last-child").find(".professionId").children().clone();
        //获得最后一个工种id
        let selectId = workBody.children("div:last-child").find(".professionId").val();
        let professionOption = "";
        $(profession).each(function (i,option) {
            if($(option).val() === selectId){
                $(option).attr("selected","selected")
            }
            professionOption += option.outerHTML
        });

        //复制员工option元素
        let supervisorUserOption = "";
        let supervisorUserBean = workBody.children("div:last-child").find(".supervisorUserBean").children().clone();
        let supervisorUserId = workBody.children("div:last-child").find(".supervisorUserBean").val();
        $(supervisorUserBean).each(function (i,option) {
            if($(option).val() === supervisorUserId){
                $(option).attr("selected","selected")
            }
            supervisorUserOption += option.outerHTML
        });

        //复制主管option元素
        let staffUserOption = "";
        let staffUserBean = workBody.children("div:last-child").find(".staffUserBean").children().clone();
        $(staffUserBean).each(function (i,option) {
            staffUserOption += option.outerHTML
        });

        //复制队长option元素
        let captainUserOption = "";
        let captainUserBean = workBody.children("div:last-child").find(".captainUserBean").children().clone();
        let captainUserId = workBody.children("div:last-child").find(".captainUserBean").val();
        $(captainUserBean).each(function (i,option) {
            if($(option).val() === captainUserId){
                $(option).attr("selected","selected")
            }
            captainUserOption += option.outerHTML
        });

        var blockquote = "<div class='add-project-div' style='position:relative' >\n" +
            "                                                <!--第一个项目工作量添加-->\n" +
            "                                                <blockquote class='layui-elem-quote layui-quote-nm'>\n" +
            "                                                    <div class='layui-row layui-form-item'>\n" +
            "                                                        <div class='layui-col-xs6 layui-col-sm6 layui-col-md3 layui-col-space12'>\n" +
            "\n" +
            "                                                            <label class='layui-form-label'>*日期：</label>\n" +
            "                                                            <div class='layui-input-block' >\n" +
            "                                                                <input type='text'  name='workLoadBeans["+index+"].workLoadDate' id='workLoadDate"+idNum+"' title='日期'  autocomplete='off'\n" +
            "                                                                       class='layui-input workLoadDate need-input' value='"+date+"' >\n" +
            "                                                            </div>\n" +
            "\n" +
            "                                                            <label class='layui-form-label'>*员工：</label>\n" +
            "                                                            <div class='layui-input-block'>\n" +
            "                                                                <select name='workLoadBeans["+index+"].staffUserBean.userId' data-notice-path='staffUserBean.userName' lay-verify='require' class='staffUserBean need-input' title='' lay-filter='staffUserBean' lay-search>\n" +
            staffUserOption +
            "                                                                </select>\n" +
            "                                                            </div>\n" +
            "\n" +
            "                                                        </div>\n" +
            "\n" +
            "                                                        <div class='layui-col-xs4 layui-col-sm12 layui-col-md3 layui-col-space12'>\n" +
            "                                                            <label class='layui-form-label'>*工种类型：</label>\n" +
            "                                                            <div class='layui-input-block'>\n" +
            "                                                                <select name='workLoadBeans["+index+"].professionBean.professionId' data-notice-path='professionBean.professionName' class='professionId need-input' title='' lay-verify='require' lay-filter='professionId' lay-search>\n" +
            professionOption +
            "                                                                </select>\n" +
            "                                                                <input type='hidden' class='workLoadProfessionName' name='workLoadBeans["+index+"].workLoadProfessionName'>\n" +
            "                                                                <input type='hidden'  name='workLoadBeans["+index+"].workLoadStatusBean.workLoadStatusId' value='1'>\n" +
            "                                                            </div>\n" +
            "                                                            <label class='layui-form-label' >*工作量：</label>\n" +
            "                                                            <div class='layui-input-block'>\n" +
            "                                                                <input type='number'  name='workLoadBeans["+index+"].workLoadWorkLoad'  placeholder='' autocomplete='off' lay-verify='require'\n" +
            "                                                                       value='0'    class='layui-input workLoadWorkLoad'>\n" +
            "                                                            </div>\n" +
            "\n" +
            "                                                            <input title='单价' type='hidden'  class='workLoadPriceStaff price' name='workLoadBeans["+index+"].workLoadPriceStaff' readonly data-name='workLoadPriceStaff' style='border: none;align-items: center' >\n" +
            "                                                            <input title='单价' type='hidden' name='workLoadBeans["+index+"].workLoadPriceManage' class='workLoadPriceManage price' readonly data-name='workLoadPriceManage' style='border: none;align-items: center'>\n" +
            "                                                            <input title='单价' type='hidden'  name='workLoadBeans["+index+"].workLoadPriceCaptain' class='workLoadPriceCaptain price' readonly data-name='workLoadPriceCaptain' style='border: none;align-items: center'>\n" +
            "\n" +
            "\n" +
            "                                                        </div>\n" +
            "\n" +
            "                                                        <div class='layui-col-xs6 layui-col-sm6 layui-col-md3 layui-col-space12'>\n" +
            "                                                            <label class='layui-form-label'>主管：</label>\n" +
            "                                                            <div class='layui-input-block'>\n" +
            "                                                                <select name='workLoadBeans["+index+"].supervisorUserBean.userId' data-notice-path='supervisorUserBean.userName' class='supervisorUserBean project-relate' title='' lay-filter='supervisorUserBean' lay-search>\n" +
            supervisorUserOption +
            "                                                                </select>\n" +
            "                                                            </div>\n" +
            "                                                            <label class='layui-form-label' >工作天数：</label>\n" +
            "                                                            <div class='layui-input-block'>\n" +
            "                                                                <label>\n" +
            "                                                                    <input type='text' class='dayNum layui-input'  name='workLoadBeans["+index+"].workLoadDuration'>           " +
            "                                                                </label>\n" +
            "                                                            </div>\n" +
            "\n" +
            "                                                            <input title='提成金额' type='hidden' style='border: none' name='workLoadBeans["+index+"].workLoadAmountStaff' class='workLoadAmountStaff price' data-name='workLoadAmountStaff' readonly>\n" +
            "                                                            <input title='人员成本' type='hidden'  class='layui-input laborCost' name='workLoadBeans["+index+"].workLoadLaborCost' style='border: none;align-items: center' readonly  >\n" +
            "                                                            <input title='提成金额' type='hidden' style='border: none' name='workLoadBeans["+index+"].workLoadAmountManage' class='workLoadAmountManage price' data-name='workLoadAmountManage' readonly>\n" +
            "                                                            <input title='提成金额' type='hidden' style='border: none' name='workLoadBeans["+index+"].workLoadAmountCaptain' class='workLoadAmountCaptain price' data-name='workLoadAmountCaptain' readonly>\n" +
            "                                                            <input title='数据职位' type='hidden' style='border: none' name='workLoadBeans["+index+"].createJobBean' class='BelongJob'  >\n" +
            "\n" +
            "\n" +
            "                                                        </div>\n" +
            "\n" +
            "                                                        <div class='layui-col-xs6 layui-col-sm6 layui-col-md3 layui-col-space12'>\n" +
            "                                                            <label class='layui-form-label'>队长：</label>\n" +
            "                                                            <div class='layui-input-block'>\n" +
            "                                                                <select name='workLoadBeans["+index+"].captainUserBean.userId' data-notice-path='captainUserBean.userName' class='captainUserBean project-relate' title='' lay-filter='captainUserBean' lay-search>\n" +
            captainUserOption+
            "                                                                </select>\n" +
            "                                                            </div>" +
            "                                                        <label class='layui-form-label'>工作备注：</label>\n" +
            "                                                            <div class='layui-input-block'>\n" +
            "                                                                <textarea name='workLoadBeans["+index+"].workLoadRemark' data-notice-path='workLoadRemark' class='workLoadRemark project-relate layui-textarea' placeholder='请输入内容'>\n" +
            "                                                                </textarea>\n" +
            "                                                            </div>\n" +
            "                                                        </div>\n" +
            "<button  type='button' class='work_del layui-btn layui-btn-danger layui-btn-xs btn-delete' style='position:absolute;right: 0;bottom: 0;'>删除</button>\n" +
            "                                                    </div>\n" +
            "                                                </blockquote>\n" +
            "                                            </div>";
        workBody.append(blockquote);

        // selectImplement();



        layui.use(['laydate'], function () {
            var laydate = layui.laydate;

            // 年月日选择器
            laydate.render({
                type: 'datetime',
                elem: '#workLoadDate'+idNum,
                trigger: 'click'
            });
        });
        layui.use(['form'], function () {
            var form = layui.form;
            layui.form.render('select');
        });
    });

    //新增的工作量删除
    $(document).on("click",".btn-delete",function () {
        $(this).parent().parent().parent().remove();
    });
});



//选择 实施部的时候 铺 工种的方法
function selectImplement(){
    //获取选中的 select 实施部名称
    let find = $(".implementDepartment").find("option:selected").text();
    if (find === ""){
        find =  $(".implementDepartment").val();
    }
    var professionId = $(".professionId");
    let staffUserLast = $(".staffUserBean");
    let supervisorUserLast =  $(".supervisorUserBean");
    let captainUserLast =  $("div:last-child").find(".captainUserBean");
    professionId.empty();
    staffUserLast.empty();
    supervisorUserLast.empty();
    captainUserLast.empty();
    professionId.append(
        '<option ></option>'
    );
    console.log(professionId);
    //调用铺 工种
    let professionAjax = $.get("/profession", {"departmentBean.departmentName":find}, function (data) {
        $(data).each(function (i,profession) {
            professionId.append(
                '<option  value="'+profession["professionId"]+'" >' + profession["professionName"] + '</option>'
            )
        });

        layui.use(['form'], function () {
            var form = layui.form;
            layui.form.render('select');
        });

    }, "json");
    return professionAjax;
}

function forbidInput(){
    $(".work-content select").each(function (i,ele) {
        $(this).siblings().remove();
        $(this).before("<p>"+$(this).find("option:selected").text()+"</p>");
        $(this).remove();
    });
    $(".work-content input").each(function (i,ele) {
        if ($(this).attr("type") === "hidden"){
            return;
        }
        $(this).before("<p>"+$(this).val()+"</p>");
        $(this).remove();
    })
}

