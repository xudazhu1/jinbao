package com.hnjbkc.jinbao.disburse.paymentstatus;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author 12
 * @Date 2019-09-18
 */
@Service
public class PaymentStatusServiceImpl implements BaseService<PaymentStatusBean> {

    private PaymentStatusDao paymentStatusDao;

    @Autowired
    public void setPaymentStatusDao(PaymentStatusDao paymentStatusDao ) {
        this.paymentStatusDao  = paymentStatusDao;
    }

    @Override
    public PaymentStatusBean add(PaymentStatusBean paymentStatusBean){
        return paymentStatusDao.save(paymentStatusBean);
    }

    @Override
    public Boolean delete(Integer id){
        paymentStatusDao.deleteById(id);
        return true;
    }

    @Override
    public PaymentStatusBean update(PaymentStatusBean paymentStatusBean){
        return paymentStatusDao.save(paymentStatusBean);
    }


    public List<PaymentStatusBean> listPaymentStatusBean() {
       return paymentStatusDao.findAll();
    }
}
