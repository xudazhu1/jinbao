package com.hnjbkc.jinbao.utils.layuisoultable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xudaz
 */
@SuppressWarnings("WeakerAccess")
@Service
public class SoulTableService {

    private SoulTableDao soulTableDao;

    @Autowired
    public void setSoulTableDao(SoulTableDao soulTableDao) {
        this.soulTableDao = soulTableDao;
    }

    public Object get4layUiTable( SoulPage soulPage, String filterSos ) {
        // 把搜索条件 封装进 soulPage
        SoulUtils.addFilterSo( soulPage , filterSos );
        return soulTableDao.get4layUiTable( soulPage   );
    }
}
