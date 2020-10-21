package com.hnjbkc.jinbao.disburse.expenseaccount.transferaccounts;

import com.hnjbkc.jinbao.utils.GetClass4BeanSimpleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author siliqiang
 * @date 2019.10.3
 */
public interface TransferAccountsDao extends JpaRepository<TransferAccountsBean, Integer> {

    /**
     * 转入
     * @return
     */
    @Query(value = "SELECT SUM(transferAccounts.transfer_accounts_money),bank.bank_card_id ,bank.bank_card_name FROM transfer_accounts AS transferAccounts\n" +
            "LEFT OUTER JOIN bank_card AS bank ON bank.bank_card_id=transferAccounts.responsible_enter_bank_card_id WHERE transfer_accounts_payment_status_id='8'\n" +
            "GROUP BY bank.bank_card_id ", nativeQuery = true)
    List<Object[]>getSumeEnter();

    /**
     * 转出
     * @return
     */
    @Query(value = "SELECT SUM(transferAccounts.transfer_accounts_money),bank.bank_card_id ,bank.bank_card_name FROM transfer_accounts AS transferAccounts\n" +
            "LEFT OUTER JOIN bank_card AS bank ON bank.bank_card_id=transferAccounts.responsible_come_bank_card_id WHERE transfer_accounts_payment_status_id='8'\n" +
            "GROUP BY bank.bank_card_id ", nativeQuery = true)
    List<Object[]> getSumCome();


}
