package com.hnjbkc.jinbao.businesscommission;

import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.implement.ImplementBean;
import com.hnjbkc.jinbao.implement.ImplementServiceImpl;
import com.hnjbkc.jinbao.utils.layuisoultable.SoulPage;
import com.hnjbkc.jinbao.utils.layuisoultable.SoulTableDao;
import com.hnjbkc.jinbao.utils.layuisoultable.SoulUtils;
import com.hnjbkc.jinbao.utils.tableutils.TableUtilsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

/**
 * @author xudaz
 */
@SuppressWarnings("unchecked")
@Service
@PropertySource("classpath:config.yml")
public class BusinessCommissionService {


    private TableUtilsDao tableUtilsDao;

    @Autowired
    public void setSqlUtilsDao( TableUtilsDao tableUtilsDao ) {
        this.tableUtilsDao = tableUtilsDao;
    }

    /**
     * 注入实体管理器,执行持久化操作
     */
    @PersistenceContext
    public EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    private SoulTableDao soulTableDao;

    @Autowired
    public void setTableUtilsDao(  SoulTableDao soulTableDao ) {
        this.soulTableDao = soulTableDao;
    }

    @SuppressWarnings({"unchecked", "Duplicates"})
     Object getBusinessCommission( SoulPage soulPage, String filterSos   ) {
        SoulUtils.addFilterSo( soulPage , filterSos );
        Page<Map<String , Object >> layUiTable = (Page<Map<String, Object>>) soulTableDao.get4layUiTable(soulPage);

        if ( layUiTable.isEmpty() ) {
            return layUiTable;
        }

        //准备好顶层map
        Map<String , Map< String , Object > > outerMap = new HashMap<>(10);

        for (Map<String, Object> mapList : layUiTable.getContent()) {
            outerMap.put((String) mapList.get( "项目编号" ), mapList  );
        }
        //添加部门
//        addImplements( outerMap );

        //添加回款金额 等级 利润比
        addIncomeMoney( outerMap );
        //添加施工亏损费
        addConstructionLoss( outerMap );
        //添加成本(不含税)
        addCost( outerMap );
        //添加税费以及项目利润 并且计算合作费 公司利润 预计剩余成本
        addProjectProfits( outerMap );
        //添加 预计公司利润(扣除前)  预计公司利润(扣除后)  业务提成(扣除前) 业务提成(扣除后)
        addStatistics( outerMap );
        //添加预付业务提成 实付业务提成
        addBusinessCommission( outerMap );
        //添加未付业务提成
        addUnpaidBusinessCommission( outerMap );

        //精确小数点
        layUiTable.getContent().forEach( map -> {
            for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
                Object value = stringObjectEntry.getValue();
                if ( value instanceof  Double ) {
                    map.put( stringObjectEntry.getKey() , doubleBitUp((Double) value, 2 ) );
                }
            }
        } );

        return layUiTable;
    }

    /**
     * 四舍五入
     * @param d  d
     * @param bit 小数位数
     * @return r
     */
    private static double doubleBitUp(double d,int bit) {
        if (d == 0.0)
            return d;
        double pow = Math.pow(10, bit);
        return (double)Math.round(d*pow)/pow;
    }

    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    private void addImplements(Map<String, Map<String, Object>> outerMap) {
        String hql = " select " +
                " new map( " +
                " impl.projectBean.projectNum as projectNum " +
                " , group_concat( impl.departmentBean.departmentName , ' ' ) as departmentName ) " +
                " from ImplementBean impl " +
                " left join impl.projectBean " +
                " left join impl.departmentBean " +
                " where impl.projectBean.projectNum in ( :projectNums ) " +
                " group by impl.projectBean.projectId " ;
        Query query = entityManager.createQuery(hql);
        query.setParameter("projectNums" , outerMap.keySet() );
        List resultList = query.getResultList();
        for (Object o : resultList) {
            if ( o instanceof  Map ) {
                Map<String , Object > map = (Map<String, Object>) o;
                for (Map.Entry<String, Map<String, Object>> stringMapEntry : outerMap.entrySet()) {
                    String key = stringMapEntry.getKey();
                    if ( key.equals( map.get("projectNum") ) ) {
                        stringMapEntry.getValue().put("部门" , map.get( "departmentName" ) );
                    }
                }
            }
        }

    }

    private void addUnpaidBusinessCommission( Map<String, Map<String, Object>> outerMap ) {
        for (Map.Entry<String, Map<String, Object>> stringMapEntry : outerMap.entrySet()) {
            Map<String, Object> value = stringMapEntry.getValue();
            double businessCommissionAfter = (double) value.get("预计业务提成(扣除后)");
            double a = (double) value.get("预支业务提成");
            double b = (double) value.get("实付业务提成");
            double c = (double) value.get("预计剩余成本");
            value.put( "未付业务提成" , businessCommissionAfter - a - b - c );
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private void addBusinessCommission(Map<String, Map<String, Object>> outerMap) {
        if ( outerMap.size() == 0 ) {
            return;
        }
        @SuppressWarnings("JpaQlInspection") String hql = "select " +
                " disburseBean.implementBean.projectBean.projectNum  " +
                ", disburseBean.disburseDetailBean.disburseDetailName " +
                ", sum( disburseBean.disbursePaymentAmount ) " +
                " from DisburseBean disburseBean " +
                " left join disburseBean.disburseDetailBean " +
                " left join disburseBean.paymentStatusBean " +
                " left join disburseBean.implementBean " +
                " left join disburseBean.implementBean.projectBean " +
                " where disburseBean.paymentStatusBean.paymentStatusId=7  " +
                " and disburseBean.implementBean.projectBean.projectNum in :list"  +
                " and disburseBean.disburseDetailBean.disburseDetailName in ( '实付业务提成' , '预支业务提成' ) "  +
                " group by disburseBean.implementBean.projectBean.projectNum ";
        Query query = entityManager.createQuery(hql);
        query.setParameter( "list" , outerMap.keySet() );
        List<Object[] > resultList = query.getResultList();
        //处理成map
        Map<String , Double> paidResultMap = new HashMap<>(resultList.size());
        Map<String , Double> advancePayResultMap = new HashMap<>(resultList.size());
        resultList.forEach( objects -> {
            if ( "实付业务提成".equals( objects[1] ) ) {
                paidResultMap.put( (String) objects[0] , (Double) objects[2]);
            }
            if ( "预支业务提成".equals( objects[1] )  ) {
                advancePayResultMap.put( (String) objects[0] , (Double) objects[2]);
            }
        });
        outerMap.forEach( ( key , value ) -> {
            Double aDouble = paidResultMap.get(key);
            value.put( "实付业务提成" , aDouble == null ? 0 : aDouble );
            Double aDouble1 = advancePayResultMap.get(key);
            value.put( "预支业务提成" , aDouble1 == null ? 0 : aDouble1 );
        });
    }


    /**
     * 添加统计
     * @param outerMap outerMap getBusinessCommission方法的outerMap
     * @see BusinessCommissionService
     */
    private void addStatistics( Map<String , Map< String , Object > > outerMap ) {
        outerMap.forEach( ( key , value ) -> {
            String projectType = (String) value.get("项目类型");
            //业务提成 扣除前 业务提成 扣除后 公司利润 扣除前 公司利润 扣除后
            double businessCommissionBefore = 0.00 , businessCommissionAfter = 0.00 , corporateProfitsBefore = 0.00 , corporateProfitsAfter = 0.00;
            String[] projectTypes = new String[]{"牛逼(特殊)" , "牛逼(普通)" , "小挣" , "劳务" , "承包" , "配合" , "初始"};
            //回款
            Double countIncomeMoney = (Double) value.get("回款总额");
            //项目利润
            double projectProfits =  (Double) value.get("项目利润");
            //成本(不含税)
            double cost =  (Double) value.get("成本(不含税)");
            //配合
            if ( projectTypes[5].equals( projectType ) ) {
                //合作费改为税费 + 管理费 所以为0.00
                double feeTemp =  0.00;
                businessCommissionBefore = countIncomeMoney - feeTemp;
                double constructionLoss =  (Double) value.get("施工亏损费");
                businessCommissionAfter = businessCommissionBefore - constructionLoss;
                corporateProfitsBefore = projectProfits - businessCommissionBefore;
                corporateProfitsAfter = corporateProfitsBefore;
                //承包 承包费不是百分比 合作费是回款的百分比
            } else if ( projectTypes[4].equals( projectType ) ) {
                //合作费改为税费 + 管理费 所以为0.00
                double feeTemp =  0.00;
                double contractFee =  (Double) value.get("承包费");
                businessCommissionBefore = countIncomeMoney - feeTemp - contractFee;
                businessCommissionAfter = businessCommissionBefore;
                corporateProfitsBefore = projectProfits - businessCommissionBefore;
                double constructionLoss =  (Double) value.get("施工亏损费");
                corporateProfitsAfter = projectProfits - businessCommissionBefore - constructionLoss;
                //劳务
            } else  if ( projectTypes[3].equals( projectType ) ) {
                double feeTemp =  (Double) value.get("公司利润");
                double contMoney = (double) value.get("合同金额");
                corporateProfitsBefore = contMoney * 0.05;
                double constructionLoss =  (Double) value.get("施工亏损费");
                corporateProfitsAfter = corporateProfitsBefore - constructionLoss * 0.5;
                businessCommissionBefore = countIncomeMoney - cost - feeTemp;
                businessCommissionAfter = businessCommissionBefore - constructionLoss * 0.5;
                //小挣
            } else  if ( projectTypes[2].equals( projectType ) ) {
                double constructionLoss =  (Double) value.get("施工亏损费");
                //如果项目利润大于0 盈利
                if ( projectProfits > 0.00  ) {
                    businessCommissionBefore = projectProfits * 0.35;
                    businessCommissionAfter = businessCommissionBefore - constructionLoss * 0.5;
                    corporateProfitsBefore = projectProfits * 0.65;
                    corporateProfitsAfter = corporateProfitsBefore - constructionLoss * 0.5;
                    //如果项目利润小于0 亏损
                } else  {
                    businessCommissionBefore = projectProfits * 0.5;
                    businessCommissionAfter = businessCommissionBefore - constructionLoss * 0.5;
                    corporateProfitsBefore = projectProfits * 0.5;
                    corporateProfitsAfter = corporateProfitsBefore - constructionLoss * 0.5;
                }
                //牛逼(普通) 牛逼(特殊)
            } else  if ( projectTypes[1].equals( projectType ) || projectTypes[0].equals( projectType ) ) {
                double constructionLoss =  (Double) value.get("施工亏损费");
                //计算回款金额 * 公司利润比例
                double corporateProfitsTemp = 0.00;
                //记录每个比例的百分比
                List<Double[]> levelTemp = new ArrayList<>();
                List<Double> incomes  = (List<Double>) value.get("回款金额");
                List<Double> profitRatios =(List<Double>) value.get("公司利润比");
                if ( incomes == null ) {
                    incomes = new ArrayList<>();
                    profitRatios = new ArrayList<>();
                }
                for (int i = 0; i < incomes.size(); i++) {
                    corporateProfitsTemp += incomes.get( i ) * profitRatios.get( i );
                    levelTemp.add( new Double[] { incomes.get( i ) / countIncomeMoney  ,  profitRatios.get( i ) });
                }
                //利润超额
                if (   projectProfits >= ( cost * 4 ) ) {
                    double maxIncome = cost * 4;
                    for (Double[] doubles : levelTemp) {
                        corporateProfitsBefore += maxIncome * doubles[0] * doubles[1];
                    }
                    corporateProfitsBefore += ( countIncomeMoney -  cost * 4 ) * 0.06;
                    corporateProfitsAfter = corporateProfitsBefore - constructionLoss * 0.5;
                    businessCommissionBefore = projectProfits - corporateProfitsBefore;
                    businessCommissionAfter = businessCommissionBefore - constructionLoss * 0.5;
                    //利润正常
                } else if (  projectProfits >= corporateProfitsTemp ) {
                    corporateProfitsBefore = corporateProfitsTemp;
                    corporateProfitsAfter = corporateProfitsBefore - constructionLoss * 0.5;
                    businessCommissionBefore = projectProfits - corporateProfitsBefore;
                    businessCommissionAfter = businessCommissionBefore - constructionLoss * 0.5;
                    //利润微盈
                } else if (    projectProfits >= 0   ) {
                    corporateProfitsBefore = projectProfits;
                    corporateProfitsAfter = corporateProfitsBefore - constructionLoss * 0.5;
                    businessCommissionBefore = 0;
                    businessCommissionAfter = businessCommissionBefore - constructionLoss * 0.5;
                    //项目亏损
                } else if (    projectProfits <  0   ) {
                    businessCommissionBefore = projectProfits * 0.5;
                    businessCommissionAfter = businessCommissionBefore - constructionLoss * 0.5;
                    corporateProfitsBefore = projectProfits * 0.5;
                    corporateProfitsAfter = corporateProfitsBefore - constructionLoss * 0.5;
                }
            } else  if ( projectTypes[6].equals( projectType ) ) {
                double constructionLoss =  (Double) value.get("施工亏损费");
                businessCommissionBefore = projectProfits * 0.5;
                businessCommissionAfter = businessCommissionBefore - constructionLoss * 0.5;
                corporateProfitsBefore = projectProfits * 0.5;
                corporateProfitsAfter = corporateProfitsBefore - constructionLoss * 0.5;
            }
            value.put("预计业务提成(扣除前)" , businessCommissionBefore);
            value.put("预计业务提成(扣除后)" , businessCommissionAfter);
            value.put("预计公司利润(扣除前)" , corporateProfitsBefore );
            value.put("预计公司利润(扣除后)" , corporateProfitsAfter );
        } );
    }

    /**
     * 添加回款金额 等级 利润比
     * @param outerMap outerMap getBusinessCommission方法的outerMap
     * @see BusinessCommissionService
     */
    private  void addIncomeMoney( Map<String , Map< String , Object > > outerMap ) {
        //每个项目的回款金额
        Map<String, Object> allIncomeMoney4projectNumbers = getIncomeMoney4projectNumbers(outerMap.keySet() , true );
        Map<String, Object> incomeMoney4projectNumbers = getIncomeMoney4projectNumbers(outerMap.keySet() , false );
        for (Map.Entry<String, Object> stringObjectEntry : allIncomeMoney4projectNumbers.entrySet()) {
            String key = stringObjectEntry.getKey();
            Map<String, Object> innerMap = outerMap.get(key);
            String projectType = (String) innerMap.get("项目类型");
            if ( projectType != null && projectType.startsWith("牛逼") ) {
                String typeDetail = "牛逼(普通)".equals( projectType ) ? "普通" : "特殊";
                Map<String, Double> value = (Map<String, Double>) incomeMoney4projectNumbers.get( key );
                if ( value == null ) {
                    continue;
                }
                //等级list; //回款list; //公司利润比list;
                List<String> levels = new ArrayList<>();
                List<Double> incomes = new ArrayList<>();
                List<Double> profitRatios = new ArrayList<>();
                for (Map.Entry<String, Double> stringDoubleEntry : value.entrySet()) {
                    //等级  //回款 //公司利润比
                    levels.add( stringDoubleEntry.getKey() ) ;
                    incomes.add( doubleBitUp( stringDoubleEntry.getValue() ,  2 ) ) ;
                    profitRatios.add(levelAndProfitRatio.get( typeDetail ).get( stringDoubleEntry.getKey() ) );
                }
                innerMap.put( "回款金额" , incomes );
                innerMap.put( "等级" , levels );
                innerMap.put( "公司利润比" , profitRatios );
            }
            else {
                innerMap.put( "回款金额" , allIncomeMoney4projectNumbers.get( key ) );
            }

        }
    }

    private void addProjectProfits(Map<String , Map< String , Object > > outerMap ) {
        //拿到税率 回款金额总额 计算税费 合作费 公司利润
        outerMap.forEach( ( key , value ) -> {
            Double rate = (Double) value.get("税率");
            Object incomeMoney = value.get("回款金额");
            if ( incomeMoney instanceof List ) {
                Double temp = 0.00;
                for (Double aDouble : ((List<Double>) incomeMoney)) {
                    temp += aDouble;
                }
                incomeMoney = temp;
            }
            if ( incomeMoney == null ) {
                incomeMoney = 0.00;
            }
            value.put("回款总额" , incomeMoney );
//            Double fee1 = (Double) value.get("合作费");
            Double fee2 = (Double) value.get("公司利润");
            double income = (Double)incomeMoney;
//            value.put("合作费" , fee1 * income );
            value.put("公司利润" , fee2 * income );
            double rateFee =     rate * income;
            value.put( "税费" , rateFee );
            //拿到成本不含税 计算项目利润
            Double cost = (Double) value.get("成本(不含税)");
            double corporateProfits = income - cost - rateFee;
            //添加预计剩余成本
            String projectBelongsType = (String) value.get("所属类型");
            String projectStatus = (String) value.get("项目状态");
            double contractMoney = (Double) value.get("合同金额");
            //预计成本
            double estimatedCost = contractMoney * ( "政府项目".equals( projectBelongsType) ? 0.3 : 0.6 );

            value.put("预计成本" , estimatedCost );
            value.put("总成本(含税)" , cost +rateFee );
            double temp = estimatedCost - (cost + rateFee);
            if ( temp < 0 ) {
                temp = 0;
            }
//            value.put("预计剩余成本" , "干活中".equals( projectStatus ) ? temp : 0  );
            //直接为零 2020/2/28  后期可能会恢复为上面
            value.put("预计剩余成本" , 0.00  );
            value.put("项目利润" , corporateProfits);
        } );
    }


    private ImplementServiceImpl implementService;

    @Autowired
    public void setImplementService(ImplementServiceImpl implementService) {
        this.implementService = implementService;
    }

    /**
     * 为outerMap 添加成本不含税
     * @param outerMap outerMap getBusinessCommission方法的outerMap
     * @see BusinessCommissionService
     */
    private  void addCost(Map<String , Map< String , Object > > outerMap ) {
        Map<String, Object> allByProjectBeanProjectNumIn = implementService.findAllByProjectBeanProjectNumIn(outerMap.keySet());
        allByProjectBeanProjectNumIn.forEach( ( key , value ) -> {
            Map<String , Object> objects = (Map<String, Object>) value;
            outerMap.get( key ).put( "项目花销" , objects.get( "项目花销" ) );
            outerMap.get( key ).put( "管理费" , objects.get( "管理费" ) );
            outerMap.get( key ).put( "人员成本" , objects.get( "人员成本" ) );
            outerMap.get( key ).put( "技术提成" , objects.get( "技术提成" ) );
            outerMap.get( key ).put( "年总奖金" , objects.get( "年总奖金" ) );
            outerMap.get( key ).put( "设备使用费" , objects.get( "设备使用" ) );
            outerMap.get( key ).put( "班组费" , objects.get( "班组费" ) );
            outerMap.get( key ).put( "其他费" , objects.get( "其他费"  ) );
            outerMap.get( key ).put( "人工天数" , objects.get( "人工天数") );
            double cost = (Double) objects.get( "项目花销" ) + (Double)objects.get( "管理费" ) + (Double)objects.get( "人员成本" ) +
                    (Double) objects.get( "技术提成" ) + (Double) objects.get( "年总奖金" ) + (Double)objects.get( "设备使用" ) +
                    (Double) objects.get( "班组费" ) + (Double)objects.get( "其他费" );
            outerMap.get( key ).put( "成本(不含税)" , cost );
        } );
    }

    /**
     * 为outerMap 添加施工亏损费
     * @param outerMap getBusinessCommission方法的outerMap
     */
    private void addConstructionLoss( Map<String , Map< String , Object > > outerMap ) {
        if ( outerMap.size() == 0 ) {
            return;
        }
        @SuppressWarnings("JpaQlInspection") String hql = "select " +
                " incomeBean.projectBean.projectNum  " +
                ", sum( incomeBean.incomeMoney ) " +
                " from IncomeBean incomeBean " +
                " left join incomeBean.projectBean " +
                " where incomeBean.incomeAuditStatus='1' " +
                " and incomeBean.incomeType = '其他收入-施工亏损费'  " +
                " and incomeBean.projectBean.projectNum in :list"  +
                "";
        Query query = entityManager.createQuery(hql);
        query.setParameter( "list" , outerMap.keySet() );
        List<Object[] > resultList = query.getResultList();
        //处理成map
        Map<String , Double> resultMap = new HashMap<>(resultList.size());
        resultList.forEach( objects -> resultMap.put( (String) objects[0] , (Double) objects[1]));
        outerMap.forEach( ( key , value ) -> {
            Double aDouble = resultMap.get(key);
            value.put( "施工亏损费" , aDouble == null ? 0 : aDouble );
        });
    }

    /**
     * 为这些项目 获取回款的金额 等级 以及公司利润比
     * @param projectNumbers  项目编号 s
     * @return 返回key为项目编号 value为 回款的金额 等级 以及公司利润比 的map
     */
    private Map<String , Object > getIncomeMoney4projectNumbers(Collection<String> projectNumbers , boolean all ) {
        //准备返回的Map
        Map<String , Object > resultMap = new HashMap<>( projectNumbers.size() );
        //先获取所有项目编号 回款日期 回款金额
        @SuppressWarnings("JpaQlInspection") String hql = "select " +
                " incomeBean.projectBean.projectNum  " +
                ",incomeBean.projectBean.managementBean.managementCommissionMode  " +
                ",incomeBean.incomeDate  " +
                ",incomeBean.incomeMoney " +
                ",incomeBean.projectBean.managementBean.managementPartnersBean.managementPartnersRefereesInitLevel  " +
                ",incomeBean.projectBean.managementBean.managementPartnersBean.userBean.userId  " +
                ",incomeBean.incomeId " +
                " from IncomeBean incomeBean " +
                " left join incomeBean.projectBean " +
                " left join incomeBean.projectBean.managementBean " +
                " left join incomeBean.projectBean.managementBean.managementPartnersBean " +
                " left join incomeBean.projectBean.managementBean.managementPartnersBean.userBean " +
                " where incomeBean.incomeAuditStatus='1'  and incomeBean.incomeType = '项目收入-项目收入' " +
                "";
        if ( ! all ) {
            hql += " and incomeBean.projectBean.earningsCompanyBean.earningsCompanyName in ( '1910后' )  ";
        }
        Query query = entityManager.createQuery(hql);
        List<Object[] > resultList = query.getResultList();
        //如果是要处理的
        for (Object[] objects : resultList) {
            //项目编号 如果不包含编号 不处理
            String projectNum = (String) objects[0];
            if ( ! projectNumbers.contains( projectNum ) ) {
                 continue;
            }
            //如果项目不是1910后 不处理

            //提成模式 //业务人 //回款时间 //回款金额 //回款Id //初始等级
            String managementCommissionMode = (String) objects[1];
            Integer managementPartnersUserId = (Integer) objects[5];
            Date incomeDate = (Date) objects[2];
            Double incomeMoney = (Double) objects[3];
            Integer incomeId = (Integer) objects[6];
            String managementPartnersRefereesInitLevel = (String) objects[4];
            if ( incomeMoney == null ) {
                incomeMoney = 0.00;
            }
            //是牛逼模式 需要计算等级
            if ( managementCommissionMode != null && managementCommissionMode.contains("牛逼")  ) {
                if ( managementPartnersRefereesInitLevel == null || "".equals( managementPartnersRefereesInitLevel ) ) {
                    managementPartnersRefereesInitLevel = "P8";
                }
                //查找已有全部回款 和初始等级 计算该笔回款金额的等级归属
                Double allIncomeMoney = getAllIncomeMoney(resultList, incomeDate, managementPartnersUserId , incomeId );
                //已有全部回款 和 初始等级 计算出现有等级
                Map<String, Double> incomeMoneyLevel = getIncomeMoneyLevel(allIncomeMoney, incomeMoney, managementPartnersRefereesInitLevel);
                Map<String, Double> o = (Map<String, Double>) resultMap.get(projectNum);
                if ( o == null ) {
                    o = incomeMoneyLevel;
                } else {
                    //遍历添加
                    for (Map.Entry<String, Double> stringDoubleEntry : incomeMoneyLevel.entrySet()) {
                        //等级
                        String key = stringDoubleEntry.getKey();
                        //如果包含等级 值加上
                        if ( o.containsKey( key ) ) {
                            o.put( key , o.get( key ) + stringDoubleEntry.getValue() );
                        } else {
                            o.put( key ,  stringDoubleEntry.getValue() );
                        }
                    }
                }
                resultMap.put(projectNum , o  );
            }
            //不是牛逼模式 只记录回款金额
            else {
                Double o = (Double) resultMap.get(projectNum);
                if ( o == null ) {
                    o = 0.00;
                }
                resultMap.put(projectNum , o + incomeMoney  );
            }
        }
        return resultMap;
    }

    /**
     * 各等级的金额范围
     */
    private Map<String , Double[]> levelAndInitMoney = new HashMap<>();
    /**
     * 各等级的公司利润占比
     */
    private Map<String , Map<String , Double>> levelAndProfitRatio = new HashMap<>();

    {
        levelAndInitMoney.put("P8" , new Double[]{0.00,5000000.00} );
        levelAndInitMoney.put("P7" , new Double[]{5000000.00,10000000.00} );
        levelAndInitMoney.put("P6" , new Double[]{10000000.00,15000000.00} );
        levelAndInitMoney.put("P5" , new Double[]{15000000.00,20000000.00} );
        levelAndInitMoney.put("P4" , new Double[]{20000000.00,25000000.00} );
        levelAndInitMoney.put("P3" , new Double[]{25000000.00,30000000.00} );
        levelAndInitMoney.put("P2" , new Double[]{30000000.00,35000000.00} );
        levelAndInitMoney.put("P1" , new Double[]{35000000.00,-1.00} );

        levelAndProfitRatio.put("普通" , new HashMap<>());
        levelAndProfitRatio.put("特殊" , new HashMap<>());
        levelAndProfitRatio.get("普通").put("P1" , 0.12 );
        levelAndProfitRatio.get("普通").put("P2" , 0.13 );
        levelAndProfitRatio.get("普通").put("P3" , 0.14 );
        levelAndProfitRatio.get("普通").put("P4" , 0.15 );
        levelAndProfitRatio.get("普通").put("P5" , 0.16 );
        levelAndProfitRatio.get("普通").put("P6" , 0.17 );
        levelAndProfitRatio.get("普通").put("P7" , 0.18 );
        levelAndProfitRatio.get("普通").put("P8" , 0.19 );

        levelAndProfitRatio.get("特殊").put("P1" , 0.15 );
        levelAndProfitRatio.get("特殊").put("P2" , 0.16 );
        levelAndProfitRatio.get("特殊").put("P3" , 0.17 );
        levelAndProfitRatio.get("特殊").put("P4" , 0.18 );
        levelAndProfitRatio.get("特殊").put("P5" , 0.19 );
        levelAndProfitRatio.get("特殊").put("P6" , 0.20 );
        levelAndProfitRatio.get("特殊").put("P7" , 0.21 );
        levelAndProfitRatio.get("特殊").put("P8" , 0.22 );


    }

    private Map<String , Double> getIncomeMoneyLevel( double allIncomeMoney , double incomeMoney ,  String managementPartnersRefereesInitLevel ) {
        Map<String , Double> splitMap = new HashMap<>(5);

        //根据所有已有回款 和本次回款 和  初始等级 计算出本次回款的等级分配
        //拿到初始等级金额
        Double[] doubles = levelAndInitMoney.get(managementPartnersRefereesInitLevel);
        if ( doubles == null ) {
            return splitMap;
        }
        // 处理回款区间
        splitIncomeMoney( splitMap ,  allIncomeMoney + doubles[0] , incomeMoney );
        return splitMap;
    }

    /**
     * 根据已有(已分配 , 包括了初始 )金额 与所剩分配金额 计算出每个等级区间所分配的金额
     * @param splitMoneyMap 分配后的map key为等级 , value 为 该区间金额
     * @param allIncomeMoney 已有(已分配 , 包括了初始 )金额
     * @param incomeMoney 所剩分配金额
     */
    private void splitIncomeMoney( Map<String , Double> splitMoneyMap,  double allIncomeMoney , double incomeMoney  ) {
        //计算出现有等级区间 与 现有在该区间的金额
        String level ;
        for (Map.Entry<String, Double[]> stringEntry : levelAndInitMoney.entrySet()) {
            Double[] value = stringEntry.getValue();
            if ( value[1] == -1.00 ) {
                value[1] = Double.MAX_VALUE;
            }
            //找到区间
            if ( value[0] <= allIncomeMoney && allIncomeMoney < value[1] ) {
                level = stringEntry.getKey();
                //如果  已有(已分配 , 包括了初始 )金额 + 所剩分配金额 超出该区间最大值 拿剩余的值和已处理的总值递归再次处理
                if ( allIncomeMoney + incomeMoney > value[1] ) {
                    double splitMoneyTemp = value[1] - allIncomeMoney;
                    splitMoneyMap.put( level , splitMoneyTemp );
                    splitIncomeMoney( splitMoneyMap , allIncomeMoney + splitMoneyTemp , incomeMoney - splitMoneyTemp );
                }
                //完全在该区间 不用递归处理
                else {
                    splitMoneyMap.put( level , incomeMoney );
                }
            }
        }
    }


    private Double getAllIncomeMoney(List<Object[] > resultList , Date incomeDate ,  Integer managementPartnersUserId , Integer incomeId  ) {
        //该业务人 小于当前时间 的 已有全部回款
        Double  incomeMoney = 0.00;
        if ( incomeDate == null ) {
            return incomeMoney;
        }
        for (Object[] objects : resultList) {
            //业务人
            Integer managementPartnersUserIdTemp = (Integer) objects[5];
            //回款Id
            Integer incomeIdTemp = (Integer) objects[6];
            //回款时间
            Date incomeDateTemp = (Date) objects[2];
            if ( incomeDateTemp == null ) {
                continue;
            }
            //回款金额
            Double incomeMoneyTemp = (Double) objects[3];
            if ( incomeMoneyTemp == null ) {
                incomeMoneyTemp = 0.00;
            }
            if ( managementPartnersUserId.equals(managementPartnersUserIdTemp) && incomeDateTemp.getTime() < incomeDate.getTime() ) {
                incomeMoney += incomeMoneyTemp;
            }
            //如果时间相等 计算id大小
            if ( managementPartnersUserId.equals(managementPartnersUserIdTemp)
                    && incomeDateTemp.getTime() == incomeDate.getTime()
                    && incomeIdTemp <  incomeId   ) {
                incomeMoney += incomeMoneyTemp;
            }
        }
        return incomeMoney;

    }

}
