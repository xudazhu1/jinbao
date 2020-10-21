
$(function () {
//选择 实施部的时候 会获取 该部门所有员工 人员
    $(".implementDepartment").change(function () {
        var departmentId = $(this).find("option:selected").val();
        var implUsers = $(".implUsers");
        implUsers.empty();
        $.get("/department/user_list",{"id":departmentId},function (data) {
            var options  = "<option value=''>全部</option>";
            $(data).each(function (i,user) {
                options += "<option value='"+user["userId"]+"'>"+user["userName"]+"</option>"
            });
            implUsers.append(options);
        },"json")
    });
//页面加载的时候 铺 实施部 下拉框
    $.get("/department",{"departmentName":"实施部"},function (data) {
        var implementDepartment = $(".implementDepartment");
        implementDepartment.empty();
        var options  = "<option value=''>全部</option>"
        $(data["content"][0]["nextDepartmentBeans"]).each(function (i,nextDepartment) {
            options += "<option value='"+nextDepartment["departmentId"]+"'>"+nextDepartment["departmentName"]+"</option>"
        });
        implementDepartment.append(options);
    },"json");

//页面加载的时候 铺 根据 部门 铺工种  (待定)
    $.get("/work_type/work_type_profession",{},function (data) {
        var professionCustom = $(".professionCustom");
        var professionTr = $(".professionTr");

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
            "                        </th>" +
            "                        <th>" +
            "                            <span>实施人员</span>" +
            "                            <i class='glyphicon glyphicon-chevron-down show-i test-normal'></i>" +
            "                            <div class='filter-div' tabindex='1'>" +
            "                                <div class='filter-body pre-scrollable test-1 need-data' data-href='implementDepartment'>" +
            "                                    <input type='text' class='form-control input-sm' name='implementDepartment' value='' autocomplete='off' placeholder='键入或勾选'>" +
            "                                    <input type='hidden' name='$D.implementDepartment' value=''>" +
            "                                </div>" +
            "                                <div class='scrollbar'>" +
            "                                </div>" +
            "                                <button type='button' class='btn btn-xs btn-warning btn-confirm'>确定</button>" +
            "                            </div>" +
            "                        </th>" +
            "                       <th>" +
            "                            <span>岗位</span>" +
            "                            <i class='glyphicon glyphicon-chevron-down show-i test-normal'></i>" +
            "                            <div class='filter-div' tabindex='1'>" +
            "                                <div class='filter-body pre-scrollable test-1 need-data' data-href='post'>" +
            "                                    <input type='text' class='form-control input-sm' name='post' value='' autocomplete='off' placeholder='键入或勾选'>" +
            "                                    <input type='hidden' name='$D.post' value=''>" +
            "                                </div>" +
            "                                <div class='scrollbar'>" +
            "                                </div>" +
            "                                <button type='button' class='btn btn-xs btn-warning btn-confirm'>确定</button>" +
            "                            </div>" +
            "              </th>");
        for (var key in data){
            //铺 自定义列
            professionCustom .append(
                "<div class='checkbox checkbox-info checkbox-circle'>" +
                "                            <input type='checkbox'  checked>" +
                "                            <label for='total'>"+key+"</label>" +
                "                        </div>"
            );
            //铺 表格 (样式 蹦了)
            professionTr.append(
                "<th >" +
                "                        <span>"+key+"</span>" +
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

    },"json");



});


