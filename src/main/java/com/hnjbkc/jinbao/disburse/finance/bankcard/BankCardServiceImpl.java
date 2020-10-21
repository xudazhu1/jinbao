package com.hnjbkc.jinbao.disburse.finance.bankcard;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.disburse.DisburseDao;
import com.hnjbkc.jinbao.disburse.expenseaccount.borrowmoney.BorrowMoneyDao;
import com.hnjbkc.jinbao.disburse.expenseaccount.transferaccounts.TransferAccountsDao;
import com.hnjbkc.jinbao.invoiceandmoneyback.moneyback.MoneyBackDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @author siliqiang
 * @date 2019.9.25
 */
@Service
public class BankCardServiceImpl implements BaseService<BankCardBean> {
    private BankCardDao bankCardDao;

    @Autowired
    public void setBankCardDao(BankCardDao bankCardDao) {
        this.bankCardDao = bankCardDao;
    }

    @Override
    public BankCardBean add(BankCardBean bankCardBean) {
        return null;
    }

    @Override
    public Boolean delete(Integer id) {
        return null;
    }

    @Override
    public BankCardBean update(BankCardBean bankCardBean) {
        return null;
    }

    private DisburseDao disburseDao;

    @Autowired
    public void setDisburseDao(DisburseDao disburseDao) {
        this.disburseDao = disburseDao;
    }

    private MoneyBackDao moneyBackDao;

    @Autowired
    public void setMoneyBackDao(MoneyBackDao moneyBackDao) {
        this.moneyBackDao = moneyBackDao;
    }

    private TransferAccountsDao transferAccountsDao;

    @Autowired
    public void setTransferAccountsDao(TransferAccountsDao transferAccountsDao) {
        this.transferAccountsDao = transferAccountsDao;
    }

    private BorrowMoneyDao borrowMoneyDao;

    @Autowired
    public void setBorrowMoneyDao(BorrowMoneyDao borrowMoneyDao) {
        this.borrowMoneyDao = borrowMoneyDao;
    }

    public Map<String, Object[]> getBankCard() {
        List<BankCardBean> all = bankCardDao.findAll();
        HashMap<String, Object[]> bankCardMap = new HashMap<>(2);
        List<Object[]> sumDisburse = disburseDao.getSumDisburse();
        List<Object[]> sumMoneyBackBean = moneyBackDao.getMoneyBackBean();
        List<Object[]> sumeEnter = transferAccountsDao.getSumeEnter();
        List<Object[]> sumCome = transferAccountsDao.getSumCome();
        List<Object[]> sumBorrow = borrowMoneyDao.getSumBorrow();

        for (BankCardBean bankCardBean : all) {
            bankCardMap.put(bankCardBean.getBankCardName(), new Object[8]);
            bankCardMap.put("init" + bankCardBean.getBankCardName(), new Object[]{bankCardBean.getBankCardInitialValue()});
        }

        //添加收入
        for (Object[] objects : sumMoneyBackBean) {
            if (objects[2] != null) {
                bankCardMap.get(objects[2])[1] = objects[0];
            }
        }
        //添加支出
        for (Object[] objects : sumDisburse) {
            if (objects[2] != null) {
                bankCardMap.get(objects[2])[2] = objects[0];
            }
        }
        //转账转入金额
        for (Object[] objects : sumeEnter) {
            if (objects[2] != null) {
                bankCardMap.get(objects[2])[3] = objects[0];
            }
        }
        //转账转出金额
        for (Object[] objects : sumCome) {
            if (objects[2] != null) {
                bankCardMap.get(objects[2])[4] = objects[0];
            }
        }

        //借款金额
        for (Object[] objects : sumBorrow) {
            if (objects[2] != null) {
                bankCardMap.get(objects[2])[5] = objects[0];
            }
        }

        for (Map.Entry<String, Object[]> objectEntry : bankCardMap.entrySet()) {
            if (objectEntry.getKey().startsWith("init")) {
                continue;
            }
            Object[] value = objectEntry.getValue();
            if (value != null) {
                for (int i = 1; i < 8; i++) {
                    if (value[i] == null) {
                        value[i] = 0.00;
                    }
                }
                if (value[0] == null) {
                    Object[] objects = bankCardMap.get("init" + objectEntry.getKey());
                    value[0] = objects[0];
                }

                //转账金额
                value[6] = (Double) value[3] - (Double) value[4];

                //期末金额
                value[7] = (Double) value[0] + (Double) value[1] - (Double) value[2] + (Double) value[6] - (Double) value[5];

            }
        }

        bankCardMap.keySet().removeIf(key -> key.startsWith("init"));
        return bankCardMap;

    }

}
