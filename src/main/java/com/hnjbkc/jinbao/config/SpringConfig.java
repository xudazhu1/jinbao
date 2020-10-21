package com.hnjbkc.jinbao.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * spring的一些配置
 * @author xudaz
 */
@Configuration
public class SpringConfig {

    /**
     * 去除请求map里value为空的键值对的过滤器
     * @return r
     */
    @Bean
    public FilterRegistrationBean filterProxy(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        DelegatingFilterProxy httpBasicFilter = new DelegatingFilterProxy();
        registrationBean.setFilter(httpBasicFilter);
        Map<String,String> m = new HashMap<>(2);
        m.put("targetBeanName","paramsFilter");
        m.put("targetFilterLifecycle","true");
        registrationBean.setInitParameters(m);
        List<String> urlPatterns = new ArrayList<>();
        urlPatterns.add("/*");
        registrationBean.setUrlPatterns(urlPatterns);
        registrationBean.setEnabled(false);
        return registrationBean;
    }
    /**
     * 去除请求map里value为空的键值对的过滤器
     * @return r
     */
    @Bean
    public FilterRegistrationBean staticVersionControllerFilterProxy(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        DelegatingFilterProxy httpBasicFilter = new DelegatingFilterProxy();
        registrationBean.setFilter(httpBasicFilter);
        Map<String,String> m = new HashMap<>(2);
        m.put("targetBeanName","staticVersionControllerFilter");
        m.put("targetFilterLifecycle","true");
        registrationBean.setInitParameters(m);
        List<String> urlPatterns = new ArrayList<>();
        urlPatterns.add("*.js");
        urlPatterns.add("*.css");
        registrationBean.setUrlPatterns(urlPatterns);
        registrationBean.setEnabled(false);
        return registrationBean;
    }
}
