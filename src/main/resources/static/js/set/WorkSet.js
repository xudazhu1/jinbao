$(function () {
    let colsData = [];
    let rowData = [];
    var ins2;
    var ins3;
    var ins4;
    // 自定义模块，这里只需要开放soulTable即可
    layui.config({
        base: '/vendor/layui/ext/',   // 模块所在目录
        version: 'v1.4.2' // 插件版本号
    }).extend({
        soulTable: 'soulTable'  // 模块别名
    });
    layui.use(['form','table','soulTable'], function(){
        var table = layui.table;
        soulTable = layui.soulTable;

        layui.use('laydate', function () {
            var laydate = layui.laydate;
            lay('.dateTime').each(function () {
                laydate.render({
                    elem: this
                    , trigger: 'click'
                    , type: 'datetime' //指定时间格式
                });
            });
        });

        //  监听选项卡
        layui.use('element', function(){
          var $ = layui.jquery
          ,element = layui.element; //Tab的切换功能，切换事件监听等，需要依赖element模块
          element.on('tab(tab)', function(data){
            var dataTab = data.index;
            if(dataTab == "1"){
                var department = '勘察部';
                survey(department); //勘察部
            }else if(dataTab == "2"){
                var department = '咨询部';
                Consultation(department); //咨询部
            }else if(dataTab == "3"){
                var department = '实验室';
                Experiment(department); //实验室
            }else if(dataTab == "4"){
                userPost();  //用户对应岗位
                searchBtn();
            }else if(dataTab == "5"){
                postInput();  //岗位
                searchBtn();
            }else if(dataTab == "6"){
                metering(); //计量单位
                searchBtn();
            }
          });
        });
        //  测量部表格
        var ins1 = table.render({
            elem: '#measure'
            ,method: 'get'
            ,url: '/profession/table'
            , where : {
               'departmentBean.departmentName' : '测量部',
                'btnId' : '#barDemo'
            }
            ,first: true //首次渲染表格
            ,parseData:function (res) {
                if (this.first) { //首次
                    colsData = res.data["cols"];
                    rowData = res.data["row"];
                    this.cols[0] = colsData;
                    this.cols[1] = rowData;
                    //添加操作列
                    this.first = false;
                    table.reload(this.id); //重载一级表头
                }
                return {
                    "code": res["code"],
                    "msg": res["message"],
                    "count": res.data["total"],
                    "data": res.data["dataList"]
                }
            }
            ,toolbar: '#toolbarDemo'
            ,title: '测量部信息'
            //,even: true //行之间颜色区分
            ,id: 'measureReload'
            ,page: true  //开启分页
            ,limit: 10 //每页展示多少条
            ,limits: [10,50,300,500,1000]
            ,height: 'full-200'
            ,totalRow: true
            ,overflow: {
                type: 'tips'
                ,hoverTime: 300 // 悬停时间，单位ms, 悬停 hoverTime 后才会显示，默认为 0
                ,color: 'black' // 字体颜色
                ,bgColor: 'white' // 背景色
                ,minWidth: 100 // 最小宽度
                ,maxWidth: 500 // 最大宽度
            }
            ,cols: [colsData,rowData]
            ,done: function(){
                this.first = true;

                //加载完之后回调
                //soulTable.render(this)
            }
            ,response: {
                statusCode: 200 //规定成功的状态码，默认：0
            }
            ,request: { // 更改页数参数名
                pageName: 'pageNum' //页码的参数名称，默认：page
                ,limitName: 'pageSize' //每页数据量的参数名，默认：limit
            }
        });
        // 监听测量部编辑
        table.on('tool(user)', function (obj) {
            var data = obj.data;
            var professionId = data.professionId;
            if(obj.event === 'edit'){
                var layerTitle = '测量部编辑页面';
                var department = '1';
                editDome(professionId,department,layerTitle);
            }
        });

        // 监听搜索传递后台
        var $ = layui.$, active = {
            reload: function(){
                var roomNum = $('input[name="measureName"]').val();//
                // 获取输入框的值
                //执行重载
                table.reload('measureReload', {
                    page: {
                        curr: 1 //重新从第 1 页开始
                    }
                    ,where: {
                        professionName: roomNum
                    }
                }, 'data');
            }
        };

        // 监听查询搜索按钮
        $('#measureQuery').on('click', function(){
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });

        // 监听重置按钮
        $('#measureReset').on('click', function(){
            $('input[name="measureName"]').val("");//获取输入框的值
            location.reload(); //关闭回调 刷新
        });

        //  勘察部表格
        function survey(department){
            // 监听勘察部编辑
            table.on('tool(survey)', function (obj) {
                var data = obj.data;
                var professionId = data.professionId;
                if(obj.event === 'edit'){
                    var layerTitle = '勘察部编辑页面';
                    var department = '2';
                    editDome(professionId,department,layerTitle);
                }
            });

            var ins1 = table.render({
                elem: '#survey'
                ,method: 'get'
                ,url: '/profession/table'
                , where : {
                   'departmentBean.departmentName' : department,
                   'btnId' : '#surveyOperation'
                }
                ,first: true //首次渲染表格
                ,parseData:function (res) {
                    if (this.first) { //首次
                        colsData = res.data["cols"];
                        rowData = res.data["row"];
                        this.cols[0] = colsData;
                        this.cols[1] = rowData;
                        //添加操作列
                        this.first = false;
                        table.reload(this.id); //重载一级表头
                    }
                    return {
                        "code": res["code"],
                        "msg": res["message"],
                        "count": res.data["total"],
                        "data": res.data["dataList"]
                    }
                }
                ,toolbar: '#surveyDemo'
                ,title: '勘察部信息'
                //,even: true //行之间颜色区分
                ,id: 'surveyReload'
                ,page: true  //开启分页
                ,limit: 10 //每页展示多少条
                ,limits: [10,50,300,500,1000]
                ,height: 'full-200'
                ,totalRow: true
                ,overflow: {
                    type: 'tips'
                    ,hoverTime: 300 // 悬停时间，单位ms, 悬停 hoverTime 后才会显示，默认为 0
                    ,color: 'black' // 字体颜色
                    ,bgColor: 'white' // 背景色
                    ,minWidth: 100 // 最小宽度
                    ,maxWidth: 500 // 最大宽度
                }
                ,cols: [colsData,rowData]
                ,done: function(){
                    this.first = true;

                    //加载完之后回调
                    //soulTable.render(this)
                }
                ,response: {
                    statusCode: 200 //规定成功的状态码，默认：0
                }
                ,request: { // 更改页数参数名
                    pageName: 'pageNum' //页码的参数名称，默认：page
                    ,limitName: 'pageSize' //每页数据量的参数名，默认：limit
                }
            });

            // 监听搜索传递后台
            var $ = layui.$, active = {
                reload: function(){
                    var roomNum = $('input[name="surveyName"]').val();//
                    // 获取输入框的值
                    //执行重载
                    table.reload('surveyReload', {
                        page: {
                            curr: 1 //重新从第 1 页开始
                        }
                        ,where: {
                            professionName: roomNum
                        }
                    }, 'data');
                }
            };

            // 监听查询搜索按钮
            $('#surveyQuery').on('click', function(){
                var type = $(this).data('type');
                active[type] ? active[type].call(this) : '';
            });

            // 监听重置按钮
            $('#surveyReset').on('click', function(){
                $('input[name="surveyName"]').val("");//获取输入框的值
                location.reload(); //关闭回调 刷新
            });
        }

         //  咨询部表格
        function Consultation(department){
             // 监听咨询部编辑
            table.on('tool(Consultation)', function (obj) {
                var data = obj.data;
                var professionId = data.professionId;
                if(obj.event === 'edit'){
                    var layerTitle = '咨询部编辑页面';
                    var department = '3';
                    editDome(professionId,department,layerTitle);
                }
            });

            var ins1 = table.render({
                elem: '#Consultation'
                ,method: 'get'
                ,url: '/profession/table'
                 , where : {
                   'departmentBean.departmentName' : department,
                    'btnId' : '#consultationOperation'
                 }
                ,first: true //首次渲染表格
                ,parseData:function (res) {
                    if (this.first) { //首次
                        colsData = res.data["cols"];
                        rowData = res.data["row"];
                        this.cols[0] = colsData;
                        this.cols[1] = rowData;
                        //添加操作列
                        this.first = false;
                        table.reload(this.id); //重载一级表头
                    }
                    return {
                        "code": res["code"],
                        "msg": res["message"],
                        "count": res.data["total"],
                        "data": res.data["dataList"]
                    }
                }
                ,toolbar: '#ConsultationDemo'
                ,title: '咨询部信息'
                //,even: true //行之间颜色区分
                ,id: 'ConsultationResetReload'
                ,page: true  //开启分页
                ,limit: 10 //每页展示多少条
                ,limits: [10,50,300,500,1000]
                ,height: 'full-200'
                ,totalRow: true
                ,overflow: {
                    type: 'tips'
                    ,hoverTime: 300 // 悬停时间，单位ms, 悬停 hoverTime 后才会显示，默认为 0
                    ,color: 'black' // 字体颜色
                    ,bgColor: 'white' // 背景色
                    ,minWidth: 100 // 最小宽度
                    ,maxWidth: 500 // 最大宽度
                }
                ,cols: [colsData,rowData]
                ,done: function(){
                    this.first = true;

                    //加载完之后回调
                    //soulTable.render(this)
                }
                ,response: {
                    statusCode: 200 //规定成功的状态码，默认：0
                }
                ,request: { // 更改页数参数名
                    pageName: 'pageNum' //页码的参数名称，默认：page
                    ,limitName: 'pageSize' //每页数据量的参数名，默认：limit
                }
            });

            // 监听搜索传递后台
            var $ = layui.$, active = {
                reload: function(){
                    var roomNum = $('input[name="ConsultationResetName"]').val();//
                    // 获取输入框的值
                    //执行重载
                    table.reload('ConsultationResetReload', {
                        page: {
                            curr: 1 //重新从第 1 页开始
                        }
                        ,where: {
                            professionName: roomNum
                        }
                    }, 'data');
                }
            };

            // 监听查询搜索按钮
            $('#ConsultationQuery').on('click', function(){
                var type = $(this).data('type');
                active[type] ? active[type].call(this) : '';
            });

            // 监听重置按钮
            $('#ConsultationReset').on('click', function(){
                $('input[name="ConsultationResetName"]').val("");//获取输入框的值
                location.reload(); //关闭回调 刷新
            });
        }

        //  实验室表格
        function Experiment(department){
                // 监听实验室编辑
                table.on('tool(Experiment)', function (obj) {
                    var data = obj.data;
                    var professionId = data.professionId;
                    if(obj.event === 'edit'){
                        var layerTitle = '实验室编辑页面';
                        var department = '4';
                        editDome(professionId,department,layerTitle);
                    }
                });

            var ins1 = table.render({
                elem: '#Experiment'
                ,method: 'get'
                ,url: '/profession/table'
                , where : {
                   'departmentBean.departmentName' : department,
                   'btnId' : '#experimentOperation'
                }
                ,first: true //首次渲染表格
                ,parseData:function (res) {
                    if (this.first) { //首次
                        colsData = res.data["cols"];
                        rowData = res.data["row"];
                        this.cols[0] = colsData;
                        this.cols[1] = rowData;
                        //添加操作列
                        this.first = false;
                        table.reload(this.id); //重载一级表头
                    }
                    return {
                        "code": res["code"],
                        "msg": res["message"],
                        "count": res.data["total"],
                        "data": res.data["dataList"]
                    }
                }
                ,toolbar: '#ExperimentDemo'
                ,title: '实验室信息'
                //,even: true //行之间颜色区分
                ,id: 'ExperimentReload'
                ,page: true  //开启分页
                ,limit: 10 //每页展示多少条
                ,limits: [10,50,300,500,1000]
                ,height: 'full-200'
                ,totalRow: true
                ,overflow: {
                    type: 'tips'
                    ,hoverTime: 300 // 悬停时间，单位ms, 悬停 hoverTime 后才会显示，默认为 0
                    ,color: 'black' // 字体颜色
                    ,bgColor: 'white' // 背景色
                    ,minWidth: 100 // 最小宽度
                    ,maxWidth: 500 // 最大宽度
                }
                ,cols: [colsData,rowData]
                ,done: function(){
                    this.first = true;

                    //加载完之后回调
                    //soulTable.render(this)
                }
                ,response: {
                    statusCode: 200 //规定成功的状态码，默认：0
                }
                ,request: { // 更改页数参数名
                    pageName: 'pageNum' //页码的参数名称，默认：page
                    ,limitName: 'pageSize' //每页数据量的参数名，默认：limit
                }
            });

            // 监听搜索传递后台
            var $ = layui.$, active = {
                reload: function(){
                    var roomNum = $('input[name="ExperimentName"]').val();//
                    // 获取输入框的值
                    //执行重载
                    table.reload('ExperimentReload', {
                        page: {
                            curr: 1 //重新从第 1 页开始
                        }
                        ,where: {
                            professionName: roomNum
                        }
                    }, 'data');
                }
            };

            // 监听查询搜索按钮
            $('#ExperimentQuery').on('click', function(){
                var type = $(this).data('type');
                active[type] ? active[type].call(this) : '';
            });

            // 监听重置按钮
            $('#ExperimentReset').on('click', function(){
                $('input[name="ExperimentName"]').val("");//获取输入框的值
                location.reload(); //关闭回调 刷新
            });
        }

        // 导出全部数据
        $(document).on('click', '.layui-measure', function () {
            $.ajax({
                url: '/profession/table',
                type: "get",
                dataType: "json",
                success: function (obj) {
                    console.log(obj)
                    var data = obj.data;
                    var table = layui.table;
                    //将上述表格示例导出为 csv 文件
                    table.exportFile(ins1.config.id, data); //data 为该实例中的任意数量的数据
                }
            });

        });

        // 添加页面跳转
        $(document).on('click', '.layui-summary', function () {
            let href = $(this).attr("data-href"); //要打开页面的地址
            let department = $(this).attr("data-id"); //要打开页面的地址
            let layerTitle = $(this).attr("data-title"); //页面标题
            layerTitle = layerTitle === "undefined" ? false : layerTitle;
            layer.ready(function () {
                layerIndex = layer.open({
                    type: 2,
                    title: layerTitle,
                    area: setpage(),
                    content: href +'?department='+department,
                    btn: ['保存','取消'], //按钮
                    yes: function(index,layero){
                       //调用内嵌的按钮
                       $(layero).find("iframe").contents().find('.submit button').trigger("click");
                    }
                    ,end: function(){
                        $(".layui-laypage-btn").click(); //当前表格刷新
                    }
                });
            });
        });

        // 编辑页面跳转
        function editDome(professionId,department,layerTitle){
            layer.ready(function () {
                layerIndex = layer.open({
                    type: 2,
                    title: layerTitle,
                    area: setpage(),
                    content: '../set/WorkSetMeasure.html?'+'department='+department+'&professionId='+professionId,
                    btn: ['保存','取消'], //按钮
                    yes: function(index,layero){
                       //调用内嵌的按钮
                       $(layero).find("iframe").contents().find('.submit button').trigger("click");
                    }
                    ,end: function(){
                        $(".layui-laypage-btn").click(); //当前表格刷新
                    }
                });
            });
        }

         //岗位
        function postInput(){
            ins2 = table.render({
                elem: '#LAY_table_post'
                ,method: 'get'
                ,url: '/post/all'
                ,parseData: function(res){ //res 即为原始返回的数据
                return {
                    "code": res["code"], //解析接口状态
                    "msg": res["message"], //解析提示文本
                    "count": res["data"]["total"], //解析数据长度
                    "data": res.data.list //解析数据列表
                };}
                ,toolbar: '#toolbarPost'
                ,title: '岗位'
                ,page: true  //开启分页
                ,height: 523
                ,limit: 300
                ,limits: [10,50,300,500,1000]
                // , rowDrag: {/*trigger: 'row',*/ done: function (obj) {
                //
                //     }
                // }
                , cols: [[
                    {type: 'checkbox', fixed: 'left'}
                    , {field: 'index', title: '序号', width: 80, fixed: 'left', type: 'numbers'}
                    , {field: 'squadGroupFeeNum', title: '岗位编号', width: 100, fixed: 'left',hide:true}
                    , {field: 'departmentName', title: '实施部', sort: true}
                    , {field: 'postLevel', title: '岗位级别', width: 120}
                    , {field: 'squadGroupFeeStatusBean', title: '状态', sort: true,hide:true}
                    , {field: 'postName', title: '岗位名称', width: 350, sort: true}
                    , {field: 'squadGroupFeeTime', title: '录入时间', width: 180,hide:true}
                    , {field: 'userBean', title: '录入人', width: 120, sort: true,hide:true}
                    , {fixed: 'right', title: '操作', toolbar: '#barPost', width: 120}
                ]]

                ,done: function(){
                    //加载完之后回调
                    soulTable.render(this)
                }
                ,response: {
                    statusCode: 200 //规定成功的状态码，默认：0
                }
                ,request: { // 更改页数参数名
                    pageName: 'pageNum' //页码的参数名称，默认：page
                    ,limitName: 'pageSize' //每页数据量的参数名，默认：limit
                }
            });
        }

        //用户对应岗位
        function userPost(){
            ins3 = table.render({
                elem: '#LAY_table_userPost'
                ,method: 'get'
                ,url: '/user_work/search'
                ,toolbar: '#toolbarUserPost'
                ,title: '用户对应岗位信息'
                ,limit: 10
                ,limits: [10,50,300,500,1000]
                //,even: true //行之间颜色区分
                //,id: 'testReload'
                ,parseData: function(res){ //res 即为原始返回的数据
                    return {
                        "code": res["code"], //解析接口状态
                        "msg": res["message"], //解析提示文本
                        "count": res["data"]["total"], //解析数据长度
                        "data": res.data.list //解析数据列表
                    };}
                ,page: true  //开启分页
                ,height: 523
                // , rowDrag: {/*trigger: 'row',*/ done: function (obj) {
                //     }
                // }
                , cols: [[
                    {type: 'checkbox', fixed: 'left'}
                    , {field: 'index', title: '序号', width: 80, fixed: 'left', type: 'numbers'}
                    , {field: 'squadGroupFeeNum', title: '用户对应编号', width: 100, fixed: 'left',hide:true}
                    , {field: 'userName', title: '用户名', sort: true}
                    , {field: 'departmentName', title: '部门', width: 120}
                    , {field: 'squadGroupFeeStatusBean', title: '状态', sort: true,hide:true}
                    , {field: 'staff', title: '员工岗位', width: 150, sort: true}
                    , {field: 'supervisor', title: '主管岗位', width: 150, sort: true}
                    , {field: 'captain', title: '队长岗位', width: 150, sort: true}
                    , {field: 'squadGroupFeeTime', title: '录入时间', width: 180,hide:true}
                    , {field: 'userBean', title: '录入人', width: 120, sort: true,hide:true}
                    , {fixed: 'right', title: '操作', toolbar: '#barPost', width: 150}
                ]]

                ,done: function(){
                    //加载完之后回调
                    soulTable.render(this)
                }
                ,response: {
                    statusCode: 200 //规定成功的状态码，默认：0
                }
                ,request: { // 更改页数参数名
                    pageName: 'pageNum' //页码的参数名称，默认：page
                    ,limitName: 'pageSize' //每页数据量的参数名，默认：limit
                }
            });
        }

        //计量单位
        function metering(){
            ins4 = table.render({
                elem: '#LAY_table_measure'
                ,method: 'get'
                ,url: '/profession_unit/all'
                ,toolbar: '#toolbarMeasure'
                ,title: '项目总计信息'
                ,limit: 300
                ,limits: [10,50,300,500,1000]
                ,parseData: function(res){ //res 即为原始返回的数据
                    return {
                        "code": res["code"], //解析接口状态
                        "msg": res["message"], //解析提示文本
                        "count": res["data"]["total"], //解析数据长度
                        "data": res.data.list //解析数据列表
                    };}
                ,page: true  //开启分页
                ,height: 523
                // , rowDrag: {/*trigger: 'row',*/ done: function (obj) {
                //
                //     }
                // }
                , cols: [[
                    {type: 'checkbox', fixed: 'left'}
                    , {field: 'index', title: '序号', width: 80, fixed: 'left', type: 'numbers'}
                    , {field: 'squadGroupFeeNum', title: '计量单位编号', width: 150, fixed: 'left',hide:true}
                    , {field: 'professionUnitName', title: '计量单位', sort: true}
                    , {field: 'squadGroupFeeStatusBean', title: '状态', sort: true,hide:true}
                    , {field: 'squadGroupFeeTime', title: '录入时间', width: 180,hide:true}
                    , {field: 'userBean', title: '录入人', width: 120, sort: true,hide:true}
                    , {fixed: 'right', title: '操作', toolbar: '#barPost', width: 130}
                ]]

                ,done: function(){
                    //加载完之后回调
                    soulTable.render(this)
                }
                ,response: {
                    statusCode: 200 //规定成功的状态码，默认：0
                }
                ,request: { // 更改页数参数名
                    pageName: 'pageNum' //页码的参数名称，默认：page
                    ,limitName: 'pageSize' //每页数据量的参数名，默认：limit
                }
            });
        }

        // 监听搜索传递后台
        function searchBtn(){
            var $ = layui.$, active = {
                reload: function(){
                  switch (tableIns) {
                      case ins1:
                          var roomNum = $('input[name="roomNum"]').val();
                          ins1.reload({
                              where: { //设定异步数据接口的额外参数，任意设
                                  multipleValue:roomNum
                              }
                              ,page: {
                                  curr: 1 //重新从第 1 页开始
                              }
                          });
                          break;
                      case ins2: //岗位
                          var postLevel= $('input[name="postLevel"]').val(), //岗位级别
                          departmentName =$('input[name="departmentName"]').val(),//实施部
                          postName =$('input[name="postName"]').val(); //岗位名称

                          tableIns.reload({
                              where: { //设定异步数据接口的额外参数，任意设
                                  postLevel,
                                  departmentName,
                                  postName
                              }
                              ,page: {
                                  curr: 1 //重新从第 1 页开始
                              }
                          });
                          break;
                      case ins3: //用户对应岗位
                          var department=$(".implementName").val(),//获取部门id
                              staff=$('input[name="staff"]').val(),//获取员工
                              supervisor=$('input[name="supervisor"]').val(),//获取主管
                              captain=$('input[name="captain"]').val(),//获取队长
                              userName=$('input[name="userName"]').val();//获取用户名

                          tableIns.reload({
                              where: { //设定异步数据接口的额外参数，任意设
                                  "departmentBean.departmentId":department,
                                  "staffPostBean.postName":staff,
                                  "supervisorPostBean.postName":supervisor,
                                  "captainPostBean.postName":captain,
                                  "userBean.userName":userName
                              }
                              ,page: {
                                  curr: 1 //重新从第 1 页开始
                              }
                          });
                          break;
                      case ins4: //计量单位
                          var professionUnitName=$('input[name="professionUnitName"]').val();//获取部门id
                          tableIns.reload({
                              where: { //设定异步数据接口的额外参数，任意设
                                  professionUnitName
                              }
                              ,page: {
                                  curr: 1 //重新从第 1 页开始
                              }
                          });
                          break;

                  }

                }
            };

             // 监听查询搜索按钮
            $('.layui-inline .layui-btn').on('click', function(){
                var type = $(this).data('type');
                //table_id='testReload';
                tableIns = ins1;
                active[type] ? active[type].call(this) : '';
            });

            $('.search').on('click', function(){
                var type = $(this).data('type');
                var searchId=$(this).attr('id');
                   // table_id='testReload';
                switch (searchId) {
                    case "search-userPost": //用户对应岗位
                        tableIns = ins3;
                        break;
                    case "search-post": //岗位
                        tableIns = ins2;
                        break;
                    case "search-measure": //计量单位
                        tableIns = ins4;
                        break;
                }
                active[type] ? active[type].call(this) : '';
            });
            // 监听重置按钮
            $('#reset').on('click', function(){
                $('input[name="roomNum"]').val("");//获取输入框的值
                location.reload(); //关闭回调 刷新
            });

            //监听清空按钮
            $(".clear").click(function () {
                $("input").val('');
                var type = $(this).data('type');
                var searchId=$(this).attr('id');
                // table_id='testReload';
                switch (searchId) {
                    case "clear-userPost": //用户对应岗位
                        tableIns = ins3;
                        break;
                    case "clear-post": //岗位
                        tableIns = ins2;
                        break;
                    case "clear-measure": //计量单位
                        tableIns = ins4;
                        break;
                }
                active[type] ? active[type].call(this) : '';
            })

            // 计量单位编辑，岗位编辑，用户对应岗位编辑列表监听
            table.on('tool(barPost)', function (obj) {
                var data = obj.data;
                if(obj.event === 'edit'){
                    if(typeof $(data).attr("professionUnitId") !== 'undefined'){
                        let title = "计量单位编辑";
                        let url = 'WorkSetEdit/measureEdit.html?id='+data.professionUnitId;
                        test(url,title);
                    }else if(typeof $(data).attr("postId") !== 'undefined'){
                        let title = "岗位编辑";
                        let url = 'WorkSetEdit/postEdit.html?id='+data.postId;
                        test(url,title);
                    }else {
                        let title = "用户对应岗位编辑";
                        let url = 'WorkSetEdit/userPostEdit.html?id='+data.UserWorkId;
                        test(url,title);
                    }

                }else if(obj.event === 'nomal' || obj.event === 'del'){
                    let useid=window.top.userTemp.userId;
                    if(obj.event === 'nomal'){
                        testStatus=2;
                        ins1.squadGroupFeeStatusBean=2;

                    } else {testStatus=1;
                        ins1.squadGroupFeeStatusBean=1;
                    }
                    //let itemData = $.param({"_method": "put"}) + "&" + $.param(data) +"&" + $.param({"userBean.userId":useid});
                    let itemData = $.param({"_method": "put"}) + "&" + $.param({"squadGroupFeeId": data.squadGroupFeeId}) +"&" + $.param({"squadGroupFeeStatusBean.squadGroupFeeStatusId":testStatus });
                    update_request(itemData);
                }
            });
        }

        $('body').on('click', '.addCase', function () {
            var href = $(this).attr("data-href"); //要打开页面的地址
            var layerTitle = $(this).attr("data-title"); //页面标题
            layerTitle = layerTitle === "undefined" ? false : layerTitle;
            var width = $(this).attr("data-width"); //页面宽高
            var height = $(this).attr("data-height");
            width = width === undefined ? '100%' : width;
            height = height === undefined ? '100%' : height;
            layer.ready(function () {
                layerIndex = layer.open({
                    type: 2,
                    title: layerTitle,
                    maxmin: false,
                    area: setpagetwo(),
                    content: href
                    ,btn: ['保存','取消'] //按钮
                ,yes: function(index,layero){
                  //  调用内嵌的按钮
                    $(layero).find("iframe").contents().find('.submit button').trigger("click");
                    }
                    ,end: function(){
                        $(".layui-laypage-btn").click(); //当前表格刷新
                    }
                });
            });
        });

         //跳转到编辑页
        function test(id,title) {
            var width = $(this).attr("data-width"); //页面宽高
            var height = $(this).attr("data-height");
            width = width === undefined ? '100%' : width;
            height = height === undefined ? '100%' : height;
            layer.ready(function () {
                layerIndex = layer.open({
                    type: 2,
                    title: title,
                    maxmin: false,
                    area: setpagetwo(),
                    content: id
                    ,btn: ['保存','取消'] //按钮
                    ,yes: function(index,layero){
                        //  调用内嵌的按钮
                        $(layero).find("iframe").contents().find('.submit button').trigger("click");
                    },end: function(){
                        $(".layui-laypage-btn").click(); //当前表格刷新
                    }
                });
            });
        }
        //修改请求
        function update_request(i){
            $.post(url , i, function (data) {
                if (data) {
                    layer.msg('操作成功 2秒后自动刷新', {
                        time: 2000 //2秒关闭（如果不配置，默认是3秒）
                    } ,  function () {
                        // window.location.reload();
                        // window.parent.flush();
                        //table.render("testReload");
                        table.reload("testReload");
                        // var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                        // parent.layer.close(index); //再执行关闭
                    });
                } else {
                    layer.msg('添加失败',{icon:5});
                    status = false;
                }
            }, "json").fail(function (res) {
                layer.msg('数据提交失败 请刷新重试',{icon:5});
                console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
                status = false;
            }, "json");
        }

         // 铺实施部
        $.get("/department/implement_department",function(data){
            var t =$(".implementName").val();//获取下拉框的值
            $(data).each(function (index,item) {

                //console.log(item.departmentId)
                var i =item.departmentId;
                if( t !== ""+i ){
                    $(".implementName").append('<option value="'+item.departmentId+'">'+item.departmentName+'</option>');
                }
                layui.form.render('select');
            })
        }, "json");

        // 跳转页面宽高度比例
        function setpage(width,height){
            if(height != undefined && width != undefined){
                return [width, height];
            }else if ($(window).width() < 458) {
                return ['100%', '100%'];
            }else if($(window).width() <= 1800 && $(window).width() > 1221) {
                return ['55%', '65%'];
            } else if( $(window).width() <= 1220) {
                return ['75%', '80%'];
            }
        }

        //岗位、用户对应岗位、计量单位页面框高比例
        function setpagetwo(width,height){
            if(height !== undefined && width !== undefined){
                return [width, height];
            }else if ($(window).width() < 458) {
                return ['100%', '100%'];
            }else if($(window).width() <= 1900 && $(window).width() > 1221) {
                return ['40%', '64%'];
            } else if( $(window).width() <= 1220) {
                return ['55%', '60%'];
            }
        }
    });
});

