//obj格式 [{id:id , name:name , subObject: [{id:id , name:name ....}] }]
function createMyTree(obj, selector) {
    let myTreeDom = $("" + selector);
    let myTreeId = Math.ceil(Math.random() * 1000);
    myTreeDom.append('<dl class="my-tree" id="myTree' + myTreeId + '"></dl>');
    let myTree = $("#myTree" + myTreeId);

    createMyTreeInner(obj, myTree, 1);
}

function createMyTreeInner(obj, jqDom, data_tree_level_temp) {
    $(obj).each(function (index, o) {

        let subTreeId = Math.ceil(Math.random() * 100000);
        let data_tree_level = data_tree_level_temp;
        let isClass = !$.isEmptyObject(o["subObject"]);
        jqDom.append(
            '<dd class="tree-info' + (isClass ? " tree-class" : "") + '" data-tree-level="' + data_tree_level + '">\n' +
            '        <span class="glyphicon glyphicon ' + (isClass ? "glyphicon-chevron-down" : "glyphicon-book") + '" aria-hidden="true"></span>\n' +
            '        <div class="checkbox checkbox-info">\n' +
            '            <input id="checkbox' + subTreeId + '" title="'+o["title"]+'" data-type="'+o["type"]+'" data-id="' + o["id"] + '" data-name="' + o["name"] + '" class="styled" type="checkbox">\n' +
            '            <label class="info-label" title="'+o["title"]+'" for="checkbox' + subTreeId + '">' + o["name"] + '</label>\n' +
            '        </div>\n' +
            '    </dd>');


        if (isClass) {
            jqDom.append('<div class="sub-tree" id="sub-tree' + subTreeId + '" ></div>');
            createMyTreeInner(o["subObject"], $("#sub-tree" + subTreeId), data_tree_level + 1);
        }
        //设置级联缩进
        $(".my-tree .tree-info").each(function (index, dd) {
            $(dd).css("padding-left", parseInt($(dd).attr("data-tree-level")) * 40 - 40);
        });

    });

}

//选择子项后 父项的样式(向上递归)
function set_select(thisTemp) {
    let sub_tree = $(thisTemp).closest(".sub-tree");
    let tree_class = sub_tree.prev(".tree-class");
    // noinspection JSValidateTypes
    if (sub_tree.children("dd").children(".checkbox").children("input[type='checkbox']:checked").length > 0) {
        tree_class.children(".checkbox").removeClass("checkbox-info");
        tree_class.children(".checkbox").children("input[type='checkbox']").prop("checked", true);
    }
    // noinspection JSValidateTypes
    if (sub_tree.children("dd").children(".checkbox-info").children("input[type='checkbox']:checked").length === sub_tree.children("dd").children(".checkbox").children("input[type='checkbox']").length) {
        if (!tree_class.children(".checkbox").hasClass("checkbox-info")) {
            tree_class.children(".checkbox").addClass("checkbox-info");
        }
        tree_class.children(".checkbox").children("input[type='checkbox']").prop("checked", true);
    }
    // noinspection JSValidateTypes
    if (sub_tree.children("dd").children(".checkbox").children("input[type='checkbox']:checked").length === 0) {
        tree_class.children(".checkbox").children("input[type='checkbox']").prop("checked", false);
        tree_class.children(".checkbox").addClass("checkbox-info");
    }

    if (sub_tree.length > 0) {
        set_select(tree_class.children(".checkbox").children("input[type='checkbox']"));
    }
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
let type ;
if ( typeof ( getParamForUrl )=== "undefined" ) {
    loadScript("/js/xudazhu.js" , function () {
        type = getParamForUrl("type");
    });
} else {
    type = getParamForUrl("type");
}


$(function () {

    //设置展开收起
    $(document).on("click", ".tree-info .glyphicon-chevron-right,  .tree-info .glyphicon-chevron-down", function () {
        if ($(this).closest(".tree-info").next().css("display") !== "none") {
            $(this).closest(".tree-info").next().slideUp();
            $(this).addClass("glyphicon-chevron-right").removeClass("glyphicon-chevron-down");
        } else {
            $(this).closest(".tree-info").next().slideDown();
            $(this).addClass("glyphicon-chevron-down").removeClass("glyphicon-chevron-right");
        }

    });

    //设置全选按钮
    $(document).on("click", ".my-tree .tree-class", function () {
        if (!$(this).find(".checked").hasClass("checkbox-info")) {
            $(this).find(".checkbox").addClass("checkbox-info");
        }
        let checkedTemp = $(this).find("input[type='checkbox']")[0].checked;
        $(this).next().find("input[type='checkbox']").prop("checked", checkedTemp);
        $(this).next().find(".checkbox").addClass("checkbox-info");
    });

    //选择子项后 父项的样式
    $(document).on("click", ".my-tree .tree-info input[type='checkbox']", function () {
        set_select(this);

        //获取已选择的权限
        setTimeout(function () {
            let selected_div = $("#selected-div");
            selected_div.empty();
            $(".my-tree .tree-info .checkbox-info input[type='checkbox']:checked").each(function () {
                if  ( $(this).attr("data-type")  === myType ||  type === $(this).attr("data-type")    ) {
                    selected_div.append("<div data-id='"+$(this).attr("data-id")+"'>"+$(this).attr("data-name")+"</div>")
                }
            });
        } , 50);
    });

    let tips;
    $(document).on("mouseenter" , ".info-label" , function () {
        if ( $(this).prop("title") !== "" ) {
            tips =layer.tips("<span style='color: #2E2D3C;'>"+$(this).prop("title")+"</span>",this,{tips:[1,'#fff']});
        }
    });
    $(document).on("mouseleave" , ".info-label" , function () {
        layer.close(tips);
    });
    // $('.info-label').on({
    //     mouseenter:function(){
    //         tips =layer.tips($(this).prop("title"),this,{tips:2});
    //     },
    //     mouseleave:function(){
    //         layer.close(tips);
    //     }
    // });

});

