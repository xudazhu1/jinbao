package com.hnjbkc.jinbao.implement.implementrecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 12
 * @Date 2019-08-21
 */
@Repository
public interface ImplementRecordDao extends JpaRepository<ImplementRecordBean,Integer> {

}
