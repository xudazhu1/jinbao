package com.hnjbkc.jinbao.disburse.expenseaccount.transferaccounts;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.disburse.expenseaccount.borrowmoney.BorrowMoneyBean;
import com.hnjbkc.jinbao.utils.MyBeanUtils;
import com.hnjbkc.jinbao.utils.tableutils.TableUtilsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author siliqiang
 * @date 2019/11/12
 */
@Service
public class TransferAccountsServiceImpl implements BaseService<TransferAccountsBean> {
    private TableUtilsDao tableUtilsDao;

    @Autowired
    public void setTableUtilsDao(TableUtilsDao tableUtilsDao) {
        this.tableUtilsDao = tableUtilsDao;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TransferAccountsBean add(TransferAccountsBean transferAccountsBean) {
        TransferAccountsBean updateTransferAccountsBean = tableUtilsDao.update(transferAccountsBean);
        if (updateTransferAccountsBean != null && updateTransferAccountsBean.getTransferAccountsNum() == null) {
            MyBeanUtils.createNumber(updateTransferAccountsBean, "Z", "transferAccountsNum", null);
        }
        return updateTransferAccountsBean;
    }

    @Override
    public Boolean delete(Integer id) {
        return null;
    }

    @Override
    public TransferAccountsBean update(TransferAccountsBean transferAccountsBean) {
        return null;
    }
}
