package com.hnjbkc.jinbao.number;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.disburse.disbursetype.DisburseTypeBean;
import com.hnjbkc.jinbao.utils.AttrSwop;
import com.hnjbkc.jinbao.utils.GetNum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.util.NullableWrapper;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author siliqiang
 * @date 2019.9.3
 */
@Service
public class NumberServiceImpl implements BaseService<NumberBean> {
    private NumberDao numberDao;

    @Autowired
    public void setNumberDao(NumberDao numberDao) {
        this.numberDao = numberDao;
    }

    @Override
    public NumberBean add(NumberBean numberBean) {
        return null;
    }

    @Override
    public Boolean delete(Integer id) {
        return null;
    }

    @Override
    public NumberBean update(NumberBean numberBean) {
        return null;
    }

    public synchronized NumberBean getNum() {
        List<NumberBean> all = numberDao.findAll();
        String year = new SimpleDateFormat("yyyy").format(new Date()).substring(2);
        if (all.size() == 0) {
            String maxSerialNum = numberDao.getMaxSerialNum(year);
            String maxnum = GetNum.getMaxnum(maxSerialNum);
            NumberBean numberBean = new NumberBean();
            numberBean.setNumberName( year + maxnum);
            return numberDao.save(numberBean);
        }

        NumberBean numberBean = all.get(0);
        String substring = numberBean.getNumberName().substring(2);
        StringBuilder s = new StringBuilder(Integer.parseInt(substring) + 1 + "");
        int a = 6;
        for (int i = s.length(); i < a; i++) {
            s.insert(0, "0");
        }
        numberBean.setNumberName(  year + s);

        return numberDao.save(numberBean);

    }
}
