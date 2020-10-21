package com.hnjbkc.jinbao.number;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author siliqiang
 * @date 2019.9.3
 */
@RestController
@RequestMapping("number")
public class NumberController {
    private NumberServiceImpl numberService;

    @Autowired
    public void setNumberService(NumberServiceImpl numberService) {
        this.numberService = numberService;
    }

    @GetMapping
    public NumberBean addNumber() {
        NumberBean num = numberService.getNum();
        return num;

    }

}
