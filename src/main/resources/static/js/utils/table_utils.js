let moveX;
let moveY;                     //用来接受鼠标位置的全局变量
function mouse_move(e) {
    e = e || window.event;
    if (e.pageX || e.pageY) {
        moveX = e.pageX;
        moveY = e.pageY
    }
}

document.onmousemove = mouse_move;

//判断鼠标是否在某元素内 传入参数是元素的jq对象
function inner(div) {
    let x = moveX;//鼠标的位置-X
    let y = moveY;//鼠标的位置-Y
    let y1 = div.offset().top;
    //div上面两个的点的y值
    let y2 = y1 + div.outerHeight();
    //div下面两个点的y值
    let x1 = div.offset().left;
    //div左边两个的点的x值
    let x2 = x1 + div.outerWidth();
    //div右边两个点的x的值
    return !(x < x1 || x > x2 || y < y1 || y > y2);
    // if( x < x1 || x > x2 || y < y1 || y > y2 ) {
    //     return false;
    // }else{
    //     return true;
    // }
}

//根据勾选 决定显示哪儿些表头
function OpTh() {
    $(".th-checkbox input[type='checkbox']").each(function (index, checkbox) {
        let label_text = $(this).next().text();
        $("[data-table] th").each(function (index, th) {
            // noinspection JSValidateTypes
            let th_text = $(th).children('span').text();
            if (label_text === th_text) {
                // let tdTemp = $("[data-table] tbody tr td:nth-child(" + (index + 1) + ")");
                if (checkbox.checked) {
                    // tdTemp.show();
                    // $(this).show();
                    $(this).width(100);
                } else {
                    // tdTemp.hide();
                    // $(this).hide();
                    $(this).css("width" , "0");
                }
            }
        })
    });
}

//导入权限css
document.write("<!--suppress HtmlUnknownTarget -->" +
    "<link rel='stylesheet' type='text/css' href='/css/utils/permissions-css.css'>");

//生成请求参数
function createMyCustomData() {
    let myCustomData = {};
    myCustomData["pageNum"] = tableConfig.pageNum; // 页数
    myCustomData["pageSize"] = tableConfig.pageSize;  //每页数量
    //如果有排序字段
    if (  ! $.isEmptyObject( tableConfig.pageable.sortField ) ) {
        let fieldTemp = [];
        $( tableConfig.pageable.sortField ).each(function (index , field ) {
            fieldTemp.push(field);
        });
        myCustomData["sortField"] = fieldTemp.toString().replace(/,/g, "$");
        //添加排序规则
        if ( ! $.isEmptyObject(  tableConfig.pageable.sort )) {
            myCustomData["sort"] = tableConfig.pageable.sort;
        }
    }
    //表名
    myCustomData["table_utils.tableName"] = tableConfig.tableName;
    //拼接字段 先添加cols里面的字段 , 再添加buttons里面用到的字段
    let fields = [];
    $(tableConfig.cols).each(function (index, col) {
        fields.push(col.field);
    });
    $(tableConfig.buttons).each(function ( index , buttonObject) {
        //先找到button的condition里的key
        if ( ! $.isEmptyObject( buttonObject.condition ) ) {
            $(buttonObject.condition).each(function (index, condition) {
                fields.push(condition.key);
            });
        }
        //再找到buttonPath里的有没有代变量
        let pathTemp = buttonObject["buttonPath"] === undefined ? "" : buttonObject["buttonPath"] ;
        while ( pathTemp.indexOf( "${") !== -1 && pathTemp.indexOf( "}")  !== -1 ) {
            let left  = pathTemp.indexOf( "${");
            let right  = pathTemp.indexOf( "}");
            fields.push(pathTemp.substring(left + 2 , right ) );
            pathTemp = pathTemp.replace("${" , "");
            pathTemp = pathTemp.replace("}" , "");
        }
    });

    myCustomData["table_utils.fields"] = fields.toString().replace(/,/g, "$");
    return myCustomData;
}


function createTh(col) {
    // noinspection RegExpRedundantEscape
    let field = col.field.replace(/\[n\]/g , "");
    let type = col.type === undefined ? "string" : col.type;
    let thClass = (col.multipleSelection ? " table-utils-multiple-selection " : "");
    thClass += (col.search ? " table-utils-search " : "");

    let inputClass = (type === "float" ) ? "table-utils-number-scope" : "";
    if ( type === "date" )  inputClass = "table-utils-lay-date";
    let style = col.width === undefined ? "" : "style='min-width: "+col.width + ";'";

    let searchName = ( type === "date" || type === "float" ) ? "$S." + field :  field;
    return (
        "<th "+style+" class=' "+field+"-th  "+ thClass +" ' data-type='"+type+"' data-name='"+field+"-th' >\n" +
        "    <span class='resize-span' >"+col.name+"</span>\n" +
        "    <i class=\"glyphicon glyphicon-chevron-down show-i test-normal\"></i>\n" +
        "    <div class=\"filter-div\" tabindex=\"1\">\n" +
        "        <div class=\"filter-body pre-scrollable \" data-property='"+field+"' >\n" +
        "            <input type=\"text\" class=\"form-control input-sm "+inputClass+" \" name=\""+searchName+"\" value=\"\"\n" +
        "                   autocomplete=\"off\" placeholder=\"键入或勾选\">\n" +
        "            <input type=\"hidden\" name='"+( "$D." + field )+"' value=\"\">\n" +
        "        </div>\n" +
        "        <div class=\"scrollbar\">\n" +
        "        </div>\n" +
        "        <button type=\"button\" class=\"btn btn-xs btn-warning btn-confirm\">确定</button>\n" +
        "    </div>\n" +
        "</th>");
}


//生成table标签结构
function createTableElement(myTable) {
    myTable.empty();

    //生成th们
    let ths = "";
    $(tableConfig.cols).each(function (index, col) {
        // ths += createTh(col);
        if ( index >= tableConfig.fixed.fixedTdLength ) {  //大于固定列数
            return ths += createTh(col);
        }
        // let TempWidth =  ( (col.length + 1) * 18  ) + "px" ;
        let style = col.width === undefined ? "" : "style='min-width: "+col.width + ";'";
        let thClass = (col.multipleSelection ? " table-utils-multiple-selection " : "");
        thClass += (col.search ? " table-utils-search " : "");
        ths += ("<th "+style+"  class=' "+ thClass +" "+col.field+"-th ' data-name='"+col.field+"-th' >" +
            "    <span>"+col.name+"</span>\n" +
            "    <i class=\"glyphicon glyphicon-chevron-down show-i test-normal\"></i>\n" +
            "</th>");
    });
    if ( tableConfig.buttons.length > 0 ) {
        ths += ("<th  style='max-width: 60px;' class='table-utils-buttons-th' data-name='table-utils-buttons-th' >操作框</th>");
    }
    //如果首行固定
    // if ( true ) {
        //创建首行th
        myTable.append("<table data-table='"+tableConfig.tableName+"' class=\"show-altrowstable copy-first-line table table-bordered table-hover \">" +
            "<thead class='thead-dark'>" +
            "<tr>" +
            ths +
            "</tr>" +
            "</thead>" +
            "</table>");
    // }
    //如果 列固定
    // if ( true ) {
        //生成前几列的th
        let columnThs = "";
        $(tableConfig.cols).each(function (index, col) {
            if ( index < tableConfig.fixed.fixedTdLength ) {  //小于固定列数
                // columnThs += ("<th class=' "+col.field+"-th ' data-name='"+col.field+"-th' >"+col.name+"</th>");
                columnThs += createTh(col);
            }
        });
        // 如果首行
        //创建前几列的首行
        // if ( true ) {
            myTable.append("<table data-table='"+tableConfig.tableName+"' class=\"show-altrowstable copy-first-column-th table table-bordered table-hover \">" +
                "<thead class='thead-dark'>" +
                "<tr>" +
                columnThs +
                "</tr>" +
                "</thead>" +
                "</table>");
        // }

        //创建列table
        myTable.append("<table data-table='"+tableConfig.tableName+"' class=\"show-altrowstable copy-first-column table table-bordered table-hover \">" +
            "<thead class='thead-dark'>" +
            "<tr>" +
            columnThs +
            "</tr>" +
            "</thead>" +
            "<tbody>" +
            "</tbody>" +
            "</table>");
    // }

    // 创建真实table
    myTable.append(
        "<table data-table='"+tableConfig.tableName+"' class=\"show-altrowstable  no-copy-show-altrowstable first-line-table table table-bordered table-hover \" >" +
            "<thead class='thead-dark'>" +
                "<tr>" +
                    ths +
                "</tr>" +
            "</thead>" +
            "<tbody id='table-utils-data-body'>" +
            "</tbody>" +
        "</table>");
    //添加分页按钮的div
    myTable.after("<div id='table-utils-page-div' ></div>");

    //添加合计行
    myTable.after("<div class='table-utils-count-div' >" +
        "<table class='table-utils-count-table' ><tr class='table-utils-count-table-tr'></tr></table>" +
        "</div>");


    tableConfig.tBody = $("#table-utils-data-body");
    tableConfig.pageDiv = $("#table-utils-page-div");

    //修复高度
    $(".table-utils-div").height(window.innerHeight * 0.8 );

    //table 宽度自动
    $(".table-div table th").each(function (index, thTemp) {
        $(thTemp).width($(thTemp).width());
    });

    //真实table对象
    let tableTemp = $(".no-copy-show-altrowstable");
    //首行table对象
    // let firstLine = $(".copy-first-line");
    //首列table对象
    let firstColumn = $(".copy-first-column");
    myTable.css( "min-height" ,  window.screen.availHeight * 0.6 + "px" );

    tableResizable(tableTemp);

    //生成lay-date
    if ( typeof(layui) === "undefined" || typeof(laydate) === "undefined" ) {
            loadScript('/vendor/laydate_custom/laydate.js', temp);
    } else {
        temp();
    }
    function temp() {
        layDateBySelector(".table-utils-lay-date");
    }

    //删除不必要的多选框
    tableTemp.find("th").children(":not(span , i)").remove();
    firstColumn.find("th").children(":not(span , i )").remove();

    //铺多选数据
    //先统计字段们
    let fields = [];
    let need_datas = [];
    $(".table-utils-multiple-selection").each(function () {
        let need_data = $(this).find(".filter-body");
        //console.log(need_datas);
        if ( need_data.length === 0 ) return;
        // noinspection RegExpRedundantEscape
        fields.push( need_data.attr("data-property").replace(/\[n\]/g , "") ) ;
        need_datas.push( need_data );
    });
    let formTemp = $("#searchForm");
    let params = $.param({ "table_utils.fields" : fields.toString().replace(/,/g, "$") ,  "table_utils.tableName" : $("table[data-table]").attr("data-table") }) + '&' + formTemp.serialize();
    // console.log( need_datas );
    if ( params.indexOf( "table_utils.fields=&" ) === -1 ) {
        $.get( "/table_utils/multiple_properties" , params , function (data) {
            $(".table-utils-multiple-selection").each(function (indexOuter) {
                let need_data = need_datas[indexOuter];
                if ( undefined ===  need_data ||  need_data.length === 0 ) return;
                need_data.find(".checkbox").remove();
                // noinspection RegExpRedundantEscape
                let dataTemps = data[0][indexOuter].replace(/\&[A]\&\$\$/g , "").split(",");
                // console.log(dataTemps);
                $(dataTemps).each(function (index, item) {
                    let temp = Math.ceil(Math.random() * 1000);
                    need_data.append(
                        '<div class="checkbox checkbox-info checkbox-circle">'
                        + '<input type="checkbox" id="item_id_' + temp + '">'
                        + '<label for="item_id_' + temp + '">' + item + '</label>'
                        + '</div>'
                    )
                })
            });
        } , "json" );
    }
    if ( tableTemp.width() < myTable.width() * 0.95  ) {
        tableTemp.css("min-width" , "100%");
    }

    //列排序
    if ( typeof ( tableTemp.colResizable ) !== "function" ) {
        loadScript('/vendor/tableSort/jquery.tablesort.min.js', function () {
            tableTemp.tablesort( );
            tableTemp.on('tablesort:complete', function() {
                fixFirstColumnData();
            });
        });
    } else {
        tableTemp.tablesort( );
        tableTemp.on('tablesort:complete', function() {
            fixFirstColumnData();
        });
    }

    //首行首列 的click事件 触发真实排序
    $(".copy-first-line , .copy-first-column-th ").find("th span").click(function (  ) {
        tableTemp.find("th[data-name='"+$(this).closest("th").attr("data-name")+"']").click();
    });

    //数字范围的提示
    myPopover();

    //绑定table宽度改变事件
    tableResize();

}

function tableResize() {
    //当table宽度发生改变的时候  同步列宽
    // $(".table-utils-div .no-copy-show-altrowstable").resize(function () {
    //     // console.log("tableResize");
    //     if (typeof (showPageButtuns) === "undefined") {
    //         loadScript('/js/utils/pageUtils.js', fixWidth4ThOnLoad );
    //     } else {
    //         fixWidth4ThOnLoad();
    //     }
    //
    // });
}

//数字范围的提示
function myPopover(  ) {
    let $table = $('.table-utils-number-scope');
    // $table.attr("data-trigger" , "focus" );
    $table.attr("title" , "输入格式示例");
    $table.attr("data-placement" , "bottom");
    $table.attr("data-content" , "数字: 直接输入正负数字 \r\n 范围: xx~xx , 如 1~2");
    $table.popover( { viewport : ".table-utils-div"} );
}


function tableResizable( tableTemp ) {
    tableTemp.parent().find(".JCLRgrips").remove();
    if ( typeof ( tableTemp.colResizable ) !== "function" ) {
        loadScript('/vendor/jquery/colResizable-1.6.min.js', function () {
            tableTemp.colResizable( { resizeMode : 'overflow'  , partialRefresh : true ,  onResize: fixWidth4ThOnLoad , postbackSafe : true } );
        });
    } else {
        tableTemp.colResizable( { resizeMode : 'overflow'  , partialRefresh : true ,  onResize: fixWidth4ThOnLoad , postbackSafe : true  } );
    }
}

// function tableOnResize() {
//         fixWidth4ThOnLoad();
// }

//同步固定列的数据
function fixFirstColumnData() {

    // if ( true ) {  //如果有固定列
    let tableTemp = $(".no-copy-show-altrowstable");
    // tableResizable(tableTemp);
    //拿到真实table里的数据
    let copyColumn = $(".copy-first-column");
    copyColumn.find("tbody").remove();
    //先将整个tbody克隆一份 然后决定谁显示
    tableTemp.find("tbody").clone(true).appendTo(copyColumn);
    let countWidth = 0;
    tableTemp.find("thead").find("tr").find("th").each(function (index, th) {
        if ( index <  tableConfig.fixed.fixedTdLength ) {
            countWidth += $(th).width();
            let name = $(th).attr("data-name");
            name = name.substring(0 , name.length-3 ) + "-td";
            // console.log(name);
            let copyTdTemp =  $(copyColumn).find("[data-name='" + name + "']");
            let tdTemp =  $(tableTemp).find("[data-name='" + name + "']");
            copyTdTemp.show();
            copyTdTemp.each(function (index, td) {
                $(td).height( $(tdTemp.get(index)).height() );
            });

        }
    });
    copyColumn.width(countWidth);
        // $(".copy-first-column-th").width(countWidth);
    // }


    var myTable = copyColumn.closest(".table-utils-div");

    var heght = window.screen.availHeight * 0.6;
    // myTable.css( "max-height" ,  window.screen.availHeight * 0.6 + "px" );
    if ( heght > tableTemp.outerHeight() ) {
        myTable.css( "max-height" ,  ( tableTemp.outerHeight() + 1 ) + "px" );
        myTable.outerHeight( tableTemp.outerHeight() + 1 );
    } else  {
        myTable.css( "max-height" ,  window.screen.availHeight * 0.6 + "px" );
        myTable.outerHeight( window.screen.availHeight * 0.6 );
    }

    fixWidth4ThOnLoad();
}

function count( table ) {
    var countTable = $(".table-utils-count-table");
    var countTableTr = countTable.find("tr");
    countTableTr.empty();
    // var table = $(".no-copy-show-altrowstable");
    countTable.css( "min-width" ,  table.outerWidth() );
    countTableTr.append( getCountTr( table )  ) ;
    countTable.css( "min-width" ,  table.outerWidth() );
    if ( tableConfig.count ) {
        countTable.closest(".table-utils-count-div").show();
    }
    //同步合计行 与table滚动t条
    $(".table-utils-count-div").scrollLeft( $(".table-utils-div").scrollLeft() );

}

function getCountTr( table ) {
    var tds = "";
    console.clear();
    table.find("tbody").find("tr").eq(0).find("td").each(function (index, td) {
        //合计并且添加
        var dataName = $(td).attr("data-name");
        var countValue = 0.00;
        table.find("[data-name='"+dataName+"']").each(function ( index , tdTemp) {
            var valueTemp = $( tdTemp ).find("span.table-utils-td-text").text();
            if ( valueTemp !== undefined && valueTemp.match(/(^[\-]?[0-9]*(.[0-9]+)?)$/g)  && valueTemp !== "" ) {
                countValue += parseFloat( valueTemp );
            }
        });

        //判断是否需要合计
        var isCount = false;
        if ( dataName !== undefined ) {
            //真实name
            var name = dataName.substring( 0 , dataName.length - 3 );
            $( tableConfig.cols ).each(function ( index , col) {
                if ( col.field === name && col.count  ) {
                    tableConfig.count = true;
                    return isCount = true;
                }
                // noinspection RegExpRedundantEscape
                if ( col.field.replace( /\[n\]/g , "") === name && col.count  ) {
                    tableConfig.count = true;
                    return isCount = true;
                }
            });
        }
        // console.log( "宽 " + $(td).width() )
        // console.log( " 外宽 " +  $(td).outerWidth() )
        var outerWidth = $(td).outerWidth();
        tds += ("<td style='width: "+ outerWidth+"px; min-width:  "+outerWidth+"px; '  class='"+dataName+"-count "+ ( isCount ? "isCount" : " notCount " ) +" ' >"+ ( isCount ?  countValue.toFixed(2) : " "   ) +"</td>");
    });
    return tds;
}


//同步th宽度
function fixWidth4ThOnLoad() {
    //给复制体宽度
    let noCopy = $(".no-copy-show-altrowstable");
    //如果宽度过窄 赋100%
    let outerDivWidth = noCopy.closest(".table-utils-div").width();
    if ( noCopy.width() < outerDivWidth ) {
        noCopy.css("min-width" , "100%" );
    }
    let copyFirstLine = $(".copy-first-line");
    copyFirstLine.width(noCopy.width() );
    //给每个th标签指定宽度
    let countWidth = 0;
    let copyColumn = $(".copy-first-column");
    let copyColumnTh = $(".copy-first-column-th");
    copyColumn.width( countWidth );

    noCopy.find("thead").find("tr").find("th").each(function (index, th) {
        // $("th[data-name='" + $(th).attr("data-name") + "']").width( $(th).width() );
        $( copyFirstLine.find("th").get(index) ).width( $(th).width() );
        $( copyColumn.find("th").get(index) ).width( $(th).width() );
        $( copyColumnTh.find("th").get(index) ).width( $(th).width() );

        if ( index <  tableConfig.fixed.fixedTdLength ) {
            countWidth += $(th).width();
            let name = $(th).attr("data-name");
            name = name.substring(0 , name.length-3 ) + "-td";
            // console.log(name);
            let copyTdTemp =  $(copyColumn).find("[data-name='" + name + "']");
            let tdTemp =  $(noCopy).find("[data-name='" + name + "']");
            copyTdTemp.show();
            copyTdTemp.each(function (index, td) {
                $(td).height( $(tdTemp.get(index)).height() );
            });

        }
    });
    copyColumn.width(countWidth);
    copyColumnTh.width( copyColumn.width() );

    //修复JCLRgrips的left定位和高度
    var tds = $(".no-copy-show-altrowstable tbody tr:first td");
    $(".JCLRgrips div.JCLRgrip").each(function (index, JCLRgrip) {
        $(JCLRgrip).height( noCopy.height() );
        $(JCLRgrip).css( "left" ,  tds.eq(index).position().left  + tds.eq(index).outerWidth() );
    });
    // tableResizable(noCopy);

    //合计行
    count( noCopy );

}


//cols []   {field : "xxx" , rowspan : default 1 }
function getPageDataUtils(config ) {
    for (let key in  config) {
        if (config.hasOwnProperty(key)) {
            tableConfig[key] = config[key];
            // console.log(tableConfig[key])
        }
    }


    //匹配行列固定
    if ( tableConfig.fixed.fixedTh !== undefined && tableConfig.fixed.fixedTh ) {
        tableConfig.elem.addClass("table-utils-fixed-first-line");
    }
    if ( tableConfig.fixed.fixedTd !== undefined && tableConfig.fixed.fixedTd ) {
        tableConfig.elem.addClass("table-utils-fixed-column");
    }
    if ( tableConfig.fixed.fixedTdLength === undefined || tableConfig.fixed.fixedTdLength  < 1 ) {
        tableConfig.fixed.fixedTdLength = 1;
    }

    createTableElement(tableConfig.elem);

    tableConfig.myCustomData = createMyCustomData();

    getAPageDataTemp(tableConfig.pageable["pageNum"], tableConfig.pageable["pageSize"]);
}

//刷新当前页
function flush(pageable) {
    if ( typeof ( pageable) === "undefined" ) {
        pageable = tableConfig.pageable;
    }
    getAPageDataTemp(pageable.pageNum , pageable.pageSize);

}

let tableConfig = {
    pageable: { //分页配置
        pageNum: 1, //初始当前页 默认1
        pageSize: 10 // 每页数量 默认10
        // sortField: ["managementId"], //排序字段 可以多个 默认无
        // sort: "true" // 排序正反 默认反
    },
    functions : {
        flush : flush
    } ,
    rollback :function(){} , //回掉函数
    fixed : {
        fixedTh : true ,  //首行固定 默认 true
        fixedTd : false ,  // 固定列 默认 false
        fixedTdLength : 0   // 固定列数 默认1 最小1
    } ,
    tableName: "management", //表名
    elem: $("#myTable"),
    rowspanLength: null , //跨行属性
    cols: [
        // {field: "projectBean.projectNum" ,
        //     type : "number" ,  //字段类型 默认string  ,  string  , number  , date , float
        //     multipleSelection :  true   // 是否加载多选数据 默认false
        //     // fixed : 2 //小数点后尾数(type="float 才有")
        // },
        // {field: "projectBean.projectName" , type : "number"  },
        // {field: "projectBean.projectManagementType"  },
        // {field: "managementMainHead" ,  multipleSelection :  true   },
        // {field: "projectBean.implementBeans[n].implementImplementHead" , multipleSelection :  true },
        // {field: "projectBean.implementBeans[n].implementDepartmentBean.implementDepartmentName" , multipleSelection :  true },
        // {field: "managementRefereesBean.managementRefereesName" , multipleSelection :  true },
        // {field: "managementInnerPartnerBean.managementInnerPartnerName" , multipleSelection :  true },
        // {field: "managementOuterPartnerBean.managementOuterPartnerName" , multipleSelection :  true },
        // {field: "managementCooperativePartnerBean.managementCooperativePartnerName" , multipleSelection :  true },
        // {field: "managementCoordinateFee" , multipleSelection :  true },
        // {field: "managementRate"  , type: "float" , fixed : 2  },
        // {field: "managementGoodsEvaluationAmount" , type: "float" , fixed : 2 },
        // {field: "managementSettlementAmount" , type: "float" , fixed : 2  },
        // {field: "managementAuditAmount" , type: "float" , fixed : 2  },
        // {field: "createUserBean.userName"  , multipleSelection : true },
        // {field: "managementCreateTime" , type : "date" }
    ] ,
    buttons: [
        // {
        //     buttonName : "编辑" ,  //按钮名称
        //     buttonType : "edit" ,  //按钮类型 edit 打开新页面 delete 删除按钮 btn other 其他(纯按钮 自己做事件用)
        //     buttonPath : "edit/edit.html?id=${primary}" ,  //按钮路径 ${field} 支持查出值用于拼接路径
        //     windowWidth : "100px",
        //     windowHeight : "100px",
        //     condition :  [ // 按钮出现条件 可以是多个
        //         {
        //             key : "primary" ,  //条件key
        //             value : 1 ,  //条件 value
        //             comparisons : "eq"
        //         }  //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
        //     ]
        // }
    ]
};
// 定义全局内容
let contentTempByUtils;

function getAPageDataTemp(pageNum, pageSize) {
    tableConfig.myCustomData.pageSize = pageSize;
    tableConfig.myCustomData.pageNum = pageNum;
    tableConfig.pageable.pageSize = pageSize;
    tableConfig.pageable.pageNum = pageNum;

    let formTemp = $("#searchForm");
    //拼接form和myCustomData
    let params = $.param(tableConfig.myCustomData) + '&' + formTemp.serialize();
    $.get("/table_utils", params, function (data) {
        contentTempByUtils = data.content;
        tableConfig.tBody.empty();
        showData4TableDom(contentTempByUtils , tableConfig.tBody.closest("table") );
        //同步th宽
        for (let i = 0; i < 2; i++) {
            setTimeout(function () {
                fixWidth4ThOnLoad();
            } , 50 * ( i + 1 ) );
        }
        //如果首列固定 同步固定列数据
        fixFirstColumnData();
        if (typeof (showPageButtuns) === "undefined") {
            loadScript('/js/utils/pageUtils.js', function () {
                showPageButtuns(
                    {
                        "pageNum": data.number + 1,
                        "countPage": data["totalPages"],
                        "pageSize": data["size"],
                        "countNum": data["totalElements"]
                    },
                    tableConfig.pageDiv ,
                    getAPageDataTemp);
            });
        } else {
            showPageButtuns(
                {
                    "pageNum": data.number + 1,
                    "countPage": data["totalPages"],
                    "pageSize": data["size"],
                    "countNum": data["totalElements"]
                },
                tableConfig.pageDiv ,
                getAPageDataTemp);
        }
        //添加刷新按钮
        tableConfig.pageDiv.append(
            "<a class=\"button  btn btn-xs  btn-default   \" href='' type=\"button\">刷新</a>");
        //添加导出按钮
        tableConfig.pageDiv.append(
            "<button class=\"button  btn btn-xs  btn-default export-all  \" type=\"button\">导出全部</button>" +
            "<button class=\"button  btn btn-xs  btn-default  export-this \" type=\"button\">导出本页</button>"
        );
        //回调函数
        tableConfig.rollback( tableConfig.tBody.closest("table") );
        //回掉完适配按钮权限
        try {
            window.top.showPermissionsElem( $(".table-utils-div") );
        } catch (e) {
        }

    }, "json").fail(function (res) {
        console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据获取失败 请刷新重试");
    });
}


function showData4TableDom( listData , tableDom ) {
    tableDom.find("tbody").empty();
    $(listData).each(function (index1, object) {
        let trs = [];
        let rowspan = 1;
        $(object).each(function (index , obj) {
            let rowspanTemp = obj.toString().split("$,").length;
            rowspan = rowspan < rowspanTemp ? rowspanTemp : rowspan;
        });
        $(tableConfig.cols).each(function (index, col) {
            let rowSpanTemp = rowspan;
            if (col.field.indexOf(tableConfig.rowspanLength) === 0) {
                rowSpanTemp = 1;
            }
            let dataTemp = object[index].toString().split("$,")[0];
            if ( dataTemp.indexOf("&A&") !== -1 ) {
                dataTemp = dataTemp.substring( dataTemp.indexOf("&A&") + 3 );
            }
            if ( tableConfig.rowspanLength === null  ) {
                rowSpanTemp = 1;
                dataTemp = object[index].toLocaleString() ;
                if ( dataTemp.indexOf("&A&") !== -1 ) {
                    let split = object[index].toLocaleString().split("$,");
                    let newArr = [];
                    $(split).each(function () {
                        newArr.push( this.substring( this.indexOf("&A&") + 3 ) );
                    });
                    dataTemp = newArr.join('，');
                }
            }

            // noinspection RegExpRedundantEscape
            trs.push("<td rowspan='" + rowSpanTemp + "' class='"+col.field.replace(/\[n\]/g , "")+"' data-name='"+col.field.replace(/\[n\]/g , "")+"-td' ><span class='table-utils-td-text' >" + dataTemp.replace(/\$/g , "") + "</span></td>");
        });
        let usedIndex = tableConfig.cols.length;
        if ( tableConfig.rowspanLength  === null ) {
            rowspan = 1;
        }
        if (!$.isEmptyObject(tableConfig.buttons)) {
            let editTemp = "<td class='buttons-td' rowspan='" + rowspan + "'>";
            $(tableConfig.buttons).each(function (index, button) {
                //先判断按钮是否出现

                let show = true;
                if ( ! $.isEmptyObject( button.condition ) ) {
                    $(button.condition).each(function (index, condition) {
                        if (condition.value === undefined) return;
                        // console.log("条件是 ==> " );
                        // console.log( condition );
                        // console.log("角标是 ==> " + usedIndex);
                        //比较方式  gt(大于) ,|  lt(小于) , eq(等于|默认值|) , notNull(不为空) , isNull(为空)
                        //如果等于
                        if (condition.comparisons === undefined || condition.comparisons === "eq") {
                            if (object[usedIndex++].toString() !== condition.value.toString()) {
                                 show = false;
                            }
                        }
                        //如果不等于
                        if (condition.comparisons === undefined || condition.comparisons === "not") {
                            if (object[usedIndex++].toString() === condition.value.toString()) {
                                 show = false;
                            }
                        }
                        //如果大于
                        if (condition.comparisons !== undefined && condition.comparisons === "gt") {
                            if ( object[usedIndex++] <= condition.value ) {
                                 show = false;
                            }
                        }
                        //如果小于
                        if (condition.comparisons !== undefined && condition.comparisons === "lt") {
                            if (  object[usedIndex++] >= condition.value )  {
                                 show = false;
                            }
                        }
                        //如果notNull
                        if (condition.comparisons !== undefined && condition.comparisons === "notNull") {
                            let tempA = object[usedIndex++];
                            if ( tempA === null || tempA === "") {
                                 show = false;
                            }
                        }
                        //如果isNull
                        if (condition.comparisons !== undefined && condition.comparisons === "isNull") {
                            let tempA = object[usedIndex++];
                            if ( tempA !== null && tempA !== "") {
                                 show = false;
                            }
                        }

                    });
                }
                //再找到buttonPath里的有没有代变量
                let pathTemp = button["buttonPath"] === undefined ? "" : button["buttonPath"] ;
                while ( pathTemp.indexOf( "${") !== -1 && pathTemp.indexOf( "}")  !== -1 ) {
                    let value = object[usedIndex++];
                    let left  = pathTemp.indexOf( "${");
                    let right  = pathTemp.indexOf( "}");
                    pathTemp = pathTemp.substring(0 , left ) + value + pathTemp.substring(right + 1);
                }
                //再拼接button元素
                // console.log(show + "  ==  adwadw");
                let display = show ? "initial" : "none";
                let width = button["windowWidth"] === undefined ? "100%" : button["windowWidth"];
                let height = button["windowHeight"] === undefined ? "100%" : button["windowHeight"];

                button.buttonType = button["buttonType"] === undefined ? "edit" : button["buttonType"];
                let buttonClass =   button.buttonType + "-btn";
                //老代码
                if ( button.isDelete ) {
                    buttonClass  = "delete-btn";
                }
                let dataId = 0;
                if ( buttonClass  === "delete-btn" ) {
                    dataId = object[usedIndex ];
                }
                try {
                    if ( buttonClass  !== "delete-btn" || window.top.userTemp.userName === "吴清典" || window.top.userTemp.userName === "朱海虹" ||
                        window.top.userTemp.userName === "王严叶" || window.top.userTemp.userName === "李正敏" || window.top.userTemp.userName === "王会清"
                        || window.top.userTemp.userName === "林日弟") {
                        editTemp += ('<button type="button"  class="btn btn-success btn-xs  ' + ( button.buttonType === "edit" ? " permissions-elem " : " " ) +buttonClass+' "  ' +
                            'style="margin-right: 5px; display: '+display+';" ' +
                            'title="'+button.buttonName+'" ' +
                            'data-title="'+button.buttonName+'" ' +
                            'data-id="'+dataId+'" ' +
                            'data-width="'+width+'" data-height="'+height+'" ' +
                            'data-href="'+pathTemp+'">'+button.buttonName+'</button>'
                        );
                    }
                } catch (e) {
                }
            });

            editTemp += "</td>";
            trs.push(editTemp);
        }
        tableDom.find("tbody").append(
            "<tr>" +
            trs.toString().replace(/,/g, "") +
            "</tr>"
        );
        if ( tableConfig.rowspanLength != null ) {
            for (let i = 1; i < rowspan; i++) {
                let trTemp = [];
                $(tableConfig.cols).each(function (index, col) {
                    if (col.field.indexOf(tableConfig.rowspanLength) === 0) {
                        let dataTemp = object[index].toString().split("$,")[i];
                        if ( typeof (dataTemp) === "undefined" ) {
                            dataTemp = "";
                        }
                        // noinspection RegExpRedundantEscape
                        trTemp.push("<td rowspan='1' class='" + col.field.replace(/\[n\]/g , "") + "' data-name='"+col.field.replace(/\[n\]/g , "")+"-td' ><span class='table-utils-td-text' >"+dataTemp.replace(/\$/g , "")+"</span></td>");
                    }
                });
                tableDom.find("tbody").append(
                    "<tr>" +
                    trTemp.toString().replace(/,/g, "") +
                    "</tr>"
                );
            }
        }

    });
}


// noinspection ES6ConvertVarToLetConst
var layerIndex;
$(function () {

    //同步合计行 与table滚动事件
    $(".table-utils-div").scroll(function () {
        $(".table-utils-count-div").scrollLeft( $(this).scrollLeft() );
    });


    //下拉搜索输入框的数字范围控件
    $(document).on("blur" , ".table-utils-number-scope" , function () {
        let value = $(this).val();
        if ( value === "") {
            return;
        }
        //如果有分隔符 ~
        if ( value.indexOf( "~" ) !== -1  ) {
            let split  = value.split("~");
            if ( split.length !== 2 ) {
                 alert('输入格式不对');
            } else {
                let left = split[0];
                let right = split[1];
                if (  left.replace(/(^\s*)|(\s*$)/g, "").match(/(^[\-]?[0-9]*(.[0-9]+)?)$/g) == null  ||  right.replace(/(^\s*)|(\s*$)/g, "").match(/(^[\-]?[0-9]*(.[0-9]+)?)$/g) === null  ) {
                    alert('输入格式不对');
                }
            }

        } else //如果是单正负数 进行范围添加
        if ( value.match(/(^[\-]?[0-9]*(.[0-9]+)?)$/g) ) {
            $(this).val( value + "~" + value );
        }
        else  {
            alert('输入格式不对');
        }

    } ) ;

    //窗口尺寸发生改变时 同步宽度
    $(window).resize(function () {
        fixFirstColumnData();
    });

    //权限按钮
    try {
        window.top.showPermissionsElem( $("body") );
    } catch (e) {
    }

    //导出全部按钮
    $(document).on('click', '.export-all', function () {
        exportFull();
    });
    //导出本页按钮
    $(document).on('click', '.export-this', function () {
        var table = $(".table-utils-div .no-copy-show-altrowstable");
        if ( tableConfig.count ) {
            $(".table-utils-count-table tr").clone(true).appendTo( table.find("tbody") );
        }
        exportExcel(  table  , function () {
            table.find(".table-utils-count-table-tr").remove();
        });

    });

    //添加按钮

    $(document).on('click', '.add-div', function () {
        let href = $(this).attr("data-href"); //要打开页面的地址
        if( href.indexOf(".html?") !== -1 ) {
            href += "&edit=true";
        } else  {
            href += "?edit=true";
        }
        let layerTitle = $(this).attr("data-title"); //页面标题
        layerTitle = layerTitle === "undefined" ? false : layerTitle;
        let width = $(this).attr("data-width"); //页面宽高
        let height = $(this).attr("data-height");
        width = width === undefined ? '100%' : width;
        height = height === undefined ? '100%' : height;

        layer.ready(function () {
            layerIndex = layer.open({
                type: 2,
                title: layerTitle,
                maxmin: false,
                area: [width, height],
                content: href
            });
        });
        // layer.full(layerIndex);
    });

    //编辑按钮
    $(document).on('click', '.edit-btn', function () {
        let href = $(this).attr("data-href");
        let layerTitle = $(this).attr("data-title");
        layerTitle = layerTitle === "undefined" ? false : layerTitle;
        let width = $(this).attr("data-width");
        let height = $(this).attr("data-height");
        width = width === undefined ? '100%' : width;
        height = height === undefined ? '100%' : height;

        layer.ready(function () {
            layerIndex = layer.open({
                type: 2,
                title: layerTitle,
                maxmin: false,
                area: [width, height],
                content: href
            });
        });
    });
    //删除按钮
    $(document).on('click', '.delete-btn', function () {
        let table = $(this).closest("table").attr("data-table");
        let id = $(this).attr("data-id");
        let tr = $(this).closest("tr");
        layer.confirm('确认删除吗？', {
            btn: ['确认', '取消'] //按钮
        }, function () {
            layer.close(layer.index);
            $.post("/table_utils" , {"_method": "DELETE", tableName : table , "id": id}, function (data) {
                if (data) {
                    layer.msg("删除成功", {icon: 1});
                    tr.remove();
                    try {
                        flush();
                    } catch (e) {
                    }
                } else {
                    layer.msg("删除失败,请刷新重试!", {icon: 5});
                }
            }, "json").fail(function (res) {
                layer.msg("服务器错误 请刷新重试", {icon: 5});
                console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据获取失败 请刷新重试");
            });
        })
    });


    // 1 点击向下展开的图标时,筛选的filter-div展开或隐藏
    $(document).on('click', 'th .show-i', function ( ) {
        if ($(this).next().css("display") !== "none") {
            $(this).next().slideUp();
            $(this).removeClass('test-inverse');
        } else {
            $(this).next().slideDown();
            $(this).next().focus();
            $(this).addClass('test-inverse');
        }
    });

    $(document).on('blur', '.filter-div', function () {
        if ((!inner($(this)))) {
            //如果鼠标不在该div里面,则收起来该div
            let temp = $(".layui-laydate");
            if (!(temp.length > 0 && inner(temp))) {
                $(this).slideUp();
                $(this).prev('i').removeClass('test-inverse');
            }
        }
    });
    $(document).on('change', '.laydate_double', function () {
        $(this).focus();
    });

    //显示哪儿些列 的点击事件
    $('#label_list input').bind('click', OpTh);


    // 多选填充到隐藏框
    $(document).on("click", ".checkbox-info input", function () {
        // $(".checkbox-info input").click(function () {
        let filter_body = $(this).closest(".filter-body");
        let arr = [];
        filter_body.find("input[type='checkbox']").each(function () {
            if (this.checked) {
                arr.push($(this).next().text())
            }
        });
        filter_body.find("input[type='hidden']").val(arr.toString().replace(/,/g, "$"));

        //如果有勾选 清除键入
        // noinspection JSValidateTypes
        $(this).closest(".filter-body").children("input[type='text']").val("");
    });

    //如果有键入 清除勾选
    // noinspection JSValidateTypes
    $(".filter-body").children("input[type='text']").keyup(function () {
        $(this).closest(".filter-body").find("input[type='checkbox']").prop("checked", false);
        $(this).next("input[type='hidden']").val("");
    });

    // 拖动表格列
    function isNullOrUndefined(obj) {
        return (typeof (obj) === "undefined" || obj == null);
    }

    //搜索按钮
    $(document).on("click", ".btn-confirm", function () {
        getAPageDataTemp(1, pageDataA["pageSize"]);
    });


    //全局搜索框条件拼接
    $(".multiple-value").change(function () {
        let formTemp = $("table[data-table]").closest("form");
        formTemp.find(".multiple-form").remove();
        let keys = [];
        //拼接任意条件的name(key)值
        formTemp.find("input[name]").each(function () {
            let nameTemp = $(this).prop("name");
            if ( nameTemp === "table_utils.custom_where") {
                return;
            }
            if (nameTemp.indexOf("$D.") === -1 && nameTemp.indexOf("$S.") === -1 && $(this).closest("th").css("display") !== "none") {
                keys.push(nameTemp);
            }
        });
        formTemp.append("<input class='multiple-form' title='' type='hidden' name='multiple_key' />");
        formTemp.find("[name='multiple_key']").val(keys.toString().replace(/,/g, "$"));
        formTemp.append("<input class='multiple-form' title='' type='hidden' name='multiple_value' />");
        formTemp.find("[name='multiple_value']").val(this.value);
    });

    function registerTableDragEvent() {
        let dragTH; //记录拖拽的列
        //第一步按下
        $('[data-table] th').mousedown(function (e) {
            e = e || window.event;
            if (e.offsetX > $(this).innerWidth() - 10) {
                dragTH = $(this);
                dragTH.mouseDown = true;
                dragTH.oldX = e.pageX || e.clientX;
                dragTH.oldWidth = $(this).innerWidth();
            }
            //第二步 拖动
        }).mousemove(function (e) {
            //改鼠标样式
            if (e.offsetX > $(this).innerWidth() - 10) {
                $(this).css({
                    cursor: "e-resize"
                });
            } else {
                $(this).css({
                    cursor: "default"
                });
            }
            if (isNullOrUndefined(dragTH)) {
                dragTH = $(this);
            }
            if (!isNullOrUndefined(dragTH.mouseDown) && dragTH.mouseDown === true) {
                let difference = (e.pageX - dragTH.oldX) || (e.clientX - dragTH.oldX);
                let newWidth = dragTH.oldWidth + difference; //新的宽度
                dragTH.width(newWidth)
            }
            // 第三步，释放
        }).mouseup(function () {
            // 还原鼠标样式
            // if (isNullOrUndefined(dragTH)) {
            //     dragTH = $(this);
            // }
            dragTH.mouseDown = false;
            // $(dragTH).css({
            //     cursor : "default"
            // });
        });
    }

    registerTableDragEvent();
    // $('.filter-body input').bind('click',function () {
    //     if (!this.checked) {
    //         console.log($(this));
    //     }
    // })

    // //获取当前选中的选项 填充到当前区域的隐藏输入框中
    // $(document).on("click",".filter-body input[type='checkbox']",function () {
    //     let checkboxTemp = $(this).closest(".filter-body").find("input[type='checkbox']");
    //     let hiddenInput = $(this).closest(".filter-body").find("input[type='hidden']");
    //     let arrTemp = "";
    //     checkboxTemp.each(function () {
    //         if ( this.checked ) {
    //             arrTemp += ( $(this).next().text() + "$" );
    //         }
    //     });
    //     hiddenInput.val( arrTemp.substring(0 , arrTemp.length-1) );
    // });


    //铺多选框
    $(".need-data").each(function () {
        let need_data = $(this);
        $.get("/" + need_data.closest("table").attr("data-table") + "/get_single_properties",
            {"property": need_data.attr("data-property")},
            function (data) {
                need_data.find(".checkbox").remove();
                $(data).each(function (index, item) {
                    let temp = Math.ceil(Math.random() * 1000);
                    need_data.append(
                        '<div class="checkbox checkbox-info checkbox-circle">'
                        + '<input type="checkbox" id="item_id_' + temp + '">'
                        + '<label for="item_id_' + temp + '">' + item + '</label>'
                        + '</div>'
                    )
                })
            });
    });

    //table 宽度自动
    $(".table-div table th").each(function (index, thTemp) {
        $(thTemp).width($(thTemp).width());
    });

    //如果没有导入laydate包 导入一下并且让输入框生效
    if (typeof (lay) === "undefined") {
        loadScript('/vendor/laydate_custom/laydate.js', function () {
            lay_date();
        });
        // import('<script src="../../vendor/laydate_custom/laydate.js"></script>');
    } else {
        lay_date();
    }


});


//tableUtils的导出全部按钮
function exportFull( ) {
    //先请求全部数据 然后封装成table标签的string形式 最后导出
    let formTemp = $("#searchForm");
    let newData = Object.assign({}, tableConfig.myCustomData);
    //重新生成pageNum , pageSize
    delete newData["pageNum"];
    delete newData["pageSize"];
    let params = $.param( newData ) + '&' + formTemp.serialize();
    $.get("/table_utils", params, function (data) {

        //生成表头
        let ths = "";
        $( tableConfig.cols ).each( function () {
            ths += "<th>"+this.name+"</th>";
        } );

        let tableTemp = $(
            "<div></div>"
        );
        $(".no-copy-show-altrowstable").clone(true).appendTo(tableTemp);
        let table = tableTemp.find("table.no-copy-show-altrowstable");
        if ( tableConfig.count ) {
            $(".table-utils-count-table tr").clone(true).appendTo( table.find("tbody") );
        }
        table.find("tbody").empty();
        showData4TableDom( data.content , table );
        tableConfig.rollback( table );
        //回掉完适配按钮权限
        try {
            window.top.showPermissionsElem( table );
        } catch (e) {
        }
        //添加合计行
        if ( tableConfig.count ) {
            table.find("tbody").append("<tr>"+getCountTr( table )+"</tr>");
        }
        exportFullDataTemp = table ;
        exportExcel( table  );

    } , "json");

}
let exportFullDataTemp = null;

//获取导出内容
function exportExcel( tableDom , rollback  ) {
    if (typeof ( $("<table></table>").tableExport ) === "undefined") {
        loadScript('/vendor/tableExprot/jquery.table2excel.js', function () {
            tableDom.table2excel({
                exclude: ".btn",
                name: "Excel Document Name",
                filename: $("title").text() + new Date().getFullYear() + "-" + ( new Date().getMonth() + 1 ) + "-" + new Date().getDate(),
                exclude_img: true,
                exclude_links: true,
                exclude_inputs: true
            });
            if ( typeof (rollback ) === "function" ) {
                rollback();
            }
        });
    } else {
        tableDom.table2excel({
            exclude: ".btn",
            name: "Excel Document Name",
            filename: $("title").text() + new Date().getFullYear() + "-" + ( new Date().getMonth() + 1 ) + "-" + new Date().getDate(),
            exclude_img: true,
            exclude_links: true,
            exclude_inputs: true
        });
        if ( typeof (rollback ) === "function" ) {
            rollback();
        }
    }
}

//匹配时间选择框
function lay_date() {
    lay('.laydate_double').each(function () {
        let thisTemp = this;
        //定义接收本月的第一天和最后一天
        let startDate1 = new Date(new Date().setDate(1));
        let endDate1 = new Date(new Date(new Date().setMonth(new Date().getMonth() + 1)).setDate(0));
        endDate1.setDate(endDate1.getDate() + 1);
        //定义接收上个月的第一天和最后一天
        let startDate2 = new Date(new Date(new Date().setMonth(new Date().getMonth() - 1)).setDate(1));
        let endDate2 = new Date(new Date().setDate(0));
        endDate2.setDate(endDate2.getDate() + 1);

        // noinspection JSUnusedLocalSymbols
        laydate.render({
            elem: this,
            type: 'date',
            range: '~',
            format: 'yyyy-M-d',
            //如果加个下面两个，那扩展的那几个快捷选择时间按扭的值就得判断处理一下
            //max:'2018-1-15',//可选最大日期
            //min:'2018-1-15',//可选最小日期
            extrabtns: [
                {id: 'today', text: '今天', range: [new Date(), new Date(new Date().setDate(new Date().getDate() + 1))]},
                {
                    id: 'lastday-7', text: '过去7天', range: [new Date(new Date().setDate(new Date().getDate() - 7)),
                        new Date()]
                },
                {
                    id: 'lastday-30',
                    text: '过去30天',
                    range: [new Date(new Date().setDate(new Date().getDate() - 30)),
                        new Date()]
                },
                {
                    id: 'yesterday', text: '昨天', range: [new Date(new Date().setDate(new Date().getDate() - 1)),
                        new Date()]
                },
                {id: 'thismonth', text: '本月', range: [startDate1, endDate1]},
                {id: 'lastmonth', text: '上个月', range: [startDate2, endDate2]}
            ],
            done: function (val, stDate, ovDate) {
                //当确认选择时间后调用这里
                $(thisTemp).val(val).focus();
            }
        });
    });
}
//匹配时间选择框
function layDateBySelector(selector) {
    lay(selector).each(function () {
        let thisTemp = this;
        //定义接收本月的第一天和最后一天
        let startDate1 = new Date(new Date().setDate(1));
        let endDate1 = new Date(new Date(new Date().setMonth(new Date().getMonth() + 1)).setDate(0));
        endDate1.setDate(endDate1.getDate() + 1);
        //定义接收上个月的第一天和最后一天
        let startDate2 = new Date(new Date(new Date().setMonth(new Date().getMonth() - 1)).setDate(1));
        let endDate2 = new Date(new Date().setDate(0));
        endDate2.setDate(endDate2.getDate() + 1);

        // noinspection JSUnusedLocalSymbols
        laydate.render({
            elem: this,
            type: 'date',
            range: '~',
            format: 'yyyy-M-d',
            //如果加个下面两个，那扩展的那几个快捷选择时间按扭的值就得判断处理一下
            //max:'2018-1-15',//可选最大日期
            //min:'2018-1-15',//可选最小日期
            extrabtns: [
                {id: 'today', text: '今天', range: [new Date(), new Date(new Date().setDate(new Date().getDate() + 1))]},
                {
                    id: 'lastday-7', text: '过去7天', range: [new Date(new Date().setDate(new Date().getDate() - 7)),
                        new Date()]
                },
                {
                    id: 'lastday-30',
                    text: '过去30天',
                    range: [new Date(new Date().setDate(new Date().getDate() - 30)),
                        new Date()]
                },
                {
                    id: 'yesterday', text: '昨天', range: [new Date(new Date().setDate(new Date().getDate() - 1)),
                        new Date()]
                },
                {id: 'thismonth', text: '本月', range: [startDate1, endDate1]},
                {id: 'lastmonth', text: '上个月', range: [startDate2, endDate2]}
            ],
            done: function (val, stDate, ovDate) {
                //当确认选择时间后调用这里
                $(thisTemp).val(val).focus();
            }
        });
    });
}

// noinspection JSUnusedLocalSymbols
function loadScript(src, callback) {
    let script = document.createElement('script'),
        head = document.getElementsByTagName('head')[0];
    script.type = 'text/javascript';
    script.charset = 'UTF-8';
    script.src = src;
    if (script.addEventListener) {
        script.addEventListener('load', function () {
            callback();
        }, false);
    } else { // noinspection JSUnresolvedVariable
        if (script.attachEvent) {
            script.attachEvent('onreadystatechange', function () {
                let target = window.event.srcElement;
                if (target.readyState === 'loaded') {
                    callback();
                }
            });
        }
    }
    head.appendChild(script);
}

// noinspection JSUnusedGlobalSymbols
function dynamicLoadCss(url, callback) {
    let head = document.getElementsByTagName('head')[0];
    let link = document.createElement('link');
    link.type = 'text/css';
    link.rel = 'stylesheet';
    link.href = url;
    if (link.addEventListener) {
        link.addEventListener('load', function () {
            callback();
        }, false);
    } else { // noinspection JSUnresolvedVariable
        if (link.attachEvent) {
            link.attachEvent('onreadystatechange', function () {
                let target = window.event.srcElement;
                if (target.readyState === 'loaded') {
                    callback();
                }
            });
        }
    }
    head.appendChild(link);
}

