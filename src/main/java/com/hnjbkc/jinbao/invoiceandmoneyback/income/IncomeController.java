package com.hnjbkc.jinbao.invoiceandmoneyback.income;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author xudaz
 * @date 2019/9/5
 */
@RestController
@RequestMapping("income")
public class IncomeController {

    private IncomeServiceImpl incomeService;

    @Autowired
    public void setIncomeService(IncomeServiceImpl incomeService) {
        this.incomeService = incomeService;
    }

    @PutMapping
    public Object update(IncomeBean incomeBean  ) {
        return incomeService.add( incomeBean );
    }
    @PostMapping
    public Object add(IncomeBean incomeBean ) {
        return  update(incomeBean);
    }

//    @Autowired
//    SqlUtilsDao sqlUtilsDao;
//
//
//
//    @GetMapping("as")
//    public Object getTest() {
//        EntityGraphUtils.EntityGraphUtilsInfo[] entityGraphUtilsInfos = new EntityGraphUtils.EntityGraphUtilsInfo[1];
//        //如果只填路径 说明只加载projectBean
////        entityGraphUtilsInfos[0] = new EntityGraphUtils().new EntityGraphUtilsInfo("projectBean" );
//        entityGraphUtilsInfos[0] = new EntityGraphUtils().new EntityGraphUtilsInfo("managementBean" );
////        //黑名单模式 不放行名单里的属性 没有的全查出来
////        entityGraphUtilsInfos[0] = new EntityGraphUtils().new EntityGraphUtilsInfo("projectBean" , MyGraphIgnoreInfoType.BLACK_LIST ,
////                 new String[]{ "a" , "b" } );
////        //仅自己模式模式 拦截fieldPath和其下的级联
////        entityGraphUtilsInfos[0] = new EntityGraphUtils().new EntityGraphUtilsInfo("projectBean" , MyGraphIgnoreInfoType.ONLY_SELF ,
////                 new String[]{ "a" , "b" } );
//
//
//        //EntityGraphUtils.EntityGraphUtilsInfo[] entityGraphUtilsInfo ,
//        // Pageable pageable, String groupBy ,
//        Page<ProjectBean> projectId = sqlUtilsDao.getAllByCustomPropsCustomJoin(
//                ProjectBean.class,
//                new HashMap<>(0),
//                entityGraphUtilsInfos,
//                PageRequest.of(0, 11237897),
//                "projectId"
//        );
//        return projectId.getContent();
//    }

    @GetMapping("project_id")
    public List<Map<String, Object>> findIncomeBeanByProjectId(String projectId){
        return incomeService.findIncomeBeanByProjectId(projectId);
    }
}
