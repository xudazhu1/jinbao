package com.hnjbkc.jinbao.invoiceandmoneyback;

import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.utils.tableutils.TableUtilsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xudaz
 */
@RestController
@RequestMapping("auto_num")
public class AutoNumController {

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    private SqlUtilsDao sqlUtilsDao;


    private TableUtilsDao tableUtilsDao;

    @Autowired
    public void setTableUtilsDao(TableUtilsDao tableUtilsDao) {
        this.tableUtilsDao = tableUtilsDao;
    }

    @GetMapping("income")
    public Object getMaxNum() {
        String hql = "from AutoNumBean ";
        List arrayList = (ArrayList)sqlUtilsDao.exSqlCustom(AutoNumBean.class, hql, null);
        Integer num = 1;
        AutoNumBean autoNumBean = new AutoNumBean();

        if ( arrayList.size() > 0 ) {
            autoNumBean = (AutoNumBean) arrayList.get(0);
            num = autoNumBean.getIncomeNum();
        }
        autoNumBean.setIncomeNum(num + 1);
        tableUtilsDao.update(autoNumBean);

        StringBuilder stringBuilder = new StringBuilder(num.toString());
        for ( int i = 5 ; i > num.toString().length() ; i-- ) {
            stringBuilder.insert(0 , "0");
        }
        return stringBuilder.toString();
    }

}
