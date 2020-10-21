package com.hnjbkc.jinbao.disburse.finance.bankcard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.midi.Soundbank;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author siliqiang
 * @date 2019.9.26
 */
@RestController
@RequestMapping("/bank_card")
public class BankCardController {
    private BankCardServiceImpl bankCardService;

    @Autowired
    public void setBankCardService(BankCardServiceImpl bankCardService) {
        this.bankCardService = bankCardService;
    }


    @GetMapping
    public Map<String, Object[]> getBankCard(){
       return bankCardService.getBankCard();
    }
    /*public void get(String date){

        System.out.println(date+"*************************************");
        List bankCard = bankCardService.getBankCard(date);
        for (Object o : bankCard) {
            System.out.println(o);
        }
    }*/
}
