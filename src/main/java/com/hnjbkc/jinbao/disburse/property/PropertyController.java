package com.hnjbkc.jinbao.disburse.property;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @author siliqiang
 * @date 2019.9.19
 */
@RestController
@RequestMapping("property")
public class PropertyController {
    private PropertyServiceImpl propertyService;

    @Autowired
    public void setPropertyService(PropertyServiceImpl propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    public Boolean addProperty(PropertyBean propertyBean) {
        return propertyService.add(propertyBean) != null;

    }

    @PutMapping
    public Boolean updateProperty(PropertyBean propertyBean) {
        return propertyService.update(propertyBean) != null;
    }

    @DeleteMapping
    public Boolean deleteProperty(Integer id) {
        return propertyService.delete(id);
    }


   /* @GetMapping
    public List get(@DateTimeFormat(pattern = "yyyy-MM-dd") Date date, Integer pageNum, Integer pageSize) throws ParseException {
        List<PropertyBean> propertyBeans = propertyService.get(date, pageNum, pageSize);
        return propertyBeans;
    }*/
}
