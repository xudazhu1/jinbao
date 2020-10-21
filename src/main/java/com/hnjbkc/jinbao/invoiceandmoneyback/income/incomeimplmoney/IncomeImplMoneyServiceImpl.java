package com.hnjbkc.jinbao.invoiceandmoneyback.income.incomeimplmoney;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.hqldao.ManyAndOneToOneAndOne;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.implement.ImplementBean;
import com.hnjbkc.jinbao.invoiceandmoneyback.income.IncomeBean;
import com.hnjbkc.jinbao.project.ProjectBean;
import com.hnjbkc.jinbao.utils.layuisoultable.FilterSo;
import com.hnjbkc.jinbao.utils.layuisoultable.SoulPage;
import com.hnjbkc.jinbao.utils.layuisoultable.SoulTableDao;
import com.hnjbkc.jinbao.utils.tableutils.TableUtilsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author xudaz
 * @date 2019/12/2
 */
@SuppressWarnings("WeakerAccess")
@Service
public class IncomeImplMoneyServiceImpl implements BaseService<IncomeImplMoneyBean> {

    SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Override
    public IncomeImplMoneyBean add(IncomeImplMoneyBean incomeImplMoneyBean) {
        return null;
    }

    @Override
    public Boolean delete(Integer id) {
        return null;
    }

    @Override
    public IncomeImplMoneyBean update(IncomeImplMoneyBean incomeImplMoneyBean){
        return null;
    }

    private SoulTableDao soulTableDao;

    @Autowired
    public void setSoulTableDao(SoulTableDao soulTableDao) {
        this.soulTableDao = soulTableDao;
    }

    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    public void setMappingJackson2HttpMessageConverter(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
        this.mappingJackson2HttpMessageConverter = mappingJackson2HttpMessageConverter;
    }

    /**
     * 注入实体管理器,执行持久化操作
     */
    @PersistenceContext
    public EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public Object get4layuiTable(SoulPage soulPage , String filterSos ) throws IOException {
        // 把搜索条件 封装进 soulPage
        if ( filterSos != null ) {
            ObjectMapper objectMapper = mappingJackson2HttpMessageConverter.getObjectMapper();
            List<Map<String , Object >> mapList =objectMapper.readValue(filterSos, List.class);
            List<FilterSo> filterSos1 = soulPage.getFilterSoses();
            for (Map<String, Object> stringObjectMap : mapList) {
                FilterSo filterSo = objectMapper.readValue(objectMapper.writeValueAsString(stringObjectMap), FilterSo.class);
                filterSos1.add(filterSo);
            }
        }
        return soulTableDao.get4layUiTable( soulPage   );
    }


    @Transactional(rollbackOn = Exception.class )
    public Object flushImplMoney()  {
        try {
            //拿到所有已通过审核的回款
            String hql = " from IncomeBean incomeBean where incomeBean.incomeAuditStatus='1' ";

            Query query = entityManager.createQuery(hql);
            //添加实体图
            List<EntityGraph<? super IncomeBean>> entityGraphs = entityManager.getEntityGraphs( IncomeBean.class );
            if (entityGraphs != null && entityGraphs.size() > 0) {
                query.setHint("javax.persistence.loadgraph", entityGraphs.get(0));
                System.out.println(" setHint 'javax.persistence.loadgraph' ==> " + entityGraphs.get(0));
            }
            List<ProjectBean> projectBeansTemp = new ArrayList<>();

            List resultList = query.getResultList();
            for (Object o : resultList) {
                IncomeBean incomeBean = (IncomeBean) o;
                if ( incomeBean.getProjectBean() != null ) {
                    projectBeansTemp.add( incomeBean.getProjectBean() );
                }
            }

            //为项目添加实施
            manyAndOneToOneAndOne.getCascades( projectBeansTemp );


            for (Object o : resultList ) {
                IncomeBean incomeBean = (IncomeBean) o;
                //如果单个实施部 拿到实施部 并生成数据
                if ( incomeBean.getProjectBean() != null && incomeBean.getProjectBean().getImplementBeans().size() == 1 ) {
                    ImplementBean implementBean = incomeBean.getProjectBean().getImplementBeans().get(0);
                    IncomeImplMoneyBean incomeImplMoneyBean = new IncomeImplMoneyBean();
                    incomeImplMoneyBean.setIncomeBean( incomeBean );
                    incomeImplMoneyBean.setImplementBean( implementBean );
                    incomeImplMoneyBean.setIncomeImplMoney( incomeBean.getIncomeMoney() );
                    List<IncomeImplMoneyBean> incomeImplMoneyBeans = incomeBean.getIncomeImplMoneyBeans();
                    for (IncomeImplMoneyBean implMoneyBean : incomeImplMoneyBeans) {
                        tableUtilsDao.delete( "income_impl_money" ,  implMoneyBean.getIncomeImplMoneyId() );
                    }
                    tableUtilsDao.update( incomeImplMoneyBean );
                }
            }
            return true;
        } catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }
    }



    TableUtilsDao tableUtilsDao;

    @Autowired
    public void setTableUtilsDao(TableUtilsDao tableUtilsDao) {
        this.tableUtilsDao = tableUtilsDao;
    }

    private ManyAndOneToOneAndOne manyAndOneToOneAndOne;

    @Autowired
    public void setManyAndOneToOneAndOne(ManyAndOneToOneAndOne manyAndOneToOneAndOne) {
        this.manyAndOneToOneAndOne = manyAndOneToOneAndOne;
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }




    public Object getInfoByIncomeId(Integer incomeId) {
        String hql = "from IncomeBean income where income.incomeId=" + incomeId;
        Page<IncomeBean> incomeBeans = sqlUtilsDao.exSql(IncomeBean.class, hql, PageRequest.of(0, Integer.MAX_VALUE), "income.incomeId");
        if ( incomeBeans.getContent().size() == 0 ) {
            return null;
        }
        IncomeBean incomeBean = incomeBeans.getContent().get(0);
        try {
            manyAndOneToOneAndOne.getCascades( new ArrayList<>( Collections.singletonList( incomeBean.getProjectBean() )) );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return incomeBean;

    }
}
