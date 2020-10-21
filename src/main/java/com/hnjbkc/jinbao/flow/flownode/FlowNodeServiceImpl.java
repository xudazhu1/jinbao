package com.hnjbkc.jinbao.flow.flownode;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.flow.flownotice.FlowNoticeBean;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.organizationalstructure.company.CompanyBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserServiceImpl;
import com.hnjbkc.jinbao.utils.AttrSwop;
import com.hnjbkc.jinbao.utils.FormatJsonMap;
import com.hnjbkc.jinbao.utils.MyBeanUtils;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import javax.persistence.Table;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author xudaz
 */
@Service
public class FlowNodeServiceImpl implements BaseService<FlowNodeBean> {

    private FlowNodeDao flowNodeDao;

    @Autowired
    public void setFlowNodeDao(FlowNodeDao flowNodeDao) {
        this.flowNodeDao = flowNodeDao;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean add(FlowNodeBean flowNodeBean, Integer upFlowSubId) {
        if (upFlowSubId == null) {
            return flowNodeDao.save(flowNodeBean).getFlowNodeId() != null;
        } else {
            //查询出页面传过来的上级流程
            Optional<FlowNodeBean> byId = flowNodeDao.findById(upFlowSubId);
            if (!byId.isPresent()) {
                return false;
            }
            //并添加进 上级流程 对应的下级流程list 并保存
            /*FlowNodeBean upFlowNodeBean = byId.get();*/
            /*FlowNodeBean save = flowNodeDao.save(flowNodeBean);*/
            /*upFlowNodeBean.getFlowNodeBeans().add(save);*/
            return flowNodeDao.save(flowNodeBean) != null;
        }

    }

    @Override
    public FlowNodeBean add(FlowNodeBean flowNodeBean) {
        if (flowNodeBean.getFlowNodeId() != null) {
            return null;
        }
        return flowNodeDao.save(flowNodeBean);
    }

    @Override
    public Boolean delete(Integer id) {
        flowNodeDao.deleteById(id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FlowNodeBean update(FlowNodeBean flowNodeBean) {
        flowNodeDao.deleteJobConnectionByFlowSubId(flowNodeBean.getFlowNodeId());
        flowNodeDao.deleteRoleConnectionByFlowSubId(flowNodeBean.getFlowNodeId());
        Optional<FlowNodeBean> byId = flowNodeDao.findById(flowNodeBean.getFlowNodeId());
        if (!byId.isPresent()) {
            return null;
        }
        FlowNodeBean saveBean = byId.get();
        AttrSwop.onAttrSwop(saveBean, flowNodeBean);
        FlowNodeBean save;
        try {
            save = flowNodeDao.save(saveBean);
        } catch (Exception e) {
            return null;
        }
        return save;
    }


    private UserServiceImpl userService;

    @Autowired
    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }


    /**
     * 根据待办标识符找到当前页的待办 或者是节点 然后给回前台数据以用来展示通知人 代办人 自定义条件等功能
     * //     * @param flowNoticeTag 待办标识符 (例 : 合同的标识符是项目Id 项目的标识符是报价Id )
     *
     * @param request request
     * @return 返回能匹配的待办或者节点(有代办优先返回待办)
     */
    public List<FlowNodeBean> match(HttpServletRequest request) {

        //准备好返回的格式
        List<FlowNodeBean> reFlowSubs = new ArrayList<>();

        //准备好前台带过来的数据
        String pageTag = request.getHeader("Referer");
        pageTag = pageTag.substring(pageTag.indexOf("/pages") , pageTag.indexOf(".html") + 5 );
        UserBean userBean = (UserBean) request.getSession().getAttribute("user");
        if ( userBean == null ) {
            return   new ArrayList<>();
        }

        //拿到匹配请求页并且匹配操作人所在分公司的所有流程节点
        List<FlowNodeBean> allByEditPageBeanPermissionTag = flowNodeDao.getAllByEditPageBeanPermissionTag(pageTag);
        for (FlowNodeBean flowNodeBean : allByEditPageBeanPermissionTag) {
            for (CompanyBean companyBean : flowNodeBean.getFlowBean().getCompanyBeans()) {
                if (companyBean.getCompanyId().equals(userService.getCompanyByUser(userBean).getCompanyId())) {
                    reFlowSubs.add(flowNodeBean);
                }
            }
        }
        return reFlowSubs;

    }

     List<FlowNoticeBean> matchTODO(Integer tagId, HttpServletRequest request) {
        //准备好前台带过来的数据
        String pageTag = request.getHeader("Referer");
        pageTag = pageTag.substring(pageTag.indexOf("/pages") , pageTag.indexOf(".html") + 5 );
        UserBean userBean = (UserBean) request.getSession().getAttribute("user");
        //待办对象是自己  被代办页面是自己 待办Id是tagId

         Page<FlowNoticeBean> allByCustomProps = sqlUtilsDao.getAllByCustomProps(FlowNoticeBean.class, new HashMap<>(0),
                 PageRequest.of(0, Integer.MAX_VALUE),
                 "flowNodeMiddleBean.flowNodeNextBean.editPageBean.permissionTag='" + pageTag + "'",
                 "objectUserBean.userId=" + userBean.getUserId(),
                 "flowNoticeTag=" + tagId);
         return allByCustomProps.getContent();
     }


    /**
     * 获取该页面下所有属性key 和属性中文备注
     * (例 : showPageTag = 页面合同
     * return  项目编号 = projectBean.projectNum
     * 项目名称 = projectBean.projectName)
     *
     * @param showPageTag 页面路径
     * @return map
     */
    Map<String, String> getKeys4ShowPage(String showPageTag) {
        //找到此页面的html文件的table表名
        String table = getTableName4showPage(showPageTag);
        if (table == null) {
            return new HashMap<>(0);
        }
        //根据表名找到有此注解注解的实体bean
        Class bean4TableName = MyBeanUtils.getBean4TableName(table);
        if (bean4TableName == null) {
            return new HashMap<>(0);
        }
        //根据实体类找到所有可用字段的中文吗和英文名
        return getAllPropertiesForBean(bean4TableName, new ArrayList<>(), new HashMap<>(0), "");
    }


    private Map<String, String> getAllPropertiesForBean(Class clazz, List<Class> ignore, Map<String, String> keyNameAndKey, String prefix) {
        if (ignore.contains(clazz)) {
            return keyNameAndKey;
        }
        //防止重复循环
        ignore.add(clazz);

        //获得表名
        Table tableObject = (Table) clazz.getAnnotation(Table.class);
        String tableName = tableObject.name();

        //获取所有字段
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("set")) {
                //获取属性名
                String substring = name.substring(3);
                String propertyName = substring.substring(0, 1).toLowerCase() + substring.substring(1);
                //如果是个Bean 那么递归 //如果是属性 那么添加
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes[0].getName().endsWith("Bean")) {
                    getAllPropertiesForBean(parameterTypes[0], ignore, keyNameAndKey, prefix + propertyName + ".");
                } else if (parameterTypes[0] != Set.class && parameterTypes[0] != List.class) {
                    List<String> commentByTableAndColumn = flowNodeDao.getCommentByTableAndColumn(tableName, beanColumn2SqlColumn(propertyName));
                    keyNameAndKey.put(prefix + propertyName, commentByTableAndColumn.get(0));
                }

            }
        }
        return keyNameAndKey;

    }

    private String beanColumn2SqlColumn(String beanColumn) {
        char[] chars = beanColumn.toCharArray();
        List<Integer> needInsert = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            if ((chars[i] + "").matches("[A-Z]")) {
                chars[i] = (char) (chars[i] + 32);
                needInsert.add(0, i);
            }
        }
        StringBuilder s = new StringBuilder(new String(chars));
        for (Integer integer : needInsert) {
            s.insert(integer, "_");
        }
        return s.toString();
    }


    private String getTableName4showPage(String showPageTag) {
        //找到此页面的html文件
        File file;
        try {
            //找到table名
            file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "static");
            File file1 = new File(file.getAbsolutePath() + showPageTag);
            Document document = Jsoup.parse(file1, "UTF-8", "http://example.com/");
            Element first = document.select("table[data-table]").first();
            return first.attr("data-table");
        } catch (Exception e) {
            return null;
        }
    }

     private String getTableName4EditPage(String editPage) {
        Page<FlowNodeBean> allByCustomProps = sqlUtilsDao.getAllByCustomProps(FlowNodeBean.class, new HashMap<>(0),
                PageRequest.of(0, Integer.MAX_VALUE),
                "editPageBean.permissionTag='" + editPage + "'");
        if ( allByCustomProps.getContent().size() > 0 ) {
            return  getTableName4showPage(allByCustomProps.getContent().get(0).getShowPageBean().getPermissionTag());
        }
        return "";
    }

    String getData4FlowNode(Integer flowNodeId, String parentKey, String parentValue) {
        //根据flowNodeId找到展示页
        Page<FlowNodeBean> allByCustomProps = sqlUtilsDao.getAllByCustomProps(FlowNodeBean.class, new HashMap<>(0),
                PageRequest.of(0, Integer.MAX_VALUE),
                "flowNodeId=" + flowNodeId);
        FlowNodeBean flowNodeBean = allByCustomProps.getContent().get(0);
        assert flowNodeBean != null;
        //找到此页面的html文件的table表名
        String table = getTableName4showPage(flowNodeBean.getShowPageBean().getPermissionTag());
        Class bean4TableName = MyBeanUtils.getBean4TableName(table);

        //找到级联路径的最里层bean
        String beanPath = parentKey.substring(0, parentKey.lastIndexOf("."));
        Class class4CascadePath = getClass4CascadePath(bean4TableName, beanPath);
        //通过此bean找到数据库数据
        //noinspection unchecked
        Page allByCustomProps1 = sqlUtilsDao.getAllByCustomProps(class4CascadePath, new HashMap<>(0),
                PageRequest.of(0, Integer.MAX_VALUE),
                parentKey.substring(parentKey.lastIndexOf(".") + 1) + "=" + parentValue);
        Object o = allByCustomProps1.getContent().get(0);
        String splitTag = ".";
        JSONObject jsonObjectTemp = new JSONObject();
        JSONObject jsonObjectBefore = jsonObjectTemp;
        String[] split = beanPath.split(splitTag);
        for (int i = 0; i < split.length; i++) {
            if (i == split.length - 1 && i == 0) {
                jsonObjectTemp.put(split[i], o);
            }
            if (i == split.length - 1) {
                jsonObjectBefore.put(split[i], o);
            } else {
                JSONObject jsonObjectBeforeTemp = new JSONObject();
                jsonObjectBefore.put(split[i], jsonObjectBeforeTemp);
                jsonObjectBefore = jsonObjectBeforeTemp;
            }
        }
        return JSONObject.fromObject(jsonObjectTemp, FormatJsonMap.jsonConfig).toString();

    }


    private Class getClass4CascadePath(Class thisClass, String cascadePath) {
        try {
            Class classTemp = thisClass;
            String splitTag = ".";
            String[] split = cascadePath.split(splitTag);
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                s = s.substring(0, 1).toUpperCase() + s.substring(1);
                @SuppressWarnings("unchecked")
                Method method = classTemp.getMethod("get" + s);
                if (i == split.length - 1) {
                    return method.getReturnType();
                } else {
                    classTemp = method.getReturnType();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }


     Object getDataByIdAndReferer(Integer id, HttpServletRequest request) {
        String pageTag = request.getHeader("Referer");
        pageTag = pageTag.substring(pageTag.indexOf("/pages") , pageTag.indexOf(".html") + 5 );
         String tableName4EditPage = request.getParameter("table");

         if ( tableName4EditPage == null ) {
             tableName4EditPage = getTableName4EditPage(pageTag);
         }
        Class bean4TableName = MyBeanUtils.getBean4TableName(tableName4EditPage);
        @SuppressWarnings("unchecked")
        Page allByCustomProps = sqlUtilsDao.getAllByCustomProps(bean4TableName, new HashMap<>(0),
                PageRequest.of(0, 1),
                "id=" + id);
        if ( allByCustomProps.getContent().size() > 0 ) {
            return allByCustomProps.getContent().get(0);
        }
        return null;
    }
}
