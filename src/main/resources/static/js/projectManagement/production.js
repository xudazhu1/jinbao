$(function () {

    getPageDate(1,10);


    $(document).on("click",".search-btn",function () {
        getPageDate(1,10);
    });
});

function getPageDate(pageNum,pageSize){
    var formTemp = $("#searchForm");
    var inputDom = formTemp.find("[name='multiple_key']");
    try {
        let value = [];
        $( inputDom.val().split('$') ).each( function (i,name) {
            if ( name.indexOf("implementBean.") !== 0 ) {
                value.push("implementBean."+name);
            } else {
                value.push( name);
            }

        });
        inputDom.val( value.toString().replace(/,/g , "$") );
    } catch (e) {

    }
    var params = $.param({"pageNum": pageNum, "pageSize": pageSize ,"sort": true})+ '&' + formTemp.serialize();
    $.get("/implement/impl_production",params,function (data) {
        $("#items").empty();
        $(data["content"]).each(function (i,ele) {
            $("#items").append('<tr>\n' +
                '                    <td>'+ele["项目编号"]+'</td>\n' +
                '                    <td>'+ele["项目名称"]+'</td>\n' +
                '                    <td>'+ele["经营类型"]+'</td>\n' +
                '                    <td>'+ele["经营部负责人"]+'</td>\n' +
                '                    <td>'+ele["实施部"]+'</td>\n' +
                '                    <td>'+ele["实施部负责人"]+'</td>\n' +
                '                    <td>'+ele["项目花销"].toFixed(2)+'</td>\n' + /*开始成本*/
                '                    <td>'+ele["税费"].toFixed(2)+'</td>\n' +
                '                    <td>'+ele["管理费"].toFixed(2)+'</td>\n' +
                '                    <td>'+ele["人员成本"].toFixed(2)+'</td>\n' +
                '                    <td>'+ele["技术提成"].toFixed(2)+'</td>\n' +
                '                    <td>'+ele["年总奖金"].toFixed(2)+'</td>\n' +
                '                    <td>'+ele["设备使用"].toFixed(2)+'</td>\n' +
                '                    <td>'+ele["班组费"].toFixed(2)+'</td>\n' +
                '                    <td>'+ele["其他费"].toFixed(2)+'</td>\n' +
                '                    <td>' +
                '                       <button class="btn btn-success btn-xs edit-btn " type="button" data-href="production/production-add.html?projectId='+ele["项目id"]+'&implementId='+ele["实施id"]+'">编辑</button>' +
                '                   </td>')
        });

        showPageButtuns({
            "pageNum": data.number + 1,
            "countPage": data["totalPages"],
            "pageSize": data["size"],
            "countNum": data["totalElements"]
        }, $("#page"), getPageDate)
    },"json")
}


