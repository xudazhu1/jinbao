package com.hnjbkc.jinbao.utils;

import javax.naming.Name;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * @author siliqiang
 * @date 2019/12/3
 */
public class Downloader {
    /**
     * 公共的下载方法
     *
     * @param fileName 前台传过来的文件名称
     * @param response 响应
     */
    public static void Downloader(String fileName, HttpServletResponse response) throws Exception {
        /**
         //         * 使用OutputStream流向客户端浏览器输出中文数据（文件下载）
         //         * 以上内容只能打开文件，却不能保存到相应的目录位置
         //         * 　文件下载功能是web开发中经常使用到的功能，使用HttpServletResponse对象就可以实现文件的下载
         //         * getOutputStream()：向客户端发送数据（字节流）
         //         * getWriter()：向客户端发送数据（字符流）
         //         */
        //1.获取要下载的文件的绝对路径
        String path = fileName;
        //截取到文件的名称
        fileName = fileName.substring(fileName.lastIndexOf("//")+2);
        //2.获取要下载的文件名
//        String format = fileName.substring(fileName.lastIndexOf("."));
//        String substringFileName = "";
//        int a =2 ;
//        for (int i = 0; i <= fileName.length(); i++) {
//            if (fileName.indexOf(".") == i) {
//                fileName = fileName.substring(i + 1, fileName.length());
//                 a++;
//            }
//        }

//        for (int i = 0; i < a; i++) {
//            substringFileName = fileName.substring(0, fileName.lastIndexOf("."));
//            fileName = substringFileName;
//        }
       // String name = substringFileName + format;
        String name = fileName;
       // String name ="1.儋州市各镇、农（林）场存量生活垃圾治理工程水文地质调查建设工程勘察合同.pdf";
        //3.设置content-disposition响应头控制浏览器以下载的形式打开文件
        //设置context-disposition响应头，控制浏览器以下载形式打开，这里注意文件字符集编码格式，设置utf-8，不然会出现乱码
        response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(name, "UTF-8"));
        //4.获取要下载的文件输入流
        //字符流输入流FileReader in = new FileReader(path);
        InputStream in = new FileInputStream(path);
        int len;
        //5.创建数据缓冲区
        //字符流缓冲区：char[] buffer = new char[1024];
        byte[] buffer = new byte[1024];
        //6.通过response对象获取OutputStream流
        //编写文件下载功能时推荐使用OutputStream流，避免使用PrintWriter流，
        //因为OutputStream流是字节流，可以处理任意类型的数据，
        //而PrintWriter流是字符流，只能处理字符数据，如果用字符流处理字节数据，会导致数据丢失
        ServletOutputStream out = response.getOutputStream();
        //7.将FileInputStream流写入到buffer缓冲区
        while (-1 != (len = in.read(buffer))) {
            //8.使用OutputStream将缓冲区的数据输出到客户端浏览器
            out.write(buffer, 0, len);
        }
        in.close();
    }

}
