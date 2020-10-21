package com.hnjbkc.jinbao.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xudaz
 */
@RestController
@RequestMapping("config")
public class ConfigController {

    @GetMapping
    public boolean update_version(HttpServletRequest request) {
        request.getServletContext().setAttribute("version" , System.currentTimeMillis() );
        return true;
    }

}
