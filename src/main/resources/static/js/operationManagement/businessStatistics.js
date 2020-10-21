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

let customWhere = " projectBeanXXXmanagementBeanXXXmanagementPartnersBeanXXXuserBean.userId = -1  ";
var tableData ;
layui.use(['form','table','soulTable'], function(){
    var table = layui.table;
    var soulTable = layui.soulTable;

    try {
        //准备业务人条件
        if ( window.top.userTemp !== null ) {
            //是否是管理员
            let superManager = false;
            $( window.top.userTemp["roleBeans"] ).each( function ( index , role ) {
                if ( role.roleName === "超级管理员" ) {
                    // superManager = true;
                    superManager = false;
                    return false;
                }

            } );
            if ( superManager ) {
                customWhere = " 1= 1 ";
            } else  {
                customWhere = " projectBeanXXXmanagementBeanXXXmanagementPartnersBeanXXXuserBean.userId =  " + window.top.userTemp.userId;
            }
        }
    } catch (e) {
    }

    //方法级渲染
    table.render( tableData = {
        elem: '#myTable',
        url: '/business_commission/all',
        method: 'post', //由于条件可能过多 超过url的字符上限 采用post
        toolbar: '#toolbarDemo',
        limit : 50,
        id : 'myTable1',
        limits : [50,100,150 , 9999 ] ,
        height: $(document).height() - $('#myTable').offset().top - 50,
        page: true,
        where : {
            selectFields : //要查询的数据(字段) 使用hql (类似sql) 语法
                " projectBean.projectId as 项目ID " +
                "$projectBean.projectNum as 项目编号 " +
                "$projectBean.projectName as 项目名称 " +
                "$group_concat( projectBeanXXXimplementBeansXXXdepartmentBean.departmentName , '' ) as 部门 " +
                "$projectBean.earningsCompanyBean.earningsCompanyName as 收益单位 " +
                "$projectBeanXXXmanagementBean.managementCommissionMode as 项目类型 " +
                "$projectBeanXXXmanagementBeanXXXmanagementPartnersBeanXXXuserBean.userName as 人员名称 " +
                "$projectBeanXXXmanagementBeanXXXmanagementPartnersBean.managementPartnersIdentity as 身份 " +
                "$( case when projectBeanXXXcontractBean.contractMoney is null or projectBeanXXXcontractBean.contractMoney = 0 then  ( case when projectBeanXXXcontractBean.contractEstimateMoney is null then 0 else projectBeanXXXcontractBean.contractEstimateMoney end ) else projectBeanXXXcontractBean.contractMoney end ) as 合同金额 " +
                "$ ( case when projectBeanXXXmanagementBean.managementCorporateProfits is null then 0 else projectBeanXXXmanagementBean.managementCorporateProfits end ) as 公司利润" +
                "$ ( case when projectBeanXXXmanagementBean.managementContractFee is null then 0 else projectBeanXXXmanagementBean.managementContractFee end )  as 承包费" +
                "$ ( case when projectBeanXXXmanagementBean.managementRate is null then 0 else projectBeanXXXmanagementBean.managementRate end ) as 税率" +
                // "$ group_concat( ( case when projectBeanXXXimplementBeans.projectStatusBean.projectStatusId in ( 6 , 7 , 8  ) then '纸质档提交' else '干活中' end ) , '$' ) as 项目状态" +
                "$ ( case when  group_concat( ( case when projectBeanXXXimplementBeansXXXprojectStatusBean.projectStatusId in ( 6 , 7 , 8  ) then '纸质档提交' else '干活中' end ) , '$' ) like '%干活中%' then '干活中' else '纸质档提交' end ) as 项目状态" +
                "$projectBean.projectBelongsType as 所属类型"
            ,joins :
                'managementBean.managementPartnersBean.userBean' +
                '$contractBean' +
                '$earningsCompanyBean' +
                '$implementBeans.projectStatusBean' + // 上面 selectFields 所用到的除了本表外的bean路径
                '$implementBeans.departmentBean' , // 上面 selectFields 所用到的除了本表外的bean路径
            tableName : "project" //表名
            , groupBy : "projectBean.projectId" //分组字段 多个以$分割
            , globalSearch : "" //全局搜索
            , customWhere : customWhere
        }
        ,totalRow: true
        ,cols: [[
            {field: '项目ID', title: 'ID', hide: true , filter: true}
            ,{field: '项目编号', title: '项目编号', filter: true ,event: 'mastrtStatus'}
            ,{field: '项目名称', title: '项目名称', filter: true}
            ,{field: '部门', title: '部门', filter: true}
            ,{field: '收益单位', title: '收益单位', filter: true}
            , {field: '项目类型', title: '提成模式', filter: true}
            , {field: '人员名称', title: '业务人', filter: true}
            , {field: '身份', title: '业务人身份', filter: true , hide : true }
            , {field: '合同金额', title: '合同金额含概算', filter: true}
            , {field: '回款金额', title: '回款金额', filter: false}
            , {field: '等级', title: '等级', filter: false, hide : true }
            , {field: '公司利润比', title: '公司利润比', filter: false, hide : true }
            , {field: '公司利润', title: '公司利润', filter: true, hide : true }
            , {field: '承包费', title: '承包费', filter: true, hide : true }
            , {field: '税率', title: '税率', filter: true, hide : true }
            , {field: '税费', title: '税费', filter: false, hide : true }
            , {field: '项目状态', title: '项目状态', filter: true}
            , {field: '所属类型', title: '所属类型', filter: true}
            , {field: '项目花销', title: '项目花销', filter: false, hide : true }
            , {field: '管理费', title: '管理费', filter: false, hide : true }
            , {field: '人员成本', title: '人员成本', filter: false, hide : true }
            , {field: '人工天数', title: '人工天数', filter: false, hide : true }
            , {field: '技术提成', title: '技术提成', filter: false, hide : true }
            , {field: '年总奖金', title: '年总奖金', filter: false, hide : true }
            , {field: '设备使用费', title: '设备使用费', filter: false, hide : true }
            , {field: '班组费', title: '班组费', filter: false, hide : true }
            , {field: '其他费', title: '其他费', filter: false, hide : true }
            , {field: '施工亏损费', title: '施工亏损费', filter: false, hide : true }
            , {field: '总成本(含税)', title: '总成本(含税)', filter: false}
            , {field: '预计剩余成本', title: '预计剩余成本', filter: false}
            , {field: '项目利润', title: '项目利润', filter: false, hide : true}
            , {field: '预计公司利润(扣除前)', title: '预计公司利润(扣除前)', filter: false, hide : true}
            , {field: '预计公司利润(扣除后)', title: '预计公司利润(扣除后)', filter: false, hide : true}
            , {field: '预计业务提成(扣除前)', title: '预计业务提成(扣除前)', filter: false, hide : true}
            , {field: '预计业务提成(扣除后)', title: '预计业务提成(扣除后)', filter: false}
            , {field: '预支业务提成', title: '预支业务提成', filter: false, totalRow: true}
            , {field: '实付业务提成', title: '实付业务提成', filter: false, totalRow: true}
            , {field: '未付业务提成', title: '未付业务提成', filter: false, totalRow: true}
            // , {fixed: 'right', title: '操作', toolbar: '#barDemo', width: 120}
        ]]
        ,filter: {
            items:['data','condition' , 'clearCache']
            , cache : true// 只显示
        },
        text: { // 更改没有数据时的文字
           none: '本用户无数据，请登录正确用户' //默认：无数据。注：该属性为 layui 2.2.5 开始新增
        }
        ,parseData: function(res){ //res 即为原始返回的数据
            return {
                "code": 0, //解析接口状态
                "msg": "msg", //解析提示文本
                "count": res["totalElements"], //解析数据长度
                "data": formatContent( res.content ) //解析数据列表
            };
        }
        ,done: function(){
            //加载完之后回调
            soulTable.render(this); // 调用筛选方法
        }

    } );

   // 4.监听链接跳转
   table.on('tool(myTable)', function(obj){ //test为你table的lay-filter对应的值
       var data = obj.data;
       var id = data.项目ID;
       if(obj.event === 'mastrtStatus'){
            var layerTitle = '项目信息详情';
            var href = "../operationManagement/projectInformation.html";
            editInformation(id,href,layerTitle)
       }
    });

    // 编辑页面跳转
    function editInformation(id,href,layerTitle){
        layer.ready(function () {
            var index = layer.open({
                type: 2,
                title: layerTitle,
                area: ["100%","100%"],
                content: href+'?id='+id,
                end: function(){
                    $(".layui-laypage-btn").click(); //当前表格刷新
                }
            });
            layer.full(index); // 弹出全屏
        });
    }

    function formatContent( content ) {
        $( content ).each( function ( index , obj ) {
            obj["税费"] = obj["税费"].toFixed(2);
            obj["总成本(含税)"] = obj["总成本(含税)"].toFixed(2);
            obj["预计剩余成本"] = obj["预计剩余成本"].toFixed(2);
            obj["预计公司利润(扣除前)"] = obj["预计公司利润(扣除前)"].toFixed(2);
            obj["预计公司利润(扣除后)"] = obj["预计公司利润(扣除后)"].toFixed(2);
            obj["预计业务提成(扣除前)"] = obj["预计业务提成(扣除前)"].toFixed(2);
            obj["预计业务提成(扣除后)"] = obj["预计业务提成(扣除后)"].toFixed(2);
            obj["预支业务提成"] = obj["预支业务提成"].toFixed(2);
            obj["实付业务提成"] = obj["实付业务提成"].toFixed(2);
            obj["未付业务提成"] = obj["未付业务提成"].toFixed(2);
        });
        return content;

    }



    //监听工具条
    table.on('tool', function(obj){
        var data = obj.data;
        if(obj.event === 'edit'){
            layer.msg('ID：'+ data.incomeId + ' 的查看操作');
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

    //全局搜索
    $(document).on( "change" , "#globalSearch-input" , function () {
        tableData.where.globalSearch = $(this).val();
    } );
    //全局搜索
    $(document).on( "click" , "#globalSearch" , function () {
        $(".layui-laypage-btn").click();
    } );



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

    // // 监听查询搜索按钮
    // $('.layui-inline .layui-btn').on('click', function(){
    //     var type = $(this).data('type');
    //     active[type] ? active[type].call(this) : '';
    // });
    //
    // // 监听重置按钮
    // $('#reset').on('click', function(){
    //     $('input[name="roomNum"]').val("");//获取输入框的值
    //     location.reload(); //关闭回调 刷新
    // });





});

var r = {route : "adw" };
console.log(r.route);

// function getData( pageNum , pageSize ) {
//     $.get("/business_commission/all" , {pageNum : pageNum , pageSize : pageSize } , function (data) {
//         dataTemp = data.content;
//         let tBody = $(".no-copy-show-altrowstable tbody");
//         tBody.empty();
//         $( data.content ).each(function ( index , project ) {
//             // console.log(project["项目编号"]);
//             tBody.append(
//                 "<tr>" +
//                     "<td>"+project["项目编号"]+"</td>" +
//                     "<td>"+project["项目名称"]+"</td>" +
//                     "<td>"+project["项目类型"]+"</td>" +
//                     "<td>"+project["人员名称"]+"</td>" +
//                     "<td>"+project["身份"]+"</td>" +
//                     "<td>"+( project["推荐人"] === undefined ? "" : project["推荐人"] )+"</td>" +
//                     "<td>"+( project["合同金额"] === undefined ? "0" : project["合同金额"] )+"</td>" +
//                     "<td>"+( project["回款金额"] === undefined ? "0" : project["回款金额"] )+"</td>" +
//                     "<td>"+( project["等级"] === undefined ? "" : project["等级"] )+"</td>" +
//                     "<td>"+( project["公司利润比"] === undefined ? "" : project["公司利润比"] )+"</td>" +
//                     "<td>"+project["合作费"].toFixed(2)+"</td>" +
//                     "<td>"+project["公司利润"].toFixed(2)+"</td>" +
//                     "<td>"+project["承包费"].toFixed(2)+"</td>" +
//                     "<td>"+project["税费"].toFixed(2)+"</td>" +
//                     "<td>"+project["项目花销"].toFixed(2)+"</td>" +
//                     "<td>"+project["管理费"].toFixed(2)+"</td>" +
//                     "<td>"+project["人员成本"].toFixed(2)+"</td>" +
//                     "<td>"+project["技术提成"].toFixed(2)+"</td>" +
//                     "<td>"+project["年总奖金"].toFixed(2)+"</td>" +
//                     "<td>"+project["设备使用费"].toFixed(2)+"</td>" +
//                     "<td>"+project["班组费"].toFixed(2)+"</td>" +
//                     "<td>"+project["施工亏损费"].toFixed(2)+"</td>" +
//                     "<td>"+project["项目利润"].toFixed(2)+"</td>" +
//                     "<td>"+project["预计公司利润(扣除前)"].toFixed(2)+"</td>" +
//                     "<td>"+project["预计公司利润(扣除后)"].toFixed(2)+"</td>" +
//                     "<td>"+project["预计业务提成(扣除前)"].toFixed(2)+"</td>" +
//                     "<td>"+project["预计业务提成(扣除后)"].toFixed(2)+"</td>" +
//                     "<td>"+project["预支业务提成"].toFixed(2)+"</td>" +
//                     "<td>"+project["实付业务提成"].toFixed(2)+"</td>" +
//                 "</tr>"
//             );
//         });
//         let tableTemp = $(".no-copy-show-altrowstable");
//         //修复JCLRgrips的left定位和高度
//         $(".JCLRgrips div.JCLRgrip").each(function (index, JCLRgrip) {
//             $(JCLRgrip).height( tableTemp.height() );
//         });
//         showPageButtuns({
//             "pageNum": data.number + 1,
//             "countPage": data["totalPages"],
//             "pageSize": data["size"],
//             "countNum": data["totalElements"]
//         }, $("#table-utils-page-div"), getData );
//         fixFirstColumnData();
//     } , "json" );
//
// }
//
// let dataTemp;
// $(function () {
//     getData( 1, 10 );
//     tableResizable( $(".no-copy-show-altrowstable")  );
// });