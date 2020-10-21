$(function () {
    // 获取url的传值函数
    Request = {
        QueryString : function(item){
            var svalue = location.search.match(new RegExp("[\?\&]" + item + "=([^\&]*)(\&?)","i"));
            return svalue ? svalue[1] : svalue;
        }
    }

    var squadGroupFee = Request.QueryString("squadGroupFeeId");//获取
    var implement = Request.QueryString("implementId");//获取
    var title = Request.QueryString("title");//获取
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
        if(title == "1"){
           $("#layui-copeWith").show();
           // 获取合计应付
          $.ajax({
              url: '/squad_group_fee/team_fee_cost',
              type: "get",
              data:{
                  squadGroupFeeId : squadGroupFee,
                  implementId : implement
              },
              dataType: "json",
              success: function (obj) {
                   var copewithHtm = "";
                   for (var i = 0; i < obj.length; i++) {
                      var nun = Number([i]) + 1;
                      copewithHtm +='<tr>'
                      copewithHtm +='<td>' + nun + '</td>'
                      copewithHtm +='<td>' + obj[i].productionCostsDetailName + '</td>'
                      copewithHtm +='<td>' + obj[i].squadGroupFeeName + '</td>'
                      copewithHtm +='<td>' + obj[i].productionCostsMoney + '</td>'
                      copewithHtm +='<td>' + obj[i].productionCostsRemark + '</td>'
                      copewithHtm += '</tr>'
                   }
                   if(copewithHtm == ""){
                      $(".layui-copeWith").append('<tr><td colspan="5">' +"无数据"+ '</td></tr>');
                   }
                   $(".layui-copeWith").append(copewithHtm); //应付合计
              }
          });
        }else if(title == "2"){
            $("#layui-paid").show();
            // 获取已付
             $.ajax({
                  url: '/squad_group_fee/team_fee_pay',
                  type: "get",
                  data:{
                      squadGroupFeeId : squadGroupFee,
                      implementId : implement
                  },
                  dataType: "json",
                  success: function (obj) {
                       console.log(obj);
                       var paidHtml = "";
                       for (var j = 0; j < obj.length; j++) {
                           paidHtml +='<tr>'
                           paidHtml +='<td>' + obj[j].disburseNum + '</td>'
                           paidHtml +='<td>' + obj[j].disburseTime + '</td>'
                           paidHtml +='<td>' + obj[j].disburseDetailName + '</td>'
                           paidHtml +='<td>' + obj[j].disbursePaymentAmount + '</td>'
                           paidHtml +='<td>' + obj[j].disburseMode + '</td>'
                           paidHtml +='<td>' + obj[j].disburseAffiliation + '</td>'
                           paidHtml += '</tr>'
                       }
                       if(paidHtml == ""){
                           $(".layui-paid").append('<tr><td colspan="6">' +"无数据"+ '</td></tr>');
                       }
                       $(".layui-paid").append(paidHtml);
                  }
             });
        }
    });
});