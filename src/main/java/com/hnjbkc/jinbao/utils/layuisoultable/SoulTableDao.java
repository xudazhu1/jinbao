package com.hnjbkc.jinbao.utils.layuisoultable;

import com.hnjbkc.jinbao.utils.MyBeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.*;

/**
 * @author xudaz
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@Component
public class SoulTableDao {


    /**
     * 注入实体管理器,执行持久化操作
     */
    @PersistenceContext
    public EntityManager entityManager;

    public Object get4layUiTable(SoulPage soulPage  ) {

        //生成 select主体
        String selectFields = createSelectHql( soulPage.getSelectFields() );

        //生成 from xxx 中间体
        String centerHql = createCenterHqlByField( MyBeanUtils.getBean4TableName( soulPage.getTableName()  ) , soulPage.getJoins());

        String hql = selectFields + " " + centerHql;

        //如果有自定义条件 添加
        hql = addCustomWhere( soulPage.getCustomWhere() ,  hql );

        //添加group By
        hql =  addGroupBy(  soulPage.getGroupBy() , hql );

        //拿到sql
        Session session = (Session) entityManager.getDelegate();
        String sql = hql2Sql(hql, session.getSessionFactory());
        //拿到所有别名 以便转换拼接
        Map<String , String > aliasMap = createAliasMap( hql , sql );

        String newSql = "select  innerTable.* from ( " + sql + " ) as innerTable";

        //生成雏形
        StringBuffer sqlTemp = new StringBuffer(newSql).append(" where ( 1=1 )");
        //添加全局搜索 globalSearch
        addGlobalSearch( sqlTemp , soulPage.getGlobalSearch() , getAlias4Sql( sql ) );

        //添加soul搜索
        //添加 hql 条件
        StringBuffer stringBuffer = addCondition4Sql( soulPage.getFilterSoses() , sqlTemp  , aliasMap );

        //组合新sql
        newSql = stringBuffer.toString();
        //添加分页
        newSql += " limit " + ( ( soulPage.getPage() - 1 ) * soulPage.getLimit() ) + " , " + soulPage.getLimit();

        //使用sql 创建子查询
        List resultList1 = entityManager.createNativeQuery( newSql ).getResultList();

        //hql别名 用作转换返回数据
        List<String> hqlAlias4Sql = getAlias4Sql( hql );

        //计算总数
        String newCountSql = " select count(*) " + newSql.substring( newSql.indexOf("from") , newSql.indexOf( " limit " ) );

        //使用sql 创建子查询
        BigInteger count = (BigInteger) entityManager.createNativeQuery( newCountSql ).getSingleResult();

        //返回数据
        return  new PageImpl<>( formatResultData( resultList1 , hqlAlias4Sql )  , PageRequest.of(  soulPage.getPage() - 1   , soulPage.getLimit() )  , count.longValue()  );

    }

    /**
     * 添加全局搜索
     * @param sqlTemp  sql
     * @param globalSearch 全局搜索的值
     * @param sqlAlias4Sql sql 别名们
     */
    public void addGlobalSearch(StringBuffer sqlTemp, String globalSearch , List<String> sqlAlias4Sql ) {
        if ( globalSearch == null || "".equals( globalSearch )  ) {
            return;
        }
        sqlTemp.append( " and ( 1=0 " );
        //sql别名 用作拼接全局like
        for (String alias : sqlAlias4Sql) {
            sqlTemp.append( " or innerTable." ).append( alias ).append( " like '%" ).append( globalSearch ).append( "%' " );
        }
        sqlTemp.append( " ) " );
    }


    private String addCustomWhere( String customWhere , String hql ) {
        if ( customWhere == null || "".equals( customWhere) ) {
            return hql;
        }
        StringBuilder stringBuilder = new StringBuilder(hql);
        if ( ! hql.contains(" where ") && ! hql.contains( " WHERE " ) ) {
           stringBuilder.append( " where ( 1=1 )");
        }
        String[] split = customWhere.split("[$]");
        for (String s : split) {
            stringBuilder.append( " and ( ").append( s ).append(" ) ");
        }
        return stringBuilder.toString();
    }

    public StringBuffer addCondition4Sql( List<FilterSo> filterSos , StringBuffer sql , Map<String , String > aliasMap ) {
        for (FilterSo filterSo : filterSos ) {
            handleFilterSo(  filterSo ,  sql , aliasMap);
        }
        return sql ;
    }

    private List<Map<String , Object>> formatResultData( List resultList1 , List<String> hqlAlias4Sql  ) {
        List<Map<String , Object >> resultMapList =  new ArrayList<>();
        for (Object o : resultList1) {
            Object[] objects = (Object[]) o;
            Map<String , Object > map = new HashMap<>(objects.length );
            for (int i = 0; i < objects.length; i++) {
                map.put( hqlAlias4Sql.get( i ) , objects[i] );
            }
            resultMapList.add( map );
        }
        return resultMapList;
    }

    /**
     * 为hql 和sql的别名做映射 把hql的别名转为sql的别名 拼接删选条件到where语句后面
     * @param sql sql 语句
     * @param hql hql 语句
     * @return 映射map key hql value sql
     */
    public Map<String , String> createAliasMap(String hql, String sql) {
        List<String> sqlAlias4Sql = getAlias4Sql( sql );
        List<String> hqlAlias4Sql = getAlias4Sql( hql );
        Map<String , String > aliasMap = new HashMap<>(sqlAlias4Sql.size() );
        //创建 hql别名 --> sql 别名的对应关系
        for (int i = 0; i < sqlAlias4Sql.size(); i++) {
            aliasMap.put( hqlAlias4Sql.get( i ) ,sqlAlias4Sql.get( i ) );
        }

        return aliasMap;
    }


    public String addGroupBy(String groupBy, String sql) {
        if ( groupBy == null || "".equals( groupBy ) ) {
            return sql;
        }
        StringBuilder stringBuilder = new StringBuilder(sql).append( "group by ");
        String[] split = groupBy.trim().split("[$]");
        for (int i = 0; i < split.length; i++) {
            if ( i == 0 ) {
                stringBuilder.append( split[i] ).append(" " );
            } else {
                stringBuilder.append(" , ").append( split[i] ).append(" " );
            }
        }
        return stringBuilder.toString();
    }


    /**
     * 处理表头筛选数据
     *
     * @author Yelog
     * @date 2019-03-16 22:52
     * @param filterSo f
     * @param filterSql  f
     */
    private void handleFilterSo(FilterSo filterSo, StringBuffer  filterSql , Map<String , String> aliasMap ) {
        //更新别名
        if (aliasMap == null ){
            filterSo.setField( "innerTable." +  filterSo.getField() );
        }else {
            filterSo.setField( "innerTable." +  aliasMap.get( filterSo.getField() ) );
        }


        if (!StringUtils.endsWith(filterSql.toString() , "(") && !StringUtils.endsWith(filterSql.toString() , "where")) {
            filterSql.append(StringUtils.isBlank(filterSo.getPrefix())?" and":" "+filterSo.getPrefix());
        }

        String value = filterSo.getValue();
        switch (filterSo.getMode() ) {
            case "in":
                if (filterSo.getValues()==null || filterSo.getValues().size()==0) {
                    filterSql.append(" 1=1");
                    break;
                }
                if ("date".equals(filterSo.getType())) {
                    filterSql.append(" DATE_FORMAT(");
                    filterSql.append(filterSo.getField())
                            .append(", '");
                    filterSql.append(filterSo.getValue()
                            .replaceAll("yyyy", "%Y")
                            .replaceAll("MM", "%m")
                            .replaceAll("dd", "%d")
                            .replaceAll("HH", "%H")
                            .replaceAll("mm", "%i")
                            .replaceAll("ss", "%s"));
                    filterSql.append("') in ('")
                            .append(StringUtils.join(filterSo.getValues(), "','"))
                            .append("')");
                } else {
                    if (StringUtils.isBlank(filterSo.getSplit())) {
                        filterSql.append(" ")
                                .append(filterSo.getField())
                                .append(" in ('")
                                .append(StringUtils.join(filterSo.getValues(), "','"))
                                .append("')");
                    } else {
                        filterSql.append(" ")
                                .append(filterSo.getField())
                                .append(" regexp '(");
                        for (String filterSoValue : filterSo.getValues()) {
                            filterSql.append("(").append(filterSo.getSplit()).append("|^){1}").append(filterSoValue).append("(").append(filterSo.getSplit()).append("|$){1}|");
                        }
                        filterSql.deleteCharAt(filterSql.length() - 1);
                        filterSql.append(")+'");
                    }
                }
                break;
            case "condition":
                if (StringUtils.isBlank(filterSo.getType()) || ((!"null".equals(filterSo.getType()) && !"notNull".equals(filterSo.getType())) && StringUtils.isBlank(filterSo.getValue()))) {
                    filterSql.append(" 1=1");
                    break;
                }
                filterSql.append(" ");
                filterSql.append( filterSo.getField() );
                switch (filterSo.getType()) {
                    case "eq":
                        filterSql.append(" = '").append(value).append("'");
                        break;
                    case "ne":
                        filterSql.append(" != '").append(value).append("'");
                        break;
                    case "gt":
                        filterSql.append(" > '").append(value).append("'");
                        break;
                    case "ge":
                        filterSql.append(" >= '").append(value).append("'");
                        break;
                    case "lt":
                        filterSql.append(" < '").append(value).append("'");
                        break;
                    case "le":
                        filterSql.append(" <= '").append(value).append("'");
                        break;
                    case "contain":
                        filterSql.append(" like '%").append(value).append("%'");
                        break;
                    case "notContain":
                        filterSql.append(" not like '%").append(value).append("%'");
                        break;
                    case "start":
                        filterSql.append(" like '").append(value).append("%'");
                        break;
                    case "end":
                        filterSql.append(" like '%").append(value).append("'");
                        break;
                    case "null":
                        filterSql.append(" is null");
                        break;
                    case "notNull":
                        filterSql.append(" is not null");
                        break;
                    default:break;
                }
                break;
            case "date":
                filterSql.append(" ");
                filterSql.append( filterSo.getField() );
                switch (filterSo.getType()) {
                    case "yesterday":
                        filterSql.append(" between date_add(curdate(), interval -1 day) and date_add(curdate(),  interval -1 second) ");
                        break;
                    case "thisWeek":
                        filterSql.append(" between date_add(curdate(), interval - weekday(curdate()) day) and date_add(date_add(curdate(), interval - weekday(curdate())+7 day), interval -1 second) ");
                        break;
                    case "lastWeek":
                        filterSql.append(" between date_add(curdate(), interval - weekday(curdate())-7 day) and date_add(date_add(curdate(), interval - weekday(curdate()) day), interval -1 second) ");
                        break;
                    case "thisMonth":
                        filterSql.append(" between date_add(curdate(), interval - day(curdate()) + 1 day) and DATE_ADD(last_day(curdate()), interval 24*60*60-1 second) ");
                        break;
                    case "thisYear":
                        filterSql.append(" between date_sub(curdate(),interval dayofyear(now())-1 day) and str_to_date(concat(year(now()),'-12-31 23:59:59'), '%Y-%m-%d %H:%i:%s') ");
                        break;
                    case "specific":
                        filterSql.append(" between str_to_date('").append(filterSo.getValue()).append("', '%Y-%m-%d') and str_to_date(concat('").append(filterSo.getValue()).append("',' 23:59:59'), '%Y-%m-%d %H:%i:%s') ");
                        break;
                    case "all":
                    default:
                        filterSql.delete(filterSql.lastIndexOf(" "), filterSql.length());
                        filterSql.append(" 1=1");
                        break;
                }
                break;
            case "group":
                filterSql.append(" (");
                if (filterSo.getChildren().size()>0) {
                    filterSo.getChildren().forEach(f-> handleFilterSo(f , filterSql , aliasMap ));
                } else {
                    filterSql.append(" 1=1");
                }
                filterSql.append(" )");
            default:break;
        }
    }




    public List<String> getAlias4Sql( String sql ) {
        List<String> alias = new ArrayList<>();
        //从from 前 切断  select 后切断
        sql = sql.substring( 0 , sql.indexOf( "from") );
        sql = sql.substring( sql.indexOf( "select") + 6 );
        //遍历找到每个as 然后 逗号 的角标 然后提取中间的字符串 然后去前后空格
        String as = " as ";
        while ( sql.indexOf( as ) > 0 ) {
            //找到as 角标后 紧接着的 , 逗号
            int asIndex = sql.indexOf(" as ");
            int indexOf = sql.substring(  asIndex ).indexOf(",");
            //如果找不到 处理完 结束
            if ( indexOf == -1 ) {
                alias.add( sql.substring( asIndex + 3  ).trim() );
                break;
            } else {
                alias.add( sql.substring( asIndex + 3 ,  indexOf + asIndex ).trim() );
            }

            //sql截取下 准备下次循环
            sql = sql.substring( indexOf + 1 + asIndex );
        }
        return alias;
    }


    public  String hql2Sql(String hql , SessionFactory sessionFactory ) {
        // 当hql为null或空时,直接返回null
        if (hql == null || "".equals(hql) ) {
            return "";
        }
        // 获取当前session
        // 得到session工厂实现类
        SessionFactoryImpl sfi = (SessionFactoryImpl) sessionFactory;
        // 得到Query转换器实现类
        QueryTranslatorImpl queryTranslator = new QueryTranslatorImpl(hql, hql, Collections.EMPTY_MAP, sfi);
        queryTranslator.compile(Collections.EMPTY_MAP, false);
        // 得到sql
        return queryTranslator.getSQLString();
    }

    private String createSelectHql( String selectFields ) {
        String[] split = selectFields.split("[$]");

        StringBuilder selectHql = new StringBuilder("select " );
        for (int i = 0; i < split.length; i++) {
            if ( i == 0 ) {
                selectHql.append( " " ).append( split[i] ).append( " " );
            } else {
                selectHql.append( " , " ).append( split[i] ).append( " " );
            }
        }
        return selectHql.toString();
    }

    private String createCenterHqlByField(Class bean, String fields  ) {
        String[] splitField = fields.split("[$]");
        List<String> split = new ArrayList<>(Arrays.asList(splitField));
        //生成新的 去包含的name前缀
        List<String> nameNew = new ArrayList<>();
        loop :
        for (String s : split) {
            s = s+ ".xudazhu";
            if( s.contains("[n]") ) {
                s = s.replace("[n]" , "");
            }
//            s = MyBeanUtils.getToLowerNameByBean(bean) + "." + s;
            String[] split1 = s.split("[.]");
            if ( split1.length == 1 ) {
                continue ;
            }
            for (int i = 0; i < nameNew.size(); i++) {
                if ( nameNew.get(i).startsWith( s ) ) {
                    continue loop;
                }
                if ( s.startsWith( nameNew.get(i) ) ) {
                    nameNew.set(i , s.substring(0 , s.lastIndexOf(".")) );
                    continue  loop;
                }
            }
            nameNew.add( s.substring(0 , s.lastIndexOf(".")) );
        }
        StringBuilder stringBuilder = new StringBuilder( " from " + bean.getSimpleName() + " " + MyBeanUtils.getToLowerNameByBean( bean ));

        List<String> newNameList = new ArrayList<>();

        for (String nameTemp : nameNew) {
            String[] split1 = nameTemp.split("[.]");
            Class oldBean = bean;
            StringBuilder oldBeanName = new StringBuilder(MyBeanUtils.getToLowerNameByBean(bean));
            for (String aSplit1 : split1) {
                //拿到此次新bean
                String oldName = oldBeanName.toString();
                String newName = oldBeanName.append("XXX").append(aSplit1).toString();
                if( ! newNameList.contains(newName) ) {
                    stringBuilder.append(createLeftJoin(oldBean, aSplit1 , oldName , newName ));
                    newNameList.add(newName);
                }
                //新bean复制给原bean 开始下个循环
                oldBean = MyBeanUtils.getBeanByField(oldBean, aSplit1);
            }
        }
        return  stringBuilder.toString();

    }


    private String createLeftJoin(Class bean , String fieldName , String oldBeanName , String newBeanName ) {
        //拿到新bean
        Class beanByField = MyBeanUtils.getBeanByField(bean, fieldName);
        assert beanByField != null;
        try {
            Field declaredField = bean.getDeclaredField(fieldName);
            OneToMany oneToMany = declaredField.getAnnotation(OneToMany.class);
            if ( oneToMany != null ) {
                String mappedBy = oneToMany.mappedBy();
                return
                        " left join fetch   "  + beanByField.getSimpleName() + " " + newBeanName
                                + "  on " + newBeanName + "." + mappedBy + "=" + oldBeanName + " ";
            }
            OneToOne oneToOne = declaredField.getAnnotation(OneToOne.class);
            if ( oneToOne != null && (! "".equals(oneToOne.mappedBy()) ) ) {
                String mappedBy = oneToOne.mappedBy();
                return
                        " left join fetch   "  + beanByField.getSimpleName() + " " + newBeanName
                                + "  on " + newBeanName + "." + mappedBy + "=" + oldBeanName + " ";
            }
            ManyToMany manyToMany = declaredField.getAnnotation(ManyToMany.class);
            if ( manyToMany != null ) {
                return
                        " left join "  + oldBeanName + "." + fieldName
                                + "   " + newBeanName ;
            }

        } catch ( Exception e) {
            System.out.println("生成左外连接 HQL 出错 ");
        }

        return
                " left join fetch   "  + beanByField.getSimpleName() + " " + newBeanName
                        + "  on " + oldBeanName + "." + fieldName + "=" + newBeanName + " ";
    }

}
