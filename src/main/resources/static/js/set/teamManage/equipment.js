$ (function () {// 自定义模块，这里只需要开放soulTable即可
    // let pageNum;
    // let pageSize;
    let itemDataP;
    let statusId ="";
    let squadGroupFeeGenre="";
    let startTime;
    let endTime;
    let testStatus;
    let url= "/production_costs_detail";
    layui.config({
        base: '/vendor/layui/ext/',   // 模块所在目录
        version: 'v1.4.2' // 插件版本号
    }).extend({
        soulTable: 'soulTable'  // 模块别名
    });

    layui.use('form', function () {
        var form = layui.form;
        var $ = layui.$;
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
                    area: setpage(),
                    content: href
                    ,btn: ['保存','取消'] //按钮
                    ,yes: function(index,layero){
                    //  调用内嵌的按钮
                    $(layero).find("iframe").contents().find('.submit button').trigger("click");
                }
                });
            });
        });
        // 监听搜索传递后台
        form.on('select', function(data){
            //得到美化后的DOM对象
            if (data.elem.name === 'productionCostsDetailType'){
             var t = $('#productionCostsDetailType').find("option:selected").val();
             console.log(t);
                if(t ==='设备使用费'){
                    document.getElementById("add_costEquipment").style.display = "block";
                    document.getElementById("teamBoss").style.display = "block";
                }
                squadGroupFeeGenre = data.value;
            }
            else {statusId= data.value;}
            //console.log(status);

        });


        //清空
        $("#clear").click(function () {
            $("input").val('');
            location.reload();
        })
    });

   function test(id) {
        var width = $(this).attr("data-width"); //页面宽高
        var height = $(this).attr("data-height");
        width = width === undefined ? '100%' : width;
        height = height === undefined ? '100%' : height;

        layer.ready(function () {
            layerIndex = layer.open({
                type: 2,
                title: "编辑",
                maxmin: false,
                area: setpage(),
                content: 'equipmentEdit.html?id='+id
                ,btn: ['保存','取消'] //按钮
                ,yes: function(index,layero){
                    //  调用内嵌的按钮
                    $(layero).find("iframe").contents().find('.submit button').trigger("click");
                }
            });
        });
    }

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


        layui.use(['form', 'table', 'soulTable'], function () {
            var table = layui.table;
            let soulTable = layui.soulTable;
            var ins1 = table.render({
                elem: '#test'
                ,method: 'get'
                ,url: '/production_costs_detail'
                ,toolbar: '#toolbarDemo'
                ,title: '项目总计信息'
                //,even: true //行之间颜色区分
                ,id: 'testReload'
                ,parseData: function(res){ //res 即为原始返回的数据
                    return {
                        "code": res["code"], //解析接口状态
                        "msg": res["msg"], //解析提示文本
                        "count": res["data"]["totalRows"], //解析数据长度
                        "data": res.data.rows //解析数据列表
                    };}
                ,page: true  //开启分页
                ,height: 523
                , rowDrag: {/*trigger: 'row',*/ done: function (obj) {
                        // 完成时（松开时）触发
                        // 如果拖动前和拖动后无变化，则不会触发此方法
                        //console.log(obj.row) // 当前行数据
                        //console.log(obj.cache) // 改动后全表数据
                        // console.log(obj.oldIndex) // 原来的数据索引
                        //console.log(obj.newIndex) // 改动后数据索引
                        // dataForm = obj.cache;
                        // for (var i = 0; i < dataForm.length;) {
                        //     dataForm[i].index = ++i;
                        //     console.log("index"+dataForm[i].index);
                        // }
                        //console.log(dataForm);
                        // loadData(dataForm);
                     }
                }
                , cols: [[
                    {type: 'checkbox', fixed: 'left'}
                    , {field: 'index', title: '序号', width: 80, fixed: 'left', type: 'numbers'}
                    , {field: 'productionCostsDetailNum', title: '编号', width: 110, fixed: 'left'}
                    , {field: 'productionCostsDetailType', title: '类型', width: 100}
                    , {field: 'productionCostsDetailName', title: '名称', sort: true}
                    , {field: 'productionCostsEquipmentPrice', title: '单天使用费', width: 100}
                    , {field: 'squadGroupFeeStatusBean.squadGroupFeeStatusId', title: '状态', sort: true,hide:true}
                    , {field: 'productionCostsDetailRemark', title: '备注', width: 350, sort: true}
                    , {field: 'productionCostsDetailTime', title: '录入时间', width: 180}
                    , {field: 'userName', title: '录入人', width: 120, sort: true}
                    , {fixed: 'right', title: '操作', toolbar: '#barDemo', width: 120}
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


            //工具栏事件
            table.on('toolbar(test)', function (obj) {
                var checkStatus = table.checkStatus(obj.config.id);
                switch (obj.event) {
                    case 'getCheckData':
                        var data = checkStatus.data;
                        layer.alert(JSON.stringify(data));
                        break;
                    case 'getCheckLength':
                        var data = checkStatus.data;
                        layer.msg('选中了：' + data.length + ' 个');
                        break;
                    case 'isAll':
                        layer.msg(checkStatus.isAll ? '全选' : '未全选')
                        break;
                }
            });
            //3.列表按钮监听
            table.on('tool(test)', function (obj) {
                var data = obj.data;
                console.log(data);
                if(obj.event === 'edit'){
                    test(data.productionCostsDetailId)
                }else if(obj.event === 'nomal' || obj.event === 'del'){
                    let useid=window.top.userTemp.userId;
                    if(obj.event === 'nomal'){
                        testStatus=2;
                        ins1.squadGroupFeeStatusBean=2;

                    } else {testStatus=1;
                        ins1.squadGroupFeeStatusBean=1;
                    }
                    //let itemData = $.param({"_method": "put"}) + "&" + $.param(data) +"&" + $.param({"userBean.userId":useid});
                    let itemData = $.param({"_method": "put"}) + "&" + $.param({"productionCostsDetailId": data.productionCostsDetailId}) +"&" + $.param({"squadGroupFeeStatusBean.squadGroupFeeStatusId":testStatus });
                    update_request(itemData);
                }
            });
            var $ = layui.$, active = {
                reload: function(){
                    let company=$('input[name="productionCostsDetailNum"]').val();
                    let teamB = $('input[name="teamBoss"]').val();
                    //执行重载
                    table.reload('testReload', {
                        page: {
                            curr: 1 //重新从第 1 页开始
                        }
                        ,where: {
                            productionCostsDetailNum: company,
                            productionCostsDetailType: squadGroupFeeGenre,
                            squadGroupFeeStatusName: statusId,
                            squadGroupFeeName:teamB
                        }
                    }, 'data');
                }
            };

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
            // 监听查询搜索按钮
            $('#search').on('click', function(){
                //console.log(itemDataP)
                var type = $(this).data('type');
                active[type] ? active[type].call(this) : '';
            });

            //监听单元格编辑
            table.on('edit(test)', function(obj){
                var value = obj.value //得到修改后的值//////
                     ,data = obj.data //得到所在行所有键值
                    var field_t = obj.field; //得到字段
                console.log(field_t);
               // let itemData = $.param({"_method": "put"}) + "&" + $.param({"_method": "put","squadGroupFeeId": data.squadGroupFeeId,field_t:value}) +"&" + $.param({field_t:value});
                let itemData = $.param({"_method": "put","productionCostsDetailId": data.productionCostsDetailId,field_t:value});
                //layer.msg('[ID: '+ data.id +'] ' + field + ' 字段更改为：'+ value);
                update_request(itemData);
            });

        });

    // 跳转页面宽高度比例
    function setpage(width,height){
        if(height != undefined && width != undefined){
            return [width, height];
        }else if ($(window).width() < 458) {
            return ['100%', '100%'];
        }else if($(window).width() <= 1800 && $(window).width() > 1221) {
            return ['55%', '30%'];
        } else if( $(window).width() <= 1220) {
            return ['75%', '50%'];
        }
    }

});
