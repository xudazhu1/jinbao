$(function () {
    // 2 开启layui的form表单,可以实现复选框的功能
    //引用share.js

    // 3复选框全选
    //引用share.js

    // 4 tbody里面的复选框选择事件
    //引用share.js

    // 5单次删除
    //引用share.js

    // 6批量删除
    //引用share.js

    // 6 iframe宽度自适应
    $(window).resize(function () {
        let iframe = $(".layui-layer-iframe");
        iframe.css("width" , Math.round(iframe.width() / $(window).width() * 100 ) +"%");
    });

    // $(document).on('click','.add-element',function () {
    //     layer.ready(function () {
    //         layerIndex = layer.open({
    //             type: 2,
    //             title: '添加主流程' ,
    //             maxmin: true,
    //             area: ['600px', '450px'],
    //             content: "flow/addMainFlow.html"
    //         });
    //     });
    // });

    getAPageData();



});


function getAPageData(pageNum, pageSize) {
    // let formTemp = $("#searchForm");
    // let keys = [];
    // //拼接任意条件的name(key)值
    // formTemp.find("input[name]").each(function () {
    //     let nameTemp = $(this).prop("name");
    //     if ( nameTemp.indexOf("$D.") === -1 && $(this).closest("th").css("display") !== "none" ) {
    //         keys.push(nameTemp);
    //     }
    // });
    // $(".multiple-key").val(keys.toString().replace(/,/g , "$"));
    //
    // let params = $.param({ "pageNum": pageNum, "pageSize": pageSize ,
    //     "multiple_key": keys.toString().replace(/,/g , "$") ,
    //     "multiple_value": $(".multiple-value").val() }) + '&' + formTemp.serialize();
    $.get('/flow' , {"pageNum" : pageNum , "pageSize" : pageSize} , function (data) {
        let content = data.content;
        //铺表格数据
        $("#flows").empty();
        $(content).each(function (index,flow) {
            let companies = [];
            $(flow["companyBeans"]).each(function (index ,  company) {
                companies.push(company.companyName);
            });

            $("#flows").append(
                '<tr>\n' +
                '    <td>\n' +
                '        <input type="checkbox" title="" lay-skin="primary">\n' +
                '    </td>\n' +
                '    <td>'+(index + 1 )+'</td>\n' +
                '    <td>'+flow.flowName+'</td>\n' +
                '    <td>'+flow["userBean"].userName+'</td>\n' +
                '    <td>'+companies.toString()+'</td>\n' +
                '    <td>\n' +
                '        <button type="button" class="layui-btn layui-btn-xs layui-btn-warm edit-btn"\n' +
                '          data-href="flow/addFlow.html?id='+flow["flowId"]+'"      title="编辑">\n' +
                '            <i class="layui-icon layui-icon-edit"></i>\n' +
                '        </button>\n' +
                '        <button type="button" class="layui-btn layui-btn-xs layui-btn-danger delete-btn"\n' +
                '        data-id="'+flow["flowId"]+'"        title="删除">\n' +
                '            <i class="layui-icon layui-icon-delete"></i>\n' +
                '        </button>\n' +
                '    </td>\n' +
                '</tr>');
        });
        // //考虑表格列数是否有删减
        OpTh();

        showPageButtuns(
            {"pageNum":data.number + 1 , "countPage": data["totalPages"] ,"pageSize" : data["size"] ,  "countNum": data["totalElements"] } ,
            $("#page") ,
            getAPageData);
    })
}