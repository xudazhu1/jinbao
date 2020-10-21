var pageDataA;
var functionObjectA;

//铺分页按钮的函数
// pageData         格式为{"pageNum":1 , "countPage": 22 ,"pageSize" : 10 ,  "countNum": 219 }
// domJQuery        把分页按钮铺到此元素中
// functionObject   铺单页数据的回调函数 函数格式 functionName(pageNum , pageSize)
function showPageButtuns(pageData, domJQuery, functionObject) {
    domJQuery.addClass("page_div");
    pageDataA = pageData;
    functionObjectA = functionObject;
    domJQuery.empty();
    domJQuery.append(
        "<button class='btn btn-xs btn-default startsPage firstPage' type='button'>首页</button>");
    domJQuery.append(
        "<button class='btn btn-xs btn-default lastPage firstPage' type='button'>上一页</button>");
    for (var int = 1; int <= pageData["countPage"]; int++) {
        if (int >= pageData["pageNum"] - 2 && int <= pageData["pageNum"] + 2) {
            domJQuery.append("<button class='button" + int
                + " pageButton btn btn-xs  btn-default  ' type='button'>"
                + int + "</button>");
        }
    }
    domJQuery.append(
        "<button class='btn btn-xs  btn-default nextPage firstPage' type='button'>下一页</button>"
        + "<button class='btn btn-xs  btn-default endPage firstPage' type='button'>末页</button>"
        + "<span class=' countPage totalPages'  >共 <span>" + pageData["countPage"] + "</span> 页  </span>"
        + "<span class=' countPage totalPages'  >共 <span>" + pageData["countNum"] + "</span> 条数据&nbsp&nbsp&nbsp </span>"
        + "<input type='number' style='width: 40px;' class='btn btn-xs  btn-default inputPage' id='inputPage' value='" + pageData["pageNum"] + "'  >"
        + "<button  class='btn btn-xs  btn-default inputPageButton firstPage' type='button' >Go!</button>"
        // + "<input style='width: 60px' type='number' class=' pageSize firstPage' value='"+pageData["pageSize"]+"'>"
        + "<select style='width: 90px' type='number' class=' pageSize firstPage'>" +
            "<option value='10'>每页10条</option>" +
            "<option value='20'>每页20条</option>" +
            "<option value='50'>每页50条</option>" +
            "<option value='3000'>每页3000条</option>" +
        "</select>"
    );
    domJQuery.find(".pageSize").val( pageData["pageSize"] );

    $(".button" + pageData["pageNum"]).css("background", "#0073A9").css("color", "#FFFFFF");

    //给每个td设置title值
    $("tbody tr td").each(function(index , tdTemp){
        // noinspection JSValidateTypes
        if ( $(tdTemp).children().length === 0 ) {
            $(tdTemp).prop("title" , $(tdTemp).text());
        }

    });
    firstLineFixed();
}

//首行固定
function firstLineFixed() {
    // //手动设置css 因为同为类选择器 有覆盖不掉的情况 需手动设置
    // // $(".project-show").css("overflow" , "auto").css("height" , "530px").css("float" , "inherit");
    // $(".project-show").css("overflow", "auto").css("float", "inherit");
    // $(".show-altrowstable").css("margin-top", "0");
    //
    // var copy_first_line = $(".copy-first-line");
    // copy_first_line.each(function () {
    //     if ($(this).css("display") !== "none") {
    //         copy_first_line = $(this);
    //     }
    // });
    //
    // // var thead1 = $(".no-copy-show-altrowstable thead");
    // var showTable = $(".no-copy-show-altrowstable");
    // showTable.each(function () {
    //     if ($(this).css("display") !== "none") {
    //         showTable = $(this);
    //     }
    // });
    //
    // var thread1 = showTable.find("thead");
    // var thraed1Th = thread1.find("th");
    // // console.log(thread1)
    // // 清空待复制的table
    // copy_first_line.empty();
    // //往待复制table里复制首行元素
    // thread1.clone(true).appendTo(copy_first_line);
    //给复制体宽度
    // copy_first_line.width(thread1.width() );
    //给每个th标签指定宽度
    // copy_first_line.find("th").each(function (index, th) {
    //     $(th).width($(thraed1Th.get(index)).width() );
    // });
}

// $(function () {
//     //铺完数据调用方法来固定
//     fixed(3 , 0);
//
// });

// Num 要固定的列数 , column如果有多个tbody 要固定每个tbody前几个tr(0为全部)
function fixed(Num, column) {
    //手动设置css 因为同为类选择器 有覆盖不掉的情况 需手动设置
    // $(".project-show").css("overflow", "auto").css("height", "530px").css("float", "inherit");
    $(".show-altrowstable").css("margin-top", "0");
    var copy_first_line = $(".copy-first-line");
    copy_first_line.each(function () {
        if ($(this).css("display") !== "none") {
            copy_first_line = $(this);
        }
    });
    // var thead1 = $(".no-copy-show-altrowstable thead");
    var showTable = $(".no-copy-show-altrowstable");
    showTable.each(function () {
        if ($(this).css("display") !== "none") {
            showTable = $(this);
        }
    });

    var copy_first_column_th = $(".copy-first-column-th");
    var copy_first_column = $(".copy-first-column");


    var thread1 = showTable.find("thead");
    var thraed1Th = thread1.find("th");


    copy_first_column.empty();
    copy_first_column_th.empty();
    copy_first_column.append("<thead><tr></tr></thead>");
    copy_first_column_th.append("<thead><tr></tr></thead>");
    showTable.find("tbody").each(function (index, c) {
        copy_first_column.append("<tbody id='tbody" + index + "'></tbody>");
        $("#tbody" + index).width($(c).width());
        // $("#tbody"+ index).height($(c).height());
    });
    // copy_first_column.append("<tbody></tbody>");
    //动态添加th
    thread1.find("th").each(function (index, thTemp) {
        if (index < Num) {
            $(thTemp).clone(true).appendTo(copy_first_column.find("thead").find("tr"));
            $(thTemp).clone(true).appendTo(copy_first_column_th.find("thead").find("tr"));

        }
    });

    copy_first_column_th.css("margin-top", "-" + thread1.height() + "px");
    copy_first_column.css("margin-top", "-4px");
    copy_first_line.css("margin-top", "-" + thread1.height() + "px");


    // console.log(thread1);
    // 清空待复制的table
    copy_first_line.empty();
    //往待复制table里复制首行元素
    thread1.clone(true).appendTo(copy_first_line);
    //给复制体宽度
    copy_first_line.width(thread1.width());
    // copy_first_line.width(thread1.width() + 1);
    //给每个th标签指定宽度
    //记录宽度总和
    var countWidth = 0;
    copy_first_line.find("th").each(function (index, th) {
        var widthTemp = $(thraed1Th.get(index)).width();
        // var widthTemp = $(thraed1Th.get(index)).width() + 1;
        if (index < Num) {
            countWidth += widthTemp;
            $(copy_first_column_th.find("th").get(index)).width(widthTemp);
            $(copy_first_column.find("th").get(index)).width(widthTemp);
        }
        $(th).width(widthTemp);
    });
    //赋值宽度
    copy_first_column_th.width(countWidth);
    copy_first_column.width(countWidth);


    // copy_first_column_tbody.empty();
    // var copy_first_column_tbody = copy_first_column.find("tbody");
    // console.log("  找不到 ?  " + copy_first_column_tbody)
    var no_copy_table_body = showTable.find("tbody");
    no_copy_table_body.each(function (TBodyIndex, TBody) {
        var trTemp = $(TBody).find("tr");
        trTemp.each(function (index, no_copy_tr) {
            if (index < column || column === 0) {
                var heightTemp = $(no_copy_tr).height();
                // $(tr).height(heightTemp);
                // console.log("no_copy_tr ==> " + index)
                var idTemp = "copy_colum" + index;
                // console.log( "See => " + TBodyIndex , idTemp)
                $("#tbody" + TBodyIndex).append("<tr id='" + idTemp + TBodyIndex + "'></tr>");
                if (column === 0) {
                    // $("#" + idTemp + TBodyIndex).height(heightTemp);
                }


                // thread1.clone(true).appendTo(copy_first_line);
                // 复制前三个td
                $(no_copy_tr).find("td").each(function (indexTd, td) {
                    if (indexTd < Num) {
                        var copy_TBodyTemp = $("#" + idTemp + TBodyIndex);
                        $(td).clone(true).appendTo(copy_TBodyTemp);
                        $(copy_TBodyTemp.find("td").get(indexTd)).height($(td).height())
                    }
                });
            }

        });
    });

}

//封装多线程工具(调用workers.js)

function workers_utils(method, object, function_a) {
    var worker = new Worker("/js/util/workers.js"); //创建一个Worker对象并向它传递将在新线程中执行的脚本的URL
    worker.postMessage({"method": method, "object": object});
    worker.onmessage = function_a;//接收worker传过来的数据函数
    // worker.terminate();
    return worker;
}


$(function () {


    //当子页面发生尺寸改变时  自动设置iframe高度
    $("html").resize(function () {
        if (!$.isEmptyObject(window.parent)) {
            const pfs = parent.frames;
            for (let i = 0; i < pfs.length; i++) {
                if (pfs[i] === window) {
                    // console.log(pfs[i].frameElement);
                    pfs[i].frameElement.style.height = ($("html").outerHeight()  ) + "px";
                }
            }
        }
    });
    //注册按钮们的点击事件
    //注册翻页按钮
    $(document).on("change", ".pageSize", function () {
        // pageDataA["pageSize"] = parseInt(this.value);
        // pageDataA["pageNum"] = 1;
        functionObjectA(1, parseInt(this.value));
    });

    $(document).on("click", ".inputPageButton", function () {
        const inputNum = parseInt($("#inputPage").val());
        if (inputNum > pageDataA["countPage"] || inputNum < 1) {
            alert("输入的页数不在范围内");
            return;
        }
        // page_num = inputNum;
        functionObjectA(inputNum, pageDataA["pageSize"]);
    });

    $(document).on("click", ".pageButton", function () {
        const pageNum = parseInt($(this)[0].textContent);
        functionObjectA(pageNum, pageDataA["pageSize"]);
    });
    $(document).on("click", ".startsPage", function () {
        // page_num = 1;
        functionObjectA(1, pageDataA["pageSize"]);
    });
    $(document).on("click", ".lastPage", function () {
        if (pageDataA["pageNum"] === 1) {
            alert("已经是第一页啦 !");
            return;
        }
        // page_num = parseInt(page_num - 1);
        functionObjectA(parseInt(pageDataA["pageNum"]) - 1, pageDataA["pageSize"]);
    });
    $(document).on("click", ".nextPage", function () {
        if (pageDataA["pageNum"] === pageDataA["countPage"]) {
            alert("已经是最末页啦 !");
            return;
        }
        // pageDataA["pagNum"]  = parseInt(pageDataA["pagNum"] + 1);
        functionObjectA(parseInt(pageDataA["pageNum"] + 1), pageDataA["pageSize"]);
    });
    $(document).on("click", ".endPage", function () {
        // page_num = all_page_num;
        functionObjectA(pageDataA["countPage"], pageDataA["pageSize"]);
    });
    //按钮注册完毕

    //给跳转到收入详情页注册事件
    //打开收入详情按钮
    $(document).on("click", ".drawSerialNum-href", function () {
        // $(".revenue").click(function () {
        const temp = $(this);
        layer.ready(function () {
            layerIndex = layer.open({
                type: 2,
                title: false,
                maxmin: false,
                scrollbar: false,
                area: ['840px', '700px'],
                content: temp.attr("data-href")
            });
        });
        layer.full(layerIndex);
    });


});


// //监听div大小变化
(function ($, h, c) {
    var a = $([]),
        e = $.resize = $.extend($.resize, {}),
        i,
        k = "setTimeout",
        j = "resize",
        d = j + "-special-event",
        b = "delay",
        f = "throttleWindow";
    e[b] = 250;
    e[f] = true;
    $.event.special[j] = {
        setup: function () {
            if (!e[f] && this[k]) {
                return false;
            }
            var l = $(this);
            a = a.add(l);
            $.data(this, d, {
                w: l.width(),
                h: l.height()
            });
            if (a.length === 1) {
                g();
            }
        },
        teardown: function () {
            if (!e[f] && this[k]) {
                return false;
            }
            var l = $(this);
            a = a.not(l);
            l.removeData(d);
            if (!a.length) {
                clearTimeout(i);
            }
        },
        add: function (l) {
            if (!e[f] && this[k]) {
                return false;
            }
            var n;

            function m(s, o, p) {
                var q = $(this),
                    r = $.data(this, d);
                r.w = o !== c ? o : q.width();
                r.h = p !== c ? p : q.height();
                n.apply(this, arguments);
            }

            if ($.isFunction(l)) {
                n = l;
                return m;
            } else {
                n = l.handler;
                l.handler = m;
            }
        }
    };

    function g() {
        i = h[k](function () {
                a.each(function () {
                    var n = $(this),
                        m = n.width(),
                        l = n.height(),
                        o = $.data(this, d);
                    if (m !== o.w || l !== o.h) {
                        n.trigger(j, [o.w = m, o.h = l]);
                    }
                });
                g();
            },
            e[b]);
    }
})(jQuery, this);
