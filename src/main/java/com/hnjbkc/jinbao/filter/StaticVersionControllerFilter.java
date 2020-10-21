package com.hnjbkc.jinbao.filter;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author xudaz
 */
@Component
public class StaticVersionControllerFilter implements Filter {
    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        Object version = request.getServletContext().getAttribute("version");
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;

        String requestURL = req.getRequestURL().toString();
        String queryStr = req.getQueryString();

        // add timestamp to static resource, to avoid cache
        // static resource
        if(  (requestURL.endsWith(".js") || requestURL.endsWith(".css"))){
            String newURL = null;
            if(StringUtils.isNotBlank(queryStr) && (  requestURL.contains(".js?") || requestURL.contains(".css?") )   ){
                newURL = requestURL + "?" + queryStr + "&my_v=" + version ;
                resp.sendRedirect(newURL);
                return;
            }
            if(StringUtils.isBlank(queryStr)){
                newURL = requestURL + "?my_v=" + version;
                resp.sendRedirect(newURL);
                return;
            }

            try{
                chain.doFilter(request, response);
            }catch(Exception e){
                e.printStackTrace();
            }
            return;
        }

        chain.doFilter(req, resp);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        //初始化版本号
        config.getServletContext().setAttribute("version" , System.currentTimeMillis() );
    }

}
