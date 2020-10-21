package com.hnjbkc.jinbao.disburse.finance;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.disburse.DisburseAutoNumUtils;
import com.hnjbkc.jinbao.disburse.DisburseBean;
import com.hnjbkc.jinbao.disburse.DisburseDao;
import com.hnjbkc.jinbao.utils.tableutils.TableUtilsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 财务日常
 * @author siliqiang
 * @date 2019.9.2
 */

@Service
public class FinanceServiceImpl implements BaseService<DisburseBean> {
    private DisburseDao disburseDao;

    @Autowired
    public void setDisburseDao(DisburseDao disburseDao) {
        this.disburseDao=disburseDao;
    }

    private TableUtilsDao tableUtilsDao;

    @Autowired
    public void setTableUtilsDao(TableUtilsDao tableUtilsDao) {
        this.tableUtilsDao = tableUtilsDao;
    }

    private DisburseAutoNumUtils disburseAutoNumUtils;

    @Autowired
    public void setDisburseAutoNumUtils(DisburseAutoNumUtils disburseAutoNumUtils) {
        this.disburseAutoNumUtils = disburseAutoNumUtils;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public DisburseBean add(DisburseBean disburseBean) {
        DisburseBean updateDisburseBean = tableUtilsDao.update(disburseBean);
        if (updateDisburseBean != null ) {
            if (updateDisburseBean.getDisburseNum() == null ||"" .equals(updateDisburseBean.getDisburseNum())){
                //编号 前缀 019 11 02132
                //先获取年月 再获取主键拼接为编号
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
                String format = simpleDateFormat.format(new Date());
                String year = format.substring(2, 4);
                String month = format.substring(5);
                Integer financeNum = disburseAutoNumUtils.getNum("财务日常");
                updateDisburseBean.setDisburseNum( "CR-" + year + month + financeNum );
            }
        }
        return updateDisburseBean;

    }

    @Override
    public Boolean delete(Integer id) {
        return null;
    }

    @Override
    public DisburseBean update(DisburseBean disburseBean) {
        return null;
    }
}
