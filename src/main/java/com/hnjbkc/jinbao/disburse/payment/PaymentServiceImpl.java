package com.hnjbkc.jinbao.disburse.payment;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.disburse.DisburseAutoNumUtils;
import com.hnjbkc.jinbao.disburse.DisburseBean;
import com.hnjbkc.jinbao.disburse.DisburseDao;
import com.hnjbkc.jinbao.disburse.property.PropertyServiceImpl;
import com.hnjbkc.jinbao.utils.AttrExchange;
import com.hnjbkc.jinbao.utils.tableutils.TableUtilsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaymentServiceImpl implements BaseService<DisburseBean> {

    private DisburseDao disburseDao;

    private PaymentDao paymentDao;

    @Autowired
    public void setPaymentDao(PaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }

    @Autowired
    public void setDisburseDao(DisburseDao disburseDao) {
        this.disburseDao = disburseDao;
    }

    private TableUtilsDao tableUtilsDao;

    @Autowired
    public void setTableUtilsDao(TableUtilsDao tableUtilsDao) {
        this.tableUtilsDao = tableUtilsDao;
    }

    private DisburseAutoNumUtils disburseAutoNumUtils;

    @Autowired
    public void setDisburseAutoNumUtils(DisburseAutoNumUtils disburseAutoNumUtils) {
        this.disburseAutoNumUtils = disburseAutoNumUtils;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public DisburseBean add(DisburseBean disburseBean) {
        DisburseBean updateDisburseBean = tableUtilsDao.update(disburseBean);
        if (updateDisburseBean != null ) {
            if (updateDisburseBean.getDisburseNum() == null||"" .equals(updateDisburseBean.getDisburseNum())){
                //编号 前缀 019 11 02132
                //先获取年月 再获取主键拼接为编号
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
                String format = simpleDateFormat.format(new Date());
                String year = format.substring(2, 4);
                String month = format.substring(5);
                Integer financeNum = disburseAutoNumUtils.getNum("付款申请单");
                updateDisburseBean.setDisburseNum( "FK-" + year + month + financeNum );
            }
        }
        return updateDisburseBean;
    }

    @Override
    public Boolean delete(Integer id) {
        disburseDao.deleteById(id);
        return true;
    }

    @Override
    public DisburseBean update(DisburseBean disburseBean) {
        Optional<DisburseBean> byId = disburseDao.findById(disburseBean.getDisburseId());
        if (byId.isPresent()) {
            DisburseBean disburseDbBean = byId.get();
            AttrExchange.onAttrExchange(disburseDbBean, disburseBean);
            return disburseDao.save(disburseDbBean);
        }
        return null;
    }

    public DisburseBean getDisburseBeanById(Integer id) {
        Optional<DisburseBean> byId = disburseDao.findById(id);
        if (byId.isPresent()) {
            DisburseBean disburseBean = byId.get();
            return disburseBean;
        }
        return null;
    }

    private PropertyServiceImpl propertyService;

    @Autowired
    public void setPropertyService(PropertyServiceImpl propertyService) {
        this.propertyService = propertyService;
    }

   // public Page<DisburseBean> listProperty(Map<String, Object> propMap, Pageable pageRequest) {
    public Page<DisburseBean> listProperty(String dateA,Integer pageNum , Integer pageSize ) {
       // String date = (String) propMap.remove("date");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Date date1 = new Date();
        String format1 = format.format(date1);
        if (dateA==null){
            dateA=format1;
       }
        Date parse = new Date();
        try {
            parse = format.parse(dateA);
        } catch (Exception e) {
            dateA = format.format( parse );
            try {
                parse = format.parse(dateA);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        List<DisburseBean> content = disburseDao.getProperty( (pageNum - 1) * pageSize , pageSize);
        for (DisburseBean disburseBean : content) {
            propertyService.getProper(disburseBean.getPropertyBean1(), parse);
        }
        return new PageImpl<DisburseBean>(content, PageRequest.of( pageNum - 1 , pageSize ),disburseDao.getProperty());
    }

    public Object[] getPropertyNumAndVal() {
        return  disburseDao.getPropertyNumAndVal();
    }

    /**
     * 获取 项目回款总金额 本次付款后累计项目花销付款金额
     * 常用于 付款 打印
     * @param id 支出 id
     * @return 返回map
     */
    public Map<String, Object> getProjectIncomeAndCost(Integer id, String type) {
       return Cost(id,type,0);
    }


    public Map<String, Object> Cost(Integer id, String type,int mark){
        Map<String, Object> map = new HashMap<>(2);
        Double projectCascadeIncomeMoney = paymentDao.projectCascadeIncomeMoney(id);
        if(projectCascadeIncomeMoney == null){
            projectCascadeIncomeMoney = 0.00;
        }
        map.put("incomeMoney",projectCascadeIncomeMoney);
        List<List<Object>> lists = null;
        if(mark == 0){
            lists = paymentDao.projectDisburseAll(id,type);
        }else if(mark == 1){
            lists = paymentDao.projectSubclassDisburseAll(id,type);
        }
        //获取 当前支出时间 当前金额
        Date nowDate = null;
        Double nowMoney = 0.00;
        Double paymentMoney = 0.00;
        for (List<Object> list : lists) {
            //获取当前时间和金额
            Integer o = (Integer)list.get(0);
            if(id.equals(o)){
                //
                nowDate = (Date)list.get(1);
                nowMoney = list.get(2) == null ? 0 : (Double) list.get(2);
            }
        }
        // 获取 日期是 当前 日期 之前的 并且 状态 是 已支付的
        for (List<Object> list : lists) {
            Date date = (Date)list.get(1);
            Integer status = (Integer)list.get(3);
            if(status == null){
                status = 0;
            }
            if(date == null || nowDate == null){
                continue;
            }
            if((date.getTime() < nowDate.getTime()) && status == 7){
                paymentMoney += (Double) list.get(2);
            }
        }
        paymentMoney += nowMoney;
        map.put("paymentMoney",paymentMoney);
        return map;
    }


    public Map<String, Object> getProjectSubclassIncomeAndCost(Integer id, String type) {
        return Cost(id,type,1);
    }

    /**
     * 已付班组费
     */
    public Double paidBossCostByProjectId(Integer id){
        return paymentDao.paidBossCostByProjectId(id) == null ? 0 : paymentDao.paidBossCostByProjectId(id);
    }
}
