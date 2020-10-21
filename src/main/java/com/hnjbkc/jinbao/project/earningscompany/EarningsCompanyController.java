package com.hnjbkc.jinbao.project.earningscompany;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 12
 * @Date 2019-08-13
 */
@RestController
@RequestMapping("earnings_company")
public class EarningsCompanyController {

    private EarningsCompanyServiceImpl earningsCompanyServiceImpl;

    @Autowired
    public void setEarningsCompanyServiceImpl(EarningsCompanyServiceImpl earningsCompanyServiceImpl) {
        this.earningsCompanyServiceImpl = earningsCompanyServiceImpl;
    }

    @PostMapping
    public Boolean add(EarningsCompanyBean earningsCompanyBean){
        return earningsCompanyServiceImpl.add(earningsCompanyBean) != null;
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return earningsCompanyServiceImpl.delete(id);
    }

    @PutMapping
    public EarningsCompanyBean update(EarningsCompanyBean earningsCompanyBean){
        return earningsCompanyServiceImpl.update(earningsCompanyBean);
    }

    @GetMapping
    public List<EarningsCompanyBean> getEarningsCompanyList(){
        return earningsCompanyServiceImpl.getEarningsCompanyList();
    }
}
