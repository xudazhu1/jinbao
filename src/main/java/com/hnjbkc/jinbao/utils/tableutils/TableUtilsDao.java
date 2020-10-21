package com.hnjbkc.jinbao.utils.tableutils;

import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.utils.AttrExchange;
import com.hnjbkc.jinbao.utils.MyBeanUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.format.WebConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author xudaz
 */
@SuppressWarnings("WeakerAccess")
@Component
public class TableUtilsDao {

    /**
     * 注入实体管理器,执行持久化操作
     */
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @SuppressWarnings({"unchecked", "DuplicatedCode"})
    public Page search4Table(Class bean , Map<String , Object> map , Pageable pageable1  , String ... custom ) {
        String name = bean.getSimpleName();
        String toLowerName = name.substring(0 , 1).toLowerCase() + name.substring(1);
        String idAttrNameByBean = MyBeanUtils.getIdAttrNameByBean(bean);
        //生成pageable
        //生成select主体
        String fields = (String) map.remove("table_utils.fields");
        String startWith = createStartWithByMap( fields , bean );
        //生成中间体
        String center = createCenterByField(bean , fields , map );
        //生成条件
        Map<String , Object> newMap = new HashMap<>(map.size());
        //开始符 与 处理multiple_key的分隔符
        String startWithTemp = "$";
        //处理multiple_key & multiple_value
        String multipleKey = (String) map.remove("multiple_key");
        String multipleValue = (String) map.remove("multiple_value");
        if ( multipleValue != null && ! "".equals( multipleValue )) {
            StringBuilder stringBuilder = new StringBuilder();
            String[] split = multipleKey.split("[$]");
            for (String s : split) {
                stringBuilder.append("$").append( fixCondition( bean , s ) );
            }
            newMap.put( "multiple_key" , stringBuilder.substring(1) ) ;
            newMap.put( "multiple_value" , multipleValue ) ;
        }

        //noinspection Duplicates
        map.forEach( (key , value ) -> {
            if ( key.startsWith(startWithTemp) ) {
                String temp = key.substring(3);
                newMap.put( key.substring(0 , 3) +fixCondition(bean , temp) , value);
            } else {
                newMap.put(fixCondition(bean , key) , value);
            }
        });


        StringBuilder replace = new StringBuilder(sqlUtilsDao.joiningHQLBySearch(bean, newMap, pageable1 ,custom));
        replace = new StringBuilder(" " + replace.substring(replace.indexOf("where ( 1 = 1 ) ")));

        String hql = "select " + startWith + " from " + name + " as " + toLowerName + center + replace.toString()  ;
        hql = sqlUtilsDao.addGroupBy(hql , toLowerName + "." + idAttrNameByBean );
        System.out.println("HQL=====> " + hql);
        Query query = entityManager.createQuery(hql);
        //设置分页
        query.setMaxResults(pageable1.getPageSize());
        query.setFirstResult(pageable1.getPageNumber() * pageable1.getPageSize());
        return new PageImpl( query.getResultList() , pageable1,
                count(bean ,  " from " + name + " as " + toLowerName + center + replace )
        );
    }

    @SuppressWarnings("DuplicatedCode")
    public List searchNoCount(Class bean , Map<String , Object> map , Pageable pageable1  , String ... custom ) {
        String name = bean.getSimpleName();
        String toLowerName = name.substring(0 , 1).toLowerCase() + name.substring(1);
        String idAttrNameByBean = MyBeanUtils.getIdAttrNameByBean(bean);
        //生成pageable
        //生成select主体
        String fields = (String) map.remove("table_utils.fields");
        String startWith = createStartWithByMap( fields , bean );
        //生成中间体
        String center = createCenterByField(bean , fields , map );
        //生成条件
        Map<String , Object> newMap = new HashMap<>(map.size());
        String startWithTemp = "$";
        //noinspection Duplicates
        map.forEach( (key , value ) -> {
            if ( key.startsWith(startWithTemp) ) {
                String temp = key.substring(3);
                newMap.put( key.substring(0 , 3) +fixCondition(bean , temp) , value);
            } else {
                newMap.put(fixCondition(bean , key) , value);
            }
        });

        StringBuilder replace = new StringBuilder(sqlUtilsDao.joiningHQLBySearch(bean, newMap, pageable1 ,custom));
        replace = new StringBuilder(" " + replace.substring(replace.indexOf("where ( 1 = 1 ) ")));

        String hql = "select " + startWith + " from " + name + " as " + toLowerName + center + replace.toString()  ;
        hql = sqlUtilsDao.addGroupBy(hql , toLowerName + "." + idAttrNameByBean );
        System.out.println("HQL=====> " + hql);
        Query query = entityManager.createQuery(hql);
        //设置分页
        query.setMaxResults(pageable1.getPageSize());
        query.setFirstResult(pageable1.getPageNumber() * pageable1.getPageSize());
        return query.getResultList();
    }


    private String fixCondition(Class bean , String name ) {
        String contains = ".";
        if ( name.contains(contains) ) {
            name = MyBeanUtils.getToLowerNameByBean(bean) +"." + name;
            String xudazhu = name.substring(0, name.lastIndexOf(".")).replaceAll("[.]", "XUDAZHU");
            return xudazhu + name.substring(name.lastIndexOf("."));
        } else {
            return MyBeanUtils.getToLowerNameByBean(bean) + "." + name ;
        }

    }

    @SuppressWarnings("DuplicatedCode")
    private Long count(Class bean , String hql ){
        hql = " select count( distinct " + MyBeanUtils.getToLowerNameByBean(bean) +
                "." + MyBeanUtils.getIdAttrNameByBean(bean) + " )" + hql;
        System.out.println("HQL COUNT  =====> " + hql);
        TypedQuery<Long> query1 = entityManager.createQuery(hql , Long.class);
        String group = "group";
        //noinspection Duplicates
        if ( hql.contains(group) ) {
            return (long) query1.getResultList().size();
        } else {
            List<Long> resultList = query1.getResultList();
            if ( resultList.size() > 0 ) {
                Collections.sort(resultList);
                return  resultList.get(resultList.size() -1 );
            } else {
                return 0L;
            }
        }

    }

    private String createCenterByField(Class bean, String fields , Map<String , Object> map ) {
        String[] splitField = fields.split("[$]");
        List<String> split = new ArrayList<>(Arrays.asList(splitField));
        for (String s : map.keySet()) {
            if ( map.get(s) != null && ! "".equals(map.get(s) ) ) {
                if ( s.startsWith("$") ) {
                    split.add(s.substring(3));
                } else {
                    split.add(s);
                }
            }
        }
        //生成新的 去包含的name前缀
        List<String> nameNew = new ArrayList<>();
        loop :
        for (String s : split) {
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
        StringBuilder stringBuilder = new StringBuilder();

        List<String> newNameList = new ArrayList<>();

        for (String nameTemp : nameNew) {
            String[] split1 = nameTemp.split("[.]");
            Class oldBean = bean;
            StringBuilder oldBeanName = new StringBuilder(MyBeanUtils.getToLowerNameByBean(bean));
            for (String aSplit1 : split1) {
                //拿到此次新bean
                String oldName = oldBeanName.toString();
                String newName = oldBeanName.append("XUDAZHU").append(aSplit1).toString();
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


    private String createStartWithByMap(String fields , Class bean ) {
        String name = bean.getSimpleName();
        name = name.substring(0 , 1).toLowerCase() + name.substring(1);
        List<String> fieldSet = new ArrayList<>();
        String[] split = fields.split("[$]");
        for (String s : split) {
            //noinspection RegExpRedundantEscape
            String oldField = s.replaceAll("\\[n\\]" , "");
            s = name + "." + s;
            if ( s.contains("[") ) {
                //noinspection RegExpRedundantEscape
                s = s.replaceAll("\\[n\\]" , "");
                //取别名
                String condition4Select = createCondition4Select(s);
                //如果是多对多
                if ( hasManyToMany( oldField , bean ) ) {
                    //加入拼接id 防止重复项被去重
                    String left = s.substring(0 , s.lastIndexOf("."));
                    Class endBean = MyBeanUtils.getBeanByField(bean , left.substring(left.indexOf(".") + 1));
                    String condition4Select1 = createCondition4Select(left + "." + MyBeanUtils.getIdAttrNameByBean(endBean));
                    assert endBean != null;
                    fieldSet.add( " group_concat_distinct(  " + condition4Select1  + " , "+ condition4Select +" , '$' ) " );
                } else {
                    fieldSet.add( " group_concat( CASE WHEN "+condition4Select+" IS NULL THEN '' ELSE "+condition4Select+" END ,   '$' ) " );
                }
            } else {
                fieldSet.add(createCondition4Select(s) );
            }
        }
        return fieldSet.toString().substring( 1 , fieldSet.toString().length() -1 ).replaceAll("[,]" , " , ");
    }
    private String createStartWithByMap4MultipleProperties(String fields , Class bean ) {
        String name = bean.getSimpleName();
        name = name.substring(0 , 1).toLowerCase() + name.substring(1);
        List<String> fieldSet = new ArrayList<>();
        String[] split = fields.split("[$]");
        for (String s : split) {
            //noinspection
            s = name + "." + s;
            if ( s.contains("[") ) {
                //noinspection RegExpRedundantEscape
                s = s.replaceAll("\\[n\\]" , "");
                //取别名
                String condition4Select = createCondition4Select(s);
                fieldSet.add(   " group_concat_distinct(  " + condition4Select  + " , '$' ) "  );
            } else {
                fieldSet.add( "group_concat_distinct(  " + createCondition4Select(s) + " , '$' ) "  );
            }
        }
        return fieldSet.toString().substring( 1 , fieldSet.toString().length() -1 ).replaceAll("[,]" , " , ");
    }

    private boolean hasManyToMany(String fields , Class bean) {
        if ( fields == null ) {
            return false;
        }
        boolean has = false;
        String[] split = fields.split("[.]");
        Class oldBean = bean;
        for (String s : split) {
            //noinspection RegExpRedundantEscape
            s = s.replaceAll("\\[n\\]" , "");
            try {
                assert oldBean != null;
                Field declaredField = oldBean.getDeclaredField(s);
                if ( declaredField == null ) {
                    break;
                }
                declaredField.setAccessible(true);
                if ( declaredField.getAnnotation(ManyToMany.class) != null ) {
                    has = true;
                    break;
                }
                oldBean = MyBeanUtils.getBeanByField(oldBean , s);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return has;
    }


    private String createCondition4Select( String name ) {
        String[] split = name.split("[.]");
        int length  = 3;
        if ( split.length < length ) {
            return name;
        }
        String substring = name.substring(0, name.lastIndexOf("."));
        String s = substring.replaceAll("[.]", "XUDAZHU");
        return s + name.substring(name.lastIndexOf("."));
    }

    /**
     * 添加或升级方法 ( 带数据交换 ) 数据依赖request请求
     * 数据依赖本次request请求
     * @see  AttrExchange
     * @param tableName 表名
     * @return r
     * @throws Exception 找不到bean的错误
     */
    @Modifying
    @Transactional(rollbackOn = Exception.class)
    public Object update( String tableName ) throws Exception {
        Class bean4TableName = MyBeanUtils.getBean4TableName(tableName);
        assert  bean4TableName != null;
        Object o = bean4TableName.newInstance();
        //调用springMVC的封装方法
                ModelAttributeMethodProcessor modelAttributeMethodProcessor = new ModelAttributeMethodProcessor(true);
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert servletRequestAttributes != null;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        NativeWebRequest webRequest = new ServletWebRequest(request);
        WebRequestDataBinder binder =  new WebRequestDataBinder(o , MyBeanUtils.getToLowerNameByBean(bean4TableName) );
        binder.setConversionService(new WebConversionService(null));

        Method bindRequestParameters = modelAttributeMethodProcessor.getClass().getDeclaredMethod("bindRequestParameters", WebDataBinder.class, NativeWebRequest.class);
        bindRequestParameters.setAccessible(true);
        bindRequestParameters.invoke( modelAttributeMethodProcessor , binder, webRequest );
        return update( o );
    }


    /**
     * 添加或升级方法 ( 带数据交换 )
     * @see  AttrExchange
     * @param o object
     * @return r
     */
    @Modifying
    @Transactional(rollbackOn = Exception.class)
    @SuppressWarnings("unchecked")
    public <T> T update( T o )  {
        try {
            MyBeanUtils.removeNullField( o );
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Session session = (Session) entityManager.getDelegate();
        if ( MyBeanUtils.idNotNull( o ) ) {
            try {
                Object primaryKey = MyBeanUtils.getPrimaryKey(o);
                String hqlByIn = "from " + o.getClass().getSimpleName() + " where " + MyBeanUtils.getIdAttrNameByBean( o.getClass() ) + "=" + primaryKey;
                TypedQuery<T> query = (TypedQuery<T>) add4Query(entityManager.createQuery(hqlByIn, o.getClass()), o.getClass());
                add4Query(query , o.getClass());
                T o1 = query.getSingleResult();
                AttrExchange.onAttrExchange( o1 , o);
                session.update( o1 );
                session.flush();
                return o1;
            } catch ( Exception e ) {
                e.printStackTrace();
                return null;
            }
        } else {
            AttrExchange.twoWayBinding(o);
            Serializable save = session.save(o);
            return o;
        }
    }


    @SuppressWarnings("unchecked")
    private <F extends  Query > F add4Query(F  query , Class type ) {
        List entityGraphs = entityManager.getEntityGraphs(type);
        if (entityGraphs != null && entityGraphs.size() > 0) {
            query.setHint("javax.persistence.loadgraph", entityGraphs.get(0));
            System.out.println( " setHint 'javax.persistence.loadgraph' ==> " + entityGraphs.get(0) );
        }
        return query;
    }

    @Transactional(rollbackOn = Exception.class)
    public Boolean delete(String tableName, Integer id) {
        return deleteByIds( tableName , id.toString() );
    }
    @Transactional(rollbackOn = Exception.class)
    public Boolean deleteByIds(String tableName, String ids) {
        Class bean4TableName = MyBeanUtils.getBean4TableName(tableName);
        if ( bean4TableName == null ) {
            return false;
        }
        @SuppressWarnings("JpaQlInspection")
        Query query = entityManager.createQuery(
                "delete from " + bean4TableName.getSimpleName() + " where id in ( " + ids + " )");
        int i = query.executeUpdate();
        return i > 0;
    }


    @SuppressWarnings("DuplicatedCode")
    public Object getMultipleProperties(Class bean, Map<String, Object> map , String ... custom) {
        String name = bean.getSimpleName();
        String toLowerName = name.substring(0 , 1).toLowerCase() + name.substring(1);
        //生成pageable
        //生成select主体
        String fields = (String) map.remove("table_utils.fields");
        String startWith = createStartWithByMap4MultipleProperties( fields , bean );
        //生成中间体
        String centerHQL = createCenterByField(bean , fields , map );

        //生成条件
        Map<String , Object> newMap = new HashMap<>(map.size());
        String startWithTemp = "$";
        //noinspection Duplicates
        map.forEach( (key , value ) -> {
            if ( key.startsWith(startWithTemp) ) {
                String temp = key.substring(3);
                newMap.put( key.substring(0 , 3) +fixCondition(bean , temp) , value);
            } else {
                newMap.put(fixCondition(bean , key) , value);
            }
        });
        StringBuilder replace = new StringBuilder(sqlUtilsDao.joiningHQLBySearch(bean, newMap, PageRequest.of(0 , Integer.MAX_VALUE ) , custom ));
        replace = new StringBuilder(" " + replace.substring(replace.indexOf("where ( 1 = 1 ) ")));
        String hql = "select " + startWith + " from " + name + " as " + toLowerName + centerHQL + replace.toString()  ;
        hql = sqlUtilsDao.addGroupBy(hql , "'1'" );
        System.out.println("HQL=====> " + hql);
        Query query = entityManager.createQuery(hql);
        @SuppressWarnings("unchecked")
        EntityGraph entityGraph = entityManager.createEntityGraph(bean);
        query.setHint("javax.persistence.loadgraph", entityGraph);
        return query.getResultList();
    }
}
