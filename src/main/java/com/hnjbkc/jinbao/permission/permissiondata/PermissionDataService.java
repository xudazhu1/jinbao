package com.hnjbkc.jinbao.permission.permissiondata;

import com.hnjbkc.jinbao.organizationalstructure.company.CompanyBean;
import com.hnjbkc.jinbao.organizationalstructure.department.DepartmentBean;
import com.hnjbkc.jinbao.organizationalstructure.department.DepartmentDao;
import com.hnjbkc.jinbao.organizationalstructure.job.JobBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.utils.MapUtils;
import com.hnjbkc.jinbao.utils.tableutils.TableUtilsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xudaz
 */
@SuppressWarnings("WeakerAccess")
@Service
public class PermissionDataService {


    private TableUtilsDao tableUtilsDao;

    @Autowired
    public void setTableUtilsDao(TableUtilsDao tableUtilsDao) {
        this.tableUtilsDao = tableUtilsDao;
    }

    private DepartmentDao departmentDao;

    @Autowired
    public void setTableUtilsDao( DepartmentDao departmentDao ) {
        this.departmentDao = departmentDao;
    }

    /**
     * 根据用户和所在页面获取最大的权限级别
     * @return r
     */
    @SuppressWarnings("unchecked")
    public int getMaxLevel4User(UserBean userBean , String permissionTag) {
        List<Object[]> page = tableUtilsDao.searchNoCount(PermissionDataBean.class,
                MapUtils.createMap(
                        new String[]{"table_utils.fields" , "$D.jobBean.userBeans.userId" , "$D.permissionBean.permissionTag"},
                        new Object[]{"permissionDataLevel$permissionDataId",
                                userBean.getUserId().toString() ,
                                permissionTag }
                ), PageRequest.of(0, Integer.MAX_VALUE ,
                        Sort.by(Sort.Order.desc("permissionDataBean.permissionDataLevel" ) ) ) );
        List<Object[]> page1 = tableUtilsDao.searchNoCount(PermissionDataBean.class,
                MapUtils.createMap(
                        new String[]{"table_utils.fields" , "$D.roleBean.userBeans.userId" , "$D.permissionBean.permissionTag"},
                        new Object[]{"permissionDataLevel$permissionDataId",
                                userBean.getUserId().toString() ,
                                permissionTag }
                ), PageRequest.of(0, Integer.MAX_VALUE ,
                        Sort.by(Sort.Order.desc("permissionDataBean.permissionDataLevel" ) ) ) );
        //默认级别
        int max = 1;
        if ( page.size() > 0 ) {
            max = (int) page.get(0)[0];
        }
        //默认级别
        int max1 = 1;
        if ( page1.size() > 0 ) {
            max1 = (int) page1.get(0)[0];
        }
        return Math.max(max, max1);
    }

    public List<Integer> getJobIds4PermissionLevelAndUser(int level , UserBean userBean ) {
        if ( userBean == null )  {
            return new ArrayList<>();
        }
        List<Integer> integers = new ArrayList<>();
        switch ( level ) {
            //如果是仅可见自己
            case 1 :
                break;
            //如果是仅可见本职位
            case 2 :
                integers.add(userBean.getJobBean().getJobId());
                break;
            //如果是仅可见本部门
            case 3 :
                addJobIds4Dept(userBean.getJobBean().getDepartmentBean() , integers);
                break;
            //如果是仅可见本分公司
            case 4 :
                addJobIds4Comp(getComp4User(userBean) , integers);
                break;
            default :
                 return integers;
        }
        return integers;
    }



    private void addJobIds4Dept(DepartmentBean departmentBean , List<Integer> integers ) {
        if ( departmentBean == null  ) {
            return ;
        }
//        //过滤hibernate代理对象
//        if (departmentBean.getJobBeans() instanceof PersistentBag) {
//            departmentBean.setJobBeans( departmentDao.findById( departmentBean.getDepartmentId() ).orElse( departmentBean ).getJobBeans() ); ;
//        }
        if ( departmentBean.getJobBeans() != null ) {
            for (JobBean jobBean : departmentBean.getJobBeans()) {
                integers.add( jobBean.getJobId() );
            }
        }
        if ( departmentBean.getNextDepartmentBeans() != null ) {
            for (DepartmentBean nextDepartmentBean : departmentBean.getNextDepartmentBeans()) {
                addJobIds4Dept(nextDepartmentBean , integers);
            }
        }
    }

    private void addJobIds4Comp(CompanyBean companyBean , List<Integer> integers ) {
        if ( companyBean == null  ) {
            return ;
        }
        if ( companyBean.getDepartmentBeans() != null ) {
            for (DepartmentBean departmentBean : companyBean.getDepartmentBeans()) {
                addJobIds4Dept( departmentBean , integers );
            }
        }
    }


    public CompanyBean getComp4User(UserBean userBean) {
        DepartmentBean departmentBean = userBean.getJobBean().getDepartmentBean();
        while ( departmentBean.getParentDepartmentBean() != null && departmentBean.getParentDepartmentBean().getDepartmentId() != null ) {
            departmentBean = departmentBean.getParentDepartmentBean();
        }
        return departmentBean.getCompanyBean();
    }


}
