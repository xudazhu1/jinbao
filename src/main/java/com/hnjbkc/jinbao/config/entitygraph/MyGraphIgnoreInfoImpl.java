package com.hnjbkc.jinbao.config.entitygraph;

import lombok.Data;

import java.lang.annotation.Annotation;

/**
 * @author xudaz
 */
@SuppressWarnings("ClassExplicitlyAnnotation")
@Data
public class MyGraphIgnoreInfoImpl implements MyGraphIgnoreInfo {

    public MyGraphIgnoreInfoImpl() {
    }

    public MyGraphIgnoreInfoImpl(MyGraphIgnoreInfoType fetchType) {
        this.fetchType = fetchType;
    }

    private String fieldPath;

    private MyGraphIgnoreInfoType fetchType = MyGraphIgnoreInfoType.WHITE_LIST;

    private  String [] fieldList = new String[0];

    @Override
    public String fieldPath() {
        return fieldPath;
    }

    /**
     * 实体图排除的类型
     *
     * @return b
     */
    @Override
    public MyGraphIgnoreInfoType fetchType() {
        return fetchType;
    }

    @Override
    public String[] fieldList() {
        return new String[0];
    }

    /**
     * Returns the annotation type of this annotation.
     *
     * @return the annotation type of this annotation
     */
    @Override
    public Class<? extends Annotation> annotationType() {
        return MyGraphIgnoreInfo.class;
    }
}
