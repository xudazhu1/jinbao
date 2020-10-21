package com.hnjbkc.jinbao.workload.profession;

import com.hnjbkc.jinbao.common.CommonResult;
import com.hnjbkc.jinbao.hqldao.ManyAndOneToOneAndOne;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.utils.AttrExchange;
import com.hnjbkc.jinbao.workload.captain.CaptainBean;
import com.hnjbkc.jinbao.workload.post.PostBean;
import com.hnjbkc.jinbao.workload.staff.StaffBean;
import com.hnjbkc.jinbao.workload.supervisor.SupervisorBean;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.hnjbkc.jinbao.base.BaseService;

import java.util.*;

/**
 * @author 12
 * @Date 2019-08-27
 */
@Service
public class ProfessionServiceImpl implements BaseService<ProfessionBean> {

    private ProfessionDao professionDao;


    private ManyAndOneToOneAndOne manyAndOneToOneAndOne;

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Autowired
    public void setManyAndOneToOneAndOne(ManyAndOneToOneAndOne manyAndOneToOneAndOne) {
        this.manyAndOneToOneAndOne = manyAndOneToOneAndOne;
    }

    @Autowired
    public void setProfessionDao(ProfessionDao professionDao ) {
        this.professionDao  = professionDao;
    }

    @Override
    public  ProfessionBean add(ProfessionBean professionBean){
        return null;
    }
    public  CommonResult addProfessionBean(ProfessionBean professionBean){
        List<StaffBean> staffBeanList = professionBean.getStaffBeanList();
        List<SupervisorBean> supervisorBeanList = professionBean.getSupervisorBeanList();
        List<CaptainBean> captainBeanList = professionBean.getCaptainBeanList();
        for (StaffBean staffBean : staffBeanList) {
            staffBean.setProfessionBean(professionBean);
        }
        for (SupervisorBean supervisorBean : supervisorBeanList) {
            supervisorBean.setProfessionBean(professionBean);
        }
        for (CaptainBean captainBean : captainBeanList) {
            captainBean.setProfessionBean(professionBean);
        }
        return CommonResult.success(professionDao.save(professionBean));
    }

    @Override
    public Boolean delete(Integer id){
        professionDao.deleteById(id);
        return true;
    }

    @Override
    public ProfessionBean update(ProfessionBean professionBean){
        List<StaffBean> staffBeanList = professionBean.getStaffBeanList();

        Optional<ProfessionBean> byId = professionDao.findById(professionBean.getProfessionId());
        if(byId.isPresent()){
            ProfessionBean professionDbBean = byId.get();
            List<String> list = new ArrayList<>();
            list.add("staffPrice");
            list.add("supervisorPrice");
            list.add("captainPrice");
            AttrExchange.onAttrExchange(professionDbBean,professionBean,list);
            return professionDao.save(professionDbBean);
        }
        return null;
    }

    public List<ProfessionBean> findProfessionBean(ProfessionBean professionBean) {
        List<ProfessionBean> professionId = professionDao.findAll(Example.of(professionBean), Sort.by(Sort.Order.desc("professionId")));
        List<PostBean> objects = new ArrayList<>();
        for (ProfessionBean bean : professionId) {
            List<StaffBean> staffBeanList = bean.getStaffBeanList();
            for (StaffBean staffBean : staffBeanList) {
                PostBean postBean = staffBean.getPostBean();
                objects.add(postBean);
            }
            List<SupervisorBean> supervisorBeanList = bean.getSupervisorBeanList();
            for (SupervisorBean supervisorBean : supervisorBeanList) {
                PostBean postBean = supervisorBean.getPostBean();
                objects.add(postBean);
            }
            List<CaptainBean> captainBeanList = bean.getCaptainBeanList();
            for (CaptainBean captainBean : captainBeanList) {
                PostBean postBean = captainBean.getPostBean();
                objects.add(postBean);
            }
        }
        try {
            manyAndOneToOneAndOne.getCascades(objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return professionId;
    }

    /**
     * 工种 动态表格
     * @param paramMap 参数
     * @param pageRequest 分页
     * @return CommonResult
     */
    public CommonResult findProfessionTable(Map<String, Object> paramMap, Pageable pageRequest) {
        Object btnId = paramMap.remove("btnId");
        Page<ProfessionBean> professionBeanPage = sqlUtilsDao.searchAllByCustomProps(ProfessionBean.class, paramMap, pageRequest);
        List<ProfessionBean> content = professionBeanPage.getContent();
        //最外侧map cols表头 dataList数据 total总数
        Map<String,Object> map = new HashMap<>(2);

        int staffCount = 0;
        int superviseCount = 0;
        int captain = 0;
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<Map<String,Object>> row = new ArrayList<>();
        List<Integer> postList = new ArrayList<>();
        for (ProfessionBean bean1 : content) {
            List<StaffBean> staffBeanList1 = bean1.getStaffBeanList();
            for (StaffBean staffBean1 : staffBeanList1) {
                PostBean postBean = staffBean1.getPostBean();
                if (postBean != null){
                    if(!postList.contains(postBean.getPostId())){
                        Map<String,Object> line = new HashMap<>(2);
                        line.put("field",postBean.getPostLevel() + postBean.getPostName() + postBean.getPostId());
                        line.put("title",postBean.getPostName());
                        row.add(line);
                        postList.add(postBean.getPostId());
                        staffCount++;
                    }
                }
            }
        }
        for (ProfessionBean bean2 : content){
            List<SupervisorBean> supervisorBeanList2 = bean2.getSupervisorBeanList();
            for (SupervisorBean supervisorBean2 : supervisorBeanList2) {
                PostBean postBean = supervisorBean2.getPostBean();
                if (postBean != null){
                    if(!postList.contains(postBean.getPostId())){
                        Map<String,Object> line = new HashMap<>(2);
                        line.put("field",postBean.getPostLevel() + postBean.getPostName() + postBean.getPostId());
                        line.put("title",postBean.getPostName());
                        row.add(line);
                        postList.add(postBean.getPostId());
                        superviseCount++;
                    }
                }
            }
        }
        for (ProfessionBean bean3 : content) {
            List<CaptainBean> captainBeanList3 = bean3.getCaptainBeanList();
            for (CaptainBean captainBean3 : captainBeanList3) {
                PostBean postBean = captainBean3.getPostBean();
                if (postBean != null){
                    if(!postList.contains(postBean.getPostId())){
                        Map<String,Object> line = new HashMap<>(2);
                        line.put("field",postBean.getPostLevel() + postBean.getPostName() + postBean.getPostId());
                        line.put("title",postBean.getPostName());
                        row.add(line);
                        postList.add(postBean.getPostId());
                        captain++;
                    }

                }
            }
        }
        for (ProfessionBean professionBean : content) {

            Map<String,Object> professionMap = new HashMap<>(5);


            List<StaffBean> staffBeanList = professionBean.getStaffBeanList();
            for (StaffBean staffBean : staffBeanList) {
                PostBean postBean = staffBean.getPostBean();
                if (postBean != null){
                 professionMap.put(postBean.getPostLevel() + postBean.getPostName() + postBean.getPostId(),staffBean.getStaffPrice());
                }
            }

            List<SupervisorBean> supervisorBeanList = professionBean.getSupervisorBeanList();
            for (SupervisorBean supervisorBean : supervisorBeanList) {
                PostBean postBean = supervisorBean.getPostBean();
                if (postBean != null){
                    professionMap.put(postBean.getPostLevel() + postBean.getPostName() + postBean.getPostId(),supervisorBean.getSupervisorPrice());
                }
            }

            List<CaptainBean> captainBeanList = professionBean.getCaptainBeanList();
            for (CaptainBean captainBean : captainBeanList) {
                PostBean postBean = captainBean.getPostBean();
                if (postBean != null){
                    professionMap.put(postBean.getPostLevel() + postBean.getPostName() + postBean.getPostId(),captainBean.getCaptainPrice());
                }
            }
            professionMap.put("professionId",professionBean.getProfessionId());
            professionMap.put("professionName",professionBean.getProfessionName());
            dataList.add(professionMap);
        }
        //添加 cols
        List<Map<String,Object>> cols = new ArrayList<>();
        Map<String,Object> map1 = new HashMap<>(5);
        map1.put("field", "professionName");
        map1.put("rowspan", 2);
        map1.put("title", "工种类型");
        map1.put("align", "center");
        cols.add(map1);

        if(staffCount != 0){
            Map<String,Object> map2 = new HashMap<>(4);
            map2.put("align", "center");
            map2.put("colspan", staffCount);
            map2.put("title", "员工提成单价");
            if(staffCount == 1){
                map2.put("colGroup",1);
            }
            cols.add(map2);
        }

        if(superviseCount != 0){
            Map<String,Object> map3 = new HashMap<>(4);
            map3.put("colspan", superviseCount);
            map3.put("title", "主管提成比例");
            map3.put("align", "center");
            if(superviseCount == 1){
                map3.put("colGroup",1);
            }
            cols.add(map3);
        }

        if(captain != 0){
            Map<String,Object> map4 = new HashMap<>(4);
            map4.put("colspan", captain);
            map4.put("title", "队长提成比例");
            map4.put("align", "center");
            if(captain == 1){
                map4.put("colGroup",1);
            }
            cols.add(map4);
        }

        Map<String,Object> map5 = new HashMap<>(4);
        map5.put("toolbar", btnId);
        map5.put("rowspan", 2);
        map5.put("title", "操作");
        map5.put("align", "center");
        cols.add(map5);

        map.put("cols",cols);
        map.put("row",row);
        map.put("dataList",dataList);
        map.put("total",professionBeanPage.getTotalElements());
        return CommonResult.success(map);
    }

    public CommonResult findProfessionById(Integer id) {
        Optional<ProfessionBean> byId = professionDao.findById(id);
        if(byId.isPresent()){
            ProfessionBean professionBean = byId.get();
           return CommonResult.success(professionBean);
        }
        return CommonResult.failed();
    }
}
