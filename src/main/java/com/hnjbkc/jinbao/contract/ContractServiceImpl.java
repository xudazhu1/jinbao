package com.hnjbkc.jinbao.contract;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.contract.departmentmoney.ContractDepartmentMoneyDao;
import com.hnjbkc.jinbao.contract.material.MaterialBean;
import com.hnjbkc.jinbao.contract.material.MaterialDao;
import com.hnjbkc.jinbao.hqldao.ManyAndOneToOneAndOne;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.project.ProjectBean;
import com.hnjbkc.jinbao.utils.AttrExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author siliqiang
 * @date 2019.8.12
 */
@Service
public class ContractServiceImpl implements BaseService<ContractBean> {
    private ContractDao contractDao;

    @Autowired
    public void setContractDao(ContractDao contractDao) {
        this.contractDao = contractDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    private MaterialDao materialDao;

    @Autowired
    public void setMaterialDao(MaterialDao materialDao) {
        this.materialDao = materialDao;
    }

    private ContractDepartmentMoneyDao contractDepartmentMoneyDao;

    @Autowired
    public void setContractDepartmentMoneyDao(ContractDepartmentMoneyDao contractDepartmentMoneyDao) {
        this.contractDepartmentMoneyDao = contractDepartmentMoneyDao;
    }

    @Override
    public ContractBean add(ContractBean contractBean) {
        return null;
    }

    @Override
    public Boolean delete(Integer id) {
        return null;
    }

    @Override
    public ContractBean update(ContractBean contractBean) {
        return null;
    }

    @Transactional(rollbackOn = Exception.class)
    ContractBean add(ContractBean contractBean, HttpServletRequest request, Collection<Part> parts) {
        Integer contractId = contractBean.getContractId();
        //执行跟新操作的时候删除之前的部门分配金额
        contractDepartmentMoneyDao.deleteDepartmentMoney(contractId);

        String delFileIds = request.getParameter("del_file_ids");
        if (delFileIds != null) {
            String split = "[$]";
            for (String s : delFileIds.split(split)) {
                try {
                    materialDao.deleteById(Integer.parseInt(s));
                } catch (Exception e) {
                    System.out.println("删除扫描件失败");
                }
            }
        }

        //拿到扫描件的集合对象
        List<MaterialBean> upload = upload(parts, request);
        //合同对象里面保存扫描件
        contractBean.setMaterialBeans(upload);

        //给部门分配金额添加合同的外键
        contractBean.getContractDepartmentMoneyBeans().forEach(contractDepartmentMoneyBean -> contractDepartmentMoneyBean.setContractBean(contractBean));
        //如果没有这个合同则放回null
        if (contractBean.getContractId() == null) {
            return null;
        }
        Optional<ContractBean> byId = contractDao.findById(contractBean.getContractId());
        if (!byId.isPresent()) {
            return null;
        }
        //创建时间
        Date date = new Date();
        //给合同添加创建时间
        if (contractBean.getContractCreateTime() == null) {
            contractBean.setContractCreateTime(date);
        }
        ContractBean saveBean = byId.get();
        AttrExchange.onAttrExchange(saveBean, contractBean);
        if (saveBean.getMaterialBeans()!=null&&saveBean.getMaterialBeans().size()>0){
            saveBean.setContractScannedExists("有");
        }else {
            saveBean.setContractScannedExists("无");
        }
        return contractDao.save(saveBean);
    }

    /**
     * 查询并且分页的方法(不模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    public Page<ContractBean> get(Map<String, Object> propMap, Pageable pageRequest) {
        Page<ContractBean> contractBeans = sqlUtilsDao.getAllByCustomProps(ContractBean.class, propMap, pageRequest);
        List<ProjectBean> projectBeans = new ArrayList<>();
        for (ContractBean contractBean : contractBeans.getContent()) {
            projectBeans.add(contractBean.getProjectBean());
        }
        try {
            manyAndOneToOneAndOne.getCascades(projectBeans);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contractBeans;

    }

    private ManyAndOneToOneAndOne manyAndOneToOneAndOne;

    @Autowired
    public void setManyAndOneToOneAndOne(ManyAndOneToOneAndOne manyAndOneToOneAndOne) {
        this.manyAndOneToOneAndOne = manyAndOneToOneAndOne;
    }

    /**
     * 查询并且分页的方法(模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    public Page<ContractBean> search(Map<String, Object> propMap, Pageable pageRequest) {
        Page<ContractBean> contractBeans = sqlUtilsDao.searchAllByCustomProps(ContractBean.class, propMap, pageRequest);
        List<ProjectBean> projectBeans = new ArrayList<>();
        for (ContractBean contractBean : contractBeans.getContent()) {
            projectBeans.add(contractBean.getProjectBean());
        }
        try {
            manyAndOneToOneAndOne.getCascades(projectBeans);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contractBeans;
    }

    /**
     * 上传图片的方法
     *
     * @param parts   文件
     * @param request HttpServletRequest
     * @return 图片对象的集合
     */
    public List<MaterialBean> upload(Collection<Part> parts, HttpServletRequest request) {
        List<MaterialBean> materialBeans = new ArrayList<>();
        for (Part part : parts) {
            if (part.getSubmittedFileName() != null && !"".equals(part.getSubmittedFileName())) {
                MaterialBean materialBean = new MaterialBean();
                //获取上传的文件名称
                System.out.println(part.getSubmittedFileName() + "文件名称");
                String oldFileName = part.getSubmittedFileName();
                //截取文件名称的后缀
                assert oldFileName != null;
                String suffixName = oldFileName.substring(oldFileName.lastIndexOf("."));
                /*上传后的路径*/
                String filePath = "D://contractFile";
                System.out.println("filePath" + filePath);
                File file = new File(filePath);
                if (!file.exists()) {
                    boolean mkdirs = file.mkdirs();
                    if (mkdirs) {
                        System.out.println("首次创建文件夹成功");
                    }
                }
                /*新文件名*/
                String newFileName = oldFileName + "" + UUID.randomUUID() + suffixName;
                File dest = new File(filePath + "/" + newFileName);
                System.out.println(dest + "******");
                String s = dest.toString();
                try {
                    part.write(s);
                    materialBean.setContractAccessoryNewName(newFileName);
                    materialBean.setContractAccessoryOldName(oldFileName);
                    materialBeans.add(materialBean);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return materialBeans;
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    List getSingleProperty(String property) {
        return sqlUtilsDao.getSingleProperties(ProjectBean.class, property);
    }

    public void updateMaterial() {
        contractDao.updateMaterialNo();
        contractDao.updateMaterial();
    }

}
