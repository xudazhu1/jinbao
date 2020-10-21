package com.hnjbkc.jinbao.implement.implementrecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 12
 * @Date 2019-08-21
 */
@RestController
@RequestMapping("implement_record")
public class ImplementRecordController {

    private ImplementRecordServiceImpl implementRecordServiceImpl;

    @Autowired
    public void setImplementRecordServiceImpl(ImplementRecordServiceImpl implementRecordServiceImpl) {
        this.implementRecordServiceImpl = implementRecordServiceImpl;
    }

    @PostMapping
    public Boolean add(ImplementRecordBean implementRecordBean){
        return implementRecordServiceImpl.add(implementRecordBean) != null;
    }

    @DeleteMapping
    public Boolean delete(Integer id) {
        return implementRecordServiceImpl.delete(id);
    }

    @PutMapping
    public Boolean update(ImplementRecordBean implementRecordBean){
        return implementRecordServiceImpl.update(implementRecordBean) != null;
    }

}
