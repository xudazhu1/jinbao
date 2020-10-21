package com.hnjbkc.jinbao.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * @author xudaz
 * @date 2019/7/16
 */
@Component
@Getter
@Setter
public class MyTreeBean {

    private Integer id;
    private String name;
    private String title;
    private String type;
    private List<MyTreeBean> subObject;

    public MyTreeBean(){

    }

    public MyTreeBean(Integer id, String name, String title, List<MyTreeBean> subObject) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.subObject = subObject;
    }
    public MyTreeBean(Integer id, String name, List<MyTreeBean> subObject) {
        this.id = id;
        this.name = name;
        this.subObject = subObject;
    }
    public MyTreeBean(Integer id, String name, String title, String type ,  List<MyTreeBean> subObject) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.title = title;
        this.subObject = subObject;
    }
}
