package com.hnjbkc.jinbao.workload;

import com.hnjbkc.jinbao.common.CommonResult;
import com.hnjbkc.jinbao.utils.PageableUtils;
import com.hnjbkc.jinbao.utils.layuisoultable.SoulPage;
import com.hnjbkc.jinbao.utils.layuisoultable.SoulUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 12
 * @Date 2019-08-27
 */
@RestController
@RequestMapping("work_load")
public class WorkLoadController {

    private WorkLoadServiceImpl workLoadServiceImpl;

    @Autowired
    public void setWorkLoadServiceImpl(WorkLoadServiceImpl workLoadServiceImpl) {
        this.workLoadServiceImpl = workLoadServiceImpl;
    }

    @PostMapping
    public Boolean add(WorkLoadBean workLoadBean){
        return workLoadServiceImpl.add(workLoadBean) != null;
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return workLoadServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(WorkLoadBean workLoadBean){
        return workLoadServiceImpl.update(workLoadBean) != null;
    }


    /**
     * 查询并且分页的方法(模糊)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping("s")
    public Page searchUsers(@RequestParam Map<String, Object> propMap) {
        return workLoadServiceImpl.search(propMap, PageableUtils.producePageable4Map(propMap, "workLoadId"));
    }

    /**
     * 技术提成
     */
    @GetMapping("team_fee")
    public Object getTeamFee(SoulPage soulPage, String filterSos){
        SoulUtils.addFilterSo( soulPage , filterSos );
        return workLoadServiceImpl.getTeamFee(soulPage);
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    @GetMapping("get_single_properties")
    public List getSingleProperties(String property) {
        return workLoadServiceImpl.getSingleProperty(property);
    }

    @GetMapping("work_load_id")
    public WorkLoadBean findWorkLoadBeanById(Integer id){
        return workLoadServiceImpl.findWorkLoadBeanById(id);
    }


    /**
     * 人员成本
     * @param propMap 参数
     */
    @GetMapping("personnel_commission")
    public CommonResult getPersonnelCommission(@RequestParam Map<String, Object> propMap) {
        return workLoadServiceImpl.getPersonnelCommission(propMap, PageableUtils.producePageable4Map(propMap, "workLoadId"));
    }



    @GetMapping("commission")
    public List getCommissionByProject(Integer id){
        return workLoadServiceImpl.getCommissionByProject(id);
    }

    /**
     * 生产费 班组费详情
     */
    @GetMapping("commission_list")
    public Object getCommissionList(SoulPage soulPage, String filterSos){
        SoulUtils.addFilterSo( soulPage , filterSos );
        return workLoadServiceImpl.getCommissionList(soulPage);
    }
}
