$(function () {
    // 获取url的传值函数
    Request = {
        QueryString : function(item){
            var svalue = location.search.match(new RegExp("[\?\&]" + item + "=([^\&]*)(\&?)","i"));
            return svalue ? svalue[1] : svalue;
        }
    }
    var contractMoney = ""; //合同金额
    var taxationStr = ""; //税费
    var managementRate = ""; // 税率
    var managementFee = ""; //管理费率
    var backMone = 0;
    var theSum = "";
    var num = "";
    var project = Request.QueryString("id");//获取月份
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

       //项目基本信息表
       $.ajax({
            url: '/project/project_id',
            type: "get",
            data:{
                id : project
            },
            dataType: "json",
            success: function (obj) {
                var data = obj.implementBeans;
                num = obj.projectNum;
                managementRate = obj.managementBean.managementRate;
                managementFee = obj.managementBean.managementFee;
                loadData(obj);
                number(obj);
                contract(obj.contractBean.contractId);
                $(".layui-place").val(obj.projectLocationBean.projectLocationName); //项目地点
                $(".layui-company").val(obj.earningsCompanyBean.earningsCompanyName); //收益以单位
                $(".layui-personIncharge").val(obj.managementBean.managementMainHead); //经营负责人
                $(".layui-royalty").val(obj.managementBean.managementCommissionMode); //提成模式
                $(".layui-businessMan").val(obj.managementBean.managementPartnersBean.userBean.userName); //业务人
                $(".layui-state").val(obj.contractBean.contractState); //合同状态
                $(".layui-time").val(obj.contractBean.contractSigningDate); //签订时间
                if(obj.contractBean.contractMoney != ""){
                    $(".layui-money").val(obj.contractBean.contractMoney); //合同金额
                    contractMoney = obj.contractBean.contractMoney;
                }else{
                    $(".layui-money").val(obj.contractBean.contract_estimate_money); //合同金额
                    contractMoney = obj.contractBean.contract_estimate_moneyl;
                }
                var htmlTh = "";
                var ids = [];
                for (var i = 0; i < data.length; i++) {
                     htmlTh +='<tr>'
                     htmlTh +='<td>' + data[i].departmentBean.departmentName + '</td>'
                     htmlTh +='<td>' + data[i].implementImplementHead + '</td>'
                     htmlTh +='<td>' + data[i].projectTypeBean.projectTypeName + '</td>'
                     htmlTh +='<td>' + data[i].secondPartyBean.secondPartyName + '</td>'
                     htmlTh +='<td>' + data[i].implementProgress + '</td>'
                     htmlTh +='<td>' + data[i].projectStatusBean.projectStatusName + '</td>'
                     htmlTh +='<td>' + data[i].implementRecordContent + '</td>'
                     htmlTh += '</tr>'
                     ids.push(data[i].departmentBean.departmentName)
                }
                $(".layui-tbody").append(htmlTh);
                $(".layui-department").val(ids);
            }
       });

       // 获取下载合同地址
      function contract(id){
         $.ajax({
              url: '/material/cont_id',
              type: "get",
              data:{
                  contractId  : id
              },
              dataType: "json",
              success: function (obj) {
                var newName = obj[0].contract_accessory_new_name;
                var href = "/download_file?fileName="+"D://contractFile//" +  newName;
                $(".layui-Buttons").append('<a download="' + newName + '" href="'+href+'" class="layui-a">下载</a>');
              }
         });
      }
      // 回款统计表
      $.ajax({
          url: '/income/project_id',
             type: "get",
             data:{
                 projectId  : project
             },
             dataType: "json",
             success: function (obj) {
                taxationStr = obj;
                var statisticsHtml = "";
                var sum = 0; // 开票总金额
                var back = 0; // 回款总金额
                for (var i = 0; i < obj.length; i++) {
                   var str = obj[i].incomeMoney / contractMoney;
                   var progress = str.toFixed(2); //汇款进度
                   statisticsHtml +='<tr>'
                   statisticsHtml +='<td>' + obj[i].incomeNum + '</td>'
                   statisticsHtml +='<td>' + obj[i].incomeType + '</td>'
                   statisticsHtml +='<td>' + obj[i].invoiceContent + '</td>'
                   statisticsHtml +='<td>' + obj[i].invoiceMoney + '</td>'
                   statisticsHtml +='<td>' + obj[i].incomeMoney + '</td>'
                   statisticsHtml +='<td>' + obj[i].incomeDate + '</td>'
                   statisticsHtml +='<td>' + progress + '</td>'
                   statisticsHtml += '</tr>'
                   sum += obj[i].invoiceMoney;
                   back += obj[i].incomeMoney;
                }
                backMone = back;
                $(".layui-Invoice").val(sum);
                $(".layui-moneyBack").val(back);
                $(".layui-statistics").append(statisticsHtml);
             }
      });

    // 项目成本表
    function number(obj){
      var itemNumber = obj.projectNum;
      var implementBeans = obj.implementBeans;
      $.ajax({
          url: '/implement/cost',
          type: "get",
          data:{
              projectNum : itemNumber
          },
          dataType: "json",
          success: function (obj) {
            var data = Object.values(obj)[0];
            var amount = decimalConversion(data.年总奖金);
            var project = decimalConversion(data.项目花销);
            var administration = decimalConversion(data.管理费);
            var personnelCost = decimalConversion(data.人员成本);
            var technicalCommission = decimalConversion(data.技术提成);
            var equipmentUseFee = decimalConversion(data.设备使用);
            var teamExpenses = decimalConversion(data.班组费);
            var otherCharges = decimalConversion(data.其他费);
            var taxation = decimalConversion(data.税费);
            theSum = Number(amount) + Number(project) + Number(administration) + Number(personnelCost) + Number(technicalCommission) + Number(equipmentUseFee) + Number(teamExpenses) + Number(otherCharges) + Number(taxation);
            $(".layui-annualTotalBonus").val(amount);
            $(".layui-projectExpense").val(project);
            $(".layui-taxation").val(taxation);
            $(".layui-managementExpense").val(administration);
            $(".layui-personnelCost").val(personnelCost);
            $(".layui-technicalCommission").val(technicalCommission);
            $(".layui-equipmentUseFee").val(equipmentUseFee);
            $(".layui-teamExpenses").val(teamExpenses);
            $(".layui-otherCharges").val(otherCharges);
          }
       });
       // 项目花销
       var expensesHtml = "";
       for (var i = 0; i < implementBeans.length; i++) {
           var disburseBeans = implementBeans[i].disburseBeans
            for (var j = 0; j < disburseBeans.length; j++) {
                if(disburseBeans[j].disburseDetailBean.disburseTypeBean.disburseTypeName == "项目花销" ){
                   expensesHtml +='<tr>'
                   expensesHtml +='<td>' + disburseBeans[j].disburseNum + '</td>'
                   expensesHtml +='<td>' + disburseBeans[j].disburseTime + '</td>'
                   expensesHtml +='<td>' + disburseBeans[j].disburseDetailBean.disburseDetailName + '</td>'
                   expensesHtml +='<td>' + disburseBeans[j].disbursePaymentAmount + '</td>'
                   expensesHtml +='<td>' + disburseBeans[j].disburseMode + '</td>'
                   expensesHtml +='<td>' + disburseBeans[j].disburseAffiliation + '</td>'
                   expensesHtml += '</tr>'
                }
            }
       }
       if(expensesHtml == ""){
           $(".layui-expenses").append('<tr><td colspan="6">' +"无数据"+ '</td></tr>');
       }
       $(".layui-expenses").append(expensesHtml);
       $('.orderType button').on('click',function(){
          $(this).addClass("orderType-btn").siblings().removeClass("orderType-btn");
       });

       // 税率
       var taxationHtml = "";
       for (var i = 0; i < taxationStr.length; i++) {
          var taxation = taxationStr[i].incomeMoney * managementRate;
          taxationHtml +='<tr>'
          taxationHtml +='<td>' + taxationStr[i].incomeNum + '</td>'
          taxationHtml +='<td>' + taxationStr[i].incomeDate + '</td>'
          taxationHtml +='<td>' + taxationStr[i].incomeMoney + '</td>'
          taxationHtml +='<td>' + managementRate + '</td>'
          taxationHtml +='<td>' + taxation + '</td>'
          taxationHtml += '</tr>'
       }
       if(taxationHtml == ""){
          $(".layui-Taxation").append('<tr><td colspan="4">' +"无数据"+ '</td></tr>');
       }
       $(".layui-Taxation").append(taxationHtml);

       //管理费率
       var managementfeeHtml = "";
       for (var i = 0; i < taxationStr.length; i++) {
          var managementExpense = taxationStr[i].incomeMoney * managementFee;
          managementfeeHtml +='<tr>'
          managementfeeHtml +='<td>' + taxationStr[i].incomeNum + '</td>'
          managementfeeHtml +='<td>' + taxationStr[i].incomeDate + '</td>'
          managementfeeHtml +='<td>' + taxationStr[i].incomeMoney + '</td>'
          managementfeeHtml +='<td>' + managementFee + '</td>'
          managementfeeHtml +='<td>' + managementExpense + '</td>'
          managementfeeHtml += '</tr>'
       }
       if(managementfeeHtml == ""){
          $(".layui-ManagementExpense").append('<tr><td colspan="4">' +"无数据"+ '</td></tr>');
       }
       $(".layui-ManagementExpense").append(managementfeeHtml);

       // 人员成本
       $.ajax({
             url: '/work_load/commission',
             type: "get",
             data:{
                 id : project
             },
             dataType: "json",
             success: function (obj) {
                  var costHtml = "";
                  var commissionHtml = "";
                  for (var i = 0; i < obj.length; i++) {
                     costHtml +='<tr>'
                     costHtml +='<td>' + obj[i].departmentName + '</td>'
                     costHtml +='<td>' + obj[i].workLoadDate + '</td>'
                     costHtml +='<td>' + obj[i].workLoadDuration + '</td>'
                     costHtml +='<td>' + obj[i].staffName + '</td>'
                     costHtml +='<td>' + obj[i].cost + '</td>'
                     costHtml += '</tr>'
                  }
                  for (var i = 0; i < obj.length; i++) {
                     var technicalCommission = Number(obj[i].workLoadAmountStaff) + Number(obj[i].workLoadAmountManage) + Number(obj[i].workLoadAmountCaptain);
                     var yearendBonus = technicalCommission * 0.3;
                     commissionHtml +='<tr>'
                     commissionHtml +='<td>' + obj[i].departmentName + '</td>'
                     commissionHtml +='<td>' + obj[i].workLoadDate + '</td>'
                     commissionHtml +='<td>' + obj[i].professionName + '</td>'
                     commissionHtml +='<td>' + obj[i].workLoadWorkLoad + '</td>'
                     commissionHtml +='<td>' + obj[i].staffName + '</td>'
                     commissionHtml +='<td>' + obj[i].workLoadPriceStaff + '</td>'
                     commissionHtml +='<td>' + obj[i].workLoadAmountStaff + '</td>'
                     commissionHtml +='<td>' + obj[i].supervisorName + '</td>'
                     commissionHtml +='<td>' + obj[i].workLoadAmountManage + '</td>'
                     commissionHtml +='<td>' + obj[i].captainName + '</td>'
                     commissionHtml +='<td>' + obj[i].workLoadAmountCaptain + '</td>'
                     commissionHtml +='<td>' + technicalCommission + '</td>'
                     commissionHtml +='<td>' + "0.3" + '</td>'
                     commissionHtml +='<td>' + yearendBonus + '</td>'
                     commissionHtml += '</tr>'
                  }
                  if(costHtml == ""){
                    $(".layui-PersonnelCost").append('<tr><td colspan="5">' +"无数据"+ '</td></tr>');
                  }
                  if(commissionHtml == ""){
                    $(".layui-TechnicalCommission").append('<tr><td colspan="14">' +"无数据"+ '</td></tr>');
                    $(".layui-AnnualTotalBonus").append('<tr><td colspan="14">' +"无数据"+ '</td></tr>');
                  }
                  $(".layui-PersonnelCost").append(costHtml);
                  $(".layui-TechnicalCommission").append(commissionHtml);
                  $(".layui-AnnualTotalBonus").append(commissionHtml);
             }
       });
       // 设备使用费
       $.ajax({
             url: '/production_costs/commission',
             type: "get",
             data:{
                 id : project
             },
             dataType: "json",
             success: function (obj) {
                console.log(obj);
               var equipmentHtml = "";
               var teamHtm = "";
               var otherHtm = "";
               for (var i = 0; i < obj.length; i++) {
                  var nun = Number([i]) + 1;
                  if(obj[i].productionCostsDetailType == "设备使用费"){
                      equipmentHtml +='<tr>'
                      equipmentHtml +='<td>' + nun + '</td>'
                      equipmentHtml +='<td>' + obj[i].productionCostsDetailName + '</td>'
                      equipmentHtml +='<td>' + obj[i].productionCostsDay + '</td>'
                      equipmentHtml +='<td>' + obj[i].productionCostsMoney + '</td>'
                      equipmentHtml +='<td>' + obj[i].productionCostsRemark + '</td>'
                      equipmentHtml += '</tr>'
                  }
               }
               for (var i = 0; i < obj.length; i++) {
                  var nun = Number([i]) + 1;
                  if(obj[i].productionCostsDetailType == "班组费"){
                      teamHtm +='<tr>'
                      teamHtm +='<td>' + nun + '</td>'
                      teamHtm +='<td>' + obj[i].productionCostsDetailName + '</td>'
                      teamHtm +='<td>' + obj[i].squadGroupFeeName + '</td>'
                      teamHtm +='<td>' + obj[i].productionCostsMoney + '</td>'
                      teamHtm +='<td>' + obj[i].productionCostsRemark + '</td>'
                      teamHtm += '</tr>'
                  }
               }
               for (var i = 0; i < obj.length; i++) {
                  var nun = Number([i]) + 1;
                  if(obj[i].productionCostsDetailType == "其他费"){
                      otherHtm +='<tr>'
                      otherHtm +='<td>' + nun + '</td>'
                      otherHtm +='<td>' + obj[i].productionCostsDetailName + '</td>'
                      otherHtm +='<td>' + obj[i].productionCostsMoney + '</td>'
                      otherHtm +='<td>' + obj[i].productionCostsRemark + '</td>'
                      otherHtm += '</tr>'
                  }
               }
               if(equipmentHtml == ""){
                 $(".layui-equipment").append('<tr><td colspan="5">' +"无数据"+ '</td></tr>');
               }
               if(teamHtm == ""){
                  $(".layui-Team").append('<tr><td colspan="5">' +"无数据"+ '</td></tr>');
               }
               if(otherHtm == ""){
                  $(".layui-Other").append('<tr><td colspan="4">' +"无数据"+ '</td></tr>');
               }
               $(".layui-equipment").append(equipmentHtml); //设备使用费
               $(".layui-Team").append(teamHtm); //班组费
               $(".layui-Other").append(otherHtm); //其他费
             }
       });
     }

    //  类型按钮监听
    $(".type-btn").click(function(){
        $(".type-form").hide();
        //动态获取ID 进行点击事件
        var typeId = $(this).attr("data-typeId")
        $("#"+typeId).toggle();

    })

     // 回款金额-总成本
     setTimeout(function () {
          var myChart = echarts.init(document.getElementById("mian"));
          var profit = backMone - theSum;
          var profitStr = decimalConversion(profit);
          var contractMoneyStr = decimalConversion(contractMoney);
          var backMoneStr = decimalConversion(backMone);
          var theSumStr = decimalConversion(theSum);
          var newTs = new Date().getDate();
          var newYs = new Date().getMonth()+1;
          var newNs = new Date().getFullYear();
          if(newYs < 10){
              newYs = "0" + newYs
          }
          if(newTs < 10){
             newTs = "0" + newTs
          }
          var tiem = newNs + '/' + newYs + '/' + newTs;
          $(".layui-tiem").text(tiem);
          $(".layui-number").text(num);
          $(".layui-backMone").val(contractMoney);
          $(".layui-contractMoney").val(backMoneStr);
          $(".layui-theSum").val(theSumStr);
          $(".layui-profit").val(profitStr);
          
          option = {
              color: ['#3398DB'],
              tooltip: {
                  trigger: 'axis',
                  axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                      type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                  }
              },
              grid: {
                  left: '3%',
                  right: '4%',
                  bottom: '3%',
                  containLabel: true
              },
              xAxis: [
                  {
                      type: 'category',
                      data: ['合同金额', '回款金额', '总成本(含税)', '当前利润'],
                      axisTick: {
                          alignWithLabel: true
                      }
                  }
              ],
              yAxis: [
                  {
                      type: 'value'
                  }
              ],
            series : [
                {
                  name:'',

                  type:'bar',
                  barWidth: '60%',
                  data: [contractMoney, backMone, theSum, profit],
                  itemStyle:{
                      normal:{
                          color:function(params){
                              if(params.value < 0){
                                  return "#5af158";
                              }else if(params.value > 0 ){
                                  return "#ff0000";
                              }
                              return "#ff0000";
                          }
                      }
                  }
              }
          ]
          };
          myChart.setOption(option);
     }, 2000);

    // 精确两位小数点
    function decimalConversion(str){
        var decimal = str.toFixed(2);
        return decimal;
    }

    // 将后台返回的 json 对象快速填充到表单
    function loadData(jsonStr) {
           var obj = jsonStr;
           var key, value, tagName, type, arr;
           for (var x in obj) {
               key = x;
               value = obj[x];
               $("[name='" + key + "'],[name='" + key + "[]'],[id='" + key + "']").each(function () {
                   tagName = $(this)[0].tagName;
                   type = $(this).attr('type');
                   if (tagName == 'INPUT') {
                       if (type == 'radio') {
                           if ($(this).val() == value) {
                               $(this).attr('checked', true);
                           }
                       } else if (type == 'checkbox') {
                           arr = value.split(',');
                           for (var i = 0; i < arr.length; i++) {
                               if ($(this).val() == arr[i]) {
                                   $(this).attr('checked', true);
                                   break;
                               }
                           }
                       } else {
                           $(this).val(value);
                       }
                   } else if (tagName == 'TEXTAREA') {
                       $(this).val(value);
                   } else if (tagName == 'SELECT') {
                       $(this).val(value);
                       $(this).attr('selected', true);
                   }
                   layui.form.render();
               });
           }
       }
    });
});
