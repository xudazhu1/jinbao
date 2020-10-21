package com.hnjbkc.jinbao.project.earningscompany;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 12
 * @Date 2019-08-13
 */
@Repository
public interface EarningsCompanyDao extends JpaRepository<EarningsCompanyBean,Integer> {

}
