package com.hnjbkc.jinbao.squadgroupfee.productioncostsdetail;

import com.hnjbkc.jinbao.utils.restresponse.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author siliqiang
 * @date 2019/12/30
 */
@RestController
@RequestMapping("/production_costs_detail")
public class ProductionCostsDetailController {
    private ProductionCostsDetailServiceImpl productionCostsDetailService;

    @Autowired
    public void setProductionCostsDetailService(ProductionCostsDetailServiceImpl productionCostsDetailService) {

        this.productionCostsDetailService = productionCostsDetailService;
    }

    @PostMapping
    public ProductionCostsDetailBean addProductionCostsDetail(ProductionCostsDetailBean productionCostsDetailBean) {
        return updateProductionCostsDetail(productionCostsDetailBean);
    }

    @PutMapping
    public ProductionCostsDetailBean updateProductionCostsDetail(ProductionCostsDetailBean productionCostsDetailBean) {
        return productionCostsDetailService.add(productionCostsDetailBean);
    }

    @GetMapping
    public RestResponse getProductionCostsDetail(@RequestParam Map<String, Object> propMap){
       return  productionCostsDetailService.getAll(propMap);
    }


}
