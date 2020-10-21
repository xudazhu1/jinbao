package com.hnjbkc.jinbao.squadgroupfee;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.utils.MyBeanUtils;
import com.hnjbkc.jinbao.utils.layuisoultable.Sql2SoulTableDao;
import com.hnjbkc.jinbao.utils.layuisoultable.SoulPage;
import com.hnjbkc.jinbao.utils.layuisoultable.SoulTableDao;
import com.hnjbkc.jinbao.utils.tableutils.TableUtilsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.*;

/**
 * @author siliqiang
 * @date 2019/11/26
 */
@Service
public class SquadGroupFeeServiceImpl implements BaseService<SquadGroupFeeBean> {

    private TableUtilsDao tableUtilsDao;

    @Autowired
    public void setTableUtilsDao(TableUtilsDao tableUtilsDao) {
        this.tableUtilsDao = tableUtilsDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    private SquadGroupFeeDao squadGroupFeeDao;

    @Autowired
    public void setSquadGroupFeeDao(SquadGroupFeeDao squadGroupFeeDao) {
        this.squadGroupFeeDao = squadGroupFeeDao;
    }

    /**
     * 注入实体管理器,执行持久化操作
     */
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private SoulTableDao soulTableDao;

    @Autowired
    public void setSoulTableDao(SoulTableDao soulTableDao) {
        this.soulTableDao = soulTableDao;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public SquadGroupFeeBean add(SquadGroupFeeBean squadGroupFeeBean) {
        SquadGroupFeeBean updateSquadGroupFeeBean = tableUtilsDao.update(squadGroupFeeBean);
        if (updateSquadGroupFeeBean != null) {
            if (updateSquadGroupFeeBean.getSquadGroupFeeNum() == null ||"".equals( updateSquadGroupFeeBean.getSquadGroupFeeNum())  ) {
                MyBeanUtils.createNumber(updateSquadGroupFeeBean, "BZ", "squadGroupFeeNum", null);
            }

        }
        return updateSquadGroupFeeBean;
    }

    @Override
    public Boolean delete(Integer id) {
        return null;
    }

    @Override
    public SquadGroupFeeBean update(SquadGroupFeeBean squadGroupFeeBean) {
        return null;
    }

    public Map<String, Object> getSquadGroupFee(@RequestParam Map<String, Object> propMap) {
        StringBuilder sql = new StringBuilder("SELECT\n" +
                "s.squad_group_fee_num,\n" +
                "s.squad_group_fee_name,\n" +
                "s.squad_group_fee_company,\n" +
                "s.squad_group_fee_genre,\n" +
                "s.squad_group_fee_content,\n" +
                "st.squad_group_fee_status_id,\n" +
                "s.squad_group_fee_time,\n" +
                "u.user_name, \n" +
                "s.squad_group_fee_id\n" +
                "FROM squad_group_fee s\n" +
                "LEFT JOIN `user` u on u.user_id =s.squad_group_fee_user_id\n" +
                "LEFT JOIN squad_group_fee_status st ON st.squad_group_fee_status_id=s.squad_group_fee_status_id");

        String pageNum = "0";
        String pageSize = "10";
        if (propMap.get("pageNum") != null && propMap.get("pageSize") != null) {
            pageNum = "1".equals(propMap.get("pageNum")) ? "0" : (Integer.parseInt((String) propMap.get("pageNum")) - 1) * (Integer.parseInt((String) propMap.get("pageSize"))) + "";
        }
        pageSize = (String) propMap.get("pageSize");
        sql.append(" where 1=1 ");
        if (propMap.get("squadGroupFeeContent") != null && propMap.get("squadGroupFeeContent") != "") {
            sql.append(" and s.squad_group_fee_genre=" + "'" + propMap.get("squadGroupFeeContent") + "'");
        }
        if (propMap.get("squadGroupFeeStatusId") != null && propMap.get("squadGroupFeeStatusId") != "") {
            sql.append(" and s.squad_group_fee_status_id=" + "'" + propMap.get("squadGroupFeeStatusId") + "'");
        }
        if (propMap.get("squadGroupFeeCompany") != null && propMap.get("squadGroupFeeCompany") != "") {
            sql.append(" and s.squad_group_fee_company" + " like " + "'%" + propMap.get("squadGroupFeeCompany") + "%'");
        }
        if (propMap.get("squadGroupFeeName") != null && propMap.get("squadGroupFeeName") != "") {
            sql.append(" and s.squad_group_fee_name" + " like " + "'%" + propMap.get("squadGroupFeeName") + "%'");
        }

        if (pageNum != null && pageSize != null) {
            sql.append("limit ");
            sql.append(pageNum);
            sql.append(",");
            sql.append(pageSize);
        }

        Query nativeQuery = entityManager.createNativeQuery(sql.toString());
        List<Object[]> resultList = nativeQuery.getResultList();
        ArrayList<Object> list = new ArrayList<>();
        for (Object[] o : resultList) {
            HashMap<String, Object> objectObjectHashMap1 = new HashMap<>();

            objectObjectHashMap1.put("squadGroupFeeNum", o[0]);
            objectObjectHashMap1.put("squadGroupFeeName", o[1]);
            objectObjectHashMap1.put("squadGroupFeeCompany", o[2]);
            objectObjectHashMap1.put("squadGroupFeeGenre", o[3]);
            objectObjectHashMap1.put("squadGroupFeeContent", o[4]);
            objectObjectHashMap1.put("squadGroupFeeStatusBean", o[5]);
            objectObjectHashMap1.put("squadGroupFeeTime", o[6]);
            objectObjectHashMap1.put("userBean.userName", o[7]);
            objectObjectHashMap1.put("squadGroupFeeId", o[8]);

            list.add(objectObjectHashMap1);
        }

        List<SquadGroupFeeBean> all = squadGroupFeeDao.findAll();
        Long count = squadGroupFeeDao.getCount();

        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("code", 200);
        objectObjectHashMap.put("msg", "succeed");
        objectObjectHashMap.put("count", count);
        objectObjectHashMap.put("data", list);
        return objectObjectHashMap;
    }

    private Sql2SoulTableDao hql2SoulTableDao;

    @Autowired
    public void setHql2SoulTableDao(Sql2SoulTableDao hql2SoulTableDao) {
        this.hql2SoulTableDao = hql2SoulTableDao;
    }

    public Page<Map<String, Object>> getTeamFee(SoulPage soulPage) {
        String sql = "select  \n" +
                "production.squad_group_fee_id2 as squadGroupFeeId,production.squad_group_fee_name2 as squadGroupFeeName,production.productionCostsMoney as productionCostsMoney ,payment.disbursePaymentAmount as disbursePaymentAmount from \n" +
                "(select \n" +
                "sum(case d.disburse_payment_status_id when 7 then d.disburse_payment_amount else 0 end)  as disbursePaymentAmount,\n" +
                "s.squad_group_fee_name as squad_group_fee_name1,\n" +
                "s.squad_group_fee_id as squad_group_fee_id1\n" +
                "from squad_group_fee s\n" +
                "left join disburse d on d.disburse_squad_group_fee_id  = s.squad_group_fee_id\n" +
                "left join disburse_detail dd on dd.disburse_detail_id = d.disburse_disburse_detail_id\n" +
                "left join disburse_type dt on dt.disburse_type_id = dd.disburse_detail_type_id\n" +
                "where   dt.disburse_type_name = '班组费' or ( d.disburse_id is null  )  \n" +
                "GROUP BY s.squad_group_fee_id) as payment,\n" +
                "(select s.squad_group_fee_id as squad_group_fee_id2 ,s.squad_group_fee_name as squad_group_fee_name2,sum(p.production_costs_money) as productionCostsMoney\n" +
                "from squad_group_fee s\n" +
                "left outer join production_costs p on p.production_costs_project_boss_id = s.squad_group_fee_id\n" +
                "left outer join production_costs_detail pc on pc.production_costs_detail_id = p.production_costs_detail_id\n" +
                "where pc.production_costs_detail_type = '班组费' or p.production_costs_id is null\n" +
                "GROUP BY s.squad_group_fee_id ) as production\n" +
                "where production.squad_group_fee_id2 = payment.squad_group_fee_id1\n";
        return hql2SoulTableDao.invokeSqlSoulTable(sql,soulPage);
    }


    public Page<Map<String, Object>> getTeamFeeDetail(SoulPage soulPage) {

        String sql = " select  \n" +
                "                 a.implement_id as implementId\n" +
                "                ,a.squad_group_fee_id as squadGroupFeeId\n" +
                "                ,a.squad_group_fee_name as squadGroupFeeName\n" +
                "                ,a.disbursePaymentAmount as disbursePaymentAmount\n" +
                "                ,a.department_name as departmentName\n" +
                "                ,a.project_num as projectNum\n" +
                "                ,a.project_name as projectName\n" +
                "                ,b.production_costs_money as productionCostsMoney\n" +
                "                from (select \n" +
                "                 i.implement_id\n" +
                "                ,s.squad_group_fee_id\n" +
                "                ,squad_group_fee_name \n" +
                "                , sum(case  when d.disburse_payment_status_id = 7 or d.disburse_payment_status_id = 8 or d.disburse_payment_status_id = 5 then d.disburse_payment_amount  else 0 end)  as disbursePaymentAmount\n" +
                "                ,de.department_name\n" +
                "                ,p.project_num\n" +
                "                ,p.project_name\n" +
                "                from squad_group_fee s\n" +
                "                left join disburse d on d.disburse_squad_group_fee_id = s.squad_group_fee_id\n" +
                "                left join disburse_detail dd on dd.disburse_detail_id = d.disburse_disburse_detail_id \n" +
                "                left join disburse_type dt on dt.disburse_type_id = dd.disburse_detail_type_id\n" +
                "                left join implement i on i.implement_id = d.disburse_implement_id\n" +
                "                left join department de on de.department_id = i.implement_department_id\n" +
                "                left join project p on p.project_id = i.implement_project_id\n" +
                "                where dt.disburse_type_name = '班组费'\n" +
                "                GROUP BY s.squad_group_fee_id, i.implement_id\n" +
                "                ) a \n" +
                "                LEFT JOIN\n" +
                "                (\n" +
                "                select \n" +
                "                 i.implement_id\n" +
                "                ,s.squad_group_fee_id\n" +
                "                ,squad_group_fee_name \n" +
                "                ,sum(pc.production_costs_money) as production_costs_money\n" +
                "                ,de.department_name\n" +
                "                ,p.project_num\n" +
                "                ,p.project_name\n" +
                "                from squad_group_fee s\n" +
                "                left join production_costs pc on pc.production_costs_project_boss_id = s.squad_group_fee_id\n" +
                "                left join implement i on i.implement_id = pc.production_costs_implement_id\n" +
                "                left join department de on de.department_id = i.implement_department_id\n" +
                "                left join project p on p.project_id = i.implement_project_id\n" +
                "                GROUP BY i.implement_id, s.squad_group_fee_id\n" +
                "                ORDER BY i.implement_id\n" +
                "                )  b on a.squad_group_fee_id = b.squad_group_fee_id and a.implement_id= b.implement_id \n" +
                "                \n" +
                "                union\n" +
                "                \n" +
                "                 select  b.implement_id\n" +
                "                ,b.squad_group_fee_id\n" +
                "                ,b.squad_group_fee_name \n" +
                "                ,a.disbursePaymentAmount\n" +
                "                ,b.department_name\n" +
                "                ,b.project_num\n" +
                "                ,b.project_name \n" +
                "                ,b.production_costs_money  from (select \n" +
                "                 i.implement_id\n" +
                "                ,s.squad_group_fee_id\n" +
                "                ,squad_group_fee_name \n" +
                "                , sum(case  when d.disburse_payment_status_id = 7 or d.disburse_payment_status_id = 8 or d.disburse_payment_status_id = 5 then d.disburse_payment_amount  else 0 end)  as disbursePaymentAmount\n" +
                "                ,de.department_name\n" +
                "                ,p.project_num\n" +
                "                ,p.project_name\n" +
                "                from squad_group_fee s\n" +
                "                left join disburse d on d.disburse_squad_group_fee_id = s.squad_group_fee_id\n" +
                "                left join disburse_detail dd on dd.disburse_detail_id = d.disburse_disburse_detail_id \n" +
                "                left join disburse_type dt on dt.disburse_type_id = dd.disburse_detail_type_id\n" +
                "                left join implement i on i.implement_id = d.disburse_implement_id\n" +
                "                left join department de on de.department_id = i.implement_department_id\n" +
                "                left join project p on p.project_id = i.implement_project_id\n" +
                "                where dt.disburse_type_name = '班组费'\n" +
                "                GROUP BY s.squad_group_fee_id, i.implement_id\n" +
                "                ) a \n" +
                "                RIGHT  JOIN\n" +
                "                (\n" +
                "                select \n" +
                "                 i.implement_id\n" +
                "                ,s.squad_group_fee_id\n" +
                "                ,squad_group_fee_name \n" +
                "                ,sum(pc.production_costs_money) as production_costs_money\n" +
                "                ,de.department_name\n" +
                "                ,p.project_num\n" +
                "                ,p.project_name\n" +
                "                from squad_group_fee s\n" +
                "                left join production_costs pc on pc.production_costs_project_boss_id = s.squad_group_fee_id\n" +
                "                left join implement i on i.implement_id = pc.production_costs_implement_id\n" +
                "                left join department de on de.department_id = i.implement_department_id\n" +
                "                left join project p on p.project_id = i.implement_project_id\n" +
                "                GROUP BY i.implement_id, s.squad_group_fee_id\n" +
                "                ORDER BY i.implement_id\n" +
                "                )  b on a.squad_group_fee_id = b.squad_group_fee_id and a.implement_id= b.implement_id ";
        return hql2SoulTableDao.invokeSqlSoulTable(sql,soulPage);
    }

    public List getTeamFeeCost(Integer squadGroupFeeId, Integer implementId) {
        String hql = "select " +
                "new Map (" +
                "s.squadGroupFeeId as squadGroupFeeId," +
                "s.squadGroupFeeNum as squadGroupFeeNum," +
                "s.squadGroupFeeName as squadGroupFeeName," +
                "i.implementId as implementId," +
                "pc.productionCostsDetailName as productionCostsDetailName," +
                "p.productionCostsMoney as productionCostsMoney," +
                "p.productionCostsRemark as productionCostsRemark " +
                ")" +
                "from ProductionCostsBean p " +
                "left join SquadGroupFeeBean s on s.squadGroupFeeId = p.squadGroupFeeBean.squadGroupFeeId " +
                "left join ImplementBean i on i.implementId = p.implementBean.implementId " +
                "left join DepartmentBean db on db.departmentId = i.departmentBean.departmentId " +
                "left join ProductionCostsDetailBean pc on pc.productionCostsDetailId = p.productionCostsDetailBean.productionCostsDetailId " +
                "where pc.productionCostsDetailType = '班组费' " +
                " and s.squadGroupFeeId = " + squadGroupFeeId +
                " and i.implementId = " + implementId;
        return hql2SoulTableDao.invokeHql2Map(hql);
    }

    public Object getTeamFeePay(Integer squadGroupFeeId, Integer implementId) {
        String hql = "select " +
                "new Map (" +
                "s.squadGroupFeeId as squadGroupFeeId," +
                "s.squadGroupFeeNum as squadGroupFeeNum," +
                "s.squadGroupFeeName as squadGroupFeeName," +
                "i.implementId as implementId," +
                "d.disburseNum as disburseNum," +
                "d.disburseTime as disburseTime," +
                "dd.disburseDetailName as disburseDetailName," +
                "d.disbursePaymentAmount as disbursePaymentAmount," +
                "d.disburseMode as disburseMode," +
                "d.disburseAffiliation as disburseAffiliation " +
                ")" +
                "from DisburseBean d " +
                "left join SquadGroupFeeBean s on s.squadGroupFeeId = d.squadGroupFeeBean.squadGroupFeeId " +
                "left join ImplementBean i on i.implementId = d.implementBean.implementId " +
                "left join DepartmentBean db on db.departmentId = i.departmentBean.departmentId " +
                "left join DisburseDetailBean dd on dd.disburseDetailId = d.disburseDetailBean.disburseDetailId " +
                "left join PaymentStatusBean ps on ps.paymentStatusId = d.paymentStatusBean.paymentStatusId " +
                "left join DisburseTypeBean dt on dt.disburseTypeId = dd.disburseTypeBean.disburseTypeId " +
                "where dt.disburseTypeName = '班组费' " +
                " and ps.paymentStatusId = 7" +
                " and s.squadGroupFeeId = " + squadGroupFeeId +
                " and i.implementId = " + implementId;
        return hql2SoulTableDao.invokeHql2Map(hql);
    }
}
