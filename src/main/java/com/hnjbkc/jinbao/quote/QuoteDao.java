package com.hnjbkc.jinbao.quote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author 12
 * @Date 2019-07-31
 */
@Repository
public interface QuoteDao extends JpaRepository<QuoteBean,Integer> {

    @Query(value = "SELECT quote_id as quoteId, quote_num as quoteNum , quote_name as quoteName , project_id as projectId FROM  quote left join  project  on  project.project_quote_id = quote.quote_id where  quote.quote_status = '中标通过'",nativeQuery = true)
    List<Map<String, Object>> getSelectBox();

    /**
     *
     * @return 返回 最大编号
     */
    @Query(value = "select max(substring(quote_num , 4 , 4)) from QuoteBean ")
    String findMaxNumber();
}
