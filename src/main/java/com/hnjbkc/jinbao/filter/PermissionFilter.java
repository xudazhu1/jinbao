package com.hnjbkc.jinbao.filter;

import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.permission.PermissionBean;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author siliqiang
 * @date 2019.8.5
 * 过滤器
 */
@WebFilter(filterName = "permission", urlPatterns = "/aac")
public class PermissionFilter implements Filter {
    private List<String> accessibleHtml;

    @Override
    public void init(FilterConfig filterConfig) {
        //创建一个白名单的集合
        accessibleHtml = new ArrayList<>();
        //拿到白名单的文件路径
        File file = new File(filterConfig.getServletContext().getRealPath("WEB-INF/classes"), "whitelist");
        try {
            //创建一个输入流
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str;
            //将白名单的权限放到集合中
            List<String> whileList = new ArrayList<>();
            //循环读取文件(单行读取)
            while ((str = bufferedReader.readLine()) != null) {
                System.out.println(str + "白名单");
                whileList.add(str);
            }
            //遍历所有的白名单的html
            accessibleHtml.addAll(whileList);
            //关流
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        StringBuffer url = request.getRequestURL();
        //判断url中是不是以.html结尾的如果不是则直接放行
        String isHtml = ".html";
        if (!url.toString().contains(isHtml)) {
            filterChain.doFilter(request, response);
            return;
        }
        //拿到URI
        String requestURI = request.getRequestURI();
        System.out.println(requestURI + "这个是URI");
        String[] pathArray = requestURI.split("/");
        String file = pathArray[pathArray.length - 1];
        System.out.println("file" + file);
        //如果白名单中包含的html则直接放行
        if (accessibleHtml.contains(file)) {
            filterChain.doFilter(request, response);
            return;
        }
        //拿到session
        HttpSession session = request.getSession();

        //判断session中否有用户信息,没有的话则是没有登录.直接重定向到登录页面
        String user = "user";

        if (session.getAttribute(user) != null) {

            //拿到当前登录用户session里面的权限
            Set permissionBeans = (Set) session.getAttribute("permissionBeans");
            UserBean user1 = (UserBean) session.getAttribute("user");
            String name = "司利强";
            if (user1.getUserName().equals(name)) {
                filterChain.doFilter(request, response);
                return;
            }
            //遍历权限
            for (Object o : permissionBeans) {
                PermissionBean permissionBean = (PermissionBean) o;
                //如果登录的用户有这个权限则直接放行没有的话直接拦截
                if (permissionBean.getPermissionTag().equals(requestURI)) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        } else {
            //重定向到登录页面
            String contextPath = request.getContextPath() + "/pages/login/login.html";
            response.sendRedirect(contextPath);
            return;
        }

        System.out.println("拦截");
        String contextPath1 = request.getContextPath() + "/pages/set/permission/no-permission.html";
        response.sendRedirect(contextPath1);

    }

    @Override
    public void destroy() {

    }
}
