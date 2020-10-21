package com.hnjbkc.jinbao.permission.permissiondata;

import com.hnjbkc.jinbao.organizationalstructure.company.CompanyBean;
import com.hnjbkc.jinbao.organizationalstructure.company.CompanyDao;
import com.hnjbkc.jinbao.organizationalstructure.department.DepartmentBean;
import com.hnjbkc.jinbao.organizationalstructure.department.DepartmentDao;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.permission.PermissionBean;
import com.hnjbkc.jinbao.permission.dataascriptionannotation.BelongJob;
import com.hnjbkc.jinbao.permission.dataascriptionannotation.BelongPerson;
import com.hnjbkc.jinbao.utils.MapUtils;
import com.hnjbkc.jinbao.utils.MyBeanUtils;
import com.hnjbkc.jinbao.utils.tableutils.TableUtilsDao;
import org.hibernate.collection.internal.PersistentBag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据权限处理类 会在过滤器里调用
 * @see com.hnjbkc.jinbao.config.ParamsFilter
 * @author xudaz
 */
@Component
public class DataPermissionFilter {

    @Autowired
    public void setTableUtilsDao(TableUtilsDao tableUtilsDao) {
        this.tableUtilsDao = tableUtilsDao;
    }

    private List<String> whiteList = new ArrayList<>();

    {
        whiteList.add("/vendor");
        whiteList.add("/pages");
        whiteList.add("/js");
        whiteList.add("/image");
        whiteList.add("/fonts");
        whiteList.add("/font");
        whiteList.add("/css");
    }

    private TableUtilsDao tableUtilsDao;

    private PermissionDataService permissionDataService;

    @Autowired
    public void setPermissionDataService(PermissionDataService permissionDataService) {
        this.permissionDataService = permissionDataService;
    }


    @SuppressWarnings({"unchecked"})
    public void doDataPermission(HttpServletRequest request , HttpServletResponse response ) {
        UserBean userBean1 = (UserBean) request.getSession().getAttribute("user");
        if ( userBean1 == null ) {
            request.getSession().removeAttribute("permissionData");
            request.getSession().removeAttribute("maxLevel4User");
            return;
        }
        Map<String, String[]> parameterMap = request.getParameterMap();
        //白名单
        String noDataPermission = "noDataPermission";
        if ( parameterMap.remove(noDataPermission) != null ) {
            return;
        }
        String url = request.getRequestURL().toString();
        for (String s : whiteList) {
            if ( url.startsWith(s) ) {
                return;
            }
        }
        //查找此页面是否需要进行数据权限过滤
        String pageTag = request.getHeader("Referer");
        String startWith = "/pages";
        if ( pageTag == null || !pageTag.contains(startWith)) {
            return;
        }
        pageTag = pageTag.substring(pageTag.indexOf("/pages") , pageTag.indexOf(".html") + 5 );

        Map<String , Boolean> permissionsData = (Map<String , Boolean>) request.getSession().getAttribute("permissionData");
        if ( permissionsData == null ) {
            permissionsData = new HashMap<>(5);
            request.getSession().setAttribute("permissionData" , permissionsData );
        }
        boolean hasPermission ;
        //包含就直接拿
        if (   permissionsData.containsKey( pageTag ) ) {
            //是否是数据权限
            hasPermission = permissionsData.get( pageTag );
        } else {
            //不包含就同步 先拿着试试 不行就发请求
            hasPermission = hasPermissionData( pageTag , request );
        }

        //是否是数据权限
        if ( ! hasPermission ) {
            return;
        }
        //需要 往请求参数里添加搜索条件 先找到当前用户所属角色 所属职位 对当前页面的最高权限级别
        Map<String , Integer> maxLevelMap = (Map<String , Integer>) request.getSession().getAttribute("maxLevel4User");
        int maxLevel4User ;
        if ( maxLevelMap == null ) {
            maxLevelMap = new HashMap<>(0);
            request.getSession().setAttribute("maxLevel4User" , maxLevelMap );
        }

        //包含就直接拿
        if (   maxLevelMap.containsKey( pageTag ) ) {
            //是否是数据权限
            maxLevel4User = maxLevelMap.get( pageTag );
        } else {
            //不包含就同步 先拿着试试 不行就发请求
            maxLevel4User = getMaxLevelForUser( pageTag , request , userBean1 );
        }

        //为搜索添加条件
        addConditionForRequest(request , userBean1 , maxLevel4User);

    }

    @SuppressWarnings("unchecked")
    private  Integer getMaxLevelForUser(String pageTag , HttpServletRequest request , UserBean userBean1 ) {
        synchronized (this) {
            //先从缓存里查有没有 没有再从数据库查
            Map<String , Integer> maxLevelMap = (Map<String , Integer>) request.getSession().getAttribute("maxLevel4User");
            assert maxLevelMap != null ;
            // 获取是否包含
            if (  maxLevelMap.get( pageTag ) != null  ) {
                return maxLevelMap.get( pageTag );
            } else {
                int maxLevel4User = permissionDataService.getMaxLevel4User(userBean1, pageTag);
                maxLevelMap.put( pageTag , maxLevel4User );
                request.getSession().setAttribute("maxLevel4User" , maxLevelMap );
                return maxLevel4User;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private  boolean hasPermissionData(String pageTag , HttpServletRequest request) {
        synchronized (this) {
            //先从缓存里查有没有 没有再从数据库查
            Map<String , Boolean> permissionsData = (Map<String , Boolean>) request.getSession().getAttribute("permissionData");
            assert permissionsData != null ;
            // 获取是否包含
            if (  permissionsData.get( pageTag ) != null && permissionsData.get( pageTag ) ) {
                return true;
            } else {
                List page = tableUtilsDao.searchNoCount(PermissionBean.class,
                        MapUtils.createMap(
                                new String[]{"table_utils.fields", "permissionDataPermission", "$D.permissionTag"},
                                new Object[]{"permissionId", "true", pageTag}), PageRequest.of(0, Integer.MAX_VALUE));
                boolean b = page.size() > 0;
                permissionsData.put( pageTag , b );
                request.getSession().setAttribute("permissionData" , permissionsData );
                return b;
            }
        }
    }

    private void  addConditionForRequest( HttpServletRequest request , UserBean userBean1 , int maxLevel4User ) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        //拿到主体bean
        String tableName = request.getParameter("table_utils.tableName") ;
        if ( tableName == null ) {
            tableName = request.getRequestURI().split("/")[0];
        }
        int all = 5;
        Class bean  = MyBeanUtils.getBean4TableName(  tableName  );
        if ( bean == null ) {
            //不是数据权限 ? 找不到
            return;
        }
        //仅自己 找到归属人字段 加进去
        if ( maxLevel4User == 1 ) {
            Field fieldByAnnotationType = MyBeanUtils.getFieldByAnnotationType(bean, BelongPerson.class);
            if ( fieldByAnnotationType != null ) {
                fieldByAnnotationType.setAccessible(true);
                parameterMap.put( "$D." +  fieldByAnnotationType.getName() + "." + "userId" , new String[]{userBean1.getUserId().toString()} );
            }
        } else  if ( maxLevel4User != all )  {
            Field fieldByAnnotationType = MyBeanUtils.getFieldByAnnotationType(bean, BelongJob.class);
            if ( fieldByAnnotationType != null ) {
                fieldByAnnotationType.setAccessible(true);

                //为用户赋值部门和公司
                if ( userBean1.getJobBean().getDepartmentBean().getJobBeans() instanceof PersistentBag ) {
                    DepartmentBean departmentBean = departmentDao.findById(userBean1.getJobBean().getDepartmentBean().getDepartmentId()).orElse(userBean1.getJobBean().getDepartmentBean());
                    userBean1.getJobBean().setDepartmentBean(departmentBean);
                }
                CompanyBean comp4User = permissionDataService.getComp4User(userBean1);

                if ( comp4User.getDepartmentBeans() instanceof PersistentBag ) {
                    comp4User.setDepartmentBeans( companyDao.findById( comp4User.getCompanyId() ).orElse( comp4User ).getDepartmentBeans() );
                }

                request.getSession().setAttribute("user" , userBean1 );

                List<Integer> jobIds4PermissionLevelAndUser = permissionDataService.getJobIds4PermissionLevelAndUser(maxLevel4User, userBean1);
                String s = jobIds4PermissionLevelAndUser.toString();
                s = s.substring(1 , s.length() - 1 ).replaceAll("," , "\\$");
                parameterMap.put( "$D." + fieldByAnnotationType.getName() + "." + "jobId" , new String[]{s} );
            }
        }
    }


    private DepartmentDao departmentDao;

    @Autowired
    public void setDepartmentDao(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }
    private CompanyDao companyDao;

    @Autowired
    public void setDepartmentDao( CompanyDao companyDao ) {
        this.companyDao = companyDao;
    }
}
