package com.hnjbkc.jinbao.workload.profession;

import com.hnjbkc.jinbao.common.CommonResult;
import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 12
 * @Date 2019-08-27
 */
@RestController
@RequestMapping("profession")
public class ProfessionController {

    private ProfessionServiceImpl professionServiceImpl;

    @Autowired
    public void setProfessionServiceImpl(ProfessionServiceImpl professionServiceImpl) {
        this.professionServiceImpl = professionServiceImpl;
    }

    @PostMapping
    public CommonResult add(ProfessionBean professionBean){
        if(professionBean.getProfessionName() == null){
            return CommonResult.failed("请输入工种名称");
        }
        return professionServiceImpl.addProfessionBean(professionBean);
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return professionServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(ProfessionBean professionBean){
        return professionServiceImpl.update(professionBean) != null;
    }

    @GetMapping
    public List<ProfessionBean> findProfessionBean(ProfessionBean professionBean){
        return professionServiceImpl.findProfessionBean(professionBean);
    }

    @GetMapping("id")
    public CommonResult findProfessionById(Integer id){

        return professionServiceImpl.findProfessionById(id);
    }

    @GetMapping("table")
    public CommonResult findProfessionTable(@RequestParam Map<String,Object> propMap){
        return professionServiceImpl.findProfessionTable(propMap, PageableUtils.producePageable4Map(propMap, "professionId"));
    }


}
