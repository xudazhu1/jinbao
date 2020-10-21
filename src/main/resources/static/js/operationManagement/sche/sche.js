$(function () {
    // 自定义模块，这里只需要开放soulTable即可
    /*layui.config({
        base: '/vendor/layui/ext/',   // 模块所在目录
        version: 'v1.4.2' // 插件版本号
    }).extend({
        soulTable: 'soulTable'  // 模块别名
    });*/

    layui.use(['form','table'], function(){
        var table = layui.table;
        //soulTable = layui.soulTable;
        //方法级渲染
        var ins1 = table.render({
            elem: '#LAY_table_user'
            ,method: 'get'
            ,url: '/project/schedule'
            ,toolbar: '#toolbarDemo'
            ,title: '进度表信息'
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
                {field:'4', title: '项目编号', fixed: 'left', width:101, sort: true}
                ,{field:'5', title: '项目名称', fixed: true, width:101, sort: true}
                ,{field:'6', title: '所属类型', width:101 ,sort: true}
                ,{field:'7', title: '区域', width:73 ,sort: true}
                ,{field:'8', title: '甲方单位', sort: true, width:86}
                ,{field:'9', title: '实施部', sort: true, width:86}
                ,{field:'10', title: '经营类型', sort: true, width:101}
                ,{field:'12', title: '业务人', sort: true, width:87}
                ,{field:'13', title: '业务介绍人', sort: true, width:115}
                ,{field:'14', title: '经营负责人', sort: true, width:115}
                ,{field:'15', title: '乙方单位', sort: true, width:101}
                ,{field:'16', title: '实施进度', sort: true, width:101}
                ,{field:'17', title: '项目状态', sort: true, width:101}
                ,{field:'18', title: '合同状态', sort: true, width:101}
                ,{field:'19', title: '项目金额', sort: true, width:101}
                ,{field:'20', title: '预估项目收入', sort: true, width:129}
                ,{field:'21', title: '回款金额', sort: true, width:101}
                ,{field:'22', title: '未回款金额', sort: true, width:115}
                ,{field:'23', title: '预估可开票金额', sort: true, width:143 , templet : function(d){
                    //预估可开票金额 小于0  则 等于 0
                        var invoiceMoney = d["23"];
                        if(invoiceMoney <= 0 ){
                          return  invoiceMoney = 0
                        }
                        return invoiceMoney.toFixed(2);
                    }}
                ,{field:'24', title: '开票金额', sort: true, width:101}
                ,{field:'25', title: '开票未回款', sort: true, width:115}
                ,{field:'11', title: '经营备注', sort: true, width:87}
            ]],
            done: function(){
                //加载完之后回调
                //soulTable.render(this)
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
                        multiple_value: roomNum,
                        multiple_key:'projectNum$projectName$projectLocationBean.projectLocationName$implementBeans.departmentBean.departmentName$projectManagementType$managementBean.managementPartnersBean.userBean.userName$managementBean.managementSponsor$managementBean.managementMainHead$implementBeans.secondPartyBean.secondPartyName$implementBeans.implementProgress$implementBeans.projectStatusBean.projectStatusName$contractBean.contractState'
                    }
                }, 'data');
            }
        };

        // 导出全部数据
        $('#layui-hideen').on('click', function(){
            $.ajax({
                url: '/project/schedule',
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
