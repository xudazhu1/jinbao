package com.hnjbkc.jinbao.utils;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultDefaultValueProcessor;
import net.sf.json.processors.DefaultValueProcessor;
import net.sf.json.processors.JsonValueProcessorMatcher;
import net.sf.json.util.CycleDetectionStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 格式化将要返回的数据
 * @author xudaz
 * @date 2019/3/18
 */
public class FormatJsonMap {
    public  static JsonConfig jsonConfig = new JsonConfig();
    static {
        //自动去除级联
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

        jsonConfig.setExcludes(new String[] { "handler", "hibernateLazyInitializer" });

        jsonConfig.registerDefaultValueProcessor(Double.class, new DefaultDefaultValueProcessor() {
            @Override
            public Object getDefaultValue(Class type) {
                return "";
            }
        });
        jsonConfig.registerDefaultValueProcessor(String.class, new DefaultDefaultValueProcessor() {
            @Override
            public Object getDefaultValue(Class type) {
                return "";
            }
        });
        jsonConfig.registerDefaultValueProcessor(Integer.class, new DefaultDefaultValueProcessor() {
            @Override
            public Object getDefaultValue(Class type) {
                return "";
            }
        });

        /*jsonConfig.registerJsonValueProcessor(Date.class, new JsonValueProcessor() {
            //自定义日期格式
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            *//**
             * 处理单个Date对象
             *//*
            @Override
            public Object processObjectValue(String propertyName, Object date,JsonConfig config) {
                return simpleDateFormat.format(date);
            }

            *//**
             * 处理数组中的Date对象
             *//*
            @Override
            public Object processArrayValue(Object date, JsonConfig config) {
                return simpleDateFormat.format(date);
            }
        });
        jsonConfig.registerJsonValueProcessor(Integer.class, new JsonValueProcessor() {
           *//* //自定义日期格式
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");*//*

            *//**
             * 处理单个integer对象
             *//*
            @Override
            public Object processObjectValue(String propertyName, Object date,JsonConfig config) {
                if ( date.getClass() == String.class ) {
                    return new Integer((String) date);
                }
                return date;
//                return simpleDateFormat.format(date);
            }

            *//**
             * 处理数组中的integer对象
             *//*
            @Override
            public Object processArrayValue(Object date, JsonConfig config) {
                if ( date.getClass() == String.class ) {
                    return new Integer((String) date);
                }
                return date;
            }
        });
        jsonConfig.registerJsonValueProcessor(Double.class, new JsonValueProcessor() {
           *//* //自定义日期格式
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");*//*

            *//**
             * 处理单个Double对象
             *//*
            @Override
            public Object processObjectValue(String propertyName, Object date,JsonConfig config) {
                if ( date.getClass() == String.class ) {
                    return new Double((String) date);
                }
                return date;
//                return simpleDateFormat.format(date);
            }
            *//**
             * 处理数组中的Double对象
             *//*
            @Override
            public Object processArrayValue(Object date, JsonConfig config) {
                if ( date.getClass() == String.class ) {
                    return new Double((String) date);
                }
                return date;
            }
        });*/

        //保留value为null的值
        jsonConfig.setJsonValueProcessorMatcher(new JsonValueProcessorMatcher() {
            @Override
            public Object getMatch(Class aClass, Set set) {
                return "";
            }
        });
        jsonConfig.registerDefaultValueProcessor( String.class,
                new DefaultValueProcessor() {
                    @Override
                    public Object getDefaultValue(Class type) {
                        return "";
                    }
                });
    }

    /**
     * 格式化将要返回的数据
     * @param pageMap 分页信息 如 pageNum(当前页) countPage(总页数) countNum(总数量) pageSize(单页数量)
     * @param statusCode 状态码
     * @param data 数据
     * @return 合成后的json对象
     */
    public static JSONObject format(Map<String , Object> pageMap , Integer statusCode , Object data ) {
        Map<String , Object> jsonObject = new HashMap<>(4);
        jsonObject.put("pageData" , pageMap) ;
        jsonObject.put("statusCode" , statusCode);
        jsonObject.put("data" , data);
        return JSONObject.fromObject(jsonObject , jsonConfig);
    }


}
