package com.hnjbkc.jinbao.workload.staff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 12
 * @Date 2019-09-05
 */
@RestController
@RequestMapping("staff")
public class StaffController {

    private StaffServiceImpl staffPostServiceImpl;

    @Autowired
    public void setStaffPostServiceImpl(StaffServiceImpl staffPostServiceImpl) {
        this.staffPostServiceImpl = staffPostServiceImpl;
    }

    @PostMapping
    public Boolean add(StaffBean staffBean){
        return staffPostServiceImpl.add(staffBean) != null;
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return staffPostServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(StaffBean staffBean){
        return staffPostServiceImpl.update(staffBean) != null;
    }

    @GetMapping
    public List<StaffBean> findStaffPostBeans(StaffBean staffBean){
        List<StaffBean> staffBeans = staffPostServiceImpl.findStaffPostBeans(staffBean);
        return staffBeans;
    }
}
