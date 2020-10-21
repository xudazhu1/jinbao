package com.hnjbkc.jinbao.productioncosts.productioncostsfile;

import org.springframework.data.jpa.repository.JpaRepository;


/**
 * 生产费的文件上传
 * @author siliqiang
 * @date 2019/12/2
 */
public interface ProductionCostsFileDao extends JpaRepository<ProductionCostsFileBean,Integer> {
}
