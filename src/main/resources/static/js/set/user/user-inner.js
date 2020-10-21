
let role_ids = [];
// let job_ids = [];



$(function () {



    //1 Tab的切换功能
    layui.use('element', function () {
        // let $ = layui.jquery , element = layui.element; //Tab的切换功能，切换事件监听等，需要依赖element模块
        return [layui.jquery , layui.element];
    });

    layui.use(['form', 'layedit', 'laydate'], function () {
        let form = layui.form;
        form.render("checkbox");
    });

    //2 提交
    let userId = getParamForUrl("id");
    $(document).on("click",".confirm-btn",function () {
        // 表单校验 引用 xudazhu.js
        if ( ! formChecking() ) {
            return false;
        }
         //该方法不能执行
        let method = getParamForUrl("id") === null ? "POST" : "PUT";
        let itemData = $.param({"_method": method}) + "&" + $("#inputForm").serialize();
        $.post("/user" , itemData, function (data) {
            if (data) {
                layer.msg('操作成功 2秒后自动刷新', {
                    time: 2000 //2秒关闭（如果不配置，默认是3秒）
                } ,  function () {
                    window.location.reload();
                });
                window.parent.getAPageData( window.parent.pageDataA["pageNum"] , window.parent.pageDataA["pageSize"]);
            } else {
                layer.msg('添加失败');
            }
        }, "json").fail(function (res) {
            layer.msg('数据提交失败 请刷新重试 ');
            console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据提交失败 请刷新重试");
        }, "json");

    });

    // 3如果带有id,则开始铺用户数据
    if(userId !== null ){
        $("[name=userPassword]").removeClass("need-input");
        $.get("/user/id",{"userId":userId},function (data) {
            let content = data.content[0];
            for (const key in content ) {
                // 3-1 铺数据
                if ( content.hasOwnProperty(key)) {
                    $('[name="'+key+'"]').val(content[key]);
                }
            }
            showRoles(content["roleBeans"] , true );

            $("[name='userPassword']").val("").prop("placeholder" , content.userPassword);
            $("#jobId").val(content["jobBean"]["jobId"]);
            $("#jobName").val(content["jobBean"]["jobName"]);
            

        })
    }


    //4 选择权限|用户
    $(document).on('click', '.select-tree-btn', function () {
        let href = $(this).attr("data-href");
        layer.ready(function () {
            layerIndex = layer.open({
                type: 2,
                title: "选择职位",
                maxmin: false,
                area: ['700px', '450px'],
                content: href
            });
        });
    });

    // 5关闭弹出层
    $(document).on('click', '.cancel-btn', function () {
        window.parent.layer.close(window.parent.layerIndex);
    });

});

function  jobTreeSelected( ids){
    $.get("/job" , {id : ids} , function (data) {
        let job = data.content[0];
        $("#jobId").val(job["jobId"]);
        $("#jobName").val(job["jobName"]);
    } , "json");
}

// function showJobs(jobBeans){
//     job_ids = [];
//     let tBodyTemp = $("#jobs");
//     tBodyTemp.empty();
//     $(jobBeans).each(function (index, job) {
//         job_ids.push(job["jobId"]);
//         tBodyTemp.append('<tr>\n' +
//             '    <td>\n' +
//             '        <input type="checkbox" lay-skin="primary">\n' +
//             '    </td>\n' +
//             '    <td>' +
//             '<span>'+job["jobId"]+'</span>' +
//             '<input type="hidden" class="beans-id" name="jobBeans['+index+'].jobId" value="'+job["jobId"]+'">' +
//             '</td>\n' +
//             '    <td>'+job["departmentBean"]["companyBean"]["companyName"]+'</td>\n' +
//             '    <td>'+job["departmentBean"]["departmentName"]+'</td>\n' +
//             '    <td>'+job["jobName"]+'</td>\n' +
//             '    <td>\n' +
//             '        <button type="button" class="layui-btn layui-btn-xs layui-btn-danger delete-btn">删除</button>\n' +
//             '    </td>\n' +
//             '</tr>');
//     });
// }

//修复权限角标
function renderIndex4Tbody( tBody ) {
    // let moneyBackMoney = $(".moneyBackMoney");
    tBody.find("tr").each(function (index , trTemp ) {
        $( trTemp ).find("[name]").each(function () {
            let nameTemp = $(this).prop("name");
            if ( nameTemp.indexOf("[") !== -1 ) {
                // noinspection RegExpRedundantEscape
                $(this).prop("name" , nameTemp.replace(/\[\d+\]/ , "["+index+"]"));
            }
        });

    });
}

function showRoles(roleBeans , empty ) {
    role_ids = [];
    let tBodyTemp = $("#roles");
    if ( empty ) {
        tBodyTemp.empty();
    }
    $(roleBeans).each(function (index , role) {
        role_ids.push(role.roleId);
        tBodyTemp.append('<tr>\n' +
            '    <td>\n' +
            '        <input title="" type="checkbox" lay-skin="primary">\n' +
            '    </td>\n' +
            '    <td>' +
            '    <span>'+role["roleId"]+'</span>' +
            '    <input type="hidden" class="beans-id" name="roleBeans['+index+'].roleId" value="'+role["roleId"]+'">' +
            '    </td>\n' +
            '    <td>'+role.roleName+'</td>\n' +
            '    <td>'+role.roleDescribe+'</td>\n' +
            '    <td>\n' +
            '        <button type="button"\n' +
            '                class="layui-btn layui-btn-xs layui-btn-danger delete-btn">删除\n' +
            '        </button>\n' +
            '    </td>\n' +
            '</tr>');
    });
    //重新渲染
    // layui.form.render('checkbox');
    renderIndex4Tbody(tBodyTemp);
}

function  roleTreeSelected( ids){
    $.get("/role" , {"$D.roleId": ids} , function (data) {
        showRoles(data.content);
        //重新渲染
        layui.form.render('checkbox');
    } , "json").fail(function (res) {
        sweetAlert('数据获取失败 请刷新重试 ');
        console.log(res.hasOwnProperty("responseJSON") ? res["responseJSON"].message : "数据获取失败 请刷新重试");
    });
}

new Vue({
    el: '#app',

    watch: {
        filterText(val) {
            this.$refs.tree.filter(val);
        }
    },

    methods: {
        filterNode(value, data) {
            if (!value) return true;
            return data.label.indexOf(value) !== -1;
        }
        ,
        getCheckedNodes() {
            console.log(this.$refs.tree.getCheckedNodes());
        }
        ,
        getCheckedKeys() {
            console.log(this.$refs.tree.getCheckedKeys());
        }

    },

    data() {
        return {
            filterText: '',
            data: [{
                id: 1,
                label: '一级 1',
                children: [{
                    id: 4,
                    label: '二级 1-1',
                    children: [{
                        id: 9,
                        label: '三级 1-1-1'
                    }, {
                        id: 10,
                        label: '三级 1-1-2'
                    }]
                }]
            }, {
                id: 2,
                label: '一级 2',
                children: [{
                    id: 5,
                    label: '二级 2-1'
                }, {
                    id: 6,
                    label: '二级 2-2'
                }]
            }, {
                id: 3,
                label: '一级 3',
                children: [{
                    id: 7,
                    label: '二级 3-1'
                }, {
                    id: 8,
                    label: '二级 3-2'
                }]
            }],
            defaultProps: {
                children: 'children',
                label: 'label'
            }
        };
    }

})
