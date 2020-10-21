package com.hnjbkc.jinbao.disburse.property;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.disburse.DisburseBean;
import com.hnjbkc.jinbao.disburse.DisburseDao;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.utils.AttrExchange;
import com.hnjbkc.jinbao.utils.GetMonths;
import com.hnjbkc.jinbao.utils.NextMonth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author siliqiang
 * @date 2019.9.19
 */
@Service
public class PropertyServiceImpl implements BaseService<PropertyBean> {
    private PropertyDao propertyDao;

    @Autowired
    public void setPropertyDao(PropertyDao propertyDao) {
        this.propertyDao = propertyDao;
    }

    private DisburseDao disburseDao;

    @Autowired
    public void setDisburseDao(DisburseDao disburseDao) {
        this.disburseDao = disburseDao;
    }

    @Override
    public PropertyBean add(PropertyBean propertyBean) {
        getCompute(propertyBean);
        return propertyDao.save(propertyBean);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean delete(Integer id) {
        Optional<PropertyBean> byId = propertyDao.findById(id);
        PropertyBean propertyBean = byId.get();
        propertyDao.deletePropertyId(propertyBean.getPropertyId());
        propertyDao.deleteById(id);
        return true;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public PropertyBean update(PropertyBean propertyBean) {
        propertyDao.deletePropertyId(propertyBean.getPropertyId());
        getCompute(propertyBean);
        Optional<PropertyBean> byId1 = propertyDao.findById(propertyBean.getPropertyId());
        if (!byId1.isPresent()) {
            return null;
        }
        PropertyBean saveBean = byId1.get();
        AttrExchange.onAttrExchange(saveBean, propertyBean);
        PropertyBean save;
        try {
            save = propertyDao.save(saveBean);
        } catch (Exception e) {
            return null;
        }
        return save;
    }

    private void getCompute(PropertyBean propertyBean) {
        Integer disburseId = propertyBean.getDisburseBean().getDisburseId();
        Optional<DisburseBean> byId = disburseDao.findById(disburseId);
        DisburseBean disburseBean1 = byId.get();
        propertyBean.setPropertyInputTime(new Date());
        //原值
        Double propertyOriginalValue = disburseBean1.getDisbursePaymentAmount();
        //获取残值率
        Double propertyResidual = propertyBean.getPropertyResidual();
        //得到净值
        double propertyNetValue = mul(propertyOriginalValue, propertyResidual);
        //得到总共的累计折旧金额
        double residueMoney = sub(propertyOriginalValue, propertyNetValue);
        //拿到折旧的期限也就是分几年折旧
        Integer propertyDeadline = propertyBean.getPropertyDeadline();
        //得到每月的折旧费
        double propertyInstantDepreciation = div(residueMoney, Double.valueOf(propertyDeadline.intValue()), 2);
        //拿到购买日期
        Date propertyBuyTime = propertyBean.getPropertyBuyTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        for (int i = 0; i < propertyDeadline; i++) {
            DisburseBean disburseBean = new DisburseBean();
            disburseBean.setDisbursePaymentAmount(propertyInstantDepreciation);
            disburseBean.setDisburseAffiliation("资产-折旧费");
            Date nextMonth = NextMonth.getNextMonth(simpleDateFormat.format(propertyBuyTime));
            disburseBean.setDisburseTime(nextMonth);
            propertyBean.getDisburseBeans().add(disburseBean);
            propertyBuyTime = nextMonth;
            disburseBean.setPropertyBean(propertyBean);
        }
    }





    @SuppressWarnings("Duplicates")
    public void getProper(PropertyBean bean, Date date) {
        if (bean == null) {
            return;
        }
        //拿到到目前为止总共的折旧费用
        double propertyAccumulatedDepreciation;
        //拿到剩余的折旧费用
        double propertyResidueDepreciation;
        //拿到每个月折旧
        double propertyInstantDepreciation;
        //调用除法的运算得到净值
        double propertyNetValue;
        //拿到中间间隔几个月
        int months;
        Double propertyOriginalValue = bean.getDisburseBean().getDisbursePaymentAmount();
//            System.out.println(propertyOriginalValue + "原值");
        //获取残值率
        Double propertyResidual = bean.getPropertyResidual();
        //调用除法的运算得到净值
        propertyNetValue = mul(propertyOriginalValue, propertyResidual);
        //拿到折旧的期限也就是分几年折旧
        Integer propertyDeadline = bean.getPropertyDeadline();
        //得到总共的累计折旧金额
        double residueMoney = sub(propertyOriginalValue, propertyNetValue);
        //得到购买的时间
        Date propertyBuyTime = bean.getPropertyBuyTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String format2 = format.format(propertyBuyTime);
        String time = format2 + "-01";
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = new Date();
        try {
            parse = format1.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        String format3 = format1.format(date);
//        Date parse1=new Date();
//        try {
//             parse1 = format1.parse(format3);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        //拿到平均每月的折旧的费用
       double meanMoney = div(residueMoney, propertyDeadline, 2);
        //拿到中间间隔几个月
        months = GetMonths.getMonths(date, parse);
        if (date.getTime() < parse.getTime()) {
            //拿到每个月折旧
            propertyInstantDepreciation = 0.00;
            //累计折旧
            propertyAccumulatedDepreciation = 0.00;
            //剩余折旧
            propertyResidueDepreciation = 0.00;
            propertyNetValue = 0.00;
        } else if (months < propertyDeadline) {
            //拿到每个月折旧
            propertyInstantDepreciation = div(residueMoney, Double.valueOf(propertyDeadline.intValue()), 2);
            //拿到到目前为止总共的折旧费用
            propertyAccumulatedDepreciation = mul(months, propertyInstantDepreciation);
            //拿到剩余的折旧费用
            propertyResidueDepreciation = sub(residueMoney, propertyAccumulatedDepreciation);
        } else {
            propertyInstantDepreciation = 0.00;
            propertyAccumulatedDepreciation = residueMoney;
            propertyResidueDepreciation = 0.00;
        }

//        if (months < propertyDeadline) {
//            //拿到每个月折旧
//            propertyInstantDepreciation = div(residueMoney, Double.valueOf(propertyDeadline.intValue()), 2);
//            //拿到到目前为止总共的折旧费用
//            propertyAccumulatedDepreciation = mul(months, propertyInstantDepreciation);
//            //拿到剩余的折旧费用
//            propertyResidueDepreciation = sub(residueMoney, propertyAccumulatedDepreciation);
//        }else {
//            propertyInstantDepreciation = 0.00;
//            propertyAccumulatedDepreciation = residueMoney;
//            propertyResidueDepreciation = 0.00;
//        }

        //剩余所有的资产
        double residue = propertyNetValue + propertyResidueDepreciation;
        //原值
        bean.setPropertyOriginalValue(propertyOriginalValue);
        //净值
        bean.setPropertyNetValue(residue);
        //得到总共的累计折旧金额
        bean.setPropertyAccumulatedDepreciation(propertyAccumulatedDepreciation);
        //剩余累计金额
        bean.setPropertyResidueDepreciation(propertyResidueDepreciation);
        //当月折旧
        bean.setPropertyInstantDepreciation(propertyInstantDepreciation);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

}
