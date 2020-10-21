package com.hnjbkc.jinbao.contract.material;

import com.hnjbkc.jinbao.utils.FormatJsonMap;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author siliqiang
 * @date 2019.4.19
 */
@RestController
@RequestMapping("material")
public class MaterialController {
    private MaterialService materialService;

    @Autowired
    public void setMaterialService(MaterialService materialService) {
        this.materialService = materialService;
    }

    @DeleteMapping
    public String delete(Integer id) {
        Boolean deleteMaterial= materialService.deleteMaterial(id);
        JSONObject format = FormatJsonMap.format(null, 1, deleteMaterial);
        return format.toString();
    }

    @GetMapping("cont_id")
    public List findMaterialList(Integer contractId){
        return materialService.findMaterialList(contractId);
    }
}
