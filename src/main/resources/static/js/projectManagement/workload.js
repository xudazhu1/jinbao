function getPageDate(pageNum,pageSize) {
    var formTemp = $("#searchForm");
    var keys = [];
    //拼接任意条件的name(key)值
    formTemp.find("input[name]").each(function () {
        var nameTemp = $(this).prop("name");
        if ( nameTemp.indexOf("$D.") === -1 && $(this).closest("th").css("display") !== "none" ) {
            keys.push(nameTemp);
        }
    });
    $(".multiple-key").val(keys.toString().replace(/,/g , "$"));
    var params = $.param({"pageNum": pageNum, "pageSize": pageSize,
        "multiple_key": keys.toString().replace(/,/g , "$") ,
        "multiple_value": $(".multiple-value").val()})+ '&' + formTemp.serialize();
    $.get("/work_load/s", params, function (data) {
        var items = $('#items');
        items.empty();
        $(data.content).each(function (index, workload) {
            console.log(workload);
            console.log(workload["supervisorUserBean"]["userName"]);
            var supervisorUserBean = typeof workload["supervisorUserBean"]["userName"] === 'undefined' ? "" : workload["supervisorUserBean"]["userName"]
            var captainUserBean = typeof workload["captainUserBean"]["userName"] === 'undefined' ? "" : workload["captainUserBean"]["userName"]
            items.append(
                '<tr>'
                + '<td>' + workload["implementBean"]["projectBean"]["projectNum"] + '</td>'
                + '<td>' + workload["implementBean"]["projectBean"]["projectName"] + '</td>'
                + '<td>' + workload["implementBean"]["implementDepartmentBean"]["implementDepartmentName"] + '</td>'
                + '<td>' + workload.workLoadDate + '</td>'
                + '<td>' + workload.workLoadDuration+ '</td>'
                + '<td>' + workload["professionBean"]["professionName"]+ '</td>'
                + '<td>' + workload.workLoadWorkLoad + '</td>'
                + '<td>' + workload["staffUserBean"]["userName"]+ '</td>'
                + '<td>' + workload["workLoadPriceStaff"]+ '</td>'
                + '<td>' + workload["workLoadAmountStaff"] + '</td>'
                + '<td>' + supervisorUserBean + '</td>'
                + '<td>' + workload["workLoadPriceManage"] + '</td>'
                + '<td>' + workload["workLoadAmountManage"] + '</td>'
                + '<td>' + captainUserBean + '</td>'
                + '<td>' + workload["workLoadPriceCaptain"] + '</td>'
                + '<td>' + workload["workLoadAmountCaptain"] + '</td>'
                + '<td>'
                    + '<button type="button" class="btn btn-warning btn-xs edit-btn " data-height="700px" data-width="850px" data-href="quote/quote-add.html?id=' + workload["workLoadId"] + '">编辑</button>'
                    + '<button type="button" class="btn btn-danger btn-xs delete-btn" data-id="'+workload["workLoadId"]+'">删除</button>'
                + '</td>'
                + '</tr>'
            )
        });
        showPageButtuns(
            {"pageNum":data.number + 1 , "countPage": data["totalPages"] ,"pageSize" : data["size"] ,  "countNum": data["totalElements"] } ,
            $("#page") ,
            getPageDate);
    },"json").fail(function (res) {
        // sweetAlert('数据提交失败 请刷新重试 ');
        console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
    });}

$(function () {
    // 1 点击弹出添加报价的子页面

    getPageDate(1 , 10);

    $(document).on("click" , ".btn-confirm" , function () {
        getPageDate(pageDataA["pageNum"] , pageDataA["pageSize"]);
    });
    var label_text;
    var th_text;
    $(".th-checkbox input[type='checkbox']:not(:checked)").each(function () {
        label_text = $(this).next().text();
        $("#operating_record_table th").each(function (index, th) {
            // noinspection JSValidateTypes
            th_text = $(th).children('span').text();
            if (label_text === th_text) {
                $("#operating_record_table tbody tr").each(function () {
                    // noinspection JSValidateTypes
                    $(this).children().eq(index).text(); //  ???????
                    // noinspection JSValidateTypes
                    $(this).children().eq(index).hide();
                });
                $(this).hide();
            }
        })
    })



    //内页padding 样式
    setTimeout(function () {
        $("iframe").each(function (index , iFrameTemp) {
            iFrameTemp.contentWindow.document.getElementsByTagName("html")[0].style.padding = "0";
        });
    } , 100 );
    $(document).on("click" , ".layui-tab-title li" , function () {
        $("iframe").each(function (index , iFrameTemp) {
            iFrameTemp.contentWindow.document.getElementsByTagName("html")[0].style.padding = "0";
        });
    });
});
