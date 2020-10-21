package com.hnjbkc.jinbao.permission.read;//package com.jbkc.financialsystem.read;


import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.permission.PermissionBean;
import com.hnjbkc.jinbao.permission.PermissionDao;
import com.hnjbkc.jinbao.utils.tableutils.TableUtilsDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author 十二
 */
@Component
public class ReadHtml implements  ServletContextListener {
    private static int count = 0;
    private String staticPath = null;

    private PermissionDao permissionDao;

    @Autowired
    public void setPermissionDao(PermissionDao permissionDao) {
        this.permissionDao = permissionDao;
    }

    private TableUtilsDao tableUtilsDao;

    @Autowired
    public void setTableUtilsDao(TableUtilsDao tableUtilsDao) {
        this.tableUtilsDao = tableUtilsDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    private int readFile(String filepath,List<PermissionBean> resPermission){
        File file = new File(filepath);
        //如果传入路径不存在则 退出
        if(!file.exists()){
            return -1;
        }
        //如果路径是文件夹
        if(file.isDirectory()){
            String[] list = file.list();
            assert list != null;
            //遍历所有 子目录或文件夹

            for (String path : list) {
                File sublime = new File(filepath+"//"+path);
                //如果是文件 判断正确 则 Insert操作
                if(!sublime.isDirectory()){
                    if(path.length()>4&&"html".equals(path.substring(path.length()-4))){
                        readHtml(sublime,resPermission);
                        //统计 一共多少 html
                        count++;
                    }
                    //如果是 文件夹 则递归
                }else if(sublime.isDirectory()){
                    //重新传入 绝对路径
                    readFile(sublime.getAbsolutePath(),resPermission);
                }
            }
        }
        return count;
    }

    private void readHtml(File file,List<PermissionBean> resPermission){
        try {
            //通过 jsoup API 类似jquery 选择器 获取标签属性
            Document document = Jsoup.parse(file, "UTF-8", "http://example.com/");
            Element first = document.select("meta#permission-tag").first();
            //如果获取的标签不为空
            if(first != null){
                PermissionBean permissionBean = new PermissionBean();
                //获取根路径 截取最后目录
                String subPath = staticPath.substring(staticPath.lastIndexOf("\\")).substring(1)+"\\";
                //获取 权限表 html 相对路径  截取最后目录 剩下文件的相对路径
                String rightPath = file.getPath().substring(file.getPath().indexOf(subPath)+subPath.length());
                rightPath = "/"+rightPath.replaceAll("[\\\\]", "/");
                //获取 文件名
                String fileName = rightPath.substring(rightPath.lastIndexOf("/"));
                //查询 文件名 Tag 是否 能查询出来

                //set 属性
                permissionBean.setPermissionTag(rightPath);
                permissionBean.setPermissionType("页面");
                permissionBean.setPermissionClass(first.attr("data-class"));
                permissionBean.setPermissionName(first.attr("data-name"));
                permissionBean.setPermissionRemark(first.attr("data-remark"));
                permissionBean.setPermissionDataPermission( "false" );
                //如果查询 出已存在的 数据 则 把 数据库查询出来的 Permission 的 ID set进 new出来的Permission
                for (PermissionBean bean : resPermission) {
                    if(bean.getPermissionTag().contains(fileName)){
                        permissionBean.setPermissionId(bean.getPermissionId());
                        if ( bean.getPermissionDataPermission() != null && ! "".equals( bean.getPermissionDataPermission() ) ) {
                            permissionBean.setPermissionDataPermission( bean.getPermissionDataPermission() );
                        }
                    }
                }
                // 保存或者更新
                tableUtilsDao.update(permissionBean);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        try {
            //获取项目路径classpath:下的static 的所有html文件路径字符串
            File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX+"static");
            staticPath = file.getPath();
            //读取所有符合规范html insert进数据库
            List<PermissionBean> resPermission = (List<PermissionBean>)sqlUtilsDao.exSqlCustom(PermissionBean.class,"from PermissionBean",null);
            System.out.println("There are "+readFile(staticPath,resPermission)+" html files");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onStartup(ServletContext servletContext) throws ServletException {
//        try {
//            //获取项目路径classpath:下的static 的所有html文件路径字符串
//            File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX+"static");
//            path = file.getPath();
//            //读取所有符合规范html insert进数据库
//            System.out.println("There are "+readFile(path)+" html files");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
