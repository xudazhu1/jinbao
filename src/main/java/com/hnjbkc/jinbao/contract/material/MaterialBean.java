package com.hnjbkc.jinbao.contract.material;

import com.hnjbkc.jinbao.contract.ContractBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 扫描件pojo类
 * @author siliqiang
 * @date 2019/8/9
 */
@Entity
@Table(name = "contract_accessory")
@Getter
@Setter
public class MaterialBean  implements Serializable {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contractAccessoryId;
    /**
     * 文件之前的名称
     */
    private String contractAccessoryOldName;
    /**
     * 随机生成的名称
     */
    private String contractAccessoryNewName;

    /**
     * 多对一
     * \@name = "material_itid"  外键
     * \@referencedColumnName = "contId" 对应合同的主键
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_accessory_contract_id" , referencedColumnName = "contractId")
    private ContractBean contractBean;

    @Override
    public String toString() {
        return "MaterialBean{" +
                "contractAccessoryId=" + contractAccessoryId +
                ", contractAccessoryOldName='" + contractAccessoryOldName + '\'' +
                ", contractAccessoryNewName='" + contractAccessoryNewName + '\'' +
                ", contractBean=" + contractBean +
                '}';
    }
}
