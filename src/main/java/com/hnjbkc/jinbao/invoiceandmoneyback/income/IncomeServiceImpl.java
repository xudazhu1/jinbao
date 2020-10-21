package com.hnjbkc.jinbao.invoiceandmoneyback.income;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.utils.MyBeanUtils;
import com.hnjbkc.jinbao.utils.PageableUtils;
import com.hnjbkc.jinbao.utils.tableutils.TableUtilsDao;
import com.hnjbkc.jinbao.workload.userwork.UserWorkBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xudaz
 */
@Service
public class IncomeServiceImpl implements BaseService<IncomeBean> {

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

    @Override
    @Transactional(rollbackOn = Exception.class )
    public IncomeBean add(IncomeBean incomeBean) {
        IncomeBean update = tableUtilsDao.update(incomeBean);
        //如果编号不存在 更新编号
        if ( update != null  ) {
            if ( update.getIncomeNum() == null  || "".equals( update.getIncomeNum() ) ) {
                //编号 S 019 11 02132
                MyBeanUtils.createNumber( update , "SR0" , "incomeNum" , "moneyBackBeans.moneyBackNum" );
            }
         }
        return  update ;
    }

    @Override
    public Boolean delete(Integer id) {
        return tableUtilsDao.delete( "income" , id );
    }

    @Override
    public IncomeBean update(IncomeBean incomeBean) {
        return add(incomeBean);
    }

    public List<Map<String, Object>> findIncomeBeanByProjectId(String projectId) {
        String resultMap = "new Map(" +
                "incomeId as incomeId, " +
                "incomeNum as incomeNum," +
                "incomeType as incomeType," +
                "invoiceBean.invoiceContent as invoiceContent," +
                "incomeMoney as incomeMoney," +
                "invoiceBean.invoiceMoney as invoiceMoney," +
                "incomeDate as incomeDate" +
                ")";
        Map<String, Object> map = new HashMap<>(1);
        map.put("projectBean.projectId",projectId);
        map.put("incomeType","项目收入-项目收入");
        map.put("incomeAuditStatus","1");
        Page<Map<String,Object>> postBeans = sqlUtilsDao.resultMapByCustomProps(IncomeBean.class, resultMap, map, PageableUtils.producePageable4Map(map, "incomeId"));
        return postBeans.getContent();
    }
}
