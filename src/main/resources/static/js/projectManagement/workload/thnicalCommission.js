$(function () {
    // 自定义模块，这里只需要开放soulTable即可
    layui.config({
        base: '/vendor/layui-soul-table/ext/'   // 模块所在目录
        // version: 'v1.4.2' // 插件版本号
    }).extend({
        soulTable: 'soulTable'  // 模块别名
    });

    layui.use(['form','table','soulTable'], function(){
        var table = layui.table;
        var soulTable = layui.soulTable;
        //方法级渲染
        var ins1 = table.render({
            elem: '#LAY_table_user'
            ,method: 'get'
            ,url: '/work_load/team_fee'
            ,toolbar: '#toolbarDemo'
            ,title: '进度表信息'
            //,even: true //行之间颜色区分
            ,id: 'testReload'
            ,page: true  //开启分页
            ,limit: 20 //每页展示多少条
            ,height: 'full-130'
            ,totalRow: true
            ,overflow: {
                type: 'tips'
                ,hoverTime: 300 // 悬停时间，单位ms, 悬停 hoverTime 后才会显示，默认为 0
                ,color: 'black' // 字体颜色
                ,bgColor: 'white' // 背景色
                ,minWidth: 100 // 最小宽度
                ,maxWidth: 500 // 最大宽度
            }
            ,where: {
                globalSearch : document.querySelector("[name='globalSearch']").value
            }
            ,cols: [
                [
                    {title: '序号', templet: '#index' , type: "numbers" , rowspan: 2 }
                    ,{field:'projectNum', title: '项目编号' , filter: true , rowspan: 2 }
                    ,{field:'userName', title: '姓名' , align:'center', filter: true ,  rowspan: 2 }
                    // ,{field:'departmentName', title: '所属部门' , align:'center', filter: true ,  rowspan: 2 }
                    ,{field:'', title: '全部提成' , align:'center', filter: true , colspan: 3 }
                    ,{field:'', title: '已付提成' , align:'center', filter: true , colspan: 3 }
                    ,{field:'', title: '未付提成' , align:'center', filter: true , colspan: 3 }
                ],
                [
                // {title: '序号', templet: '#index' , type: "numbers"}
                // ,{field:'projectNum', title: '项目编号' , filter: true}
                // ,{field:'userName', title: '姓名' , align:'center', filter: true}
                {field:'sumStaffAmont', title: '员工提成', filter: true , totalRow : true ,templet : function(d){
                        let sumStaffAmont = parseFloat( d["sumStaffAmont"] );
                        return sumStaffAmont.toFixed(2);
                    }}
                ,{field:'sumSupervisorAmont', title: '主管提成', filter: true , totalRow : true ,templet : function(d){
                        let sumSupervisorAmont = parseFloat( d["sumSupervisorAmont"] );
                        return sumSupervisorAmont.toFixed(2);
                    }}
                ,{field:'sumProfessionAmont', title: '队长提成', filter: true , totalRow : true ,templet : function(d){
                        let sumProfessionAmont = parseFloat( d["sumProfessionAmont"] );
                        return sumProfessionAmont.toFixed(2);
                    }}
                ,{field:'paidSumStaffAmont', title: '员工提成', filter: true , totalRow : true ,templet : function(d){
                        let paidSumStaffAmont = parseFloat( d["paidSumStaffAmont"] );
                        return paidSumStaffAmont.toFixed(2);
                    }}
                ,{field:'paidSumSupervisorAmont', title: '主管提成', filter: true , totalRow : true ,templet : function(d){
                        let paidSumSupervisorAmont = parseFloat( d["paidSumSupervisorAmont"] );
                        return paidSumSupervisorAmont.toFixed(2);
                    }}
                ,{field:'paidSumProfessionAmont', title: '队长提成', filter: true , totalRow : true ,templet : function(d){
                        let paidSumProfessionAmont = parseFloat( d["paidSumProfessionAmont"] );
                        return paidSumProfessionAmont.toFixed(2);
                    }}
                ,{field:'unpaidSumStaffAmont', title: '员工提成', filter: true , totalRow : true ,templet : function(d){
                        let unpaidSumStaffAmont = parseFloat( d["unpaidSumStaffAmont"] );
                        return unpaidSumStaffAmont.toFixed(2);
                    }}
                ,{field:'unpaidSumSupervisorAmont', title: '主管提成', filter: true , totalRow : true ,templet : function(d){
                        let unpaidSumSupervisorAmont = parseFloat( d["unpaidSumSupervisorAmont"] );
                        return unpaidSumSupervisorAmont.toFixed(2);
                    }}
                ,{field:'unpaidSumProfessionAmont', title: '队长提成', filter: true , totalRow : true ,templet : function(d){
                        let unpaidSumProfessionAmont = parseFloat( d["unpaidSumProfessionAmont"] );
                        return unpaidSumProfessionAmont.toFixed(2);
                    }}
            ]]
             ,filter: {
                items:['data','condition' , 'clearCache']
                , cache : true// 只显示
            }
            ,done: function(){
                //加载完之后回调
                soulTable.render(this); // 调用筛选方法
            }
            ,parseData: function(res){ //res 即为原始返回的数据
                return {
                    "code": 0, //解析接口状态
                    "msg": "msg", //解析提示文本
                    "count": res["totalElements"], //解析数据长度
                    "data": res.content //解析数据列表
                };
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
                        globalSearch : document.querySelector("[name='globalSearch']").value
                    }
                }, 'data');
            }
        };

        // 导出全部数据
        $('#layui-hideen').on('click', function(){
            $.ajax({
                url: '/work_load/team_fee',
                type: "get",
                dataType: "json",
                success: function (obj) {
                    var data = obj.content;
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
