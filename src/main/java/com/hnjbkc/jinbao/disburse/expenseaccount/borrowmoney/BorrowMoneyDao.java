package com.hnjbkc.jinbao.disburse.expenseaccount.borrowmoney;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * @author siliqiaing
 * @date 2019.9.29
 */
public interface BorrowMoneyDao extends JpaRepository<BorrowMoneyBean, Integer> {
    @Query(value = "SELECT date_format( br.borrow_money_date , '%Y-%m') as dateA , sum(br.borrow_money_money) , u.user_name  , pbc.personal_bank_card_initial_value,br.borrow_money_id\n" +
            "from borrow_money as br \n" +
            "LEFT OUTER JOIN personal_bank_card as pbc on pbc.personal_bank_card_id = br.borrow_money_personal_bank_card_id  \n" +
            "LEFT OUTER JOIN `user` as u on u.user_id = pbc.personal_bank_card_user_id WHERE u.user_name=?1 \n" +
            "GROUP BY dateA , br.borrow_money_personal_bank_card_id ORDER BY  br.borrow_money_personal_bank_card_id  , dateA ", nativeQuery = true)
    List<Object[]> getBorrowRental(String name);

    @Query(value = "SELECT SUM(bro.borrow_money_money),bank.bank_card_id ,bank.bank_card_name,bro.borrow_money_id FROM borrow_money AS bro\n" +
            "LEFT OUTER JOIN bank_card AS bank ON bank.bank_card_id=bro.borrow_money_bank_card_id\n" +
            "GROUP BY bank.bank_card_id ", nativeQuery = true)
    List<Object[]> getSumBorrow();
}
