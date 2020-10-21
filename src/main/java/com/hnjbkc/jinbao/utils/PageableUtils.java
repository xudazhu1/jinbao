package com.hnjbkc.jinbao.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;

/**
 * 生成适用于jpa的pageable实例
 * @author xudaz
 * @date 2019/4/11
 */
public class PageableUtils {

    /**
     * 生成pageable
     * @param pageNum pageNum
     * @param pageSize pageSize
     * @param sort sort 排序 true 正 false 倒序
     * @param sortField sortField 排序用的字段(属性名)
     * @param nullSortField sortField 空时 排序用的字段(属性名)
     * @return pageable
     */
    private static Pageable producePageable(Integer pageNum, Integer pageSize, Boolean sort, String [] sortField , String nullSortField){
        Sort.Order [] orders;
        if ( sortField != null ) {
             orders = new Sort.Order[sortField.length];
            for (int i = 0; i < sortField.length; i++) {
                if ( sort != null && sort ) {
                    orders[i] = Sort.Order.asc(sortField[i]);
                } else {
                    orders[i] = Sort.Order.desc(sortField[i]);
                }
            }
        } else {
            if ( sort != null && sort ) {
                orders = new Sort.Order[]{Sort.Order.asc(nullSortField)};
            } else {
                orders = new Sort.Order[]{Sort.Order.desc(nullSortField)};
            }
        }

        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = Integer.MAX_VALUE;
        }
        return PageRequest.of(pageNum - 1, pageSize, Sort.by(orders));
    }

    /**
     * 生成pageable
     * @param map map
     * @param nullSortField sortField 空时 排序用的字段(属性名)
     * @return pageable
     */
    public static Pageable producePageable4Map(Map<String , Object> map , String nullSortField ) {
        //Integer pageNum, Integer pageSize, Boolean sort, String sortFieid , String nullSortFieid
        Object pageNum = map.remove("pageNum");
        Object pageSize = map.remove("pageSize");
        Object sort = map.remove("sort");
        String sortField = (String)map.remove("sortField");
        String[] split = sortField == null ? null : sortField.split("[$]");

        return PageableUtils.producePageable(
                pageNum == null ? null :Integer.parseInt((String)pageNum),
                pageSize == null ? null :Integer.parseInt((String)pageSize),
                sort == null ? null : Boolean.parseBoolean((String)sort),
                split,
                 nullSortField
        );
    }

    /**
     * 生成pageable
     * @param map map
     * @param nullSortField sortField 空时 排序用的字段(属性名)
     * @param bean4TableName 主体类
     * @return pageable
     */
    public static Pageable producePageable4Map(Map<String , Object> map , String nullSortField  ,Class bean4TableName ) {
        Object pageNum = map.remove("pageNum");
        Object pageSize = map.remove("pageSize");
        Object sort = map.remove("sort");
        String sortField = (String)map.remove("sortField");
        String[] split = sortField == null ? null : sortField.split("[$]");
        if (split != null  ) {
            if ( bean4TableName != null ) {
                String toLowerNameByBean = MyBeanUtils.getToLowerNameByBean(bean4TableName);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < split.length; i++) {
                    stringBuilder.delete(0 , stringBuilder.length());
                    split[i] = stringBuilder.append(toLowerNameByBean).append(".").append(split[i]).toString() ;
                }
            }
        }

        return PageableUtils.producePageable(
                pageNum == null ? null :Integer.parseInt((String)pageNum),
                pageSize == null ? null :Integer.parseInt((String)pageSize),
                sort == null ? null : Boolean.parseBoolean((String)sort),
                split,
                 nullSortField
        );
    }
    @SuppressWarnings("unused")
    public static Pageable producePageable4MapByGet(Map<String , Object> map , String nullSortField ) {
        //Integer pageNum, Integer pageSize, Boolean sort, String sortFieid , String nullSortFieid
        Object pageNum = map.get("pageNum");
        Object pageSize = map.get("pageSize");
        Object sort = map.get("sort");
        String sortField = (String)map.get("sortFieid");
        String[] split = sortField == null ? null : sortField.split("[$]");

         return PageableUtils.producePageable(
                pageNum == null ? null :Integer.parseInt((String)pageNum),
                pageSize == null ? null :Integer.parseInt((String)pageSize),
                sort == null ? null : Boolean.parseBoolean((String)sort),
                 split,
                 nullSortField
        );
    }
}
