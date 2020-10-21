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
            ,url: '/squad_group_fee/team_fee'
            ,toolbar: '#toolbarDemo'
            ,title: '进度表信息'
            //,even: true //行之间颜色区分
            ,id: 'testReload'
            ,page: true  //开启分页
            ,limit: 10 //每页展示多少条
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
                {title: '序号', templet: '#index' , type: "numbers"}
                ,{field:'squadGroupFeeNum', title: '编号' , align:'center', filter: true}
                ,{field:'squadGroupFeeName', title: '名称' , filter: true}
                ,{field:'productionCostsMoney', title: '合计应付', filter: true ,sort: true ,templet : function(d){
                        let productionCostsMoney = d["productionCostsMoney"];
                        if(typeof productionCostsMoney === "string"){
                           return 0;
                        }
                        return productionCostsMoney.toFixed(2);
                    }}
                ,{field:'disbursePaymentAmount', title: '已付', filter: true ,sort: true ,templet : function(d){
                        let disbursePaymentAmount = d["disbursePaymentAmount"];
                        if(typeof disbursePaymentAmount === "string"){
                           return 0;
                        }
                        return disbursePaymentAmount.toFixed(2);
                    }}
                ,{ title: '未付', filter: true ,sort: true ,templet : function(d){
                        let productionCostsMoney = d["productionCostsMoney"];
                        let disbursePaymentAmount = d["disbursePaymentAmount"];
                        if(typeof productionCostsMoney === "string"){
                            productionCostsMoney = 0;
                        }
                        if(typeof disbursePaymentAmount === "string"){
                            disbursePaymentAmount = 0;
                        }
                        let calculate = productionCostsMoney - disbursePaymentAmount;
                        if(calculate === 0){
                            return calculate;
                        }
                        return calculate.toFixed(2);
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
                        multiple_value: roomNum,
                        multiple_key:'projectNum$projectName$projectLocationBean.projectLocationName$implementBeans.departmentBean.departmentName$projectManagementType$managementBean.managementPartnersBean.userBean.userName$managementBean.managementSponsor$managementBean.managementMainHead$implementBeans.secondPartyBean.secondPartyName$implementBeans.implementProgress$implementBeans.projectStatusBean.projectStatusName$contractBean.contractState'
                    }
                }, 'data');
            }
        };

        // 导出全部数据
        $('#layui-hideen').on('click', function(){
            $.ajax({
                url: '/squad_group_fee/team_fee',
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
