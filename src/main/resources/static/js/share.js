$(function () {
    //1 Tab的切换功能
    layui.use('element', function () {
        var $ = layui.jquery
            , element = layui.element; //Tab的切换功能，切换事件监听等，需要依赖element模块
    });

    // 2 开启layui的form表单,可以实现复选框的功能
    layui.use(['form', 'layedit', 'laydate'], function () {
        var form = layui.form;
    });

    // 3复选框全选
    var layui_unselect;
    var all_delete_btn;
    var allCheckNum;
    var checkedNum;
    $(document).on('click', '.layui-table thead tr th:nth-child(1) .layui-unselect', function () {
        //找到当前表格中tbody里面所有的layui-unselect
        layui_unselect = $(this).closest('.layui-table').find('tbody .layui-unselect');
        all_delete_btn = $(this).closest('.layui-tab-item').find('.all_delete_btn');
        checkedNum = $(this).closest(".layui-table").children('tbody').children().children().children("input[type='checkbox']").length;
        if ($(this).prev()[0].checked) {
            layui_unselect.addClass('layui-form-checked');
            layui_unselect.prev().prop("checked", true);
            all_delete_btn.show();
            $(this).closest('.layui-tab-item').find('.checked-num').text(checkedNum);
        } else {
            layui_unselect.removeClass('layui-form-checked');
            layui_unselect.prev().prop("checked", false);
            all_delete_btn.hide();
            $(this).closest('.layui-tab-item').find('.checked-num').text('0');
        }

    })

    //4 tbody里面的复选框选择事件
    $(document).on('click', '.layui-table tbody tr td:nth-child(1) .layui-unselect', function () {
        all_delete_btn = $(this).closest('.layui-tab-item').find('.all_delete_btn');
        allCheckNum = $(this).closest("tbody").children().children().children("input[type='checkbox']").length;
        checkedNum = $(this).closest("tbody").children().children().children("input[type='checkbox']:checked").length;
        var select_all = $(this).closest('.layui-table').find('th .layui-unselect');
        if (allCheckNum === checkedNum) {
            //    tbody全部选中是时 th的全选按钮生效
            select_all.prev().prop("checked", true);
            select_all.addClass('layui-form-checked');
        } else {
            //    部分选中
            select_all.prev().prop("checked", false);
            select_all.removeClass('layui-form-checked');
        }
        if (checkedNum === 0) {
            all_delete_btn.hide();
        } else {
            all_delete_btn.show();
        }
        //已选择几项
        $(this).closest('.layui-tab-item').find('.checked-num').text(checkedNum);
    })

    // 5单次删除
    $(document).on('click', '.delete-btn', function () {
        var thisBtn = $(this);
        var select_all = $(this).closest('.layui-tab-item').find('th .layui-unselect');
        swal({
                title: "是否确认删除?",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Yes, delete it!",
                closeOnConfirm: false
            },
            function () {
                swal("Deleted!", "", "success");
                var checkboxTemp = thisBtn.closest("tr").find("input[type='checkbox']");
                var checkedNum = thisBtn.closest('.layui-tab-item').find('.checked-num').text();
                var table = thisBtn.closest("table");
                //如果当前需要删除的这一行处于被选择状态,则选中数量减1
                if (checkboxTemp[0].checked) {
                    thisBtn.closest('.layui-tab-item').find('.checked-num').text(parseInt(checkedNum) - 1);
                }
                // 如果单次删除过后未选中任何一个checkbox,则全选按钮隐藏
                if (thisBtn.closest('.layui-tab-item').find('.checked-num').text() === "0") {
                    thisBtn.closest('.layui-tab-item').find('.all_delete_btn').hide();
                    select_all.prev().prop("checked", false);
                    select_all.removeClass('layui-form-checked');
                }
                thisBtn.parent().parent().remove();
                table.find(".beans-id").each(function (index, inputTemp) {
                    var name = $(inputTemp).prop("name");
                    $(inputTemp).prop("name", name.substring(0, name.lastIndexOf("[")) + "[" + index + "]" + name.substring(name.lastIndexOf("]") + 1))
                });
                if ( typeof  deleteThis === "function" ) {
                    deleteThis();
                }
            });
    });

    // 6批量删除
    $(document).on('click', '.all_delete_btn', function () {
        var thisBtn = $(this);
        var count = $(this).next().children(".checked-num").text();
        var checked = $(this).siblings("table").children('tbody').children('tr').children('td').children("input[type='checkbox']:checked");
        var select_all = $(this).closest('.layui-tab-item').find('th .layui-unselect');
        swal({
                title: "是否确认删除这" + count + "项?",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Yes, delete it!",
                closeOnConfirm: false
            },
            function () {
                swal("Deleted!", "", "success");
                checked.each(function (index, checkbox) {
                    $(checkbox).parent().parent().remove();
                });
                // 重新计数
                var checkedNum = thisBtn.siblings("table").children('tbody').children('tr').children('td').children("input[type='checkbox']:checked").length;
                thisBtn.closest('.layui-tab-item').find('.checked-num').text(parseInt(checkedNum));
                if (checkedNum === 0) {
                    thisBtn.hide();
                    select_all.prev().prop("checked", false);
                    select_all.removeClass('layui-form-checked');
                }

                thisBtn.siblings("table").find(".beans-id").each(function (index, inputTemp) {
                    var name = $(inputTemp).prop("name");
                    $(inputTemp).prop("name", name.substring(0, name.lastIndexOf("[")) + "[" + index + "]" + name.substring(name.lastIndexOf("]") + 1))
                });
                if ( typeof  deleteAll === "function" ) {
                    deleteAll();
                }
            });
    });

});