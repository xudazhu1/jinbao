package com.hnjbkc.jinbao.squadgroupfee;

import com.hnjbkc.jinbao.squadgroupfee.squadgroupfeestatus.SquadGroupFeeStatusBean;
import com.hnjbkc.jinbao.utils.layuisoultable.SoulPage;
import com.hnjbkc.jinbao.utils.layuisoultable.SoulUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author siliqiang
 * @date 2019/11/26
 */
@RestController
@RequestMapping("squad_group_fee")
public class SquadGroupFeeController {
    private SquadGroupFeeServiceImpl squadGroupFeeService;

    @Autowired
    public void setSquadGroupFeeService(SquadGroupFeeServiceImpl squadGroupFeeService) {
        this.squadGroupFeeService = squadGroupFeeService;
    }

    @PutMapping
    public SquadGroupFeeBean put(SquadGroupFeeBean squadGroupFeeBean){
        return squadGroupFeeService.add(squadGroupFeeBean);
    }

    @PostMapping
    public SquadGroupFeeBean add(SquadGroupFeeBean squadGroupFeeBean){
        return put(squadGroupFeeBean);
    }

    @GetMapping("amount")
    public Map<String, Object> getSquadGroupFee(@RequestParam Map<String, Object> propMap){
        return squadGroupFeeService.getSquadGroupFee(propMap);
    }

    /**
     * 生产费 班组费总计
     */
    @GetMapping("team_fee")
    public Object getTeamFee(SoulPage soulPage, String filterSos){
        SoulUtils.addFilterSo( soulPage , filterSos );
        return squadGroupFeeService.getTeamFee(soulPage);
    }

    /**
     * 生产费 班组费详情
     */
    @GetMapping("team_fee_detail")
    public Object getTeamFeeDetail(SoulPage soulPage, String filterSos){
        SoulUtils.addFilterSo( soulPage , filterSos );
        return squadGroupFeeService.getTeamFeeDetail(soulPage);
    }

    /**
     * 生产费 班组费详情
     */
    @GetMapping("team_fee_cost")
    public Object getTeamFeeCost(@RequestParam Integer squadGroupFeeId, @RequestParam Integer implementId){
        return squadGroupFeeService.getTeamFeeCost(squadGroupFeeId, implementId);
    }

    /**
     * 生产费 班组费支付模块详情
     */
    @GetMapping("team_fee_pay")
    public Object getTeamFeePay(@RequestParam Integer squadGroupFeeId, @RequestParam Integer implementId){
        return squadGroupFeeService.getTeamFeePay(squadGroupFeeId, implementId);
    }
}
