
$(function () {
    $(".searchBtn").click(function () {
        getPageDate(1,10);
    });

    $.get("/implement_department",{},function (data) {
        console.log(data);
        var implementDepartment = $(".implementDepartment");
        implementDepartment.empty();
        var options  = "<option value='"+" "+"'>全部</option>"
        $(data).each(function (i,impl) {
            options += "<option value='"+impl["implementDepartmentId"]+"'>"+impl["implementDepartmentName"]+"</option>"
        });
        implementDepartment.append(options);
    },"json");
    //页面加载的时候 铺 根据 部门 铺工种  (待定)
    $.get("/work_type/work_type_profession", {}, function (data) {
        var professionCustom = $(".professionCustom");
        var professionTr = $(".professionTr");
        professionTr.empty();

        professionTr.append("<th>" +
            "                            <span >项目编号</span>" +
            "                            <i class='glyphicon glyphicon-chevron-down show-i test-normal'></i>" +
            "                            <div class='filter-div' tabindex='1'>" +
            "                                <div class='filter-body pre-scrollable test-1 need-data' data-property='projectName'>" +
            "                                    <input type='text' class='form-control input-sm' name='projectNum' value='' autocomplete='off' placeholder='键入或勾选'>" +
            "                                    <input type='hidden' name='$D.projectNum' value=''>" +
            "                                </div>" +
            "                                <div class='scrollbar'>" +
            "                                </div>" +
            "                                <button type='button' class='btn btn-xs btn-warning btn-confirm'>确定</button>" +
            "                            </div>" +
            "                        </th>" +
            "                        <th>" +
            "                            <span >项目名称</span>" +
            "                            <i class='glyphicon glyphicon-chevron-down show-i test-normal'></i>" +
            "                            <div class='filter-div' tabindex='1'>" +
            "                                <div class='filter-body pre-scrollable test-1 need-data' data-property='projectName'>" +
            "                                    <input type='text' class='form-control input-sm' name='projectName' value='' autocomplete='off' placeholder='键入或勾选'>" +
            "                                    <input type='hidden' name='$D.projectName' value=''>" +
            "                                </div>" +
            "                                <div class='scrollbar'>" +
            "                                </div>" +
            "                                <button type='button' class='btn btn-xs btn-warning btn-confirm'>确定</button>" +
            "                            </div>" +
            "                        </th>" +
            "                        <th>" +
            "                            <span >实施部</span>" +
            "                            <i class='glyphicon glyphicon-chevron-down show-i test-normal'></i>" +
            "                            <div class='filter-div' tabindex='1'>" +
            "                                <div class='filter-body pre-scrollable test-1 need-data' data-property='total'>" +
            "                                    <input type='text' class='form-control input-sm' name='total' value='' autocomplete='off' placeholder='键入或勾选'>" +
            "                                    <input type='hidden' name='$D.total' value=''>" +
            "                                </div>" +
            "                                <div class='scrollbar'>" +
            "                                </div>" +
            "                                <button type='button' class='btn btn-xs btn-warning btn-confirm '>确定</button>" +
            "                            </div>" +
            "                        </th>"+"                        " +
            "                        <th>" +
            "                            <span >总量</span>" +
            "                            <i class='glyphicon glyphicon-chevron-down show-i test-normal'></i>" +
            "                            <div class='filter-div' tabindex='1'>" +
            "                                <div class='filter-body pre-scrollable test-1 need-data' data-property='total'>" +
            "                                    <input type='text' class='form-control input-sm' name='total' value='' autocomplete='off' placeholder='键入或勾选'>" +
            "                                    <input type='hidden' name='$D.total' value=''>" +
            "                                </div>" +
            "                                <div class='scrollbar'>" +
            "                                </div>" +
            "                                <button type='button' class='btn btn-xs btn-warning btn-confirm '>确定</button>" +
            "                            </div>" +
            "                        </th>");
        for (var key in data){
            //铺 自定义列
            professionCustom.append(
                "<div class='checkbox checkbox-info checkbox-circle'>" +
                "                            <input type='checkbox'  checked>" +
                "                            <label for='total'>" + key + "</label>" +
                "                        </div>"
            );
            //铺 表格 (样式 蹦了)
            professionTr.append(
                "<th >" +
                "                        <span>" + key + "</span>" +
                "                            <i class='glyphicon glyphicon-chevron-down show-i test-normal'></i>\n" +
                "                            <div class='filter-div' tabindex='1'>\n" +
                "                                <div class='filter-body pre-scrollable test-1 need-data' data-href='implementDepartment'>\n" +
                "                                    <input type='text' class='form-control input-sm' name='implementDepartment' value='' autocomplete='off' placeholder='键入或勾选'>\n" +
                "                                    <input type='hidden' name='$D.implementDepartment' value=''>\n" +
                "                                </div>\n" +
                "                                <div class='scrollbar'>\n" +
                "                                </div>\n" +
                "                                <button type='button' class='btn btn-xs btn-warning btn-confirm'>确定</button>\n" +
                "                            </div>\n" +
                "</th>"
            );
        }

    }, "json");

    getPageDate(1,10);
});

function getPageDate(pageNum,pageSize) {
    $("#items").empty();
    var formTemp = $("#searchForm");
    var params = $.param({"pageNum": pageNum, "pageSize": pageSize})+ '&' + formTemp.serialize();
    $.get("/implement/work_count", params, function (data) {
        $(data["content"]).each(function (i, ele) {
            var contentStr = "<tr>\n" +
                "                        <td>" + ele["projectNum"] + "</td>" +
                "                        <td>" + ele["projectName"] + "</td>" +
                "                        <td>" + ele["implementName"] + "</td>"+
                "                        <td>" + ele["count"] + "</td>";

            var professionSum = $(".professionTr").children().length;
            var start = 4;
            for (let j = start; j < professionSum; j++) {
                var thProfessionName = $(".professionTr").children().eq(start).children("span").text();
                var work = typeof(ele["professions"][thProfessionName]) === "undefined" ? "" : ele["professions"][thProfessionName];
                contentStr += "<td>" + work + "</td>";
                if (professionSum === start) {
                    return false;
                }
                start++;
            }
            contentStr += "</tr>";
            $("#items").append(contentStr);
        });

        showPageButtuns(
            {"pageNum":data.number + 1 , "countPage": data["totalPages"] ,"pageSize" : data["size"] ,  "countNum": data["totalElements"] } ,
            $("#page") ,
            getPageDate);
    }, "json");

}


