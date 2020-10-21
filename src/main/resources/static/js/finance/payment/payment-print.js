let id = getParamForUrl("id");
let type = getParamForUrl("type");//项目花销 //部门日常//班组费
let broad = getParamForUrl("broad");//大类
let subclass = getParamForUrl("subclass");//小类
$(function () {
    $(".content").attr("data-category", type);
    if(type == "班组费"){
        $(".pay-money-td,.pay-money-th").attr("rowspan","2");
    }
    console.log(type);
    if (type === "部门日常") {
        $(".subclass-money-th,.subclass-money-td").hide();
        $(".innerTable-td").attr("colspan", "6");
        $(".pay-money-th").attr("rowspan", "1");
        $(".pay-money-td").attr("rowspan", "1").attr("colspan", "5");
        $(".content-th").text("内容")
    }
    if (broad === "项目支出") {
        $(".content-th").text("项目名称");
        $(".content-td").attr("data-text", "implementBean.projectBean.projectName");
        $(".department-span").attr("data-text", "implementBean.departmentBean.departmentName");

        $(".all-boss-th").text("项目回款总金额:");
        let addBossTh = $(".add-boss-th");
        addBossTh.text("本次付款后累计"+type+"付款金额：");
        $(".typeName").text("本次付款后累计"+type+"/回款总金额（%）:");
        $.get("/payment/print_paid_boss_cost", {"id": id}, function (data) {
            if(data === null){
                data = 0;
            }
            $(".paidBoss").text(data)
        }, "json");
        // 铺数据
        $.get("/payment/print_income_cost", {"id": id,"type":type}, function (data) {
            $(".all-boss-td").text(data["incomeMoney"].toFixed(2));
            $(".add-boss-td").text(data["paymentMoney"].toFixed(2));
            let incomeMoneyRatioTd = $(".incomeMoneyRatio-td");
            incomeMoneyRatioTd.parent().show();
            /*本次付款后累计业务提成/回款总金额（%）:*/
            let ratio = "0%";
            if(data["paymentMoney"] !== 0 && data["incomeMoney"] !== 0){
                 ratio = ((data["paymentMoney"] / data["incomeMoney"]) * 100).toFixed(2) + "%";
             }

            incomeMoneyRatioTd.text(ratio);
             //$(".pay-money-td").attr("rowspan","3");
          }, "json");
          //小类 的 付款金额模块
          $.get("/payment/print_subclass_income_cost", {"id": id,"type":subclass}, function (data) {
              $(".subclass-money-th").text("本次付款后累计"+subclass+"付款金额：");
              $(".subclass-money-td").text(data["paymentMoney"].toFixed(2));
              $(".subclass-ratio-th").text("本次付款后累计"+subclass+"/回款总金额（%）:");
              let subclassRatio = '0%';
              if(data["paymentMoney"] !== 0 && data["incomeMoney"] !== 0){
                 subclassRatio = ((data["paymentMoney"] / data["incomeMoney"]) * 100).toFixed(2) + "%";
              }
              console.log(subclassRatio);
              $(".subclass-ratio-td").text(subclassRatio);
          }, "json");
    }

    // 铺数据
    $.get("/table_utils/info", {disburseId: id, "table_utils.tableName": "disburse"}, function (data) {
        if(data.content[0].squadGroupFeeBean.squadGroupFeeName != undefined){
            $(".subclass-money-th,.subclass-money-td,.subclass-ratio-th,.subclass-ratio-td").remove();
            $(".pay-money-td").attr("colspan","3");
            $(".pay-money-th,.pay-money-td").attr("rowspan","3");
        }
        showData4Object(data["content"]["0"]);
        convertNum(data["content"]["0"]["disbursePaymentAmount"], "innerTable");
    }, "json");


});

// 转化成大写
function convertNum(num, className) {
    var cnNums = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'];
    var innerTable = document.getElementsByClassName(className)[0]; //获取该table
    var tds = innerTable.firstElementChild.firstElementChild.children; //找到所有td
    //toFixed() 方法可把 Number 四舍五入为指定小数位数的数字。
    if(typeof num === "string"){
        num = 0;
    }
    num = num.toFixed(2);
    //将输入的整形数字先转化成字符型，然后将该字符型反转,此时的结果是一个倒着排序的字符串
    // 5000.00
    var numStr = new Object(num).toString();
    //500000
    numStr = numStr.substring(0, numStr.length - 3) + numStr.substring(numStr.length - 2, numStr.length);
    numStr = numStr.split("").reverse().join("");
    //设置一个变量a,判断numStr这个字符串是否存在"."，如果存在，变量a的值为2，如果不存在。变量a的值为6
    // var a = numStr.indexOf(".") === -1 ? 6 : 2;
    var a = 2;
    for (var i = 0; i < numStr.length; i++) {
        // if (numStr.charAt(i) === ".") {
        //     //如果该字符串拥有"."这个字符，则跳过，继续循环
        //     continue;
        // }
        var char1 = numStr.charAt(i); //这里通过遍历的方法，获得该字符串除了"."之外的所有字符
        var index = parseInt(char1); //该字符转化成整形
        tds[tds.length - a].innerText = cnNums[index]; // 将td的值根据下标的不同填入不同的数字
        a = a + 2;
    }
}
