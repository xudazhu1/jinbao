package com.hnjbkc.jinbao.organizationalstructure.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author siliqiang
 * @date 2019.7.11
 */
@Repository
public interface CompanyDao extends JpaRepository<CompanyBean, Integer> {
}
