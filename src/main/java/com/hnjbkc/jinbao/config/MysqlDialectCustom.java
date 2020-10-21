package com.hnjbkc.jinbao.config;

import org.hibernate.dialect.InnoDBStorageEngine;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.MySQLStorageEngine;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Component;

/**
 * 改变hibernate自动创建的表的引擎为innodb
 * @author xudaz
 */
@Component
public class MysqlDialectCustom extends MySQL5Dialect {

    @Override
    protected MySQLStorageEngine getDefaultMySQLStorageEngine() {
        return InnoDBStorageEngine.INSTANCE;
    }

    public MysqlDialectCustom() {
        super();
        registerFunction("group_concat", new StandardSQLFunction("group_concat", StringType.INSTANCE));
        registerFunction("concat", new StandardSQLFunction("concat", StringType.INSTANCE));
        registerFunction("group_concat_distinct",new SQLFunctionTemplate(StandardBasicTypes.STRING,"group_concat( DISTINCT CONCAT( ?1 , '&A&' ,  ?2 )  , '$' )"));
    }
}
