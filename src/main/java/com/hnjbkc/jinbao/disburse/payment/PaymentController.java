package com.hnjbkc.jinbao.disburse.payment;

import com.hnjbkc.jinbao.disburse.DisburseBean;
import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.hnjbkc.jinbao.utils.PageableUtils.producePageable4Map;

/**
 * @author 12
 */
@RestController
@RequestMapping("payment")
public class PaymentController {

    private PaymentServiceImpl paymentServiceImpl;

    @Autowired
    public void setDisburseBean(PaymentServiceImpl paymentServiceImpl) {
        this.paymentServiceImpl = paymentServiceImpl;
    }

    @PostMapping
    public DisburseBean add(DisburseBean disburseBean) {
        return update(disburseBean);
    }

    @PutMapping
    public DisburseBean update(DisburseBean disburseBean) {
        return paymentServiceImpl.add(disburseBean);
    }

    @DeleteMapping
    public boolean delete(Integer id) {
        return paymentServiceImpl.delete(id);
    }

    @GetMapping("by_id")
    public DisburseBean getDisburseBeanById(Integer id) {
        return paymentServiceImpl.getDisburseBeanById(id);
    }

    @GetMapping("list_property")
    public Page<DisburseBean> listProperty(String dateA, Integer pageNum, Integer pageSize) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        return paymentServiceImpl.listProperty(dateA, pageNum, pageSize);
    }

    @GetMapping("property_num_val")
    public Object[] getPropertyNumAndVal() {
        return paymentServiceImpl.getPropertyNumAndVal();
    }

    /**
     * 本次付款后累计业务提成付款金额
     * @param id 支出id
     * @param type 种类
     * @return Map<String, Object>
     */
    @GetMapping("print_income_cost")
    public Map<String, Object> getProjectIncomeAndCost(Integer id,String type) {
        return paymentServiceImpl.getProjectIncomeAndCost(id,type);
    }

    /**
     * 本次付款后累计业务提成付款金额
     * @param id 支出id
     * @param type 小类
     * @return Map<String, Object>
     */
    @GetMapping("print_subclass_income_cost")
    public Map<String, Object> getProjectSubclassIncomeAndCost(Integer id,String type) {
        return paymentServiceImpl.getProjectSubclassIncomeAndCost(id,type);
    }

    @GetMapping("print_paid_boss_cost")
    public Double paidBossCostByProjectId(Integer id) {
        return paymentServiceImpl.paidBossCostByProjectId(id);
    }
}
