package com.hnjbkc.jinbao.quote.quotefile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 12
 * @Date 2019-10-06
 */
@RestController
@RequestMapping("quote_file")
public class QuoteFileController {

    private QuoteFileServiceImpl quoteFileServiceImpl;

    @Autowired
    public void setQuoteFileServiceImpl(QuoteFileServiceImpl quoteFileServiceImpl) {
        this.quoteFileServiceImpl = quoteFileServiceImpl;
    }

    @PostMapping
    public Boolean add(QuoteFileBean quoteFileBean){
        return quoteFileServiceImpl.add(quoteFileBean) != null;
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return quoteFileServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(QuoteFileBean quoteFileBean){
        return quoteFileServiceImpl.update(quoteFileBean) != null;
    }

}
