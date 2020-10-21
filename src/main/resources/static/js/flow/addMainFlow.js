$(function () {
    $.get("/company"  , function (data) {
        // console.log(data.content)
        let company = $("#company");
        company.empty();
        $(data.content).each(function (index,content) {
            company.append(
            '<option value="'+content["companyId"]+'" data-company-id="'+content["companyId"]+'">'+content.companyName+'</option>'
            )
        });
        //加载模块
        layui.use(['jquery', 'formSelects'], function () {
            let formSelects = layui.formSelects;
            formSelects.value('select1');
        });

    });


    $(".submit").click(function(){
        $(".company-select .xm-select-this").each(function(index , select_div ){
            $(".company-select").append(
                "<input type='hidden' name='companyBeans["+index+"]companyId' value='"+$(select_div).attr("lay-value")+"' />");
        });

        $.post("/flow" , $("#inputForm").serialize() , function (data) {
            if ( data ) {
                // window.parent.layer.full(window.parent.layerIndex);
                const pfs = parent.frames;
                for (let i = 0; i < pfs.length; i++) {
                    if (pfs[i] === window) {
                        // console.log(pfs[i].frameElement);
                        // pfs[i].frameElement.style.height ="100%";
                        // pfs[i].frameElement.style.width ="100%";
                        $(pfs[i].frameElement).closest(".layui-layer-iframe").height("100%").width("100%").css("top" , 0).css("left" , 0);
                    }
                }

                window.location.href="addFlow.html?id=" + data["flowId"];
            } else {
                alert('数据提交失败 请刷新重试');
            }
        } , "json").fail(function (res) {
            alert('数据提交失败 请刷新重试');
            console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
        });
    });


});