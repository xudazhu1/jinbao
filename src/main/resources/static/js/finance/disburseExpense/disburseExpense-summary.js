$(function () {
    // 获取url的传值函数
    Request = {
        QueryString : function(item){
            var svalue = location.search.match(new RegExp("[\?\&]" + item + "=([^\&]*)(\&?)","i"));
            return svalue ? svalue[1] : svalue;
        }
    }
    var month = Request.QueryString("month");//获取月份
    var name = window.top.userTemp.userName; //获取姓名
    var year = month.substring(0,4); //年
    var strMonth = month.substring(month.length-2); // 月
    $(".layui-year").text(year);
    $(".layui-month").text(strMonth);
    var params = {
        "date":month, //酒店ID,
        "userName":name, //公安类型
    }
    // 获取接口数据
    $.ajax({
        url: '/expense_account/statistics4month_and_user',
        type: "get",
        data:params,
        dataType: "json",
        success: function (obj) {
            var data = obj.data;
            var lastMonth = obj.lastMonth
            var LastMonth = lastMonth.toFixed(2)
            var theMonth = obj.theMonth
            var TheMonth = theMonth.toFixed(2)
            var borrowing = obj.borrowing
            var Borrowing = borrowing.toFixed(2)

            $(".reimbursement-person").html(name)
            $(".reimbursement-month").html(month)
            $(".last-months-balance").html(LastMonth);
            $(".borrowing").html(Borrowing)
            $(".balance-ofthe-month").html(TheMonth)
            loadData(data);
        }
    });
    function loadData(data){
        layui.use(['form','table'], function(){
            var table = layui.table;
            soulTable = layui.soulTable;
            //方法级渲染
            var ins1 = table.render({
                elem: '#LAY_table_user'
                ,method: 'get'
                ,data:data
                //,toolbar: '#toolbarDemo'
                ,title: '汇总信息'
                //,even: true //行之间颜色区分
                //,id: 'testReload'
                ,page: false  //开启分页
                ,limit: 20 //每页展示多少条
                ,limits: [10,20,30,40,50] //自定义
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
                    ,{field:'department', title: '归属部门', align:'center'}
                    ,{field:'content', title: '内容', align:'center'}
                    ,{field:'expenseMoney', title:'报销金额',totalRow: true, align:'center',templet : function(d){
                        var expenseMoney = d.expenseMoney;
                        var Expense = expenseMoney.toFixed(2);
                        return Expense;
                    }}
                     ,{field:'invoiceMoney', title:'发票金额',totalRow: true, align:'center',templet : function(d){
                         var invoiceMoney = d.invoiceMoney;
                         var Invoice = invoiceMoney.toFixed(2);
                         return Invoice;
                     }}
                    ,{field:'earningsCompany', title: '收益单位', align:'center'}
                ]]
                ,done: function(){
                    //加载完之后回调
                }
                ,response: {
                    statusCode: 200 //规定成功的状态码，默认：0
                }
                ,request: { // 更改页数参数名
                    pageName: 'pageNum' //页码的参数名称，默认：page
                    ,limitName: 'pageSize' //每页数据量的参数名，默认：limit
                }
            });

        });
    }

    // 打印表格
    $(".print-btn").on('click', function () {
        $("#LAY_table_user").next().find(".layui-table-box").find(".layui-table-header").find("thead").find("th").find('div').css("width", "164");
        $("#LAY_table_user").next().find("tbody").find("tr").find('td').find('div').css("width", "164");
        $("#layui-Summary").print({
            globalStyles: true,
            mediaPrint: true,
        })
    });
});
