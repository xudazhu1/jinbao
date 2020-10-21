package com.hnjbkc.jinbao.utils.layuisoultable;

import org.hibernate.Session;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * @author linjianfeng
 * @date 2020/1/7
 */
@Component
public class Sql2SoulTableDao {

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

    public Page<Map<String,Object>> invokeHqlSoulTable(String hql, SoulPage soulPage){
        Session delegate = (Session) entityManager.getDelegate();
        String sql = soulTableDao.hql2Sql(hql, delegate.getSessionFactory());

        Map<String , String > aliasMap = soulTableDao.createAliasMap( hql , sql );
        sql = setSqlAlias(sql, aliasMap);

       return invoke(sql, soulPage);
    }

    public Page<Map<String,Object>> invokeSqlSoulTable(String sql, SoulPage soulPage){

       return invoke(sql, soulPage);
    }


    public Page<Map<String,Object>> invoke(String sql, SoulPage soulPage){
        String newSql = "select  innerTable.* from ( " + sql + " ) as innerTable where (1 = 1) ";
        StringBuffer stringBuffer = new StringBuffer( newSql );
        //添加全局搜索
        soulTableDao.addGlobalSearch( stringBuffer , soulPage.getGlobalSearch() , soulTableDao.getAlias4Sql( sql ) );
        newSql = stringBuffer.toString();
        newSql = soulTableDao.addCondition4Sql( soulPage.getFilterSoses() , new StringBuffer(newSql)  , null ) .toString();

        //设置分页参数
        newSql += " limit " + ( ( soulPage.getPage() - 1 ) * soulPage.getLimit() ) + " , " + soulPage.getLimit();

        //计算总数
        String countSql = " select count(*) " + newSql.substring( newSql.indexOf("from") , newSql.indexOf("limit"));
        BigInteger count = (BigInteger) entityManager.createNativeQuery( countSql ).getSingleResult();
        System.out.println("count -->  " + count);

        //查询结果List<Map>
        Query nativeQuery = entityManager.createNativeQuery(newSql);
        nativeQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List resultList = nativeQuery.getResultList();
        System.out.println("content -->  " + resultList);
        return new PageImpl<Map<String,Object>>( resultList  , PageRequest.of(  soulPage.getPage() - 1   , soulPage.getLimit() )  , count.longValue()  );
    }
    public List invokeHql2Map(String hql){
        Query query = entityManager.createQuery(hql);
        return query.getResultList();
    }

    /**
     * 替换 别名
     * @param sql sql
     * @param aliasMap 别名map
     * @return 返回sql
     */
    private String setSqlAlias(String sql, Map<String , String > aliasMap){
        for (String hqlAlias : aliasMap.keySet()) {
            sql = sql.substring(0, sql.indexOf(aliasMap.get(hqlAlias))) +
                    hqlAlias +
                    sql.substring(sql.indexOf(aliasMap.get(hqlAlias)) + aliasMap.get(hqlAlias).length());
        }
        return sql;
    }

    public String getInHqlCondition4String(String column, List list){
       return getInHqlCondition(column, list, null);
    }

    public String getInHqlCondition4Map(String column, List list, String keyName){
        return getInHqlCondition(column, list, keyName);
    }

    /**
     * 返回 column in ()  前面需要制定and
     * @param column 列名
     * @param list list   里面元素是 map 或者string
     * @param keyName 如果是map 需要传key name
     * @return String
     */
    @SuppressWarnings("unchecked")
    private String getInHqlCondition(String column, List list, String keyName){
        if (column == null || list.size() == 0){
            return " 1 = 0 ";
        }
        StringBuilder inCondition = new StringBuilder(column).append(" in (");
        if (list.get(0) instanceof List){
            for (Object o : list) {
                String key = (String) o;
                inCondition.append("'").append(key).append("',");
            }
        }
        if (list.get(0) instanceof Map){
            if (keyName == null){
                return " 1 = 0 ";
            }
            for (Object o : list) {
                Map<String, Object> map = (Map<String, Object>) o;
                inCondition.append("'").append(map.get(keyName)).append("',");
            }
        }
        inCondition.deleteCharAt(inCondition.length() - 1);
        inCondition.append(" )");
        return inCondition.toString();
    }
}
