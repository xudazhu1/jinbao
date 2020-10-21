package com.hnjbkc.jinbao.disburse.paymentstatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 12
 * @Date 2019-09-18
 */
@Repository
public interface PaymentStatusDao extends JpaRepository<PaymentStatusBean,Integer> {

}
