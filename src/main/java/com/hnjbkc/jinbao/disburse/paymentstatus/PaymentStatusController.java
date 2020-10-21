package com.hnjbkc.jinbao.disburse.paymentstatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 12
 * @Date 2019-09-18
 */
@RestController
@RequestMapping("payment_status")
public class PaymentStatusController {

    private PaymentStatusServiceImpl paymentStatusServiceImpl;

    @Autowired
    public void setPaymentStatusServiceImpl(PaymentStatusServiceImpl paymentStatusServiceImpl) {
        this.paymentStatusServiceImpl = paymentStatusServiceImpl;
    }

    @PostMapping
    public Boolean add(PaymentStatusBean paymentStatusBean){
        return paymentStatusServiceImpl.add(paymentStatusBean) != null;
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return paymentStatusServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(PaymentStatusBean paymentStatusBean){
        return paymentStatusServiceImpl.update(paymentStatusBean) != null;
    }

    @GetMapping
    public List<PaymentStatusBean> listPaymentStatusBean(){
        return paymentStatusServiceImpl.listPaymentStatusBean();
    }
}
