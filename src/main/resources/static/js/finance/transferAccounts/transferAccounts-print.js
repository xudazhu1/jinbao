let id = getParamForUrl("id");
$(function () {
    if (id !== null) {
        $.get("/table_utils/info", {transferAccountsId: id, "table_utils.tableName": "transfer_accounts"}, function (data) {
            console.log(data["content"]["0"]);
            // 铺数据
            showData4Object(data["content"]["0"]);
            convertNum(data["content"]["0"]["transferAccountsMoney"], "innerTable");
            // 日期格式转换
            let transferAccountsDate = $(".transferAccountsDate");
            let tempVal = transferAccountsDate.text();
            tempVal = tempVal.substring(0,10);
            transferAccountsDate.text(tempVal);
        }, "json")
    }

    $(".print-btn").on('click', function () {
        $(".print-div").print({
            globalStyles: true,//是否包含父文档的样式，默认为true
            stylesheet: null,//外部样式表的URL地址，默认为null
        });
    });
});
// 转化成大写
function convertNum(num, className) {
    var cnNums = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'];
    var innerTable = document.getElementsByClassName(className)[0]; //获取该table
    var tds = innerTable.firstElementChild.firstElementChild.children; //找到所有td
    //toFixed() 方法可把 Number 四舍五入为指定小数位数的数字。
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