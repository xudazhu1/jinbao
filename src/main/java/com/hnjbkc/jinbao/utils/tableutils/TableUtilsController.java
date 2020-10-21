package com.hnjbkc.jinbao.utils.tableutils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author xudaz
 */
@RestController
@RequestMapping("table_utils")
public class TableUtilsController {

    private  TableUtilsService tableUtilsService;

    @Autowired
    public void setTableUtilsService(TableUtilsService tableUtilsService) {
        this.tableUtilsService = tableUtilsService;
    }


    /**
     * table_utils.tableName=project 表名
     * table_utils.fields=projectNum$projectName 要查询的字段
     * projectId = xx
     * @param map map 条件(包括分页按钮)
     * @return r
     */
    @GetMapping
    public Page getByTableUtils(@RequestParam Map<String , Object> map , HttpServletRequest request) {
        return tableUtilsService.getByTableUtils(map , request);
    }

    /**
     * table_utils.tableName=project 表名
     * table_utils.fields=projectNum$projectName 要查询的字段
     * projectId = xx
     * @param map map 条件(包括分页按钮)
     * @return r
     */
    @GetMapping("info")
    public Page getInfo(@RequestParam Map<String , Object> map ) {
        return tableUtilsService.getInfo(map);
    }

    @RequestMapping( method = { RequestMethod.PUT , RequestMethod.POST})
    public Object addPartners(@RequestParam Map<String , Object> map ) {
        try {
            return tableUtilsService.update( (String) map.remove("tableName") );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @DeleteMapping
    public Boolean delete(String tableName , Integer id ) {
        return tableUtilsService.delete(tableName , id);
    }
    @DeleteMapping({"id" , "ids"})
    public Boolean deleteByIds(String tableName , String ids ) {
        return tableUtilsService.deleteByIds(tableName , ids);
    }

    @GetMapping("multiple_properties")
    public Object getMultipleProperties( @RequestParam Map<String , Object> map , HttpServletRequest request  ) {
        return tableUtilsService.getMultipleProperties(map , request );
    }

    @GetMapping("export")
    public void exportExcel( @RequestParam Map<String , Object> map , HttpServletResponse response ) throws Exception {

        HSSFWorkbook wb = tableUtilsService.export(map);
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment;filename=student.xls");
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }





}
