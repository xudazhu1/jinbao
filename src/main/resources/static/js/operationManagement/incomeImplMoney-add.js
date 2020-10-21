
var incomeId = getParamForUrl("id");
var incomeTemp;

$(function () {

    $.get("/income_impl_money/info_by_incomeId", {"incomeId": incomeId }, function (data) {
        incomeTemp = data;
        $(".incomeNum").val(incomeTemp["incomeNum"]);
        $(".incomeMoney").val(data.incomeMoney);
        $(".incomeId").val(incomeId);

        var impls = $(".impls");
        impls.empty();
        $(incomeTemp["projectBean"]["implementBeans"]).each(function (index , implementBean) {
            impls.append(
                "<div class=\"input-group\">\n" +
                "    <span class=\"input-group-addon\" >"+implementBean["departmentBean"]["departmentName"]+"</span>\n" +
                "    <input type=\"text\" class=\"form-control incomeImplMoney \" placeholder=\"请输入分配金额\" data-impl-id='"+implementBean.implementId+"' name='incomeImplMoneyBeans["+index+"].incomeImplMoney' value='0' aria-describedby=\"basic-addon1\">\n" +
                "    <input type='hidden' class='incomeImplMoneyId' name='incomeImplMoneyBeans["+index+"].incomeImplMoneyId'  data-impl-id='"+implementBean.implementId+"' >" +
                "    <input type='hidden' class='ImplementId' name='incomeImplMoneyBeans["+index+"].implementBean.implementId' value='"+implementBean.implementId+"'>" +
                "</div>");
        });

        //遍历分配金额 铺到对应已经铺的实施上
        $( incomeTemp["incomeImplMoneyBeans"] ).each( function ( index , incomeImplMoneyBean ) {
            //铺 分配bean id
            $(".incomeImplMoneyId[data-impl-id='"+incomeImplMoneyBean.implementBean.implementId+"']").val( incomeImplMoneyBean["incomeImplMoneyId"] )
            //铺 分配bean money
            $(".incomeImplMoney[data-impl-id='"+incomeImplMoneyBean.implementBean.implementId+"']").val( incomeImplMoneyBean["incomeImplMoney"] )

        });

    } , "json").fail(function () {

    } , "json");


    $(".submit-update").click(function () {
        var dataTemp = $("#data1").serialize();
        $.ajax({
            type: "put",
            async: true,
            url: "/table_utils",
            processData: true,
            data: dataTemp,
            dataType: "json",
            success: function (data) {
                if ( data ) {
                    alert("操作成功");
                    // $("#submit").remove();
                    try {
                        window.parent.flush();
                    } catch (e) {
                    }
                    window.parent.layer.close(parent.layerIndex);
                } else {
                    alert("操作失败");
                }
            } ,
            error: function (res) {
                alert("操作失败 请刷新重试 " );
                console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "操作失败 请刷新重试");
            }
        });
    });


});