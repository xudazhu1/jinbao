$(function () {
    var items = $('#items');
    var url = "/statistics";
    var data = "";
    $.ajax({
        "url": url,
        "data": data,
        "type": "GET",
        "dataType": "json",
        "success": function (json) {
            for (var key in json) {
                let rowSpan = Object.keys(json[key]).length;
                let key0 = Object.keys(json[key])[0];
                let values0 = json[key][key0];
                items.append('<tr>'
                    + '<td rowspan="'+rowSpan+'" >' + key + '</td>'//年月
                    + '<td  >' + key0 + '</td>'//收益單位
                    + '<td  >' + parseFloat(values0[0]).toFixed(2) + '</td>'//实际回款
                    + '<td  >' + parseFloat(values0[1]).toFixed(2) + '</td>'//项目费用
                    + '<td  >' + parseFloat(values0[2]).toFixed(2) + '</td>'//部门费用
                    + '<td  >' + parseFloat(values0[3]).toFixed(2) + '</td>'//公司费用
                    + '<td  >' + parseFloat(values0[4]).toFixed(2) + '</td>'//利润
                    + '<td rowspan="'+rowSpan+'"  ><button type="button" class="btn btn-primary btn-xs edit-btn " style="margin-right: 5px" title="详情" data-title="汇总详情" data-href="../finance/financialSummary/financialSummary-detail.html?date='+key+'">详情</button></td>'//利润
                    + '</tr>');
                let index = 0;
                for (const key1 in json[key]){
                    let values = json[key][key1];
                    if ( index > 0 ) {
                        items.append('<tr>'
                            + '<td  >' + key1 + '</td>'//收益單位
                            + '<td  >' + parseFloat(values[0]).toFixed(2) + '</td>'//实际回款
                            + '<td  >' + parseFloat(values[1]).toFixed(2) + '</td>'//项目费用
                            + '<td  >' + parseFloat(values[2]).toFixed(2) + '</td>'//部门费用
                            + '<td  >' + parseFloat(values[3]).toFixed(2) + '</td>'//公司费用
                            + '<td  >' + parseFloat(values[4]).toFixed(2) + '</td>'//利润
                            + '</tr>');
                    }
                    index++;
                }
            }
            //首行固定
            fixedHead($(".financialSummary"));
        }
    });

    // 窗口大小发生变化时,固定表头的代码重新执行
    $(window).resize(function () {
        fixedHead($(".financialSummary"));
    });
});