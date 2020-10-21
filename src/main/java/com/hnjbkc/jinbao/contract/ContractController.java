package com.hnjbkc.jinbao.contract;

import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author siliqiang
 * @date 2019.8.12
 */

@RestController
@RequestMapping("contract")
public class ContractController {

    private ContractServiceImpl contractService;

    @Autowired
    public void setContractService(ContractServiceImpl contractService) {
        this.contractService = contractService;
    }

    /**
     * 合同录入的方法
     */
    @PutMapping()
    public Boolean contUpdate(ContractBean contBean, HttpServletRequest request) {
        Collection<Part> parts;
        try {
            parts = request.getParts();
        } catch (Exception e) {
            System.out.println("请求格式不对 不能读取到上传的文件");
            return false;
        }
        return contractService.add(contBean, request, parts) != null;
    }

    /**
     * 查询并且分页的方法(不模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping
    public Page getContracts(@RequestParam Map<String, Object> propMap) {
        return contractService.get(propMap, PageableUtils.producePageable4Map(propMap, "contractId"));
    }

    /**
     * 查询并且分页的方法(模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping("s")
    public Page searchContracts(@RequestParam Map<String, Object> propMap) {
        return contractService.search(propMap, PageableUtils.producePageable4Map(propMap, "contractId"));
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    @GetMapping("get_single_properties")
    public List getSingleProperties(String property) {
        return contractService.getSingleProperty(property);
    }

    @PutMapping("update_material")
    public void updateMaterial() {
        contractService.updateMaterial();
    }

//    @GetMapping("contractFile/*")
//    public void contractFilePath(HttpServletRequest request , HttpServletResponse response ) throws ServletException, IOException {
//        String requestURI = request.getRequestURI();
//        request.getServletContext().getContext("http://localhost:9090").getRequestDispatcher(
//                requestURI.substring( requestURI.indexOf("/contractFile/"))).forward(request , response );
//    }

}
