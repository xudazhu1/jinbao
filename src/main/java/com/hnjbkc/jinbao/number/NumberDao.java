package com.hnjbkc.jinbao.number;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author siliqiang
 * @date 2019.9.3
 */
public interface NumberDao extends JpaRepository<NumberBean,Integer> {

    /**
     * 获取最大的可用流水号后四位
     *
     * @param year year
     * @return max(Serial)
     */
    @Query(value = "select max(substring(numberName , 6 , 5) ) from NumberBean  where substring(numberName , 3 , 3 )=?1 ")
    String getMaxSerialNum(String year);
}
