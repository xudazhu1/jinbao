package com.hnjbkc.jinbao.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QuickCreateJavaFile {

    public static void main(String[] args) {
        try {
            create("D:\\jinbao4.0\\src\\main\\java\\com\\hnjbkc\\jinbao\\aa", "si");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void create(String path, String authName) throws IOException {
        File file = new File(path);
        if (!file.isDirectory()){
            throw new RuntimeException("The path cannot be a file");
        }
        if(!file.exists()){
            file.mkdirs();
        }
        String folderName = path.substring((path.lastIndexOf("\\")) + 1);
        String upperName = folderName.substring(0,1).toUpperCase() + folderName.substring(1);
        String beanName = upperName+"Bean";
        String daoName = upperName+"Dao";
        String serviceName = upperName+"Service";
        String serviceImplName = upperName+"ServiceImpl";
        String controllerName = upperName+"Controller";
        createFile(path, beanName, beanContent(path, beanName, authName));
        createFile(path, daoName, daoContent(path, daoName, authName));
        createFile(path, serviceName, serviceContent(path, serviceName, authName));
        createFile(path, serviceImplName, serviceImplContent(path, serviceImplName, authName));
        createFile(path, controllerName, controllerContent(path, controllerName, authName));


    }



    public static void createFile(String path, String fileName ,String content) throws IOException {
        File createBean = new File(path,fileName+".java");
        if(!createBean.exists()){
            FileOutputStream outputStream = new FileOutputStream(createBean);
            byte [] bytes = content.getBytes();
            outputStream.write(bytes);
            outputStream.close();
            createBean.createNewFile();
        }
    }

    public static String beanContent(String path, String fileName, String authName){
        StringBuilder sb = new StringBuilder();
        sb.append("package ");
        sb.append(path.substring(path.indexOf("com")).replace('\\','.'));
        sb.append(";\n\n");
        sb.append("import lombok.Getter;\nimport lombok.Setter;\n\nimport javax.persistence.Entity;\nimport javax.persistence.Table;\n\n");
        setAuth(sb, authName);
        sb.append("@Getter\n@Setter\n@Entity\n@Table(name = \"");
        sb.append(path.substring(path.lastIndexOf("\\")+1));
        sb.append("\")\n");
        sb.append("public class ");
        sb.append(fileName);
        sb.append("{\n\n}");
        return sb.toString();
    }

    public static String daoContent(String path, String fileName, String authName){
        StringBuilder sb = new StringBuilder();
        System.out.println();
        sb.append("package ");
        sb.append(path.substring(path.indexOf("com")).replace('\\','.'));
        sb.append(";\n\n");
        sb.append("import org.springframework.data.jpa.repository.JpaRepository;\n");
        sb.append("import org.springframework.stereotype.Repository;\n\n");
        setAuth(sb, authName);
        sb.append("@Repository\n");
        sb.append("public interface ");
        sb.append(fileName);
        sb.append(" extends JpaRepository<");
        sb.append(fileName.substring(0,fileName.indexOf("Dao")));
        sb.append("Bean,Integer> {\n\n}");
        return sb.toString();
    }

    public static String serviceContent(String path, String fileName, String authName){
        String service = "Service";
        StringBuilder sb = new StringBuilder();
        sb.append("package ");
        sb.append(path.substring(path.indexOf("com")).replace('\\','.'));
        sb.append(";\n\n");
        setAuth(sb, authName);
        sb.append("public interface ");
        sb.append(fileName);
        sb.append(" {\n\n");
        sb.append("    boolean add");
        sb.append(fileName.substring(0,fileName.indexOf(service)));
        sb.append("(");
        sb.append(fileName.substring(0,fileName.indexOf(service)));
        sb.append("Bean ");
        sb.append(fileName.substring(0,fileName.indexOf(service)).substring(0,1).toLowerCase());
        sb.append(fileName.substring(0,fileName.indexOf(service)).substring(1));
        sb.append("Bean);\n\n");

        sb.append("    boolean delete");
        sb.append(fileName.substring(0,fileName.indexOf(service)));
        sb.append("(");
        sb.append(fileName.substring(0,fileName.indexOf(service)));
        sb.append("Bean ");
        sb.append(fileName.substring(0,fileName.indexOf(service)).substring(0,1).toLowerCase());
        sb.append(fileName.substring(0,fileName.indexOf(service)).substring(1));
        sb.append("Bean);\n\n");

        sb.append("    boolean update");
        sb.append(fileName.substring(0,fileName.indexOf(service)));
        sb.append("(");
        sb.append(fileName.substring(0,fileName.indexOf(service)));
        sb.append("Bean ");
        sb.append(fileName.substring(0,fileName.indexOf(service)).substring(0,1).toLowerCase());
        sb.append(fileName.substring(0,fileName.indexOf(service)).substring(1));
        sb.append("Bean);\n\n");

        sb.append("}");
        return sb.toString();
    }

    public static String serviceImplContent(String path, String fileName, String authName){
        String service = "Service";
        StringBuilder sb = new StringBuilder();
        sb.append("package ");
        sb.append(path.substring(path.indexOf("com")).replace('\\','.'));
        sb.append(";\n\n");
        sb.append("import org.springframework.stereotype.Service;\n");
        sb.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        setAuth(sb, authName);
        sb.append("@Service\n");
        sb.append("public class ");
        sb.append(fileName);
        sb.append(" implements ");
        sb.append(fileName.substring(0,fileName.indexOf("Impl")));
        sb.append(" {\n\n");
        sb.append("    @Autowired\n");
        sb.append("    private ");
        sb.append(fileName.substring(0,fileName.indexOf("ServiceImpl")));
        sb.append("Dao ");
        sb.append(fileName.substring(0,fileName.indexOf("ServiceImpl")).substring(0,1).toLowerCase());
        sb.append(fileName.substring(0,fileName.indexOf("ServiceImpl")).substring(1));
        sb.append("Dao;");
        sb.append("\n\n");
        sb.append("    @Override\n");
        sb.append("    public boolean add");
        sb.append(fileName.substring(0,fileName.indexOf(service)));
        sb.append("(");
        sb.append(fileName.substring(0,fileName.indexOf(service)));
        sb.append("Bean ");
        sb.append(fileName.substring(0,fileName.indexOf(service)).substring(0,1).toLowerCase());
        sb.append(fileName.substring(0,fileName.indexOf(service)).substring(1));
        sb.append("Bean){\n        return true;\n    }\n\n");

        sb.append("    @Override\n");
        sb.append("    public boolean delete");
        sb.append(fileName.substring(0,fileName.indexOf(service)));
        sb.append("(");
        sb.append(fileName.substring(0,fileName.indexOf(service)));
        sb.append("Bean ");
        sb.append(fileName.substring(0,fileName.indexOf(service)).substring(0,1).toLowerCase());
        sb.append(fileName.substring(0,fileName.indexOf(service)).substring(1));
        sb.append("Bean){\n        return true;\n    }\n\n");

        sb.append("    @Override\n");
        sb.append("    public boolean update");
        sb.append(fileName.substring(0,fileName.indexOf(service)));
        sb.append("(");
        sb.append(fileName.substring(0,fileName.indexOf(service)));
        sb.append("Bean ");
        sb.append(fileName.substring(0,fileName.indexOf(service)).substring(0,1).toLowerCase());
        sb.append(fileName.substring(0,fileName.indexOf(service)).substring(1));
        sb.append("Bean){\n        return true;\n    }\n\n");

        sb.append("}");
        return sb.toString();
    }

    public static String controllerContent(String path, String fileName, String authName){
        String controller = "Controller";
        StringBuilder sb = new StringBuilder();
        sb.append("package ");
        sb.append(path.substring(path.indexOf("com")).replace('\\','.'));
        sb.append(";\n\n");
        sb.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        sb.append("import org.springframework.web.bind.annotation.*;\n\n");
        setAuth(sb, authName);
        sb.append("@RestController\n");
        sb.append("@RequestMapping(\"");
        sb.append(path.substring(path.lastIndexOf("\\") + 1));
        sb.append("\")\n");
        sb.append("public class ");
        sb.append(fileName);
        sb.append(" {\n\n");
        sb.append("    @Autowired\n");
        sb.append("    private ");
        sb.append(fileName.substring(0,fileName.indexOf(controller)));
        sb.append("Service ");
        sb.append(fileName.substring(0,fileName.indexOf(controller)).substring(0,1).toLowerCase());
        sb.append(fileName.substring(0,fileName.indexOf(controller)).substring(1));
        sb.append("Service;");
        sb.append("\n\n");
        sb.append("    @PostMapping\n");
        sb.append("    public boolean add");
        sb.append(fileName.substring(0,fileName.indexOf(controller)));
        sb.append("(");
        sb.append(fileName.substring(0,fileName.indexOf(controller)));
        sb.append("Bean ");
        sb.append(fileName.substring(0,fileName.indexOf(controller)).substring(0,1).toLowerCase());
        sb.append(fileName.substring(0,fileName.indexOf(controller)).substring(1));
        sb.append("Bean){\n        return true;\n    }\n\n");

        sb.append("    @DeleteMapping\n");
        sb.append("    public boolean delete");
        sb.append(fileName.substring(0,fileName.indexOf(controller)));
        sb.append("(");
        sb.append(fileName.substring(0,fileName.indexOf(controller)));
        sb.append("Bean ");
        sb.append(fileName.substring(0,fileName.indexOf(controller)).substring(0,1).toLowerCase());
        sb.append(fileName.substring(0,fileName.indexOf(controller)).substring(1));
        sb.append("Bean){\n        return true;\n    }\n\n");

        sb.append("    @PutMapping\n");
        sb.append("    public boolean update");
        sb.append(fileName.substring(0,fileName.indexOf(controller)));
        sb.append("(");
        sb.append(fileName.substring(0,fileName.indexOf(controller)));
        sb.append("Bean ");
        sb.append(fileName.substring(0,fileName.indexOf(controller)).substring(0,1).toLowerCase());
        sb.append(fileName.substring(0,fileName.indexOf(controller)).substring(1));
        sb.append("Bean){\n        return true;\n    }\n\n");

        sb.append("}");
        return sb.toString();
    }

    public static void setAuth(StringBuilder sb, String authName){
        sb.append("/**\n * @author ");
        sb.append(authName);
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String format = formatter.format(date);
        sb.append("\n * @Date ");
        sb.append(format);
        sb.append("\n */\n");
    }
}
