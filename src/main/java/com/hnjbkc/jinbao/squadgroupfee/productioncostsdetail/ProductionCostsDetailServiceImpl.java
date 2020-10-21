package com.hnjbkc.jinbao.squadgroupfee.productioncostsdetail;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.utils.MyBeanUtils;
import com.hnjbkc.jinbao.utils.restresponse.PagingData;
import com.hnjbkc.jinbao.utils.restresponse.RestResponse;
import com.hnjbkc.jinbao.utils.tableutils.TableUtilsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * @author siliqiang
 * @date 2019/12/30
 */
@Service
public class ProductionCostsDetailServiceImpl implements BaseService<ProductionCostsDetailBean> {

    private TableUtilsDao tableUtilsDao;

    @Autowired
    public void setTableUtilsDao(TableUtilsDao tableUtilsDao) {
        this.tableUtilsDao = tableUtilsDao;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductionCostsDetailBean add(ProductionCostsDetailBean productionCostsDetailBean) {
        ProductionCostsDetailBean updateProductionCostsDetailBean = tableUtilsDao.update(productionCostsDetailBean);
        if (updateProductionCostsDetailBean != null) {
            if (updateProductionCostsDetailBean.getProductionCostsDetailNum() == null || "".equals(updateProductionCostsDetailBean.getProductionCostsDetailNum())) {
                MyBeanUtils.createNumber(updateProductionCostsDetailBean, "FY", "productionCostsDetailNum", null);
            }
        }
        return updateProductionCostsDetailBean;
    }

    @Override
    public Boolean delete(Integer id) {
        return null;
    }

    @Override
    public ProductionCostsDetailBean update(ProductionCostsDetailBean productionCostsDetailBean) {
        return null;
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

    public RestResponse<PagingData> getAll(@RequestParam Map<String, Object> propMap) {
        StringBuilder sql = new StringBuilder("SELECT \n" +
                " new map(a.productionCostsDetailId as productionCostsDetailId," +
                "a.productionCostsDetailName as productionCostsDetailName," +
                "a.productionCostsDetailNum as productionCostsDetailNum," +
                "a.productionCostsDetailType as productionCostsDetailType," +
                "a.productionCostsDetailRemark as productionCostsDetailRemark ," +
                "a.squadGroupFeeStatusBean.squadGroupFeeStatusName as squadGroupFeeStatusName," +
                "a.productionCostsDetailTime as productionCostsDetailTime," +
                "a.productionCostsEquipmentPrice as productionCostsEquipmentPrice," +
                "a.userBean.userName as userName)  " +
                "FROM ProductionCostsDetailBean as a " +
                "LEFT  JOIN  a.userBean " +
                "LEFT JOIN a.squadGroupFeeStatusBean ");
        sql.append(" where 1=1 ");
        if (propMap.get("productionCostsDetailType") != null && !"".equals(propMap.get("productionCostsDetailType"))) {
            sql.append(" and a.productionCostsDetailType=" + "'" + propMap.get("productionCostsDetailType") + "'");
        }
        if (propMap.get("squadGroupFeeStatusName") != null && !"".equals(propMap.get("squadGroupFeeStatusName"))) {
            sql.append(" and a.squadGroupFeeStatusBean.squadGroupFeeStatusName=" + "'" + propMap.get("squadGroupFeeStatusName") + "'");
        }
        if (propMap.get("productionCostsDetailNum") != null && !"".equals(propMap.get("productionCostsDetailNum"))) {
            sql.append(" and a.productionCostsDetailNum=" + "'" + propMap.get("productionCostsDetailNum") + "'");
        }
        if (propMap.get("productionCostsDetailRemark") != null && !"".equals(propMap.get("productionCostsDetailRemark"))) {
            sql.append(" and a.productionCostsDetailRemark" + " like " + "'%" + propMap.get("productionCostsDetailRemark") + "%'");
        }


        StringBuilder sum = new StringBuilder("SELECT " +
                "COUNT(*)" +
                "FROM ProductionCostsDetailBean as a " +
                "LEFT  JOIN  a.userBean " +
                "LEFT JOIN a.squadGroupFeeStatusBean ");
        sum.append(" where 1=1 ");
        if (propMap.get("productionCostsDetailType") != null && !"".equals(propMap.get("productionCostsDetailType"))) {
            sum.append(" and a.productionCostsDetailType=" + "'" + propMap.get("productionCostsDetailType") + "'");
        }
        if (propMap.get("squadGroupFeeStatusName") != null && !"".equals(propMap.get("squadGroupFeeStatusName"))) {
            sum.append(" and a.squadGroupFeeStatusBean.squadGroupFeeStatusName=" + "'" + propMap.get("squadGroupFeeStatusName") + "'");
        }
        if (propMap.get("productionCostsDetailNum") != null && !"".equals(propMap.get("productionCostsDetailNum"))) {
            sum.append(" and a.productionCostsDetailNum=" + "'" + propMap.get("productionCostsDetailNum") + "'");
        }
        if (propMap.get("productionCostsDetailRemark") != null && !"".equals(propMap.get("productionCostsDetailRemark"))) {
            sum.append(" and a.productionCostsDetailRemark" + " like " + "'%" + propMap.get("productionCostsDetailRemark") + "%'");
        }

        Query nativeQuery = entityManager.createQuery(sql.toString());
        Query nativeQuery1 = entityManager.createQuery(sum.toString());
        Long count = (Long) nativeQuery1.getSingleResult();
        int pageNum = 0;
        int pageSize = 10;
        if (propMap.get("pageNum") != null && propMap.get("pageSize") != null) {
            pageNum = (Integer.parseInt((String) propMap.get("pageNum")) - 1) * Integer.parseInt((String) propMap.get("pageSize"));
            pageSize = Integer.parseInt((String) propMap.get("pageSize"));
        }
        nativeQuery.setFirstResult(pageNum);
        nativeQuery.setMaxResults(pageSize);
        List<Map> resultList = nativeQuery.getResultList();
        PagingData<Object> objectPagingData = new PagingData<>();
        objectPagingData.setRows(resultList);
        objectPagingData.setTotalRows(count);
        return RestResponse.success(objectPagingData);
    }

}
