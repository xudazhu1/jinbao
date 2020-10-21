$("a[id^=aRangeProj]").on("click", function(){
  $("a[id^=aRangeProj] span").addClass("layui-bg-gray");
  $(this).find("span").removeClass("layui-bg-gray");
  range = $(this).data('range');
  getFirstRowData("proj", range)
})

$("a[id^=aRangeContract]").on("click", function(){
  $("a[id^=aRangeContract] span").addClass("layui-bg-gray");
  $(this).find("span").removeClass("layui-bg-gray");
  range = $(this).data('range');
  getFirstRowData("contract", range)
})

$("a[id^=aRangeReturn]").on("click", function(){
  $("a[id^=aRangeReturn] span").addClass("layui-bg-gray");
  $(this).find("span").removeClass("layui-bg-gray");
  range = $(this).data('range');
  getFirstRowData("return", range)
})

$("a[id^=aRangeCost]").on("click", function(){
  $("a[id^=aRangeCost] span").addClass("layui-bg-gray");
  $(this).find("span").removeClass("layui-bg-gray");
  range = $(this).data('range');
  getFirstRowData("cost", range)
})

//立项
var projectChart = echarts.init(document.getElementById('project'));
var option = {
  legend: {},
  tooltip: {},
  dataset: {
    dimensions: [],
    source: []
  },
  dataZoom: [
    { // 这个dataZoom组件，默认控制x轴。
      type: 'slider', // 这个 dataZoom 组件是 slider 型 dataZoom 组件
      start: 0,      // 左边在 0% 的位置。
      end: 25         // 右边在 25% 的位置。
    },
    {
      type: 'inside',
      start: 0,
      end: 25
    }
  ],
  xAxis: {type: 'category'},
  yAxis: {},
  series: []
};
projectChart.setOption(option);

//合同
var contractChart = echarts.init(document.getElementById('contract'));
var option = {
  legend: {},
  tooltip: {},
  dataset: {
    dimensions: [],
    source: []
  },
  dataZoom: [
    {
      type: 'slider',
      start: 0,
      end: 25
    },
    {
      type: 'inside',
      start: 0,
      end: 25
    }
  ],
  xAxis: {type: 'category'},
  yAxis: {},
  series: []
};
contractChart.setOption(option);

//项目状态
var projectStatusChart = echarts.init(document.getElementById('projectStatus'));
var option = {
  tooltip: {
    trigger: "axis",
  },
  legend: {
  },
  xAxis: {},
  yAxis: {
    data: []
  },
  series: []
};
projectStatusChart.setOption(option);

//合同状态
var contractStatusChart = echarts.init(document.getElementById('contractStatus'));
var option = {
  legend: {},
  tooltip: {
    trigger: 'item',
    formatter: "{a} <br/>{b} : {c} ({d}%)"
  },
  series : [
    {
      name: '合同状态',
      type: 'pie',
      radius: '70%',
      center: ['50%', '50%'],
      data:[]
    }
  ]
};
contractStatusChart.setOption(option);

function getFirstRowData(type, range){
  // 第一排数据
  $.ajax({
    url: "/statistic/count",
    type: "GET",
    dataType: "json",
    success: function(data){
      if (type == "all" || type == "proj"){
        $("#projNum").text(data["项目立项"][range][0]);
        $("#projGrow").html(data["项目立项"][range][1]);
      }
      if (type == "all" || type == "contract"){
        $("#contractNum").text(data["合同签订"][range][0]);
        $("#contractGrow").html(data["合同签订"][range][1]);
      }
      if (type == "all" || type == "return"){
        $("#returnNum").text(data["回款笔数"][range][0]);
        $("#returnGrow").html(data["回款笔数"][range][1]);
      }
      if (type == "all" || type == "cost"){
        // alert(data["项目费用"][range][1])
        $("#projCost").text(data["项目费用"][range][0]);
        $("#projCostGrow").text(data["项目费用"][range][1]);
        $("#departCost").text(data["部门费用"][range][0]);
        $("#departCostGrow").text(data["部门费用"][range][1]);
        $("#compCost").text(data["公司费用"][range][0]);
        $("#compCostGrow").text(data["公司费用"][range][1]);
      }
    }
  })
}

function getAllData(){

  getFirstRowData("all", "week");
  // 获取项目立项及合同签订信息
  $.ajax({
    url: "/statistic/statistics_by_time",
    type: "GET",
    dataType: "json",
    success: function(data){
      length = data['部门分组'].length - 1
      var series = []
      for(i=0;i<length;i++){
        series.push({type: 'bar'})
      };
      projectChart.setOption({
        dataset: {
          dimensions: data['部门分组'],
          source: data['项目立项'],
        },
        series: series
      });
      contractChart.setOption({
        dataset: {
          dimensions: data['部门分组'],
          source: data['合同签订'],
        },
        series: series
      })
    }
  });

  // 获取项目状态及合同状态信息
  $.ajax({
    url: "/statistic/statistics_by_status",
    type: "GET",
    dataType: "json",
    success: function(data){
      projstatus = data['项目状态']
      series = []
      for (var status in projstatus){
        series.push({
          name: status,
          type: 'bar',
          stack:'状态',
          data: projstatus[status],
        })
      }
      projectStatusChart.setOption({
        yAxis:{
          data: data['部门分组'],
        },
        series: series
      });
      contractStatusChart.setOption({
        series: [
          {
            data: data['合同状态']
          }
        ]
      });
    },
  })
}

$(document).ready(
  getAllData()
)
