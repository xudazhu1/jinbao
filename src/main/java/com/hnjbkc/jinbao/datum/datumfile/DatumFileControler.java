package com.hnjbkc.jinbao.datum.datumfile;

import com.hnjbkc.jinbao.utils.FormatJsonMap;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author siliqiang
 * @date 2019.8.15
 */
@RestController
@RequestMapping("datum_file")
public class DatumFileControler {
    private DatumFileServiceImpl datumFileService;

    @Autowired
    public void setDatumFileService(DatumFileServiceImpl datumFileService) {
        this.datumFileService = datumFileService;
    }

    @GetMapping()
    public String selectPath(String path, String name) {
        DatumFileBean datumFileBean = new DatumFileBean();
        datumFileBean.setDatumFilePath(path);
        datumFileBean.setDatumFileType(name);
        Boolean select = datumFileService.select(datumFileBean);
        System.out.println(select);
        JSONObject format = FormatJsonMap.format(null, 1, select);
        return format.toString();
    }

}
