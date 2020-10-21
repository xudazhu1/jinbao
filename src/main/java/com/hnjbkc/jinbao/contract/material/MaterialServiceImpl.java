package com.hnjbkc.jinbao.contract.material;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author siliqiang
 * @date 2019.4.19
 */
@Service
public class MaterialServiceImpl implements MaterialService {
    private MaterialDao materialDao;
    @Autowired
    public void setMaterialDao(MaterialDao materialDao){
        this.materialDao=materialDao;
    }

    @Override
    public Boolean deleteMaterial(Integer id) {
        if (id!=null){
            materialDao.deleteById(id);
        }
       return id!=null;
    }

    @Override
    public List<Map<String, String>> findMaterialList(Integer contId) {
        List<Map<String, String>> materialList = materialDao.findMaterialList(contId);
        return materialList;
    }
}
