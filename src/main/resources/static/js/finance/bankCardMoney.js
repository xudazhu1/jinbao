$(function () {
    // 年选择器
    laydate.render({
        elem: '#year'
        , range: true
        , done: function (value) {
            let dateL = value.substring(0, 10);
            let dateR = value.substring(13, value.length);
            console.log(dateL + "~" + dateR)
        }
    });
    var items = $('#items');
    var url = "/bank_card";
    var data = "";
    $.ajax({
        "url": url,
        "data": data,
        "type": "GET",
        "dataType": "json",
        "success": function (json) {
            for (var key in json) {
                items.append('<tr>'
                    + '<td>' + key+ '</td>'//银行卡号
                    + '<td>' +parseFloat(json[key][0]).toFixed(2) + '</td>'//初期余额
                    + '<td>' +parseFloat(json[key][1]).toFixed(2)+ '</td>'//收入
                    + '<td>' +parseFloat(json[key][2]).toFixed(2) + '</td>'//支出
                    + '<td>' +parseFloat(json[key][6]).toFixed(2) + '</td>'//转账
                    + '<td>' +parseFloat(json[key][5]).toFixed(2) + '</td>'//借款
                    + '<td>' +parseFloat(json[key][7]).toFixed(2) + '</td>'//期末
                    + '</tr>')
            }
            fixedHead($(".bankCard"));
        }
    });
    // 窗口大小发生变化时,固定表头的代码重新执行
    $(window).resize(function () {
        fixedHead($(".bankCard"));
    });

});