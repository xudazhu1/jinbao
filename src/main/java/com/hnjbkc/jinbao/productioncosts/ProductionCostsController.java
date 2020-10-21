package com.hnjbkc.jinbao.productioncosts;

import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 12
 * @Date 2019-09-29
 */
@RestController
@RequestMapping("production_costs")
public class ProductionCostsController {

    private ProductionCostsServiceImpl productionCostsServiceImpl;

    @Autowired
    public void setProductionCostsServiceImpl(ProductionCostsServiceImpl productionCostsServiceImpl) {
        this.productionCostsServiceImpl = productionCostsServiceImpl;
    }

    @PostMapping
    public Boolean add(ProductionCostsBean productionCostsBean){
        return productionCostsServiceImpl.add(productionCostsBean) != null;
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return productionCostsServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(ProductionCostsBean productionCostsBean){
        return productionCostsServiceImpl.update(productionCostsBean) != null;
    }

    @GetMapping("production_info")
    public Map<String,Object> getProductionInfo(Integer id){
        return productionCostsServiceImpl.getProductionInfo(id);
    }


    @GetMapping("project_production")
    public Map<String,Object> getProjectAndProductionInfo(@RequestParam Map<String, Object> propMap){
        return productionCostsServiceImpl.getProjectAndProductionInfo(propMap);
    }

    @GetMapping("find_all")
    public Object findAll(@RequestParam Map<String, Object> propMap){
        return productionCostsServiceImpl.findAll(propMap);
    }

    @GetMapping("commission")
    public List<Map<String, Object>> getCommissionByProjectId(@RequestParam String id){
        return productionCostsServiceImpl.getCommissionByProjectId(id);
    }

}
