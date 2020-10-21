$(function () {
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
        //方法级渲染
        var ins1 = table.render({
            elem: '#LAY_table_user'
            ,method: 'get'
            ,url: '/project/amount'
            ,toolbar: '#toolbarDemo'
            ,title: '项目总计信息'
            //,even: true //行之间颜色区分
            ,id: 'testReload'
            ,page: true  //开启分页
            ,limit: 20 //每页展示多少条
            ,limits: [10,20,30,40,50] //自定义
            ,height: 'full-110'
            ,totalRow: true
            ,overflow: {
                type: 'tips'
                ,hoverTime: 300 // 悬停时间，单位ms, 悬停 hoverTime 后才会显示，默认为 0
                ,color: 'black' // 字体颜色
                ,bgColor: 'white' // 背景色
                ,minWidth: 100 // 最小宽度
                ,maxWidth: 500 // 最大宽度
            }
            ,cols: [[
                {field:'projectNum', title: '项目编号', sort: true, filter: true}
                ,{field:'projectName', title: '项目名称',sort: true, filter: true}
                ,{field:'disburseMoney', title: '支出总金额',sort: true, filter: true}
                ,{field:'incomeMoney', title: '回款总金额', sort: true, filter: true}
                ,{field:'invoiceMoney', title: '开票总金额',sort: true, filter: true}
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

        // 监听搜索传递后台
        var $ = layui.$, active = {
            reload: function(){
                var roomNum = $('input[name="roomNum"]').val();//获取输入框的值
                //执行重载
                table.reload('testReload', {
                    page: {
                        curr: 1 //重新从第 1 页开始
                    }
                    ,where: {
                        multipleValue: roomNum
                    }
                }, 'data');
            }
        };

        // 导出全部数据
        $('#layui-hideen').on('click', function(){
            $.ajax({
                url: '/project/amount',
                type: "get",
                dataType: "json",
                success: function (obj) {
                    var data = obj.data;
                    var table = layui.table;
                    //将上述表格示例导出为 csv 文件
                    table.exportFile(ins1.config.id, data); //data 为该实例中的任意数量的数据
                }
            });

        });

        // 监听查询搜索按钮
        $('.layui-inline .layui-btn').on('click', function(){
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });

        // 监听重置按钮
        $('#reset').on('click', function(){
            $('input[name="roomNum"]').val("");//获取输入框的值
            location.reload(); //关闭回调 刷新
        });
    });
});
