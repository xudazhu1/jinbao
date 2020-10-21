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
        // noinspection JSValidateTypes
        $(beginDom).closest("li").children("ul").children("li").children(".node-box").each(function (index , endDom) {
            showLineInDom($(beginDom) , $(endDom));
        })
    });
}
// noinspection ES6ConvertVarToLetConst
var layerIndex;
let flowId = getParamForUrl("id");
$(function () {


    //进来铺点数据
    $.get("/flow" , {id : flowId} , function (data) {
        let flow = data["content"][0];
        for (const key in flow ) {
            // 3-1 铺数据
            if ( flow.hasOwnProperty(key)) {
                $('[name="'+key+'"]').val(flow[key]);
            }
        }
        let companyIds = {};
        $(flow["companyBeans"]).each(function (index,companyTemp) {
            companyIds[companyTemp["companyId"]] = "true";
        });

        $.get("/company"  , function (data) {
            // console.log(data.content)
            let company = $("#company");
            company.empty();
            $(data.content).each(function (index,content) {

                company.append(
                    '<option '+(companyIds.hasOwnProperty(content["companyId"]) ? "selected='selected'" : "")+' value="'+content["companyId"]+'" data-company-id="'+content["companyId"]+'">'+content.companyName+'</option>'
                )
            });
            //加载模块
            layui.use(['jquery', 'formSelects'], function () {
                let formSelects = layui.formSelects;
                formSelects.value('select1');
            });




        });



    } , "json" );



    showAllLine();
    //2 点击加号,出现添加下级结构弹出层
    layui.use('layer', function () {
        layerIndex = layui.layer
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


    //添加/编辑按钮
    $(document).on('click','.add-sub-flow-btn',function () {
        let id = $(this).attr("data-id");
        layer.ready(function () {
            layerIndex = layer.open({
                type: 2,
                title: false,
                maxmin: false,
                area: ['100%', '100%'],
                content: "editSubFlow.html?flowId="+flowId+"&id=" + id
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

});