$(function () {
    // 自定义模块，这里只需要开放soulTable即可
    layui.config({
        base: '/vendor/layui/ext/',   // 模块所在目录
        bases: 'lay_exts/',
        version: 'v1.4.2' // 插件版本号
    }).extend({
        soulTable: 'soulTable',  // 模块别名
        excel: 'excel',
    });

    layui.use(['form','table','soulTable',"excel"], function(){
        var table = layui.table;
        var soulTable = layui.soulTable;
        var excel = layui.excel;
        //方法级渲染
        var ins1 = table.render({
            elem: '#LAY_table_user'
            ,method: 'get'
            ,url: '/work_load/personnel_commission'
            ,parseData:function (res) {
                return {
                    "code": res["code"],
                    "msg": res["message"],
                    "count": res.data["total"],
                    "data": res.data["list"]
                }
            }
            ,toolbar: '#toolbarDemo'
            ,title: '人员成本'
            //,even: true //行之间颜色区分
            ,id: 'testReload'
            ,page: true  //开启分页
            ,limit: 20 //每页展示多少条
            ,limits: [50,100,300,500,9999] //自定义
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
                  {field:"projectNum" , title: '项目编号'}
                , {field:"projectName" , title: '项目名称'}
                , {field:"projectManagementType", title: '经营类型'}
                , {field:"implementName" , title: '实施部'}
                , {field:"date" , title: '日期'}
                , {field:"day" , title: '工作天数'}
                , {field:"staff" , title: '员工'}
                , {field:"cost" , title: '人员成本'}

                // ,{field:'projectName', title: '项目名称',sort: true, filter: true}
                // ,{field:'disburseMoney', title: '支出总金额',sort: true, filter: true}
                // ,{field:'incomeMoney', title: '回款总金额', sort: true, filter: true}
                // ,{field:'invoiceMoney', title: '开票总金额',sort: true, filter: true}
            ]]
            ,done: function(){
                //加载完之后回调
                // soulTable.render(this)
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
                var multipleKey = $('input[name="multipleKey"]').val();//获取输入框的值
                //执行重载
                table.reload('testReload', {
                    page: {
                        curr: 1 //重新从第 1 页开始
                    }
                    ,where: {
                        multiple_value: roomNum,
                        multiple_key: multipleKey
                    }
                }, 'data');
            }
        };

        // 导出全部数据
        $('#layui-hideen').on('click', function(){
            $.ajax({
                url: '/work_load/personnel_commission',
                type: "get",
                dataType: "json",
                success: function (obj) {
                    var data = obj.data.list;
                    // 重点！！！如果后端给的数据顺序和映射关系不对，请执行梳理函数后导出
                    data = excel.filterExportData(data, [
                        'projectNum'
                        ,'projectName'
                        ,'projectManagementType'
                        ,'implementName'
                        ,'date'
                        ,'day'
                        ,'staff'
                        ,'cost'
                    ]);
                    // 重点2！！！一般都需要加一个表头，表头的键名顺序需要与最终导出的数据一致
                    data.unshift({ projectNum: "项目编号", projectName: "项目名称", projectManagementType: '经营类型', implementName: '实施部', date: '日期', day: '工作天数', staff: '员工', cost: '人员成本'});
                    var table = layui.table;
                    //将上述表格示例导出为 csv 文件
                    //table.exportFile(ins1.config.id, data); //data 为该实例中的任意数量的数据
                    excel.exportExcel(data, '导出全部数据.xlsx', 'xlsx');
                }
            })
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
