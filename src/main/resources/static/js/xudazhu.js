// 1 通过key 查找 value xxx.html?id=3&name=xudazhu
function getParamForUrl(keyName) { // getParamForUrl("idawdawd")
    //  structure.html?key1=value1&key2=value2
    let url = decodeURIComponent(window.location.href);
    //  key1=value1&key2=value2
    let allKeysAndValues = url.substring(url.indexOf("?") + 1);
    // [ "key1=value1" , "key2=value2"]
    let strings = allKeysAndValues.split("&");
    // "key1=value1"
    // "key2=value2"
    for (const index in strings) {
        let keyAndValue = strings[index];
        // ["key1" , "value1"]
        let strings1 = keyAndValue.split("=");
        if (strings1[0] === keyName) {
            return (strings1[1] === ""  || strings1[1] === "undefined" || strings1[1] === "null") ? null : strings1[1];
        }
    }
    return null;
}

//2 表单校验 使用方法 在必须要填的表单添加一个类need-input ,该方法需要引用layer的tips
function formChecking() {
    let check = true;
    $(".need-input").each(function () {
        let tipsInfo = $(this).parent().prev().text();
        let tipsInfo2 = tipsInfo.substring(0,tipsInfo.length-1);
        if ($(this).val() === null || $(this).val().replace(/(^\s+)|(\s+$)/g, "") === "") {
            let that = this;
            layer.tips( tipsInfo2 + '不能为空', that, {
                tips: [1, '#3595CC'],
                time: 4000
            });
            return check = false;
        }
    });
    return check;

}

// 格式化时间 new Date(xxDate.time) 2019 0318
function formatDate(now , length) {
    // console.log(now)
    let dateString = "";
    dateString += now.getFullYear() < 10 ? "0" + now.getFullYear() : now.getFullYear();
    if ( length > 4 ) {
        dateString = dateString + "-" + (now.getMonth() + 1  < 10 ? "0" + (now.getMonth() + 1) : now.getMonth() + 1);
        dateString = dateString + "-" + (now.getDate() < 10 ? "0" + now.getDate() : now.getDate() );
    }
    if ( length > 8 ) {
        dateString = dateString + " " + (now.getHours() < 10 ? "0" + now.getHours() : now.getHours() );
        dateString = dateString + ":" + (now.getMinutes() < 10 ? "0" + now.getMinutes() : now.getMinutes() );
    }
    // let year = now.getFullYear() < 10 ? "0" + now.getFullYear() : now.getFullYear();
    // let month = now.getMonth() + 1  < 10 ? "0" + (now.getMonth() + 1) : now.getMonth() + 1;
    // let date = now.getDate() < 10 ? "0" + now.getDate() : now.getDate();
    // let hour = now.getHours() < 10 ? "0" + now.getHours() : now.getHours();
    // let minute = now.getMinutes() < 10 ? "0" + now.getMinutes() : now.getMinutes();
    // let second = now.getSeconds() < 10 ? "0" + now.getSeconds() : now.getSeconds();
    // return year + "-" + month + "-" + date + " " + hour + ":" + minute + ":"
    //     + second;
    return dateString;
}


// noinspection JSUnusedGlobalSymbols
function getJson4Dom (jqDom) {
    let json = {};
    jqDom.find("[name]").each(function () {
        if ( this.value !== "" ) {
            json[this.name] = this.value;
        }
    });
    return json;

}

//传入name 获取json对象此name的值 如若没有 返回null
function getValueByName( object , name) {
    if ( name === null || name === undefined || name === "undefined" ) {
        return "";
    }
    if ( name.lastIndexOf(".") === -1 ) {
        if ( object.hasOwnProperty(name)  ) {
            return object[name] === undefined ? "" : object[name];
        }
    }
    let split = name.split(".");
    let temp = object[split[0]];
    if ( temp == null ) {
        return "";
    }
    for(let j = 1,len = split.length; j < len; j++){
        let nameTemp = split[j];
        if ( j > 0 && j === split.length -1 ) {
            return temp[nameTemp] === undefined ? "" : temp[nameTemp];
        }

        if ( nameTemp.indexOf("[") !== -1 ) {
            let indexTemp = 0;
            if ( nameTemp.indexOf("[n]") === -1 ) {
                indexTemp = parseInt( nameTemp.substring(nameTemp.indexOf("[") + 1 , nameTemp.indexOf("]") ) );
            }
            temp = temp[nameTemp.substring(0 , nameTemp.indexOf("["))][indexTemp];
        } else if (  ! $.isEmptyObject(temp[nameTemp]) ) {
            temp = temp[nameTemp];
        }


        // if ( ! $.isEmptyObject(temp[nameTemp]) ) {
        //     if ( nameTemp.indexOf("[") !== -1 ) {
        //         let indexTemp = 0;
        //         if ( nameTemp.indexOf("[n]") === -1 ) {
        //             indexTemp = parseInt( nameTemp.substring(nameTemp.indexOf("[") + 1 , nameTemp.indexOf("]") ) );
        //         }
        //         object = temp[indexTemp][nameTemp];
        //     } else {
        //         object = temp[nameTemp];
        //     }
        // }
    }
}


try {
    $(function () {
        if ( window.top.userTemp === undefined || window.top.userTemp.userId === undefined) {
            $.get("/user/get_session_user" , function (data) {
                window.top.userTemp = data;
            } , "json" );
        }
    });
} catch (e) {

}

//通用铺数据代码
// tableName 表名
// noinspection JSUnusedLocalSymbols
function showDataTemp(tableName ) {
    let id = getParamForUrl("id");
    if ( id === null ) {
        return false;
    }
    $.get( "/flow_node/data_temp" , {id : id  , table : tableName } ,  function (data) {
        showData4Object(data);
    } , "json" );


}

//处理只读状态
function fixWay(way) {
    let body = $("body");
    //审核状态
    // body.removeClass("way");
    if ( way === "audit" ) {
        body.find(".audit-only-write").removeAttr("readonly");
        $("input:not(.audit-only-write , .layui-unselect) , select:not(.audit-only-write), textarea:not(.audit-only-write) ").attr("readonly" , "readonly");
    } else if ( way === "add" ) {
        body.find(".audit-only-write").attr("readonly" , "readonly");
    } else
    if ( way === "info" || way === null  ) {
        $("input  , select , textarea").attr("readonly" , "readonly");
    } else {
        $("input  , select , textarea").attr("readonly" , "readonly");
    }

    $(".lay_date[readonly]").removeClass("lay_date").each(function (index , elem) {
       elem.onFocus = null;
    });
}


//如果以有json对象
//传入本页主体json对象
function showData4Object(object ) {
    if ( ! $.isEmptyObject(object) ) {
        // noinspection ES6ConvertVarToLetConst
        // var object = data;
        $("[lay-filter]").each(function () {
            // if ( this.value === "" ) {
                let nameTemp = $(this).prop("name");
                if ( nameTemp === "undefined" || nameTemp === "" ) nameTemp = $(this).attr("data-notice-path");
                let valueTemp = getValueByName( object , nameTemp );
                if ( valueTemp !== undefined ) {
                    valueTemp = "" + valueTemp;
                    // console.log(typeof ( valueTemp ) + valueTemp);
                }
                $(this).val(valueTemp);
                try {
                    // $(this).next(".layui-form-select").find("dd[lay-value='"+valueTemp+"']").click();
                    $(this).next(".layui-form-select").find("dd").each(function (index , dd) {
                        if ( $(dd).attr("lay-value") === valueTemp ) {
                            return $(dd).click();
                        }

                    });
                } catch (e) {
                }
            // }
        });
        $("[name]").each(function () {
            // if ( this.value === "" ) {
                $(this).val( getValueByName( object , $(this).prop("name")) );
            // }
        });
        $("[data-notice-path]").each(function () {
            let value = getValueByName( object , $(this).attr("data-notice-path"));
            // if ( this.value === "" ) {
                $(this).val( value ) ;
            // }
        });
        $("[data-text]").each(function () {
            let value = getValueByName( object , $(this).attr("data-text"));
            $(this).text( value ) ;
        });

    }

}

//重写layui 的form.render("select") 方法 让其支持单个select渲染
function renderFix(layFilter , rollback) {
    layui.use(['element', 'layer', 'form'], function () {
        // noinspection JSUnusedLocalSymbols
        let form = layui.form;
        let allSelect = $("select:not([lay-ignore])");
        allSelect.attr("lay-ignore", "lay-ignore");
        $("select[lay-filter='" + layFilter + "']").removeAttr("lay-ignore");
        layui.form.render('select');
        allSelect.removeAttr("lay-ignore");
        setTimeout(function () {
            if ( typeof (renderFixed ) === "function" ) {
                renderFixed(layFilter);
            }
            if ( typeof (rollback ) === "function" ) {
                rollback(layFilter);
            }
        });

    });
}
// 简单固定表头的方法
function fixedHead(dom) {
    let dom_h = dom.height();// 表格高度
    let win_h = $(window).height();// 窗口高度
    if(dom_h < ($(window).height())*0.8){//表格高度过低,不需要表头固定
        return false;
    }
    dom.siblings(".copyTable").remove();//去除再添加
    dom.addClass("trueTable");//表格添加类
    dom.closest("div").addClass("contain");// 表格的父级元素添加类
    dom.closest(".contain").height(win_h*0.8);// 表格高度设置
    dom.before('<table class="layui-table copyTable"></table>');//追加表格标签
    let copyThead = dom.find("thead").clone();//克隆表头
    dom.prev(".copyTable").append(copyThead);//在假table里追加表头
    dom.prev(".copyTable").width(dom.width());//table 的宽度设置一致
    let copyThWidth = dom.prev(".copyTable").find("th");
    dom.find("th").each(function (index,th) {
        copyThWidth.eq(index).outerWidth($(th).outerWidth());// 宽度一致设置
        copyThWidth.eq(index).outerHeight($(th).outerHeight()+1);// 高度一致设置
    })
}



