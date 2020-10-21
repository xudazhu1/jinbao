package com.hnjbkc.jinbao.gettheconfigurationfile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author siliqiang
 * @date 2019.8.27
 * 读取配置文件的内容
 */
@RestController
@RequestMapping(value = "get_config_file")
public class GetTheConfigurationFile {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;

    @GetMapping
    public Object gateway() {
        Map<String , Object> map =  new HashMap<>(2);
        map.put("maxFileSize" , maxFileSize);
        map.put("maxRequestSize" , maxRequestSize);
        return map;
        //1、使用@Value注解读取
//        return " { maxFileSize: '" + maxFileSize +
//                "' , maxRequestSize: '" + maxRequestSize + "'}";
    }

}
