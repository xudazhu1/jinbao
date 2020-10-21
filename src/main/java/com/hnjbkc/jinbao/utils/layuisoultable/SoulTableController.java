package com.hnjbkc.jinbao.utils.layuisoultable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 为soulTable 创建的公共类
 * @author xudaz
 */
@RestController
@RequestMapping("soulTable")
public class SoulTableController {

    private SoulTableService soulTableService;

    @Autowired
    public void setSoulTableService(SoulTableService soulTableService) {
        this.soulTableService = soulTableService;
    }

    @PostMapping
    public Object get4layUiTable(SoulPage soulPage , String filterSos ) throws Exception {
        return soulTableService.get4layUiTable( soulPage , filterSos  );
    }

}
