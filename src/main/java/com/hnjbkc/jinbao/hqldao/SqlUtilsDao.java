package com.hnjbkc.jinbao.hqldao;

import com.hnjbkc.jinbao.utils.EntityGraphUtils;
import com.hnjbkc.jinbao.utils.MyBeanUtils;
import com.hnjbkc.jinbao.utils.tableutils.CustomQueryDsl;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * @author xudaz
 * @date 2019/3/24
 */
@SuppressWarnings({"WeakerAccess", "unused", "Duplicates"})
@Component
public class SqlUtilsDao {

    /**
     * 注入实体管理器,执行持久化操作
     */
    @PersistenceContext
    public EntityManager entityManager;


    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }



    /**
     * 使用HQL的方式自定义查询的方法(模糊查询 key = '%value%')
     *
     * @param tClass      要查询的pojo类 / javaBean
     * @param map         查询条件
     *                    正常 : key = value
     *                    范围 : $S.key = value[2] 拼接后 key >= value[1] and key <= value[2]
     *                    多选 : $D.key = value[n] 拼接后 key = value[1] or key = value[2] ...
     * @param pageable    分页参数
     * @param customWhere 自定义要查询的条件 如 : X = Y
     * @param <T>         泛型 决定返回什么类型的查询结果
     * @return 返回查询结果 包括分页参数
     */
    public <T> Page<T> searchAllByCustomProps(@NotNull Class<T> tClass, @NotNull Map<String, Object> map, @NotNull Pageable pageable, @Nullable String... customWhere) {
        String strBuilder = joiningHQLBySearch(tClass, map, pageable, customWhere);
        return exSql(tClass, strBuilder, pageable, null);
    }

    public Page<Map<String,Object>> resultMapByCustomProps(@NotNull Class tClass,String resultHql ,@NotNull Map<String, Object> map, @NotNull Pageable pageable, @Nullable String... customWhere) {
        String strBuilder = joiningHQLByGet(tClass, map, pageable, customWhere);
        return exHqlResultMap(tClass,"select " + resultHql ,strBuilder, pageable, null);
    }
    /**
     * 使用HQL的方式自定义查询的方法 (精确查询 key = 'value')
     *
     * @param tClass      要查询的pojo类 / javaBean
     * @param map         查询条件
     *                    正常 : key = value
     *                    范围 : $S.key = value[2] 拼接后 key >= value[1] and key <= value[2]
     *                    多选 : $D.key = value[n] 拼接后 key = value[1] or key = value[2] ...
     * @param pageable    分页参数
     * @param customWhere 自定义要查询的条件 如 : X = Y
     * @param <T>         泛型 决定返回什么类型的查询结果
     * @return 返回查询结果 包括分页参数
     */
    public <T> Page<T> getAllByCustomProps(Class<T> tClass, Map<String, Object> map, Pageable pageable, String... customWhere) {
        String strBuilder = joiningHQLByGet(tClass, map, pageable, customWhere);
        return exSql(tClass, strBuilder, pageable, null);
    }


    public <T> Page<T> getAllByCustomPropsCustomJoin(Class<T> tClass, Map<String, Object> map, EntityGraphUtils.EntityGraphUtilsInfo[] entityGraphUtilsInfo, Pageable pageable, String groupBy, String... customWhere) {
        //自定义join
        String strBuilder = joiningHQLByGet(tClass, map, pageable, customWhere);
        return exSqlCustomJoin(tClass, strBuilder, entityGraphUtilsInfo, pageable, groupBy);
    }

    /**
     * 使用HQL的方式自定义查询的方法 (模糊查询 key = '%value%')
     *
     * @param tClass      要查询的pojo类 / javaBean
     * @param map         查询条件
     *                    正常 : key = value
     *                    范围 : $S.key = value[2] 拼接后 key >= value[1] and key <= value[2]
     *                    多选 : $D.key = value[n] 拼接后 key = value[1] or key = value[2] ...
     * @param pageable    分页参数
     * @param groupBy     groupBy
     * @param customWhere 自定义要查询的条件 如 : X = Y
     * @param <T>         泛型 决定返回什么类型的查询结果
     * @return 返回查询结果 包括分页参数
     */
    public <T> Page<T> searchAllByCustomPropsGroupBy(Class<T> tClass, Map<String, Object> map, Pageable pageable, String groupBy, String... customWhere) {
        String strBuilder = joiningHQLBySearch(tClass, map, pageable, customWhere);
        return exSql(tClass, strBuilder, pageable, groupBy);
    }


    /**
     * 使用HQL的方式自定义查询的方法(自定义前缀)
     *
     * @param tClass      要查询的pojo类 / javaBean (精确查询 key = 'value')
     * @param startWith   select xxx
     * @param groupBy     groupBy xxx
     * @param map         查询条件
     *                    正常 : key = value
     *                    范围 : $S.key = value[2] 拼接后 key >= value[1] and key <= value[2]
     *                    多选 : $D.key = value[n] 拼接后 key = value[1] or key = value[2] ...
     * @param pageable    分页参数
     * @param customWhere 自定义要查询的条件 如 : X = Y
     * @param <T>         泛型 决定返回什么类型的查询结果
     * @return 返回查询结果 包括分页参数
     */
    public <T> Page getAllByCustomPropsAndSelectAndGroupBy(Class<T> tClass, Map<String, Object> map, Pageable pageable, String startWith, String groupBy, String... customWhere) {
        String strBuilder = joiningHQLBySearch(tClass, map, pageable, customWhere);
        return exSqlBySelect(tClass, startWith + " " + strBuilder, pageable, groupBy);
    }


    /**
     * 拼接hql的方法 按照 map 拼接
     *
     * @param tClass      class
     * @param map         查询条件
     *                    正常 : key = %value%
     *                    范围 : $S.key = value[2] 拼接后 key >= value[0] and key <= value[1]
     *                    多选 : $D.key = value[n] 拼接后 key = value[1] or key = value[2] ...
     * @param pageable    分页参数
     * @param customWhere 自定义要查询的条件 如 : X = Y
     * @return 返回查询结果 包括分页参数
     */
    public String joiningHQLBySearch(Class tClass, Map<String, Object> map, Pageable pageable, String... customWhere) {
        //生成类名
        String simpleName = tClass.getSimpleName();
        //生成假删除属性名
        //类名首字母转成小写 (作用: 类的别名 from DrawBean drawBean where xxxx=xxxx)
        String propName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
        //from DrawBean drawBean
        StrBuilder strBuilder = new StrBuilder(" from ").append(simpleName).append(" ").append(propName);
        //from DrawBean drawBean  where ( 1 = 1 )
        strBuilder.append(" where ( 1 = 1 ) ");
        //from DrawBean drawBean  where ( 1 = 1 )  and ( roleName like '%强%' or roleDescribe like '%强%' )
        strBuilder.append(" and ( ").append(joiningAllSearch(map, tClass)).append(" ) ");
        //拼接map条件
        String startsWith = "$S.", contains = "~", startsWith2 = "$D.";
        String pageNumStr = "pageNum", pageSizeStr = "pageSize", sortFieid = "sortFieid", sort = "sort", intMatches = "\\d+";
        map.forEach((key, value) -> {
            if ((!"".equals(value)) && (!pageNumStr.equals(key)) && (!pageSizeStr.equals(key)) && (!sortFieid.equals(key)) && (!sort.equals(key))) {
                //from DrawBean drawBean  where ( 1 = 1 )
                // and ( roleName like '%强%' or roleDescribe like '%强%' ) and (
                strBuilder.append(" and ( ");
                //如果是范围 $S.
                // from DrawBean drawBean  where ( 1 = 1 )
                //  and ( roleName like '%强%' or roleDescribe like '%强%' )
                //  and ( drawDate >= '2019-8-13' and drawDate < '2019-8-14' )
                if (key.startsWith(startsWith) && ((String) value).contains(contains)) {
                    String[] split = ((String) value).split("~");
                    if (!split[0].trim().matches(intMatches)) {
                        split[0] = "'" + split[0].trim() + "'";
                        split[1] = "'" + split[1].trim() + "'";
                    }
                    strBuilder.append(key.substring(3)).append(" >= ").append(split[0].trim()).append(" and ");
                    //如果value包含 -  说明是个日期
                    String dateTag = "[']\\d{4}[-]\\d{2}[\\s\\S]*";
                    if (split[1].matches(dateTag)) {
                        strBuilder.append(key.substring(3)).append(" < ").append(split[1].trim());
                    } else {
                        strBuilder.append(key.substring(3)).append(" <= ").append(split[1].trim());
                    }
                    //如果是 多选 $D.
                    // from DrawBean drawBean  where ( 1 = 1 )
                    //  and ( roleName like '%强%' or roleDescribe like '%强%' )
                    //  and ( drawDate >= '2019-8-13' and drawDate < '2019-8-14' )
                    //  and ( 1 = 0    or drawReturnWay='公对公' or drawReturnWay='公对私' )
                } else if (key.startsWith(startsWith2)) {
                    strBuilder.append("1 = 0 ");
                    String[] split = ((String) value).split("[$]");
                    for (String s : split) {
                        strBuilder.append(" or ");
                        strBuilder.append(key.substring(3)).append(" = '").append(s.trim()).append("' ");
                    }
                    //如果什么都不是
                    //from DrawBean drawBean  where ( 1 = 1 ) and ( key like '%value%' )
                } else {
                    strBuilder.append(key).append(" like ").append("'%" + value + "%'");
                }
                strBuilder.append(" ) ");

            }
        });
        //拼接自定义HQL条件
        if (customWhere != null && customWhere.length > 0) {
            for (String s : customWhere) {
                strBuilder.append(" and ( ").append(s).append(" ) ");
            }
        }
        //获取排序方式
        if (!pageable.getSort().isUnsorted()) {
            String[] split = pageable.getSort().toString().split(":");
            String orderBy = split[0] + " " + split[1];
            strBuilder.append("order by " + orderBy);
        }
        return strBuilder.toString();
    }

    /**
     * 拼接hql的方法 按照 map 拼接 (正常拼接 非%value%)
     *
     * @param tClass      class
     * @param map         查询条件
     *                    正常 : key = value
     *                    范围 : $S.key = value[2] 拼接后 key >= value[1] and key <= value[2]
     *                    多选 : $D.key = value[n] 拼接后 key = value[1] or key = value[2] ...
     * @param pageable    分页参数
     * @param customWhere 自定义要查询的条件 如 : X = Y
     * @param <T>         泛型 决定返回什么类型的查询结果
     * @return 返回查询结果 包括分页参数
     */
    @SuppressWarnings("Duplicates")
    public <T> String joiningHQLByGet(Class<T> tClass, Map<String, Object> map, Pageable pageable, String... customWhere) {
        String simpleName = tClass.getSimpleName();
        String propName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
        StrBuilder strBuilder = new StrBuilder(" from ").append(simpleName).append(" ").append(propName);
        strBuilder.append(" where ( 1 = 1 ) ");
        strBuilder.append(" and ( ").append(joiningAllSearch(map, tClass)).append(" ) ");
        //拼接map条件
        String startsWith = "$S.", contains = "~", startsWith2 = "$D.";
        String pageNumStr = "pageNum", pageSizeStr = "pageSize", sortFieid = "sortFieid", sort = "sort", intMatches = "\\d+";
        map.forEach((key, value) -> {
            if ((!"".equals(value)) && (!pageNumStr.equals(key)) && (!pageSizeStr.equals(key)) && (!sortFieid.equals(key)) && (!sort.equals(key))) {
                strBuilder.append(" and ( ");
                //如果是范围 $S.
                if (key.startsWith(startsWith) && ((String) value).contains(contains)) {
                    String[] split = ((String) value).split("~");
                    if (!split[0].trim().matches(intMatches)) {
                        split[0] = "'" + split[0].trim() + "'";
                        split[1] = "'" + split[1].trim() + "'";
                    }
                    strBuilder.append(key.substring(3)).append(" >= ").append(split[0].trim()).append(" and ");
                    //如果value包含 -  说明是个日期
                    String dateTag = "[']\\d{4}[-]\\d{2}[\\s\\S]*";
                    if (split[1].matches(dateTag)) {
                        strBuilder.append(key.substring(3)).append(" < ").append(split[1].trim());
                    } else {
                        strBuilder.append(key.substring(3)).append(" <= ").append(split[1].trim());
                    }
                    //如果是 多选 $D.
                } else if (key.startsWith(startsWith2)) {
                    strBuilder.append("1 = 0 ");
                    String[] split = ((String) value).split("[$]");
                    for (String s : split) {
                        strBuilder.append(" or ");
                        strBuilder.append(key.substring(3)).append(" = '").append(s.trim()).append("' ");
                    }
                } else {
                    strBuilder.append(key).append(" = ").append("'" + value + "'");
                }
                strBuilder.append(" ) ");

            }
        });
        //拼接自定义HQL条件
        if (customWhere != null && customWhere.length > 0) {
            for (String s : customWhere) {
                strBuilder.append(" and ( ").append(s).append(" ) ");
            }
        }
        //获取排序方式
        if (!pageable.getSort().isUnsorted()) {
            String[] split = pageable.getSort().toString().split(":");
            String orderBy = split[0] + " " + split[1];
            strBuilder.append("order by " + orderBy);
        }
        return strBuilder.toString();
    }

    private String joiningAllSearch(Map<String, Object> map, Class tClass) {
        //multiple_key: roleName$roleDescribe
        String multipleKey = (String) map.remove("multiple_key");
        //multiple_value: 强
        String multipleValue = (String) map.remove("multiple_value");
        // 1=0
        StringBuilder stringBuilder = new StringBuilder("  1=0 ");
        if (multipleValue == null || "".equals(multipleValue)) {
            //在外面是这样 and ( 1=1 ) 占位置用的
            return " 1=1 ";
        }
        if (multipleKey != null) {
            //两个都不是null
            // and ( roleName like '%强%' or roleDescribe like '%强%' )
            String splitStr = "[$]";
            for (String key : multipleKey.split(splitStr)) {
                //判断是否是时间
                String key2 = key.replaceAll("XUDAZHU", ".");
                Class beanByField = MyBeanUtils.getBeanByField(tClass, key2.substring(key2.indexOf(".") + 1));
                if (beanByField == Date.class) {
                    stringBuilder.append(" or ").append(" date_format ( ").append(key).append(" , '%Y%m%d%H%i%s' )  like '%").append(multipleValue).append("%' ");
                } else {
                    stringBuilder.append(" or ").append(key).append(" like '%").append(multipleValue).append("%' ");
                }

            }
        }
        return stringBuilder.toString();
    }


    public Long count(String hql, String groupBy) {
        hql = addGroupBy(hql, groupBy);
        String countProp = "*";
        String selectC = "select";
        if (hql.contains(selectC)) {
            String select = hql.substring(hql.indexOf("select") + 6);
            countProp = select.substring(0, select.indexOf("from")).trim();
        }
        hql = "select count(" + countProp + ") " + hql.substring(hql.indexOf("from"));
        System.out.println("hql的count语句 => " + hql);
        TypedQuery<Long> query1 = entityManager.createQuery(hql, Long.class);
        String group = "group";
        //noinspection Duplicates
        if (hql.contains(group)) {
            return (long) query1.getResultList().size();
        } else {
            List<Long> resultList = query1.getResultList();
            if (resultList.size() > 0) {
                Collections.sort(resultList);
                return resultList.get(resultList.size() - 1);
            } else {
                return 0L;
            }
        }
    }

    public Long countBySelect(String hql, String groupBy) {
        hql = addGroupBy(hql, groupBy);
        hql = "select count(*) " + hql.substring(hql.indexOf("from"));
        System.out.println("hql的count语句 => " + hql);
        TypedQuery<Long> query1 = entityManager.createQuery(hql, Long.class);
        String group = "group";
        //noinspection Duplicates
        if (hql.contains(group)) {
            return (long) query1.getResultList().size();
        } else {
            List<Long> resultList = query1.getResultList();
            if (resultList.size() > 0) {
                Collections.sort(resultList);
                return resultList.get(resultList.size() - 1);
            } else {
                return 0L;
            }
        }
    }

    public String addGroupBy(String hql, String groupBy) {
        if (groupBy != null) {
            String order = "order";
            if (hql.contains(order)) {
                hql = hql.replace("order", "  group by " + groupBy + " order");
            } else {
                hql += ("  group by " + groupBy);
            }
        }
        return hql;
    }

    /**
     * 执行sql(包括分页)的方法
     *
     * @param tClass   class
     * @param hql      hql
     * @param pageable page
     * @param groupBy  property
     * @param <T>      t
     * @return page data
     */
    public <T> Page<T> exSql(Class<T> tClass, String hql, Pageable pageable, String groupBy) {
        hql = addGroupBy(hql, groupBy);
        System.out.println("hql的语句 => " + hql);
        TypedQuery<T> query = entityManager.createQuery(hql, tClass);
        List<EntityGraph<? super T>> entityGraphs = entityManager.getEntityGraphs(tClass);
        if (entityGraphs != null && entityGraphs.size() > 0) {
            query.setHint("javax.persistence.loadgraph", entityGraphs.get(0));
        }
        //设置分页
        query.setMaxResults(pageable.getPageSize());
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        List<T> resultList = query.getResultList();
        return new PageImpl<>(resultList, pageable, count(hql, null));
    }


    public Page<Map<String, Object>> exHqlResultMap(Class tClass, String resultHql, String hql, Pageable pageable, String groupBy) {
        hql = addGroupBy(hql, groupBy);
        System.out.println("hql的语句 => " + resultHql + hql);
        Query query = entityManager.createQuery(resultHql + hql);
        //设置分页
        query.setMaxResults(pageable.getPageSize());
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        List<Map<String, Object>> resultList = query.getResultList();
        return new PageImpl<>(resultList, pageable, count(hql, null));
    }

    public  List<Map<String, Object>> exHqlResultMap(Class tClass,String hql) {
        System.out.println("hql的语句 => " + hql);
        Query query = entityManager.createQuery(hql);
        //设置分页
        List<Map<String, Object>> resultList = query.getResultList();
        return resultList;
    }

    @SuppressWarnings("unchecked")
    public Page exSqlBySelect(Class tClass, String hql, Pageable pageable, String groupBy) {
        hql = addGroupBy(hql, groupBy);
        System.out.println("hql的语句 => " + hql);
        Query query = entityManager.createQuery(hql);
        List<EntityGraph> entityGraphs = entityManager.getEntityGraphs(tClass);
        if (entityGraphs != null && entityGraphs.size() > 0) {
            query.setHint("javax.persistence.loadgraph", entityGraphs.get(0));
        }
        //设置分页
        query.setMaxResults(pageable.getPageSize());
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        List resultList = query.getResultList();
        return new PageImpl<>(resultList, pageable, countBySelect(hql, null));
    }

    public <T> Page<T> exSqlCustomJoin(Class<T> tClass, String hql, EntityGraphUtils.EntityGraphUtilsInfo[] entityGraphUtilsInfo, Pageable pageable, String groupBy) {
        hql = addGroupBy(hql, groupBy);
        System.out.println("hql的语句 => " + hql);
        TypedQuery<T> query = entityManager.createQuery(hql, tClass);
        query.setHint("javax.persistence.loadgraph",
                EntityGraphUtils.createGraphByClassAndEntityManager(tClass, entityManager, entityGraphUtilsInfo));
        //设置分页
        query.setMaxResults(pageable.getPageSize());
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        List<T> resultList = query.getResultList();
        return new PageImpl<>(resultList, pageable, count(hql, null));
    }

    /**
     * 执行sql(包括分页)的方法
     *
     * @param tClass  class
     * @param hql     hql
     * @param groupBy property
     * @param <T>     t
     * @return page data
     */
    public <T> Object exSqlCustom(Class<T> tClass, String hql, String groupBy) {
        hql = addGroupBy(hql, groupBy);
        System.out.println("hql的语句 => " + hql);
        Query query = entityManager.createQuery(hql);
        return query.getResultList();
    }
    /**
     * 执行sql(包括分页)的方法
     *
     * @param tClass  class
     * @param hql     hql
     * @param groupBy property
     * @param <T>     t
     * @return page data
     */
    public List  exSqlCustom( String hql) {
        System.out.println("hql的语句 => " + hql);
        Query query = entityManager.createQuery(hql);
        List resultList = query.getResultList();
        return resultList;
    }


    public <T> List getSingleProperties(Class<T> tClass, String property) {
        if (property == null) {
            return new ArrayList();
        }
        String simpleName = tClass.getSimpleName();
        String toLowerCase = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
        String hql = "select " + property + " from " + simpleName + " " + toLowerCase + "  group by " + property;
        System.out.println("执行的hql ==> " + hql);
        Query query = entityManager.createQuery(hql);
        return query.getResultList();
    }


//    private   <T> List<String> getCascadePropertiesNamesOuter(Class<T> tClass  ) throws Exception {
//        String simpleName = tClass.getSimpleName();
//        String propName = simpleName.substring(0 , 1).toLowerCase() + simpleName.substring(1);
//        return getCascadePropertiesNames(new ArrayList<>() , tClass , propName);
//    }


//        /**
//         * 拿到所有需要join fetch 的属性 (hql)
//         * @param joinFetchs joinFetchs
//         * @param aClass aClass
//         * @param propertiesName propertiesName
//         * @param <T> <T>
//         * @return 所有需要join fetch 的属性
//         * @throws Exception e
//         */
//    private   <T> List<String> getCascadePropertiesNames(List<String> joinFetchs , Class<T> aClass , String propertiesName ) throws Exception {
//        List<String> list = new ArrayList<>();
//        Method[] methods = aClass.getMethods();
//        for (Method method : methods) {
//            if ( method.getName().startsWith("get") && method.getName().endsWith("Bean") ) {
//                String name = method.getReturnType().getSimpleName();
//                list.add(  (char)(name.charAt(0) + 32 ) + name.substring(1));
//            }
//
//        }
//        return list;
//
//    }


}
