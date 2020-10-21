var str = "";
var addStaff = []; //员工
var addExecutiveDirector = []; //主管
var addCaptain = []; //队长
$(function () {
    // 获取url的传值函数
    Request = {
        QueryString : function(item){
            var svalue = location.search.match(new RegExp("[\?\&]" + item + "=([^\&]*)(\&?)","i"));
            return svalue ? svalue[1] : svalue;
        }
    }
    var departmentId = Request.QueryString("department");//获取部门
    var professionId = Request.QueryString("professionId");//获取工种ID
    //  判断添加还是编辑
    if(professionId){
           layui.use(['form','table'], function(){
              // 所属部门
               $.ajax({
                   url: '/profession/id',
                   type: "get",
                   data : {
                       id : professionId
                   },
                   dataType: "json",
                   success: function (obj) {
                       var data = obj.data;
                       var staffBeanList = data.staffBeanList;
                       var supervisorBeanList = data.supervisorBeanList;
                       var captainBeanList = data.captainBeanList;
                       var professionUnitId = data.professionUnitBean.professionUnitId;
                       var department = data.departmentBean.departmentName;
                       var staffBeaHtml = "";
                       var supervisorBeanHtml = "";
                       var captainBeanHtml = "";
                       for (var i = 0; i < staffBeanList.length; i++) {  // 员工
                          staffBeaHtml +='<div class="layui-col-sm6 layui-col-xs12  layui-col-md4">'
                          staffBeaHtml +='<div class="layui-form-item">'
                          staffBeaHtml += '<label class="layui-form-label"><span class="pms-required">*</span>' + staffBeanList[i].postBean.postName + '</label>'
                          staffBeaHtml += '<div class="layui-input-inline">'
                          staffBeaHtml += '<input type="number" name="staffBeanList[' + i + '].staffPrice" autocomplete="off" value="'+staffBeanList[i].staffPrice+'" placeholder="请输入" class="layui-input"><input value="'+staffBeanList[i].postBean.postId+'" name="staffBeanList[' + i + '].postBean.postId" hidden="hidden"><input value="'+staffBeanList[i].staffId+'" name="staffBeanList[' + i + '].staffId" hidden="hidden">'
                          staffBeaHtml += '</div>'
                          staffBeaHtml += '</div>'
                          staffBeaHtml += '</div>'
                       }
                       $(".layui-staff").append(staffBeaHtml);
                       for (var i = 0; i < supervisorBeanList.length; i++) {  // 主管
                          supervisorBeanHtml +='<div class="layui-col-sm6 layui-col-xs12  layui-col-md4">'
                          supervisorBeanHtml +='<div class="layui-form-item">'
                          supervisorBeanHtml += '<label class="layui-form-label"><span class="pms-required">*</span>' + supervisorBeanList[i].postBean.postName + '</label>'
                          supervisorBeanHtml += '<div class="layui-input-inline">'
                          supervisorBeanHtml += '<input type="number" name="supervisorBeanList[' + i + '].supervisorPrice" autocomplete="off" value="'+supervisorBeanList[i].supervisorPrice+'" placeholder="请输入" class="layui-input"><input value="'+supervisorBeanList[i].postBean.postId+'" name="supervisorBeanList[' + i + '].postBean.postId" hidden="hidden"><input value="'+supervisorBeanList[i].supervisorId+'" name="supervisorBeanList[' + i + '].supervisorId" hidden="hidden">'
                          supervisorBeanHtml += '</div>'
                          supervisorBeanHtml += '</div>'
                          supervisorBeanHtml += '</div>'
                       }
                       $(".layui-executiveDirector").append(supervisorBeanHtml);
                       for (var i = 0; i < captainBeanList.length; i++) {  // 队长
                          captainBeanHtml +='<div class="layui-col-sm6 layui-col-xs12  layui-col-md4">'
                          captainBeanHtml +='<div class="layui-form-item">'
                          captainBeanHtml += '<label class="layui-form-label"><span class="pms-required">*</span>' + captainBeanList[i].postBean.postName + '</label>'
                          captainBeanHtml += '<div class="layui-input-inline">'
                          captainBeanHtml += '<input type="number" name="captainBeanList[' + i + '].captainPrice" autocomplete="off" value="'+captainBeanList[i].captainPrice+'" placeholder="请输入" class="layui-input"><input value="'+captainBeanList[i].postBean.postId+'" name="captainBeanList[' + i + '].postBean.postId" hidden="hidden"><input value="'+captainBeanList[i].captainId+'" name="captainBeanList[' + i + '].captainId" hidden="hidden">'
                          captainBeanHtml += '</div>'
                          captainBeanHtml += '</div>'
                          captainBeanHtml += '</div>'
                       }
                       $(".layui-captain").append(captainBeanHtml);
                       // 所属部门
                        $.ajax({
                            url: '/department/department_name',
                            type: "get",
                            data : {
                                departmentName :department
                            },
                            dataType: "json",
                            success: function (obj) {
                                var departmentId = obj[0].departmentId;
                                staff(departmentId);
                                executiveDirector(departmentId);
                                captain(departmentId);
                            }
                        });

                        // 员工
                        function staff(departmentId){
                           $.ajax({
                               url: '/post',
                               type: "get",
                               data : {
                                   postLevel :'员工',
                                   'departmentBean.departmentId':departmentId
                               },
                               dataType: "json",
                               success: function (obj) {
                                   addStaff = obj;
                               }
                           });
                        }

                        // 主管
                        function executiveDirector(departmentId){
                            $.ajax({
                                 url: '/post',
                                 type: "get",
                                 data : {
                                     postLevel :'主管',
                                     'departmentBean.departmentId':departmentId
                                 },
                                 dataType: "json",
                                 success: function (obj) {
                                    addExecutiveDirector = obj;
                                 }
                            });
                        }

                        // 队长
                       function captain(departmentId){
                           $.ajax({
                               url: '/post',
                               type: "get",
                               data : {
                                   postLevel :'队长',
                                   'departmentBean.departmentId':departmentId
                               },
                               dataType: "json",
                               success: function (obj) {
                                    addCaptain = obj;
                               }
                           });
                       }
                       // 以前没有的岗位，再次补充
                       setTimeout(function () {
                          // 员工;
                          for (var j = 0; j < addStaff.length; j++) {
                               var id = addStaff[j].post_Id;
                               for (var i = 0; i < staffBeanList.length; i++) {
                                 if(staffBeanList[i].postBean.postId == id){
                                     addStaff.splice(j--,1);
                                     break;
                                  }
                              }
                          }
                          var staffBeanListLength = staffBeanList.length;
                          var staffBeanListhtml = "";
                          for (var i = 0; i < addStaff.length; i++) {
                              var a = i + staffBeanListLength;
                              staffBeanListhtml +='<div class="layui-col-sm6 layui-col-xs12  layui-col-md4">'
                              staffBeanListhtml +='<div class="layui-form-item">'
                              staffBeanListhtml += '<label class="layui-form-label"><span class="pms-required">*</span>' + addStaff[i].post_name + '</label>'
                              staffBeanListhtml += '<div class="layui-input-inline">'
                              staffBeanListhtml += '<input type="number" name="staffBeanList[' + a + '].staffPrice" autocomplete="off" placeholder="请输入" class="layui-input"><input value="'+addStaff[i].post_Id+'" name="staffBeanList[' + a + '].postBean.postId" hidden="hidden">'
                              staffBeanListhtml += '</div>'
                              staffBeanListhtml += '</div>'
                              staffBeanListhtml += '</div>'
                          }
                          $(".layui-staff").append(staffBeanListhtml);
                          // 主管
                          for (var j = 0; j < addExecutiveDirector.length; j++) {
                              var id = addExecutiveDirector[j].post_Id;
                              for (var i = 0; i < supervisorBeanList.length; i++) {
                                 if(supervisorBeanList[i].postBean.postId == id){
                                    addExecutiveDirector.splice(j--,1);
                                    break;
                                 }
                             }
                          }
                          var supervisorBeanListLength = supervisorBeanList.length;
                          var supervisorBeanListhtml = "";
                          for (var i = 0; i < addExecutiveDirector.length; i++) {
                                var a = i + supervisorBeanListLength;
                                supervisorBeanListhtml +='<div class="layui-col-sm6 layui-col-xs12  layui-col-md4">'
                                supervisorBeanListhtml +='<div class="layui-form-item">'
                                supervisorBeanListhtml += '<label class="layui-form-label"><span class="pms-required">*</span>' + addExecutiveDirector[i].post_name + '</label>'
                                supervisorBeanListhtml += '<div class="layui-input-inline">'
                                supervisorBeanListhtml += '<input type="number" name="supervisorBeanList[' + a + '].supervisorPrice" autocomplete="off" placeholder="请输入" class="layui-input"><input value="'+addExecutiveDirector[i].post_Id+'" name="supervisorBeanList[' + a + '].postBean.postId" hidden="hidden">'
                                supervisorBeanListhtml += '</div>'
                                supervisorBeanListhtml += '</div>'
                                supervisorBeanListhtml += '</div>'
                          }
                           $(".layui-executiveDirector").append(supervisorBeanListhtml);
                          // 队长
                          for (var j = 0; j < addCaptain.length; j++) {
                             var id = addCaptain[j].post_Id;
                             for (var i = 0; i < captainBeanList.length; i++) {
                                if(captainBeanList[i].postBean.postId == id){
                                   addCaptain.splice(j--,1);
                                   break;
                                }
                            }
                          }
                          var captainBeanListLength = captainBeanList.length;
                          var captainBeanListhtml = "";
                          for (var i = 0; i < addCaptain.length; i++) {
                              var a = i + captainBeanListLength;
                              captainBeanListhtml +='<div class="layui-col-sm6 layui-col-xs12  layui-col-md4">'
                              captainBeanListhtml +='<div class="layui-form-item">'
                              captainBeanListhtml += '<label class="layui-form-label"><span class="pms-required">*</span>' + addCaptain[i].post_name + '</label>'
                              captainBeanListhtml += '<div class="layui-input-inline layui-duration">'
                              captainBeanListhtml += '<input type="number" name="captainBeanList[' + a + '].captainPrice" autocomplete="off" placeholder="请输入" class="layui-input captain"><input value="'+addCaptain[i].post_Id+'" name="captainBeanList[' + a + '].postBean.postId" hidden="hidden">'
                              captainBeanListhtml += '</div>'
                              captainBeanListhtml += '</div>'
                              captainBeanListhtml += '</div>'
                          }
                          $(".layui-captain").append(captainBeanListhtml);
                          $(".layui-executiveDirector").append(supervisorBeanListhtml);
                          $(".professionId").val(professionId);
                          $(".layui-department-hidden").val(data.departmentBean.departmentId);
                          $(".layui-department").val(department);
                          metering(professionUnitId);
                          loadData(data);
                       }, 500);
                   }
               });


               var form = layui.form;
                form.on('submit(save)', function(data) {
                    Submission(data);
                    return false;
                });

                 //  监听select下拉
                form.on('select(metering)', function(data){
                     var value = data.value;
                     $(".layui-metering").val(value);
                });

                 //实时监听队长输入框值变化
                 $('.layui-duration :input').bind('input propertychange', function(){
                      //获取.input-form下的所有 <input> 元素,并实时监听用户输入
                      var captain = $(".captain").val();
                      if( captain > 1){
                          layer.msg('输入不能大于1',{time : 1500, icon : 2});
                          $(".captain").val("");
                          return false;
                      }
                  })

                function Submission(data) {
                     $.ajax({
                        url : '/profession',
                        type : 'PUT',
                        dataType:"JSON",
                        data : data.field,
                        beforeSend:function() { //触发ajax请求开始时执行
                            $(".submit button").attr("disabled","true"); //改变提交按钮上的文字并将按钮设置为不可点击
                        },
                        success : function(res){
                            if(res){
                                parent.layer.closeAll();
                                parent.layer.msg('修改成功',{time : 1500, icon : 1});
                                location.reload();
                            }else{
                                parent.layer.msg('修改失败',{time : 1500, icon : 2});
                                $(".submit button").removeAttr("disabled"); //改变提交按钮上的文字并将按钮设置为可点击
                            }
                        }
                    });
                }

               // 计量单位
               function metering(professionUnitId){
                     $.ajax({
                        url: '/profession_unit',
                        type: "get",
                        dataType: "json",
                        success: function (obj) {
                            var htmlBook="";
                            for (var i=0;i<obj.length;i++){
                                htmlBook+='<option value='+obj[i].professionUnitId+'>'+obj[i].professionUnitName+'</option>'
                            }
                             $(".Company").append(htmlBook); //计量单位
                             if(professionUnitId){
                                 $('select[name="professionUnitBean.professionUnitId"]').val(professionUnitId); // 获取下拉的name值，用val渲染
                              }
                             layui.form.render('select'); //刷新select标签
                       }
                     });
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
    }else{ // 新增
            if(departmentId == "1"){
               var str = "测量部"
               department(str);
            }else if(departmentId == "2"){
               var str = "勘察部"
               department(str);
            }else if(departmentId == "3"){
               var str = "咨询部"
               department(str);
            }else if(departmentId == "4"){
               var str = "实验室"
               department(str);
            }

             // 所属部门
             function department(str){
                 $.ajax({
                     url: '/department/department_name',
                     type: "get",
                     data : {
                         departmentName :str
                     },
                     dataType: "json",
                     success: function (obj) {
                         var departmentId = obj[0].departmentId;
                         staff(departmentId);
                         executiveDirector(departmentId);
                         captain(departmentId);
                         $(".layui-department-hidden").val(obj[0].departmentId);
                         $(".layui-department").val(obj[0].departmentName)
                     }
                 });
             }

              // 员工
              function staff(departmentId){
                 $.ajax({
                     url: '/post',
                     type: "get",
                     data : {
                         postLevel :'员工',
                         'departmentBean.departmentId':departmentId
                     },
                     dataType: "json",
                     success: function (obj) {
                         addStaff = obj;
                         var html = "";
                         for (var i = 0; i < obj.length; i++) {
                             html +='<div class="layui-col-sm6 layui-col-xs12  layui-col-md4">'
                             html +='<div class="layui-form-item">'
                             html += '<label class="layui-form-label"><span class="pms-required">*</span>' + obj[i].post_name + '</label>'
                             html += '<div class="layui-input-inline">'
                             html += '<input type="number" name="staffBeanList[' + i + '].staffPrice" autocomplete="off" placeholder="请输入" class="layui-input"><input value="'+obj[i].post_Id+'" name="staffBeanList[' + i + '].postBean.postId" hidden="hidden">'
                             html += '</div>'
                             html += '</div>'
                             html += '</div>'
                         }
                         $(".layui-staff").append(html);
                     }
                 });
              }

              // 主管
              function executiveDirector(departmentId){
                  $.ajax({
                       url: '/post',
                       type: "get",
                       data : {
                           postLevel :'主管',
                           'departmentBean.departmentId':departmentId
                       },
                       dataType: "json",
                       success: function (obj) {
                           var html = "";
                           for (var i = 0; i < obj.length; i++) {
                               html +='<div class="layui-col-sm6 layui-col-xs12  layui-col-md4">'
                               html +='<div class="layui-form-item">'
                               html += '<label class="layui-form-label"><span class="pms-required">*</span>' + obj[i].post_name + '</label>'
                               html += '<div class="layui-input-inline">'
                               html += '<input type="number" name="supervisorBeanList[' + i + '].supervisorPrice" autocomplete="off" placeholder="请输入" class="layui-input"><input value="'+obj[i].post_Id+'" name="supervisorBeanList[' + i + '].postBean.postId" hidden="hidden">'
                               html += '</div>'
                               html += '</div>'
                               html += '</div>'
                           }
                           $(".layui-executiveDirector").append(html);
                       }
                  });
              }

              // 队长
             function captain(departmentId){
                 $.ajax({
                     url: '/post',
                     type: "get",
                     data : {
                         postLevel :'队长',
                         'departmentBean.departmentId':departmentId
                     },
                     dataType: "json",
                     success: function (obj) {
                         var html = "";
                         for (var i = 0; i < obj.length; i++) {
                             html +='<div class="layui-col-sm6 layui-col-xs12  layui-col-md4">'
                             html +='<div class="layui-form-item">'
                             html += '<label class="layui-form-label"><span class="pms-required">*</span>' + obj[i].post_name + '</label>'
                             html += '<div class="layui-input-inline layui-duration">'
                             html += '<input type="number" name="captainBeanList[' + i + '].captainPrice" autocomplete="off" placeholder="请输入" class="layui-input captain"><input value="'+obj[i].post_Id+'" name="captainBeanList[' + i + '].postBean.postId" hidden="hidden">'
                             html += '</div>'
                             html += '</div>'
                             html += '</div>'
                         }
                         $(".layui-captain").append(html);
                     }
                 });
             }

             layui.use(['form','table'], function(){
                 var table = layui.table;
                 // 监听提交按钮
                 var form = layui.form;
                 form.on('submit(save)', function(data) {
                     Submission(data);
                     return false;
                 });

                  //  监听select下拉
                 form.on('select(metering)', function(data){
                      var value = data.value;
                      $(".layui-metering").val(value);
                 });

                 function Submission(data) {
                      $.ajax({
                         url : '/profession',
                         type : 'POST',
                         dataType:"JSON",
                         data : data.field,
                         beforeSend:function() { //触发ajax请求开始时执行
                             $(".submit button").attr("disabled","true"); //改变提交按钮上的文字并将按钮设置为不可点击
                         },
                         success : function(res){
                             if(res.code==200){
                                 parent.layer.closeAll();
                                 parent.layer.msg(res.message,{time : 1500, icon : 1});
                             }else{
                                 parent.layer.msg(res.message,{time : 1500, icon : 2});
                                 $(".submit button").removeAttr("disabled"); //改变提交按钮上的文字并将按钮设置为可点击
                             }
                         }
                     });
                 }

                 //实时监听队长输入框值变化
                 $('.layui-duration :input').bind('input propertychange', function(){
                      //获取.input-form下的所有 <input> 元素,并实时监听用户输入
                      var captain = $(".captain").val();
                      if( captain > 1){
                          layer.msg('输入不能大于1',{time : 1500, icon : 2});
                          $(".captain").val("");
                          return false;
                      }
                  })

                  // 计量单位
                  $.ajax({
                     url: '/profession_unit',
                     type: "get",
                     dataType: "json",
                     success: function (obj) {
                         var htmlBook="";
                         for (var i=0;i<obj.length;i++){
                             htmlBook+='<option value='+obj[i].professionUnitId+'>'+obj[i].professionUnitName+'</option>'
                         }
                          $(".Company").append(htmlBook); //计量单位
                          layui.form.render('select'); //刷新select标签
                     }
                  });
             });
    }

});
