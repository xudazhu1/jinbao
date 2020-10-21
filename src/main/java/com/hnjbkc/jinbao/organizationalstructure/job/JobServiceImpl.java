package com.hnjbkc.jinbao.organizationalstructure.job;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.organizationalstructure.company.CompanyBean;
import com.hnjbkc.jinbao.organizationalstructure.department.DepartmentBean;
import com.hnjbkc.jinbao.utils.AttrExchange;
import com.hnjbkc.jinbao.utils.MyTreeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author siliqiang
 * @date 2019.7.11
 */
@SuppressWarnings("WeakerAccess")
@Service
public class JobServiceImpl implements BaseService<JobBean> {
    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    private JobDao jobDao;

    @Autowired
    public void setJobDao(JobDao jobDao) {
        this.jobDao = jobDao;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public JobBean add(JobBean jobBean) {
        return jobDao.save(jobBean);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean delete(Integer id) {
        jobDao.deleteById(id);
        return true;
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public JobBean update(JobBean jobBean) {
        jobDao.deletePermissionConnectionByRoleId(jobBean.getJobId());
        Optional<JobBean> byId = jobDao.findById(jobBean.getJobId());
        if (!byId.isPresent()) {
            return null;
        }
        JobBean saveBean = byId.get();
        AttrExchange.onAttrExchange(saveBean , jobBean);
        JobBean save ;
        try {
            save = jobDao.save(saveBean);
        } catch (Exception e) {
            return null;
        }
        return save;
    }

    /**
     * 查询并且分页的方法(精确)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    public Page<JobBean> getJob(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.getAllByCustomProps(JobBean.class, propMap, pageRequest);
    }
    /**
     * 查询并且分页的方法(模糊)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    public Page<JobBean> searchJob(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.searchAllByCustomProps(JobBean.class, propMap, pageRequest);
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    public List getSingleProperty(String property) {
        return sqlUtilsDao.getSingleProperties(JobBean.class, property);
    }



    public List<MyTreeBean> getTree(Map<String, Object> propMap, Pageable pageRequest) {
        Page<CompanyBean> companyBeans = sqlUtilsDao.getAllByCustomProps(CompanyBean.class, propMap, pageRequest);

        List<MyTreeBean> myTreeBeanList  = new ArrayList<>();

        companyBeans.getContent().forEach(companyBean -> {
            MyTreeBean companyTree = new MyTreeBean(
                    companyBean.getCompanyId(),
                    companyBean.getCompanyName(),
                    companyBean.getCompanyName(),
                    "company" ,
                    new ArrayList<>()
            );
            companyBean.getDepartmentBeans().forEach(departmentBean -> joinTree4Dept( companyTree.getSubObject() , departmentBean , false ));

            myTreeBeanList.add(companyTree);
        });

        return myTreeBeanList;

    }
    public List<MyTreeBean> getTree(Map<String, Object> propMap, Pageable pageRequest , Boolean addUser ) {
        Page<CompanyBean> companyBeans = sqlUtilsDao.getAllByCustomProps(CompanyBean.class, propMap, pageRequest);

        List<MyTreeBean> myTreeBeanList  = new ArrayList<>();

        companyBeans.getContent().forEach(companyBean -> {
            MyTreeBean companyTree = new MyTreeBean(
                    companyBean.getCompanyId(),
                    companyBean.getCompanyName(),
                    companyBean.getCompanyName(),
                    "company" ,
                    new ArrayList<>()
            );
            companyBean.getDepartmentBeans().forEach(departmentBean -> joinTree4Dept( companyTree.getSubObject() , departmentBean , addUser ));

            myTreeBeanList.add(companyTree);
        });

        return myTreeBeanList;

    }


    private void joinTree4Dept(List<MyTreeBean> deptTrees , DepartmentBean departmentBean , Boolean addUser ) {
        MyTreeBean departmentTree = new MyTreeBean(
                departmentBean.getDepartmentId() ,
                departmentBean.getDepartmentName(),
                departmentBean.getDepartmentName(),
                "department" ,
                new ArrayList<>()
        );
        List<MyTreeBean> jobTreeBeanList = departmentTree.getSubObject();
        departmentBean.getJobBeans().forEach(jobBean -> {
            MyTreeBean jobTree = new MyTreeBean(
                    jobBean.getJobId(),
                    jobBean.getJobName(),
                    jobBean.getJobName(),
                    "job" ,
                    new ArrayList<>()
            );
            if ( addUser ) {
                joinTree4Job( jobTree , jobBean );
            }

            jobTreeBeanList.add(jobTree);
        });
        deptTrees.add(departmentTree);
        if ( departmentBean.getNextDepartmentBeans() != null && departmentBean.getNextDepartmentBeans().size() > 0 ) {
            departmentBean.getNextDepartmentBeans().forEach(nextDept -> joinTree4Dept(departmentTree.getSubObject() , nextDept ,addUser ));
        }
    }
    private void joinTree4Job(MyTreeBean jobTree , JobBean jobBean1 ) {
        List<MyTreeBean> jobTreeBeanList = jobTree.getSubObject();
        jobBean1.getUserBeans().forEach(userBean -> {
            MyTreeBean userTree = new MyTreeBean(
                    userBean.getUserId(),
                    userBean.getUserName(),
                    userBean.getUserName(),
                    "user" ,
                    new ArrayList<>()
            );
            jobTreeBeanList.add(userTree);
        });
    }
}
