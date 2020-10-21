$(function () {
    data();
    $(".search-btn").click(function () {
        data($(".form-control").val())
        data($(".form-control").val())
    })
});

function data(value) {
    var items = $('#items');
    var url = "/borrow_money";
    var data = {"name":value};
    $.ajax({
        "url": url,
        "data": data,
        "type": "GET",
        "dataType": "json",
        "success": function (json) {
            for (var key in json ){
                for ( var date in json[key]){
                    items.append('<tr>'
                        + '<td>' + key+ '</td>'//支出编号
                        + '<td>' + date+ '</td>'//支出编号
                        + '<td>' + parseFloat(json[key][date][0]).toFixed(2) + '</td>'//支出编号
                        + '<td>' + parseFloat(json[key][date][1]).toFixed(2)+ '</td>'//支出编号
                        + '<td>' + parseFloat(json[key][date][2]).toFixed(2) + '</td>'//支出编号
                        + '<td>' +  parseFloat(json[key][date][3]).toFixed(2)+ '</td>'//支出编号
                        // + '<td>' + json[key][date][4]+ '</td>'//支出编号
                        + '</tr>')
                }
            }
        }
    })
}