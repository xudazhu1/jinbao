package com.hnjbkc.jinbao.quote.quotefile;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;

/**
 * @author 12
 * @Date 2019-10-06
 */
@Service
public class QuoteFileServiceImpl implements BaseService<QuoteFileBean> {

    private QuoteFileDao quoteFileDao;

    @Autowired
    public void setQuoteFileDao(QuoteFileDao quoteFileDao ) {
        this.quoteFileDao  = quoteFileDao;
    }

    @Override
    public  QuoteFileBean add(QuoteFileBean quoteFileBean){
        return quoteFileDao.save(quoteFileBean);
    }

    @Override
    public Boolean delete(Integer id){
        quoteFileDao.deleteById(id);
        return true;
    }

    @Override
    public QuoteFileBean update(QuoteFileBean quoteFileBean){
        return quoteFileDao.save(quoteFileBean);
    }

}
