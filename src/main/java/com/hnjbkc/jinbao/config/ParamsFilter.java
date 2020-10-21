package com.hnjbkc.jinbao.config;

import com.hnjbkc.jinbao.permission.permissiondata.DataPermissionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 替换提交的空字符串并且处理数据权限的过滤器
 * @author xudaz
 */
@Component
public class ParamsFilter implements Filter {

    private DataPermissionFilter dataPermissionFilter;

    @Autowired
    public void setDataPermissionFilter(DataPermissionFilter dataPermissionFilter) {
        this.dataPermissionFilter = dataPermissionFilter;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ParameterRequestWrapper paramsRequest = new ParameterRequestWrapper(  (HttpServletRequest) request);
        dataPermissionFilter.doDataPermission(paramsRequest  , (HttpServletResponse) response);
        chain.doFilter(paramsRequest, response);
    }

    @Override
    public void init(FilterConfig arg0)   {

    }

    @Override
    public void destroy() {

    }

}