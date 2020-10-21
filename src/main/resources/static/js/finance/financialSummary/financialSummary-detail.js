let date = getParamForUrl("date");
$(function () {
    $("#date").text(date);
    $.get("/statistics/month", {"date": date}, function (data) {
        console.log(data);
        // 本月利润
        $(".profit").text(data["利润"]["0"]);
        // 利润分配
        if(!$.isEmptyObject(data["利润分配"])){
            $(".profit-distribution").text(data["利润分配"]["0"]["1"]);
        }
        // 未分配例如
        $(".no-profit-distribution").text(data["未分配利润"]);
        // 1 收入
        let incomeTbody = $("#incomeTbody");
        incomeTbody.empty();
        for (let key in data) {
            if (key === "收入") {
                let rowSpan = data[key].length;
                if(rowSpan === 0){
                    rowSpan = 1;
                }
                let idTemp = Math.ceil(Math.random() * 10000);
                incomeTbody.append(
                    '<tr id="tempTr' + idTemp + '">'
                    + '<td rowspan="' + rowSpan + '">1</td>'
                    + '<td rowspan="' + rowSpan + '">收入</td>'
                    + '</tr>'
                );
                if($.isEmptyObject(data[key])){//判空
                    $("#tempTr" + idTemp).append(
                        '<td></td>'
                        +'<td></td>'
                        +'<td></td>'
                        +'<td></td>'
                        +'<td></td>'
                    );
                }else{
                    let monthMoney = 0;//月合计金额
                    // 铺收益单位和金额
                    for (let key2 in data[key]) {
                        // console.log(key2);
                        monthMoney += data[key][key2][1];
                        if (key2 === "0") {
                            $("#tempTr" + idTemp).append(
                                '<td>' + data[key][key2][0] + '</td>'
                                + '<td>' + data[key][key2][1].toFixed(2) + '</td>'
                            )
                        } else if (key2 > 0) {
                            incomeTbody.append(
                                '<tr>'
                                + '<td>' + data[key][key2][0] + '</td>'//收益单位
                                + '<td>' + data[key][key2][1].toFixed(2) + '</td>'//金额
                                + '</tr>'
                            )
                        }
                    }
                    //铺月合计金额
                    $("#tempTr" + idTemp).append(
                        '<td rowspan="' + rowSpan + '">' + monthMoney.toFixed(2) + '</td>'
                        + '<td rowspan="' + rowSpan + '"></td>'
                        + '<td rowspan="' + rowSpan + '"></td>'
                    );
                }

            }
        }

        // 2 公司费用
        let companyTbody = $("#companyTbody");
        companyTbody.empty();
        for (let key in data) {
            if (key === "公司费用") {
                let rowSpan = data[key].length;
                if(rowSpan === 0){
                    rowSpan = 1;
                }
                let idTemp = Math.ceil(Math.random() * 10000);
                companyTbody.append(
                    '<tr id="tempTr' + idTemp + '">'
                    + '<td rowspan="' + rowSpan + '">2</td>'
                    + '<td rowspan="' + rowSpan + '">公司费用</td>'
                    + '</tr>'
                );

                if($.isEmptyObject(data[key])){//判空
                    $("#tempTr" + idTemp).append(
                        '<td></td>'
                        +'<td></td>'
                        +'<td></td>'
                        +'<td></td>'
                        +'<td></td>'
                    );
                }else {
                    let monthMoney = 0;//月合计金额
                    // 铺收益单位和金额
                    for (let key2 in data[key]) {

                        monthMoney += data[key][key2][1];
                        if (key2 === "0") {
                            $("#tempTr" + idTemp).append(
                                '<td>' + data[key][key2][0] + '</td>'
                                + '<td>' + data[key][key2][1].toFixed(2) + '</td>'
                            )
                        } else if (key2 > 0) {
                            companyTbody.append(
                                '<tr>'
                                + '<td>' + data[key][key2][0] + '</td>'//收益单位
                                + '<td>' + data[key][key2][1].toFixed(2) + '</td>'//金额
                                + '</tr>'
                            )
                        }
                    }
                    //铺月合计金额
                    $("#tempTr" + idTemp).append(
                        '<td rowspan="' + rowSpan + '">' + monthMoney.toFixed(2) + '</td>'
                        + '<td rowspan="' + rowSpan + '"></td>'
                        + '<td rowspan="' + rowSpan + '"></td>'
                    );
                }



            }
        }

        // 3 部门费用
        let departmentTbody = $("#departmentTbody");
        departmentTbody.empty();
        for (let key in data) {
            if (key === "部门费用") {
                let rowSpan = data[key].length;
                if(rowSpan === 0){
                    rowSpan = 1;
                }
                let idTemp = Math.ceil(Math.random() * 10000);
                departmentTbody.append(
                    '<tr id="tempTr' + idTemp + '">'
                    + '<td rowspan="' + rowSpan + '">3</td>'
                    + '<td rowspan="' + rowSpan + '">部门费用</td>'
                    + '</tr>'
                );

                if($.isEmptyObject(data[key])){//判空
                    $("#tempTr" + idTemp).append(
                        '<td></td>'
                        +'<td></td>'
                        +'<td></td>'
                        +'<td></td>'
                        +'<td></td>'
                    );
                }else {
                    let monthMoney = 0;//月合计金额
                    // 铺收益单位和金额
                    for (let key2 in data[key]) {
                        monthMoney += data[key][key2][1];
                        if (key2 === "0") {
                            $("#tempTr" + idTemp).append(
                                '<td>' + data[key][key2][0] + '</td>'
                                + '<td>' + data[key][key2][1].toFixed(2) + '</td>'
                            )
                        } else if (key2 > 0) {
                            departmentTbody.append(
                                '<tr>'
                                + '<td>' + data[key][key2][0] + '</td>'//收益单位
                                + '<td>' + data[key][key2][1].toFixed(2) + '</td>'//金额
                                + '</tr>'
                            )
                        }
                    }
                    //铺月合计金额
                    $("#tempTr" + idTemp).append(
                        '<td rowspan="' + rowSpan + '">' + monthMoney.toFixed(2) + '</td>'
                        + '<td rowspan="' + rowSpan + '"></td>'
                        + '<td rowspan="' + rowSpan + '"></td>'
                    );
                }
            }
        }

        // 4 项目支出
        let disburseTbody = $("#disburseTbody");
        disburseTbody.empty();
        for (let key in data) {
            if (key === "项目支出") {
                let rowSpan = data[key].length;
                if(rowSpan === 0){
                    rowSpan = 1;
                }
                let idTemp = Math.ceil(Math.random() * 10000);
                disburseTbody.append(
                    '<tr id="tempTr' + idTemp + '">'
                    + '<td rowspan="' + rowSpan + '">4</td>'
                    + '<td rowspan="' + rowSpan + '">项目支出</td>'
                    + '</tr>'
                );

                if($.isEmptyObject(data[key])){//判空
                    $("#tempTr" + idTemp).append(
                        '<td></td>'
                        +'<td></td>'
                        +'<td></td>'
                        +'<td></td>'
                        +'<td></td>'
                    );
                }else{
                    let monthMoney = 0;//月合计金额
                    // 铺收益单位和金额
                    for (let key2 in data[key]) {
                        monthMoney += data[key][key2][1];
                        if (key2 === "0") {
                            $("#tempTr" + idTemp).append(
                                '<td>' + data[key][key2][0] + '</td>'
                                + '<td>' + data[key][key2][1].toFixed(2) + '</td>'
                            )
                        } else if (key2 > 0) {
                            disburseTbody.append(
                                '<tr>'
                                + '<td>' + data[key][key2][0] + '</td>'//收益单位
                                + '<td>' + data[key][key2][1].toFixed(2) + '</td>'//金额
                                + '</tr>'
                            )
                        }
                    }
                    //铺月合计金额
                    $("#tempTr" + idTemp).append(
                        '<td rowspan="' + rowSpan + '">' + monthMoney.toFixed(2) + '</td>'
                        + '<td rowspan="' + rowSpan + '"></td>'
                        + '<td rowspan="' + rowSpan + '"></td>'
                    );
                }

            }
        }
    }, "json")
});