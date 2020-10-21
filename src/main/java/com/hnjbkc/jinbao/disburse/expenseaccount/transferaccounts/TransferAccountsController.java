package com.hnjbkc.jinbao.disburse.expenseaccount.transferaccounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author siliqiang
 * @date 2019.11.12
 */
@RestController
@RequestMapping("transfer_accounts")
public class TransferAccountsController {
    private TransferAccountsServiceImpl transferAccountsService;

    @Autowired
    public void setTransferAccountsService(TransferAccountsServiceImpl transferAccountsService) {
        this.transferAccountsService = transferAccountsService;
    }

    @PutMapping
    public TransferAccountsBean put(TransferAccountsBean transferAccountsBean) {
        return transferAccountsService.add(transferAccountsBean);
    }

    @PostMapping
    public TransferAccountsBean add(TransferAccountsBean transferAccountsBean) {
        return put(transferAccountsBean);
    }
}
