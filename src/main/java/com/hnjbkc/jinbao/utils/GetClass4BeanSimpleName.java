package com.hnjbkc.jinbao.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 根据类名反射获取类实例的工具类
 * @author xudaz
 * @date 2019/4/18
 */
public class GetClass4BeanSimpleName {


    public static Class getClass4BeanSimpleName(String simpleName) {
        String packageDirName = "com.hnjbkc.jinbao".replace('.', '/');
        Enumeration<URL> resources = null;
        try {
            resources = Thread.currentThread().getContextClassLoader().getResources(
                    packageDirName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<File> dirs = new ArrayList<>();
        while ( resources!= null && resources.hasMoreElements()) {
            URL url = resources.nextElement();
            String packagePath = url.toString().replace("file:/" , "");
            System.out.println(packagePath);
            dirs.add(new File(packagePath));
        }

        for (File dir : dirs) {
            String drawBean = getClassName4Dir(dir, simpleName);
            if ( drawBean != null ) {
                try {
                    return Class.forName(drawBean);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
        return  null;

    }



    private static String getClassName4Dir(File dir , String name ) {
        File[] files = dir.listFiles();
        assert files != null;
        for (File file : files) {
            if ( file.isFile() && file.getName().contains(name) ) {
                String absolutePath = file.getAbsolutePath();
                int i = absolutePath.lastIndexOf("com\\");
                return absolutePath.substring(i).replace("\\" , ".").replace(".class" , "");
            }
        }
        for (File file : files) {
            if ( file.isDirectory() ) {
                String className4Dir = getClassName4Dir(file, name);
                if ( className4Dir != null ) {
                    return  className4Dir;
                }
            }
        }
        return  null;

    }
}
