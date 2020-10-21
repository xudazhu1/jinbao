package com.hnjbkc.jinbao.productioncosts.productioncostsfile;

import com.hnjbkc.jinbao.implement.ImplementBean;
import lombok.Data;

import javax.persistence.*;

/**
 * 生产费的文件上传
 * @author siliqiang
 * @date 2019/12/2
 */
@Entity
@Data
@Table(name = "production_costs_file")
public class ProductionCostsFileBean {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productionCostsFileId;
    /**
     * 文件相对路径
     */
    private String productionCostsFileRelativePaths;

    /**
     * 文件名
     */
    private String productionCostsFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_costs_file_implement_id", referencedColumnName = "implementId")
    private ImplementBean implementBean;
}
