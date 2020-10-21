package com.hnjbkc.jinbao.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author xudaz
 * @date 2018/2/29
 */
@SuppressWarnings("WeakerAccess")
@Component
public class MyBeanUtils {
    private static final SimpleDateFormat SDF_LONG = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    private static List<Class> beans = new ArrayList<>();


    @Autowired
    private void setBeans( EntityManagerFactory entityManagerFactory ) {
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        Set<EntityType<?>> entities = metamodel.getEntities();
        for (EntityType<?> entity : entities) {
            Class<?> bindableJavaType = entity.getBindableJavaType();
            beans.add(bindableJavaType);
        }

    }

//    public static <T> List<T> populateList(List<Map<String, Object>> mapList, Class<T> class1) {
//        List<T> tList = new ArrayList<>(mapList.size());
//        for (Map<String, Object> objectMap : mapList) {
//            T newInstance;
//            try {
//                newInstance = class1.newInstance();
//            } catch (Exception e) {
//                return null;
//            }
//            if (populate(newInstance, objectMap)) {
//                tList.add(newInstance);
//            }
//        }
//        return tList;
//    }


    /**
     * 公用生成编号的方法
     * @param bean  bean
     * @param prefix  前缀
     * @param fieldName  编号字段名
     * @param subFieldName  子编号字段名( null 为不传 )
     * @return 新编号 不带前缀
     */
    @SuppressWarnings("UnusedReturnValue")
    public static <T> T createNumber(T bean , String prefix , String fieldName , String subFieldName  ) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        //获取主键 如果没有主键 不予设置
        Object primaryKey = getPrimaryKey(bean);
        if ( primaryKey == null ) {
            return  bean;
        }
        //编号 前缀 019 11 02132
        //先获取年月 再获取主键拼接为编号
        String format = simpleDateFormat.format(new Date());
        String year = format.substring(2, 4);
        String month = format.substring(5);
        //获取主键 , 不够长不全
        StringBuilder  pri = new StringBuilder(  primaryKey + "" );
        for (int i = 5; i > pri.length() ; i-- ) {
            pri.insert(0 , "0");
        }
        String number = prefix + year + month + pri;

        try {
            Field fieldName1 = bean.getClass().getDeclaredField(fieldName );
            fieldName1.setAccessible( true );
            fieldName1.set( bean , number );
            //如果子编号存在 找到子编号的字段 赋值
            if ( subFieldName != null ) {
                //拿到集合
                String[] split = subFieldName.split("[.]");
                int containsLength = 2;
                if ( split.length != containsLength ) {
                    return  bean;
                }
                Field declaredField = bean.getClass().getDeclaredField(split[0]);
                declaredField.setAccessible( true );
                //拿到级联的类类型
                Class beanByField = getBeanByField(bean.getClass(), split[0]);
                List collection = (List) declaredField.get(bean);
                for (int i = 0; i < collection.size(); i++) {
                    //设置子编号
                    Field declaredField1 = Objects.requireNonNull(beanByField).getDeclaredField(split[1]);
                    declaredField1.setAccessible( true );
                    int index = i + 1;
                    declaredField1.set(  collection.get( i ) , number + "-" + ( index < 10 ? "0" + index : index + "" )  );
                }

            }

        } catch (NoSuchFieldException e) {
            System.out.println("所传入的编号字段不对 ");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.out.println("编号赋值异常");
            e.printStackTrace();
        } catch ( Exception e ) {
            System.out.println("编号赋值异常 ");
            e.printStackTrace();
        }

        return bean;
    }


    //传入参数map 和要set的bean

    /**
     * 此方法可以把一个map集合 封装到所传入的对象里
     *
     * @param object 被封装的对象
     * @param map    待封装的数据
     */
    @SuppressWarnings("unused")
    public static void populate(Object object, Map<String, Object> map) {
        //获取传入对象的类对象
        Class<?> class1 = object.getClass();
        //遍历map
        loop :
            for (Entry<String, Object> stringObjectEntry : map.entrySet()) {
                String key = stringObjectEntry.getKey();
                Object value = stringObjectEntry.getValue();
                if ( key.contains(".") ) {
                    String[] split = key.split("[.]");
                    Object oldObj = object;
                    for (String s : split) {
                        Field declaredField ;
                        try {
                            declaredField = oldObj.getClass().getDeclaredField(s);
                        } catch (NoSuchFieldException e) {
                            continue loop;
                        }
                        if ( declaredField != null ) {
                            declaredField.setAccessible(true);
                            setField(declaredField , oldObj , value );
                            try {
                                oldObj = declaredField.get(oldObj);
                            } catch (IllegalAccessException e) {
                                System.out.println("封装bean有点小异常 不过不要紧");
                            }
                        }
                    }
                } else {
                    Field declaredField ;
                    try {
                        declaredField = class1.getDeclaredField(key);
                    } catch (NoSuchFieldException e) {
                        continue;
                    }
                    if ( declaredField != null ) {
                        setField(declaredField , object , value );
                    }
                }
            }
    }

    /**
     * 给字段赋值的函数
     *
     * @param field 字段对象
     * @param object 调用这个方法的主体对象
     * @param arg    执行的方法所传入的参数
     */
    private static void setField(Field field, Object object, Object arg)  {
        if ( field != null ) {
            field.setAccessible(true);
        } else {
            return;
        }
        try {
            Class<?> parameterType = field.getType();
            String type = parameterType.getName();
//            //判断级联bean
//            String myProjectPack = "com.hnjbkc.jinbao";
            if ( beans.contains( parameterType ) ) {
//            if (type.contains(myProjectPack)) {
                Object newInstance = field.get(object);
                if ( newInstance == null ) {
                    newInstance = parameterType.newInstance();
                }
                field.set(object , newInstance );
            }
            if (String.class.getName().equals(type)) {
                field.set(object,  arg);
            } else if (Date.class.getName().equals(type)) {
                String s = arg.toString();
                String dateTemp = "2019-09.10";
                if ( s.length() > dateTemp.length() ) {
                    field.set(object, SDF_LONG.parse( s ));
                } else {
                    field.set(object, SDF.parse( s ));
                }
            } else if (Integer.class.getName().equals(type)) {
                field.set(object, Integer.parseInt(arg.toString()));
            } else if (Long.class.getName().equals(type)) {
                field.set(object, Long.parseLong(arg.toString()));
            } else if (Float.class.getName().equals(type)) {
                field.set(object, Float.parseFloat(arg.toString()));
            } else if (Double.class.getName().equals(type)) {
                field.set(object, Double.parseDouble(arg.toString()));
            } else if (Boolean.class.getName().equals(type)) {
                field.set(object, Boolean.parseBoolean(arg.toString()));
            }
        } catch ( Exception e ) {
            System.out.println("bean封装异常");
        }
    }

    /**
     * 根据表明 获取跟表对应的javaBean的类对象 .class
     * @param table 表明
     * @return r
     */
    public static Class getBean4TableName(String table) {
        if ( table == null ) {
            return null;
        }
        // new build 直接从entityManagerFactory 拿取bean
        //遍历所有bean
        for (Class bean : beans) {
            Table tableObject = (Table) bean.getAnnotation(Table.class);
            if ( tableObject != null && table.equals( tableObject.name() ) ) {
                return bean;
            }
        }
        return null;



//            获取当前目录并且传入getBean4TableNameInner
//        try {
////            定义包明 转为目录 com/hnjbkc/jinbao
//            String packageDirName = "com.hnjbkc.jinbao".replace('.', '/');
//            //通过目录获取到当前java线程运行的所在源
//            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(
//                    packageDirName);
//            //通过源找到当前的目录 把头去掉 file:/
//            String s = resources.nextElement().toString();
//            //适配linux mac 的分隔符
//
//            String packagePath = s.replace("file://", "/").replace("file:/" , "/");
//            return getBean4TableNameInner( Collections.singletonList( new File(packagePath) ) , table);
//        } catch (IOException e) {
//            return null;
//        }
    }

    public static String getIdAttrNameByBean(Class bean) {
        if ( bean == null ) {
            return  "";
        }
        Field[] declaredFields = bean.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if ( declaredField.getAnnotation(Id.class) != null   ) {
                return declaredField.getName();
            }
        }
        return "";
    }

    /**
     * 根据表名和所在目录拿到和表对应的javaBean.class
     * @param dir 目录
     * @param table 表名
     * @return r
     */
    @SuppressWarnings({"Duplicates", "unused"})
    private static Class getBean4TableNameInner(List<File>  dir, String table) {
        if (null == dir ) {
            return null;
        }
        List<File> oldDirs = dir;
        while (  oldDirs.size() > 0 ) {
            List<File> dirs = new ArrayList<>();
            List<File> files = new ArrayList<>();
            //分类文件和文件夹
            for (File file : oldDirs) {
                File[] files1 = file.listFiles();
                if ( files1 == null ) {
                    continue;
                }
                for (File listFile : files1) {
                    if ( listFile.isFile() ) {
                        files.add(listFile);
                    } else {
                        dirs.add(listFile);
                    }
                }
            }
            for (File listFile : files) {
                if (  listFile.getAbsolutePath().endsWith("Bean.class")) {
                    //拿到类Class 然后判断 @Table注解的name值是否符合\
                    String absolutePath = listFile.getAbsolutePath();
                    //适配linux mac 的分隔符
                    int indexOf = absolutePath.lastIndexOf("com\\");
                    int indexOf2 = absolutePath.lastIndexOf("com/");
                    int index = Math.max(indexOf , indexOf2);
                    String classPath = absolutePath.substring(index).replaceAll("[\\\\]", ".").replaceAll("[/]" , ".").replace(".class", "");
                    Class<?> aClass = null;
                    try {
                        aClass = Class.forName(classPath);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    //获得表名
                    assert aClass != null;
                    Table tableObject = aClass.getAnnotation(Table.class);
                    if ( tableObject == null ) {
                        continue;
                    }
                    if (table.equals(tableObject.name())) {
                        return aClass;
                    }

                }
            }
            //如果本层没有找到 对下层文件夹集合开始下个循环
            oldDirs = dirs;
        }
        return null;
    }


    @SuppressWarnings("unused")
    public static Boolean objectIsEmpty(Object object) {
        if ( object == null ) {
            return  true;
        }
        Field[] declaredFields = object.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            if ( "serialVersionUID".equals(declaredField.getName() ) ) {
                continue;
            }
            //noinspection CatchMayIgnoreException
            try {
                Class<?> type = declaredField.getType();
                if (  type == List.class || type == Set.class ) {
                    Collection collection = (Collection) declaredField.get(object);
                    if ( collection == null || collection.size() == 0 ) {
                        continue;
                    }
                }
                Object o = declaredField.get(object);
                if ( declaredField.get(object)  != null ) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    /**
     * 判断javaBean 的主键是否为null 一般在调用hibernate的update方法时使用
     * @param o javaBean Entity
     * @return bool
     */
    public  static Boolean idNotNull(Object o ) {
        try {
            Class<?> aClass = o.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                if ( declaredField.getAnnotation(Id.class) != null && declaredField.get(o) != null ) {
                    return true;
                }
            }
            return false;
        } catch ( Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取Entity的主键的值 如果每页 返回null
     * @param o javaBean Entity
     * @return value
     */
    public static  Object  getPrimaryKey(Object o ) {
        try {
            Class<?> aClass = o.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                if ( declaredField.getAnnotation(Id.class) != null && declaredField.get(o) != null ) {
                    return declaredField.get(o);
                }
            }
            return null;
        } catch ( Exception e) {
            return null;
        }
    }

    /**
     * 返回类的简称
     * @param bean Class
     * @return 简称
     */
    public static String getToLowerNameByBean(Class bean ) {
        String nameTemp = bean.getSimpleName();
        return nameTemp.substring(0 , 1).toLowerCase() + nameTemp.substring( 1 );
    }


    /**
     * 根据class new一个全空对象(使用空参构造 如果没有空参构造 返回null )
     * @param oClass class
     * @return newInstance
     */
    @SuppressWarnings("unused")
    public  static Object newInstance(Class oClass) {
        Object o = null;
        try {
            o = oClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Field[] declaredFields = Objects.requireNonNull(o).getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if ( declaredField.getType() == List.class ) {
                declaredField.setAccessible(true);
                try {
                    declaredField.set(o , new ArrayList<>() );
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if ( declaredField.getType() == Set.class ) {
                declaredField.setAccessible(true);
                try {
                    declaredField.set(o , new HashSet<>() );
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return o;
    }

    public static Field getFieldByAnnotationType(Class bean , Class annotation ) {
        Field[] declaredFields = bean.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            //noinspection unchecked
            if ( declaredField.getAnnotation(annotation) != null ) {
                return declaredField;
            }

        }
        return null;
    }


    /**
     * 根据主体bean 和属性名 查出该属性的class类型
     * 如 :
     *      传入SonBean.class 和 "fatherBean.gFatherBean"  返货 GrandFatherBean.class
     * @param bean 主体class
     * @param field 字段名
     * @return 返回class
     */
    public static Class getBeanByField(Class bean, String field) {
        try {
            String temp = field.split("[.]")[0];
            String contains = ".";
            if ( field.contains(contains) ) {
                return getBeanByField(getBeanByField(bean , temp ) ,  field.substring( field.indexOf(".") + 1 ));
            } else {
                Field declaredField = bean.getDeclaredField(field);
                Class<?> type = declaredField.getType();
                if ( type == List.class || type == Set.class ) {
                    // 获得field的泛型类型
                    Type gType = declaredField.getGenericType();
                    // 就把它转换成ParameterizedType对象
                    ParameterizedType pType = (ParameterizedType)gType;
                    // 获得泛型类型的泛型参数（实际类型参数)
                    Type[] tArgs = pType.getActualTypeArguments();
                    return  tArgs == null ? null : Class.forName(tArgs[0].getTypeName());
                } else  {
                    return Class.forName( type.getName() );
                }
            }
        } catch (NoSuchFieldException e) {
            return null;
        } catch ( Exception e ) {
            e.printStackTrace();
            return null;
        }

    }

    public static Boolean isPrimaryKey(Class bean, String field) {
        if ( field == null || bean == null) {
            return false;
        }
        String contains1 = "$S." , contains2 = "$D.";
        if ( field.startsWith(contains1) || field.startsWith(contains2) ) {
            field = field.substring(3);
        }
        Class beanTemp = bean;
        String contains = ".";
        if ( field.contains( contains ) ) {
            beanTemp = getBeanByField( bean , field.substring( 0 , field.lastIndexOf("." ) ) );
            if ( beanTemp == null ) {
                return false;
            }
            field = field.substring( field.lastIndexOf(".") + 1 );
        }
        try {
            Field declaredField = beanTemp.getDeclaredField(field);
            if ( declaredField == null ) {
                return false;
            }
            return declaredField.getAnnotation(Id.class) != null;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 为javaBean 去除全部属性为null 的级联对象
     * @param object obj
     */
    public static void removeNullField( Object object ) throws IllegalAccessException {
        if ( object == null ) {
            return;
        }
        Class<?> aClass = object.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            Class<?> type = declaredField.getType();
            Object o = declaredField.get(object);
            Class beanByField = getBeanByField(object.getClass(), declaredField.getName());
            assert beanByField != null;
            if ( beans.contains( beanByField  ) ) {
                if ( objectIsEmpty(o) ) {
                    declaredField.set( object ,  null);
                } else if ( type == List.class || type == Set.class ) {
                    Collection o1 = (Collection) o;
                    for (Object o2 : o1) {
                        if ( beans.contains(  o2.getClass() ) ) {
                            removeNullField(o2);
                        }
                    }
                }
            }



        }

    }



}
