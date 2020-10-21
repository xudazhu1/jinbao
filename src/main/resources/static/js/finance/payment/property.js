$(function () {
    //处理当前年月
    let date = new Date();
    let year = date.getFullYear();
    $("#year").val(year);
    let month = date.getMonth() + 1 + "";
    if ( month.length === 1 ) {
        month = "0" + month;
    }
    $("[name='month'][value='" + month + "']").click();
    getPageDate(1, 10);
    $(document).on("click", ".comeback-btn", function () {
        window.history.back();
    });
    // 年选择器
    laydate.render({
        elem: '#year'
        , type: 'year'
        , done: function (value) {
            let monthVal = $(".radio-div input[type='radio']:checked").val();
            if (monthVal !== "") {
                dateA = value + "-" + monthVal;
                getPageDate(pageDataA["pageNum"] , pageDataA["pageSize"] );
            }
        }
    });

    $(document).on("click", ".radio-div input", function () {
        let monthVal = $(this).val();
        let yearVal = $("#year").val();
        if (yearVal !== "") {
            dateA = yearVal + "-" + monthVal;
            getPageDate(pageDataA["pageNum"] , pageDataA["pageSize"] );
        }
    });
});

let dateA = "";

function getPageDate(pageNum, pageSize ) {
    // var formTemp = $("#searchForm");
    var items = $('#items');
    var url = "/payment/list_property";
    var data = {"dateA": dateA , "pageNum" :pageNum , "pageSize" : pageSize , sortField : "disburseId" };
    $.ajax({
        "url": url,
        "data": data,
        "type": "GET",
        "dataType": "json",
        "success": function (json) {
            items.empty();
            $(json.content ).each(function (index, payment) {
                let propertyBean1 = payment["propertyBean1"];
                items.append('<tr>'
                    + '<td>' + payment.disburseNum + '</td>'//支出编号
                    + '<td >' + ($.isEmptyObject(propertyBean1) ? "" : propertyBean1.propertyName) + '</td>'//资产名称
                    + '<td >' + ($.isEmptyObject(propertyBean1) ? "" : propertyBean1.propertyType) + '</td>'//物品型号
                    + '<td >' + ($.isEmptyObject(propertyBean1) ? "" : propertyBean1.propertyBuyTime) + '</td>'//购买日期
                    + '<td >' + ($.isEmptyObject(propertyBean1) ? "" : propertyBean1.propertyDepreciationMethod) + '</td>'//折旧方式
                    + '<td >' + payment["disbursePaymentAmount"] + '</td>'//原值
                    + '<td >' + ($.isEmptyObject(propertyBean1) ? "" : propertyBean1.propertyDeadline) + '</td>'//折旧期限
                    + '<td >' + ($.isEmptyObject(propertyBean1) ? "" : (propertyBean1.propertyResidual * 100)+"%") + '</td>'//残值率
                    + '<td >' + ($.isEmptyObject(propertyBean1) ? "" : propertyBean1["propertyAccumulatedDepreciation"]) + '</td>'//累计折旧
                    + '<td >' + ($.isEmptyObject(propertyBean1) ? "" : (propertyBean1["propertyInstantDepreciation"]).toFixed(2)) + '</td>'//本月折旧
                    //+ '<td >' + ($.isEmptyObject(propertyBean1) ? "" : propertyBean1["propertyResidueDepreciation"]) + '</td>' 剩余折旧
                    + '<td >' + ($.isEmptyObject(propertyBean1) ? "" : (propertyBean1["propertyNetValue"]).toFixed(2)) + '</td>'//净值
                    + '<td >' + ($.isEmptyObject(propertyBean1) ? "" : propertyBean1["propertyInputTime"]) + '</td>'//录入日期
                    + '<td ><button type="button" class="btn btn-info btn-xs edit-btn" title="资产录入" data-title="资产录入" data-width="100%" data-height="100%" data-href="property-add.html?id=' + payment["disburseId"] + '">资产录入</button></td>'//操作框
                    + '</tr>')
            });
            showPageButtuns(
                {"pageNum":json.number + 1 , "countPage": json["totalPages"] ,"pageSize" : json["size"] ,  "countNum": json["totalElements"] } ,
                $("#page") ,
                getPageDate);

        }
    })
}