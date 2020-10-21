package com.hnjbkc.jinbao.implement.secondparty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 12
 * @Date 2019-08-08
 */
@RestController
@RequestMapping("second_party")
public class SecondPartyController {

    private SecondPartyServiceImpl secondPartyServiceImpl;

    @Autowired
    public void setSecondPartyServiceImpl(SecondPartyServiceImpl secondPartyServiceImpl) {
        this.secondPartyServiceImpl = secondPartyServiceImpl;
    }

    @PostMapping
    public boolean add(SecondPartyBean secondpartyBean){
        return secondPartyServiceImpl.add(secondpartyBean) != null ;
    }

    @DeleteMapping
    public boolean delete(Integer id) {
        return secondPartyServiceImpl.delete(id);
    }

    @PutMapping
    public boolean update(SecondPartyBean secondpartyBean){
        return secondPartyServiceImpl.update(secondpartyBean) != null;
    }

    @GetMapping
    public List<SecondPartyBean> getSecondPartyList(){
        return secondPartyServiceImpl.getSecondPartyList();
    }
}
