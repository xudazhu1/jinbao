package com.hnjbkc.jinbao.flow.aop;

import com.hnjbkc.jinbao.flow.flownode.flownodemiddle.FlowNodeMiddleBean;
import com.hnjbkc.jinbao.flow.flownotice.FlowNoticeBean;
import com.hnjbkc.jinbao.flow.flownotice.FlowNoticeDao;
import com.hnjbkc.jinbao.flow.flownode.FlowNodeBean;
import com.hnjbkc.jinbao.flow.flownode.FlowNodeDao;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


/**
 * 为提交的数据自动产生通知
 *
 * @author xudaz
 * @date 2019/8/12
 */
@Aspect
@Component
public class CreateNotice {


    /**
     * 指定切点
     * 匹配service的所有add方法
     */
    @Pointcut(value = "execution(* com.hnjbkc.jinbao..*ServiceImpl.*add(..))")
    public void add() {
    }

    /**
     * 指定切点
     * 匹配service的所有update方法
     */
    @Pointcut(value = "execution(* com.hnjbkc.jinbao..*ServiceImpl.*update(..))")
    public void update() {
    }

    private FlowNodeDao flowNodeDao;

    @Autowired
    public void setFlowNodeDao(FlowNodeDao flowNodeDao) {
        this.flowNodeDao = flowNodeDao;
    }


    @AfterReturning(returning = "reObject", pointcut = "update() || add()")
    public void addOrUpdate(Object reObject) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert servletRequestAttributes != null;
        HttpServletRequest request = servletRequestAttributes.getRequest();

        UserBean userBean = (UserBean) request.getSession().getAttribute("user");
        //如果没有登陆
        if ( userBean == null ) {
            return;
        }

        //先判断是否有节点在此页面
        String pageTag = request.getHeader("Referer");
        pageTag = pageTag.substring(pageTag.indexOf("/pages") , pageTag.indexOf(".html") + 5 );

        List<FlowNodeBean> allByEditPageBeanPermissionTag = flowNodeDao.getAllByEditPageBeanPermissionTag(pageTag);
        //如果没有 结束方法
        if (allByEditPageBeanPermissionTag.size() == 0) {
            return;
        }

        //拿到前台所选择的流程节点以及下个节点所对应的通知|待办对象
        //拿到所选择的本页节点
        String thisFlowNodeId = request.getParameter("thisFlowNodeId");
        //本待办(节点通知)中间表Id
        String thisFlowNoticeMiddleId = request.getParameter("thisFlowNoticeId");
        //被通知人Id []
        String[] noticeObjectUserIds = request.getParameterValues("noticeObjectUserIds");
        //下节点封装数据 []
        String[] nextToDoData = request.getParameterValues("nextToDoData");

        //如果没有本流程Id 方法结束
        if (thisFlowNodeId == null || "".equals(thisFlowNodeId)) {
            return;
        }

        //如果没有返回啥或者返回false 不执行流程操作
        if (reObject == null) {
            return;
        }


        //获取本流程节点的标识符路径
        //根据返回值的Bean和标识符路径查找到标识符
        Integer tagId = getTagId(reObject);
        assert tagId != null;

        //完成本流程节点的待办(如果有)
        if ( thisFlowNoticeMiddleId != null ) {
            finishThis(Integer.parseInt(thisFlowNoticeMiddleId));
        }

        //根据本节点Id 被通知人ids 生成通知
        if ( noticeObjectUserIds != null ) {
            createNotices(tagId, Integer.parseInt(thisFlowNodeId), noticeObjectUserIds, userBean);
        }

        //根据本封装的待办人ids下节点id 生成待办数据
        try {
            createTodoData(tagId, nextToDoData, userBean);
        } catch ( Exception e ) {
            System.out.println("生成待办失败 , 代办人可能为空");
        }

    }


    private FlowNoticeDao flowNoticeDao;

    @Autowired
    public void setFlowNoticeDao(FlowNoticeDao flowNoticeDao) {
        this.flowNoticeDao = flowNoticeDao;
    }

    private void createTodoData(Integer tagId , String[] nextToDoData, UserBean userBean) {

        for (String nextToDoDatum : nextToDoData) {
            String[] s = nextToDoDatum.split("[$]");
            //拿到next节点走向id
            FlowNodeMiddleBean nextFlowNodeMiddleBean = new FlowNodeMiddleBean();
            nextFlowNodeMiddleBean.setFlowNodeMiddleId(Integer.parseInt(s[0]));
            //拿到此节点每个被代办人
            String splitStr = "&";
            for (String s1 : s[1].split(splitStr)) {
                Integer todoUserId = Integer.parseInt(s1);

                FlowNoticeBean flowNoticeBean = new FlowNoticeBean();
                flowNoticeBean.setFlowNoticeStatus("待办");
                flowNoticeBean.setFlowNodeMiddleBean(nextFlowNodeMiddleBean);
                flowNoticeBean.setFlowNoticeContent("待办测试 待拼接");
                flowNoticeBean.setFlowNoticeTag(tagId);
                flowNoticeBean.setFlowNoticeTime(new Date());
                flowNoticeBean.setUserBean(userBean);
                UserBean objectUserBean = new UserBean();
                objectUserBean.setUserId(todoUserId);
                flowNoticeBean.setObjectUserBean(objectUserBean);
                flowNoticeDao.save(flowNoticeBean);
            }
        }
    }

    /**
     * 根据本节点Id 被通知人ids 生成通知
     * @param tagId 标识符Id
     * @param thisFlowNode 本节点Id
     * @param noticeObjectUserIds 通知对象userId
     * @param userBean 发起人
     */
    private void createNotices(Integer tagId, Integer thisFlowNode ,  String[] noticeObjectUserIds, UserBean userBean) {

        for (String noticeObjectUserId : noticeObjectUserIds) {
            FlowNoticeBean flowNoticeBean = new FlowNoticeBean();
            flowNoticeBean.setFlowNoticeTag(tagId);
            FlowNodeBean flowNodeBean = new FlowNodeBean();
            flowNodeBean.setFlowNodeId(thisFlowNode);
            flowNoticeBean.setFlowNodeBean(flowNodeBean);
            flowNoticeBean.setFlowNoticeStatus("未读");
            flowNoticeBean.setFlowNoticeTime(new Date());
            flowNoticeBean.setFlowNoticeContent("通知测试 待拼接");
            //设置发起人
            flowNoticeBean.setUserBean(userBean);
            //生成每条通知对象
            UserBean noticeObjectUserBean = new UserBean();
            Integer noticeObjectUser = Integer.parseInt(noticeObjectUserId);
            noticeObjectUserBean.setUserId(noticeObjectUser);
            flowNoticeBean.setObjectUserBean(noticeObjectUserBean);
            flowNoticeDao.save(flowNoticeBean);
        }

    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    /**
     * //完成本流程节点的待办(如果有)
     *
     * @param flowNoticeId 本待办(节点通知)中间表Id
     */
    private void finishThis(Integer flowNoticeId) {
        Optional<FlowNoticeBean> byId = flowNoticeDao.findById(flowNoticeId);
        assert byId.isPresent();
        FlowNoticeBean myFlowNoticeMiddleBean = byId.get();
        // 找到该通知所属走向相同 tagId相同的所有代办 设为已办
        Page<FlowNoticeBean> allByCustomProps = sqlUtilsDao.getAllByCustomProps(FlowNoticeBean.class, new HashMap<>(0),
                PageRequest.of(0, Integer.MAX_VALUE),
                "flowNodeMiddleBean.flowNodeMiddleId=" + myFlowNoticeMiddleBean.getFlowNodeMiddleBean().getFlowNodeMiddleId(),
                "flowNoticeTag=" + myFlowNoticeMiddleBean.getFlowNoticeTag()
        );
        //如果通知id相同 被通知流程相同 全部设为已办
        allByCustomProps.getContent().forEach(flowNoticeBean -> {
            flowNoticeBean.setFlowNoticeStatus("已办");
            flowNoticeDao.save(flowNoticeBean);
        });

    }

    private Integer getTagId(Object reObject)  {
        Field[] fields = reObject.getClass().getDeclaredFields();
        for (Field field : fields) {
            Id annotation = field.getAnnotation(Id.class);
            if ( annotation != null ) {
                try {
                    String name = field.getName();
                    name = name.substring(0 , 1).toUpperCase() + name.substring(1);
                    Method method = reObject.getClass().getMethod("get" + name);
                    return (Integer) method.invoke(reObject);
                } catch (Exception e ) {
                    return null;
                }
            }
        }
        return  null;

    }
}
