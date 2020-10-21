// 自定义模块，这里只需要开放soulTable即可
layui.config({
    base: '/vendor/layui-soul-table/ext/'   // 模块所在目录
    // version: 'v1.4.2' // 插件版本号
}).extend({
    soulTable: 'soulTable'  // 模块别名
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

let customWhere = " incomeBean.incomeAuditStatus = '1' and incomeBean.projectBean.projectId > 0 ";

layui.use(['form','table','soulTable'], function(){
    var table = layui.table;
    soulTable = layui.soulTable;
    //方法级渲染
    //先行定义cols 以便当条件传给后台
    // var cols = [[
    //     {field: 'incomeId', title: 'ID', filter: true}
    //     , {field: 'incomeNum', title: '流水编号', filter: true}
    //     , {field: 'incomeMoney', title: '回款金额', filter: true}
    //     , {field: 'incomeCountMoneyBackMoney', title: '总实际回款', filter: true}
    // ]];

    table.render({
        elem: '#myTable',
        url: '/soulTable',
        method: 'post', //由于条件可能过多 超过url的字符上限 采用post
        toolbar: '#toolbarDemo',
        height: $(document).height() - $('#myTable').offset().top - 50,
        page: true,
        where : { selectFields : //要查询的数据(字段) 使用hql (类似sql) 语法
                "incomeBean.incomeId as incomeId" +
                "$incomeBeanXXXprojectBean.projectNum as projectNum" + //bean 与 bean 之间的连接使用 XXX 代替 bean与字段的连接不用
                "$incomeBeanXXXprojectBean.projectName as projectName" +
                "$incomeBean.incomeNum as incomeNum" +
                "$incomeBean.incomeMoney as incomeMoney" +
                "$ sum ( case when  incomeBeanXXXincomeImplMoneyBeans.incomeImplMoney  is null then 0   else incomeBeanXXXincomeImplMoneyBeans.incomeImplMoney   end )  as countIncomeImplMoney" +
                "$ ( case when (  incomeBean.incomeMoney - sum ( incomeBeanXXXincomeImplMoneyBeans.incomeImplMoney  ) ) is null then '未分配' else ( incomeBean.incomeMoney -  sum ( incomeBeanXXXincomeImplMoneyBeans.incomeImplMoney  ) ) end )  as difference" +
                "$ (  case " +
                "       when " +
                "       sum ( incomeBeanXXXincomeImplMoneyBeans.incomeImplMoney  ) = incomeBean.incomeMoney " +
                "           then '分配完成' " +
                "       else '未分配' " +
                "    end ) as status " ,
               joins :
                   'projectBean' +
                   '$incomeImplMoneyBeans' , // 上面 selectFields 所用到的除了本表外的bean路径
                tableName : "income" //表名
                , groupBy : "incomeBean.incomeId" //分组字段 多个以$分割
                , customWhere : customWhere
        },
        cols: [[
            {field: 'incomeId', title: '收入ID', filter: true},
            {field: 'projectNum', title: '项目编号', filter: true},
            {field: 'projectName', title: '项目名称', filter: true}
            , {field: 'incomeNum', title: '流水编号', filter: true}
            , {field: 'incomeMoney', title: '回款金额', filter: true}
            , {field: 'countIncomeImplMoney', title: '总分配金额', filter: true}
            , {field: 'difference', title: '差额', filter: true}
            , {field: 'status', title: '状态', filter: true}
            , {fixed: 'right', title: '操作', toolbar: '#barDemo', width: 120}
        ]]
        ,filter: {
            items:['data','condition' , 'clearCache']
            , cache : true// 只显示
        }
        ,parseData: function(res){ //res 即为原始返回的数据
            return {
                "code": 0, //解析接口状态
                "msg": "msg", //解析提示文本
                "count": res["totalElements"], //解析数据长度
                "data": res.content //解析数据列表
            };
        }
        ,done: function(){
            //加载完之后回调
            soulTable.render(this); // 调用筛选方法
        }

    });

    //监听工具条
    table.on('tool', function(obj){
        var data = obj.data;
        if(obj.event === 'edit'){
            layer.ready(function () {
                layerIndex = layer.open({
                    type: 2,
                    title: '编辑',
                    maxmin: false,
                    area: [ '600px' , '400px' ],
                    content: './incomeImplMoney-add.html?id=' + data.incomeId
                });
            });


        } else if(obj.event === 'del'){
            layer.confirm('真的删除行么', function(index){
                obj.del();
                layer.close(index);
            });
        }
    });



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

    $("#oneKeyFlush").on("click" ,  function () {
        loadingIndex = layer.load(2, { //icon支持传入0-2
            shade: [0.5, 'gray'], //0.5透明度的灰色背景
            content: '操作中...',
            success: function (layero) {
                layero.find('.layui-layer-content').css({
                    'padding-top': '39px',
                    'width': '60px'
                });
            }
        });
        $.get("/income_impl_money/flush" , function () {
            layer.close(loadingIndex);
            layer.msg("完成 ! ");
        });
    });

});


$(function () {
    $(document).on("mouseenter" , "#oneKeyFlush" , function () {
        tips = layer.tips("<span style='color: #2E2D3C;'>为所有只有单个实施部的项目自动分配100%的回款金额</span>", this, {tips: [1, '#fffff8']});
    });
    $(document).on("mouseleave" , "#oneKeyFlush" , function () {
        layer.close(tips);
    });


});

let loadingIndex;
let tips;

