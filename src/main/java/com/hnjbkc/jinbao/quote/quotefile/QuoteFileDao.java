package com.hnjbkc.jinbao.quote.quotefile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 12
 * @Date 2019-10-06
 */
@Repository
public interface QuoteFileDao extends JpaRepository<QuoteFileBean,Integer> {

}
