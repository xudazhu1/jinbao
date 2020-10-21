package com.hnjbkc.jinbao.datum;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.contract.ContractBean;
import com.hnjbkc.jinbao.datum.datumfile.DatumFileBean;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.Part;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author siliqiang
 * @date 2019.8.15
 */
@Service
@Transactional(rollbackOn = IOException.class)
public class DatumServiceImpl implements BaseService<DatumBean> {

    private DatumDao datumDao;

    @Autowired
    public void setDatumDao(DatumDao datumDao) {
        this.datumDao = datumDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Override
    public DatumBean add(DatumBean datumBean) {
        return null;
    }

    @Override
    public Boolean delete(Integer id) {
        return null;
    }

    @Override
    public DatumBean update(DatumBean datumBean) {
        return null;
    }

    /**
     * 增加的方法
     *
     * @param datumBean  前端传过来的对象封装了有,无,不需
     * @param department 需要用的的部门用来创建文件夹
     * @param itemNum    项目编号用来创建文件夹名称
     * @param parts      控制层传来的所有文件
     * @return 返回资料的对象
     */
    DatumBean add(DatumBean datumBean, String department, String itemNum, Collection<Part> parts) {
        //创建一个文件路径的集合对象
        List<DatumFileBean> datumFileBeans = new ArrayList<>();
        for (Part part : parts) {
            System.out.println(part.getSubmittedFileName()+"**********");
            System.out.println(part.getName()+"______________________________________");
            if (part.getSubmittedFileName() != null && !"".equals(part.getSubmittedFileName())) {
                String strPath = "D:/" + "/" + "资料归总" + "/" + "/" + "资料归总" + "/" + itemNum + "/" + department + "/" + part.getName().replaceAll("[*]", "") + "";
                String path = "D:/" + "/" + "资料归总" + "/" + "/" + "资料归总" + "/" + itemNum + "/" + department + "/" + part.getName().replaceAll("[*]", "") + part.getSubmittedFileName() + "";
                new File(path).exists();
                File file = new File(strPath);
                if (!file.exists()) {
                    System.out.println("创建成功");
                    file.mkdirs();
                }
                try {
                    part.write(strPath + "/" + part.getSubmittedFileName());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("保存失败路径是" + strPath);
                }
                System.out.println(part.getSubmittedFileName()+"文件路径");
                DatumFileBean datumFileBean = new DatumFileBean();
                datumFileBean.setDatumFileType(part.getName().replaceAll("[*]", ""));
                datumFileBean.setDatumFilePath(part.getSubmittedFileName());
                datumFileBean.setDatumBean(datumBean);
                datumFileBeans.add(datumFileBean);
            }
        }
        datumBean.setDatumFileBeans(datumFileBeans);
        return datumDao.save(datumBean);
    }

    /**
     * 查询并且分页的方法(不模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    public Page<DatumBean> get(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.getAllByCustomProps(DatumBean.class, propMap, pageRequest);
    }

    /**
     * 查询并且分页的方法(模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    public Page<DatumBean> search(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.searchAllByCustomProps(DatumBean.class, propMap, pageRequest);
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    List getSingleProperty(String property) {
        return sqlUtilsDao.getSingleProperties(DatumBean.class, property);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @SuppressWarnings("unused")
    public DatumBean selectById(Integer id) {
        if (id != null) {
            Optional<DatumBean> byId = datumDao.findById(id);
            DatumBean datumBean = byId.orElse(null);
            if ( datumBean != null ) {
                List<DatumFileBean> datumFileBeans = datumBean.getDatumFileBeans();
                datumFileBeans.forEach( datumFileBean -> {
                    int a = datumFileBean.getDatumFileId() + 1;
                } );
            }
            return  datumBean;
        }
        return null;
    }

    /**
     * 查询所有的方法
     * @param datumBean
     * @return
     */
    public List<DatumBean> selsectAll(DatumBean datumBean) {
        return datumDao.findAll(Example.of(datumBean));
    }
}

