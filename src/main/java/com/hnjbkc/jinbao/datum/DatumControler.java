package com.hnjbkc.jinbao.datum;

import com.hnjbkc.jinbao.utils.FormatJsonMap;
import com.hnjbkc.jinbao.utils.PageableUtils;
import net.sf.json.JSONObject;
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
 * @date 2019.8.15
 */
@RestController
@RequestMapping("datum")
public class DatumControler {

    private DatumServiceImpl datumService;

    @Autowired
    public void setDatumService(DatumServiceImpl datumService) {
        this.datumService = datumService;
    }

    @PostMapping
    public Boolean addDatum(DatumBean datumBean, @RequestParam("department") String department, @RequestParam("datum_num") String itemNum, HttpServletRequest request) {
        Collection<Part> parts;
        try {
            parts = request.getParts();
        } catch (Exception e) {
            System.out.println("请求格式不对 不能读取到上传的文件");
            return false;
        }
        return datumService.add(datumBean, department, itemNum, parts) != null;
    }

    /**
     * 查询并且分页的方法(不模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping
    public Page getDatum(@RequestParam Map<String, Object> propMap) {
        return datumService.get(propMap, PageableUtils.producePageable4Map(propMap, "datumId"));
    }

    /**
     * 查询并且分页的方法(模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping("s")
    public Page searchDatum(@RequestParam Map<String, Object> propMap) {
        return datumService.search(propMap, PageableUtils.producePageable4Map(propMap, "datumId"));
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    @GetMapping("get_single_properties")
    public List getSingleProperties(String property) {
        return datumService.getSingleProperty(property);
    }

    @GetMapping("find_By_Id")
    public DatumBean selectById(Integer id) {
        return datumService.selectById(id);
    }

    @GetMapping("findAll")
    public List selectAll(DatumBean datumBean) {
       return datumService.selsectAll(datumBean);
    }
}
