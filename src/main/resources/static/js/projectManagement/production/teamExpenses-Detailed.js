$(function () {
    // 自定义模块，这里只需要开放soulTable即可
    layui.config({
        base: '/vendor/layui-soul-table/ext/',   // 模块所在目录
        bases: 'lay_exts/',
        // version: 'v1.4.2' // 插件版本号
    }).extend({
        soulTable: 'soulTable',  // 模块别名
        excel: 'excel',
    });

    layui.use(['form','table','soulTable',"excel"], function(){
        var table = layui.table;
        var soulTable = layui.soulTable;
        var excel = layui.excel;
       // 4.监听费用明细
       table.on('tool(detailed)', function(obj){ //test为你table的lay-filter对应的值
           var data = obj.data;
           var squadGroupFeeId = data.squadGroupFeeId;
           var implementId = data.implementId;
           if(obj.event === 'copeWith'){
                var layerTitle = '班组费合计应付详情';
                var title = 1;
                var href = "../production/paymentDetails.html";
                editInformation(title,squadGroupFeeId,implementId,href,layerTitle)
           }else if(obj.event === 'paid'){
                var layerTitle = '班组费已付详情';
                var title = 2;
                var href = "../production/paymentDetails.html";
                editInformation(title,squadGroupFeeId,implementId,href,layerTitle)
           }
        });
        //方法级渲染
        var ins1 = table.render({
            elem: '#Detailed'
            ,method: 'get'
            ,url: '/squad_group_fee/team_fee_detail'
            ,toolbar: '#toolbarDemo'
            ,title: '班组费明细信息'
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
                {field:'LAY_TABLE_INDEX', title:'序号', align:'center', totalRowText: '合计',templet : function(d){
                        return d.LAY_TABLE_INDEX + 1;
                }, width:60}
                ,{field:'squadGroupFeeNum', title: '编号明细' , align:'center', filter: true}
                ,{field:'squadGroupFeeName', title: '名称' , filter: true}
                ,{field:'projectNum', title: '项目编号' , filter: true}
                ,{field:'projectName', title: '项目名称' , filter: true}
                ,{field:'departmentName', title: '实施部' , filter: true}
                ,{field:'productionCostsMoney', title: '合计应付', filter: true ,totalRow: true ,event: 'copeWith' ,sort: true ,templet : function(d){
                        let productionCostsMoney = d["productionCostsMoney"];
                        if(typeof productionCostsMoney === "string"){
                           return 0;
                        }
                        return productionCostsMoney.toFixed(2);
                    }}
                ,{field:'disbursePaymentAmount', title: '已付', filter: true ,totalRow: true,event: 'paid',sort: true ,templet : function(d){
                        let disbursePaymentAmount = d["disbursePaymentAmount"];
                        if(typeof disbursePaymentAmount === "string" || disbursePaymentAmount === undefined){
                           return 0;
                        }
                        return disbursePaymentAmount.toFixed(2);
                    }}
                ,{ title: '未付', filter: true ,sort: true ,totalRow: true,templet : function(d){
                        let productionCostsMoney = d["productionCostsMoney"];
                        let disbursePaymentAmount = d["disbursePaymentAmount"];
                        if(typeof productionCostsMoney === "string" || productionCostsMoney === undefined){
                            productionCostsMoney = 0;
                        }
                        if(typeof disbursePaymentAmount === "string" || disbursePaymentAmount === undefined){
                            disbursePaymentAmount = 0;
                        }
                        console.log((productionCostsMoney - disbursePaymentAmount));
                        return (productionCostsMoney - disbursePaymentAmount).toFixed(2);
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
                url: '/squad_group_fee/team_fee_detail',
                type: "get",
                dataType: "json",
                success: function (obj) {
                    var data = obj.content;
                    // 重点！！！如果后端给的数据顺序和映射关系不对，请执行梳理函数后导出
                    data = excel.filterExportData(data, [
                        'LAY_TABLE_INDEX'
                        ,'squadGroupFeeNum'
                        ,'squadGroupFeeName'
                        ,'projectNum'
                        ,'projectName'
                        ,'departmentName'
                        ,'productionCostsMoney'
                        ,'disbursePaymentAmount'
                    ]);
                    // 重点2！！！一般都需要加一个表头，表头的键名顺序需要与最终导出的数据一致
                    data.unshift({ LAY_TABLE_INDEX: "序号", squadGroupFeeNum: "编号明细", squadGroupFeeName: '名称', projectNum: '项目编号', projectName: '项目名称', departmentName: '实施部', productionCostsMoney: '合计应付', disbursePaymentAmount: '已付'});
                    //将上述表格示例导出为 csv 文件
                    //table.exportFile(ins1.config.id, data); //data 为该实例中的任意数量的数据
                    excel.exportExcel(data, '导出全部数据.xlsx', 'xlsx');
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

        // 编辑页面跳转
        function editInformation(title,squadGroupFeeId,implementId,href,layerTitle){
            layer.ready(function () {
                var index = layer.open({
                    type: 2,
                    title: layerTitle,
                    area: ["100%","100%"],
                    content: href+'?squadGroupFeeId='+squadGroupFeeId + '&implementId=' + implementId + '&title=' + title,
                    end: function(){
                        $(".layui-laypage-btn").click(); //当前表格刷新
                    }
                });
                layer.full(index); // 弹出全屏
            });
        }
    });
});
