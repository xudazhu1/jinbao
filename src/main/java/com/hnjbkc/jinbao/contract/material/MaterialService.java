package com.hnjbkc.jinbao.contract.material;


import java.util.List;

/**
 * @author siliqiang
 * @date 2019.4.19
 */
public interface MaterialService {
    /**
     * 根据id上传数据
     * @param id 传过来需要删除数据的id
     * @return  返回已布尔值
     */
    Boolean deleteMaterial(Integer id);

    /**
     * 通过合同id查找扫描件
     * @param contId 合同id
     * @return List
     */
    List findMaterialList(Integer contId);
}
