package com.hnjbkc.jinbao.workload;

import com.hnjbkc.jinbao.common.CommonResult;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.implement.ImplementBean;
import com.hnjbkc.jinbao.project.ProjectBean;
import com.hnjbkc.jinbao.utils.AttrExchange;
import com.hnjbkc.jinbao.utils.layuisoultable.Sql2SoulTableDao;
import com.hnjbkc.jinbao.utils.layuisoultable.SoulPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;

import java.util.*;

/**
 * @author 12
 * @Date 2019-08-27
 */
@Service
public class WorkLoadServiceImpl implements BaseService<WorkLoadBean> {

    private WorkLoadDao workLoadDao;

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Autowired
    public void setWorkLoadDao(WorkLoadDao workLoadDao ) {
        this.workLoadDao  = workLoadDao;
    }

    @Override
    public WorkLoadBean add(WorkLoadBean workLoadBean){
        return workLoadDao.save(workLoadBean);
    }

    @Override
    public Boolean delete(Integer id){
        workLoadDao.deleteById(id);
        return true;
    }

    @Override
    public WorkLoadBean update(WorkLoadBean workLoadBean){
        Optional<WorkLoadBean> byId = workLoadDao.findById(workLoadBean.getWorkLoadId());
        workLoadBean.setWorkLoadUpdateDate(new Date());
        if(byId.isPresent()){
            WorkLoadBean workLoadDbBean = byId.get();
            List<String> notJudgeProps = new ArrayList<>();
            notJudgeProps.add("staffUserBean");
            notJudgeProps.add("supervisorUserBean");
            notJudgeProps.add("captainUserBean");
            List<String> continueProps = new ArrayList<>();
            continueProps.add("createJobBean");
            continueProps.add("departmentBean");
            AttrExchange.onAttrExchange(workLoadDbBean,workLoadBean,notJudgeProps, continueProps);
            return workLoadDao.save(workLoadDbBean);
        }
      return null;
    }

    public Page<Map<String, Object>> getTeamFee(SoulPage soulPage) {
        String sql = "select \n" +
                " a.project_num as projectNum\n" +
                " ,a.user_name as  userName\n" +
                " ,d.department_name as departmentName\n" +
                " ,SUM(a.staffAmont) as sumStaffAmont\n" +
                " , SUM(a.supervisorAmont) as sumSupervisorAmont\n" +
                " ,SUM(a.professionAmont) as sumProfessionAmont \n" +
                " ,SUM(a.paidStaffAmont) as paidSumStaffAmont\n" +
                " , SUM(a.paidSupervisorAmont) as paidSumSupervisorAmont\n" +
                " ,SUM(a.paidProfessionAmont) as paidSumProfessionAmont \n" +
                " ,SUM(a.unpaidStaffAmont) as unpaidSumStaffAmont\n" +
                " , SUM(a.unpaidSupervisorAmont) as unpaidSumSupervisorAmont\n" +
                " ,SUM(a.unpaidProfessionAmont) as unpaidSumProfessionAmont " +
                " from (\n" +
                " select \n" +
                " user_id," +
                " user_job_id,\n" +
                " project_num, \n" +
                " user_name,\n" +
                " work_load_id,\n" +
                " work_load_implement_id,\n" +
                " CASE WHEN user_id = work_load_staff_id AND work_load_status_id != 8 THEN work_load_amount_staff ELSE 0 END staffAmont,\n" +
                " CASE WHEN user_id = work_load_supervisor_id AND work_load_status_id != 8 THEN work_load_amount_manage ELSE 0 END supervisorAmont,\n" +
                " CASE WHEN user_id = work_load_captain_id AND work_load_status_id != 8 THEN work_load_amount_captain ELSE 0 END professionAmont\n" +
                " ,\n" +
                " CASE WHEN user_id = work_load_staff_id AND work_load_status_id = 4 THEN work_load_amount_staff ELSE 0 END paidStaffAmont,\n" +
                " CASE WHEN user_id = work_load_supervisor_id AND work_load_status_id = 4 THEN work_load_amount_manage ELSE 0 END paidSupervisorAmont,\n" +
                " CASE WHEN user_id = work_load_captain_id AND work_load_status_id = 4 THEN work_load_amount_captain ELSE 0 END paidProfessionAmont\n" +
                " ,\n" +
                " CASE WHEN user_id = work_load_staff_id AND work_load_status_id = 3 THEN work_load_amount_staff ELSE 0 END unpaidStaffAmont,\n" +
                " CASE WHEN user_id = work_load_supervisor_id AND work_load_status_id = 3 THEN work_load_amount_manage ELSE 0 END unpaidSupervisorAmont,\n" +
                " CASE WHEN user_id = work_load_captain_id AND work_load_status_id = 3 THEN work_load_amount_captain ELSE 0 END unpaidProfessionAmont" +
                " FROM USER, work_load,implement , project \n" +
                "  WHERE (user_id = work_load_supervisor_id OR user_id = work_load_staff_id OR user_id = work_load_captain_id) \n" +
                "  AND work_load_implement_id = implement_id AND implement_project_id = project_id\n" +
                " ) as a " +
                " LEFT JOIN job j ON a.user_job_id = j.job_id " +
                " LEFT JOIN department d ON d.department_id = j.job_department_id \n" +
                " GROUP BY user_id,project_num ";
        return hql2SoulTableDao.invokeSqlSoulTable(sql,soulPage);
    }

    public Page search(Map<String, Object> propMap, Pageable pageRequest) {
        Page<WorkLoadBean> workLoadBeans = sqlUtilsDao.searchAllByCustomProps(WorkLoadBean.class, propMap, pageRequest);
        return workLoadBeans;
    }

    List getSingleProperty(String property) {
        return sqlUtilsDao.getSingleProperties(WorkLoadBean.class, property);
    }


    public WorkLoadBean findWorkLoadBeanById(Integer id) {
        Optional<WorkLoadBean> byId = workLoadDao.findById(id);
        return byId.orElse(null);
    }



    public CommonResult getPersonnelCommission(Map<String, Object> propMap, Pageable pageRequest) {
        Page<WorkLoadBean> workLoadBeans = sqlUtilsDao.searchAllByCustomProps(WorkLoadBean.class, propMap, pageRequest);
        List<WorkLoadBean> content = workLoadBeans.getContent();
        List<Integer> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        for (WorkLoadBean workLoadBean : content) {
            list.add(workLoadBean.getWorkLoadId());
        }
        //工作量 分页id 去查询 对应的人员成本    人员成本(工资日金额 * 工作量天数)
        List<List> personnelCommission = workLoadDao.getPersonnelCommission(list);
        for (List commission : personnelCommission) {
            for (WorkLoadBean workLoadBean : content) {
                if(workLoadBean.getWorkLoadId().equals(commission.get(0))){
                    Object wage = commission.get(1);
                    workLoadBean.setWorkLoadLaborCost((Double) wage);
                }
            }
        }
        List<Map<String,Object>> objects = new ArrayList<>();
        for (WorkLoadBean workLoadBean : content) {
            Map<String, Object> objectObjectHashMap = new HashMap<>();
            ImplementBean implementBean = workLoadBean.getImplementBean();
            ProjectBean projectBean = implementBean.getProjectBean();
            objectObjectHashMap.put("projectNum",projectBean.getProjectNum());
            objectObjectHashMap.put("projectName",projectBean.getProjectName());
            objectObjectHashMap.put("projectManagementType",projectBean.getProjectManagementType());
            objectObjectHashMap.put("implementName",implementBean.getDepartmentBean().getDepartmentName());
            objectObjectHashMap.put("date",workLoadBean.getWorkLoadDate());
            objectObjectHashMap.put("day",workLoadBean.getWorkLoadDuration());
            objectObjectHashMap.put("staff",workLoadBean.getStaffUserBean().getUserName());
            objectObjectHashMap.put("cost",workLoadBean.getWorkLoadLaborCost());
            objects.add(objectObjectHashMap);
        }
        map.put("list",objects);
        map.put("count",workLoadBeans.getTotalElements());
        return CommonResult.success(map);
    }

    public List getCommissionByProject(Integer id) {
        String hql = "select new Map(" +
                "w.workLoadId as workLoadId, " +
                "w.implementBean.departmentBean.departmentName as departmentName," +
                "w.implementBean.implementId as implementId," +
                "w.implementBean.projectBean.projectId as projectId,"+
                "w.professionBean.professionName as professionName," +
                "w.workLoadWorkLoad as workLoadWorkLoad," +
                "w.staffUserBean.userName as staffName," +
                "w.staffUserBean.userId as staffUserId," +
                "w.workLoadAmountStaff as workLoadAmountStaff," +
                "w.supervisorUserBean.userName as supervisorName," +
                "w.supervisorUserBean.userId as supervisorUserId," +
                "w.workLoadAmountManage as workLoadAmountManage," +
                "w.captainUserBean.userName as captainName," +
                "w.captainUserBean.userId as captainUserId," +
                "w.workLoadAmountCaptain as workLoadAmountCaptain," +
                "w.workLoadDate as workLoadDate," +
                "w.workLoadDuration as workLoadDuration," +
                "w.workLoadPriceStaff as workLoadPriceStaff" +
                ") " +
                "from WorkLoadBean w " +
                "left join w.implementBean  " +
                "left join w.professionBean  " +
                "left join w.staffUserBean  " +
                "left join w.supervisorUserBean  " +
                "left join w.captainUserBean  " +
                "left join w.implementBean.projectBean  " +
                "left join w.implementBean.departmentBean " +
                "left join w.workLoadStatusBean " +
                "where  " +
                " w.implementBean.projectBean.projectId = " + id ;
        List<Map<String, Object>> list = sqlUtilsDao.exHqlResultMap(WorkLoadBean.class, hql);
        List<Integer> objects = new ArrayList<>();
        for (Map<String, Object> map : list) {
            objects.add((Integer)map.get("workLoadId"));
        }
        if (objects.size() == 0) {
            return list;
        }
        List<List> personnelCommission = workLoadDao.getPersonnelCommission(objects);
        for (List commission : personnelCommission) {
            for (Map<String, Object> map : list) {
                if(map.get("workLoadId").equals(commission.get(0))){
                    Object wage = commission.get(1);
                    map.put("cost",wage);
                }
            }
        }
        return list;
    }

    private Sql2SoulTableDao hql2SoulTableDao;

    @Autowired
    public void setHql2SoulTableDao(Sql2SoulTableDao hql2SoulTableDao) {
        this.hql2SoulTableDao = hql2SoulTableDao;
    }

    public Object getCommissionList(SoulPage soulPage) {
        String hql = "select " +
                "wl.workLoadId as workLoadId," +
                "wl.workLoadDate as workLoadDate," +
                "wl.professionBean.professionName as professionName," +
                "i.departmentBean.departmentName as departmentName," +
                "pb.projectNum as projectNum," +
                "pb.projectName as projectName," +
                "wl.workLoadWorkLoad as workLoadWorkLoad," +
                "wl.staffUserBean.userName as staffUserName," +
                "wl.workLoadPriceStaff as workLoadPriceStaff," +
                "wl.workLoadAmountStaff as workLoadAmountStaff," +
                "wl.supervisorUserBean.userName as supervisorUserName," +
                "wl.workLoadAmountManage as workLoadAmountManage, " +
                "wl.captainUserBean.userName as captainUserName," +
                "wl.workLoadAmountCaptain as workLoadAmountCaptain," +
                "(wl.workLoadAmountStaff + wl.workLoadAmountManage  + wl.workLoadAmountCaptain) as commission ," +
                " 0.3 as annualPercentage, " +
                " 0.3 * (wl.workLoadAmountStaff + wl.workLoadAmountManage  + wl.workLoadAmountCaptain) as bonus " +
                "from WorkLoadBean wl " +
                "left join ProfessionBean p on p.professionId =  wl.professionBean.professionId " +
                "left join ImplementBean i on i.implementId =  wl.implementBean.implementId " +
                "left join ProjectBean pb on pb.projectId = i.projectBean.projectId " +
                "left join DepartmentBean d on d.departmentId = i.departmentBean.departmentId " +
                "left join UserBean staffU on staffU.userId =  wl.staffUserBean.userId " +
                "left join UserBean supervisorU on supervisorU.userId =  wl.supervisorUserBean.userId " +
                "left join UserBean captainU on captainU.userId =  wl.captainUserBean.userId ";
        return hql2SoulTableDao.invokeHqlSoulTable(hql,soulPage);
    }
}
