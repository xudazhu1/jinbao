//两点划线函数
function showLine(beginIndex , endIndex ) {
    if ( beginIndex.x !== endIndex.x && beginIndex.y !== endIndex.y )  {
        return null;
    }
    let temp = Math.ceil(Math.random() * 100000);
    //如果y轴相等 画横线
    if ( beginIndex.y === endIndex.y ) {
        $(".canvas").append(
            "<canvas class='my-line heng' id='"+temp+"'    " +
            "height='0'  width=\""+ Math.abs(endIndex.x - beginIndex.x) +"\"  " +
            "></canvas>");
    }
    //如果x轴相等 画竖线
    if ( beginIndex.x === endIndex.x ) {
        $(".canvas").append(
            "<canvas class='my-line shu' id='"+temp+"' " +
            "width=\"0\" height=\""+ Math.abs(endIndex.y - beginIndex.y) +"\"  " +
            "></canvas>");
    }
    $("#" + temp).css("top" ,
        ( beginIndex.y < endIndex.y ? beginIndex.y : endIndex.y )).css("left" ,
        (beginIndex.x < endIndex.x ? beginIndex.x : endIndex.x));

}

//两个div间连线的方法
function showLineInDom(JqDomA , JqDomB) {
    if ( JqDomA.closest("ul").css("display") === "none" ||  JqDomB.closest("ul").css("display") === "none" ) {
        return false;
    }
    let indexA = JqDomA.offset();
    let indexB = JqDomB.offset();

    let beginIndexA = {};
    beginIndexA.x = indexA.left + (JqDomA.outerWidth()/2);
    beginIndexA.y = indexA.top + JqDomA.outerHeight();

    let endIndexA = {};
    endIndexA.x = beginIndexA.x;
    endIndexA.y = beginIndexA.y + 50;
    //画第一条线
    showLine(beginIndexA , endIndexA) ;

    let endIndexC = {};
    endIndexC.x = indexB.left + (JqDomB.outerWidth()/2);
    endIndexC.y = indexB.top-2;

    let beginC = {};
    beginC.x = endIndexC.x;
    beginC.y = endIndexA.y;

    //画第二条线
    showLine(beginC , endIndexA) ;

    //第三条
    showLine(beginC , endIndexC) ;

}

//铺所有的线 的方法
function showAllLine(){
    $(".canvas").empty();
    $("li .node-box").each(function (index , beginDom) {
        $(beginDom).closest("li").children("ul").children("li").children(".node-box").each(function (index , endDom) {
            showLineInDom($(beginDom) , $(endDom));
        })
    });
}

//用来接受鼠标位置的全局变量
var moveX;
var moveY;
function mouse_move(e)
{
    e=e  || window.event;
    if(e.pageX || e.pageY)
    {
        moveX=e.pageX;
        moveY=e.pageY
    }
}
document.onmousemove=mouse_move;
//判断鼠标是否在某元素内 传入参数是元素的jq对象
function inner(div) {
    var x = moveX;//鼠标的位置-X
    var y = moveY;//鼠标的位置-Y
    var y1 = div.offset().top;
    //div上面两个的点的y值
    var y2 = y1 + div.outerHeight();
    //div下面两个点的y值
    var x1 = div.offset().left;
    //div左边两个的点的x值
    var x2 = x1 + div.outerWidth();
    //div右边两个点的x的值
    if( x < x1 || x > x2 || y < y1 || y > y2 ) {
        console.log('');
        return false;
    }else{
        //	alert('鼠标在该DIV中');
        return true;
    }
}


var layerIndex;
$(function () {
    //1 执行划线函数
    // showAllLine();

    //2 点击加号,出现添加下级结构弹出层
    layui.use('layer', function () {
        layerIndex = layui.layer
    });
    $(document).on('click','.add-element',function () {
        let type = $(this).attr("data-type");
        let typeName = $(this).attr("data-type-name");
        let parentName = $(this).attr("data-parent-name");
        let parentKey = $(this).attr("data-parent-key");
        let parentId = $(this).attr("data-parent-id");
        layer.ready(function () {
                    layerIndex = layer.open({
                        type: 2,
                        title: '添加'+ typeName,
                        maxmin: true,
                        area: ['440px', '300px'],
                        content: 'structure/addStructure.html?type='+type+'&typeName='+typeName+'&parentName='+parentName+'&parentKey=' + parentKey +'&parentId='+parentId
                    });
                });
    });

    //3点击节点,跳出该节点的编辑页面弹出层
    $(document).on('click','.node-name',function () {
        let parentName = $(this).closest("ul").siblings(".node-box").find("span").text();
        let thisName = $(this).text();
        let spanType = $(this).attr("data-span-type");
        let spanId = $(this).attr("data-span-id");
        layer.ready(function () {
            layerIndex = layer.open({
                type: 2,
                title: false,
                maxmin: false,
                area: ['100%', '100%'],
                content: 'structure/editStructure.html?parentName='+parentName+'&thisName='+thisName+'&spanType='+spanType+'&spanId='+spanId
            });
        });
        layer.full(layerIndex);
    });

    //添加部门|职位 按钮
    $(document).on('click','.department-add-btn',function () {
        $(this).parent().find(".select-type-div").fadeToggle();
    });

    $(document).on('blur','.department-add-btn',function () {
        if ( ! inner($(this)) ) {
            //如果鼠标不在该div里面,则收起来该div
            $(this).parent().find(".select-type-div").fadeOut();
        }
    });

    // 4 删除按钮由鼠标滑动出现事件
    $(document).on('mouseenter','.node-box',function () {
        //在这里加入.stop()以防止动画效果不能及时停止
        $(this).find('.layui-icon-close').stop().fadeIn();
    });
    $(document).on('mouseleave','.node-box',function () {
        $(this).find('.layui-icon-close').stop().fadeOut();
    });

    // 5 删除节点
    $(document).on('click',".node-box .layui-icon-close",function () {
        let this_i = $(this);
        let span_type = $(this).next().attr("data-span-type");
        let span_id = $(this).next().attr("data-span-id");
        // console.log(span_type,span_id)
        swal({
                title: "确认删除该节点?",
                text: "该节点及其以下的节点都会被删除",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Yes, delete it!",
                closeOnConfirm: false
            },
            function(){
               $.ajax({
                   type : 'delete',
                   url : "/" + span_type +"?id=" +span_id,
                   contentType : 'application/json;charset=UTF-8',
                   dataType : 'json',
                   success:function () {
                       this_i.parent().parent().remove();
                       swal("Deleted!", "", "success");
                       showStructure();
                   },
               })
        });
    });

    // 6 iframe宽度自适应
    $(window).resize(function () {
        showAllLine();
        let iframe = $(".layui-layer-iframe");
        iframe.css("width" , Math.round(iframe.width() / $(window).width() * 100 ) +"%");
    });

    // 7 节点的向下伸缩展开事件
    $(document).on('click','.ul-toggle',function () {
        if($(this).hasClass('layui-icon-triangle-d')){
            if($(this).parent().siblings('ul')){
                $(this).removeClass('layui-icon-triangle-d').addClass('layui-icon-triangle-r');
                $(this).parent().siblings('ul').slideUp(300,function () {
                    showAllLine();
                })
            }
        } else if ($(this).hasClass('layui-icon-triangle-r')) {
            $(this).removeClass('layui-icon-triangle-r').addClass('layui-icon-triangle-d');
            $(this).parent().siblings('ul').slideDown(300,function () {
                showAllLine();
            });
        }
    });
    showStructure();

    // 8 关闭添加部门或者添加职位的弹窗
    $(document).on("click",".select-type-div .layui-icon-close",function () {
        $(this).closest(".select-type-div").fadeOut("fast")
    })

});

function showStructure() {
    // 8 页面铺数据
    $(".node-li ul").remove();
    $.get("/company",{},function (data) {
        let companyUl=$("<ul></ul>");
        let data_type;
        // 8-1 分公司数据
        $(".node-add-btn[data-type='company']").each(function () {
            data_type = $(this).attr("data-type");
            if(data_type === "company") {
                let thisBtn  = $(this);
                $(data.content).each(function (index,content) {
                    companyUl.append(
                        '<li>'
                        +'<div class="node-box">'
                        +'   <i class="layui-icon layui-icon-close" title="删除"></i>'
                        +'   <span class="node-name" data-span-type="company" data-span-id="'+content["companyId"]+'">'+ content.companyName+'</span>'
                        +'   <i class="layui-icon layui-icon-triangle-d ul-toggle" title="向下伸缩"></i>'
                        +'   </div>'
                        +'   <br>'
                        +'   <div class="node-add-box">'
                        +'   <div class="node-add">'
                        +'   <button class="node-add-btn add-element" data-type="department" data-type-name="部门" data-parent-name="'+content.companyName +'" data-parent-id="'+content["companyId"]+'" data-parent-key="companyBean.companyId" >'
                        +'   <span class="glyphicon glyphicon-plus"></span>'
                        +'   </button>'
                        +'   </div>'
                        +'   </div>'
                        +'   </li>'
                    );
                    thisBtn.closest(".node-add-box").after(companyUl);
                });
                // showAllLine();
            }
        });

        // 8-2 部门数据
        $(".node-add-btn[data-type='department']").each(function () {
            let thisBtn2 = $(this);
            let data_parent_id = parseInt($(this).attr("data-parent-id"));
            $(data.content).each(function (index,content){
                if(data_parent_id === content["companyId"]) {
                    let departmentUl=$("<ul></ul>");
                    $(content["departmentBeans"]).each(function (index,department) {
                        let idTemp =  Math.ceil(Math.random() * 1000);
                        departmentUl.append(
                            '<li>'
                            +'<div class="node-box">'
                            +'   <i class="layui-icon layui-icon-close" title="删除"></i>'
                            +'   <span class="node-name" data-span-type="department" data-span-id="'+department["departmentId"]+'">'+ department.departmentName+'</span>'
                            +'   <i class="layui-icon layui-icon-triangle-d ul-toggle" title="向下伸缩"></i>'
                            +'   </div>'
                            +'   <br>'
                            +'   <div class="node-add-box" id="'+idTemp+'">'
                            +'   <div class="node-add">'
                            +'   <button class="node-add-btn department-add-btn"  data-type="department" data-type-name="部门" data-parent-id="'+department["departmentId"]+'">'
                            +'   <span class="glyphicon glyphicon-plus"></span>'
                            +'   </button>'
                            +'   <div class="select-type-div">'
                            +'   <div class="arrow">'
                            +'   </div>'
                            +'   <div class="select-type">'
                            +'   <i class="layui-icon layui-icon-close" title="关闭"></i>'
                            +'   <div class="layui-row">'
                            +'   <div class="layui-col-xs6 add-element"  data-type="department" data-type-name="部门" data-parent-name="'+department.departmentName +'" data-parent-id="'+department["departmentId"]+'" data-parent-key="parentDepartmentBean.departmentId">'
                            +'   <i class="layui-icon layui-icon-home"></i>'
                            +'   <span>添加部门</span>'
                            +'   </div>'
                            +'   <div class="layui-col-xs6 add-element" data-type="job" data-type-name="职位" data-parent-name="'+department.departmentName +'" data-parent-id="'+department["departmentId"]+'" data-parent-key="departmentBean.departmentId">'
                            +'   <i class="layui-icon layui-icon-user" ></i>'
                            +'   <span>添加职位</span>'
                            +'   </div>'
                            +'   </div>'
                            +'   </div>'
                            +'   </div>'
                            +'   </div>'
                            +'   </div>'
                            +'   </li>'
                        );
                        thisBtn2.closest(".node-add-box").after(departmentUl);
                        $("#" + idTemp).after(showData4Dept(  department) ) ;
                    })
                    // showAllLine();
                }
            })
        });

        //  8-3 铺职位数据
        // $(".node-add-btn[data-type='job']").each(function (index,btn) {
        //     let thisBtn3 = $(this);
        //     let data_parent_id = parseInt($(this).attr("data-parent-id"));
        //     $(data.content).each(function (index,content){
        //         $(content.departmentBeans).each(function (index,department) {
        //             if(data_parent_id === department.departmentId) {
        //                 let jobUl=$("<ul></ul>");
        //                 $(department.jobBeans).each(function (index,job) {
        //                     jobUl.append(
        //                         '<li>'
        //                         +'<div class="node-box">'
        //                         +'   <i class="layui-icon layui-icon-close" title="删除"></i>'
        //                         +'   <span class="node-name" data-span-type="job" data-span-id="'+job["jobId"]+'">'+ job.jobName+'</span>'
        //                         +'   </div>'
        //                         +'   <br>'
        //                         +'   </div>'
        //                         +'   </li>'
        //                     );
        //                     thisBtn3.closest(".node-add-box").after(jobUl);
        //                 })
        //
        //             }
        //         })
        //
        //     })
        // });

        // 8-3 铺子部门和职位数据
        // $(".select-type").each(function (index,select) {
        //     let thisBtn3 = $(this);
        //     //如果该按钮下面的数据类型是部门的话 展示出该部门的id
        //     if(thisBtn3.find(".add-element [data-type='department']")) {
        //         var data_parent_id = thisBtn3.find(".add-element").attr("data-parent-id")
        //         // 获取到上级部门的id,如果数据库里的department_parent_id和data-parent-id一致 则开始铺数据
        //         $(data.content).each(function (index,content) {
        //             $(content.departmentBeans).each(function (index,department) {
        //                 if(data_parent_id == department["departmentId"]) {
        //                     let DepUl = $("<ul></ul>");
        //                     // 下级部门
        //                     $(department.nextDepartmentBeans).each(function (index,nextDepartment) {
        //                         DepUl.append(
        //                             '<li>'
        //                             +'<div class="node-box">'
        //                             +'   <i class="layui-icon layui-icon-close" title="删除"></i>'
        //                             +'   <span class="node-name" data-span-type="job" data-span-id="'+nextDepartment["departmentId"]+'">'+ nextDepartment.departmentName+'</span>'
        //                             +'   <i class="layui-icon layui-icon-triangle-d ul-toggle" title="向下伸缩"></i>'
        //                             +'   </div>'
        //                             +'   <br>'
        //                             +'   <div class="node-add-box">'
        //                             +'   <div class="node-add">'
        //                             +'   <button class="node-add-btn department-add-btn"  data-type="department" data-type-name="部门" data-parent-id="'+department["departmentId"]+'">'
        //                             +'   <span class="glyphicon glyphicon-plus"></span>'
        //                             +'   </button>'
        //                             +'   <div class="select-type-div">'
        //                             +'   <div class="arrow">'
        //                             +'   </div>'
        //                             +'   <div class="select-type">'
        //                             +'   <i class="layui-icon layui-icon-close" title="关闭"></i>'
        //                             +'   <div class="layui-row">'
        //                             +'   <div class="layui-col-xs6 add-element"  data-type="department" data-type-name="部门" data-parent-name="'+nextDepartment.departmentName +'" data-parent-id="'+nextDepartment["departmentId"]+'" data-parent-key="parentDepartmentBean.departmentId">'
        //                             +'   <i class="layui-icon layui-icon-home"></i>'
        //                             +'   <span>添加部门</span>'
        //                             +'   </div>'
        //                             +'   <div class="layui-col-xs6 add-element" data-type="job" data-type-name="职位" data-parent-name="'+nextDepartment.departmentName +'" data-parent-id="'+nextDepartment["departmentId"]+'" data-parent-key="departmentBean.departmentId">'
        //                             +'   <i class="layui-icon layui-icon-user" ></i>'
        //                             +'   <span>添加职位</span>'
        //                             +'   </div>'
        //                             +'   </div>'
        //                             +'   </div>'
        //                             +'   </div>'
        //                             +'   </div>'
        //                             +'   </div>'
        //                             +'   </li>'
        //                         );
        //                     })
        //
        //                     //职位
        //                     $(department.jobBeans).each(function (index,job) {
        //                         DepUl.append(
        //                             '<li>'
        //                             +'<div class="node-box">'
        //                             +'   <i class="layui-icon layui-icon-close" title="删除"></i>'
        //                             +'   <span class="node-name" data-span-type="job" data-span-id="'+job["jobId"]+'">'+ job.jobName+'</span>'
        //                             +'   </div>'
        //                             +'   <br>'
        //                             +'   </div>'
        //                             +'   </li>'
        //                         );
        //
        //                     })
        //
        //
        //                     thisBtn3.closest(".node-add-box").after(DepUl);
        //                 }
        //             })
        //         })
        //     }
        // });

        function showData4Dept( department) {
            let DepUl = "<ul>";
            //职位
            $(department["jobBeans"]).each(function (index,job) {
                DepUl += (
                    '<li>'
                    +'  <div class="node-box">'
                    +'     <i class="layui-icon layui-icon-close" title="删除"></i>'
                    +'     <span class="node-name" data-span-type="job" data-span-id="'+job["jobId"]+'">'+ job.jobName+'</span>'
                    +'     </div>'
                    +'     <br>'
                    +'  </div>'
                    +'</li>'
                );
            });
            // 下级部门
            $(department["nextDepartmentBeans"]).each(function (index,nextDepartment) {
                DepUl += (
                    '<li>'
                    +'<div class="node-box">'
                    +'   <i class="layui-icon layui-icon-close" title="删除"></i>'
                    +'   <span class="node-name" data-span-type="department" data-span-id="'+nextDepartment["departmentId"]+'">'+ nextDepartment.departmentName+'</span>'
                    +'   <i class="layui-icon layui-icon-triangle-d ul-toggle" title="向下伸缩"></i>'
                    +'   </div>'
                    +'   <br>'
                    +'   <div class="node-add-box" id="dept'+department["departmentId"]+'">'
                    +'   <div class="node-add">'
                    +'   <button class="node-add-btn department-add-btn"  data-type="department" data-type-name="部门" data-parent-id="'+department["departmentId"]+'">'
                    +'   <span class="glyphicon glyphicon-plus"></span>'
                    +'   </button>'
                    +'   <div class="select-type-div">'
                    +'   <div class="arrow">'
                    +'   </div>'
                    +'   <div class="select-type">'
                    +'   <i class="layui-icon layui-icon-close" title="关闭"></i>'
                    +'   <div class="layui-row">'
                    +'   <div class="layui-col-xs6 add-element"  data-type="department" data-type-name="部门" data-parent-name="'+nextDepartment.departmentName +'" data-parent-id="'+nextDepartment["departmentId"]+'" data-parent-key="parentDepartmentBean.departmentId">'
                    +'   <i class="layui-icon layui-icon-home"></i>'
                    +'   <span>添加部门</span>'
                    +'   </div>'
                    +'   <div class="layui-col-xs6 add-element" data-type="job" data-type-name="职位" data-parent-name="'+nextDepartment.departmentName +'" data-parent-id="'+nextDepartment["departmentId"]+'" data-parent-key="departmentBean.departmentId">'
                    +'   <i class="layui-icon layui-icon-user" ></i>'
                    +'   <span>添加职位</span>'
                    +'   </div>'
                    +'   </div>'
                    +'   </div>'
                    +'   </div>'
                    +'   </div>'
                    +'   </div>'
                    + ( showData4Dept(nextDepartment).toString() )
                    +'   </li>'
                );


            });

            return DepUl + "</ul>";

        }

        showAllLine();
    },"json")
}
