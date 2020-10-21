$(function () {
    getPageDate(1, 10);
});

function getPageDate(pageNum, pageSize) {
    var formTemp = $("#items");
    var params = $.param({"pageNum": pageNum, "pageSize": pageSize}) + '&' + formTemp.serialize();
    var url = "/management/estimated_income";
    var data = params;
    $.ajax({
        "url": url,
        "data": data,
        "type": "GET",
        "dataType": "json",
        "success": function (json) {
            formTemp.empty();
            $(json.content).each(function (index, en) {
                console.log(en["分配金额"][0]);
                //计算项目进度
                var schedule;
                var sumMoney = 0;
                //总共的合同金额
                var allocation = 0
                var rowSpan = en["分配部门"].length > 1 ? en["分配部门"].length : 1
                for (let i = 0; i < rowSpan; i++) {
                    var a = 0;
                    var b = 0
                    if (typeof en["分配金额"][i] === "number" && typeof en["部门进度"][i] === "number") {
                        a = en["分配金额"][i] * en["部门进度"][i]

                    }
                    if (typeof en["分配金额"][i] === "number") {
                        b = en["分配金额"][i]
                    }
                    sumMoney = a + sumMoney;
                    allocation = b + allocation;
                }
                if (allocation != 0) {
                    schedule = sumMoney / allocation
                } else {
                    schedule = 0;
                }

                // console.log(allocation+"pppp");

                //计算预估收入
                var discreet_value;
                if (en["项目类型"] === "政府项目") {
                    console.log("进来了");
                    if (en["审计金额"] != 0) {
                        discreet_value = en["审计金额"]
                    } else if (en["财评金额"] != 0) {
                        discreet_value = en["财评金额"] * 0.8 * schedule
                    } else if (en["预算金额"] != 0) {
                        discreet_value = en["预算金额"] * 0.7
                    }
                    else if (allocation != 0) {
                        discreet_value = allocation * 0.7 * schedule
                    }
                    else if (en["报价金额"] != 0 != 0) {
                        discreet_value = en["报价金额"] * 0.7 * schedule
                    }
                    else {
                        discreet_value = 0;
                    }
                } else {
                    if (en["预算金额"] != 0) {
                        discreet_value = en["预算金额"]
                    }
                    else if (allocation != 0) {
                        discreet_value = allocation * schedule
                    }
                    else if (en["报价金额"] != 0 != 0) {
                        discreet_value = en["报价金额"] * schedule
                    }else {
                        discreet_value =0
                    }
                }

                //计算待回款金额
                var stay_back_money = allocation - en["已回款金额"]
                //左边表格
                leftTable =
                    '<tr>'
                    + '<td rowspan="' + rowSpan + '">' + en["项目编号"] + '</td>'//项目编号
                    + '<td rowspan="' + rowSpan + '">' + en["项目名称"] + '</td>'//项目编号
                    + '<td rowspan="' + rowSpan + '">' + en["审计金额"] + '</td>'//项目编号
                    + '<td rowspan="' + rowSpan + '">' + en["财评金额"] + '</td>'//项目编号
                    + '<td rowspan="' + rowSpan + '">' + en["预算金额"] + '</td>'//项目编号
                    + '<td >' + en["分配金额"][0] + '</td>'//项目编号
                    + '<td >' + en["分配部门"][0] + '</td>'//项目编号
                    + '<td >' + en["部门进度"][0] + '</td>'//项目编号
                    + '<td rowspan="' + rowSpan + '">' + en["报价金额"] + '</td>'//项目编号
                    + '<td rowspan="' + rowSpan + '">' + schedule + '</td>'//进度
                    + '<td rowspan="' + rowSpan + '">' + discreet_value + '</td>'//项目编号
                    + '<td rowspan="' + rowSpan + '">' + en["已回款金额"] + '</td>'//项目编号
                    + '<td rowspan="' + rowSpan + '">' + stay_back_money + '</td>'//项目编号
                    // + '<td rowspan="' + rowSpan + '">' + en["项目类型"][0] + '</td>'//项目编号

                    + '</tr>';
                for (let i = 1; i < rowSpan; i++) {
                    leftTable +=
                        "<tr>" +
                        "<td>" + en["分配金额"][i] + "</td>" +
                        "<td>" + en["分配部门"][i] + "</td>" +
                        "<td>" + en["部门进度"][i] + "</td>" +
                        "</tr>";
                }
                formTemp.append(leftTable);
            });
            showPageButtuns(
                {
                    "pageNum": json.number + 1,
                    "countPage": json["totalPages"],
                    "pageSize": json["size"],
                    "countNum": json["totalElements"]
                },
                $("#page"),
                getPageDate);
        }
    });

    $(document).on("click", ".comeback-btn", function () {
        window.history.back();
    });
}