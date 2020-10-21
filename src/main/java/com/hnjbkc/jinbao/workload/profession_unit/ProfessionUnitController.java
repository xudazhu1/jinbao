package com.hnjbkc.jinbao.workload.profession_unit;

import com.hnjbkc.jinbao.common.CommonResult;
import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 12
 * @Date 2019-09-16
 */
@RestController
@RequestMapping("profession_unit")
public class ProfessionUnitController {

    private ProfessionUnitServiceImpl professionUnitServiceImpl;

    @Autowired
    public void setProfessionUnitServiceImpl(ProfessionUnitServiceImpl professionUnitServiceImpl) {
        this.professionUnitServiceImpl = professionUnitServiceImpl;
    }

    @PostMapping
    public Boolean add(ProfessionUnitBean professionUnitBean){
        return professionUnitServiceImpl.add(professionUnitBean) != null;
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return professionUnitServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(ProfessionUnitBean professionUnitBean){
        return professionUnitServiceImpl.update(professionUnitBean) != null;
    }

    @GetMapping
    public List listProfessionUnit(){
        return professionUnitServiceImpl.listProfessionUnit();
    }

    @GetMapping("all")
    public CommonResult all(@RequestParam Map<String, Object> propMap){
        return professionUnitServiceImpl.search(propMap, PageableUtils.producePageable4Map(propMap, "professionUnitId"));
    }
}
