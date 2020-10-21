package com.hnjbkc.jinbao.disburse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author siliqiang
 * @date 2019.9.2
 */
@RestController
@RequestMapping("disburse")
public class DisburseController {
    private DisburseServiceImpl disburseService;

    @Autowired
    public void setDisburseService(DisburseServiceImpl disburseService) {
        this.disburseService = disburseService;
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    @GetMapping("get_single_properties")
    public List getSingleProperties(String property) {
        return disburseService.getSingleProperty(property);
    }
}
